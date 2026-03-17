package com.fintech.fintech_platform.domain.account.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
// 계좌이체 요청
public class TransferRequest {
    private String fromAccountNumber;       //보내는 계좌번호
    private String toAccountNumber;         //받는 계좌번호
    private Long amount;                  //이체 금액
}