import os

from dotenv import load_dotenv
from google import genai
from google.genai import types
from pydantic import BaseModel, Field
from typing import Literal


# Load variables from .env
load_dotenv()

API_KEY = os.getenv("GEMINI_API_KEY")

if not API_KEY:
    raise RuntimeError("GEMINI_API_KEY is missing from .env")


# Create Gemini client
client = genai.Client(api_key=API_KEY)


# This is the exact structure we want Gemini to return
class ScamAnalysis(BaseModel):
    risk_score: int = Field(ge=0, le=100)
    level: Literal["LOW", "MEDIUM", "HIGH"]
    category: str
    confidence: Literal["low", "medium", "high"]
    reasons: list[str]
    explanation: str
    recommendation: str


def analyze_message(source: str, text: str) -> ScamAnalysis:

    prompt = f"""
You are ScamShield AI, an AI system that detects scam attempts
in messages and notifications.

Analyze the following message.

SOURCE:
{source}

MESSAGE:
{text}

Determine whether this message is likely to be a scam.

Look for:
- urgency or threats
- requests for OTPs, passwords, PINs or banking information
- suspicious payment requests
- suspicious links
- KYC or account verification requests
- fake bank or government impersonation
- courier scams
- job scams
- investment scams
- lottery or prize scams
- electricity or recharge scams
- APK or app installation requests
- police or digital-arrest impersonation
- requests for sensitive personal information

Important rules:

1. Do not assume every link is malicious.
2. Consider the entire message and its context.
3. Do not claim absolute certainty.
4. risk_score must be between 0 and 100.
5. LOW means the message appears relatively safe.
6. MEDIUM means there are suspicious characteristics.
7. HIGH means there are strong signs of a scam.
8. Give short, understandable reasons.
9. Do not reveal internal reasoning or chain-of-thought.
10. Give practical safety advice to the user.

Return the result using the requested structured format.
"""

    response = client.models.generate_content(
        model="gemini-3.5-flash-lite",
        contents=prompt,
        config=types.GenerateContentConfig(
            response_mime_type="application/json",
            response_schema=ScamAnalysis,
        ),
    )

    return response.parsed