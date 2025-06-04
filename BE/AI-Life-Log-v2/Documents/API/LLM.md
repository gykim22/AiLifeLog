아래는 당신의 LLMController와 LLMService 구조를 기반으로 작성한 **API 명세서** 예시입니다.  
실제 문서(예: Swagger, Notion, Markdown 등)에 바로 활용할 수 있도록 구성했습니다.

---

# LLM API 명세서

## 1. LLM 질의/요약 API

### 요청

- **URL**: `/api/v2/llms/ask`
- **Method**: `POST`
- **Content-Type**: `application/x-www-form-urlencoded` 또는 `application/json`
- **인증**: 필수 (Spring Security의 `@AuthenticationPrincipal`)

#### 파라미터

| 이름         | 타입   | 위치   | 필수 | 설명            |
|--------------|--------|--------|------|-----------------|
| userInput    | String | Body   | 예   | 사용자 입력 프롬프트(질문/요약 요청 등) |

#### 예시 요청 (form)

```
POST /api/v2/llms/ask
Content-Type: application/x-www-form-urlencoded

userInput=2024년 6월 1일부터 6월 7일까지의 라이프로그를 요약해줘.
```

#### 예시 요청 (json)

```json
{
  "userInput": "2024년 6월 1일부터 6월 7일까지의 라이프로그를 요약해줘."
}
```

### 응답

- **HTTP 코드**: 200 OK
- **Body**: String (LLM이 생성한 요약 또는 답변)

#### 예시 응답

```json
"2024년 6월 1일부터 6월 7일까지 주요 활동은 운동, 독서, 업무였습니다. 감정 변화는 대체로 긍정적이었습니다."
```

---

## 2. LifeLog 구조화 생성 API

### 요청

- **URL**: `/api/v2/llms/generate`
- **Method**: `POST`
- **Content-Type**: `application/x-www-form-urlencoded` 또는 `application/json`
- **인증**: 필수

#### 파라미터

| 이름         | 타입   | 위치   | 필수 | 설명                 |
|--------------|--------|--------|------|----------------------|
| userInput    | String | Body   | 예   | 자유 형식의 일기/텍스트 |

#### 예시 요청 (form)

```
POST /api/v2/llms/generate
Content-Type: application/x-www-form-urlencoded

userInput=2024년 6월 1일 아침에 조깅을 하고, 오후엔 친구와 카페에서 대화를 나눴다. 저녁엔 새로운 소설을 읽었다.
```

#### 예시 요청 (json)

```json
{
  "userInput": "2024년 6월 1일 아침에 조깅을 하고, 오후엔 친구와 카페에서 대화를 나눴다. 저녁엔 새로운 소설을 읽었다."
}
```

### 응답

- **HTTP 코드**: 200 OK
- **Body**: List

#### 예시 응답

```json
[
  {
    "title": "아침 조깅",
    "description": "2024년 6월 1일 아침에 조깅을 했다.",
    "timestamp": "2024-06-01T07:30:00"
  },
  {
    "title": "친구와 카페",
    "description": "오후에는 친구와 카페에서 대화를 나눴다.",
    "timestamp": "2024-06-01T15:00:00"
  },
  {
    "title": "저녁 독서",
    "description": "저녁에는 새로운 소설을 읽었다.",
    "timestamp": "2024-06-01T20:00:00"
  }
]
```

---

## 3. 공통 사항

- 모든 요청은 인증(로그인)이 필요합니다.
- 응답 코드가 200이 아닌 경우, 일반적으로 401(인증 실패), 400(파라미터 오류), 500(서버 오류) 등이 반환될 수 있습니다.
- `userInput`은 한글, 영어 등 자연어로 자유롭게 입력 가능합니다.
- `/ask`는 LLM의 자유 답변(요약/질의 등)을, `/generate`는 구조화된 LifeLog 리스트(JSON)를 반환합니다.

---

## 4. 요약

| 엔드포인트                | 설명                       | 입력 파라미터         | 응답 타입               |
|---------------------------|----------------------------|-----------------------|-------------------------|
| POST /api/v2/llms/ask     | LLM에게 요약/질의          | userInput(String)     | String                  |
| POST /api/v2/llms/generate| 일기 → LifeLog 구조화 변환 | userInput(String)     | List     |

---

**이 문서를 `Documents/API/LLM.md` 등에 추가하면, 개발자와 사용자 모두 쉽게 이해하고 활용할 수 있습니다!**  
필요하면 LifeLogOutput의 필드 구조 등도 추가로 문서화해 주세요.