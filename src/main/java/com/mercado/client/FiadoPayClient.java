package com.mercado.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercado.dto.MerchantCredentialsDTO;
import com.mercado.dto.MerchantDTO;
import com.mercado.dto.PaymentDTO;
import com.mercado.dto.TokenRequestDTO;
import com.mercado.dto.TokenResponseDTO;

public class FiadoPayClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String BASE_URL = "http://localhost:8080";

    // 1. Cadastra e retorna as CREDENCIAIS (ClientId e ClientSecret)
    public MerchantCredentialsDTO registerMerchant(MerchantDTO merchant) {
        try {
            String json = mapper.writeValueAsString(merchant);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/fiadopay/admin/merchants"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                // Converte o JSON de resposta nas credenciais
                return mapper.readValue(response.body(), MerchantCredentialsDTO.class);
            } else {
                System.err.println("[ERRO] Falha ao cadastrar loja: " + response.body());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 2. Pede o token usando as credenciais certas
    public String fetchToken(String clientId, String clientSecret) {
        try {
            // CORREÇÃO AQUI: Passando as variáveis para o construtor novo
            // O primeiro argumento vai para 'client_id', o segundo para 'client_secret'
            TokenRequestDTO dto = new TokenRequestDTO(clientId, clientSecret);
            
            String json = mapper.writeValueAsString(dto);
            
            System.out.println("[DEBUG] JSON enviado para /auth/token: " + json);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/fiadopay/auth/token"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                TokenResponseDTO tokenResponse = mapper.readValue(response.body(), TokenResponseDTO.class);
                System.out.println("[API] Token obtido com sucesso!");
                return tokenResponse.accessToken();
            } else {
                System.err.println("[ERRO] Falha ao pegar token. Status: " + response.statusCode());
                System.err.println("[ERRO] Corpo da resposta: " + response.body());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendPayment(PaymentDTO payment, String token) {
        try {
            String jsonBody = mapper.writeValueAsString(payment);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/fiadopay/gateway/payments"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

            System.out.println("[API] Enviando pagamento de R$" + payment.amount() + " (" + payment.method() + ")...");
            
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("[API] Resposta do Servidor: " + response.statusCode());
                if(response.statusCode() >= 400) System.out.println(" -> Detalhe: " + response.body());
            } catch (Exception e) {
                System.out.println("[API] Erro de conexão.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPaymentStatus(String paymentId, String token) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/fiadopay/gateway/payments/" + paymentId))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("[API QUERY] Status do pagamento " + paymentId + ": " + response.statusCode());
            System.out.println(" -> Corpo: " + response.body());
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void refundPayment(String paymentId, String token) {
        try {
            String jsonBody = "{\"paymentId\": \"" + paymentId + "\"}";
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/fiadopay/gateway/refunds")) 
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("[API REFUND] Tentativa de devolução: " + response.statusCode());
        } catch (Exception e) { e.printStackTrace(); }
    }
}