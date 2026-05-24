package com.resumeanalyzer.repository;

import com.resumeanalyzer.model.ResumeAnalysis;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResumeAnalysisRepository extends MongoRepository<ResumeAnalysis, String> {

    List<ResumeAnalysis> findTop10ByOrderByCreatedAtDesc();
}
