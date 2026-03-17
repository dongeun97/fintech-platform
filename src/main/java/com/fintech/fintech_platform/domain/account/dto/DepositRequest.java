package com.fintech.fintech_platform.domain.account.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DepositRequest {
    private Long amount;
}