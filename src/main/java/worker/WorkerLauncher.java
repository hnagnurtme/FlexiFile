package worker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.bean.FileJob;

public class WorkerLauncher {

    // Thread pool để chạy worker, mỗi user có 1 thread
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    /** Start worker cho 1 user với danh sách fileJobs */
    public static void launchWorker(String userId, List<FileJob> jobs) {
        ConvertWorker worker = new ConvertWorker(userId, jobs);
        executor.submit(worker);
    }

    /** Test helper: chỉ nhận URL file gốc + targetFormat, trả về URL file đích */
    public static String launchTestSingleFile(String fileUrl, String targetFormat) {
        return ConvertWorker.convertSingleFile(fileUrl, targetFormat);
    }

    /** Shutdown pool khi server dừng */
    public static void shutdown() {
        executor.shutdown();
    }
}
