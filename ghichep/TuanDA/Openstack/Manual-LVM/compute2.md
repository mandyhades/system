# Tài liệu cài đặt OpenStack trên COMPUTE 1
---
## 1. Chuẩn bị
### Cấu hình IP

- vlan mgnt : eno1: 172.16.4.242
- vlan data-vm : ens1f1: 10.10.12.242
- vlan provider: eno2: 10.10.11.242

**Neu cai dat tren ao hoa ESXi  phai emable mode ao hoa.
grep -E '(vmx|svm)' /proc/cpuinfo**

**Đối với cài đặt trên VMware ESXi phải chỉnh sửa cat /etc/nova/nova.conf dòng config virt_type = qemu**

#cai dat offfline

### Set hostname
```sh
hostnamectl set-hostname compute02

echo "nameserver 8.8.8.8" > /etc/resolv.conf
echo "Setup IP  ens160"
nmcli c modify ens160 ipv4.addresses 172.16.4.242/20
nmcli c modify ens160 ipv4.gateway 172.16.10.1
nmcli c modify ens160 ipv4.dns 8.8.8.8
nmcli c modify ens160 ipv4.method manual
nmcli con mod ens160 connection.autoconnect yes
 
echo "Setup IP  ens192"
nmcli c modify ens192 ipv4.addresses 10.10.11.242/24
nmcli c modify ens192 ipv4.method manual
nmcli con mod ens192 connection.autoconnect yes
 
echo "Setup IP  ens224"
nmcli c modify ens224 ipv4.addresses 10.10.12.242/24
nmcli c modify ens224 ipv4.method manual
nmcli con mod ens224 connection.autoconnect yes
```
```
sudo systemctl disable firewalld
sudo systemctl stop firewalld
sudo systemctl disable NetworkManager
sudo systemctl stop NetworkManager
sudo systemctl enable network
sudo systemctl restart network
```
```
sed -i 's/SELINUX=enforcing/SELINUX=disabled/g' /etc/sysconfig/selinux
sed -i 's/SELINUX=enforcing/SELINUX=disabled/g' /etc/selinux/config
```
```
yum install -y epel-release
sudo yum install -y byobu
byobu
```

### Cấu hình các mode sysctl
```sh
echo 'net.ipv4.conf.all.arp_ignore = 1'  >> /etc/sysctl.conf
echo 'net.ipv4.conf.all.arp_announce = 2'  >> /etc/sysctl.conf
echo 'net.ipv4.conf.all.rp_filter = 2'  >> /etc/sysctl.conf
echo 'net.netfilter.nf_conntrack_tcp_be_liberal = 1'  >> /etc/sysctl.conf

cat << EOF >> /etc/sysctl.conf
net.ipv4.ip_nonlocal_bind = 1
net.ipv4.tcp_keepalive_time = 6
net.ipv4.tcp_keepalive_intvl = 3
net.ipv4.tcp_keepalive_probes = 6
net.ipv4.ip_forward = 1
net.ipv4.conf.all.rp_filter = 0
net.ipv4.conf.default.rp_filter = 0
EOF

sysctl -p
```

### Stop firewalld và selinux
```sh
sed -i 's/SELINUX=enforcing/SELINUX=disabled/g' /etc/selinux/config
sed -i 's/SELINUX=enforcing/SELINUX=disabled/g' /etc/sysconfig/selinux
systemctl disable firewalld
systemctl stop firewalld
systemctl disable NetworkManager
systemctl stop NetworkManager
```

### Khai bao file hosts
**Thực hiện với việc cài đặt offline**
```sh
echo "172.16.4.240 controller" >> /etc/hosts
echo "172.16.4.241 compute01" >> /etc/hosts
echo "172.16.4.242 compute02" >> /etc/hosts

yum localinstall -y /root/packages/compute/01-ops/*.rpm
yum localinstall -y /root/packages/compute/02-extra/*.rpm
yum localinstall -y /root/packages/compute/03-python-ops/*.rpm
yum localinstall -y /root/packages/compute/04-chrony/*.rpm
yum localinstall -y /root/packages/compute/05-nova/*.rpm
yum localinstall -y /root/packages/compute/06-neutron/*.rpm
```

# Cai dat cac goi can thiet 
#yum -y install centos-release-openstack-queens
#yum -y install crudini wget vim
#yum -y install python-openstackclient openstack-selinux python2-PyMySQL

## Cau hinh chrony
```sh
#yum -y install chrony
VIP_MGNT_IP='172.16.4.240'
sed -i '/server/d' /etc/chrony.conf
echo "server $VIP_MGNT_IP iburst" >> /etc/chrony.conf
systemctl enable chronyd.service
systemctl restart chronyd.service
chronyc sources
```

#Chinh sua file /etc/yum.repos.d/CentOS-QEMU-EV.repo

#sed -i 's|baseurl=http:\/\/mirror.centos.org\/$contentdir\/$releasever\/virt\/$basearch\/kvm-common\/|baseurl=http:\/\/mirror.centos.org\/centos\/7\/virt\/x86_64\/kvm-common\/|g' /etc/yum.repos.d/CentOS-QEMU-EV.repo

 # Cai nova
#yum install openstack-nova-compute libvirt-client -y

#Cau hinh nova
```sh
cp /etc/nova/nova.conf  /etc/nova/nova.conf.org
rm -rf /etc/nova/nova.conf

cat << EOF >> /etc/nova/nova.conf 
[DEFAULT]
enabled_apis = osapi_compute,metadata
transport_url = rabbit://openstack:Welcome123@172.16.4.240:5672
use_neutron = True
firewall_driver = nova.virt.firewall.NoopFirewallDriver
[api]
auth_strategy = keystone
[api_database]
[barbican]
[cache]
[cells]
[cinder]
[compute]
[conductor]
[console]
[consoleauth]
[cors]
[crypto]
[database]
[devices]
[ephemeral_storage_encryption]
[filter_scheduler]
[glance]
api_servers = http://172.16.4.240:9292
[guestfs]
[healthcheck]
[hyperv]
[ironic]
[key_manager]
[keystone]
[keystone_authtoken]
auth_url = http://172.16.4.240:5000/v3
memcached_servers = 172.16.4.240:11211
auth_type = password
project_domain_name = default
user_domain_name = default
project_name = service
username = nova
password = Welcome123
[libvirt]
virt_type = kvm (sua thanh qemu neu cai tren ESXi)
[matchmaker_redis]
[metrics]
[mks]
[neutron]
url = http://172.16.4.240:9696
auth_url = http://172.16.4.240:35357
auth_type = password
project_domain_name = default
user_domain_name = default
region_name = RegionOne
project_name = service
username = neutron
password = Welcome123
[notifications]
[osapi_v21]
[oslo_concurrency]
lock_path = /var/lib/nova/tmp
[oslo_messaging_amqp]
[oslo_messaging_kafka]
[oslo_messaging_notifications]
[oslo_messaging_rabbit]
rabbit_ha_queues = true
rabbit_retry_interval = 1
rabbit_retry_backoff = 2
amqp_durable_queues= true
[oslo_messaging_zmq]
[oslo_middleware]
[oslo_policy]
[pci]
[placement]
os_region_name = RegionOne
project_domain_name = Default
project_name = service
auth_type = password
user_domain_name = Default
auth_url = http://172.16.4.240:5000/v3
username = placement
password = Welcome123
[quota]
[rdp]
[remote_debug]
[scheduler]
discover_hosts_in_cells_interval = 300
[serial_console]
[service_user]
[spice]
[upgrade_levels]
[vault]
[vendordata_dynamic_auth]
[vmware]
[vnc]
enabled = True
server_listen = 0.0.0.0
server_proxyclient_address = 172.16.4.242
novncproxy_base_url = http://172.16.4.240:6080/vnc_auto.html
[workarounds]
[wsgi]
[xenserver]
[xvp]
EOF
```

# Phan quyen
```
chown root:nova /etc/nova/nova.conf
```

# Enable + start
```
systemctl enable libvirtd.service openstack-nova-compute.service
```

# Cai neutron

#yum install openstack-neutron openstack-neutron-ml2 openstack-neutron-linuxbridge ebtables -y


#Cau hinh neutron
```
cp /etc/neutron/neutron.conf /etc/neutron/neutron.conf.org 
rm -rf /etc/neutron/neutron.conf

cat << EOF >> /etc/neutron/neutron.conf
[DEFAULT]
transport_url = rabbit://openstack:Welcome123@172.16.4.240:5672
auth_strategy = keystone
[agent]
[cors]
[database]
[keystone_authtoken]
auth_uri = http://172.16.4.240:5000
auth_url = http://172.16.4.240:35357
memcached_servers = 172.16.4.240:11211
auth_type = password
project_domain_name = default
user_domain_name = default
project_name = service
username = neutron
password = Welcome123
[matchmaker_redis]
[nova]
[oslo_concurrency]
lock_path = /var/lib/neutron/tmp
[oslo_messaging_amqp]
[oslo_messaging_kafka]
[oslo_messaging_notifications]
[oslo_messaging_rabbit]
rabbit_ha_queues = true
rabbit_retry_interval = 1
rabbit_retry_backoff = 2
amqp_durable_queues= true
[oslo_messaging_zmq]
[oslo_middleware]
[oslo_policy]
[quotas]
[ssl]
EOF
```

### Cau hinh file LB agent
```
cp /etc/neutron/plugins/ml2/linuxbridge_agent.ini /etc/neutron/plugins/ml2/linuxbridge_agent.ini.org 
rm -rf /etc/neutron/plugins/ml2/linuxbridge_agent.ini

cat << EOF >> /etc/neutron/plugins/ml2/linuxbridge_agent.ini
[DEFAULT]
[agent]
[linux_bridge]
physical_interface_mappings = provider:ens192
[network_log]
[securitygroup]
enable_security_group = true
firewall_driver = neutron.agent.linux.iptables_firewall.IptablesFirewallDriver
[vxlan]
enable_vxlan = true
local_ip = 172.16.4.242
l2_population = true
EOF
```

### Cấu hình dhcp agent
```
cp /etc/neutron/dhcp_agent.ini /etc/neutron/dhcp_agent.ini.org
rm -rf /etc/neutron/dhcp_agent.ini

cat << EOF >> /etc/neutron/dhcp_agent.ini
[DEFAULT]
interface_driver = linuxbridge
dhcp_driver = neutron.agent.linux.dhcp.Dnsmasq
enable_isolated_metadata = true
force_metadata = True
[agent]
[ovs]
EOF
```

### Cấu hình metadata agent
```
cp /etc/neutron/metadata_agent.ini /etc/neutron/metadata_agent.ini.org 
rm -rf /etc/neutron/metadata_agent.ini

cat << EOF >> /etc/neutron/metadata_agent.ini
[DEFAULT]
nova_metadata_host = 172.16.4.240
metadata_proxy_shared_secret = Welcome123
[agent]
[cache]
EOF
```

### Phân quyền 
```
chown root:neutron /etc/neutron/metadata_agent.ini /etc/neutron/neutron.conf /etc/neutron/dhcp_agent.ini /etc/neutron/plugins/ml2/linuxbridge_agent.ini
```


- Restart dich vu nova
```
systemctl restart libvirtd.service openstack-nova-compute
```

- Enable va start 
```
systemctl enable neutron-linuxbridge-agent.service neutron-dhcp-agent.service neutron-metadata-agent.service
systemctl restart neutron-linuxbridge-agent.service neutron-dhcp-agent.service neutron-metadata-agent.service
```

- Truy cập dashboard ở địa chỉ http://172.16.4.240 và tạo máy ảo

`openstack network agent list` -> phai up

`openstack compute service list `-> ra compute (luu y sua kvm thanh qemu vs esxi)
