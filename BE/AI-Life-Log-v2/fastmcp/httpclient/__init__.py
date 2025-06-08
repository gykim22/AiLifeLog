from typing import Optional, Dict, Any
from datetime import datetime
import httpx
import os


class Client:
    """Life Log HTTP 클라이언트"""
    
    def __init__(self, base_url: Optional[str] = None):
        self._base_url = base_url or os.getenv(
            "APPLICATION_URL", 
            "http://localhost:8080"
        )
        self._token: Optional[str] = None
    
    async def login(self, username: str, password: str) -> str:
        """로그인
        
        Args:
            username: 사용자 이름
            password: 비밀번호
            
        Returns:
            str: 인증 토큰
        """
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{self._base_url}/api/v2/auth/login",
                json={"username": username, "password": password}
            )
            response.raise_for_status()
            self._token = response.json()["token"]
            return self._token
    
    async def get_logs(
        self, 
        page: int = 0, 
        size: int = 10,
        from_date: Optional[datetime] = None,
        to_date: Optional[datetime] = None
    ) -> Dict[str, Any]:
        """라이프로그 전체 조회
        
        Args:
            page: 페이지 번호 (0부터 시작)
            size: 페이지 크기
            from_date: 시작 날짜
            to_date: 종료 날짜
            
        Returns:
            Dict[str, Any]: 페이지네이션된 라이프로그 목록
        """
        params = {"page": page, "size": size}
        
        if from_date:
            params["from"] = from_date.strftime("%Y-%m-%d")
        if to_date:
            params["to"] = to_date.strftime("%Y-%m-%d")
            
        async with httpx.AsyncClient() as client:
            response = await client.get(
                f"{self._base_url}/api/v2/logs",
                params=params,
                headers={"Authorization": f"Bearer {self._token}"}
            )
            response.raise_for_status()
            return response.json()
    
    async def create_log(
        self, 
        title: str,
        description: str,
        timestamp: Optional[datetime] = None
    ) -> Dict[str, Any]:
        """라이프로그 생성
        
        Args:
            title: 제목
            description: 설명
            timestamp: 타임스탬프 (생략시 현재 시간)
            
        Returns:
            Dict[str, Any]: 생성된 라이프로그
        """
        data = {
            "title": title,
            "description": description
        }
        
        if timestamp:
            data["timestamp"] = timestamp.strftime("%Y-%m-%dT%H:%M:%S.%f")
            
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{self._base_url}/api/v2/logs",
                json=data,
                headers={"Authorization": f"Bearer {self._token}"}
            )
            response.raise_for_status()
            return response.json()
    
    async def update_log(
        self,
        log_id: int,
        title: Optional[str] = None,
        description: Optional[str] = None,
        timestamp: Optional[datetime] = None
    ) -> Dict[str, Any]:
        """라이프로그 수정
        
        Args:
            log_id: 라이프로그 ID
            title: 제목 (선택)
            description: 설명 (선택)
            timestamp: 타임스탬프 (선택)
            
        Returns:
            Dict[str, Any]: 수정된 라이프로그
        """
        data = {}
        if title is not None:
            data["title"] = title
        if description is not None:
            data["description"] = description
        if timestamp is not None:
            data["timestamp"] = timestamp.strftime("%Y-%m-%dT%H:%M:%S.%f")
            
        async with httpx.AsyncClient() as client:
            response = await client.put(
                f"{self._base_url}/api/v2/logs/{log_id}",
                json=data,
                headers={"Authorization": f"Bearer {self._token}"}
            )
            response.raise_for_status()
            return response.json() 