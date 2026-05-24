const html = document.documentElement;
const themeToggle = document.getElementById("themeToggle");
const form = document.getElementById("analysisForm");
const dropZone = document.getElementById("dropZone");
const fileInput = document.getElementById("resumeFile");
const fileLabel = document.getElementById("fileLabel");
const fileMessage = document.getElementById("fileMessage");
const jdDropZone = document.getElementById("jdDropZone");
const jdFileInput = document.getElementById("jdFile");
const jdFileLabel = document.getElementById("jdFileLabel");
const jdFileMessage = document.getElementById("jdFileMessage");
const jobDescription = document.getElementById("jobDescription");
const jdMessage = document.getElementById("jdMessage");
const charCount = document.getElementById("charCount");
const analyzeBtn = document.getElementById("analyzeBtn");
const statusBox = document.getElementById("statusBox");
const emptyResults = document.getElementById("emptyResults");
const resultsDashboard = document.getElementById("resultsDashboard");
const historyGrid = document.getElementById("historyGrid");
const downloadReportBtn = document.getElementById("downloadReportBtn");

const savedTheme = localStorage.getItem("resumeai-theme") || "dark";
html.setAttribute("data-theme", savedTheme);
updateThemeIcon(savedTheme);

themeToggle.addEventListener("click", () => {
    const nextTheme = html.getAttribute("data-theme") === "dark" ? "light" : "dark";
    html.setAttribute("data-theme", nextTheme);
    localStorage.setItem("resumeai-theme", nextTheme);
    updateThemeIcon(nextTheme);
});

setupDropZone(dropZone, fileInput, fileLabel, fileMessage, "resume");
setupDropZone(jdDropZone, jdFileInput, jdFileLabel, jdFileMessage, "job description");
loadHistory();

jobDescription.addEventListener("input", () => {
    const count = jobDescription.value.length;
    charCount.textContent = count;
    jdMessage.textContent = count > 0 && count < 40 && !jdFileInput.files[0] ? "Add a little more detail or upload a JD PDF." : "";
    jdMessage.className = count > 0 && count < 40 ? "validation-message error" : "validation-message";
});

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const file = fileInput.files[0];
    const jdFile = jdFileInput.files[0];
    const fileValid = validatePdfFile(file, fileLabel, fileMessage, "resume");
    const jdFileValid = jdFile ? validatePdfFile(jdFile, jdFileLabel, jdFileMessage, "job description") : true;
    const descriptionValid = validateJobDescription();

    if (!fileValid || !jdFileValid || !descriptionValid) {
        setStatus("Please fix the highlighted fields before running the analysis.", "error");
        return;
    }

    const formData = new FormData();
    formData.append("resume", file);
    formData.append("jobDescription", jobDescription.value.trim());
    if (jdFile) {
        formData.append("jobDescriptionFile", jdFile);
    }

    // Send the PDF and job description to the Spring Boot API as multipart form data.
    setLoading(true);
    setStatus("Reading your resume and job description, then comparing them...", "info");

    try {
        const response = await fetch("/api/analyze", {
            method: "POST",
            body: formData
        });

        const payload = await readApiResponse(response);
        if (!response.ok) {
            throw new Error(payload.message || "Analysis failed. Please try again.");
        }

        renderResults(payload);
        loadHistory();
        setStatus("Analysis complete. Your dashboard is ready.", "success");
        document.getElementById("results").scrollIntoView({ behavior: "smooth" });
    } catch (error) {
        setStatus(error.message, "error");
    } finally {
        setLoading(false);
    }
});

function updateThemeIcon(theme) {
    themeToggle.innerHTML = theme === "dark"
        ? '<i class="bi bi-sun-fill"></i>'
        : '<i class="bi bi-moon-stars-fill"></i>';
}

function setupDropZone(zone, input, label, message, labelText) {
    zone.addEventListener("dragover", (event) => {
        event.preventDefault();
        zone.classList.add("drag-over");
    });

    zone.addEventListener("dragleave", () => {
        zone.classList.remove("drag-over");
    });

    zone.addEventListener("drop", (event) => {
        event.preventDefault();
        zone.classList.remove("drag-over");
        const file = event.dataTransfer.files[0];
        if (file) {
            input.files = event.dataTransfer.files;
            validatePdfFile(file, label, message, labelText);
        }
    });

    input.addEventListener("change", () => {
        validatePdfFile(input.files[0], label, message, labelText);
    });
}

function validatePdfFile(file, label, message, labelText) {
    if (!file) {
        label.textContent = labelText === "resume" ? "Drop your PDF resume here" : "Optional: upload job description PDF";
        message.textContent = labelText === "resume" ? "Please choose a PDF resume." : "";
        message.className = labelText === "resume" ? "validation-message error" : "validation-message";
        return false;
    }

    const isPdf = file.type === "application/pdf" || file.name.toLowerCase().endsWith(".pdf");
    const underLimit = file.size <= 8 * 1024 * 1024;

    if (!isPdf) {
        message.textContent = "Only PDF files are supported.";
        message.className = "validation-message error";
        return false;
    }

    if (!underLimit) {
        message.textContent = "The selected file is larger than 8 MB.";
        message.className = "validation-message error";
        return false;
    }

    label.textContent = file.name;
    message.textContent = "PDF selected successfully.";
    message.className = "validation-message success";
    return true;
}

function validateJobDescription() {
    const value = jobDescription.value.trim();
    const hasJdPdf = Boolean(jdFileInput.files[0]);
    if (value.length < 40 && !hasJdPdf) {
        jdMessage.textContent = "Paste at least 40 characters or upload a JD PDF.";
        jdMessage.className = "validation-message error";
        return false;
    }
    jdMessage.textContent = "";
    jdMessage.className = "validation-message";
    return true;
}

function setLoading(isLoading) {
    analyzeBtn.disabled = isLoading;
    analyzeBtn.classList.toggle("loading", isLoading);
}

function setStatus(message, type) {
    const icon = type === "success" ? "check-circle" : type === "error" ? "exclamation-circle" : "info-circle";
    statusBox.innerHTML = `<i class="bi bi-${icon}"></i>${escapeHtml(message)}`;
    statusBox.style.color = type === "error" ? "var(--danger)" : type === "success" ? "var(--success)" : "var(--muted)";
}

function renderResults(data) {
    emptyResults.classList.add("d-none");
    resultsDashboard.classList.remove("d-none");

    animateScore(data.atsScore);
    document.getElementById("scoreSummary").textContent = scoreSummary(data.atsScore);
    document.getElementById("fitLevel").textContent = data.fitLevel || scoreSummary(data.atsScore);
    document.getElementById("fileName").textContent = data.fileName || "Uploaded resume";
    downloadReportBtn.href = `/api/analysis/${data.id}/report`;

    renderSkillChips("matchedSkills", data.matchedSkills, "success", "check2-circle");
    renderSkillChips("missingSkills", data.missingSkills, "danger", "x-circle");
    renderSectionChecks(data.sectionChecks);
    renderInsightCards("formattingWarnings", data.formattingWarnings);
    renderInsightCards("keywordInsights", data.keywordInsights);
    renderInsightCards("suggestions", data.suggestions);
    document.getElementById("suggestedSummary").textContent = data.suggestedSummary || "No summary suggestion available.";
    renderInsightCards("suggestedBullets", data.suggestedBullets);
    renderPlainList("strengths", data.strengths);
    renderPlainList("weakAreas", data.weakAreas);
    renderPlainList("improvements", data.improvements);
}

async function loadHistory() {
    try {
        const response = await fetch("/api/history");
        if (!response.ok) {
            return;
        }
        const history = await response.json();
        if (!history.length) {
            return;
        }
        historyGrid.innerHTML = history.map(item => `
            <article class="history-card">
                <div>
                    <span class="fit-badge small-fit">${escapeHtml(item.fitLevel || "Analysis")}</span>
                    <h3>${escapeHtml(item.fileName || "Uploaded resume")}</h3>
                    <p>${formatDate(item.createdAt)}</p>
                </div>
                <div class="history-score">${Number(item.atsScore) || 0}</div>
                <button class="btn btn-sm soft-btn" type="button" data-analysis-id="${item.id}">
                    <i class="bi bi-eye me-1"></i>Open
                </button>
            </article>
        `).join("");

        historyGrid.querySelectorAll("[data-analysis-id]").forEach(button => {
            button.addEventListener("click", async () => {
                const analysis = await fetchAnalysis(button.dataset.analysisId);
                if (analysis) {
                    renderResults(analysis);
                    document.getElementById("results").scrollIntoView({ behavior: "smooth" });
                }
            });
        });
    } catch (error) {
        // History is a helpful enhancement, so the main analyzer should still work if it fails.
    }
}

async function fetchAnalysis(id) {
    const response = await fetch(`/api/analysis/${id}`);
    const payload = await readApiResponse(response);
    if (!response.ok) {
        setStatus(payload.message || "Could not open this saved analysis.", "error");
        return null;
    }
    return payload;
}

async function readApiResponse(response) {
    const contentType = response.headers.get("content-type") || "";
    if (contentType.includes("application/json")) {
        return response.json();
    }

    const text = await response.text();
    return {
        message: text || "The backend API is not responding. Start Spring Boot and open http://localhost:8080."
    };
}

function animateScore(score) {
    const ring = document.getElementById("scoreRing");
    const value = document.getElementById("scoreValue");
    let current = 0;
    const target = Number(score) || 0;
    const interval = setInterval(() => {
        current += Math.max(1, Math.ceil((target - current) / 12));
        if (current >= target) {
            current = target;
            clearInterval(interval);
        }
        value.textContent = current;
        ring.style.setProperty("--score", `${current}%`);
    }, 24);
}

function scoreSummary(score) {
    if (score >= 85) return "Excellent alignment with the role.";
    if (score >= 70) return "Good match with room to sharpen keywords.";
    if (score >= 50) return "Moderate match. Add missing role-specific details.";
    return "Needs stronger targeting for this job description.";
}

function renderSkillChips(containerId, skills, tone, icon) {
    const container = document.getElementById(containerId);
    const safeSkills = skills && skills.length ? skills : ["No items found"];
    container.innerHTML = safeSkills.map(skill => `
        <span class="skill-chip ${tone}">
            <i class="bi bi-${icon}"></i>${escapeHtml(skill)}
        </span>
    `).join("");
}

function renderInsightCards(containerId, items) {
    const container = document.getElementById(containerId);
    const safeItems = items && items.length ? items : ["No suggestions were generated."];
    container.innerHTML = safeItems.map(item => `
        <div class="insight-card">
            <i class="bi bi-stars"></i>
            <span>${escapeHtml(item)}</span>
        </div>
    `).join("");
}

function renderSectionChecks(items) {
    const container = document.getElementById("sectionChecks");
    const safeItems = items && items.length ? items : ["No section checks available"];
    container.innerHTML = safeItems.map(item => {
        const present = item.toLowerCase().startsWith("present");
        return `
            <span class="skill-chip ${present ? "success" : "danger"}">
                <i class="bi bi-${present ? "check2-circle" : "x-circle"}"></i>${escapeHtml(item)}
            </span>
        `;
    }).join("");
}

function formatDate(value) {
    if (!value) {
        return "Date unavailable";
    }
    return new Date(value).toLocaleString([], {
        year: "numeric",
        month: "short",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    });
}

function renderPlainList(containerId, items) {
    const container = document.getElementById(containerId);
    const safeItems = items && items.length ? items : ["No insight available yet."];
    container.innerHTML = `<ul class="mb-0">${safeItems.map(item => `<li>${escapeHtml(item)}</li>`).join("")}</ul>`;
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
