# Frontend Deployment

This folder is the Vercel frontend for AI Resume Analyzer & ATS Checker.

Before deploying to Vercel, update `config.js`:

```js
window.RESUME_AI_CONFIG = {
    API_BASE_URL: "https://your-render-backend-url.onrender.com"
};
```

Then deploy this `frontend` folder as a static project on Vercel.
