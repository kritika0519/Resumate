package com.resumeanalyzer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AiSuggestionService {

    private final RestClient restClient = RestClient.create();

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    public List<String> generateSuggestions(String resumeText, String jobDescription, List<String> missingSkills) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            return fallbackSuggestions(missingSkills);
        }

        try {
            String prompt = """
                    You are an ATS resume reviewer. Give 5 concise, practical resume improvement suggestions.
                    Focus on tailoring this resume to the job description. Do not invent experience.

                    Resume:
                    %s

                    Job description:
                    %s

                    Missing skills:
                    %s
                    """.formatted(limit(resumeText, 5000), limit(jobDescription, 3000), String.join(", ", missingSkills));

            Map<String, Object> request = Map.of(
                    "contents", List.of(Map.of(
                            "parts", List.of(Map.of("text", prompt))
                    ))
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri(geminiApiUrl + "?key=" + geminiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(Map.class);

            String text = extractGeminiText(response);
            List<String> parsed = splitSuggestionText(text);
            return parsed.isEmpty() ? fallbackSuggestions(missingSkills) : parsed;
        } catch (Exception ex) {
            return fallbackSuggestions(missingSkills);
        }
    }

    public List<String> generateStrengths(List<String> matchedSkills, int atsScore) {
        List<String> strengths = new ArrayList<>();
        if (!matchedSkills.isEmpty()) {
            strengths.add("Your resume already includes relevant keywords such as " + String.join(", ", matchedSkills.subList(0, Math.min(3, matchedSkills.size()))) + ".");
        }
        if (atsScore >= 75) {
            strengths.add("The resume appears well aligned with the job description and should pass many basic ATS filters.");
        }
        strengths.add("Clear sections and measurable project outcomes help recruiters understand your impact quickly.");
        return strengths;
    }

    public List<String> generateWeakAreas(List<String> missingSkills, int atsScore) {
        List<String> weakAreas = new ArrayList<>();
        if (!missingSkills.isEmpty()) {
            weakAreas.add("Important job-description keywords are missing: " + String.join(", ", missingSkills.subList(0, Math.min(4, missingSkills.size()))) + ".");
        }
        if (atsScore < 70) {
            weakAreas.add("Keyword alignment is below the ideal range for a targeted application.");
        }
        weakAreas.add("Generic bullet points can reduce ATS ranking if they do not mirror the role's responsibilities.");
        return weakAreas;
    }

    public List<String> generateImprovements(List<String> missingSkills) {
        List<String> improvements = new ArrayList<>();
        improvements.add("Add a short skills section that mirrors the most relevant terms from the job description.");
        improvements.add("Rewrite experience bullets with action verbs, tools used, and measurable outcomes.");
        if (!missingSkills.isEmpty()) {
            improvements.add("Mention missing skills only when they truthfully match your experience or projects.");
        }
        return improvements;
    }

    public String generateSuggestedSummary(String resumeText, String jobDescription, List<String> matchedSkills) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            return fallbackSummary(matchedSkills);
        }

        String prompt = """
                Write one ATS-friendly resume summary in 35-45 words. Use only truthful information implied by the resume.

                Resume:
                %s

                Job description:
                %s
                """.formatted(limit(resumeText, 3500), limit(jobDescription, 2500));

        String text = callGemini(prompt);
        return text.isBlank() ? fallbackSummary(matchedSkills) : text.replaceAll("\\s+", " ").trim();
    }

    public List<String> generateSuggestedBullets(String resumeText, String jobDescription, List<String> missingSkills) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            return fallbackBullets(missingSkills);
        }

        String prompt = """
                Suggest 4 resume bullet points tailored to the job description.
                Use action verbs, tools, and measurable impact placeholders only when needed.
                Do not invent specific company names or fake metrics.

                Resume:
                %s

                Job description:
                %s
                """.formatted(limit(resumeText, 3500), limit(jobDescription, 2500));

        List<String> parsed = splitSuggestionText(callGemini(prompt));
        return parsed.isEmpty() ? fallbackBullets(missingSkills) : parsed.stream().limit(4).toList();
    }

    private String extractGeminiText(Map<String, Object> response) {
        if (response == null) {
            return "";
        }
        List<?> candidates = (List<?>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            return "";
        }
        Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
        Map<?, ?> content = (Map<?, ?>) candidate.get("content");
        List<?> parts = (List<?>) content.get("parts");
        if (parts == null || parts.isEmpty()) {
            return "";
        }
        Map<?, ?> part = (Map<?, ?>) parts.get(0);
        Object text = part.get("text");
        return text == null ? "" : String.valueOf(text);
    }

    private String callGemini(String prompt) {
        try {
            Map<String, Object> request = Map.of(
                    "contents", List.of(Map.of(
                            "parts", List.of(Map.of("text", prompt))
                    ))
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri(geminiApiUrl + "?key=" + geminiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(Map.class);

            return extractGeminiText(response);
        } catch (Exception ex) {
            return "";
        }
    }

    private List<String> splitSuggestionText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return text.lines()
                .map(line -> line.replaceAll("^[-*\\d.)\\s]+", "").trim())
                .filter(line -> line.length() > 12)
                .limit(6)
                .toList();
    }

    private List<String> fallbackSuggestions(List<String> missingSkills) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("Tailor your summary to the job title and include two or three high-value keywords from the description.");
        suggestions.add("Use measurable outcomes in experience bullets, such as performance gains, time saved, users served, or revenue impact.");
        suggestions.add("Keep formatting ATS-friendly: simple headings, readable fonts, and no text hidden inside images.");
        suggestions.add("Place your strongest technical skills near the top so recruiters and ATS systems find them quickly.");
        if (!missingSkills.isEmpty()) {
            suggestions.add("Review whether you can truthfully add experience with " + String.join(", ", missingSkills.subList(0, Math.min(3, missingSkills.size()))) + ".");
        }
        return suggestions;
    }

    private String fallbackSummary(List<String> matchedSkills) {
        String skills = matchedSkills.isEmpty()
                ? "role-relevant technical skills"
                : String.join(", ", matchedSkills.subList(0, Math.min(4, matchedSkills.size())));
        return "Results-focused candidate with hands-on experience in " + skills
                + ", able to build practical solutions, collaborate with teams, and improve resume alignment by connecting project work to measurable business outcomes.";
    }

    private List<String> fallbackBullets(List<String> missingSkills) {
        List<String> bullets = new ArrayList<>();
        bullets.add("Built and improved application features using relevant tools while keeping code readable, maintainable, and aligned with user requirements.");
        bullets.add("Integrated backend services, database operations, and clean UI workflows to deliver reliable end-to-end functionality.");
        bullets.add("Improved project quality through testing, debugging, documentation, and structured problem solving.");
        if (!missingSkills.isEmpty()) {
            bullets.add("Strengthen one bullet with truthful exposure to " + String.join(", ", missingSkills.subList(0, Math.min(3, missingSkills.size()))) + " if applicable.");
        } else {
            bullets.add("Quantified project outcomes with metrics such as response time, completion time, accuracy, or user impact where available.");
        }
        return bullets;
    }

    private String limit(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() <= maxLength ? text : text.substring(0, maxLength);
    }
}
