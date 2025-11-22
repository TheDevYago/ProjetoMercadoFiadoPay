package com.mercado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentDTO(
    @JsonProperty("method") String method,       
    @JsonProperty("currency") String currency,         
    @JsonProperty("amount") double amount,            
    @JsonProperty("installments") int installments,    
    @JsonProperty("metadataOrderId") String orderId
) {}
