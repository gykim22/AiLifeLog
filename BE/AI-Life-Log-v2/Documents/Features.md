# LLM 관련 주요 기능 설명

## 1. Templator

- Templator는 LLM 프롬프트를 동적으로 생성하기 위한 템플릿 시스템입니다.
- 사용자가 입력한 변수나 데이터를 템플릿에 삽입하여, 일관되고 재사용 가능한 프롬프트를 만들 수 있습니다.
- 이를 통해 다양한 상황에 맞는 맞춤형 프롬프트를 쉽게 생성하고 관리할 수 있습니다.

## 2. Structured Output

- Structured Output 기능은 LLM의 응답을 미리 정의된 구조화된 데이터 형식으로 변환하는 기능입니다.
- Spring AI의 StructuredOutputConverter, 특히 BeanOutputConverter를 활용하여 JSON 형태의 응답을 Java 객체로 자동 변환할 수 있습니다.
- 이를 통해 LLM의 자유 텍스트 응답을 신뢰성 있고 쉽게 처리할 수 있는 구조화된 데이터로 받을 수 있습니다.

## 3. Function Calling

- Function Calling은 LLM이 특정 기능(함수)을 호출하여 외부 데이터나 서비스를 연동하는 기능입니다.
- 사용자의 요청에 따라 적절한 함수를 호출해 데이터를 조회하거나 조작하고, 그 결과를 바탕으로 LLM이 응답을 생성합니다.
- 예를 들어, 라이프로그 데이터를 특정 기간이나 키워드로 검색하는 함수를 호출하여, 최신 데이터 기반의 맞춤형 답변을 제공할 수 있습니다.
- Spring AI에서는 @Tool, @ToolParam 어노테이션을 사용해 함수들을 정의하고, LLM이 이를 호출하도록 연동할 수 있습니다.

---

이상은 templator, structured output, function calling 기능에 대한 간략한 설명입니다.