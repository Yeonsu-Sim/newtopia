sudo apt update

curl -fsSL https://tailscale.com/install.sh | sh

sudo tailscale up --authkey=tskey-auth-kJv7aW1jNs11CNTRL-xWxkZ1jS9ULbrh5sgJxDULeSuFtWNxaP

sudo apt install nfs-common -y

curl -sfL https://get.k3s.io | INSTALL_K3S_SKIP_ENABLE=true K3S_URL=https://100.93.48.126:6443 K3S_TOKEN=K10e12ca2d59cc6ae4e331cb98b0ea9affa0b3cc95c3e64a487184ea7f3f6fe5604::server:ea40a0417fee978cc9825d7fb048750a K3S_NODE_LABEL="location=local" sh -

sudo systemctl start k3s-agent