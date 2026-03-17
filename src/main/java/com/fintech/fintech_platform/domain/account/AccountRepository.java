package com.fintech.fintech_platform.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // username으로 계좌 조회
    Optional<Account> findByUsername(String username);

    // 계좌번호로 계좌 조회
    Optional<Account> findByAccountNumber(String accountNumber);
}