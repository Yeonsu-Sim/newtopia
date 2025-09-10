# 마스터 노드 NFS 서버 설정 가이드

이 문서는 k3s 마스터 노드에서 NFS 서버를 설정하는 모든 과정을 단계별로 정리한 것입니다.

## 1. 시스템 업데이트 및 NFS 서버 설치

### 패키지 목록 업데이트
```bash
sudo apt update
```

### NFS 서버 패키지 설치
```bash
sudo apt install -y nfs-kernel-server
```

설치되는 패키지:
- `nfs-kernel-server`: NFS 서버 메인 패키지
- `nfs-common`: NFS 클라이언트 도구
- `rpcbind`: RPC 바인딩 서비스
- `keyutils`: 인증 키 관리
- `libnfsidmap1`: NFS ID 매핑 라이브러리

## 2. NFS 공유 디렉토리 생성

### 공유 디렉토리 구조 생성
```bash
sudo mkdir -p /nfs/{postgres-data,redis-data,kafka-data}
```

생성되는 디렉토리:
- `/nfs/postgres-data`: PostgreSQL 데이터 저장용
- `/nfs/redis-data`: Redis 데이터 저장용  
- `/nfs/kafka-data`: Kafka 데이터 저장용

## 3. 마스터 노드 IP 주소 확인

### 내부 IP 주소 조회
```bash
ip route get 1 | awk '{print $7; exit}' | head -1
```

결과: `172.26.14.247` (마스터 노드 IP)

## 4. NFS Exports 설정

### /etc/exports 파일에 NFS 공유 설정 추가
```bash
sudo tee -a /etc/exports << 'EOF'

# Newtopia k3s cluster NFS exports
/nfs/postgres-data 172.26.0.0/16(rw,sync,no_subtree_check,no_root_squash)
/nfs/redis-data 172.26.0.0/16(rw,sync,no_subtree_check,no_root_squash)
/nfs/kafka-data 172.26.0.0/16(rw,sync,no_subtree_check,no_root_squash)
EOF
```

### Exports 설정 옵션 설명:
- `172.26.0.0/16`: 허용할 클라이언트 네트워크 대역
- `rw`: 읽기/쓰기 권한
- `sync`: 동기화 모드 (안정성 우선)
- `no_subtree_check`: 서브트리 체크 비활성화 (성능 향상)
- `no_root_squash`: root 권한 유지 (Kubernetes Pod에서 필요)

## 5. NFS 서비스 설정 및 시작

### Exports 설정 재로드
```bash
sudo exportfs -ra
```

### NFS 서버 서비스 활성화 및 시작
```bash
sudo systemctl enable --now nfs-kernel-server
```

### NFS 서버 상태 확인
```bash
sudo systemctl status nfs-kernel-server
```

## 6. NFS 서버 동작 확인

### Export 목록 확인
```bash
showmount -e localhost
```

예상 출력:
```
Export list for localhost:
/nfs/kafka-data    172.26.0.0/16
/nfs/redis-data    172.26.0.0/16
/nfs/postgres-data 172.26.0.0/16
```

### NFS 포트 확인
```bash
sudo netstat -tlnp | grep :2049
```

### 방화벽 설정 (UFW 활성화시 필수)
```bash
# UFW 사용시 (필수 - UFW가 활성화되어 있을 경우)
sudo ufw allow from 172.26.0.0/16 to any port 2049
sudo ufw allow from 172.26.0.0/16 to any port 111
sudo ufw allow from 172.26.0.0/16 to any port 20048

# firewalld 사용시  
sudo firewall-cmd --permanent --add-service=nfs
sudo firewall-cmd --permanent --add-service=rpc-bind
sudo firewall-cmd --permanent --add-service=mountd
sudo firewall-cmd --reload
```

**방화벽 설정이 필수인 이유:**
- UFW 방화벽이 활성화된 경우 NFS 포트들이 기본적으로 차단됨
- 워커 노드에서 마스터 노드의 NFS 서버로 접근할 때 필요한 포트들:
  - **2049**: NFS 메인 서비스 포트
  - **111**: RPC 포트매퍼 (서비스 검색용)
  - **20048**: mountd 포트 (마운트 요청 처리용)
- 클러스터 내부 네트워크(172.26.0.0/16)에서만 접근 허용하여 보안 유지

## 7. 디렉토리 권한 설정 (필요시)

### NFS 공유 디렉토리 권한 조정
```bash
# 모든 사용자가 접근 가능하도록 설정 (개발 환경용)
sudo chmod 777 /nfs/{postgres-data,redis-data,kafka-data}

# 또는 특정 UID/GID로 소유권 변경
# sudo chown -R 999:999 /nfs/postgres-data  # PostgreSQL
# sudo chown -R 999:999 /nfs/redis-data     # Redis
# sudo chown -R 1000:1000 /nfs/kafka-data   # Kafka
```

## 8. 로그 및 트러블슈팅

### NFS 서버 로그 확인
```bash
sudo journalctl -u nfs-kernel-server -f
```

### Export 상태 확인
```bash
sudo exportfs -v
```

### NFS 통계 확인
```bash
nfsstat -s
```

## 9. 서비스 재시작 (필요시)

### NFS 서비스 재시작
```bash
sudo systemctl restart nfs-kernel-server
```

### RPC 서비스 재시작  
```bash
sudo systemctl restart rpcbind
```

## 10. 자동 시작 설정 확인

### 부팅시 자동 시작 설정 확인
```bash
systemctl is-enabled nfs-kernel-server
systemctl is-enabled rpcbind
```

## 완료 후 확인사항

1. ✅ NFS 서버가 정상 실행 중인지 확인
2. ✅ Export 목록에 3개 디렉토리가 모두 표시되는지 확인  
3. ✅ 네트워크 포트 2049가 수신 대기 중인지 확인
4. ✅ 공유 디렉토리 권한이 적절히 설정되었는지 확인

## 주요 설정 파일 위치

- **NFS Exports 설정**: `/etc/exports`
- **NFS 서버 설정**: `/etc/default/nfs-kernel-server`
- **NFS 일반 설정**: `/etc/nfs.conf`
- **ID 매핑 설정**: `/etc/idmapd.conf`

## 다음 단계

마스터 노드에서 NFS 서버 설정이 완료되면:
1. Kubernetes Storage 리소스를 NFS 방식으로 업데이트
2. 워커 노드에 NFS 클라이언트 설치 (`worker_node_nfs_guideline.md` 참고)
3. 애플리케이션 배포 및 스토리지 연결 테스트

## 추가 데이터베이스/서비스 추가시 작업

새로운 서비스(예: MongoDB, Elasticsearch 등)를 위한 NFS 스토리지를 추가할 때:

### 1. NFS 공유 디렉토리 추가
```bash
# 새로운 서비스용 디렉토리 생성
sudo mkdir -p /nfs/새서비스-data

# 권한 설정
sudo chmod 777 /nfs/새서비스-data
# 또는 특정 UID/GID 설정
# sudo chown -R 특정UID:특정GID /nfs/새서비스-data
```

### 2. /etc/exports 파일 업데이트
```bash
# /etc/exports에 새 공유 추가
echo "/nfs/새서비스-data 172.26.0.0/16(rw,sync,no_subtree_check,no_root_squash)" | sudo tee -a /etc/exports

# exports 재로드
sudo exportfs -ra
```

### 3. 새로운 PV/PVC YAML 파일 생성
```bash
# storage/ 디렉토리에 새서비스-volume.yaml 생성
# 기존 파일들(postgres-volume.yaml 등)을 참고하여 작성
```

### 4. 설정 확인
```bash
# export 목록에 새 공유가 추가되었는지 확인
showmount -e localhost

# Kubernetes 리소스 적용
kubectl apply -f storage/새서비스-volume.yaml
```