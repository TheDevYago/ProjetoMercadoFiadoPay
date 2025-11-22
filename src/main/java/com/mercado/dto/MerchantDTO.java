package com.mercado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MerchantDTO(@JsonProperty("name") String name, @JsonProperty("webhookUrl") String webhookUrl) {

}
