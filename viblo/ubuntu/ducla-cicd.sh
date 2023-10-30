mkdir kubernetes_installation/
git clone https://github.com/kubernetes-sigs/kubespray.git --branch release-2.16
cd /home/sysadmin/kubernetes_installation/kubespray cp -rf inventory/sample inventory/ducla-cluster
cd /home/sysadmin/kubernetes_installation/kubespray/ cd inventory/ducla-cluster vi host.yaml
