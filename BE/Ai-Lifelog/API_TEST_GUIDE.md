# JWT 인증 API 테스트 가이드

## 1. 회원가입 (POST /api/v1/auth/signup)

```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass123",
    "nickname": "테스트유저"
  }'
```

**응답 예시:**
```json
{
  "username": "testuser",
  "nickname": "테스트유저"
}
```

## 2. 로그인 (POST /api/v1/auth/login)

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass123"
  }'
```

**응답 예시:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImF1dGgiOiJST0xFX1VTRVIiLCJpYXQiOjE2..."
}
```

## 3. 인증이 필요한 엔드포인트 테스트 (GET /api/v1/auth/test)

```bash
# 토큰 없이 요청 (실패)
curl -X GET http://localhost:8080/api/v1/auth/test

# 토큰과 함께 요청 (성공)
curl -X GET http://localhost:8080/api/v1/auth/test \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## 4. 다이어리 조회 (인증 불필요)

```bash
curl -X GET http://localhost:8080/api/v1/diary/
```

## JWT 토큰 사용법

1. 로그인 API를 통해 JWT 토큰을 받습니다.
2. 인증이 필요한 API 호출 시 `Authorization` 헤더에 `Bearer {토큰}` 형식으로 포함합니다.
3. 토큰은 1시간(3600초) 후 만료됩니다.

## 에러 응답

- **400 Bad Request**: 잘못된 요청 데이터
- **401 Unauthorized**: 인증 실패 (토큰 없음, 만료, 잘못된 토큰)
- **403 Forbidden**: 권한 부족
- **500 Internal Server Error**: 서버 오류

## 보안 설정

- CSRF 비활성화 (JWT 사용으로 인해)
- 세션 사용 안함 (STATELESS)
- CORS 설정 활성화
- BCrypt 패스워드 암호화 