package com.fintech.fintech_platform.domain.account;


import com.fintech.fintech_platform.domain.account.dto.AccountCreateRequest;
import com.fintech.fintech_platform.domain.account.dto.WithdrawRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    // 계좌 생성
    @PostMapping("/create")
    public ResponseEntity<String> createAccount(@RequestBody AccountCreateRequest request) {
        accountService.createAccount(request.getUsername(), request.getBalance());
        return ResponseEntity.ok("계좌생성이 완료되었습니다.");
    }

    // 계좌 조회
    @GetMapping("/balance")
    public ResponseEntity<String> getBalance(Principal principal) {
        String balance = accountService.getBalance(principal.getName());
        return ResponseEntity.ok(balance);
    }

    // 출금
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody WithdrawRequest request, Principal principal) {
        String result = accountService.withdraw(principal.getName(), request.getAmount());
        return ResponseEntity.ok(result);
    }


    // 출금 + 이체 + 계좌이체 (redis 분산락)
    /*
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        String result = accountService.transfer(request.getFromAccountNumber(), request.getToAccountNumber(), request.getAmount());
        return ResponseEntity.ok(result);
    }

     */
}