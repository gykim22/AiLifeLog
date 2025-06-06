# 클라우드컴퓨팅 TermProject Backend

## 소개
이 프로젝트는 AI 기반 라이프로그 관리 시스템의 백엔드 서버입니다. 사용자의 일상을 기록하고, AI를 통해 이를 분석하고 요약하는 기능을 제공합니다.

## 주요 기능

### 1. 사용자 인증 시스템
- 회원가입 및 로그인 기능
- JWT 토큰 기반 인증
- 사용자 정보 관리

### 2. 라이프로그 관리
- 일상 활동 기록 생성, 조회, 수정, 삭제
- 날짜 기반 필터링
- 페이지네이션 지원

### 3. AI 기반 분석 (LLM)
- 자연어 기반 라이프로그 요약
- 텍스트 기반 라이프로그 자동 생성
- 맞춤형 질의응답 기능

## AI 특징

### 1. Templator
- 동적 프롬프트 생성 시스템
- 상황별 맞춤형 프롬프트 관리

### 2. Structured Output
- LLM 응답의 구조화된 데이터 변환
- Spring AI의 BeanOutputConverter 활용

### 3. Function Calling
- LLM과 외부 서비스 연동
- 데이터 기반 맞춤형 응답 생성

## API 문서

### 인증 API
- POST `/api/v2/auth/login` - 로그인
- POST `/api/v2/auth/signup` - 회원가입

### 사용자 API
- GET `/api/v2/users/self` - 내 정보 조회
- DELETE `/api/v2/users/self` - 계정 삭제

### 라이프로그 API
- GET `/api/v2/logs` - 전체 라이프로그 조회
- GET `/api/v2/logs/{id}` - 특정 라이프로그 조회
- POST `/api/v2/logs` - 라이프로그 생성
- PUT `/api/v2/logs/{id}` - 라이프로그 수정
- DELETE `/api/v2/logs/{id}` - 라이프로그 삭제

### LLM API
- POST `/api/v2/llms/ask` - AI 질의/요약
- POST `/api/v2/llms/generate` - 구조화된 라이프로그 생성

## API 상세 문서
각 API의 상세 명세는 다음 문서를 참조하세요:
- [인증 API 명세서](./AI-Life-Log-v2/Documents/API/Auth.md)
- [라이프로그 API 명세서](./AI-Life-Log-v2/Documents/API/LifeLog.md)
- [LLM API 명세서](./AI-Life-Log-v2/Documents/API/LLM.md)
- [사용자 API 명세서](./AI-Life-Log-v2/Documents/API/User.md)
- [AI 기능 설명서](./AI-Life-Log-v2/Documents/Features.md)
