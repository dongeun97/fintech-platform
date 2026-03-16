# 💰 Fintech Platform

> 계좌이체 동시성 제어 및 일별 정산 배치 금융 서비스

---

## 📌 프로젝트 개요

금융권 실무 환경을 고려한 백엔드 서비스입니다.
계좌이체 시 발생할 수 있는 동시성 문제를 Redis 분산락으로 해결하고,
일별 거래 정산을 Spring Batch로 처리합니다.

---

## 🛠 기술 스택

| 분류 | 기술 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.11 |
| ORM | Spring Data JPA |
| Security | Spring Security + JWT |
| Batch | Spring Batch |
| Database | MySQL 8.0 |
| Cache/Lock | Redis 7 |
| Build | Gradle |
| Infra | Docker, Docker Compose |

---

## 🏗 기술 선택 이유

### Java 17
- LTS 버전으로 금융권 실무에서 가장 많이 사용
- Record, Sealed Class 등 최신 문법 활용 가능

### Spring Boot 3.5.11
- 안정화된 LTS 버전
- Spring Security 6.x 기반의 최신 보안 설정 적용

### Redis 분산락
- 계좌이체 동시 요청 시 데이터 정합성 보장
- DB Lock 대비 성능 우수
- 분산 환경에서도 동작 가능

### Spring Batch
- 대용량 일별 정산 처리에 최적화
- Chunk 기반 처리로 메모리 효율적 관리
- 재시작, 재처리 등 배치 관리 기능 내장

### MySQL 8.0
- 금융권 표준 RDBMS
- 트랜잭션 ACID 보장

### Docker Compose
- 개발 환경 표준화
- MySQL, Redis를 별도 설치 없이 실행 가능

---

## 🚀 실행 방법

### 사전 요구사항
- Java 17
- Docker Desktop

### 1. 저장소 클론
```bash
git clone https://github.com/dongeun97/fintech-platform.git
cd fintech-platform
```

### 2. 인프라 실행 (MySQL + Redis)
```bash
docker-compose up -d
```

### 3. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 4. API 확인
```
http://localhost:8080
```

---

## 📂 프로젝트 구조
```
src/main/java/com/fintech/
├── domain/
│   ├── account/       # 계좌이체 API (동시성 제어)
│   ├── batch/         # 일별 정산 Spring Batch
│   └── auth/          # JWT 인증
├── global/
│   ├── lock/          # Redis 분산락
│   └── exception/     # 공통 예외 처리
```

---

## 📋 주요 기능

### 1. 계좌이체 API
- Redis 분산락을 활용한 동시성 제어
- 트랜잭션 처리로 데이터 정합성 보장
- 잔액 부족, 계좌 없음 등 예외 처리

### 2. 일별 정산 배치
- Spring Batch Chunk 방식으로 대용량 처리
- 매일 자정 스케줄링 실행
- 거래 내역 집계 및 정산 결과 저장

### 3. JWT 인증
- Access Token / Refresh Token 구조
- Spring Security 필터 체인 적용