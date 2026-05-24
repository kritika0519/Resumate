# 🚀 RESUMATE - QUICK REFERENCE GUIDE

## What is Resumate?

Resumate is a **full-stack web application** that helps job seekers optimize their resumes for Applicant Tracking Systems (ATS). It analyzes resumes against job descriptions and provides AI-powered suggestions to improve your chances of getting past automated systems.

---

## Architecture Overview

```
YOUR BROWSER
     ↓
VERCEL (Frontend) ← YOU USE THIS LINK
     ↓ (Calls /api/*)
RENDER (Backend API - Java Spring Boot)
     ↓ (Queries)
MONGODB ATLAS (Cloud Database)
```

---

## 🎯 WHICH LINK TO USE?

### **For Public Demo / Users:**

```
👉 https://resumate-zeta-lac.vercel.app/
```

This is your **COMPLETE APPLICATION**. Everything is here:

- Upload resume (PDF)
- Paste job description (optional)
- Get analysis results
- Download PDF report
- View history

### **For Developers / API Access:**

```
Backend API: https://resumate-backend-xy8b.onrender.com/
Health Check: https://resumate-backend-xy8b.onrender.com/api/health
GitHub Repo: https://github.com/kritika0519/Resumate
```

---

## Tech Stack

| Layer        | Technology                 | Provider      | URL                                        |
| ------------ | -------------------------- | ------------- | ------------------------------------------ |
| **Frontend** | HTML, CSS, JS, Bootstrap   | Vercel        | https://resumate-zeta-lac.vercel.app       |
| **Backend**  | Java 17, Spring Boot 3.3.5 | Render        | https://resumate-backend-xy8b.onrender.com |
| **Database** | MongoDB Atlas              | MongoDB Cloud | (Automatic)                                |
| **AI**       | Google Gemini API          | Google Cloud  | (Optional)                                 |

---

## How It Works

### Flow:

1. **User uploads PDF resume**
2. **Backend extracts text** using Apache PDFBox
3. **Analyzes content:**
   - Calculates ATS score (0-100)
   - Matches skills
   - Detects formatting issues
   - Checks keyword density
4. **AI suggests improvements** (Gemini API - optional)
5. **Saves analysis** to MongoDB
6. **Returns results** to frontend
7. **User can download PDF report**

### Key Features:

- ✅ ATS Score (0-100)
- ✅ Job Fit Analysis
- ✅ Skill Matching
- ✅ Keyword Analysis
- ✅ AI Suggestions
- ✅ PDF Reports
- ✅ Analysis History
- ✅ Formatting Warnings

---

## Project Structure

```
Resumate/
│
├── Backend (Java Spring Boot)
│   ├── src/main/java/com/resumeanalyzer/
│   │   ├── controller/       → REST API endpoints (@CrossOrigin for CORS)
│   │   ├── service/          → Business logic & analysis
│   │   ├── repository/       → MongoDB data access
│   │   ├── model/            → Database schema (@Document)
│   │   └── dto/              → API response objects
│   │
│   ├── Dockerfile            → Docker image for Render
│   ├── pom.xml               → Maven dependencies
│   └── src/main/resources/application.properties
│
├── Frontend (Static)
│   ├── src/main/resources/static/
│   │   ├── index.html        → Main UI
│   │   ├── css/styles.css    → Bootstrap 5 styling
│   │   ├── js/app.js         → JavaScript logic
│   │   └── assets/           → Images
│   │
│   └── frontend/vercel.json  → Vercel config (proxies to Render)
│
├── Database
│   ├── MongoDB Atlas          → Cloud database "resumeats"
│   └── Collection: resume_analyses
│
├── README.md                 → Full documentation
└── database-schema.sql       → Schema reference
```

---

## API Endpoints

All endpoints start with `https://resumate-backend-xy8b.onrender.com/api/` (or `http://localhost:8080/api/` locally)

### 1. Health Check

```
GET /api/health
Response: {"status":"ok"}
```

### 2. Analyze Resume

```
POST /api/analyze
Form Data:
  - resume: PDF file (required)
  - jobDescription: text (optional)

Response: {
  "id": "...",
  "atsScore": 78,
  "fitLevel": "Good",
  "matchedSkills": ["Java", "Spring"],
  "missingSkills": ["Docker"],
  "aiSuggestions": ["Add Docker skills", "..."],
  "createdAt": "2024-05-24..."
}
```

### 3. Get History (Last 10)

```
GET /api/history
Response: [
  {
    "id": "...",
    "fileName": "resume.pdf",
    "atsScore": 78,
    "fitLevel": "Good",
    "createdAt": "..."
  },
  ...
]
```

### 4. Get Analysis by ID

```
GET /api/analysis/{id}
Response: Full analysis details
```

### 5. Download PDF Report

```
GET /api/analysis/{id}/report
Response: PDF file
```

---

## Key Classes

### **ResumeAnalysisController.java**

- REST API endpoints
- CORS enabled for Vercel
- Handles file uploads
- Routes requests to services

### **ResumeAnalysisService.java**

- Core analysis logic
- Orchestrates workflow
- Saves to MongoDB

### **PdfTextExtractorService.java**

- Extracts text from PDF
- Uses Apache PDFBox

### **SkillMatcherService.java**

- Matches skills
- Compares with job description
- Identifies gaps

### **AiSuggestionService.java**

- Calls Gemini API for suggestions
- Falls back to rule-based suggestions

### **PdfReportService.java**

- Generates PDF reports
- Professional formatting

---

## Environment Variables

### Required:

```env
MONGODB_URI=mongodb+srv://username:password@cluster0.xxxxx.mongodb.net/resumeats?retryWrites=true&w=majority&appName=Cluster0
```

### Optional:

```env
GEMINI_API_KEY=your_api_key_here
PORT=8080
```

### Where to set:

- **Local:** PowerShell `$env:MONGODB_URI="..."`
- **Render:** Dashboard → Settings → Environment
- **Vercel:** Dashboard → Settings → Environment (not needed for frontend)

---

## Development Quick Start

### 1. Clone

```bash
git clone https://github.com/kritika0519/Resumate.git
cd Resumate
```

### 2. Setup MongoDB

- Go to mongodb.com/cloud/atlas
- Create free cluster
- Create user with password
- Copy connection string

### 3. Set Environment

```powershell
$env:MONGODB_URI="mongodb+srv://user:pass@cluster.xxxxx.mongodb.net/resumeats?retryWrites=true&w=majority&appName=Cluster0"
```

### 4. Run

```bash
mvn clean spring-boot:run
```

### 5. Test

- Frontend: http://localhost:8080
- API: http://localhost:8080/api/health

---

## Deployment

### Frontend (Vercel)

- Automatically deployed when you push to GitHub
- Uses `frontend/vercel.json` to proxy API calls to Render
- Zero-config deployment

### Backend (Render)

- Automatically deployed when you push to GitHub
- Uses `Dockerfile` to build Docker image
- Environment variables set in Render dashboard
- Health checks: every 30 seconds

### Database (MongoDB Atlas)

- Already running in cloud
- No manual deployment needed
- Secure access with username/password

---

## Important Files

| File                                                    | Purpose                           |
| ------------------------------------------------------- | --------------------------------- |
| `pom.xml`                                               | Maven dependencies & build config |
| `Dockerfile`                                            | Docker image for Render           |
| `README.md`                                             | Full documentation                |
| `application.properties`                                | Spring Boot config                |
| `src/main/.../controller/ResumeAnalysisController.java` | API endpoints                     |
| `src/main/.../service/ResumeAnalysisService.java`       | Business logic                    |
| `src/main/resources/static/`                            | Frontend (HTML, CSS, JS)          |
| `frontend/vercel.json`                                  | Vercel configuration              |

---

## GitHub Links

- **Repository:** https://github.com/kritika0519/Resumate
- **Live Demo:** https://resumate-zeta-lac.vercel.app/
- **Backend API:** https://resumate-backend-xy8b.onrender.com/

---

## Troubleshooting

### API Returns 500 Error

**Check:**

- Is Render running? (visit `/api/health`)
- Is MongoDB connection string correct?
- Do you have database access permissions in MongoDB?

### Upload Not Working

**Check:**

- File is PDF format
- File size < 8MB
- Browser console (F12) for errors

### No History Shown

**Check:**

- Has MongoDB connected?
- Any analyses saved yet?

---

## MongoDB Schema

**Database:** `resumeats`
**Collection:** `resume_analyses`

```json
{
  "_id": ObjectId,
  "fileName": "resume.pdf",
  "extractedText": "Full text",
  "atsScore": 78,
  "fitLevel": "Good",
  "matchedSkills": ["Java", "Spring"],
  "missingSkills": ["Docker"],
  "keywordInsights": "45 keywords",
  "aiSuggestions": ["Suggestion 1"],
  "sectionChecks": "Good",
  "formattingWarnings": "Warning",
  "createdAt": ISODate("2024-05-24T...")
}
```

---

## Summary

✅ **Project is working!**

- Frontend deployed on **Vercel** (static hosting)
- Backend deployed on **Render** (Docker container)
- Database deployed on **MongoDB Atlas** (cloud)
- Everything communicates via REST API
- CORS enabled for cross-domain requests
- Automatic deployments on GitHub push

**Use this link for everything:** https://resumate-zeta-lac.vercel.app/

---

**Created:** May 24, 2026  
**Status:** ✅ Production Ready  
**GitHub:** https://github.com/kritika0519/Resumate
