package com.example.keymanager.service;

import com.example.keymanager.dto.CoseSignInnerRequest;
import com.example.keymanager.dto.CoseSignResponse;
import com.upokecenter.cbor.CBORObject;
import COSE.*;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;

@Service
public class CoseSignService {

    private static final HashMap<String, PrivateKey> keyStore = new HashMap<>();

    static {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
            kpg.initialize(256);
            KeyPair kp = kpg.generateKeyPair();
            keyStore.put("123", kp.getPrivate());

            // Write public key to file
            ECPublicKey pub = (ECPublicKey) kp.getPublic();
            byte[] x = pub.getW().getAffineX().toByteArray();
            byte[] y = pub.getW().getAffineY().toByteArray();

            String xHex = bytesToHex(x);
            String yHex = bytesToHex(y);

            String content = "x=" + xHex + "\ny=" + yHex;
            java.nio.file.Files.writeString(java.nio.file.Path.of("ec_public_key.txt"), content);

            System.out.println("✅ Public key written to ec_public_key.txt");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize mock key store", e);
        }
    }

    public CoseSignResponse.ResponseBody sign(CoseSignInnerRequest req) throws Exception {
        // Decode Base64URL inputs
        byte[] payloadBytes = base64UrlDecode(req.getCosePayload());
        byte[] protectedHeaderBytes = base64UrlDecode(req.getCoseProtectedHeader());
        byte[] unprotectedHeaderBytes = base64UrlDecode(req.getCoseUnprotectedHeader());

        // Parse headers using CBOR
        CBORObject protectedHeader = CBORObject.DecodeFromBytes(protectedHeaderBytes);
        CBORObject unprotectedHeader = CBORObject.DecodeFromBytes(unprotectedHeaderBytes);

        System.out.println("🔍 Decoded Protected Header: " + protectedHeader);
        System.out.println("🔍 Decoded Unprotected Header: " + unprotectedHeader);

        // Extract 'kid'
        String kid = null;
        CBORObject kidKey = CBORObject.FromObject(4);  // CBOR map key for 'kid'

        if (protectedHeader.ContainsKey(kidKey)) {
            kid = new String(protectedHeader.get(kidKey).GetByteString(), StandardCharsets.UTF_8);
            System.out.println("🔑 Found kid in protected header: " + kid);
        } else if (unprotectedHeader.ContainsKey(kidKey)) {
            kid = new String(unprotectedHeader.get(kidKey).GetByteString(), StandardCharsets.UTF_8);
            System.out.println("🔑 Found kid in unprotected header: " + kid);
        }

        if (kid == null || !keyStore.containsKey(kid)) {
            throw new IllegalArgumentException("Unknown or missing 'kid'");
        }

        // Create COSE_Sign1Message
        Sign1Message msg = new Sign1Message();
        msg.addAttribute(HeaderKeys.Algorithm, protectedHeader.get(CBORObject.FromObject(1)), Attribute.PROTECTED);
        msg.addAttribute(HeaderKeys.KID, protectedHeader.get(CBORObject.FromObject(4)), Attribute.PROTECTED);
        msg.SetContent(payloadBytes);

        PrivateKey signingKey = keyStore.get(kid);
        msg.sign(new OneKey(null, signingKey));

        byte[] signedCose = msg.EncodeToBytes();
        String base64Cose = base64UrlEncode(signedCose);

        CoseSignResponse.ResponseBody body = new CoseSignResponse.ResponseBody();
        body.setCoseSignedData(base64Cose);
        body.setTimestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        return body;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private byte[] base64UrlDecode(String input) {
        return Base64.getUrlDecoder().decode(input);
    }

    private String base64UrlEncode(byte[] input) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input);
    }
}
