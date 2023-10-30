#[K8S] Phần 2 - Cài đặt Kubernetes Cluster và Rancher
## Lời tựa

Chào các bạn, tiếp tục series K8S ở phần này mình sẽ hướng dẫn các bạn cài đặt một hệ thống Kubernetes Cluster với đầy đủ các thành phần giống như một hệ thống Product thường làm. Trong phạm vi lab của mình, mình sẽ xây dựng hệ thống K8S Cluster gồm 03 node Master, 03 node Worker và các hệ thống phụ trợ như Gitlab, Rancher, CICD (Jenkins) để phục vụ các bài lab liên quan tới CICD về sau.
## Giới thiệu

Môi trường cài đặt LAB của mình gồm 01 máy workstation cài Window server 2016, trên đó cài VMWare Workstation Pro và tạo ra các máy ảo VM chạy Centos7 để cài đặt K8S. Các VM sử dụng NAT interface để kết nối ra ngoài internet (phục vụ cài đặt).
![](image/2.png)

Chi tiết hơn, trên các Master Node mình sẽ cài **Keepalive** và **Haproxy** để nâng cao tính dự phòng và phân chia tải cho hệ thống (Đóng vai trò như một node **Load Balancer** ảo).
Trên server vtq-rancher, ngoài rancher để quản lý hệ thống K8S thì mình sẽ cài thêm **Harbor-Registry** là một **Private Docker Registry** nhằm lưu trữ các Docker Image trên local phục vụ deploy lên hệ thống K8S. Đồng thời node này cũng sẽ đóng vai trò **NFS-Server** để cung cấp phân vùng lưu trữ, sau này sẽ cài **NFS-StorageClass** cho K8S

Trên node vtq-cicd mình sẽ cài **Jenkins** (chạy trên OS) để sau này sẽ cài đặt và cấu hình các job **CICD** cho hệ thống. Đồng thời, node này cũng là node dùng để cài đặt **kubespray** để cài đặt k8s cluster, mình sẽ hướng dẫn chi tiết bên dưới. Trên node Gitlab sẽ cài gitlab để quản lý các source code project, nơi quản lý các code cho các ví dụ lab và là đầu vào trong luồng CICD.

Trên các VM này, ngoài một Disk để cài OS thì tạo thêm cho mỗi VM một phân vùng /data có dung lượng 20GB để sau này sẽ dùng để cài đặt longhorn storage. Riêng trên node vtq-rancher sẽ có thêm một phân vùng /data2 có dung lượng 40GB để vừa dùng cho NFS-Server (sau này sẽ dùng cho nfs-storageclass) vừa dùng lưu dữ liệu cho Harbor-Registry.

![](image/3.png)
![](image/4.png)

**Mô hình chi tiết hệ thống lab sẽ như sau:**

![](image/6.png)

## Yêu cầu về máy chủ

**Trong phạm vi bài LAB này mình chuẩn bị 09 máy chủ (là các VM chạy centos7), các máy chủ cần thực hiện các bước cấu hình ban đầu như sau:**

* Cài đặt Centos7, cài đặt network để thông kết nối với nhau và thông ra ngoài Internet. Mình đã cấu hình các VM này dùng mạng NAT ở dải IP 192.168.10.x và ra ngoài internet qua máy host (là máy chủ cài Window Server 2016)
* Các VM cần tắt swap (ngay từ khi cài đặt các bạn có thể bỏ qua Swap hoặc lỡ có cài rồi thì search gg cách tắt swap đi nhé)
* Cài đặt một số tham số cấu hình của OS liên quan tới security và port forwarding
* Tạo SSH Key và cấu hình kết nối từ node cicd (sẽ chạy kubespray) tới các node trong cụm k8s
* Cấu hình hostname cho các node trong cụm k8s

## Cấu hình tham số trên OS:

* Disable SELinux

> sudo setenforce 0
sudo sed -i --follow-symlinks 's/SELINUX=enforcing/SELINUX=disabled/g' /etc/sysconfig/selinux

* Tắt service firewallD

> sudo systemctl stop firewalld
sudo systemctl disable firewalld

* Cấu hình ip_forward

> sudo sysctl -w net.ipv4.ip_forward=1

## Tạo SSH key và cấu hình kết nối SSH

* Thực hiện gen ssh-key trên tất cả các node (Master Node/Worker Node và CICD-node) với tùy chọn mặc định
> ssh-keygen

* Thực hiện copy ssh-key từ CICD-node tới các node sẽ cài K8S (nhập mật khẩu của node đích khi được hỏi):

> ssh-keygen

* Cấu hình file hosts trên tất cả các node theo IP/Hostname:

> 192.168.10.11 viettq-master1
192.168.10.12 viettq-master2
192.168.10.13 viettq-master3
192.168.10.14 viettq-worker1
192.168.10.15 viettq-worker2
192.168.10.16 viettq-worker3
192.168.10.18 viettq-gitlab
192.168.10.19 viettq-rancher
192.168.10.20 viettq-cicd

## Cài đặt Kubernetes Cluster bằng Kubespray

Tới đây bạn đã có đủ 6 VM sẵn sàng cho việc cài đặt cụm K8S Cluster với 03 node Master và 03 Worker. Mình sẽ hướng dẫn các bạn cài đặt K8S Cluster bằng Kubespray vì cách này là đơn giản và ít phải thao tác nhất.
Kubespray là một phần mềm opensource hỗ trợ việc cài đặt cụm k8s cluster trên rất nhiều nền tảng khác nhau như ubuntu, redhat hay centos.
Ý tưởng là sẽ cài kubespray một lên máy chủ, tạm gọi là Installation Server (trong bài lab này mình chọn node vtq-cicd). Từ đó tất cả những gì bạn cần làm là cho Kubespray biết bạn muốn cài một cụm K8S với bao nhiêu node master, bao nhiêu worker, cài etcd trên bao nhiêu node, thông tin kết nối của các node là gì. Việc còn lại đã có kubespray lo

### Nói nhiều quá giờ bắt tay vào cài đặt thôi!!!

Việc cài đặt, cấu hình sẽ chủ yếu thực hiện trên Installation Server (vtq-cicd). Các bạn cần tạo thư mục cài đặt trước:

>  mkdir kubernetes_installation/

Vào thư mục cài đặt vào download Kubespray về, lưu ý cần down đúng phiên bản bạn cần. Ở đây mình muốn cài kubernetes version v1.20.7 thì sẽ cần down kuberpay phiên bản release-2.16:

> git clone https://github.com/kubernetes-sigs/kubespray.git --branch release-2.16

Lúc này kubespray sẽ được tải về máy tại thư mục /home/sysadmin/kubernetes_installation/kubespray. Bạn cần tạo một inventory mới của riêng bạn từ bộ mẫu của kubespray:

> cd /home/sysadmin/kubernetes_installation/kubespray
cp -rf inventory/sample inventory/viettq-cluster

Tiếp theo là bước quan trọng nhất - cấu hình file host.yaml trong thư mục inventory của bạn

> cd /home/sysadmin/kubernetes_installation/kubespray/
cd inventory/viettq-cluster
vi host.yaml

Bạn cần chỉnh sửa nội dung file này theo đúng hostname/IP của các node bạn sẽ cài Kubernetes, ví dụ trong bài lab này mình sẽ thực hiện như sau:

> [all]
viettq-master1  ansible_host=192.168.10.11      ip=192.168.10.11
viettq-master2  ansible_host=192.168.10.12      ip=192.168.10.12
viettq-master3  ansible_host=192.168.10.13      ip=192.168.10.13
viettq-worker1  ansible_host=192.168.10.14      ip=192.168.10.14
viettq-worker2  ansible_host=192.168.10.15      ip=192.168.10.15
viettq-worker3  ansible_host=192.168.10.16      ip=192.168.10.16
[kube-master]
viettq-master1
viettq-master2
viettq-master3
[kube-node]
viettq-worker1
viettq-worker2
viettq-worker3
[etcd]
viettq-master1
viettq-master2
viettq-master3
[k8s-cluster:children]
kube-node
kube-master
[calico-rr]
[vault]
viettq-master1
viettq-master2
viettq-master3
viettq-worker1
viettq-worker2
viettq-worker3

Trong thẻ **[all]** là nơi khai báo thông tin chi tiết của tất cả các node gồm tên và IP. **[kube-master]** là các node sẽ chạy với role master, **[kube-node]** là các node chạy role worker, **[etcd]** là các node sẽ chạy etcd, thường chọn là các node master luôn dù không bắt buộc.

Tiếp đến nếu bạn muốn đổi CNI (network plugin của K8S) thì sửa file config sau:

> inventory/viettq-cluster/group_vars/k8s_cluster/k8s-cluster.yml

Sửa tham số:

> Từ
kube_network_plugin: calico
Thành
kube_network_plugin: flannel

Rồi, sắp xong rồi bạn ơi. Bình thường tới đây sẽ chạy lệnh ansible trên node Installation Server (vtq-cicd) để cài đặt cụm K8S lên, nhưng chắc nhiều bạn làm theo cách này sẽ gặp khá nhiều vấn đề rắc rối liên quan tới việc cài đặt cấu hình ansible, python, pip... K**hông sao, quên nó đi**!!!

Mình sẽ hướng dẫn các bạn chạy ansible trong docker-container, trong đó đã đóng gói sẵn toàn bộ các package cần thiết nên đảm bảo chạy thành công 100% trên mọi môi trường miễn có cài docker 😄 Vậy bạn cần cài docker lên node này đã:
> sudo yum update
curl -fsSL https://get.docker.com/ | sh

Rồi cấu hình để user của bạn (non-root) có thể chạy lệnh docker không cần sudo, sau đó restart lại session kết nối vào server để user được cập nhật:
 > sudo usermod -aG docker sysadmin
 
Giờ thì tạo một docker-container từ kubespray image, sau đó chúng ta sẽ thực hiện cài đặt k8s cluster từ bên trong container này:

> docker run --rm -it --mount type=bind,source=/home/sysadmin/kubernetes_installation/kubespray/inventory/viettq-cluster,dst=/inventory \
  --mount type=bind,source=/home/sysadmin/.ssh/id_rsa,dst=/root/.ssh/id_rsa \
  --mount type=bind,source=/home/sysadmin/.ssh/id_rsa,dst=/home/sysadmin/.ssh/id_rsa \
  quay.io/kubespray/kubespray:v2.16.0 bash 

*Lưu ý tham số "source=/home/sysadmin/kubernetesinstallation/kubespray/inventory/viettq-cluster" cần trỏ đúng tới thư mục inventory mình đã tạo ra và cấu hình ở bên trên. Các file key cũng được tạo từ bước generate ssh-key đã hướng dẫn bên trên. *
**Sau khi chạy lệnh trên, thì chúng ta đã exec vào bên trong container đó rồi, các bạn lưu ý dấu nhắc bây giờ sẽ là "root@b2dfac52ad07:/#"**

## Okela, giờ thì chỉ còn 1 lệnh này nữa thôi:

> ansible-playbook -i /inventory/hosts.yaml cluster.yml --user=sysadmin --ask-pass --become --ask-become-pass

Bạn sẽ nhập thông tin password vào nữa và chờ đợi để ansible chạy các task cài đặt lên các node cho bạn. Bước này thì nhanh chậm sẽ phụ thuộc nhiều vào internet của bạn có nhanh hay không 😄

**Kết quả hoàn thành sẽ như sau:**

```
PLAY RECAP *****************************************************************************************************************************************************************************
localhost                  : ok=3    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0
viettq-master1             : ok=480  changed=106  unreachable=0    failed=0    skipped=1017 rescued=0    ignored=1
viettq-master2             : ok=448  changed=98   unreachable=0    failed=0    skipped=944  rescued=0    ignored=0
viettq-master3             : ok=450  changed=99   unreachable=0    failed=0    skipped=942  rescued=0    ignored=0
viettq-worker1             : ok=342  changed=73   unreachable=0    failed=0    skipped=647  rescued=0    ignored=0
viettq-worker2             : ok=320  changed=70   unreachable=0    failed=0    skipped=568  rescued=0    ignored=0
viettq-worker3             : ok=320  changed=70   unreachable=0    failed=0    skipped=568  rescued=0    ignored=0

Wednesday 06 April 2022  09:22:13 +0000 (0:00:00.089)       0:48:05.297 *******
===============================================================================
kubernetes/preinstall : Install packages requirements ------------------------------------------------------------------------------------------------------------------------ 2043.59s
download_file | Download item -------------------------------------------------------------------------------------------------------------------------------------------------- 99.24s
kubernetes/control-plane : Joining control plane node to the cluster. ---------------------------------------------------------------------------------------------------------- 41.14s
download_file | Download item -------------------------------------------------------------------------------------------------------------------------------------------------- 35.98s
kubernetes/kubeadm : Join to cluster ------------------------------------------------------------------------------------------------------------------------------------------- 30.79s
download_file | Download item -------------------------------------------------------------------------------------------------------------------------------------------------- 27.33s
kubernetes/control-plane : kubeadm | Initialize first master ------------------------------------------------------------------------------------------------------------------- 24.87s
download_container | Download image if required -------------------------------------------------------------------------------------------------------------------------------- 24.69s
download_container | Download image if required -------------------------------------------------------------------------------------------------------------------------------- 24.61s
download_file | Download item -------------------------------------------------------------------------------------------------------------------------------------------------- 23.56s
download_container | Download image if required -------------------------------------------------------------------------------------------------------------------------------- 23.05s
download_file | Download item -------------------------------------------------------------------------------------------------------------------------------------------------- 20.04s
container-engine/docker : ensure service is started if docker packages are already present ------------------------------------------------------------------------------------- 17.01s
download_container | Download image if required -------------------------------------------------------------------------------------------------------------------------------- 12.00s
download : check_pull_required |  Generate a list of information about the images on a node ------------------------------------------------------------------------------------ 11.73s
prep_download | Register docker images info ------------------------------------------------------------------------------------------------------------------------------------ 10.67s
reload etcd -------------------------------------------------------------------------------------------------------------------------------------------------------------------- 10.42s
download_container | Download image if required --------------------------------------------------------------------------------------------------------------------------------- 8.40s
download : check_pull_required |  Generate a list of information about the images on a node ------------------------------------------------------------------------------------- 7.67s
download_container | Download image if required --------------------------------------------------------------------------------------------------------------------------------- 7.43s
```

### Hola, chúc mừng bạn đã cài đặt thành công Kubernetes Cluster
Tiếp theo ta cần cấu hình kubectl trên các master node
> mkdir -p $HOME/.kube
sudo cp /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config

Giờ thì kiểm tra thành quả thôi, lưu ý sau khi cài đặt thì kubectl chỉ có trên master node thôi nhé:

> kubectl get nodes -o wide
NAME             STATUS   ROLES                  AGE   VERSION   INTERNAL-IP     EXTERNAL-IP   OS-IMAGE                KERNEL-VERSION                CONTAINER-RUNTIME
viettq-master1   Ready    control-plane,master   17h   v1.20.7   192.168.10.11   <none>        CentOS Linux 7 (Core)   3.10.0-1160.45.1.el7.x86_64   docker://20.10.10
viettq-master2   Ready    control-plane,master   17h   v1.20.7   192.168.10.12   <none>        CentOS Linux 7 (Core)   3.10.0-1160.45.1.el7.x86_64   docker://20.10.10
viettq-master3   Ready    control-plane,master   17h   v1.20.7   192.168.10.13   <none>        CentOS Linux 7 (Core)   3.10.0-1160.45.1.el7.x86_64   docker://20.10.10
viettq-worker1   Ready    <none>                 17h   v1.20.7   192.168.10.14   <none>        CentOS Linux 7 (Core)   3.10.0-1160.45.1.el7.x86_64   docker://20.10.10
viettq-worker2   Ready    <none>                 17h   v1.20.7   192.168.10.15   <none>        CentOS Linux 7 (Core)   3.10.0-1160.45.1.el7.x86_64   docker://20.10.10
viettq-worker3   Ready    <none>                 17h   v1.20.7   192.168.10.16   <none>        CentOS Linux 7 (Core)   3.10.0-1160.45.1.el7.x86_64   docker://20.10.10

Kubernetes Cluster đã cài xong rồi, giờ cài thêm quả Rancher nữa để nhìn trên giao diện cho nó pro chút, nãy giờ toàn commandline chưa có gì để khoe với chúng bạn cả 😆😆

## Cài đặt Rancher để quản lý cụm K8S

Giờ ta chuyển sang node rancher (vtq-rancher) để cài rancher lên nhé. Nó phải gọi là cực kỳ dễ, 5 phút là xong thôi. Đầu tiên chạy rancher-server lên (bản chất là một Docker Container):

> docker run --name rancher-server -d --restart=unless-stopped -p 6860:80 -p 6868:443 --privileged rancher/rancher:v2.5.7 

*À có một lưu ý ở đây cho các bạn là giữa Rancher và Kubernetes có bảng tương thích. Ví dụ bạn cài Kubernetes v1.20.7 thì cần cài Rancher v2.5.7. Các bạn có thể check trên trang chủ của rancher nhé!
*

À có một lưu ý ở đây cho các bạn là giữa Rancher và Kubernetes có bảng tương thích. Ví dụ bạn cài Kubernetes v1.20.7 thì cần cài Rancher v2.5.7. Các bạn có thể check trên trang chủ của rancher nhé!

Giờ thì rancher container đã chạy xong, ta vào web của nó ở địa chỉ https://<ip>:<https-port> cụ thể là https://192.168.10.19:6868 Lúc này bạn chọn đổi password mới để nhập password mới và ấn OK để ra giao diện chính của Rancher như sau:

![](image/7.png)

Tiếp theo chọn vào **Add Cluster** --> **Other Cluster **--> Nhập **Cluster Name** --> Chọn **Create**
Tiếp theo copy dòng lệnh dưới cùng để chạy trên K8S Master Node để cài đặt rancher-agent lên K8S:

> curl --insecure -sfL https://192.168.10.19:6868/v3/import/d6mqd55wnz7vh8ltfg4xvgnfhhmdvmdzxs5m6b24znl5chwjgd977q_c-p4rh9.yaml |kubectl apply -f -

**Chờ cho việc cài đặt Agent hoàn thành bạn check kết quả trên giao diện của Rancher:
**

![](8.png)

Vào tiếp giao diện Cluster Explorer:
![](9.png)

Bổ sung thêm: Khi trên giao diện Rancher báo Controller Manager unhealthy thì các bạn thực hiện thêm các bước sau trên các node Master nhé:

```
sudo sed -i 's|- --port=0|#- --port=0|' /etc/kubernetes/manifests/kube-scheduler.yaml
sudo sed -i 's|- --port=0|#- --port=0|' /etc/kubernetes/manifests/kube-controller-manager.yaml
sudo systemctl restart kubelet
```

**Tuyệt vời, tới đây là có ảnh đẹp chụp up facebook khoe chúng bạn rồi đó! Giờ ngồi tận hưởng, vọc vạch hệ thống chán chê trước khi tiếp tục phần tiếp theo nhé!
**