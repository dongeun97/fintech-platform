package com.fintech.fintech_platform.domain.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public void createAccount(String username, Long balance) {

        // 계좌번호 자동 생성
        String accountNumber = UUID.randomUUID().toString().substring(0, 12);

        // 계좌 객체 생성
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setUsername(username);
        account.setBalance(balance);

        // DB 저장
        accountRepository.save(account);
    }

    @Transactional
    public String getBalance(String username) {

        // username으로 계좌 조회
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("계좌가 존재하지 않습니다."));

        // 잔액 반환
        return "잔액: " + account.getBalance() + "원";
    }


    @Transactional
    public String withdraw(String username, Long amount) {

        // 계좌 조회
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("계좌가 존재하지 않습니다."));

        // 잔액 부족 체크
        if (account.getBalance() == 0) {
            throw new IllegalArgumentException("계좌에 잔액이 없습니다.");
        } else if (account.getBalance() < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다. 현재 잔액: " + account.getBalance() + "원");
        }

        // 잔액 감소
        account.setBalance(account.getBalance() - amount);

        return "출금 완료! 잔액: " + account.getBalance() + "원";
    }



}