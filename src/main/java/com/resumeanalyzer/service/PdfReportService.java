package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.AnalysisResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

@Service
public class PdfReportService {

    private static final float MARGIN = 50;
    private static final float LEADING = 16;

    public byte[] createReport(AnalysisResponse analysis) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ReportWriter writer = new ReportWriter(document);

            writer.title("AI Resume Analyzer Report");
            writer.line("File: " + value(analysis.getFileName()));
            writer.line("ATS Score: " + analysis.getAtsScore() + "/100");
            writer.line("Job Fit: " + value(analysis.getFitLevel()));
            writer.space();

            writer.section("Matched Skills", analysis.getMatchedSkills());
            writer.section("Missing Skills", analysis.getMissingSkills());
            writer.section("Keyword Insights", analysis.getKeywordInsights());
            writer.section("ATS Formatting Warnings", analysis.getFormattingWarnings());
            writer.section("AI Suggestions", analysis.getSuggestions());
            writer.section("Suggested Bullets", analysis.getSuggestedBullets());
            writer.heading("Suggested Summary");
            writer.line(value(analysis.getSuggestedSummary()));

            writer.close();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private String value(String text) {
        return text == null || text.isBlank() ? "Not available" : text;
    }

    private static final class ReportWriter {
        private final PDDocument document;
        private final PDType1Font regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        private final PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        private PDPage page;
        private PDPageContentStream stream;
        private float y;

        private ReportWriter(PDDocument document) throws IOException {
            this.document = document;
            newPage();
        }

        private void title(String text) throws IOException {
            write(text, boldFont, 20);
            space();
        }

        private void heading(String text) throws IOException {
            space();
            write(text, boldFont, 14);
        }

        private void section(String heading, List<String> items) throws IOException {
            heading(heading);
            if (items == null || items.isEmpty()) {
                line("No items available.");
                return;
            }
            for (String item : items) {
                line("- " + item);
            }
        }

        private void line(String text) throws IOException {
            for (String wrapped : wrap(text, 92)) {
                write(wrapped, regularFont, 11);
            }
        }

        private void space() throws IOException {
            y -= 8;
            ensureSpace();
        }

        private void write(String text, PDType1Font font, int size) throws IOException {
            ensureSpace();
            stream.beginText();
            stream.setFont(font, size);
            stream.newLineAtOffset(MARGIN, y);
            stream.showText(sanitize(text));
            stream.endText();
            y -= LEADING;
        }

        private void ensureSpace() throws IOException {
            if (y < MARGIN) {
                newPage();
            }
        }

        private void newPage() throws IOException {
            if (stream != null) {
                stream.close();
            }
            page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            stream = new PDPageContentStream(document, page);
            y = page.getMediaBox().getHeight() - MARGIN;
        }

        private void close() throws IOException {
            if (stream != null) {
                stream.close();
            }
        }

        private List<String> wrap(String text, int maxLength) {
            String safe = sanitize(text);
            if (safe.length() <= maxLength) {
                return List.of(safe);
            }
            java.util.ArrayList<String> lines = new java.util.ArrayList<>();
            StringBuilder current = new StringBuilder();
            for (String word : safe.split("\\s+")) {
                if (current.length() + word.length() + 1 > maxLength) {
                    lines.add(current.toString());
                    current = new StringBuilder(word);
                } else {
                    if (!current.isEmpty()) {
                        current.append(" ");
                    }
                    current.append(word);
                }
            }
            if (!current.isEmpty()) {
                lines.add(current.toString());
            }
            return lines;
        }

        private String sanitize(String text) {
            return text == null ? "" : text.replaceAll("[^\\x20-\\x7E]", " ");
        }
    }
}
