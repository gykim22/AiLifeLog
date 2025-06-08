## 지원 기능

+ AI를 활용한 라이프 로깅 앱
    + 일상 생활 패턴을 로그와 같은 형식으로 기록하고 분석 & 평가가 가능한 앱
    + 일상 로깅을 기반으로 앞으로의 생활 패턴을 예측하고 추천하는 기능
    
+ LLM 기술 활용
    + 비정형 텍스트 데이터(일기 OR 음성 정보)에서 정형화된 시간열의 생활 패턴을 추출하고 이를 저장하는 기능
    + 추출된 생활 패턴을 기반으로 앞으로의 생활 패턴을 예측하고 추천하는 기능


## 사용자 입력 정보 활용

1. 라이프 로그 활용 방안
    1. 음성 데이터 -> 비정형 텍스트 데이터 -> 라이프 로그 정형 데이터 변환
    2. 비정형 텍스트 데이터 -> 라이프 로그 정형 데이터 변환
    3. 라이프 로그 정형 데이터 -> 라이프 로그 분석 & 평가 & 추천

2. GPS 정보 활용 방반
    1. 라이프 로그에 태그 기반 정도로 정형화된 데이터 저장
    2. 사용자가 함께 입력 시 자동으로 태그로 변환 혹은 태그 기반 데이터 저장

3. 유사도 기반 사용자 질문 파악 및 답변 제공
   1. Life Log의 내용을 기반으로 유사도 기반 질문 파악 및 답변 제공

## 예상 모델(초안)

base_entity
+ id
+ created_at
+ updated_at

User extends base_entity
+ username
+ email
+ password

Session extends base_entity
+ user_id
+ expired_at


Location extends base_entity
+ tag_name
+ latitude
+ longitude
+ user_id

LifeLog extends base_entity
+ user_id
+ location_tag_id
+ content
+ timestamp 

Diary extends base_entity
+ user_id
+ title
+ content
+ date 

llm_log extends base_entity
+ user_id
+ content
+ timestamp 


## 예상 API

## 인증

+ `POST /api/auth/login`
+ `POST /api/auth/logout`
+ `POST /api/auth/register`


## 사용자 정보

+ `GET /api/users/`
+ `GET /api/users/{user_id}`
+ `PUT /api/users/{user_id}` 
+ `DELETE /api/users/{user_id}`


## 라이프 로그 관리

+ `GET /api/life-logs/`
+ `GET /api/life-logs/{life_log_id}`
+ `PUT /api/life-logs/{life_log_id}`
+ `DELETE /api/life-logs/{life_log_id}`

## 일기 관리

+ `GET /api/diaries/`
+ `GET /api/diaries/{diary_id}`
+ `PUT /api/diaries/{diary_id}`
+ `PATCH /api/diaries/{diary_id}`
+ `DELETE /api/diaries/{diary_id}`


## 위치 관리

+ `GET /api/locations/`
+ `GET /api/locations/{location_id}`
+ `PUT /api/locations/{location_id}`
+ `DELETE /api/locations/{location_id}`


## llm 요약 요청


+ `GET /api/ai/summaries/life-logs/{life_log_id}`
+ `GET /api/ai/summaries/diaries/{diary_id}`


## 유사도 기반 질문 파악 및 답변 제공

+ `GET /api/ai/answers/`

- 사용자는 하루 동안 기분, 활동 태그, 짧은 텍스트 일지, 사진, 음성 메모 등을 간편하게 입력할 수 있음[2][6].
- GPS 기반 위치 정보가 자동으로 기록되어 이동 경로 및 방문 장소가 저장됨[1][4].
- 반복되는 활동 패턴이나 특정 장소 방문 빈도, 이동 경로 등 위치 기반 데이터가 자동 분석되어 시각화됨[4].
- 입력된 데이터(기분, 활동, 위치, 메모 등)는 통계 차트, 월별/연별 그래프, Year in Pixels 등 다양한 방식으로 시각화되어 제공됨[2][6].
- 목표 설정, 습관 추적, 데이터 백업, 프라이버시 보호(PIN, 지문, Face ID 잠금) 등 부가 기능 제공[2][6].
- 데이터는 .csv 파일 등으로 내보내기 가능하며, 일기장처럼 검색 및 회고에 활용할 수 있음[2][6].


## Use case 분석

