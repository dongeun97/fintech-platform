package com.fintech.fintech_platform.domain.account;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final RedisTemplate<String, String> redisTemplate;

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

        // 1일 출금 한도 체크(500만원)
        String today = LocalDate.now().toString();
        String redisKey = "withdraw:daily:" + username + ":" + today;

        String dailyAmountStr = redisTemplate.opsForValue().get(redisKey);
        Long dailyAmount = dailyAmountStr == null ? 0L : Long.parseLong(dailyAmountStr);

        if (dailyAmount + amount > 5_000_000L) {
            throw new IllegalArgumentException("1일 출금 한도 500만원을 초과했습니다. 오늘 출금 금액: " + dailyAmount + "원");
        }

        // 잔액 부족 체크
        if (account.getBalance() == 0) {
            throw new IllegalArgumentException("계좌에 잔액이 없습니다.");
        } else if (account.getBalance() < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다. 현재 잔액: " + account.getBalance() + "원");
        }

        // 잔액 감소
        account.setBalance(account.getBalance() - amount);

        // Redis에 오늘 출금 금액 누적
        redisTemplate.opsForValue().set(redisKey, String.valueOf(dailyAmount + amount));

        // 자정까지 TTL 설정
        LocalDateTime midnight = LocalDate.now().plusDays(1).atStartOfDay();
        long secondsUntilMidnight = LocalDateTime.now().until(midnight, ChronoUnit.SECONDS);
        redisTemplate.expire(redisKey, secondsUntilMidnight, TimeUnit.SECONDS);


        return "출금 완료! 잔액: " + account.getBalance() + "원";
    }

    @Transactional
    public String deposit(String username, Long amount) {

        // 계좌 조회
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("계좌가 존재하지 않습니다."));

        // 잔액 증가
        account.setBalance(account.getBalance() + amount);

        return "입금 완료! 잔액: " + account.getBalance() + "원";
    }

    @Transactional
    public String transfer(String fromAccountNumber, String toAccountNumber, Long amount) {

        // Redis 분산락 키
        String lockKey = "lock:account:" + fromAccountNumber;

        // 분산락 획득 시도 (5초 후 자동 해제)
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "lock", 5, TimeUnit.SECONDS);

        if (!Boolean.TRUE.equals(locked)) {
            throw new IllegalStateException("현재 이체가 진행 중입니다. 잠시 후 다시 시도해주세요.");
        }

        try {
            // 보내는 계좌 조회
            Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                    .orElseThrow(() -> new IllegalArgumentException("출금 계좌가 존재하지 않습니다."));

            // 받는 계좌 조회
            Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                    .orElseThrow(() -> new IllegalArgumentException("입금 계좌가 존재하지 않습니다."));

            // 잔액 부족 체크
            if (fromAccount.getBalance() == 0) {
                throw new IllegalArgumentException("계좌에 잔액이 없습니다.");
            } else if (fromAccount.getBalance() < amount) {
                throw new IllegalArgumentException("잔액이 부족합니다. 현재 잔액: " + fromAccount.getBalance() + "원");
            }

            // 출금 처리
            fromAccount.setBalance(fromAccount.getBalance() - amount);

            // 입금 처리
            toAccount.setBalance(toAccount.getBalance() + amount);

            return "이체 완료! 잔액: " + fromAccount.getBalance() + "원";

        } finally {
            // 분산락 해제 (무조건 해제!)
            redisTemplate.delete(lockKey);
        }
    }


}