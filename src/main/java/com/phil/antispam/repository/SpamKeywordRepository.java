package com.phil.antispam.repository;

import com.phil.antispam.model.SpamKeyword;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

@Repository
public interface SpamKeywordRepository extends JpaRepository<SpamKeyword, Long> {

    @NativeQuery("SELECT keyword FROM spam_keywords")
    List<String> findKeywordAll();
}
