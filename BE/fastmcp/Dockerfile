FROM python:3.11-slim

WORKDIR /app

# 시스템 의존성 설치
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

# 필요한 Python 패키지 설치를 위한 requirements.txt 복사
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 애플리케이션 코드 복사
COPY . .

# 환경 변수 설정
ENV APPLICATION_URL=http://localhost:8080
ENV CURSOR_ENABLED=true
ENV CURSOR_URL=http://localhost:8000
ENV HOST=0.0.0.0
ENV PORT=8000

# 포트 노출
EXPOSE 8000

# 서버 실행
CMD ["python", "app.py"] 