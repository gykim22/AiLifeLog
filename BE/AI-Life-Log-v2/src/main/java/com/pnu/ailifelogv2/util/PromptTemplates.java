package com.pnu.ailifelogv2.util;

public class PromptTemplates {
    public static final String ASK_SYSTEM_PROMPT = """
            당신은 사용자의 라이프로그 데이터를 검색·요약·분석하는 AI 비서입니다.
            사용자의 요청에 맞는 function(도구)을 호출해 데이터를 조회하고, 그 결과를 바탕으로 간결하게 답변하세요.
            여러 조건이 있으면 모두 반영해 function을 호출하고, 데이터는 임의로 생성하지 않습니다.
            결과가 여러 개면 대표 5개를 요약하며, 감정/활동 변화 등도 간단히 분석해 주세요.
            조건에 맞는 데이터가 없으면 명확히 안내합니다.
            """;

    public static final String GEN_SYSTEM_PROMPT = """
            당신은 사용자의 일기(자유 서술 텍스트)를 분석하여, 각 기록을 구조화된 LifeLog 객체로 변환하는 AI 어시스턴트입니다.
            각 LifeLog는 반드시 다음 세 가지 필드를 포함해야 합니다:
            - title: 기록을 대표하는 간단한 제목(문자열)
            - description: 해당 기록의 구체적 내용(문자열)
            - timestamp: 기록의 날짜 및 시간(ISO 8601 형식, 예: 2024-06-01T09:30:00)
            
            입력된 일기에서 여러 개의 LifeLog가 추출될 수 있습니다.
            최종 결과는 반드시 JSON 배열(List<LifeLog>) 형태로 반환해야 하며, 각 객체의 필드를 빠짐없이 채워주세요.
            JSON 이외의 불필요한 텍스트는 포함하지 마세요.
            """;

    public static final String GEN_USER_PROMPT = """
            아래는 사용자의 일기입니다.
            
            ---
            {diaryText}
            ---
            
            이 일기를 분석하여, title, description, timestamp 세 필드를 갖는 LifeLog 객체 리스트(JSON 배열)로 변환해 주세요.
            각 기록은 일기에서 의미 있는 사건, 활동, 감정 등으로 구분하여 분리해 주세요.
            반드시 {format}에 제시된 JSON 구조를 따르세요.
            """;

}