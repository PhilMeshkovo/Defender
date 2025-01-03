package com.phil.antispam.repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SpamKeywordRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SpamKeywordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> findAllKeywords() {
        String sql = "SELECT keyword FROM spam_keywords";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("keyword"));
    }
}
