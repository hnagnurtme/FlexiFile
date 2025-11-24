package model.bo;

import java.util.List;

import model.bean.ConvertHistory;
import model.dao.ConvertHistoryDAO;

public class ConvertHistoryBO {
    private final ConvertHistoryDAO convertHistoryDAO = new ConvertHistoryDAO();

    /**
     * Lấy 5 file convert gần nhất
     */
    public List<ConvertHistory> getRecentConverts(String userId) {
        return convertHistoryDAO.getRecentConvertsByUserId(userId, 5);
    }

    /**
     * Lấy tất cả lịch sử convert
     */
    public List<ConvertHistory> getAllConverts(String userId) {
        return convertHistoryDAO.getAllConvertsByUserId(userId);
    }

    /**
     * Đếm số file đã convert
     */
    public int countSuccessfulConverts(String userId) {
        return convertHistoryDAO.countSuccessfulConverts(userId);
    }
}