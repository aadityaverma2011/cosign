# 🔐 Cosign – COSE Sign1 API Backend with Python Verifier

This repository implements a secure Spring Boot backend and a Python verification script for performing **COSE (CBOR Object Signing and Encryption) Sign1** operations using ECDSA keys. It enables cryptographically secure signing of CBOR payloads using centrally managed keys, supporting scenarios like digital credentials (e.g., mDocs).

---

## 📌 Overview

This project is split into two main parts:

1. **Spring Boot Backend** (`/`)
   - Exposes an HTTP POST endpoint `/coseSign`
   - Accepts a CBOR-encoded payload and headers (Base64URL encoded)
   - Produces a COSE_Sign1-compliant signature using an in-memory EC key pair
   - Returns a `Base64URL-encoded` COSE_Sign1 structure

2. **Python Verifier** (`/verification-python`)
   - Generates valid CBOR-encoded payloads and headers
   - Sends requests to `/coseSign`
   - Decodes and verifies the COSE signature using the corresponding public key

---

## ⚙️ Backend (Spring Boot)

### ✅ Features
- Secure HTTP endpoint `/coseSign`
- Follows the COSE_Sign1 structure (RFC 8152)
- Uses `ES256` (ECDSA over P-256) for signing
- Keys managed internally (mock HSM)
- Public key exported automatically to `ec_public_key.txt`

### 📦 Request Format
```json
POST /coseSign
Content-Type: application/json

{
  "id": "req-1",
  "version": "1.0",
  "requesttime": "2025-05-11T12:00:00Z",
  "metadata": {},
  "request": {
    "cosePayload": "<Base64URL CBOR payload>",
    "applicationId": "test-app",
    "referenceId": "ref-001",
    "coseProtectedHeader": "<Base64URL CBOR map>",
    "coseUnprotectedHeader": "<Base64URL CBOR map>"
  }
}
