## 인증 API 명세서

### 1. 엔드포인트 요약

| 구분      | 로그인 API                                           | 회원가입 API                                          |
|-----------|---------------------------------------------------|---------------------------------------------------|
| **URL**   | `/api/v2/auth/login`                              | `/api/v2/auth/signup`                             |
| **Method**| `POST`                                            | `POST`                                            |
| **Request Content-Type** | `application/json`                                | `application/json`                                |
| **입력값**| `username` (3 ~ 50자, 필수)`password` (3 ~ 100자, 필수) | `username` (3 ~ 50자, 필수)`password` (3 ~ 100자, 필수) |
| **성공 응답** | `200 OK``{ "token": "string" }`                   | `201 Created``{ "token": "string" }`              |
| **실패 응답** | 400 Bad Request (입력값 오류)401 Unauthorized (인증 실패)  | 400 Bad Request (입력값 오류)                          |
| **제약조건** | 모든 입력값 필수길이 미달/초과 시 400                           | 모든 입력값 필수길이 미달/초과 시 400                           |

---

### 2. 입력값 및 제약조건

| 필드명      | 타입    | 길이       | 필수 | 비고                  |
|-------------|---------|----------|------|-----------------------|
| username    | string  | 3  ~ 50자 | 예   | 영문/숫자 조합 가능   |
| password    | string  | 3 ~ 100자 | 예   | 영문/숫자/특수문자 가능 |

- 모든 필드는 필수입니다.
- 길이 미달 또는 초과 시 400 Bad Request 반환

---

### 3. 요청/응답 예시

**로그인/회원가입 요청**

#### 헤더
POST /api/v2/auth/login
Content-Type: application/json

#### body
```json
{
  "username": "testuser",
  "password": "testpass"
}
```

**성공 응답**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**실패 응답 예시**

- 입력값 오류:  
  `400 Bad Request`
- 인증 실패(로그인):  
  `401 Unauthorized`

---

### 4. 반환값

| 상황       | HTTP 상태 | Body 예시                                  |
|------------|-----------|---------------------------------------------|
| 성공       | 200(로그인)201(회원가입) | `{ "token": "string" }`                |
| 입력 오류  | 400       | `{ "error": "Invalid username or password" }` 등 |
| 인증 실패  | 401       | `{ "error": "Unauthorized" }`               |

---

### 5. 요약

- **로그인**과 **회원가입** 모두 동일한 입력값, 제약조건, 반환값 구조를 가집니다.
- 입력값이 조건을 충족하지 않으면 400, 로그인 실패 시 401 반환
- 성공 시 JWT 토큰 반환

---
