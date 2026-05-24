# 📄 Resumate - AI Resume Analyzer & ATS Checker

> **Intelligent resume analysis platform** — Upload your resume, paste a job description, and instantly get ATS compatibility scoring, skill matching, AI-powered suggestions, and professional PDF reports.

**🚀 Live Demo:** https://resumate-zeta-lac.vercel.app/

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-green.svg)](https://www.mongodb.com/cloud/atlas)
[![Vercel](https://img.shields.io/badge/Frontend-Vercel-000.svg)](https://vercel.com)
[![Render](https://img.shields.io/badge/Backend-Render-46E3B7.svg)](https://render.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## 📊 Overview

Resumate helps job seekers optimize their resumes for **Applicant Tracking Systems (ATS)**. Get:

- ✅ ATS compatibility scores (0-100)
- ✅ Job fit analysis based on job descriptions
- ✅ AI-powered improvement suggestions
- ✅ Skill gap analysis
- ✅ ATS formatting warnings
- ✅ Professional PDF reports
- ✅ Analysis history tracking

---

## ✨ Key Features

| Feature                 | Description                                       |
| ----------------------- | ------------------------------------------------- |
| 📊 **ATS Scoring**      | Realistic ATS compatibility score (0-100)         |
| 🎯 **Job Fit Analysis** | Match your resume against job descriptions        |
| 🤖 **AI Suggestions**   | Google Gemini-powered improvement recommendations |
| 📝 **Skill Matching**   | Identifies matched and missing skills             |
| 🔍 **Keyword Analysis** | Analyzes keyword frequency and impact             |
| ✅ **Format Checking**  | Detects ATS-unfriendly formatting                 |
| 💼 **PDF Reports**      | Download professional analysis reports            |
| 📜 **Analysis History** | View all previous analyses                        |
| 🎨 **Modern UI**        | Responsive, intuitive interface                   |

---

## 🛠️ Tech Stack

### **Frontend**

- HTML5, CSS3, JavaScript
- Bootstrap 5 (responsive design)
- **Deployed on:** Vercel

### **Backend**

- Java 17
- Spring Boot 3.3.5
- Spring Data MongoDB
- Apache PDFBox 3.0.3 (PDF processing)
- **Deployed on:** Render (Docker container)

### **Database**

- MongoDB Atlas (Cloud - free tier)
- Collection: `resume_analyses`

### **AI**

- Google Gemini API 1.5 Flash (optional)
- Rule-based fallback if API not configured

### **Deployment Architecture**

```
┌─────────────────────┐
│   User's Browser    │
└──────────┬──────────┘
           │ https://resumate-zeta-lac.vercel.app
           ▼
┌─────────────────────┐
│  Vercel (Frontend)  │  ◄── HTML, CSS, JS
│ - React/Static App  │
└──────────┬──────────┘
           │ /api/* calls
           ▼
┌─────────────────────┐
│ Render (Backend)    │  ◄── Java Spring Boot API
│ - Docker Container  │
│ - Port 8080         │
└──────────┬──────────┘
           │ TCP 27017 (MongoDB driver)
           ▼
┌─────────────────────┐
│ MongoDB Atlas       │  ◄── Cloud Database
│ - resumeats DB      │
└─────────────────────┘
```

---

## 🚀 WHICH LINK TO USE?

### **👉 For Users / Public Demo:**

# https://resumate-zeta-lac.vercel.app/

**This is your complete application.** It includes everything:

- Frontend UI
- Automatic backend API calls
- Analysis history
- PDF reports

### **For API Access Only (Developers):**

- Backend API: https://resumate-backend-xy8b.onrender.com
- Health Check: https://resumate-backend-xy8b.onrender.com/api/health
- GitHub Repo: https://github.com/kritika0519/Resumate

---

## 📋 Prerequisites (For Local Development)

- ✅ Java 17 or higher
- ✅ Maven 3.6+
- ✅ MongoDB Atlas account (free tier: https://www.mongodb.com/cloud/atlas)
- ✅ (Optional) Google Gemini API key (free: https://aistudio.google.com)

---

## 🏃 Quick Start - Local Development

### 1. Clone Repository

```bash
git clone https://github.com/kritika0519/Resumate.git
cd Resumate
```

### 2. Setup MongoDB Atlas

1. Create free account: https://www.mongodb.com/cloud/atlas
2. Create a cluster (free tier available)
3. Create database user with strong password
4. Network Access → Add IP `0.0.0.0/0` (for any IP)
5. Create database: `resumeats`
6. Copy connection string (choose "Java" driver format)

### 3. Set Environment Variables

**PowerShell:**

```powershell
$env:MONGODB_URI="mongodb+srv://username:password@cluster0.xxxxx.mongodb.net/resumeats?retryWrites=true&w=majority&appName=Cluster0"
$env:GEMINI_API_KEY="your_gemini_api_key_optional"
```

**Or create `.env` file in project root:**

```env
MONGODB_URI=mongodb+srv://username:password@cluster0.xxxxx.mongodb.net/resumeats?retryWrites=true&w=majority&appName=Cluster0
GEMINI_API_KEY=your_api_key_here
```

### 4. Run Application

```bash
mvn clean spring-boot:run
```

Application starts at: **http://localhost:8080**

Test locally:

- http://localhost:8080/ → Frontend
- http://localhost:8080/api/health → Health check

---

## 📡 API Endpoints

All endpoints are accessible at `https://resumate-backend-xy8b.onrender.com` (production) or `http://localhost:8080` (local).

### 1. Health Check

```http
GET /api/health
```

**Response:**

```json
{ "status": "ok" }
```

### 2. Upload & Analyze Resume

```http
POST /api/analyze
Content-Type: multipart/form-data

Parameters:
- resume (file): PDF file [Required] Max 8MB
- jobDescription (text): Job description text [Optional]
```

**Response:**

```json
{
  "id": "507f1f77bcf86cd799439011",
  "fileName": "john_resume.pdf",
  "atsScore": 78,
  "fitLevel": "Good",
  "matchedSkills": ["Java", "Spring Boot", "MongoDB"],
  "missingSkills": ["Docker", "Kubernetes"],
  "keywordInsights": "Resume contains 45 relevant keywords",
  "aiSuggestions": [
    "Add Docker/container experience",
    "Include cloud deployment skills",
    "Expand on leadership experience"
  ],
  "sectionChecks": "All required sections present",
  "formattingWarnings": "Consider using bullet points for better ATS parsing",
  "createdAt": "2024-05-24T10:30:00Z"
}
```

### 3. Get Analysis by ID

```http
GET /api/analysis/{id}
```

**Response:** Full analysis object

### 4. Download PDF Report

```http
GET /api/analysis/{id}/report
```

**Response:** PDF file download

### 5. View Analysis History (Last 10)

```http
GET /api/history
```

**Response:**

```json
[
  {
    "id": "507f1f77bcf86cd799439011",
    "fileName": "resume.pdf",
    "atsScore": 78,
    "fitLevel": "Good",
    "createdAt": "2024-05-24T10:30:00Z"
  },
  ...
]
```

---

## 🏗️ Project Structure

```
Resumate/
├── src/main/java/com/resumeanalyzer/
│   ├── ResumeAnalyzerApplication.java      # Spring Boot entry point
│   ├── controller/
│   │   └── ResumeAnalysisController.java   # REST API endpoints (@CrossOrigin enabled)
│   ├── service/
│   │   ├── ResumeAnalysisService.java      # Business logic
│   │   ├── PdfTextExtractorService.java    # PDF text extraction
│   │   ├── PdfReportService.java           # PDF report generation
│   │   ├── AiSuggestionService.java        # AI suggestions (Gemini)
│   │   ├── SkillMatcherService.java        # Skill matching logic
│   │   └── ResumeInsightService.java       # Analysis insights
│   ├── model/
│   │   └── ResumeAnalysis.java             # MongoDB @Document entity
│   ├── repository/
│   │   └── ResumeAnalysisRepository.java   # MongoRepository data access
│   └── dto/
│       ├── AnalysisResponse.java           # API response DTOs
│       └── AnalysisSummaryResponse.java
├── src/main/resources/
│   ├── application.properties               # Spring Boot config
│   ├── static/
│   │   ├── index.html                      # Frontend UI
│   │   ├── css/styles.css
│   │   └── js/app.js
├── pom.xml                                 # Maven dependencies
├── Dockerfile                              # Docker build for Render
├── README.md                               # This file
└── database-schema.sql                     # MongoDB schema reference
```

---

## 🔧 Key Classes & Methods

### **ResumeAnalysisController.java**

REST API entry point with CORS support for Vercel frontend.

**Endpoints:**

- `POST /api/analyze` - Upload and analyze resume
- `GET /api/history` - Get last 10 analyses
- `GET /api/analysis/{id}` - Get specific analysis
- `GET /api/analysis/{id}/report` - Download PDF report
- `GET /api/health` - Health check

### **ResumeAnalysisService.java**

Core business logic for resume analysis.

**Methods:**

- `analyze(file, jobDescription)` - Main analysis workflow
- `history()` - Retrieve analysis history
- `findById(id)` - Find analysis by ID
- `generatePdfReport(analysis)` - Generate PDF

**Flow:**

1. Extract text from PDF
2. Match skills against predefined list
3. Generate AI suggestions (if Gemini API configured)
4. Calculate ATS score
5. Save to MongoDB
6. Return results

### **PdfTextExtractorService.java**

Extracts text from PDF files using Apache PDFBox.

**Methods:**

- `extractText(multipartFile)` - Extract text from PDF
- Handles parsing and text cleaning

### **SkillMatcherService.java**

Matches resume skills against job description and predefined database.

**Features:**

- Skill extraction from resume text
- Job description skill requirements
- Matched vs. missing skills
- Skill suggestions

---

## 📊 MongoDB Database Schema

**Database:** `resumeats`
**Collection:** `resume_analyses`

**Document Structure:**

```json
{
  "_id": "ObjectId",
  "fileName": "resume.pdf",
  "extractedText": "Full resume text...",
  "atsScore": 78,
  "fitLevel": "Good",
  "matchedSkills": ["Java", "Spring Boot"],
  "missingSkills": ["Docker"],
  "sectionChecks": "All sections present",
  "formattingWarnings": "Use bullet points",
  "keywordInsights": "45 relevant keywords found",
  "aiSuggestions": ["Suggestion 1", "Suggestion 2"],
  "suggestedSummary": "Professional summary suggestion",
  "suggestedBullets": "Sample bullet points",
  "strengths": "Your strongest areas",
  "weakAreas": "Areas to improve",
  "improvements": "Specific improvement suggestions",
  "createdAt": "2024-05-24T10:30:00Z"
}
```

---

## 🌐 Deployment

### **Frontend Deployment (Vercel)**

1. Push code to GitHub
2. Connect Vercel to GitHub repo
3. Vercel automatically deploys on every push
4. Configured in `frontend/vercel.json` to proxy `/api/*` calls to Render

**Current:** https://resumate-zeta-lac.vercel.app/

### **Backend Deployment (Render)**

1. Create `Dockerfile` (multi-stage Maven + JRE build)
2. Push to GitHub
3. Connect Render to GitHub repo
4. Render builds Docker image automatically
5. Set environment variables:
   - `MONGODB_URI` - MongoDB Atlas connection string
   - `GEMINI_API_KEY` - (optional) Google Gemini API key

**Current:** https://resumate-backend-xy8b.onrender.com

**Health Check:** Render checks `/api/health` every 30 seconds

### **Database Deployment (MongoDB Atlas)**

Already running on MongoDB cloud infrastructure. No manual deployment needed.

**Database:** `resumeats`

---

## 🔑 Environment Variables

### Required for Backend

```env
# MongoDB Connection String (REQUIRED)
MONGODB_URI=mongodb+srv://username:password@cluster0.xxxxx.mongodb.net/resumeats?retryWrites=true&w=majority&appName=Cluster0

# Google Gemini API Key (OPTIONAL - AI suggestions work without it)
GEMINI_API_KEY=your_api_key_here

# Server Port (OPTIONAL - defaults to 8080)
PORT=8080
```

### Where to Set:

- **Local Development:** PowerShell or `.env` file
- **Render:** Environment variables in Dashboard → Settings
- **Frontend (Vercel):** `frontend/config.js` has API URL

---

## 🛠️ Building & Testing

### Build Locally

```bash
mvn clean package
```

### Run Tests

```bash
mvn test
```

### Build Docker Image

```bash
docker build -t resumate:latest .
docker run -p 8080:8080 -e MONGODB_URI="your_uri" resumate:latest
```

---

## 🐛 Troubleshooting

### Issue: "Analysis Failed" or 500 Error

**Causes:**

- MongoDB connection string incorrect
- MongoDB user credentials wrong
- Database `resumeats` doesn't exist
- User lacks read/write permissions

**Solution:**

1. Verify `MONGODB_URI` in Render environment variables
2. Check MongoDB Atlas database access user permissions
3. Ensure database `resumeats` exists in MongoDB Atlas
4. Check Render logs: Dashboard → Logs

### Issue: API Returns 404

**Cause:** Backend API not running or URL incorrect

**Solution:**

- Check Render is running: https://resumate-backend-xy8b.onrender.com/api/health
- Verify frontend uses correct API URL

### Issue: CORS Errors

**Cause:** Frontend and backend origin mismatch

**Solution:**

- Ensure `@CrossOrigin(origins = "*")` is in `ResumeAnalysisController.java`
- Check Vercel rewrites in `frontend/vercel.json`

### Issue: File Upload Not Working

**Cause:** File size too large (max 8MB)

**Solution:**

- Compress resume PDF
- Max file size configured in `application.properties`: `spring.servlet.multipart.max-file-size=8MB`

---

## 🤝 Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👨‍💻 Author

**Kritika** - AI Resume Analyzer & ATS Checker

- GitHub: https://github.com/kritika0519
- Email: kritika0519@gmail.com

---

## 🙏 Acknowledgments

- Apache PDFBox for PDF handling
- Google Gemini API for AI suggestions
- Spring Boot for framework
- MongoDB for database
- Vercel & Render for deployment platforms

---

## 📞 Support

If you need help:

1. Check this README's Troubleshooting section
2. Check GitHub Issues
3. Review Render logs for backend errors
4. Review browser console (F12) for frontend errors

---

**Last Updated:** May 24, 2026

**Project Status:** ✅ Production Ready

**Live Demo:** https://resumate-zeta-lac.vercel.app/
