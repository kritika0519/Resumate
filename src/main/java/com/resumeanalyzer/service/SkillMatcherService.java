package com.resumeanalyzer.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class SkillMatcherService {

    private static final List<String> COMMON_SKILLS = List.of(
            "java", "spring boot", "spring data jpa", "hibernate", "mysql", "sql",
            "rest api", "html", "css", "javascript", "bootstrap", "git", "maven",
            "python", "react", "node.js", "aws", "azure", "docker", "kubernetes",
            "microservices", "agile", "scrum", "testing", "junit", "problem solving",
            "communication", "leadership", "machine learning", "data analysis",
            "excel", "linux", "api integration", "security", "cloud", "typescript",
            "angular", "vue", "next.js", "express", "mongodb", "postgresql", "oracle",
            "redis", "jenkins", "github actions", "ci cd", "restful services", "graphql",
            "oauth", "spring security", "unit testing", "integration testing", "mockito",
            "postman", "swagger", "openapi", "jira", "confluence", "intellij", "eclipse",
            "vs code", "firebase", "gcp", "terraform", "ansible", "devops", "etl",
            "power bi", "tableau", "pandas", "numpy", "tensorflow", "pytorch",
            "nlp", "llm", "prompt engineering", "analytics", "collaboration",
            "stakeholder management", "documentation", "debugging", "performance tuning"
    );

    private static final Pattern WORD_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z+#.\\- ]{1,35}");

    public MatchResult match(String resumeText, String jobDescription) {
        // Skills are pulled from a focused beginner-friendly dictionary so the app
        // remains easy to understand and customize for different roles.
        Set<String> requiredSkills = extractSkills(jobDescription);
        Set<String> resumeSkills = extractSkills(resumeText);

        List<String> matched = requiredSkills.stream()
                .filter(resumeSkills::contains)
                .sorted()
                .toList();

        List<String> missing = requiredSkills.stream()
                .filter(skill -> !resumeSkills.contains(skill))
                .sorted()
                .toList();

        int score = calculateScore(resumeText, jobDescription, matched.size(), requiredSkills.size());
        return new MatchResult(score, prettify(matched), prettify(missing));
    }

    private Set<String> extractSkills(String text) {
        String normalized = normalize(text);
        Set<String> skills = new LinkedHashSet<>();

        for (String skill : COMMON_SKILLS) {
            if (normalized.contains(skill)) {
                skills.add(skill);
            }
        }

        Matcher matcher = WORD_PATTERN.matcher(normalized);
        while (matcher.find()) {
            String candidate = matcher.group().trim();
            if (candidate.length() > 2 && COMMON_SKILLS.contains(candidate)) {
                skills.add(candidate);
            }
        }

        return skills;
    }

    private int calculateScore(String resumeText, String jobDescription, int matchedCount, int requiredCount) {
        int skillScore = requiredCount == 0 ? 45 : (int) Math.round((matchedCount * 70.0) / requiredCount);
        int keywordScore = calculateKeywordOverlap(resumeText, jobDescription);
        int structureScore = calculateStructureScore(resumeText);
        return Math.min(100, Math.max(0, skillScore + keywordScore + structureScore));
    }

    private int calculateKeywordOverlap(String resumeText, String jobDescription) {
        Set<String> resumeWords = importantWords(resumeText);
        Set<String> jobWords = importantWords(jobDescription);
        if (jobWords.isEmpty()) {
            return 10;
        }

        long overlap = jobWords.stream().filter(resumeWords::contains).count();
        return (int) Math.min(20, Math.round((overlap * 20.0) / jobWords.size()));
    }

    private int calculateStructureScore(String resumeText) {
        String normalized = normalize(resumeText);
        int score = 0;
        if (normalized.contains("experience")) {
            score += 3;
        }
        if (normalized.contains("education")) {
            score += 3;
        }
        if (normalized.contains("skills")) {
            score += 3;
        }
        if (normalized.contains("project")) {
            score += 3;
        }
        if (normalized.matches("(?s).*\\b\\d+%.*") || normalized.matches("(?s).*\\b\\d+\\+.*")) {
            score += 3;
        }
        return Math.min(10, score);
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

    private List<String> prettify(List<String> skills) {
        return skills.stream()
                .sorted(Comparator.naturalOrder())
                .map(skill -> {
                    String[] parts = skill.split(" ");
                    List<String> formatted = new ArrayList<>();
                    for (String part : parts) {
                        if (part.equals("api") || part.equals("sql") || part.equals("jpa") || part.equals("aws")) {
                            formatted.add(part.toUpperCase(Locale.ROOT));
                        } else {
                            formatted.add(part.substring(0, 1).toUpperCase(Locale.ROOT) + part.substring(1));
                        }
                    }
                    return String.join(" ", formatted);
                })
                .toList();
    }

    private String normalize(String text) {
        return text == null ? "" : text.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    public record MatchResult(int atsScore, List<String> matchedSkills, List<String> missingSkills) {
    }

    private static final class StopWords {
        private static final Set<String> WORDS = Set.of(
                "with", "from", "that", "this", "your", "will", "have", "must", "able",
                "about", "into", "their", "they", "them", "such", "work", "role", "using",
                "based", "good", "strong", "team", "plus", "year", "years", "and", "for"
        );
    }
}
