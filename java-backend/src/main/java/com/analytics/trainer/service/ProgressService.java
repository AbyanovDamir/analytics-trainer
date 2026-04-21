package com.analytics.trainer.service;

import com.analytics.trainer.dao.ProgressDao;
import com.analytics.trainer.dao.AttemptDao;
import com.analytics.trainer.model.Progress;
import com.analytics.trainer.model.Attempt;
import java.sql.SQLException;
import java.util.List;

public class ProgressService {
    private final ProgressDao progressDao;
    private final AttemptDao attemptDao;
    
    public ProgressService(ProgressDao progressDao, AttemptDao attemptDao) {
        this.progressDao = progressDao;
        this.attemptDao = attemptDao;
    }
    
    public void updateProgress(int userId) throws SQLException {
        progressDao.updateProgress(userId);
    }
    
    public Progress getProgress(int userId) throws SQLException {
        return progressDao.getProgress(userId);
    }
    
    public List<Attempt> getUserAttempts(int userId) throws SQLException {
        return attemptDao.getUserAttempts(userId);
    }
}
