package com.finalshield.Security;

import io.jsonwebtoken.security.Keys;

import java.util.Base64;

public class GenerarSecreto {
    public static void main(String[] args) {
        byte[] secret = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512).getEncoded();
        String base64Secret = Base64.getEncoder().encodeToString(secret);
        System.out.println(base64Secret);
    }
}
