## 라이프로그 API 명세서

### 1. 엔드포인트 요약

| 구분             | URL                        | Method | 인증 | 입력값/파라미터 | 성공 응답 (HTTP/Body)      | 설명                         |
|------------------|---------------------------|--------|------|-----------------|----------------------------|------------------------------|
| 전체 조회        | `/api/v2/logs`            | GET    | 필요 | `page`, `size`, `from`, `to` (Query) | 200 / Page    | 페이지네이션, 날짜 범위 조회 |
| 단건 조회        | `/api/v2/logs/{id}`       | GET    | 필요 | 없음            | 200 / ResLifeLogDto         | 특정 라이프로그 조회         |
| 생성             | `/api/v2/logs`            | POST   | 필요 | `title`, `description`, `timestamp` (Body) | 201 / ResLifeLogDto         | 라이프로그 생성             |
| 수정             | `/api/v2/logs/{id}`       | PUT    | 필요 | 최소 1개 필드 (Body) | 200 / ResLifeLogDto         | 라이프로그 수정             |
| 삭제             | `/api/v2/logs/{id}`       | DELETE | 필요 | 없음            | 200 / 없음                  | 라이프로그 삭제             |

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
          "id": 1,
          "title": "제목",
          "description": "설명",
          "timestamp": "2024-06-07T15:30:45.123"
        }
      ],
      "totalPages": 1,
      "totalElements": 1,
      "size": 10,
      "number": 0
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

#### 2.4 수정

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

#### 2.5 삭제

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

---

> 표와 예시, 주요 제약조건을 통해 라이프로그 API를 한눈에 이해할 수 있도록 정리하였습니다.