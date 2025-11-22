package com.mercado.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MerchantCredentialsDTO(@JsonProperty("clientId") String clientId, @JsonProperty("clientSecret") String clientSecret){

}