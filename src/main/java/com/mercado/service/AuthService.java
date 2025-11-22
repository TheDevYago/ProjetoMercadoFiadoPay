package com.mercado.service;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mercado.client.FiadoPayClient;
import com.mercado.dto.MerchantCredentialsDTO;
import com.mercado.dto.MerchantDTO;

public class AuthService {
    private String currentToken;
    private MerchantCredentialsDTO credentials; // Guarda as chaves da loja
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final FiadoPayClient client = new FiadoPayClient();

    public AuthService() {
        System.out.println("[AUTH] Iniciando autenticação real...");
        initializeMerchant();
    }

    private void initializeMerchant() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String name = "Aluno POOA " + uniqueId;
        
        MerchantDTO newMerchant = new MerchantDTO(name, "http://localhost:8080/callback");
        
        // 1. Cadastra e recebe as chaves (ClientId / ClientSecret)
        this.credentials = client.registerMerchant(newMerchant);
        
        if (this.credentials != null) {
            System.out.println("[AUTH] Loja cadastrada! ClientID: " + this.credentials.clientId());
            refreshToken();
        } else {
            System.err.println("[AUTH FATAL] Falha no cadastro.");
        }
    }

    public void startTokenRefresh() {
        scheduler.scheduleAtFixedRate(this::refreshToken, 4, 4, TimeUnit.MINUTES);
    }

    private void refreshToken() {
        if (this.credentials == null) return;

        System.out.println("[JOB] Renovando token...");
        // 2. Usa as chaves para pedir o token
        String newToken = client.fetchToken(this.credentials.clientId(), this.credentials.clientSecret());
        
        if (newToken != null) {
            this.currentToken = newToken;
            System.out.println("[AUTH] Token atualizado!");
        }
    }

    public String getToken() {
        return currentToken;
    }

    public void stop() {
        scheduler.shutdown();
    }
}