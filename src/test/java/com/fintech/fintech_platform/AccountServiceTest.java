package com.fintech.fintech_platform;

import com.fintech.fintech_platform.domain.account.Account;
import com.fintech.fintech_platform.domain.account.AccountRepository;
import com.fintech.fintech_platform.domain.account.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void transferConcurrencyTest() throws InterruptedException {

        // given
        String fromAccountNumber = "909e887b-aef"; // kim 계좌번호
        String toAccountNumber = "9a434b10-d79";   // park 계좌번호
        Long amount = 1000L;
        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when - 10개 스레드가 동시에 이체 요청
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    accountService.transfer(fromAccountNumber, toAccountNumber, amount);
                } catch (Exception e) {
                    System.out.println("에러 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드 완료 대기
        latch.await();

        // then - DB에서 잔액 확인
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber).get();
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber).get();

        System.out.println("kim 잔액: " + fromAccount.getBalance());
        System.out.println("park 잔액: " + toAccount.getBalance());

        // 총 잔액이 변하지 않아야 함
        assertThat(fromAccount.getBalance() + toAccount.getBalance())
                .isEqualTo(4_999_995_155_000L); // kim + park 초기 잔액 합계
    }
}