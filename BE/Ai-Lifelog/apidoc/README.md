# AI 라이프로그 API 문서

## 개요
AI 라이프로그 백엔드 시스템의 REST API 문서입니다. 이 시스템은 사용자의 일상 기록(스냅샷)을 관리하고, AI를 통해 라이프로그와 일기 간의 변환 기능을 제공합니다.

## 인증 시스템
모든 API는 JWT(JSON Web Token) 기반 인증을 사용합니다.

## API 문서 목록

### 1. [인증 API](./API_TEST_GUIDE.md)
- 회원가입 및 로그인
- JWT 토큰 발급 및 관리
- 인증 테스트

### 2. [스냅샷 API](./SNAPSHOT_API_GUIDE.md)
- 스냅샷 생성, 조회, 수정, 삭제
- 위치 기반 스냅샷 관리
- 날짜별/기간별 스냅샷 조회
- 위치 정보 관리

### 3. [사용자 API](./USER_API_GUIDE.md)
- 사용자 정보 조회 및 관리
- 프로필 수정
- 관리자 기능

### 4. [일기 API](./DIARY_API_GUIDE.md)
- 일기 생성, 조회, 수정, 삭제
- 날짜별/기간별 일기 조회
- 키워드 검색 기능

## 주요 엔티티

### User (사용자)
- 시스템 사용자 정보
- JWT 인증 및 권한 관리

### Snapshot (스냅샷)
- 개별 생활 기록
- 내용, 시간, 위치 정보 포함

### DailySnapshot (일별 스냅샷)
- 날짜별 스냅샷 컨테이너
- 하루의 여러 스냅샷을 그룹화

### Location (위치)
- 위치 태그 및 GPS 좌표
- 스냅샷과 연결되어 위치 정보 제공

### Diary (일기)
- 전통적인 일기 형태
- AI 변환 시스템의 대상

## 시스템 특징

### 🔐 보안
- JWT 기반 인증
- 사용자별 데이터 격리
- 권한 기반 접근 제어

### 📱 사용자 친화적
- GPS 없이도 태그만으로 위치 생성 가능
- 실시간 기록 및 과거/미래 기록 지원
- 한국어 에러 메시지

### ⚡ 성능
- 페이지네이션으로 대용량 데이터 처리
- 자동 데이터 정리 및 최적화
- 효율적인 쿼리 구조

### 🤖 AI 준비
- Lifelog ↔ Diary 변환을 위한 데이터 구조
- 확장 가능한 아키텍처

## 개발 환경

- **Framework**: Spring Boot 3.x
- **Database**: JPA/Hibernate
- **Security**: Spring Security + JWT
- **Validation**: Jakarta Validation
- **Documentation**: Swagger/OpenAPI 3.0

## 시작하기

1. **인증 토큰 획득**
   ```bash
   # 회원가입
   curl -X POST http://localhost:8080/api/v1/auth/signup \
     -H "Content-Type: application/json" \
     -d '{"username": "testuser", "password": "testpass123", "nickname": "테스트유저"}'
   
   # 로그인
   curl -X POST http://localhost:8080/api/v1/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username": "testuser", "password": "testpass123"}'
   ```

2. **스냅샷 생성**
   ```bash
   curl -X POST http://localhost:8080/api/v1/snapshots \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
     -d '{"content": "첫 번째 스냅샷", "locationTag": "집"}'
   ```

3. **스냅샷 조회**
   ```bash
   curl -X GET http://localhost:8080/api/v1/snapshots \
     -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
   ```

## 에러 코드

| 코드 | 설명 | 해결 방법 |
|------|------|-----------|
| 400 | Bad Request | 요청 데이터 검증 실패 |
| 401 | Unauthorized | JWT 토큰 없음/만료/잘못됨 |
| 403 | Forbidden | 권한 부족 |
| 404 | Not Found | 리소스를 찾을 수 없음 |
| 500 | Internal Server Error | 서버 오류 |

## 연락처

- **개발자**: Swallow Lee
- **프로젝트**: AI 라이프로그 시스템
- **버전**: 1.0 