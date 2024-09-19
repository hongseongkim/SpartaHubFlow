# SpartaHubFlow
[내일배움캠프 Spring 심화] Ch.4 팀 프로젝트 물류관리 및 배송 시스템

---

## 팀원 역할 분담

- 전반적인 설계 및 프로젝트 관리는 모든 팀원이 공동으로 작업

### 김홍성
- `eureka-server`, `user`, `gateway`, `slack` App 기능 구현

### 박상훈

- `product`, `order`, `company` App 기능 구현

### 이미연

- `hub`, `delivery` App 기능 구현

---

## 서비스 구성 및 실행 방법

![image](https://github.com/user-attachments/assets/076304fd-a903-428c-a4f3-a20cfee73c57)


프로젝트는 `docker-compose`와 별도의 `.yml` 파일을 이용하여 실행할 수 있습니다. 자세한 실행 방법은 아래의 단계를 따릅니다:

1. 루트 폴더 아래에 .env 파일 제작
2. `docker-compose up -d` 명령어를 사용하여 서비스 실행
3. 서비스가 정상적으로 기동되었는지 확인

---

## 프로젝트 목적/상세

이 프로젝트는 B2B 물류 관리 및 배송 시스템은 MSA 기반 플랫폼으로 개발된 프로젝트입니다. 더 나아가 AI API와 연동하여 기존의 서비스들과 차별화를 두었습니다.


---

## ERD

![image](https://github.com/user-attachments/assets/c8b5e554-0aff-42dd-b80e-d17f8d7db8c3)

---

## 기술 스택

- **백엔드**: Spring Boot 3.3.3
- **데이터베이스:** PostgreSQL, Redis
- **빌드 툴:**  Docker, Gradle
- **버전 관리:** Git을 이용한 버전 관리
- **API 문서**: Swagger

---

## API Docs

- [Notion 링크](https://www.notion.so/teamsparta/2c39338ec4114b2da46db8a172de505d?v=06e3a960012b45b0b5ccbff5aefb41a9)

---

