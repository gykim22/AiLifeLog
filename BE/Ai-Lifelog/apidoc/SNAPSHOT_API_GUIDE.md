# 스냅샷 API 테스트 가이드

## 인증 필요
모든 스냅샷 API는 JWT 토큰이 필요합니다. 요청 시 `Authorization: Bearer {토큰}` 헤더를 포함해야 합니다.

## 1. 스냅샷 생성 (현재 시간)

**POST /api/v1/snapshots**

```bash
curl -X POST http://localhost:8080/api/v1/snapshots \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "content": "오늘 점심은 김치찌개를 먹었다",
    "locationTag": "회사 구내식당",
    "latitude": 37.5665,
    "longitude": 126.9780
  }'
```

**응답 예시:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "content": "오늘 점심은 김치찌개를 먹었다",
  "timestamp": "2024-01-15T12:30:00",
  "location": {
    "id": 1,
    "tagName": "회사 구내식당",
    "latitude": 37.5665,
    "longitude": 126.9780
  }
}
```

## 2. 스냅샷 생성 (특정 시간)

**POST /api/v1/snapshots/with-time**

```bash
curl -X POST http://localhost:8080/api/v1/snapshots/with-time \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "content": "어제 저녁에 친구와 만났다",
    "timestamp": "2024-01-14T19:00:00",
    "locationTag": "강남역",
    "latitude": 37.4979,
    "longitude": 127.0276
  }'
```

## 3. 모든 스냅샷 조회 (페이지네이션)

**GET /api/v1/snapshots**

```bash
curl -X GET "http://localhost:8080/api/v1/snapshots?page=0&size=10&sort=timestamp,desc" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

**응답 예시:**
```json
{
  "content": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "content": "오늘 점심은 김치찌개를 먹었다",
      "timestamp": "2024-01-15T12:30:00",
      "location": {
        "tagName": "회사 구내식당"
      }
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

## 4. 특정 스냅샷 조회

**GET /api/v1/snapshots/{snapshotId}**

```bash
curl -X GET http://localhost:8080/api/v1/snapshots/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## 5. 날짜별 스냅샷 조회

**GET /api/v1/snapshots/date/{date}**

```bash
curl -X GET http://localhost:8080/api/v1/snapshots/date/2024-01-15 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## 6. 기간별 스냅샷 조회 (페이지네이션)

**POST /api/v1/snapshots/range**

```bash
curl -X POST http://localhost:8080/api/v1/snapshots/range \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "startDate": "2024-01-01",
    "endDate": "2024-01-31"
  }'
```

## 7. 위치별 스냅샷 조회 (페이지네이션)

**GET /api/v1/snapshots/location/{locationTag}**

```bash
curl -X GET "http://localhost:8080/api/v1/snapshots/location/회사 구내식당?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## 8. 스냅샷 수정

**PUT /api/v1/snapshots/{snapshotId}**

```bash
curl -X PUT http://localhost:8080/api/v1/snapshots/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "content": "수정된 스냅샷 내용",
    "timestamp": "2024-01-15T13:00:00",
    "locationTag": "새로운 위치",
    "latitude": 37.5665,
    "longitude": 126.9780
  }'
```

## 9. 스냅샷 삭제

**DELETE /api/v1/snapshots/{snapshotId}**

```bash
curl -X DELETE http://localhost:8080/api/v1/snapshots/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## 10. 일별 스냅샷 조회 (DailySnapshot)

**GET /api/v1/snapshots/daily**

```bash
curl -X GET "http://localhost:8080/api/v1/snapshots/daily?page=0&size=10&sort=date,desc" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

**응답 예시:**
```json
{
  "content": [
    {
      "id": 1,
      "date": "2024-01-15",
      "snapshots": [
        {
          "id": "123e4567-e89b-12d3-a456-426614174000",
          "content": "오늘 점심은 김치찌개를 먹었다",
          "timestamp": "2024-01-15T12:30:00"
        }
      ]
    }
  ]
}
```

## 11. 위치 목록 조회

**GET /api/v1/snapshots/locations**

```bash
curl -X GET http://localhost:8080/api/v1/snapshots/locations \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

**응답 예시:**
```json
[
  {
    "id": 1,
    "tagName": "회사 구내식당",
    "latitude": 37.5665,
    "longitude": 126.9780
  },
  {
    "id": 2,
    "tagName": "강남역",
    "latitude": 37.4979,
    "longitude": 127.0276
  }
]
```

## 12. 위치 정보 수정

**PUT /api/v1/snapshots/locations/{locationId}**

```bash
curl -X PUT http://localhost:8080/api/v1/snapshots/locations/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "tagName": "수정된 위치명",
    "latitude": 37.5665,
    "longitude": 126.9780
  }'
```

## 13. 위치 삭제

**DELETE /api/v1/snapshots/locations/{locationId}**

```bash
curl -X DELETE http://localhost:8080/api/v1/snapshots/locations/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## 에러 응답

- **400 Bad Request**: 잘못된 요청 데이터 (validation 실패)
- **401 Unauthorized**: 인증 실패 (토큰 없음, 만료, 잘못된 토큰)
- **403 Forbidden**: 권한 부족 (다른 사용자의 스냅샷 접근)
- **404 Not Found**: 스냅샷 또는 위치를 찾을 수 없음
- **500 Internal Server Error**: 서버 오류

## 주요 특징

- **자동 위치 관리**: 동일한 태그명의 위치가 있으면 재사용, GPS 정보 자동 업데이트
- **DailySnapshot 자동 생성**: 날짜별로 스냅샷을 자동으로 그룹화
- **페이지네이션 지원**: 대용량 데이터 효율적 처리
- **유연한 시간 관리**: 현재 시간 또는 특정 시간으로 스냅샷 생성 가능
- **데이터 무결성**: 스냅샷 삭제 시 빈 DailySnapshot 자동 정리 