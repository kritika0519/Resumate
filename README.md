# 📄 AI Resume Analyzer

> **AI-powered resume analysis tool** — Upload your resume, paste a job description, and instantly get ATS compatibility scoring, skill matching, AI suggestions, and a professional PDF report.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-green.svg)](https://www.mongodb.com/cloud/atlas)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## ✨ Features

- 📊 **ATS Scoring** — Get a realistic ATS (Applicant Tracking System) score
- 🎯 **Job Fit Analysis** — See how well your resume matches the job description
- 🤖 **AI-Powered Insights** — Intelligent suggestions powered by Google Gemini API
- 📝 **Skill Matching** — Identify matched and missing skills
- 🔍 **Keyword Analysis** — Analyze keyword frequency and impact
- ✅ **Format Checking** — Get ATS formatting warnings and tips
- 💼 **PDF Reports** — Download professional analysis reports
- 📜 **History** — Track all your analyses
- 🎨 **Modern UI** — Responsive Bootstrap 5 interface

## 🛠️ Tech Stack

| Layer              | Technology                                      |
| ------------------ | ----------------------------------------------- |
| **Backend**        | Java 17, Spring Boot 3.3.5, Spring Data MongoDB |
| **Database**       | MongoDB Atlas (Cloud)                           |
| **PDF Processing** | Apache PDFBox 3.0.3                             |
| **Frontend**       | HTML5, CSS3, JavaScript, Bootstrap 5            |
| **AI**             | Google Gemini API (optional, with fallback)     |

## 📋 Prerequisites

- ✅ Java 17+
- ✅ Maven 3.6+
- ✅ MongoDB Atlas account (free tier available)
- ✅ (Optional) Google Gemini API key

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/ai-resume-analyzer.git
cd ai-resume-analyzer
```

### 2. Setup MongoDB Atlas

1. Go to [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)
2. Create a free cluster
3. Create a database user with a strong password
4. Add your IP address to Network Access
5. Create database: `resume_analyzer_db`
6. Copy the connection string (Java driver format)

### 3. Configure Environment Variables

Create a `.env` file in the project root:

```env
MONGODB_URI=mongodb+srv://username:password@cluster0.xxxxx.mongodb.net/resume_analyzer_db?retryWrites=true&w=majority&appName=Cluster0
GEMINI_API_KEY=your_api_key_here
FRONTEND_URL=http://localhost:3000
```

**Or set in PowerShell:**

```powershell
$env:MONGODB_URI="your_mongodb_connection_string"
$env:GEMINI_API_KEY="your_gemini_api_key"
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The app starts at: **http://localhost:8080**

Check health: **http://localhost:8080/api/health**

## 📡 API Endpoints

### Upload & Analyze Resume

```http
POST /api/analyze
Content-Type: multipart/form-data

Parameters:
- resume (file): PDF file of resume [Required]
- jobDescription (text): Job description text [Optional]
```

**Response:**

```json
{
  "id": "507f1f77bcf86cd799439011",
  "fileName": "resume.pdf",
  "atsScore": 78,
  "fitLevel": "Good",
  "matchedSkills": ["Java", "Spring Boot", "MongoDB"],
  "missingSkills": ["Docker", "Kubernetes"],
  "aiSuggestions": [
    "Add Docker experience to your resume",
    "Highlight cloud deployment skills"
  ],
  "createdAt": "2024-05-24T10:30:00Z"
}
```

### Get Analysis by ID

```http
GET /api/analysis/{id}
```

### Download PDF Report

```http
GET /api/analysis/{id}/report
```

### View Analysis History (Last 10)

```http
GET /api/history
```

### Health Check

```http
GET /api/health
```

## 🏗️ Project Structure

```
ai-resume-analyzer/
├── src/main/java/com/resumeanalyzer/
│   ├── controller/          # REST API endpoints
│   ├── service/             # Business logic
│   ├── model/               # MongoDB documents
│   ├── repository/          # Data access
│   └── dto/                 # Data transfer objects
├── src/main/resources/
│   ├── application.properties  # Spring Boot config
│   └── static/              # Frontend files
├── frontend/                # React/Vue frontend (optional)
├── pom.xml                  # Maven dependencies
└── README.md
```

## 🔧 Configuration Files

- **`application.properties`** — Spring Boot configuration
- **`.env.example`** — Environment variables template
- **`.gitignore`** — Git exclude patterns
- **`pom.xml`** — Maven dependencies

## 📦 Build & Deploy

### Local Build

```bash
mvn clean package
```

Compiled JAR: `target/ai-resume-analyzer-1.0.0.jar`

### Run JAR Locally

```bash
java -jar target/ai-resume-analyzer-1.0.0.jar
```

### Deploy to Render (Backend)

1. Push to GitHub
2. Go to [Render.com](https://render.com)
3. Create new Web Service
4. Connect GitHub repo
5. Set environment variables:
   - `MONGODB_URI`
   - `GEMINI_API_KEY`
   - `FRONTEND_URL`
6. Build: `mvn clean package -DskipTests`
7. Start: `java -jar target/ai-resume-analyzer-1.0.0.jar`

### Deploy to Vercel (Frontend)

1. Connect GitHub repo to [Vercel](https://vercel.com)
2. Set root directory to `frontend/`
3. Deploy!

## 🔐 Security Notes

- **Never commit `.env`** — Only `.env.example` is in git
- **Change MongoDB password** before deploying to production
- **Secure API keys** — Use environment variables, never hardcode
- **HTTPS only** — Use HTTPS in production deployments

## 📚 API Testing

### Using cURL

```bash
# Test health check
curl http://localhost:8080/api/health

# Upload resume (with file)
curl -X POST http://localhost:8080/api/analyze \
  -F "resume=@resume.pdf" \
  -F "jobDescription=Python Developer wanted with 5+ years experience"
```

### Using Postman

1. Import the API endpoints above
2. Set `http://localhost:8080` as base URL
3. Upload a PDF resume file
4. Send request and check response

## 🐛 Troubleshooting

| Issue                      | Solution                                                           |
| -------------------------- | ------------------------------------------------------------------ |
| MongoDB connection error   | Verify MONGODB_URI and add your IP to MongoDB Atlas Network Access |
| Port 8080 in use           | Change `server.port=9090` in `application.properties`              |
| Maven build fails          | Ensure Java 17+ with `java -version`                               |
| PDF upload fails           | Check file size limit (max 8MB in `application.properties`)        |
| AI suggestions not working | GEMINI_API_KEY is optional; app uses rule-based fallback           |

## 📝 Example Workflow

1. **Open** http://localhost:8080
2. **Upload** your resume (PDF)
3. **Paste** job description (optional)
4. **Click** "Analyze"
5. **Review** instant ATS score and recommendations
6. **Download** PDF report

## 🤝 Contributing

Found a bug or want to improve? Feel free to:

1. Fork the repo
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m "Add amazing feature"`
4. Push: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License — see [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spring Boot** — Framework foundation
- **MongoDB** — Cloud database
- **Apache PDFBox** — PDF processing
- **Google Gemini** — AI suggestions
- **Bootstrap 5** — Frontend styling

## 📧 Support

Have questions? Open an issue on GitHub or contact the maintainers.

---

**Made with ❤️ for job seekers**
