package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.AnalysisResponse;
import com.resumeanalyzer.dto.AnalysisSummaryResponse;
import com.resumeanalyzer.model.ResumeAnalysis;
import com.resumeanalyzer.repository.ResumeAnalysisRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResumeAnalysisService {

    private final PdfTextExtractorService pdfTextExtractorService;
    private final SkillMatcherService skillMatcherService;
    private final AiSuggestionService aiSuggestionService;
    private final ResumeInsightService resumeInsightService;
    private final ResumeAnalysisRepository resumeAnalysisRepository;

    public ResumeAnalysisService(PdfTextExtractorService pdfTextExtractorService,
                                 SkillMatcherService skillMatcherService,
                                 AiSuggestionService aiSuggestionService,
                                 ResumeInsightService resumeInsightService,
                                 ResumeAnalysisRepository resumeAnalysisRepository) {
        this.pdfTextExtractorService = pdfTextExtractorService;
        this.skillMatcherService = skillMatcherService;
        this.aiSuggestionService = aiSuggestionService;
        this.resumeInsightService = resumeInsightService;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
    }

    public AnalysisResponse analyze(MultipartFile file, String jobDescription, MultipartFile jobDescriptionFile) throws IOException {
        validateResume(file);

        // The analysis pipeline stays intentionally simple: extract text, match keywords,
        // generate recommendations, then persist the result for future lookup.
        String extractedText = pdfTextExtractorService.extractText(file);
        if (extractedText.isBlank()) {
            throw new IllegalArgumentException("Could not read text from this PDF. Please upload a text-based resume PDF.");
        }

        String finalJobDescription = buildJobDescription(jobDescription, jobDescriptionFile);

        SkillMatcherService.MatchResult matchResult = skillMatcherService.match(extractedText, finalJobDescription);
        String fitLevel = resumeInsightService.fitLevel(matchResult.atsScore());
        List<String> sectionChecks = resumeInsightService.detectSections(extractedText);
        List<String> formattingWarnings = resumeInsightService.formattingWarnings(extractedText);
        List<String> keywordInsights = resumeInsightService.keywordInsights(extractedText, finalJobDescription);
        List<String> suggestions = aiSuggestionService.generateSuggestions(extractedText, finalJobDescription, matchResult.missingSkills());
        String suggestedSummary = aiSuggestionService.generateSuggestedSummary(extractedText, finalJobDescription, matchResult.matchedSkills());
        List<String> suggestedBullets = aiSuggestionService.generateSuggestedBullets(extractedText, finalJobDescription, matchResult.missingSkills());
        List<String> strengths = aiSuggestionService.generateStrengths(matchResult.matchedSkills(), matchResult.atsScore());
        List<String> weakAreas = aiSuggestionService.generateWeakAreas(matchResult.missingSkills(), matchResult.atsScore());
        List<String> improvements = aiSuggestionService.generateImprovements(matchResult.missingSkills());

        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setFileName(file.getOriginalFilename());
        analysis.setExtractedText(extractedText);
        analysis.setAtsScore(matchResult.atsScore());
        analysis.setFitLevel(fitLevel);
        analysis.setMatchedSkills(String.join("|", matchResult.matchedSkills()));
        analysis.setMissingSkills(String.join("|", matchResult.missingSkills()));
        analysis.setSectionChecks(String.join("|", sectionChecks));
        analysis.setFormattingWarnings(String.join("|", formattingWarnings));
        analysis.setKeywordInsights(String.join("|", keywordInsights));
        analysis.setAiSuggestions(String.join("|", suggestions));
        analysis.setSuggestedSummary(suggestedSummary);
        analysis.setSuggestedBullets(String.join("|", suggestedBullets));
        analysis.setStrengths(String.join("|", strengths));
        analysis.setWeakAreas(String.join("|", weakAreas));
        analysis.setImprovements(String.join("|", improvements));
        analysis.setCreatedAt(LocalDateTime.now());

        ResumeAnalysis saved = resumeAnalysisRepository.save(analysis);
        return toResponse(saved);
    }

    public AnalysisResponse findById(String id) {
        ResumeAnalysis analysis = resumeAnalysisRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found."));
        return toResponse(analysis);
    }

    public List<AnalysisSummaryResponse> history() {
        return resumeAnalysisRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(this::toSummary)
                .toList();
    }

    private void validateResume(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please upload a PDF resume.");
        }
        String fileName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF files are supported for resume upload.");
        }
    }

    private String buildJobDescription(String jobDescription, MultipartFile jobDescriptionFile) throws IOException {
        String typedDescription = jobDescription == null ? "" : jobDescription.trim();
        String pdfDescription = "";

        if (jobDescriptionFile != null && !jobDescriptionFile.isEmpty()) {
            String fileName = jobDescriptionFile.getOriginalFilename() == null ? "" : jobDescriptionFile.getOriginalFilename().toLowerCase();
            if (!fileName.endsWith(".pdf")) {
                throw new IllegalArgumentException("Only PDF files are supported for job description upload.");
            }
            pdfDescription = pdfTextExtractorService.extractText(jobDescriptionFile);
            if (pdfDescription.isBlank()) {
                throw new IllegalArgumentException("Could not read text from the job description PDF. Please upload a text-based PDF or paste the text.");
            }
        }

        String combined = (typedDescription + "\n\n" + pdfDescription).trim();
        if (combined.length() < 40) {
            throw new IllegalArgumentException("Please paste a job description, upload a job description PDF, or use both.");
        }
        return combined;
    }

    private AnalysisResponse toResponse(ResumeAnalysis analysis) {
        AnalysisResponse response = new AnalysisResponse();
        response.setId(analysis.getId());
        response.setFileName(analysis.getFileName());
        response.setAtsScore(analysis.getAtsScore());
        response.setFitLevel(analysis.getFitLevel());
        response.setMatchedSkills(split(analysis.getMatchedSkills()));
        response.setMissingSkills(split(analysis.getMissingSkills()));
        response.setSectionChecks(split(analysis.getSectionChecks()));
        response.setFormattingWarnings(split(analysis.getFormattingWarnings()));
        response.setKeywordInsights(split(analysis.getKeywordInsights()));
        response.setSuggestions(split(analysis.getAiSuggestions()));
        response.setSuggestedSummary(analysis.getSuggestedSummary());
        response.setSuggestedBullets(split(analysis.getSuggestedBullets()));
        response.setStrengths(split(analysis.getStrengths()));
        response.setWeakAreas(split(analysis.getWeakAreas()));
        response.setImprovements(split(analysis.getImprovements()));
        response.setCreatedAt(analysis.getCreatedAt());
        return response;
    }

    private AnalysisSummaryResponse toSummary(ResumeAnalysis analysis) {
        AnalysisSummaryResponse response = new AnalysisSummaryResponse();
        response.setId(analysis.getId());
        response.setFileName(analysis.getFileName());
        response.setAtsScore(analysis.getAtsScore());
        response.setFitLevel(analysis.getFitLevel());
        response.setCreatedAt(analysis.getCreatedAt());
        return response;
    }

    private List<String> split(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value.split("\\|"));
    }
}
