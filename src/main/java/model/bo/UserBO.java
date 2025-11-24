package model.bo;

import model.dao.UserDAO;

public class UserBO {
    private static final UserDAO userDAO = new UserDAO();

    public boolean decrementRemainingConverts(String userId, Long numberOfFilesL) {
        return userDAO.decrementRemainingConverts(userId, numberOfFilesL);
    }

    public boolean incrementRemainingConverts(String userId, Long numberOfFilesL) {
        return userDAO.incrementRemainingConverts(userId, numberOfFilesL);
    }
}
