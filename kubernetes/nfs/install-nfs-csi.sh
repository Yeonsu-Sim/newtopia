#!/bin/bash

# NFS CSI Driver 설치 스크립트
# NFS 기반 StorageClass 사용을 위한 필수 구성요소 설치

echo "=== Installing NFS CSI Driver ==="

# 1. NFS Server 설치 (예제용 - 실제 환경에서는 외부 NFS 서버 사용)
# 주석: 로컬에 NFS 서버가 이미 있는 경우 이 부분을 주석 처리하세요
# echo "Installing NFS Server (example)..."
# kubectl apply -f https://raw.githubusercontent.com/kubernetes-csi/csi-driver-nfs/master/deploy/example/nfs-provisioner/nfs-server.yaml

# 2. NFS CSI Driver 설치 (필수 - 기존 NFS 서버와 통신하기 위해 필요)
echo "Installing NFS CSI Driver components..."
curl -skSL https://raw.githubusercontent.com/kubernetes-csi/csi-driver-nfs/v4.9.0/deploy/install-driver.sh | bash -s v4.9.0 --

echo "=== Installation completed ==="

echo "Waiting for CSI driver pods to be ready..."
sleep 30

echo "Checking NFS CSI Driver status:"
kubectl get pods -n kube-system | grep nfs

echo "Checking CSI Driver registration:"
kubectl get csidriver | grep nfs

echo "=== NFS CSI Driver installation finished ==="
echo "You can now use StorageClasses with provisioner: nfs.csi.k8s.io"