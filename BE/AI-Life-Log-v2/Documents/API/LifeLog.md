## 라이프로그 API 명세서

### 1. 엔드포인트 요약

| 구분             | URL                        | Method | 인증 | 입력값/파라미터 | 성공 응답 (HTTP/Body)      | 설명                         |
|------------------|---------------------------|--------|------|-----------------|----------------------------|------------------------------|
| 전체 조회        | `/api/v2/logs`            | GET    | JWT  | `page`, `size`, `from`, `to` (Query) | 200 / Page    | 페이지네이션, 날짜 범위 조회 |
| 단건 조회        | `/api/v2/logs/{id}`       | GET    | JWT  | 없음            | 200 / ResLifeLogDto         | 특정 라이프로그 조회         |
| 생성             | `/api/v2/logs`            | POST   | JWT  | `title`, `description`, `timestamp` (Body) | 201 / ResLifeLogDto         | 라이프로그 생성             |
| 다건 생성        | `/api/v2/logs/batch`      | POST   | JWT  | LifeLogDto[] (Body) | 201 / ResLifeLogDto[]      | 여러 라이프로그 생성         |
| 수정             | `/api/v2/logs/{id}`       | PUT    | JWT  | 최소 1개 필드 (Body) | 200 / ResLifeLogDto         | 라이프로그 수정             |
| 삭제             | `/api/v2/logs/{id}`       | DELETE | JWT  | 없음            | 200 / 없음                  | 라이프로그 삭제             |

---

### 2. 파라미터 및 요청/응답 예시

#### 2.1 전체 조회

- **Query 파라미터:**
    - `page` (선택, 0부터 시작)
    - `size` (선택)
    - `from`, `to` (선택, `yyyy-MM-dd` 형식)

- **요청 예시:**
    ```
    GET /api/v2/logs?page=0&size=10&from=2024-06-01&to=2024-06-07
    Authorization: Bearer {token}
    ```

- **응답 예시:**

```json
{
  "content": [
    {
      "id": 32,
      "title": "저녁 영화",
      "description": "가족과 영화를 봤다.",
      "timestamp": "2024-06-07T20:00"
    },
    {
      "id": 31,
      "title": "오후 회의",
      "description": "클라이언트 미팅을 했다.",
      "timestamp": "2024-06-07T15:00"
    },
    {
      "id": 30,
      "title": "점심 식사",
      "description": "새로운 식당에서 점심을 먹었다.",
      "timestamp": "2024-06-07T12:30"
    },
    {
      "id": 29,
      "title": "오전 개발",
      "description": "테스트 코드를 작성했다.",
      "timestamp": "2024-06-07T09:00"
    },
    {
      "id": 28,
      "title": "아침 명상",
      "description": "30분 명상을 했다.",
      "timestamp": "2024-06-07T07:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
     },
    "offset": 0,
    "paged": true,
    "unpaged": false
    },
  "last": false,
  "totalPages": 7,
  "totalElements": 32,
  "size": 5,
  "number": 0,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "first": true,
  "numberOfElements": 5,
  "empty": false
  }
```
---

#### 2.2 단건 조회

- **요청 예시:**
    ```
    GET /api/v2/logs/1
    Authorization: Bearer {token}
    ```

- **응답 예시:**
    ```json
    {
      "id": 1,
      "title": "제목",
      "description": "설명",
      "timestamp": "2024-06-07T15:30:45.123"
    }
    ```

---

#### 2.3 생성

- **Body 필수값:**
    - `title` (문자열)
    - `description` (문자열)
    - `timestamp` (문자열, `yyyy-MM-dd'T'HH:mm:ss[.SSS]`)

- **요청 예시:**
    ```json
    {
      "title": "제목",
      "description": "설명",
      "timestamp": "2024-06-07T15:30:45.123"
    }
    ```

- **응답 예시:** (201 Created)
    ```json
    {
      "id": 1,
      "title": "제목",
      "description": "설명",
      "timestamp": "2024-06-07T15:30:45.123"
    }
    ```

---

#### 2.4 다건 생성

- **Body:**
    - LifeLogDto 배열

- **요청 예시:**
    ```json
    [
      {
        "title": "아침 조깅",
        "description": "5km 조깅을 했다.",
        "timestamp": "2024-06-07T07:30:00"
      },
      {
        "title": "점심 식사",
        "description": "친구와 점심을 먹었다.",
        "timestamp": "2024-06-07T12:30:00"
      }
    ]
    ```

- **응답 예시:** (201 Created)
    ```json
    [
      {
        "id": 1,
        "title": "아침 조깅",
        "description": "5km 조깅을 했다.",
        "timestamp": "2024-06-07T07:30:00"
      },
      {
        "id": 2,
        "title": "점심 식사",
        "description": "친구와 점심을 먹었다.",
        "timestamp": "2024-06-07T12:30:00"
      }
    ]
    ```

---

#### 2.5 수정

- **Body:**
    - 최소 1개 필드(`title`, `description`, `timestamp`) 필요

- **요청 예시:**
    ```json
    {
      "description": "수정된 설명"
    }
    ```

- **응답 예시:**
    ```json
    {
      "id": 1,
      "title": "제목",
      "description": "수정된 설명",
      "timestamp": "2024-06-07T15:30:45.123"
    }
    ```

---

#### 2.6 삭제

- **요청 예시:**
    ```
    DELETE /api/v2/logs/1
    Authorization: Bearer {token}
    ```

- **응답:**
    - 200 OK (Body 없음)

---

### 3. 공통 사항 및 제약조건

- **인증:** 모든 API는 JWT 토큰 인증 필요 (`Authorization: Bearer {token}`)
- **날짜/시간 포맷:**
    - 조회 파라미터: `yyyy-MM-dd`
    - 본문 timestamp: `yyyy-MM-dd'T'HH:mm:ss[.SSS]`
- **실패 시:** 적절한 HTTP 상태 코드(예: 400, 401, 404 등)와 메시지 반환
- **Content-Type:** 모든 요청은 `application/json` 형식으로 전송

---

> 표와 예시, 주요 제약조건을 통해 라이프로그 API를 한눈에 이해할 수 있도록 정리하였습니다.