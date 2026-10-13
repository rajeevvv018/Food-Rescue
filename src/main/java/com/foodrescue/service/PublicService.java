package com.foodrescue.service;

import com.foodrescue.dao.PublicDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class PublicService {

    private final PublicDAO publicDAO;

    public PublicService() {
        this.publicDAO = new PublicDAO();
    }

    public Map<String, Integer> getPublicStats() throws SQLException {
        return publicDAO.getPublicStats();
    }

    public List<Map<String, String>> getPublicAvailableFood() throws SQLException {
        return publicDAO.getPublicAvailableFood();
    }
}
