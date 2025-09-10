# NFS CSI Driver

## CSI (Container Storage Interface)란?

CSI는 컨테이너 오케스트레이션 시스템(Kubernetes, Docker Swarm 등)과 스토리지 시스템 간의 표준화된 인터페이스입니다.

### 주요 특징
- **표준화**: 다양한 스토리지 벤더가 동일한 인터페이스로 플러그인 제공
- **플러그인 아키텍처**: 스토리지 드라이버를 독립적으로 배포 및 업데이트 가능
- **벤더 독립성**: Kubernetes 코어와 분리되어 각 스토리지 솔루션별 최적화 가능

## NFS CSI Driver

NFS(Network File System) 기반 스토리지를 Kubernetes에서 사용하기 위한 CSI 드라이버입니다.

### 구성 요소
1. **CSI Controller**: PV 생성/삭제, 볼륨 어태치/디태치 담당
2. **CSI Node**: 실제 노드에서 볼륨 마운트/언마운트 수행
3. **CSI Driver**: nfs.csi.k8s.io 이름으로 등록되는 드라이버

### 동작 로직

```
PVC 생성 → StorageClass 참조 → CSI Provisioner 호출 → NFS 볼륨 생성 → PV 바인딩
```

1. **PVC 생성**: 애플리케이션이 스토리지 요청
2. **StorageClass 확인**: `provisioner: nfs.csi.k8s.io` 설정 확인
3. **CSI Controller 호출**: 볼륨 생성 요청 전달
4. **NFS 볼륨 생성**: 지정된 NFS 서버에 디렉터리/볼륨 생성
5. **PV 생성 및 바인딩**: 자동으로 PV 생성하여 PVC와 바인딩

## 설치 및 사용

### 설치
```bash
./install-nfs-csi.sh
```

### StorageClass 예제
```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: nfs-kafka
provisioner: nfs.csi.k8s.io
parameters:
  server: 172.26.14.247      # NFS 서버 IP
  share: /nfs/kafka-data     # NFS 공유 경로
volumeBindingMode: Immediate
allowVolumeExpansion: true
```

### 확인 명령어
```bash
# CSI 드라이버 확인
kubectl get csidriver

# NFS 관련 Pod 상태 확인
kubectl get pods -n kube-system | grep nfs

# PVC 상태 확인
kubectl get pvc -A
```

## 트러블슈팅

### PVC Pending 상태
- **원인**: NFS CSI 드라이버 미설치
- **해결**: `install-nfs-csi.sh` 실행

### 볼륨 마운트 실패
- NFS 서버 접근성 확인
- 방화벽 및 네트워크 정책 점검
- NFS 서버의 공유 디렉터리 권한 확인