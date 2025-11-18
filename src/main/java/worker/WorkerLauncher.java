package worker;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import model.bean.FileJob;
import config.RabbitMQConfig;

public class WorkerLauncher {

    private static final String QUEUE_NAME = "convert_jobs";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void launchWorker(List<FileJob> jobs) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(RabbitMQConfig.CLOUDAMQP_URL);

        try (Connection conn = factory.newConnection();
             Channel channel = conn.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            for (FileJob job : jobs) {
                String json = mapper.writeValueAsString(job);
                channel.basicPublish(
                        "", QUEUE_NAME,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        json.getBytes("UTF-8")
                );
                System.out.println("Published job: " + job.getId());
            }
        }
    }
}
