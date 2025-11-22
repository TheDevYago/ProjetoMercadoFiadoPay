package com.mercado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenRequestDTO(@JsonProperty("client_id") String client_id, @JsonProperty("client_secret") String clientSecret){

}
