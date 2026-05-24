package com.resumeanalyzer.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class ResumeInsightService {

    private static final Map<String, List<String>> SECTION_ALIASES = Map.of(
            "Contact", List.of("@", "linkedin", "github", "phone", "email"),
            "Summary", List.of("summary", "profile", "objective"),
            "Skills", List.of("skills", "technical skills", "technologies"),
            "Experience", List.of("experience", "employment", "work history"),
            "Projects", List.of("projects", "project"),
            "Education", List.of("education", "degree", "university", "college"),
            "Certifications", List.of("certification", "certifications", "certificate")
    );

    private static final Set<String> ACTION_VERBS = Set.of(
            "built", "developed", "designed", "implemented", "improved", "optimized",
            "created", "led", "managed", "delivered", "automated", "integrated"
    );

    public List<String> detectSections(String resumeText) {
        String normalized = normalize(resumeText);
        List<String> checks = new ArrayList<>();
        SECTION_ALIASES.forEach((section, aliases) -> {
            boolean present = aliases.stream().anyMatch(normalized::contains);
            checks.add((present ? "Present: " : "Missing: ") + section);
        });
        return checks;
    }

    public List<String> formattingWarnings(String resumeText) {
        String normalized = normalize(resumeText);
        List<String> warnings = new ArrayList<>();

        if (!Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", Pattern.CASE_INSENSITIVE).matcher(resumeText).find()) {
            warnings.add("Add a professional email address near the top of the resume.");
        }
        if (!Pattern.compile("(\\+?\\d[\\d\\s().-]{8,}\\d)").matcher(resumeText).find()) {
            warnings.add("Add a reachable phone number in the contact section.");
        }
        if (!normalized.contains("linkedin")) {
            warnings.add("Add a LinkedIn profile link if it is complete and professional.");
        }
        if (!normalized.matches("(?s).*\\b\\d+%.*") && !normalized.matches("(?s).*\\b\\d+\\+.*")) {
            warnings.add("Add measurable outcomes such as percentages, counts, time saved, or scale.");
        }
        if (resumeText.length() < 1200) {
            warnings.add("Resume content looks short; add more role-relevant projects, tools, and outcomes.");
        }
        if (ACTION_VERBS.stream().noneMatch(normalized::contains)) {
            warnings.add("Start more bullets with strong action verbs like built, improved, automated, or led.");
        }

        if (warnings.isEmpty()) {
            warnings.add("No major ATS formatting warnings found in the extracted text.");
        }
        return warnings;
    }

    public List<String> keywordInsights(String resumeText, String jobDescription) {
        Set<String> resumeWords = importantWords(resumeText);
        Map<String, Integer> jobFrequency = new LinkedHashMap<>();
        Matcher matcher = Pattern.compile("[a-zA-Z]{4,}").matcher(normalize(jobDescription));
        while (matcher.find()) {
            String word = matcher.group();
            if (!StopWords.WORDS.contains(word)) {
                jobFrequency.put(word, jobFrequency.getOrDefault(word, 0) + 1);
            }
        }

        return jobFrequency.entrySet().stream()
                .sorted((first, second) -> second.getValue().compareTo(first.getValue()))
                .limit(10)
                .map(entry -> prettify(entry.getKey()) + " appears " + entry.getValue()
                        + " time(s) in the JD and is "
                        + (resumeWords.contains(entry.getKey()) ? "present" : "missing") + " in the resume.")
                .toList();
    }

    public String fitLevel(int atsScore) {
        if (atsScore >= 85) {
            return "Excellent Match";
        }
        if (atsScore >= 70) {
            return "Good Match";
        }
        if (atsScore >= 50) {
            return "Moderate Match";
        }
        return "Low Match";
    }

    private Set<String> importantWords(String text) {
        Set<String> words = new LinkedHashSet<>();
        Matcher matcher = Pattern.compile("[a-zA-Z]{4,}").matcher(normalize(text));
        while (matcher.find()) {
            String word = matcher.group();
            if (!StopWords.WORDS.contains(word)) {
                words.add(word);
            }
        }
        return words;
    }

    private String prettify(String word) {
        return word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1);
    }

    private String normalize(String text) {
        return text == null ? "" : text.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    private static final class StopWords {
        private static final Set<String> WORDS = Set.of(
                "with", "from", "that", "this", "your", "will", "have", "must", "able",
                "about", "into", "their", "they", "them", "such", "work", "role", "using",
                "based", "good", "strong", "team", "plus", "year", "years", "and", "for",
                "company", "candidate", "responsibilities", "requirements", "preferred"
        );
    }
}
