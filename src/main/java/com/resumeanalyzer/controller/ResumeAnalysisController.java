package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.AnalysisResponse;
import com.resumeanalyzer.dto.AnalysisSummaryResponse;
import com.resumeanalyzer.service.PdfReportService;
import com.resumeanalyzer.service.ResumeAnalysisService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ResumeAnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;
    private final PdfReportService pdfReportService;

    public ResumeAnalysisController(ResumeAnalysisService resumeAnalysisService, PdfReportService pdfReportService) {
        this.resumeAnalysisService = resumeAnalysisService;
        this.pdfReportService = pdfReportService;
    }

    @PostMapping("/analyze")
    public AnalysisResponse analyzeResume(@RequestParam("resume") MultipartFile resume,
                                          @RequestParam(value = "jobDescription", required = false) String jobDescription,
                                          @RequestParam(value = "jobDescriptionFile", required = false) MultipartFile jobDescriptionFile) throws IOException {
        // Multipart upload keeps the frontend simple and works well for Spring Boot deployment.
        return resumeAnalysisService.analyze(resume, jobDescription, jobDescriptionFile);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @GetMapping("/analysis/{id}")
    public AnalysisResponse getAnalysis(@PathVariable String id) {
        return resumeAnalysisService.findById(id);
    }

    @GetMapping("/history")
    public List<AnalysisSummaryResponse> getHistory() {
        return resumeAnalysisService.history();
    }

    @GetMapping("/analysis/{id}/report")
    public ResponseEntity<byte[]> downloadReport(@PathVariable String id) throws IOException {
        AnalysisResponse analysis = resumeAnalysisService.findById(id);
        byte[] report = pdfReportService.createReport(analysis);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("resume-analysis-report-" + id + ".pdf")
                .build());
        return ResponseEntity.ok().headers(headers).body(report);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, String>> handleFileError(IOException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("message", "Unable to process the PDF file. Please try a different resume."));
    }
}
