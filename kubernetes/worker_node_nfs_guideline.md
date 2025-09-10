# Worker Node NFS 설정 가이드라인

이 문서는 k3s 클러스터의 워커 노드에서 NFS 클라이언트를 설정하여 마스터 노드의 NFS 서버에 접근하는 방법을 설명합니다.

## 1. NFS 클라이언트 설치

### Ubuntu/Debian 시스템
```bash
sudo apt update
sudo apt install -y nfs-common
```

## 2. NFS 서버 정보

- **NFS 서버 IP**: `172.26.14.247` (마스터 노드)
- **공유 디렉토리**:
  - PostgreSQL: `/nfs/postgres-data`
  - Redis: `/nfs/redis-data`
  - Kafka: `/nfs/kafka-data`

## 3. NFS 마운트 테스트

워커 노드에서 NFS 공유가 제대로 접근 가능한지 확인:

```bash
# NFS 서버의 공유 목록 확인
showmount -e 172.26.14.247

# 임시 마운트 포인트 생성 후 테스트
sudo mkdir -p /tmp/nfs-test
sudo mount -t nfs 172.26.14.247:/nfs/postgres-data /tmp/nfs-test

# 마운트 확인
ls -la /tmp/nfs-test
df -h /tmp/nfs-test

# 테스트 완료 후 언마운트
sudo umount /tmp/nfs-test
sudo rmdir /tmp/nfs-test
```

## 4. 네트워크 설정 확인

### 방화벽 설정 (필요시)
워커 노드에서 마스터 노드로의 NFS 포트 접근을 허용:

```bash
# UFW (Ubuntu)
sudo ufw allow from 172.26.14.247 to any port 2049
sudo ufw allow from 172.26.14.247 to any port 111

# firewalld (CentOS/RHEL)
sudo firewall-cmd --permanent --add-service=nfs
sudo firewall-cmd --permanent --add-service=rpc-bind
sudo firewall-cmd --reload
```

### 네트워크 연결 테스트
```bash
# NFS 포트 연결 테스트
telnet 172.26.14.247 2049

# ping 테스트
ping -c 3 172.26.14.247
```

## 5. k3s 에이전트 설정

k3s 에이전트가 NFS 마운트를 처리할 수 있도록 설정이 필요할 수 있습니다.

### 서비스 의존성 확인
```bash
# NFS 관련 서비스가 k3s 시작 전에 실행되도록 확인
sudo systemctl enable rpcbind
sudo systemctl start rpcbind
```

## 6. 트러블슈팅

### 일반적인 문제와 해결책

#### 1. 마운트 실패 - "No such file or directory"
```bash
# NFS 서버 상태 확인
ssh 172.26.14.247 'sudo systemctl status nfs-kernel-server'

# 공유 디렉토리 존재 확인
ssh 172.26.14.247 'ls -la /nfs/'
```

#### 2. 권한 오류 - "Permission denied"
```bash
# NFS 서버의 exports 설정 확인
ssh 172.26.14.247 'sudo cat /etc/exports'

# exportfs 재로드
ssh 172.26.14.247 'sudo exportfs -ra'
```

#### 3. 네트워크 연결 문제
```bash
# 방화벽 상태 확인
sudo ufw status
# 또는
sudo firewall-cmd --list-all

# 포트 수신 대기 확인
ssh 172.26.14.247 'sudo netstat -tlnp | grep :2049'
```

#### 4. 로그 확인
```bash
# 워커 노드 시스템 로그
sudo journalctl -u k3s-agent -f

# NFS 관련 로그 (마스터 노드)
ssh 172.26.14.247 'sudo journalctl -u nfs-kernel-server -f'

# 커널 로그에서 NFS 관련 메시지 확인
sudo dmesg | grep -i nfs
```

## 7. 성능 최적화 (선택사항)

### NFS 마운트 옵션 최적화
Kubernetes에서 NFS 볼륨을 사용할 때 성능 향상을 위한 권장 설정:

```yaml
# PV에서 사용할 수 있는 NFS 마운트 옵션 예시
nfs:
  server: 172.26.14.247
  path: "/nfs/postgres-data"
mountOptions:
  - nfsvers=4.1
  - rsize=1048576
  - wsize=1048576
  - hard
  - timeo=600
  - retrans=2
```

## 8. 보안 고려사항

1. **네트워크 분리**: NFS 트래픽은 신뢰할 수 있는 네트워크에서만 허용
2. **액세스 제어**: `/etc/exports`에서 특정 IP 대역만 허용 (현재: 172.26.0.0/16)
3. **데이터 암호화**: 중요한 데이터의 경우 애플리케이션 레벨에서 암호화 고려

## 9. 모니터링

NFS 연결 상태와 성능을 모니터링하기 위한 명령어:

```bash
# NFS 통계 확인
nfsstat -c

# 마운트된 NFS 볼륨 확인
mount | grep nfs

# I/O 통계 확인
iostat -x 1
```

이 가이드라인을 따라 설정하면 워커 노드에서 마스터 노드의 NFS 공유 저장소에 정상적으로 접근할 수 있습니다.