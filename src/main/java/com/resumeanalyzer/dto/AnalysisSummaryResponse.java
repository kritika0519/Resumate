package com.resumeanalyzer.dto;

import java.time.LocalDateTime;

public class AnalysisSummaryResponse {

    private String id;
    private String fileName;
    private int atsScore;
    private String fitLevel;
    private LocalDateTime createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(int atsScore) {
        this.atsScore = atsScore;
    }

    public String getFitLevel() {
        return fitLevel;
    }

    public void setFitLevel(String fitLevel) {
        this.fitLevel = fitLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
