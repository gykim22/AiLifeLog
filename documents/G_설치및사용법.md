# G. 개발 결과물을 사용하는 방법 소개 (설치 방법, 동작 방법 등)


## 프론트 엔드  안드로이드 앱 실행 방법

### 1. 개발 환경
- **Android Studio Ladybug 2024.2.1 Patch 3**
- Kotlin 2.0.20 이상
- Gradle 8.x
- Android SDK 34 이상
- Java 11
- Windows 11

### 2. 실행 방법
1. 본 레포지토리의 FE 디렉토리를 Android Studio로 프로젝트 오픈.
2. `build.gradle.kts` 및 `settings.gradle.kts` sync.
3. 실제 기기(Android 모바일 디바이스) 유선 연결 또는 IDE 내 탑재된 Android 에뮬레이터 설정
4. `app` 모듈을 선택하고 **Run** 실행 (Shift + F10)
5. 아이디/비밀번호로 회원 가입 후 라이프로그 작성
6. 앱을 종료 및 재실행 시 토큰이 유효하다면 자동 로그인, 아닐 시 로그인 페이지로 이동

### 3. 주요 기능
- 회원가입 / 로그인 (JWT 기반)
- 라이프로그 생성 / 삭제 / 조회
- GPT 기반 분석 / 생성 기능 호출

## Backend 배포 환경 구성 및 실행 방법

### Spring Boot API Server 실행 방법

+ 위치: [BE/AI-Life-Log-v2/](../../BE/AI-Life-Log-v2/)

### 요구사항
- Java 17 이상
- Android Studio (앱 개발용)
- Python 3.11 이상 (fastmcp 서버용)
- PostgreSQL 데이터베이스
- OpenAI API 키
- Docker (배포용)

### 로컬에서 실행하는 방법

1. 환경 변수 설정

```properties
OPENAI_API_KEY=your_api_key
SPRING_DATASOURCE_URL=your_database_url
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
```

2. 의존성 설치 및 빌드 (Spring Boot)

```bash
./gradlew build
```

3. 애플리케이션 실행 (Spring Boot)

```bash
./gradlew bootRun
```

### Docker-compose 를 통한 실행 방법

1. `.env` 파일 생성 및 환경 변수 설정

```properties
SERVER_PORT=8080
OPENAI_API_KEY=sk-???
DB_URL=jdbc:postgresql://your-host:your-port/your-database
DB_USER=postgres
DB_PASSWORD=your-password
JWT_SECRET=???
```

2. 빌드 및 실행


```bash
docker-compose up --build
```
> 데이터베이스와 함께 서버가 실행됩니다. 

## mcp 기반 챗봇 서버 실행 방법

+ 위치: [BE/AI-Life-Log-v2/fastmcp/](../../BE/AI-Life-Log-v2/fastmcp/)

### 1. 가상 Python 환경 생성 및 의존성 설치

```bash
python -m venv .venv
# Windows
.venv\Scripts\activate
# macOS/Linux
source .venv/bin/activate
pip install --upgrade pip
pip install -r requirements.txt
```

### 2. 환경 변수 설정 또는 `.env` 파일 생성

- `.env` 파일 예시:
```properties
APPLICATION_URL=http://localhost:8080
HOST=0.0.0.0
PORT=8000
CURSOR_ENABLED=true
CURSOR_URL=http://localhost:8000
```
- 또는 환경변수로 직접 지정 가능

### 3. 서버 실행

```bash
python app.py
```
- 기본적으로 8000번 포트에서 SSE 기반으로 실행됩니다.
- Windows 환경에서는 내부적으로 asyncio 설정이 자동 적용됩니다.

### 4. Docker로 실행 (선택)

```bash
docker build -t fastmcp .
docker run -p 8000:8000 --env-file .env fastmcp
```
- Docker 환경에서도 `.env` 파일을 활용할 수 있습니다.

---

### 사용 방법

#### 1. Cursor 등 외부 툴 연동

- 예시 (Cursor 환경):
```json
mcp {
    "name": "ai-life-log-mcp",
    "url": "http://localhost:8000/mcp"
}
```
- Cursor의 settings > mcp 탭에서 위와 같이 등록

#### 2. 챗봇 명령어 예시

- 로그인: `login(username, password)`
- 라이프로그 생성: `create_log(title, description, timestamp)`
- 라이프로그 조회: `get_logs(page, size, from_date, to_date)`
- 라이프로그 수정: `update_log(log_id, title, description, timestamp)`

#### 3. 서버 종료

- 가상환경 비활성화:  
  - Windows: `deactivate`  
  - macOS/Linux: `deactivate`

---

### 실전 사용 예시

1. 서버 실행 후, Cursor에서 MCP 연결 설정을 추가합니다.
2. 챗봇에게 "login", "create_log", "get_logs" 등 명령어를 자연어로 입력해 라이프로그를 관리할 수 있습니다.
3. 서버 로그와 챗봇 응답을 통해 정상 동작을 확인하세요.

> 포트 충돌, 환경 변수 누락 등 오류가 발생하면 설정을 다시 확인하세요.


