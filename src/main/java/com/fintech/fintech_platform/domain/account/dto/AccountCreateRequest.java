package com.fintech.fintech_platform.domain.account.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
//계좌 생성 요청
public class AccountCreateRequest {
    private String username;            //계좌 소유자
    private Long balance;             //초기 잔액
}