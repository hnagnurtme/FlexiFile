package worker;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import model.bean.FileJob;
import config.RabbitMQConfig;

public class WorkerConsumer {

    private static final String QUEUE_NAME = "convert_jobs";
    private static final ObjectMapper mapper = new ObjectMapper();

    // Thread pool size = number of jobs processed in parallel
    private static final int THREAD_POOL_SIZE = 5;

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(RabbitMQConfig.CLOUDAMQP_URL);

        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();

        // Durable queue
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        // Purge old messages (optional, keep it for testing/dev)
        channel.queuePurge(QUEUE_NAME);

        // Allow multiple unacked messages to be sent to this consumer
        channel.basicQos(THREAD_POOL_SIZE);

        System.out.println("WorkerConsumer listening for new jobs...");

        // Thread pool to process jobs in parallel
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        DeliverCallback callback = (consumerTag, delivery) -> {
            executor.submit(() -> {
                try {
                    String json = new String(delivery.getBody(), "UTF-8");
                    FileJob job = mapper.readValue(json, FileJob.class);

                    // Null-safe check to avoid NPE
                    if (!"done".equals(job.getStatus())) {
                        ConvertWorker worker = new ConvertWorker();
                        worker.process(job); // idempotent processing recommended

                        System.out.println("Job DONE: " + job.getId() + " -> " + job.getResultUrl());
                    } else {
                        System.out.println("Skipping already done job: " + job.getId());
                    }

                    // Safe ack with synchronized block
                    synchronized (channel) {
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        // Optional: mark job failed if processing throws
                        synchronized (channel) {
                            channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        };

        // Start consuming messages
        channel.basicConsume(QUEUE_NAME, false, callback, consumerTag -> { });

        // Keep JVM alive and handle shutdown gracefully
        CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Shutting down consumer...");
                executor.shutdownNow();  // Stop all threads
                channel.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }));

        latch.await(); // wait until shutdown
    }
}
