# 📄 Resumate - AI Resume Analyzer & ATS Checker

**Live Demo:** https://resumate-zeta-lac.vercel.app/

An AI-powered resume analysis tool that helps job seekers optimize their resumes for Applicant Tracking Systems (ATS).

---

## 📊 What is Resumate?

Resumate analyzes your resume and gives you:
- **ATS Score** (0-100) - How likely your resume will pass automated screening
- **Skill Matching** - Which skills match the job description
- **AI Suggestions** - Specific improvements powered by Google Gemini
- **PDF Report** - Download a detailed analysis report
- **Analysis History** - Track all your analyses

---

## 🛠️ Tech Stack

| Layer | Technology | Deployed On |
|-------|-----------|------------|
| **Frontend** | HTML5, CSS3, JavaScript, Bootstrap 5 | Vercel |
| **Backend** | Java 17, Spring Boot 3.3.5 | Render (Docker) |
| **Database** | MongoDB Atlas | Cloud |
| **PDF Processing** | Apache PDFBox 3.0.3 | Backend |
| **AI** | Google Gemini API (Optional) | Cloud |

---

## 💡 How It Works

```
1. User uploads PDF resume
        ↓
2. Backend extracts text using Apache PDFBox
        ↓
3. Analysis Engine:
   - Calculates ATS score
   - Matches skills
   - Detects formatting issues
   - Analyzes keywords
        ↓
4. AI generates suggestions (Gemini API)
        ↓
5. Results saved to MongoDB
        ↓
6. Frontend displays results + PDF download option
```

### Real-Time Use Case

Resumate is designed for **real-time instant analysis**:
- ✅ **Fast Upload** - Analyzes within seconds
- ✅ **Instant Score** - ATS score calculated immediately
- ✅ **Live Feedback** - See results right away
- ✅ **No Waiting** - No batch processing delays
- ✅ **Multiple Analyses** - Analyze unlimited resumes
- ✅ **Compare Results** - Track improvements over time

Perfect for:
- Job seekers optimizing resumes before applying
- Career coaches analyzing client resumes
- HR teams reviewing candidate quality
- Resume review sessions in real-time

---

## 📁 Folder Structure

```
Resumate/
│
├── src/main/java/com/resumeanalyzer/
│   ├── ResumeAnalyzerApplication.java          ← Spring Boot entry point
│   ├── controller/
│   │   └── ResumeAnalysisController.java       ← REST API endpoints
│   ├── service/
│   │   ├── ResumeAnalysisService.java          ← Main analysis logic
│   │   ├── PdfTextExtractorService.java        ← Extract PDF text
│   │   ├── SkillMatcherService.java            ← Match skills
│   │   ├── AiSuggestionService.java            ← Gemini API integration
│   │   ├── PdfReportService.java               ← Generate PDF report
│   │   └── ResumeInsightService.java           ← Analysis insights
│   ├── model/
│   │   └── ResumeAnalysis.java                 ← MongoDB document model
│   ├── repository/
│   │   └── ResumeAnalysisRepository.java       ← Database access layer
│   └── dto/
│       ├── AnalysisResponse.java               ← API response objects
│       └── AnalysisSummaryResponse.java
│
├── src/main/resources/
│   ├── application.properties                   ← Spring Boot config
│   └── static/
│       ├── index.html                          ← Frontend UI
│       ├── css/styles.css                      ← Bootstrap styling
│       ├── js/app.js                           ← Frontend logic
│       └── assets/                             ← Images
│
├── pom.xml                                     ← Maven dependencies
├── Dockerfile                                  ← Docker build config
├── README.md                                   ← This file
└── database-schema.sql                         ← MongoDB schema
```

---

## 🚀 Key Endpoints

```
POST /api/analyze          → Upload resume + analyze
GET /api/history           → Get last 10 analyses
GET /api/analysis/{id}     → Get specific analysis
GET /api/analysis/{id}/report → Download PDF
GET /api/health            → Health check
```

---

## 🏗️ Architecture

```
Browser (Vercel Frontend)
    ↓ API calls
Render Backend (Java Spring Boot)
    ↓ Queries
MongoDB Atlas (Cloud Database)
```

---

## 🔧 Quick Start

### Local Development

1. **Clone Repository**
   ```bash
   git clone https://github.com/kritika0519/Resumate.git
   cd Resumate
   ```

2. **Setup MongoDB**
   - Go to https://mongodb.com/cloud/atlas
   - Create free cluster
   - Create user with password
   - Copy connection string

3. **Set Environment Variable**
   ```powershell
   $env:MONGODB_URI="mongodb+srv://user:pass@cluster0.xxxxx.mongodb.net/resumeats?retryWrites=true&w=majority&appName=Cluster0"
   ```

4. **Run Application**
   ```bash
   mvn clean spring-boot:run
   ```

5. **Open in Browser**
   ```
   http://localhost:8080
   ```

---

## 📦 Dependencies

**Backend:**
- Spring Boot Web (REST API)
- Spring Data MongoDB (Database)
- Apache PDFBox (PDF processing)
- Google Gemini API (AI suggestions)

**Frontend:**
- Bootstrap 5 (Responsive UI)
- Vanilla JavaScript (No framework)

---

## 🌐 Deployment

- **Frontend:** Vercel (Auto-deployed on GitHub push)
- **Backend:** Render (Docker container, auto-deployed)
- **Database:** MongoDB Atlas (Cloud)

**Live App:** https://resumate-zeta-lac.vercel.app/

---

## 🔑 Environment Variables

```env
MONGODB_URI=mongodb+srv://username:password@cluster.xxxxx.mongodb.net/resumeats?retryWrites=true&w=majority&appName=Cluster0

GEMINI_API_KEY=your_api_key_here (Optional)
```

---

## 💾 Database Schema

**Database:** `resumeats`  
**Collection:** `resume_analyses`

```json
{
  "_id": ObjectId,
  "fileName": "resume.pdf",
  "extractedText": "Full text...",
  "atsScore": 78,
  "fitLevel": "Good",
  "matchedSkills": ["Java", "Spring"],
  "missingSkills": ["Docker"],
  "aiSuggestions": ["Add Docker skills"],
  "createdAt": ISODate("2024-05-24T...")
}
```

---

## ✅ Features Working

- ✅ Upload PDF resume
- ✅ Extract text from PDF
- ✅ Calculate ATS score
- ✅ Match skills
- ✅ AI suggestions
- ✅ PDF reports
- ✅ Analysis history
- ✅ Responsive UI
- ✅ Real-time analysis

---

## 🎯 Why Real-Time Analysis?

**Traditional Resume Review:**
- Wait days for feedback
- Manual analysis is time-consuming
- Limited insights

**Resumate Real-Time:**
- ✨ Instant ATS score
- 🔄 Multiple iterations quick
- 📊 Comprehensive analysis
- 🤖 AI-powered insights
- ⚡ Compare versions instantly
- 🎯 Perfect for job application workflow

Perfect for job seekers who need to:
- Test resume variations quickly
- Get immediate feedback
- Optimize before applying
- Track improvements

---

## 🐛 Troubleshooting

**"Analysis Failed"**
- Check MongoDB connection
- Verify MONGODB_URI in Render environment

**"File Upload Not Working"**
- File must be PDF
- Max size: 8MB

**"No History Shown"**
- MongoDB might not be connected
- Check backend logs

---

## 📝 License

MIT License

---

**GitHub:** https://github.com/kritika0519/Resumate  
**Live Demo:** https://resumate-zeta-lac.vercel.app/

---

**Status:** ✅ Production Ready | Last Updated: May 24, 2026
