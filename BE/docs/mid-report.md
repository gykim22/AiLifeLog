

### 도전과제

1. Springboot 에서 llm 서비스 어떻게 호출할 것인가?
2. 임베딩을 어떤 방식으로 구현할 것인가?
3. 사용자 요청에 최적화 된 질의응답을 어떻게 구성할 것인가?
4. ai 서비스의 사용량을 어떻게 추적하고 모니터링 할 것인가?


### 해결 계획

1. Spring Ai 프레임워크를 사용하여 기본적으로 사용가능한 모델을 구성하고 사용자 요청에 따라 최적화 된 질의응답을 구성할 것입니다.
해당 프레임워크는 chat completion, embedding, text to image, audio transcription등에 다양한 상황에 맞춘 ai 서비스를 제공합니다.
저희는 이 기능 중 chat completion 기능과 embedding 기능을 활용하여 openai의 gpt 모델에서 제공하는 openai chat 모델을 사용할 것입니다.


참고 자료 : 
+ Spring ai 공식문서 : https://docs.spring.io/spring-ai/reference/index.html
+ Spring ai openai chat 관련 : https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html


2. openai chat model의 벡터 임베딩을 활용하여 특정 lifelog의 content에 대해 임베딩 벡터를 구성 후 PG vector 데이터베이스에 저장할 것입니다.

PG Vector란 PostgreSQL의 확장 모듈로 기존의 db와 함께 벡터 데이터를 저장하고 유사도 검색을 할 수 있는 기능을 제공합니다.
Spring framework에서도 PG vector에 대한 지원을 제공하고 있고, amazon RDS에서도 이에 대한 지원을 제공하고 있으므로 해당 데이터베이스에 통합하여 활용하기로 결정했습니다.

참고 자료
+ PG Vector github : https://github.com/pgvector/pgvector
+ Spring AI PG Vector 참고 자료 : https://docs.spring.io/spring-ai/reference/api/vectordbs/pgvector.html


3. PG vector를 통해 저장된 lifelog의 데이터를 유사도 기반으로 의미론적으로 유사한 데이터를 찾아 사용자 요청에 따라 최적화 된 질의응답을 구성할 것입니다. 
또한 Function Calling 기능을 활용하여 사용자가 자신의 기록과 관련된 추가적인 정보를 요청할 때 이를 제공할 수 있도록 할 것입니다.

+ Spring Ai RAG : https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html
+ Spring AI tool Calling : https://docs.spring.io/spring-ai/reference/api/tools.html

4. Spring AI의 응답 객체의 메타데이터를 적극활용하여 ai 호출과 동시에 사용량을 db에 저장하고 이를 통해 모니터링 할 것입니다.

참고자료
+ Spring ai의 기본 컨셉 : https://docs.spring.io/spring-ai/reference/concepts.html
+ 관련 블로그 : https://www.danvega.dev/blog/spring-ai-tokens