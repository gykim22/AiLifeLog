## 사용자 정보 API 명세서

### 1. 엔드포인트 요약

| 구분         | 내 정보 조회                          | 내 계정 삭제                         |
|--------------|--------------------------------------|--------------------------------------|
| **URL**      | `/api/v2/users/self`                 | `/api/v2/users/self`                 |
| **Method**   | `GET`                                | `DELETE`                             |
| **인증**     | JWT 토큰 필요`Authorization: Bearer {token}` | JWT 토큰 필요`Authorization: Bearer {token}` |
| **입력값**   | 없음                                 | `password` (필수, 3~100자)           |
| **성공 응답**| `200 OK``{ "id": Long, "username": "string" }` | `200 OK`Body 없음                |
| **실패 응답**| 401 Unauthorized (인증 실패)         | 401 Unauthorized (인증/비밀번호 오류)|
| **제약조건** | 인증된 사용자만 가능                 | 인증된 사용자만 가능비밀번호 불일치 시 401 |

---

### 2. 입력값 및 제약조건

| 필드명   | 타입   | 길이      | 필수 | 비고                  |
|----------|--------|-----------|------|-----------------------|
| password | string | 3~100자   | 예   | 내 계정 삭제 시 필요  |

- **내 정보 조회**는 입력값이 없습니다.
- **내 계정 삭제**는 `password`가 필수이며, 3~100자 이내여야 합니다.

---

### 3. 요청/응답 예시

#### 내 정보 조회

**요청**

```
GET /api/v2/users/self
Authorization: Bearer {token}
```

**응답**

```json
{
  "id": 1,
  "username": "testuser"
}
```

---

#### 내 계정 삭제

**요청**

```
DELETE /api/v2/users/self?password=testpass
Authorization: Bearer {token}
```

**응답**

```
200 OK
```

---

### 4. 반환값

| 상황           | HTTP 상태    | Body 예시                                   |
|----------------|-------------|---------------------------------------------|
| 내 정보 조회 성공 | 200 OK      | `{ "id": 1, "username": "testuser" }`      |
| 내 계정 삭제 성공 | 200 OK      | (Body 없음)                                |
| 인증 실패/비밀번호 오류 | 401 Unauthorized | `{ "error": "Unauthorized" }` 등           |

---

### 5. 요약

- 모든 요청은 **JWT 인증**이 필요합니다.
- 내 정보 조회: 사용자 정보(id, username) 반환
- 내 계정 삭제: 비밀번호 일치 시 계정 삭제, Body 없음
- 인증 실패 또는 비밀번호 불일치 시 401 Unauthorized 반환

---

> 표와 예시를 통해 각 엔드포인트의 타입, 입력, 반환, 제약조건을 한눈에 확인할 수 있습니다.