# 일기 API 테스트 가이드

## 인증 필요
모든 일기 API는 JWT 토큰이 필요합니다. 요청 시 `Authorization: Bearer {토큰}` 헤더를 포함해야 합니다.

## 1. 일기 생성

**POST /api/v1/diaries**

```bash
curl -X POST http://localhost:8080/api/v1/diaries \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "title": "오늘의 일기",
    "content": "오늘은 정말 좋은 하루였다. 아침에 일찍 일어나서 운동을 하고, 친구들과 맛있는 점심을 먹었다. 저녁에는 영화를 보며 여유로운 시간을 보냈다.",
    "date": "2024-01-15"
  }'
```

**응답 예시:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "title": "오늘의 일기",
  "content": "오늘은 정말 좋은 하루였다. 아침에 일찍 일어나서 운동을 하고, 친구들과 맛있는 점심을 먹었다. 저녁에는 영화를 보며 여유로운 시간을 보냈다.",
  "date": "2024-01-15",
  "createdAt": "2024-01-15T20:30:00",
  "updatedAt": "2024-01-15T20:30:00"
}
```

**주의사항:**
- 같은 날짜에 이미 일기가 있으면 409 Conflict 에러 발생
- 제목: 1-100자, 내용: 1-5000자 제한

## 2. 모든 일기 조회 (페이지네이션)

**GET /api/v1/diaries**

```bash
curl -X GET "http://localhost:8080/api/v1/diaries?page=0&size=10&sort=date,desc" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

**응답 예시:**
```json
{
  "content": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "title": "오늘의 일기",
      "content": "오늘은 정말 좋은 하루였다...",
      "date": "2024-01-15",
      "createdAt": "2024-01-15T20:30:00",
      "updatedAt": "2024-01-15T20:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 25,
  "totalPages": 3
}
```

## 3. 특정 일기 조회

**GET /api/v1/diaries/{diaryId}**

```bash
curl -X GET http://localhost:8080/api/v1/diaries/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## 4. 날짜별 일기 조회

**GET /api/v1/diaries/date/{date}**

```bash
curl -X GET http://localhost:8080/api/v1/diaries/date/2024-01-15 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

**응답:**
- 해당 날짜의 일기가 있으면 일기 정보 반환
- 없으면 404 Not Found

## 5. 기간별 일기 조회 (페이지네이션)

**POST /api/v1/diaries/range**

```bash
curl -X POST http://localhost:8080/api/v1/diaries/range \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "startDate": "2024-01-01",
    "endDate": "2024-01-31"
  }'
```

**쿼리 파라미터:**
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 10)
- `sort`: 정렬 기준 (기본값: date,desc)

## 6. 일기 검색

**GET /api/v1/diaries/search**

```bash
curl -X GET "http://localhost:8080/api/v1/diaries/search?keyword=운동&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

**검색 범위:**
- 일기 제목에서 검색
- 일기 내용에서 검색
- 대소문자 구분 없음

## 7. 일기 수정

**PUT /api/v1/diaries/{diaryId}**

```bash
curl -X PUT http://localhost:8080/api/v1/diaries/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "title": "수정된 일기 제목",
    "content": "수정된 일기 내용입니다.",
    "date": "2024-01-16"
  }'
```

**수정 가능한 필드:**
- `title`: 새로운 제목 (선택적)
- `content`: 새로운 내용 (선택적)
- `date`: 새로운 날짜 (선택적)

**주의사항:**
- 날짜 변경 시 해당 날짜에 다른 일기가 있으면 409 Conflict 에러
- 모든 필드는 선택적이며, 제공된 필드만 업데이트됨

## 8. 일기 삭제

**DELETE /api/v1/diaries/{diaryId}**

```bash
curl -X DELETE http://localhost:8080/api/v1/diaries/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

**응답:** `204 No Content`

## 에러 응답

- **400 Bad Request**: 잘못된 요청 데이터
  ```json
  {
    "message": "일기 제목은 1자 이상 100자 이하로 입력하세요."
  }
  ```

- **401 Unauthorized**: 인증 실패
  ```json
  {
    "message": "인증이 필요합니다."
  }
  ```

- **403 Forbidden**: 권한 부족 (다른 사용자의 일기 접근)
  ```json
  {
    "message": "다른 사용자의 일기에 접근할 수 없습니다."
  }
  ```

- **404 Not Found**: 일기를 찾을 수 없음
  ```json
  {
    "message": "일기를 찾을 수 없습니다."
  }
  ```

- **409 Conflict**: 날짜 중복
  ```json
  {
    "message": "해당 날짜에 이미 일기가 존재합니다. 기존 일기를 수정하거나 다른 날짜를 선택해주세요."
  }
  ```

## 주요 특징

### 📅 날짜 기반 관리
- 하루에 하나의 일기만 작성 가능
- 날짜별 빠른 조회 지원
- 기간별 조회로 특정 기간의 일기 모아보기

### 🔍 강력한 검색
- 제목과 내용에서 동시 검색
- 대소문자 구분 없는 검색
- 페이지네이션으로 대량 검색 결과 처리

### 🔐 보안
- 사용자별 데이터 격리
- 본인의 일기만 조회/수정/삭제 가능
- JWT 기반 인증

### ⚡ 성능
- 페이지네이션으로 효율적인 데이터 로딩
- 인덱스 최적화된 쿼리
- 날짜 기반 빠른 검색

## 사용 시나리오

### 1. 일기 작성 워크플로우
```bash
# 1. 오늘 일기가 있는지 확인
curl -X GET http://localhost:8080/api/v1/diaries/date/2024-01-15 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"

# 2. 없으면 새 일기 작성
curl -X POST http://localhost:8080/api/v1/diaries \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{"title": "오늘의 일기", "content": "...", "date": "2024-01-15"}'
```

### 2. 일기 검색 및 조회
```bash
# 1. 키워드로 일기 검색
curl -X GET "http://localhost:8080/api/v1/diaries/search?keyword=여행" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"

# 2. 특정 기간의 일기 조회
curl -X POST http://localhost:8080/api/v1/diaries/range \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{"startDate": "2024-01-01", "endDate": "2024-01-31"}'
```

### 3. 일기 수정
```bash
# 1. 기존 일기 조회
curl -X GET http://localhost:8080/api/v1/diaries/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"

# 2. 내용만 수정
curl -X PUT http://localhost:8080/api/v1/diaries/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{"content": "수정된 내용"}'
``` 