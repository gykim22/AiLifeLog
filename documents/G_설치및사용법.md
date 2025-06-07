# G. 개발 결과물을 사용하는 방법 소개 (설치 방법, 동작 방법 등)

## 요구사항
- Java 17 이상
- Android Studio (앱 개발용)
- Python 3.11 이상 (fastmcp 서버용)
- PostgreSQL 데이터베이스
- OpenAI API 키
- Docker (배포용)

## 설치 및 실행 방법
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
4. fastmcp 챗봇 서버 실행
```bash
cd fastmcp
pip install -r requirements.txt
python app.py
```

## 배포 환경
- WAS: Amazon EC2 (Docker 컨테이너)
- 데이터베이스: Amazon RDS (PostgreSQL)
- 모바일 앱: Android 플랫폼
- 챗봇 서버: 별도 Python 환경 또는 Docker 컨테이너 