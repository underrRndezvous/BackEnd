package com.underrRndezvous.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class KakaoLoginRequest {
    private String code;
}