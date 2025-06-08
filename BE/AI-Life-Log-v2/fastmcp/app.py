import os
from fastmcp import FastMCP
from typing import Optional, Annotated
from datetime import datetime
from pydantic import Field
from httpclient import Client


# 기본 환경변수 설정
default_env = {
    "APPLICATION_URL": "http://localhost:8080",
    "CURSOR_ENABLED": "true",
    "CURSOR_URL": "http://localhost:8000",
    "HOST": "0.0.0.0",
    "PORT": "8000"
}

# 환경변수 설정
if os.path.exists(".env"):
    from dotenv import load_dotenv
    load_dotenv()
else:
    os.environ.update(default_env)

# 전역 변수 초기화
APPLICATION_URL = os.getenv("APPLICATION_URL", "http://localhost:8080")
jwt_token: Optional[str] = None
client = Client(APPLICATION_URL)


mcp = FastMCP(
    name="LifeLogServer",
    instructions="""
    당신은 일상을 기록하는 life log 애플리케이션의 커뮤니케이션을 도와주는 챗봇입니다.
    사용자의 요청에 따라 특정 로그 정보들을 agent에게 전달하고 간단한 lifelog 작성을 도와줍니다.
    """
)


@mcp.tool(
    name="login",
    description="""
    사용자 인증을 처리하는 도구입니다.
    성공적으로 로그인하면 이후 다른 작업에 사용할 토큰이 저장됩니다.
    """
)
async def login(
    username: Annotated[str, Field(description="로그인할 사용자 이름")],
    password: Annotated[str, Field(description="사용자 비밀번호")]
) -> str:
    """사용자 로그인 처리 tool"""
    try:
        global jwt_token
        jwt_token = await client.login(username, password)
        return f"로그인 성공! {username}님 환영합니다."
    except Exception as e:
        return f"로그인 실패: {str(e)}"


@mcp.tool(
    name="get_logs",
    description="""
    라이프로그 목록을 조회하는 도구입니다.
    페이지네이션과 날짜 기반 필터링을 지원합니다.
    로그인이 필요한 작업입니다.
    """
)
async def get_logs(
    page: Annotated[int, Field(description="페이지 번호", ge=0)] = 0,
    size: Annotated[int, Field(description="페이지 크기", ge=1, le=100)] = 10,
    from_date: Annotated[Optional[str], Field(
        description="시작 날짜 (YYYY-MM-DD)",
        pattern=r"^\d{4}-\d{2}-\d{2}$"
    )] = None,
    to_date: Annotated[Optional[str], Field(
        description="종료 날짜 (YYYY-MM-DD)",
        pattern=r"^\d{4}-\d{2}-\d{2}$"
    )] = None
) -> str:
    """라이프로그 목록을 조회합니다."""
    if not jwt_token:
        return "먼저 로그인해 주세요!"
    
    try:
        # 날짜 변환
        from_datetime = (
            datetime.strptime(from_date, "%Y-%m-%d") if from_date else None
        )
        to_datetime = (
            datetime.strptime(to_date, "%Y-%m-%d") if to_date else None
        )
        
        logs = await client.get_logs(
            page=page,
            size=size,
            from_date=from_datetime,
            to_date=to_datetime
        )
        
        # 응답 포맷팅
        content = logs.get("content", [])
        if not content:
            return "조회된 라이프로그가 없습니다."
            
        result = "라이프로그 목록:\n"
        for log in content:
            result += (
                f"- [{log['timestamp']}] "
                f"{log['title']}: {log['description']}\n"
            )
            
        return result
        
    except Exception as e:
        return f"조회 실패: {str(e)}"


@mcp.tool(
    name="create_log",
    description="""
    새로운 라이프로그를 생성하는 도구입니다.
    제목, 설명, 선택적으로 타임스탬프를 지정할 수 있습니다.
    로그인이 필요한 작업입니다.
    """
)
async def create_log(
    title: Annotated[str, Field(
        description="라이프로그 제목",
        min_length=1,
        max_length=100
    )],
    description: Annotated[str, Field(
        description="라이프로그 내용",
        min_length=1,
        max_length=1000
    )],
    timestamp: Annotated[Optional[str], Field(
        description="작성 시간 (YYYY-MM-DD HH:MM:SS)",
        pattern=r"^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$"
    )] = None
) -> str:
    """새로운 라이프로그를 생성합니다."""
    if not jwt_token:
        return "먼저 로그인해 주세요!"
    
    try:
        # 타임스탬프 변환
        datetime_obj = (
            datetime.strptime(timestamp, "%Y-%m-%d %H:%M:%S")
            if timestamp
            else None
        )
        
        log = await client.create_log(
            title=title,
            description=description,
            timestamp=datetime_obj
        )
        
        return (
            f"라이프로그가 생성되었습니다!\n"
            f"제목: {log['title']}\n"
            f"설명: {log['description']}\n"
            f"시간: {log['timestamp']}"
        )
        
    except Exception as e:
        return f"생성 실패: {str(e)}"


@mcp.tool(
    name="update_log",
    description="""
    기존 라이프로그를 수정하는 도구입니다.
    제목, 설명, 타임스탬프를 선택적으로 업데이트할 수 있습니다.
    로그인이 필요한 작업입니다.
    """
)
async def update_log(
    log_id: Annotated[int, Field(description="수정할 라이프로그 ID", ge=1)],
    title: Annotated[Optional[str], Field(
        description="새 제목",
        min_length=1,
        max_length=100
    )] = None,
    description: Annotated[Optional[str], Field(
        description="새 내용",
        min_length=1,
        max_length=1000
    )] = None,
    timestamp: Annotated[Optional[str], Field(
        description="새 작성 시간 (YYYY-MM-DD HH:MM:SS)",
        pattern=r"^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$"
    )] = None
) -> str:
    """기존 라이프로그를 수정합니다."""
    if not jwt_token:
        return "먼저 로그인해 주세요!"
    
    try:
        # 타임스탬프 변환
        datetime_obj = (
            datetime.strptime(timestamp, "%Y-%m-%d %H:%M:%S")
            if timestamp
            else None
        )
        
        log = await client.update_log(
            log_id=log_id,
            title=title,
            description=description,
            timestamp=datetime_obj
        )
        
        return (
            f"라이프로그가 수정되었습니다!\n"
            f"제목: {log['title']}\n"
            f"설명: {log['description']}\n"
            f"시간: {log['timestamp']}"
        )
        
    except Exception as e:
        return f"수정 실패: {str(e)}"


if __name__ == "__main__":
    import platform
    
    # Windows에서 실행 시 특별한 설정 추가
    if platform.system() == "Windows":
        import asyncio
        asyncio.set_event_loop_policy(asyncio.WindowsSelectorEventLoopPolicy())
    
    # STDIO 트랜스포트로 서버 실행
    mcp.run(transport="sse", host="127.0.0.1", port=8000)
