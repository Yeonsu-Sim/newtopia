# Newtopia Kubernetes Cluster

k3s 기반 온프레미스 Kubernetes 클러스터 구성 및 워크로드 정의

## Architecture

### Namespace Structure
- `newtopia-app` - Application workloads (Backend, Frontend, Crawler, Pipeline)
- `newtopia-db` - Database layer (PostgreSQL, Redis)
- `newtopia-kafka` - Kafka messaging cluster
- `newtopia-minio` - Object storage service
- `gitlab-runner` - CI/CD pipeline runner
- `platform` - Infrastructure components (Ingress, Cert-manager)
- `observability` - Monitoring and logging stack

### Storage Architecture
- NFS-based persistent volumes with CSI driver
- Dedicated storage paths per service
- Dynamic volume provisioning support

## Directory Structure

```
kubernetes/
├── namespace/          # Namespace definitions
├── storage/           # StorageClass, PV, PVC configurations
├── middleware/        # Application workload definitions
├── gitlab/           # CI/CD GitLab Runner setup
├── secret/           # Secret templates and examples
├── nfs/              # NFS CSI driver setup and documentation
└── etc/              # Installation scripts and utilities
```

## Quick Start

### 1. Setup Infrastructure
```bash
# Apply namespaces
kubectl apply -f namespace/

# Setup NFS storage
./nfs/install-nfs-csi.sh
kubectl apply -f storage/

# Create secrets (modify examples first)
cp secret/*-example.yaml secret/
# Edit secret files with actual values
kubectl apply -f secret/
```

### 2. Deploy Middleware
```bash
# Deploy databases
kubectl apply -f middleware/postgres-workload.yaml
kubectl apply -f middleware/redis-workload.yaml

# Deploy Kafka cluster
./etc/strimzi-manifest.sh
kubectl apply -f middleware/kafka-workload.yaml

# Deploy MinIO
kubectl apply -f middleware/minio-workload.yaml
```

### 3. Setup CI/CD
```bash
kubectl apply -f gitlab/runner.yaml
```

## Services and Ports

| Service | Namespace | Internal Port | Description |
|---------|-----------|---------------|-------------|
| PostgreSQL | newtopia-db | 5432 | Primary database |
| Redis | newtopia-db | 6379 | Cache and session store |
| Kafka | newtopia-kafka | 9092 | Message broker |
| MinIO API | newtopia-minio | 9000 | Object storage API |
| MinIO Console | newtopia-minio | 9001 | Management interface |

## Storage Configuration

- **PostgreSQL**: 5Gi NFS volume at `/nfs/postgres-data`
- **Redis**: 2Gi NFS volume at `/nfs/redis-data`
- **Kafka**: 20Gi per replica at `/nfs/kafka-data`
- **MinIO**: 50Gi NFS volume at `/nfs/minio-data`

## Documentation

- `master_setup_nfs.md` - NFS server setup guide
- `worker_node_nfs_guideline.md` - Worker node configuration
- `nfs/README.md` - NFS CSI driver documentation