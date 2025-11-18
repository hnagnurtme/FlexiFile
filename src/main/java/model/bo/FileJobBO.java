package model.bo;

import java.util.Date;
import java.util.List;

import model.bean.FileJob;
import model.dao.FileJobDAO;
import util.FirebaseUtil;

public class FileJobBO {

    private final FileJobDAO fileJobDAO = new FileJobDAO();

    /** Lấy danh sách fileJobs của user */
    public List<FileJob> getJobsForUser(String userId) {
        return fileJobDAO.getFileJobsByUser(userId);
    }

    /** Cập nhật trạng thái file */
    public void markProcessing(FileJob job) {
        if (job.getStatus().equals("PROCESSING")) {
            return; // Đã ở trạng thái PROCESSING
        }
        job.setStatus("PROCESSING");
        job.setUpdatedAt(new Date());
        fileJobDAO.updateFileJob(job);
    }

    public void markDone(FileJob job, String resultUrl) {
        if (job.getStatus().equals("DONE")) {
            return; // Đã ở trạng thái DONE
        }
        job.setStatus("DONE");
        job.setResultUrl(resultUrl);
        job.setUpdatedAt(new Date());
        fileJobDAO.updateFileJob(job);
    }

    public void markFailed(FileJob job) {
        if (job.getStatus().equals("FAILED")) {
            return; // Đã ở trạng thái FAILED
        }
        job.setStatus("FAILED");
        job.setUpdatedAt(new Date());
        fileJobDAO.updateFileJob(job);
    }

    /** Signal user khi tất cả file hoàn thành */
    public void signalUserAllDone(String userId) {
        // Ví dụ cập nhật flag conversionComplete trong users collection
        try {
            fileJobDAO.getFileJobsByUser(userId); // có thể dùng dao để query
            // Cập nhật signal
            FirebaseUtil.getFirestore()
                    .collection("users")
                    .document(userId)
                    .update("conversionComplete", true)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean createFileJob(FileJob job) {
        return fileJobDAO.createFileJob(job);
    }
}
