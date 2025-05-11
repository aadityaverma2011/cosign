import base64
import requests
import cbor2
from pycose.messages import Sign1Message
from pycose.keys import EC2Key
from pycose.algorithms import Es256
from pycose.keys.curves import P256
import os


# --- Utility Functions ---
def base64url_encode(b):
    return base64.urlsafe_b64encode(b).rstrip(b"=").decode()

def base64url_decode(s):
    s += '=' * (-len(s) % 4)
    return base64.urlsafe_b64decode(s)

def read_public_key():
    path = r"C:\misc\grind\keymanager\keymanager\ec_public_key.txt"
    if not os.path.exists(path):
        raise FileNotFoundError("❌ ec_public_key.txt not found at expected path!")

    with open(path, "r") as f:
        lines = f.read().splitlines()
        if len(lines) < 2:
            raise ValueError("❌ ec_public_key.txt is malformed")

        x_hex = lines[0].split("=")[1].strip()
        y_hex = lines[1].split("=")[1].strip()

        x = bytes.fromhex(x_hex)
        y = bytes.fromhex(y_hex)

        print("🔑 Public key loaded:")
        print("  x =", x_hex)
        print("  y =", y_hex)

        return EC2Key(crv=P256, x=x, y=y, optional_params={"ALG": Es256})


# --- Step 1: Generate CBOR Encoded Inputs ---
payload_obj = {"name": "Alice", "dob": "1990-01-01"}
protected_header_obj = {1: -7, 4: b"123"}  # alg: ES256, kid: "123"
unprotected_header_obj = {}

cose_payload = base64url_encode(cbor2.dumps(payload_obj))
cose_protected_header = base64url_encode(cbor2.dumps(protected_header_obj))
cose_unprotected_header = base64url_encode(cbor2.dumps(unprotected_header_obj))

print("📦 Encoded Inputs:")
print("  cosePayload:", cose_payload)
print("  coseProtectedHeader:", cose_protected_header)
print("  coseUnprotectedHeader:", cose_unprotected_header)

# --- Step 2: Prepare Request ---
data = {
    "id": "req-1",
    "version": "1.0",
    "requesttime": "2025-05-11T12:00:00Z",
    "metadata": {},
    "request": {
        "cosePayload": cose_payload,
        "applicationId": "test-app",
        "referenceId": "ref-001",
        "coseProtectedHeader": cose_protected_header,
        "coseUnprotectedHeader": cose_unprotected_header
    }
}

# --- Step 3: Call the API ---
print("📡 Sending request to /coseSign...")
res = requests.post("http://localhost:8080/coseSign", json=data)
assert res.status_code == 200, "API call failed!"

response_json = res.json()
if "response" not in response_json or response_json["response"] is None:
    print("❌ Server returned an error:")
    print(response_json)
    exit(1)

cose_b64 = response_json["response"]["coseSignedData"]
print("✅ Received COSE signature from API.")

# --- Step 4: Decode & Verify ---
cose_bytes = base64url_decode(cose_b64)
msg = Sign1Message.decode(cose_bytes)
verifier_key = read_public_key()
msg.key = verifier_key  # ✅ SET key on message object
print("🔐 Verifying signature...")

if msg.verify_signature():
    print("🎉 COSE signature is VALID.")
    # 🔓 Decode CBOR payload
    decoded_payload = cbor2.loads(msg.payload)
    print("📝 Decoded COSE payload:")
    print(decoded_payload)
else:
    print("❌ COSE signature is INVALID.")
