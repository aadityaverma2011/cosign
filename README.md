**Cosign – COSE Sign1 API Backend with Python Verifier**
========================================================

**Overview**
------------

The Cosign project implements a secure backend service for generating cryptographic signatures in compliance with the [COSE (CBOR Object Signing and Encryption)](https://datatracker.ietf.org/doc/html/rfc8152) standard. Built using **Spring Boot**, the backend exposes a RESTful endpoint that enables COSE\_Sign1 signing using ECDSA. A Python-based verifier script accompanies the backend to validate the generated COSE messages and ensure the integrity of the payload.

This project is designed to support secure document issuance workflows, such as **mDoc (mobile document) issuance**, by allowing applications to delegate signing operations to a centralized key-managed service without exposing private keys.

**Project Structure**
---------------------

### **1\. Spring Boot Backend**

*   **Purpose**: Handle COSE\_Sign1 signing using securely managed EC (P-256) keys.
    
*   **Endpoint**: POST /coseSign
    
*   **Key Handling**: Keys are stored in an in-memory key store (mock HSM) and are not exposed externally.
    
*   **Signing Algorithm**: ES256 (ECDSA over NIST P-256 curve).
    
*   **Public Key Export**: The public component of the key pair is automatically written to ec\_public\_key.txt for external verification.
    

### **2\. Python Verifier (Located in /verification-python)**

*   **Purpose**: Create a test COSE request, send it to the backend, decode the COSE\_Sign1 response, and verify its digital signature using the public key.
    
*   **Verification Tooling**: Built using pycose, cbor2, and requests.
    

**API Implementation Plan – Feature Checklist**
-----------------------------------------------

StepDescriptionStatus**1\. API Endpoint**Exposes /coseSign via a Spring Boot REST controller.✅**2\. Request Handling**Parses JSON input, Base64URL-decodes COSE fields, and decodes headers using CBOR.✅**3\. Key Selection**Extracts kid from headers and fetches corresponding key from the in-memory store.✅**4\. COSE Signing**Constructs the COSE Sig\_structure and signs using ECDSA.✅**5\. Response Formation**Returns Base64URL-encoded COSE\_Sign1 message along with a timestamp.✅**6\. Error Handling**Returns structured error responses for invalid inputs, key lookup failures, etc.✅

**Backend API Specification**
-----------------------------

### **Endpoint**

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   POST /coseSign   `

### **Request Headers**

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   Content-Type: application/json   `

### **Request Body Example**

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   {    "id": "req-1",    "version": "1.0",    "requesttime": "2025-05-11T12:00:00Z",    "metadata": {},    "request": {      "cosePayload": "",      "applicationId": "test-app",      "referenceId": "ref-001",      "coseProtectedHeader": "",      "coseUnprotectedHeader": ""    }  }   `

### **Response Body Example**

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   {    "id": "req-1",    "version": "1.0",    "responsetime": "2025-05-11T12:01:00Z",    "metadata": {},    "response": {      "coseSignedData": "",      "timestamp": "2025-05-11T12:01:00Z"    },    "errors": []  }   `

**Python Verifier**
-------------------

### **Location**

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   /verification-python   `

### **Dependencies**

Defined in requirements.txt:

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   requests==2.31.0  cbor2==5.6.0  pycose==1.0.1   `

### **Execution Instructions**

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   cd verification-python  pip install -r requirements.txt  python verify_cose_sign.py   `

### **Verifier Flow**

1.  Encodes a test payload and headers using CBOR.
    
2.  Encodes the CBOR structures to Base64URL.
    
3.  Sends them to the /coseSign endpoint.
    
4.  Decodes the COSE\_Sign1 response.
    
5.  Verifies the signature using the public key written by the backend.
    

### **Sample Output**

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   Encoded Inputs:    cosePayload: omRuYW1lZUFsaWNlY2RvYmoxOTkwLTAxLTAx    coseProtectedHeader: ogEmBEMxMjM    coseUnprotectedHeader: oA  Sending request to /coseSign...  Received COSE signature from API.  Public key loaded:    x = 58b1a7...    y = 3ec18a...  Verifying signature...  COSE signature is VALID.  Decoded COSE payload:  {    "name": "Alice",    "dob": "1990-01-01"  }   `

**Internal Logic Overview**
---------------------------

### Signing Process (Backend)

*   Input fields are Base64URL-decoded.
    
*   Headers are decoded from CBOR format.
    
*   The system extracts the kid from headers.
    
*   Constructs a Sign1Message object using the COSE-JAVA library.
    
*   Computes a signature using ECDSA over the Sig\_structure.
    
*   Returns the signed COSE\_Sign1 object encoded as Base64URL.
    

### Verification Process (Python)

*   Decodes the COSE\_Sign1 structure.
    
*   Loads public key from the ec\_public\_key.txt file.
    
*   Reconstructs the signature structure using pycose.
    
*   Validates the signature cryptographically.
    
*   Displays the decoded payload for human verification.
    

**Future Enhancements**
-----------------------

*   Add /coseVerify endpoint to allow public verification through the backend.
    
*   Integrate with a secure keystore (e.g., HSM, HashiCorp Vault, AWS KMS).
    
*   Add Swagger UI and OpenAPI specification.
    
*   Containerize the project with Docker for deployment.
    
*   Include full test suite and CI workflow.
    

**Maintainer**
--------------

**Aaditya Verma**

*   GitHub: [aadityaverma2011](https://github.com/aadityaverma2011)
    
*   LinkedIn: [aaditya-verma](https://linkedin.com/in/aaditya-verma-661105266)