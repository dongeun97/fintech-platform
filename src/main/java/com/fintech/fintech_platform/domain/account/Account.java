package com.fintech.fintech_platform.domain.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 계좌번호
    @Column(nullable = false, unique = true)
    private String accountNumber;

    // 계좌 소유자
    @Column(nullable = false)
    private String username;

    // 잔액
    @Column(nullable = false)
    private Long balance;
}