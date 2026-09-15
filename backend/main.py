from fastapi import FastAPI
from pydantic import BaseModel

from ai_engine import analyze_message, ScamAnalysis


app = FastAPI(
    title="ScamShield AI",
    description="AI-powered scam detection and protection API",
    version="0.2.0"
)


class ScamRequest(BaseModel):
    source: str
    text: str


@app.get("/")
def root():
    return {
        "app": "ScamShield AI",
        "status": "online",
        "message": "AI scam protection API is running."
    }


@app.post("/analyze", response_model=ScamAnalysis)
def analyze(request: ScamRequest):

    result = analyze_message(
        source=request.source,
        text=request.text
    )

    return result