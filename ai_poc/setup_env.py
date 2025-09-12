#!/usr/bin/env python3
"""
conda nlp 환경에서 FastAPI 뉴스 분류 서비스 설정 및 실행 스크립트
"""

import subprocess
import sys
import os

def run_command(command, description):
    """명령어 실행"""
    print(f"\n🔧 {description}")
    print(f"명령어: {command}")
    
    try:
        result = subprocess.run(command, shell=True, check=True, capture_output=True, text=True)
        if result.stdout:
            print(f"✅ 성공: {result.stdout.strip()}")
        return True
    except subprocess.CalledProcessError as e:
        print(f"❌ 실패: {e}")
        if e.stderr:
            print(f"오류 메시지: {e.stderr}")
        return False

def main():
    print("🚀 FastAPI 뉴스 분류 서비스 환경 설정")
    print("=" * 50)
    
    # 현재 활성화된 conda 환경 확인
    try:
        result = subprocess.run("conda info --envs", shell=True, capture_output=True, text=True)
        active_env = None
        for line in result.stdout.split('\n'):
            if '*' in line:
                active_env = line.split()[0]
                break
        
        print(f"현재 활성화된 환경: {active_env}")
        
        if active_env != 'nlp':
            print("⚠️  경고: 'nlp' conda 환경이 활성화되지 않았습니다.")
            print("다음 명령어로 환경을 활성화하세요: conda activate nlp")
            return
    except:
        print("❌ conda 환경을 확인할 수 없습니다.")
        return
    
    # requirements.txt의 패키지 설치
    print(f"\n📦 필요한 패키지 설치 중...")
    if not run_command("pip install -r requirements.txt", "의존성 패키지 설치"):
        print("❌ 패키지 설치 실패. 수동으로 설치해주세요.")
        return
    
    print("\n✅ 환경 설정 완료!")
    print("\n🔥 서버 실행 방법:")
    print("1. conda activate nlp")
    print("2. python main.py")
    print("또는:")
    print("uvicorn main:app --host 0.0.0.0 --port 8000 --reload")
    
    print("\n📋 API 엔드포인트:")
    print("• GET  /                    - 서비스 정보")
    print("• GET  /health             - 헬스 체크")
    print("• POST /classify           - 단일 뉴스 분류")
    print("• POST /classify/batch     - 배치 뉴스 분류")
    print("• GET  /categories         - 카테고리 정보")
    print("• GET  /model/info         - 모델 정보")
    
    print("\n🌐 웹 인터페이스:")
    print("• http://localhost:8000/docs - Swagger UI")
    print("• http://localhost:8000/redoc - ReDoc")

if __name__ == "__main__":
    main()