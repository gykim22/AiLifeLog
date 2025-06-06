# API 테스트 가이드

## 개요
이 문서는 AI Lifelog 백엔드 시스템의 JUnit 테스트 실행 방법과 구조를 설명합니다.

## 테스트 구조

### 테스트 환경 설정
- **테스트 프로필**: `test`
- **데이터베이스**: H2 인메모리 데이터베이스
- **설정 파일**: `src/test/resources/application-test.yml`

### 테스트 클래스 구조

#### 1. TestConfig
- **위치**: `src/test/java/com/pnu/ailifelog/config/TestConfig.java`
- **역할**: 테스트용 공통 설정 및 유틸리티 메서드 제공
- **주요 기능**:
  - 테스트 사용자 생성 (`createTestUser()`)
  - JWT 토큰 생성 (`generateTestToken()`)
  - ObjectMapper Bean 제공

#### 2. UserControllerTest
- **위치**: `src/test/java/com/pnu/ailifelog/controller/UserControllerTest.java`
- **테스트 대상**: 사용자 관리 API
- **테스트 케이스**:
  - 모든 사용자 조회 (관리자 권한)
  - 로그인 ID로 사용자 조회
  - 현재 사용자 정보 조회
  - 사용자 ID로 조회
  - 사용자 정보 수정
  - 사용자 삭제
  - 인증 실패 케이스

#### 3. AuthControllerTest
- **위치**: `src/test/java/com/pnu/ailifelog/controller/AuthControllerTest.java`
- **테스트 대상**: 인증 관련 API
- **테스트 케이스**:
  - 회원가입 성공/실패
  - 로그인 성공/실패
  - 유효성 검증 오류
  - JWT 인증 테스트

#### 4. SnapshotControllerTest
- **위치**: `src/test/java/com/pnu/ailifelog/controller/SnapshotControllerTest.java`
- **테스트 대상**: 스냅샷 관리 API
- **테스트 케이스**:
  - 스냅샷 생성 (현재 시간/특정 시간)
  - 스냅샷 조회 (전체/날짜별/위치별/기간별)
  - 일별 스냅샷 조회
  - 위치 목록 조회
  - 인증 실패 케이스

#### 5. DiaryControllerTest
- **위치**: `src/test/java/com/pnu/ailifelog/controller/DiaryControllerTest.java`
- **테스트 대상**: 일기 관리 API
- **테스트 케이스**:
  - 일기 생성/수정/삭제
  - 일기 조회 (전체/날짜별/검색)
  - 중복 날짜 방지 테스트
  - 인증 실패 케이스

## 테스트 실행 방법

### 1. 전체 테스트 실행
```bash
./gradlew test
```

### 2. 특정 테스트 클래스 실행
```bash
# User API 테스트
./gradlew test --tests UserControllerTest

# Auth API 테스트
./gradlew test --tests AuthControllerTest

# Snapshot API 테스트
./gradlew test --tests SnapshotControllerTest

# Diary API 테스트
./gradlew test --tests DiaryControllerTest
```

### 3. 특정 테스트 메서드 실행
```bash
./gradlew test --tests UserControllerTest.getAllUsers_Success
```

### 4. IDE에서 실행
- IntelliJ IDEA: 테스트 클래스 우클릭 → "Run Tests"
- Eclipse: 테스트 클래스 우클릭 → "Run As" → "JUnit Test"

## 테스트 데이터

### 기본 테스트 사용자
- **로그인 ID**: `testuser`
- **이름**: `테스트 사용자`
- **비밀번호**: `password123`
- **권한**: `ROLE_USER`

### JWT 토큰
- 각 테스트에서 자동으로 생성
- 유효 기간: 2시간
- Bearer 토큰 형식으로 사용

## 테스트 주의사항

### 1. 데이터 격리
- 각 테스트는 `@Transactional`로 격리됨
- 테스트 완료 후 자동 롤백
- H2 인메모리 DB 사용으로 테스트 간 독립성 보장

### 2. 인증 테스트
- 모든 보호된 엔드포인트는 JWT 토큰 필요
- 잘못된 토큰 시 401 Unauthorized 응답
- 토큰 없이 접근 시 401 Unauthorized 응답

### 3. 유효성 검증
- DTO 필드 유효성 검증 테스트 포함
- 빈 값, 길이 제한, 형식 오류 등 테스트
- 400 Bad Request 응답 확인

## 테스트 결과 확인

### 1. 콘솔 출력
```
BUILD SUCCESSFUL in 15s
4 actionable tasks: 4 executed
```

### 2. 테스트 리포트
- **위치**: `build/reports/tests/test/index.html`
- 브라우저에서 열어 상세 결과 확인

### 3. 커버리지 리포트 (선택사항)
```bash
./gradlew jacocoTestReport
```
- **위치**: `build/reports/jacoco/test/html/index.html`

## 문제 해결

### 1. 테스트 실패 시
1. 로그 확인: `build/reports/tests/test/classes/`
2. H2 콘솔 활성화하여 데이터 확인
3. 디버그 모드로 테스트 실행

### 2. 의존성 문제
```bash
./gradlew clean build
```

### 3. 포트 충돌
- 테스트는 랜덤 포트 사용
- 실제 애플리케이션과 독립적으로 실행

## 추가 테스트 작성 가이드

### 1. 새로운 테스트 클래스 생성
```java
@SpringBootTest
@AutoConfigureTestMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class NewControllerTest {
    // 테스트 코드
}
```

### 2. 테스트 메서드 패턴
```java
@Test
@DisplayName("기능 설명")
void methodName_Condition_ExpectedResult() throws Exception {
    // Given
    // 테스트 데이터 준비
    
    // When & Then
    // API 호출 및 검증
}
```

이 가이드를 통해 모든 API의 동작을 체계적으로 테스트할 수 있습니다. 