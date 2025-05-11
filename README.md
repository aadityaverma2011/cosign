Cosign – COSE Sign1 API Backend with Python Verifier
====================================================

This repository implements a secure Spring Boot backend and a Python verification script for performing **COSE (CBOR Object Signing and Encryption) Sign1** operations using ECDSA keys. It enables cryptographically secure signing of CBOR payloads using centrally managed keys, supporting scenarios like digital credentials (e.g., mDocs).

Project Overview
----------------

This project is split into two main components:

1.  **Spring Boot Backend** (/)
    
    *   Exposes an HTTP POST endpoint /coseSign
        
    *   Accepts a CBOR-encoded payload and headers (Base64URL encoded)
        
    *   Produces a COSE\_Sign1-compliant signature using an in-memory EC key pair
        
    *   Returns a Base64URL-encoded COSE\_Sign1 structure
        
2.  **Python Verifier** (/verification-python)
    
    *   Generates valid CBOR-encoded payloads and headers
        
    *   Sends requests to /coseSign
        
    *   Decodes and verifies the COSE signature using the corresponding public key
        

Implementation Plan Checklist
-----------------------------

StepDescriptionStatus1. API EndpointPOST /coseSign using Spring Boot REST controller✅2. Request HandlingParse JSON input, Base64URL-decode and CBOR-decode headers and payload✅3. Key SelectionExtract kid from headers and retrieve corresponding private key✅4. COSE SigningConstruct Sig\_structure and sign with ECDSA over P-256✅5. Response FormationReturn Base64URL-encoded COSE\_Sign1, timestamp, and error array✅6. Error HandlingValidate inputs and report key/signing-related errors in a structured way✅

Backend (Spring Boot)
---------------------

### Key Features

*   HTTP POST endpoint /coseSign
    
*   COSE\_Sign1 signature generation using ES256 (ECDSA over P-256)
    
*   In-memory key management (mock HSM implementation)
    
*   Public key exported automatically to ec\_public\_key.txt
    

### Request Format

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   POST /coseSign  Content-Type: application/json  {    "id": "req-1",    "version": "1.0",    "requesttime": "2025-05-11T12:00:00Z",    "metadata": {},    "request": {      "cosePayload": "",      "applicationId": "test-app",      "referenceId": "ref-001",      "coseProtectedHeader": "",      "coseUnprotectedHeader": ""    }  }   `

### Response Format

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   {    "id": "req-1",    "version": "1.0",    "responsetime": "2025-05-11T12:01:00Z",    "metadata": {},    "response": {      "coseSignedData": "",      "timestamp": "2025-05-11T12:01:00Z"    },    "errors": []  }   `