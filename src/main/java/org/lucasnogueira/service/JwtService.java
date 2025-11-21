package org.lucasnogueira.service;

import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Singleton;

@Singleton
public class JwtService {

    public String generateJwt() {
        return Jwt.issuer("simulador")
                .subject("caixaverso")
                .expiresIn(315360000)
                .sign();
    }
}
