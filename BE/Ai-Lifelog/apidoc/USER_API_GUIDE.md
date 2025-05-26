# 사용자 API 테스트 가이드

## 인증 필요
모든 사용자 API는 JWT 토큰이 필요합니다. 요청 시 `Authorization: Bearer {토큰}` 헤더를 포함해야 합니다.

## 1. 모든 사용자 조회 (관리자 전용)

**GET /api/v1/users**

```bash
curl -X GET "http://localhost:8080/api/v1/users?page=0&size=10" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN_HERE"
```

**권한**: `ROLE_ADMIN` 필요

**응답 예시:**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "loginId": "testuser",
    "name": "테스트유저",
    "roles": ["ROLE_USER"],
    "createdAt": "2024-01-15T10:00:00",
    "updatedAt": "2024-01-15T10:00:00"
  },
  {
    "id": "456e7890-e89b-12d3-a456-426614174001",
    "loginId": "admin",
    "name": "관리자",
    "roles": ["ROLE_ADMIN"],
    "createdAt": "2024-01-14T09:00:00",
    "updatedAt": "2024-01-14T09:00:00"
  }
]
```

## 2. 로그인 ID로 사용자 조회

**GET /api/v1/users/{loginId}**

```bash
curl -X GET http://localhost:8080/api/v1/users/testuser \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

**응답 예시:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "loginId": "testuser",
  "name": "테스트유저",
  "roles": ["ROLE_USER"],
  "createdAt": "2024-01-15T10:00:00",
  "updatedAt": "2024-01-15T10:00:00"
}
```

## 3. 현재 사용자 정보 조회

**GET /api/v1/users/me**

```bash
curl -X GET http://localhost:8080/api/v1/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

**응답 예시:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "loginId": "testuser",
  "name": "테스트유저",
  "roles": ["ROLE_USER"],
  "createdAt": "2024-01-15T10:00:00",
  "updatedAt": "2024-01-15T10:00:00"
}
```

## 4. UUID로 사용자 조회

**GET /api/v1/users/{id}**

```bash
curl -X GET http://localhost:8080/api/v1/users/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

**주의**: 이 엔드포인트는 현재 `/users/{loginId}` 엔드포인트와 경로가 겹치는 문제가 있습니다. 실제 구현에서는 경로를 구분해야 합니다.

## 5. 사용자 정보 수정

**PUT /api/v1/users/{id}**

```bash
curl -X PUT http://localhost:8080/api/v1/users/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "name": "수정된 닉네임",
    "password": "newpassword123"
  }'
```

**요청 본문 (ReqUpdateUserDto):**
```json
{
  "name": "수정된 닉네임",        // 선택적, 3-40자
  "password": "newpassword123"   // 선택적, 3-100자
}
```

**응답 예시:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "loginId": "testuser",
  "name": "수정된 닉네임",
  "roles": ["ROLE_USER"],
  "createdAt": "2024-01-15T10:00:00",
  "updatedAt": "2024-01-15T14:30:00"
}
```

**권한**: 본인 또는 관리자만 수정 가능

## 6. 사용자 삭제

**DELETE /api/v1/users/{id}**

```bash
curl -X DELETE http://localhost:8080/api/v1/users/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

**응답**: `204 No Content`

**권한**: 본인 또는 관리자만 삭제 가능

## 에러 응답

- **400 Bad Request**: 잘못된 요청 데이터
  ```json
  {
    "message": "닉네임은 3자 이상 40자 이하로 입력하세요."
  }
  ```

- **401 Unauthorized**: 인증 실패 (토큰 없음, 만료, 잘못된 토큰)
  ```json
  {
    "message": "인증이 필요합니다."
  }
  ```

- **403 Forbidden**: 권한 부족
  ```json
  {
    "message": "접근 권한이 없습니다."
  }
  ```

- **404 Not Found**: 사용자를 찾을 수 없음
  ```json
  {
    "message": "사용자를 찾을 수 없습니다."
  }
  ```

- **500 Internal Server Error**: 서버 오류

## 권한 체계

### ROLE_USER (일반 사용자)
- 자신의 정보 조회/수정/삭제
- 다른 사용자의 기본 정보 조회 (loginId로)

### ROLE_ADMIN (관리자)
- 모든 사용자 목록 조회
- 모든 사용자 정보 수정/삭제
- 시스템 관리 기능

## 주요 특징

- **보안**: 사용자는 본인의 정보만 수정/삭제 가능
- **관리자 권한**: `@PreAuthorize("hasRole('ROLE_ADMIN')")` 사용
- **Validation**: DTO에 한국어 에러 메시지 포함
- **페이지네이션**: 사용자 목록 조회 시 페이지네이션 지원
- **JWT 인증**: 모든 엔드포인트에서 JWT 토큰 필요

## 사용 시나리오

### 1. 사용자 프로필 수정
```bash
# 1. 현재 사용자 정보 확인
curl -X GET http://localhost:8080/api/v1/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"

# 2. 닉네임만 수정
curl -X PUT http://localhost:8080/api/v1/users/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{"name": "새로운 닉네임"}'
```

### 2. 관리자의 사용자 관리
```bash
# 1. 모든 사용자 목록 조회
curl -X GET "http://localhost:8080/api/v1/users?page=0&size=20" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN_HERE"

# 2. 특정 사용자 삭제
curl -X DELETE http://localhost:8080/api/v1/users/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN_HERE"
``` 