package com.example.loanapi.dto;

import lombok.Builder;

@Builder
public class BaseResponseDTO {

    private String msg;
    private boolean success;

}
