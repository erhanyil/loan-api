package com.example.loanapi.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponseDTO {
    private final String token;

}