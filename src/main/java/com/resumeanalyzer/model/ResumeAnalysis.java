package com.resumeanalyzer.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "resume_analyses")
public class ResumeAnalysis {

    @Id
    private String id;

    private String fileName;

    private String extractedText;

    private int atsScore;

    private String fitLevel;

    private String matchedSkills;

    private String missingSkills;

    private String sectionChecks;

    private String formattingWarnings;

    private String keywordInsights;

    private String aiSuggestions;

    private String suggestedSummary;

    private String suggestedBullets;

    private String strengths;

    private String weakAreas;

    private String improvements;

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

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
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

    public String getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(String matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public String getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(String missingSkills) {
        this.missingSkills = missingSkills;
    }

    public String getSectionChecks() {
        return sectionChecks;
    }

    public void setSectionChecks(String sectionChecks) {
        this.sectionChecks = sectionChecks;
    }

    public String getFormattingWarnings() {
        return formattingWarnings;
    }

    public void setFormattingWarnings(String formattingWarnings) {
        this.formattingWarnings = formattingWarnings;
    }

    public String getKeywordInsights() {
        return keywordInsights;
    }

    public void setKeywordInsights(String keywordInsights) {
        this.keywordInsights = keywordInsights;
    }

    public String getAiSuggestions() {
        return aiSuggestions;
    }

    public void setAiSuggestions(String aiSuggestions) {
        this.aiSuggestions = aiSuggestions;
    }

    public String getSuggestedSummary() {
        return suggestedSummary;
    }

    public void setSuggestedSummary(String suggestedSummary) {
        this.suggestedSummary = suggestedSummary;
    }

    public String getSuggestedBullets() {
        return suggestedBullets;
    }

    public void setSuggestedBullets(String suggestedBullets) {
        this.suggestedBullets = suggestedBullets;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeakAreas() {
        return weakAreas;
    }

    public void setWeakAreas(String weakAreas) {
        this.weakAreas = weakAreas;
    }

    public String getImprovements() {
        return improvements;
    }

    public void setImprovements(String improvements) {
        this.improvements = improvements;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
