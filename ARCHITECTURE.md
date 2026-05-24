# 🏗️ RESUMATE - ARCHITECTURE OVERVIEW

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          USER'S BROWSER                                 │
│                     https://resumate-zeta-lac.vercel.app                │
└────────────────────────────┬────────────────────────────────────────────┘
                             │
                             │ HTTPS Request
                             │ (Static HTML/CSS/JS)
                             ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    VERCEL (CDN - Frontend)                              │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐  │
│  │  index.html                                                     │  │
│  │  ┌─────────────────┐  ┌──────────────┐  ┌───────────────────┐  │  │
│  │  │  Upload Section │  │  Analysis UI │  │  History Section  │  │  │
│  │  └─────────────────┘  └──────────────┘  └───────────────────┘  │  │
│  │                                                                  │  │
│  │  app.js - JavaScript Logic                                      │  │
│  │  - File upload handling                                         │  │
│  │  - API calls to backend                                         │  │
│  │  - Result display                                               │  │
│  └─────────────────────────────────────────────────────────────────┘  │
│                                                                         │
│  vercel.json - Rewrites                                                │
│  /api/*  ──────────────────> Backend                                   │
└────────────────────────────┬────────────────────────────────────────────┘
                             │
        API Call: POST /api/analyze + PDF file
        or: GET /api/history
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────┐
│              RENDER (Docker Container - Backend API)                    │
│         https://resumate-backend-xy8b.onrender.com                     │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐  │
│  │  Spring Boot Application (Java 17)                             │  │
│  │                                                                 │  │
│  │  ResumeAnalyzerApplication.java (Entry Point)                  │  │
│  │        │                                                        │  │
│  │        ├──> ResumeAnalysisController.java                      │  │
│  │        │    @CrossOrigin(origins = "*")                        │  │
│  │        │    REST Endpoints:                                    │  │
│  │        │    - POST /api/analyze                                │  │
│  │        │    - GET /api/history                                 │  │
│  │        │    - GET /api/analysis/{id}                           │  │
│  │        │    - GET /api/analysis/{id}/report                    │  │
│  │        │    - GET /api/health                                  │  │
│  │        │                                                        │  │
│  │        └──> ResumeAnalysisService.java                         │  │
│  │             - Main analysis orchestration                      │  │
│  │             - Business logic                                   │  │
│  │             - Data processing                                  │  │
│  │             │                                                  │  │
│  │             ├──> PdfTextExtractorService.java                  │  │
│  │             │    (Apache PDFBox - Extract text from PDF)       │  │
│  │             │                                                  │  │
│  │             ├──> SkillMatcherService.java                      │  │
│  │             │    (Match skills in resume vs job description)   │  │
│  │             │                                                  │  │
│  │             ├──> AiSuggestionService.java                      │  │
│  │             │    (Call Google Gemini API for suggestions)      │  │
│  │             │                                                  │  │
│  │             ├──> ResumeInsightService.java                     │  │
│  │             │    (Generate insights & analysis)                │  │
│  │             │                                                  │  │
│  │             └──> PdfReportService.java                         │  │
│  │                  (Generate PDF report)                         │  │
│  │                                                                 │  │
│  │        └──> ResumeAnalysisRepository.java                      │  │
│  │             (MongoDB data access)                              │  │
│  │                                                                 │  │
│  │  ┌─────────────────────────────────────────────────────────┐  │  │
│  │  │  application.properties                                 │  │  │
│  │  │  ├─ server.port=8080                                   │  │  │
│  │  │  ├─ spring.data.mongodb.uri=${MONGODB_URI}             │  │  │
│  │  │  ├─ spring.servlet.multipart.max-file-size=8MB         │  │  │
│  │  │  └─ gemini.api.key=${GEMINI_API_KEY}                   │  │  │
│  │  └─────────────────────────────────────────────────────────┘  │  │
│  └─────────────────────────────────────────────────────────────────┘  │
│                                                                         │
│  Port: 8080                                                             │
│  Health Check: Every 30 seconds                                         │
│  Deployed via: Dockerfile (Multi-stage build)                          │
└────────────────────────────┬────────────────────────────────────────────┘
                             │
         TCP Connection: Port 27017
         MongoDB Connection String
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────┐
│               MONGODB ATLAS (Cloud Database)                            │
│                    cluster0.qllvn.mongodb.net                          │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐  │
│  │  Database: resumeats                                            │  │
│  │                                                                 │  │
│  │  ┌───────────────────────────────────────────────────────────┐ │  │
│  │  │  Collection: resume_analyses                              │ │  │
│  │  │                                                            │ │  │
│  │  │  Documents:                                               │ │  │
│  │  │  {                                                         │ │  │
│  │  │    "_id": ObjectId,                                        │ │  │
│  │  │    "fileName": "resume.pdf",                              │ │  │
│  │  │    "extractedText": "...",                                │ │  │
│  │  │    "atsScore": 78,                                         │ │  │
│  │  │    "fitLevel": "Good",                                     │ │  │
│  │  │    "matchedSkills": ["Java", "Spring Boot"],              │ │  │
│  │  │    "missingSkills": ["Docker"],                           │ │  │
│  │  │    "aiSuggestions": [...],                                │ │  │
│  │  │    "createdAt": ISODate("2024-05-24T...")                 │ │  │
│  │  │  }                                                         │ │  │
│  │  └───────────────────────────────────────────────────────────┘ │  │
│  │                                                                 │  │
│  │  User: kritika1905                                              │  │
│  │  Auth: Username/Password                                        │  │
│  │  Role: Read/Write on "resumeats"                               │  │
│  │  Network: 0.0.0.0/0 (Allow all)                                │  │
│  └─────────────────────────────────────────────────────────────────┘  │
│                                                                         │
│  Hosting: MongoDB Cloud (AWS)                                           │
│  Tier: M0 (Free)                                                        │
│  Replicas: 3 (Automatic)                                                │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│           GOOGLE CLOUD (Optional - AI Suggestions)                      │
│                                                                         │
│  Google Gemini API 1.5 Flash                                            │
│  ├─ Endpoint: generativelanguage.googleapis.com/v1beta/models/...     │
│  ├─ API Key: ${GEMINI_API_KEY}                                          │
│  └─ Purpose: Generate AI suggestions for resume improvement            │
│                                                                         │
│  (If not configured, app uses rule-based fallback suggestions)         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Data Flow Diagram

```
USER UPLOADS RESUME
        │
        ▼
┌──────────────────────┐
│  Browser receives   │
│  PDF file selection │
└──────────────────────┘
        │
        ▼
┌──────────────────────────────────────────────┐
│  app.js sends:                               │
│  POST /api/analyze                           │
│  multipart/form-data                         │
│  - resume: [PDF file]                        │
│  - jobDescription: [optional text]           │
└──────────────────────────────────────────────┘
        │ (via Vercel → Render)
        ▼
┌──────────────────────────────────────────────┐
│  ResumeAnalysisController                    │
│  @PostMapping("/analyze")                    │
└──────────────────────────────────────────────┘
        │
        ▼
┌──────────────────────────────────────────────┐
│  ResumeAnalysisService.analyze()             │
│  Orchestrates the workflow                   │
└──────────────────────────────────────────────┘
        │
        ├─> Step 1: Extract PDF Text
        │   └─> PdfTextExtractorService
        │       └─> Apache PDFBox
        │           └─ Result: extracted_text
        │
        ├─> Step 2: Analyze Content
        │   ├─> Calculate ATS Score
        │   ├─> Check Formatting
        │   ├─> Extract Keywords
        │   └─> Result: atsScore, warnings, keywords
        │
        ├─> Step 3: Match Skills
        │   └─> SkillMatcherService
        │       ├─ Parse resume skills
        │       ├─ Parse job description
        │       ├─ Compare
        │       └─ Result: matchedSkills, missingSkills
        │
        ├─> Step 4: Generate AI Suggestions (if Gemini API key set)
        │   └─> AiSuggestionService
        │       ├─ Call Google Gemini API
        │       ├─ Process response
        │       └─ Result: aiSuggestions
        │
        ├─> Step 5: Generate Insights
        │   └─> ResumeInsightService
        │       └─ Result: strengths, weakAreas, improvements
        │
        ├─> Step 6: Save to MongoDB
        │   └─> ResumeAnalysisRepository
        │       ├─ Create document
        │       ├─ Save to "resume_analyses" collection
        │       └─ Result: _id (generated)
        │
        └─> Step 7: Return Response
            └─> JSON with all analysis data
                {
                  "id": "507f1f77bcf86cd799439011",
                  "atsScore": 78,
                  "matchedSkills": [...],
                  "aiSuggestions": [...],
                  ...
                }
        │
        ▼
┌──────────────────────────────────────────────┐
│  Frontend receives response                  │
│  Displays results to user                    │
│  - ATS Score                                 │
│  - Matched/Missing Skills                    │
│  - AI Suggestions                            │
│  - Download Report button                    │
└──────────────────────────────────────────────┘
        │
        ▼
┌──────────────────────────────────────────────┐
│  User can download PDF report                │
│  GET /api/analysis/{id}/report               │
│                                              │
│  Backend:                                    │
│  - Retrieves analysis from MongoDB           │
│  - PdfReportService generates PDF            │
│  - Sends to browser as download              │
└──────────────────────────────────────────────┘
```

---

## Deployment Flow

```
GITHUB REPOSITORY (github.com/kritika0519/Resumate)
        │
        ├─────────────────────────────────┬───────────────────────┐
        │                                 │                       │
        ▼ (Git Push)                      ▼ (Git Push)           ▼ (Git Push)
    VERCEL                            RENDER                  MONGODB ATLAS
    (Frontend)                        (Backend)               (Database)
        │                                 │                       │
        ├─ Fetch code                    ├─ Fetch code          (No deployment)
        │                                 │                       |
        ├─ Install deps                  ├─ Read Dockerfile      Already running
        │                                 │                       
        ├─ Build                         ├─ Build Docker image
        │  (Static site)                 │  - Maven: mvn clean package
        │                                 │  - Docker: build image
        ├─ Deploy                        ├─ Push to registry
        │  (~1 min)                      │                       
        │                                 ├─ Start container
        └─ https://resumate-...         │  - Map port 8080
           zeta-lac.vercel.app          │  - Set env vars
                                        │  - Health checks
                                        │
                                        └─ https://resumate-backend-...
                                           xy8b.onrender.com
```

---

## Component Interactions

```
ResumeAnalysisController
        │
        ├─> ResumeAnalysisService
        │   └─ Core orchestration
        │
        ├─ Calls multiple services:
        │
        ├──> PdfTextExtractorService
        │    └─ Uses: Apache PDFBox
        │    └─ Returns: extracted_text
        │
        ├──> SkillMatcherService
        │    └─ Compares resume vs job description
        │    └─ Returns: matchedSkills, missingSkills
        │
        ├──> AiSuggestionService
        │    └─ Uses: Google Gemini API
        │    └─ Returns: aiSuggestions
        │
        ├──> ResumeInsightService
        │    └─ Analyzes content
        │    └─ Returns: strengths, weakAreas, improvements
        │
        ├──> PdfReportService
        │    └─ Generates PDF with analysis
        │    └─ Returns: PDF bytes
        │
        └──> ResumeAnalysisRepository
             └─ MongoDB data access
             └─ CRUD operations
             └─ Returns: saved documents
```

---

## Technology Stack Details

### Frontend Layer
- **Location:** `src/main/resources/static/`
- **Server:** Vercel CDN
- **Files:**
  - `index.html` - Main UI structure
  - `css/styles.css` - Bootstrap 5 styling
  - `js/app.js` - JavaScript logic
  - `assets/` - Images and resources

### Backend Layer
- **Language:** Java 17
- **Framework:** Spring Boot 3.3.5
- **Server:** Tomcat (embedded)
- **Port:** 8080
- **Deployment:** Render Docker container
- **Key Dependencies:**
  - Spring Web (REST API)
  - Spring Data MongoDB (Database access)
  - Apache PDFBox (PDF processing)

### Database Layer
- **Type:** NoSQL (MongoDB)
- **Hosting:** MongoDB Atlas (Cloud)
- **Database:** `resumeats`
- **Collection:** `resume_analyses`
- **Auth:** Username/Password
- **Access:** Anywhere (0.0.0.0/0)

### Optional: AI Layer
- **Provider:** Google Cloud
- **Service:** Gemini API 1.5 Flash
- **Purpose:** Generate suggestions
- **Status:** Optional (fallback available)

---

## Security Considerations

```
┌────────────────────────────────────────┐
│  Security Layers                       │
├────────────────────────────────────────┤
│                                        │
│  1. HTTPS/SSL                          │
│     ├─ Vercel: Auto SSL                │
│     ├─ Render: Auto SSL                │
│     └─ MongoDB Atlas: SSL               │
│                                        │
│  2. CORS                               │
│     ├─ @CrossOrigin(origins="*")       │
│     ├─ Allows frontend requests        │
│     └─ Verified by backend             │
│                                        │
│  3. MongoDB Authentication             │
│     ├─ Username: kritika1905           │
│     ├─ Password: encrypted             │
│     └─ Role-based access               │
│                                        │
│  4. File Upload Validation             │
│     ├─ Max size: 8MB                   │
│     ├─ File type: PDF only             │
│     └─ Server-side validation          │
│                                        │
│  5. API Keys                           │
│     ├─ Gemini API key: Environment var │
│     ├─ MongoDB URI: Environment var    │
│     └─ Never hardcoded                 │
│                                        │
│  6. Docker Container                   │
│     ├─ Non-root user (appuser)         │
│     ├─ Read-only filesystem            │
│     └─ Health checks enabled           │
│                                        │
└────────────────────────────────────────┘
```

---

## Scalability Considerations

### Current Setup (Working)
- MongoDB: M0 (free tier) ~512MB storage
- Render: Basic tier (~0.50 GB RAM)
- Vercel: Free tier (unlimited)

### To Scale Up
```
Increase Users
    │
    ├─> Upgrade MongoDB
    │   └─ M2+ tier for higher performance
    │
    ├─> Upgrade Render
    │   └─ Standard tier for more resources
    │
    ├─> Add Database Indexing
    │   └─ Index on: createdAt, userId (if added)
    │
    ├─> Add Caching
    │   └─ Redis for frequently accessed data
    │
    ├─> Add Load Balancer
    │   └─ Distribute traffic across servers
    │
    └─> Monitor Performance
        └─ Use Render/Vercel monitoring
```

---

**Created:** May 24, 2026  
**Architecture Version:** 1.0  
**Status:** ✅ Production Ready
