package com.resumeanalyzer.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AnalysisResponse {

    private String id;
    private String fileName;
    private int atsScore;
    private String fitLevel;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> sectionChecks;
    private List<String> formattingWarnings;
    private List<String> keywordInsights;
    private List<String> suggestions;
    private String suggestedSummary;
    private List<String> suggestedBullets;
    private List<String> strengths;
    private List<String> weakAreas;
    private List<String> improvements;
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

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<String> getSectionChecks() {
        return sectionChecks;
    }

    public void setSectionChecks(List<String> sectionChecks) {
        this.sectionChecks = sectionChecks;
    }

    public List<String> getFormattingWarnings() {
        return formattingWarnings;
    }

    public void setFormattingWarnings(List<String> formattingWarnings) {
        this.formattingWarnings = formattingWarnings;
    }

    public List<String> getKeywordInsights() {
        return keywordInsights;
    }

    public void setKeywordInsights(List<String> keywordInsights) {
        this.keywordInsights = keywordInsights;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public String getSuggestedSummary() {
        return suggestedSummary;
    }

    public void setSuggestedSummary(String suggestedSummary) {
        this.suggestedSummary = suggestedSummary;
    }

    public List<String> getSuggestedBullets() {
        return suggestedBullets;
    }

    public void setSuggestedBullets(List<String> suggestedBullets) {
        this.suggestedBullets = suggestedBullets;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getWeakAreas() {
        return weakAreas;
    }

    public void setWeakAreas(List<String> weakAreas) {
        this.weakAreas = weakAreas;
    }

    public List<String> getImprovements() {
        return improvements;
    }

    public void setImprovements(List<String> improvements) {
        this.improvements = improvements;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
