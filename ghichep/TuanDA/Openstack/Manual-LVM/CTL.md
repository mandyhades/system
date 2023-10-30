# Tài liệu cài đặt OpenStack trên Controller
---
## 1. Chuẩn bị
### Cấu hình IP

- vlan mgnt : eno1: 172.16.4.240
- vlan data-vm : ens1f1: 10.10.12.240
- vlan provider: eno2: 10.10.11.240
Neu cai dat tren ao hoa ESXi  phai emable mode ao hoa.

```sh
hostnamectl set-hostname controller
echo "nameserver 8.8.8.8" > /etc/resolv.conf
echo "Setup IP  ens160"
nmcli c modify ens160 ipv4.addresses 172.16.4.240/20
nmcli c modify ens160 ipv4.gateway 172.16.10.1
nmcli c modify ens160 ipv4.dns 8.8.8.8
nmcli c modify ens160 ipv4.method manual
nmcli con mod ens160 connection.autoconnect yes

echo "Setup IP  ens192"
nmcli c modify ens192 ipv4.addresses 10.10.11.240/24
nmcli c modify ens192 ipv4.method manual
nmcli con mod ens192 connection.autoconnect yes
 
echo "Setup IP  ens224"
nmcli c modify ens224 ipv4.addresses 10.10.12.240/24
nmcli c modify ens224 ipv4.method manual
nmcli con mod ens224 connection.autoconnect yes
```

Disable firewalld:

```sh
sudo systemctl disable firewalld
sudo systemctl stop firewalld
sudo systemctl disable NetworkManager
sudo systemctl stop NetworkManager
sudo systemctl enable network
sudo systemctl start network
```
Update:
```sh
yum install -y epel-release
sudo yum install -y byobu nano
byobu
```

Set hostname

```sh
hostnamectl set-hostname controller
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
```
 
Kiểm tra mode sysctl 
```
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

### Khai báo repo mariadb và update

```sh
echo '[mariadb]
name = MariaDB
baseurl = http://yum.mariadb.org/10.2/centos7-amd64
gpgkey=https://yum.mariadb.org/RPM-GPG-KEY-MariaDB
gpgcheck=1' >> /etc/yum.repos.d/MariaDB.repo
```

**Lưu ý khi sử dụng lệnh `yum -y update` sẽ update Kernel và CentOS-release**

### Khai báo file hosts

```sh
echo "172.16.4.240 controller" >> /etc/hosts
echo "172.16.4.241 compute01" >> /etc/hosts
```

### Tạo SSH key
```sh
ssh-keygen -t rsa -f /root/.ssh/id_rsa -q -P ""

ssh-copy-id -o StrictHostKeyChecking=no -i /root/.ssh/id_rsa.pub root@controller
ssh-copy-id -o StrictHostKeyChecking=no -i /root/.ssh/id_rsa.pub root@compute01

ssh-copy-id -o StrictHostKeyChecking=no -i /root/.ssh/id_rsa.pub root@compute02

ssh-copy-id root@compute01
scp /root/.ssh/id_rsa root@compute01:/root/.ssh/ (thu muc phai tao truoc)
scp /root/.ssh/id_rsa root@compute02:/root/.ssh/
```

### Cài đặt các gói cần thiết

```sh
yum -y install centos-release-openstack-queens
yum -y install crudini wget vim
yum -y install python-openstackclient openstack-selinux python2-PyMySQL
```

## 2. Cấu hình NTP

```sh
yum -y install chrony
sed -i 's/server 0.centos.pool.ntp.org iburst/ \
server 1.vn.pool.ntp.org iburst \
server 0.asia.pool.ntp.org iburst \
server 3.asia.pool.ntp.org iburst/g' /etc/chrony.conf
sed -i 's/server 1.centos.pool.ntp.org iburst/#/g' /etc/chrony.conf
sed -i 's/server 2.centos.pool.ntp.org iburst/#/g' /etc/chrony.conf
sed -i 's/server 3.centos.pool.ntp.org iburst/#/g' /etc/chrony.conf
sed -i 's/#allow 192.168.0.0\/16/allow 172.16.4.0\/24/g' /etc/chrony.conf
```

Restart service ntp
```sh
systemctl enable chronyd.service
systemctl start chronyd.service
chronyc sources
```

## 3. Cài đặt và cấu hình memcache

```sh
yum install -y memcached
sed -i "s/-l 127.0.0.1,::1/-l 172.16.4.240/g" /etc/sysconfig/memcached
 
systemctl enable memcached.service
systemctl restart memcached.service
```

## 4. Cài đặt và cấu hình MySQL galera

```sh
yum install -y mariadb mariadb-server python2-PyMySQL
```

### Cấu hình galera 

- Backup cấu hình mysql

```sh 
cp /etc/my.cnf.d/server.cnf /etc/my.cnf.d/server.cnf.orig
rm -rf /etc/my.cnf.d/server.cnf
```

- Cấu hình mysql cho OpenStack
 
```sh
cat << EOF > /etc/my.cnf.d/openstack.cnf
[mysqld]
bind-address = 172.16.4.240
default-storage-engine = innodb
innodb_file_per_table
max_connections = 4096
collation-server = utf8_general_ci
character-set-server = utf8
EOF
```

- Restart service
 
```sh
systemctl enable mariadb.service
systemctl restart mariadb.service
```

 - Đặt lại password cho user mysql
```
mysql_secure_installation
```

## 5. Cài đặt và cấu hình RabbitMQ 

```sh
yum -y install rabbitmq-server
```
 - Cấu hình rabbitmq 
 
```sh
systemctl enable rabbitmq-server.service
systemctl start rabbitmq-server.service
rabbitmq-plugins enable rabbitmq_management
systemctl restart rabbitmq-server
curl -O http://localhost:15672/cli/rabbitmqadmin
chmod a+x rabbitmqadmin
mv rabbitmqadmin /usr/sbin/
rabbitmqadmin list users
```

 - Cấu hình trên node CTL
 
```sh
rabbitmqctl add_user openstack Welcome123
rabbitmqctl set_permissions openstack ".*" ".*" ".*"
rabbitmqctl set_user_tags openstack administrator
```

## 6. Cài đặt các thành phần OpenStack

### 6.1. Keystone

 - Tạo db
 
```sh
mysql -u root -p0435626533a@
CREATE DATABASE keystone;
GRANT ALL PRIVILEGES ON keystone.* TO 'keystone'@'localhost' IDENTIFIED BY 'Welcome123';
GRANT ALL PRIVILEGES ON keystone.* TO 'keystone'@'%' IDENTIFIED BY 'Welcome123';
exit
```

 - Cài packages

```sh
yum install openstack-keystone httpd mod_wsgi -y
```

 - Cấu hình bind port

```sh
cp /usr/share/keystone/wsgi-keystone.conf /etc/httpd/conf.d/
sed -i -e 's/VirtualHost \*/VirtualHost 172.16.4.240/g' /etc/httpd/conf.d/wsgi-keystone.conf
sed -i -e 's/Listen 5000/Listen 172.16.4.240:5000/g' /etc/httpd/conf.d/wsgi-keystone.conf
sed -i -e 's/Listen 35357/Listen 172.16.4.240:35357/g' /etc/httpd/conf.d/wsgi-keystone.conf
sed -i -e 's/^Listen.*/Listen 172.16.4.240:80/g' /etc/httpd/conf/httpd.conf
```

 - Cấu hình keystone
 
```sh
cp /etc/keystone/keystone.conf /etc/keystone/keystone.conf.org
rm -rf /etc/keystone/keystone.conf

cat << EOF >> /etc/keystone/keystone.conf
[DEFAULT]
[assignment]
[auth]
[cache]
[catalog]
[cors]
[credential]
[database]
connection = mysql+pymysql://keystone:Welcome123@172.16.4.240/keystone
[domain_config]
[endpoint_filter]
[endpoint_policy]
[eventlet_server]
[federation]
[fernet_tokens]
[healthcheck]
[identity]
[identity_mapping]
[ldap]
[matchmaker_redis]
[memcache]
[oauth1]
[oslo_messaging_amqp]
[oslo_messaging_kafka]
[oslo_messaging_notifications]
[oslo_messaging_rabbit]
[oslo_messaging_zmq]
[oslo_middleware]
[oslo_policy]
[paste_deploy]
[policy]
[profiler]
[resource]
[revoke]
[role]
[saml]
[security_compliance]
[shadow_users]
[signing]
[token]
provider = fernet
[tokenless_auth]
[trust]
EOF
```

 - Phân quyền file cấu hình

```sh
chown root:keystone /etc/keystone/keystone.conf
```
 - Sync db

```sh
su -s /bin/sh -c "keystone-manage db_sync" keystone
```

 - Set up fernet key

```sh
keystone-manage fernet_setup --keystone-user keystone --keystone-group keystone
keystone-manage credential_setup --keystone-user keystone --keystone-group keystone
```

 -  Bootstrap keystone

```sh
keystone-manage bootstrap --bootstrap-password Welcome123 \
  --bootstrap-admin-url http://172.16.4.240:5000/v3/ \
  --bootstrap-internal-url http://172.16.4.240:5000/v3/ \
  --bootstrap-public-url http://172.16.4.240:5000/v3/ \
  --bootstrap-region-id RegionOne
```

 - Enable và start httpd
 
```sh
systemctl enable httpd.service
systemctl restart httpd.service
```

 - Export biến môi trường
 
```sh
export OS_USERNAME=admin
export OS_PASSWORD=Welcome123
export OS_PROJECT_NAME=admin
export OS_USER_DOMAIN_NAME=Default
export OS_PROJECT_DOMAIN_NAME=Default
export OS_AUTH_URL=http://172.16.4.240:35357/v3
export OS_IDENTITY_API_VERSION=3
```

 - Tạo domain 

```sh
openstack domain create --description "An Example Domain" example
openstack project create --domain default --description "Service Project" service
```

 - Tạo project và user
 
```sh
openstack project create --domain default  --description "Demo Project" demo
openstack user create --domain default --password Welcome123 demo
```

 - Tạo role và gắn role

```sh
openstack role create user
openstack role add --project demo --user demo user
```

 - Unset 2 biến môi trường
 
```sh
unset OS_AUTH_URL OS_PASSWORD
```

 - Tạo token

```sh
openstack --os-auth-url http://172.16.4.240:35357/v3 \
  --os-project-domain-name Default --os-user-domain-name Default \
  --os-project-name admin --os-username admin token issue
```

 - Tạo file xác thực 

```sh
cat << EOF >> admin-openrc
export OS_PROJECT_DOMAIN_NAME=Default
export OS_USER_DOMAIN_NAME=Default
export OS_PROJECT_NAME=admin
export OS_USERNAME=admin
export OS_PASSWORD=Welcome123
export OS_AUTH_URL=http://172.16.4.240:5000/v3
export OS_IDENTITY_API_VERSION=3
export OS_IMAGE_API_VERSION=2
EOF

cat << EOF >> demo-openrc
export OS_PROJECT_DOMAIN_NAME=Default
export OS_USER_DOMAIN_NAME=Default
export OS_PROJECT_NAME=demo
export OS_USERNAME=demo
export OS_PASSWORD=Welcome123
export OS_AUTH_URL=http://172.16.4.240:5000/v3
export OS_IDENTITY_API_VERSION=3
export OS_IMAGE_API_VERSION=2
EOF
```

 - Kiểm tra cấu hình keystone

```sh
 . admin-openrc
openstack token issue
```

Sau khi thực hiện câu lệnh hiện ra bảng token là OK


### 6.2. Cài đặt và cấu hình glance 

 - Tạo db

```sh
mysql -u root -p0435626533a@
CREATE DATABASE glance;
GRANT ALL PRIVILEGES ON glance.* TO 'glance'@'localhost' IDENTIFIED BY 'Welcome123';
GRANT ALL PRIVILEGES ON glance.* TO 'glance'@'%' IDENTIFIED BY 'Welcome123';
exit
```

 - Tạo user
 
```sh
openstack user create --domain default --password Welcome123 glance
openstack role add --project service --user glance admin
openstack service create --name glance --description "OpenStack Image" image
```

 - Tạo endpoint

```sh
openstack endpoint create --region RegionOne image public http://172.16.4.240:9292
openstack endpoint create --region RegionOne image admin http://172.16.4.240:9292
openstack endpoint create --region RegionOne image internal http://172.16.4.240:9292
```

 - Cài packages
 
```sh
yum install -y openstack-glance
```

 - Cấu hình glance api

```sh
cp /etc/glance/glance-api.conf /etc/glance/glance-api.conf.org 
rm -rf /etc/glance/glance-api.conf

cat << EOF >> /etc/glance/glance-api.conf
[DEFAULT]
bind_host = 172.16.4.240
registry_host = 172.16.4.240
[cors]
[database]
connection = mysql+pymysql://glance:Welcome123@172.16.4.240/glance
[glance_store]
stores = file,http
default_store = file
filesystem_store_datadir = /var/lib/glance/images/
[image_format]
[keystone_authtoken]
auth_uri = http://172.16.4.240:5000
auth_url = http://172.16.4.240:5000
memcached_servers = 172.16.4.240:11211
auth_type = password
project_domain_name = Default
user_domain_name = Default
project_name = service
username = glance
password = Welcome123
[matchmaker_redis]
[oslo_concurrency]
[oslo_messaging_amqp]
[oslo_messaging_kafka]
[oslo_messaging_notifications]
[oslo_messaging_rabbit]
[oslo_messaging_zmq]
[oslo_middleware]
[oslo_policy]
[paste_deploy]
flavor = keystone
[profiler]
[store_type_location_strategy]
[task]
[taskflow_executor]
EOF
```

 - Cấu hình glance registry

```sh
cp /etc/glance/glance-registry.conf /etc/glance/glance-registry.conf.org
rm -rf /etc/glance/glance-registry.conf

cat << EOF >> /etc/glance/glance-registry.conf
[DEFAULT]
bind_host = 172.16.4.240
[database]
connection = mysql+pymysql://glance:Welcome123@172.16.4.240/glance
[keystone_authtoken]
auth_uri = http://172.16.4.240:5000
auth_url = http://172.16.4.240:5000
memcached_servers = 172.16.4.240
auth_type = password
project_domain_name = Default
user_domain_name = Default
project_name = service
username = glance
password = Welcome123
[matchmaker_redis]
[oslo_messaging_amqp]
[oslo_messaging_kafka]
[oslo_messaging_notifications]
[oslo_messaging_rabbit]
[oslo_messaging_zmq]
[oslo_policy]
[paste_deploy]
flavor = keystone
[profiler]
EOF
```

 - Phân quyền file cấu hình
 
```sh
chown root:glance /etc/glance/glance-api.conf
chown root:glance /etc/glance/glance-registry.conf
```

 - Sync db

```sh
su -s /bin/sh -c "glance-manage db_sync" glance
```

 - Enable và start dịch vụ 

```sh
systemctl enable openstack-glance-api.service openstack-glance-registry.service
systemctl start openstack-glance-api.service openstack-glance-registry.service
```

 - Download và tạo image
 
```sh
wget http://download.cirros-cloud.net/0.3.5/cirros-0.3.5-x86_64-disk.img
openstack image create "cirros" \
  --file cirros-0.3.5-x86_64-disk.img \
  --disk-format qcow2 --container-format bare \
  --public
```

**Lưu ý**: Sau khi tạo images, mặc định image sẽ được đưa vào thư mục /var/lib/glance/images

### 6.3. Cài đặt và cấu hình nova

 - Tạo database nova
 
```sh
mysql -u root -p0435626533a@
CREATE DATABASE nova_api;
CREATE DATABASE nova;
CREATE DATABASE nova_cell0;
GRANT ALL PRIVILEGES ON nova_api.* TO 'nova'@'localhost' IDENTIFIED BY 'Welcome123';
GRANT ALL PRIVILEGES ON nova_api.* TO 'nova'@'%' IDENTIFIED BY 'Welcome123';
GRANT ALL PRIVILEGES ON nova.* TO 'nova'@'localhost' IDENTIFIED BY 'Welcome123';
GRANT ALL PRIVILEGES ON nova.* TO 'nova'@'%' IDENTIFIED BY 'Welcome123';
GRANT ALL PRIVILEGES ON nova_cell0.* TO 'nova'@'localhost' IDENTIFIED BY 'Welcome123';
GRANT ALL PRIVILEGES ON nova_cell0.* TO 'nova'@'%' IDENTIFIED BY 'Welcome123';
exit
```

 - Tạo user và endpoint

```sh
openstack user create --domain default --password Welcome123 nova
openstack role add --project service --user nova admin
openstack service create --name nova --description "OpenStack Compute" compute
```

```sh
openstack endpoint create --region RegionOne compute public http://172.16.4.240:8774/v2.1
openstack endpoint create --region RegionOne compute admin http://172.16.4.240:8774/v2.1
openstack endpoint create --region RegionOne compute internal http://172.16.4.240:8774/v2.1
```

```sh
openstack user create --domain default --password Welcome123 placement
openstack role add --project service --user placement admin
openstack service create --name placement --description "Placement API" placement
```

```sh
openstack endpoint create --region RegionOne placement public http://172.16.4.240:8778
openstack endpoint create --region RegionOne placement admin http://172.16.4.240:8778
openstack endpoint create --region RegionOne placement internal http://172.16.4.240:8778
```

 - Tải packages
```sh
yum install -y openstack-nova-api openstack-nova-conductor openstack-nova-console openstack-nova-novncproxy openstack-nova-scheduler openstack-nova-placement-api
```

 - Cấu hình nova
```sh
cp /etc/nova/nova.conf /etc/nova/nova.conf.org 
rm -rf /etc/nova/nova.conf

cat << EOF >> /etc/nova/nova.conf
[DEFAULT]
my_ip = 172.16.4.240
enabled_apis = osapi_compute,metadata
use_neutron = True
osapi_compute_listen=172.16.4.240
metadata_host=172.16.4.240
metadata_listen=172.16.4.240
metadata_listen_port=8775
firewall_driver = nova.virt.firewall.NoopFirewallDriver
transport_url = rabbit://openstack:Welcome123@172.16.4.240:5672
[api]
auth_strategy = keystone
[api_database]
connection = mysql+pymysql://nova:Welcome123@172.16.4.240/nova_api
[barbican]
[cache]
backend = oslo_cache.memcache_pool
enabled = true
memcache_servers = 172.16.4.240:11211
[cells]
[cinder]
[compute]
[conductor]
[console]
[consoleauth]
[cors]
[crypto]
[database]
connection = mysql+pymysql://nova:Welcome123@172.16.4.240/nova
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
[matchmaker_redis]
[metrics]
[mks]
[neutron]
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
novncproxy_host=172.16.4.240
enabled = true
vncserver_listen = 172.16.4.240
vncserver_proxyclient_address = 172.16.4.240
novncproxy_base_url = http://172.16.4.240:6080/vnc_auto.html
[workarounds]
[wsgi]
[xenserver]
[xvp]
EOF
```

 - Thêm vào file 00-nova-placement-api.conf 

```sh
cat << 'EOF' >> /etc/httpd/conf.d/00-nova-placement-api.conf
<Directory /usr/bin>
   <IfVersion >= 2.4>
      Require all granted
   </IfVersion>
   <IfVersion < 2.4>
      Order allow,deny
      Allow from all
   </IfVersion>
</Directory>
EOF
```

 - Cấu hình bind port cho nova-placement
```sh
sed -i -e 's/VirtualHost \*/VirtualHost 172.16.4.240/g' /etc/httpd/conf.d/00-nova-placement-api.conf
sed -i -e 's/Listen 8778/Listen 172.16.4.240:8778/g' /etc/httpd/conf.d/00-nova-placement-api.conf
```

 - Restart httpd
```sh
systemctl restart httpd
```
 - Sync db
```sh
su -s /bin/sh -c "nova-manage api_db sync" nova
su -s /bin/sh -c "nova-manage cell_v2 map_cell0" nova
su -s /bin/sh -c "nova-manage cell_v2 create_cell --name=cell1 --verbose" nova
su -s /bin/sh -c "nova-manage db sync" nova
```

 - Enable và start service

```sh
systemctl enable openstack-nova-api.service openstack-nova-scheduler.service openstack-nova-consoleauth.service openstack-nova-conductor.service openstack-nova-novncproxy.service
systemctl start openstack-nova-api.service openstack-nova-scheduler.service openstack-nova-consoleauth.service openstack-nova-conductor.service openstack-nova-novncproxy.service
```

 - Kiểm tra lại dịch vụ

```sh
openstack compute service list
```

### 6.4. Cài đặt và cấu hình neutron

 - Tạo database neutron
 
```sh
mysql -u root -p0435626533a@
CREATE DATABASE neutron;
GRANT ALL PRIVILEGES ON neutron.* TO 'neutron'@'localhost' IDENTIFIED BY 'Welcome123';
GRANT ALL PRIVILEGES ON neutron.* TO 'neutron'@'%' IDENTIFIED BY 'Welcome123';
exit
```

 - Tạo user, endpoint trên 1 node

```sh
openstack user create --domain default --password Welcome123 neutron
openstack role add --project service --user neutron admin
openstack service create --name neutron --description "OpenStack Networking" network
```

```sh
openstack endpoint create --region RegionOne network public http://172.16.4.240:9696
openstack endpoint create --region RegionOne network internal http://172.16.4.240:9696
openstack endpoint create --region RegionOne network admin http://172.16.4.240:9696
```

 - Cài packages

```sh
yum install openstack-neutron openstack-neutron-ml2 openstack-neutron-linuxbridge ebtables -y
```

 - Cấu hình neutron
 
**Lưu ý**: Mô hình này sử dụng mô hình mạng provider (flat) sử dụng linuxbridge
DHCP agent và metadata agent được chạy trên node compute

```sh
cp /etc/neutron/neutron.conf /etc/neutron/neutron.conf.org
rm -rf /etc/neutron/neutron.conf
  
cat << EOF >> /etc/neutron/neutron.conf
[DEFAULT]
bind_host = 172.16.4.240
core_plugin = ml2
service_plugins = router
transport_url = rabbit://openstack:Welcome123@172.16.4.240:5672
auth_strategy = keystone
notify_nova_on_port_status_changes = true
notify_nova_on_port_data_changes = true
allow_overlapping_ips = True
dhcp_agents_per_network = 2
[agent]
[cors]
[database]
connection = mysql+pymysql://neutron:Welcome123@172.16.4.240/neutron
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
auth_url = http://172.16.4.240:35357
auth_type = password
project_domain_name = default
user_domain_name = default
region_name = RegionOne
project_name = service
username = nova
password = Welcome123
[oslo_concurrency]
lock_path = /var/lib/neutron/tmp
[oslo_messaging_amqp]
[oslo_messaging_kafka]
[oslo_messaging_notifications]
[oslo_messaging_rabbit]
rabbit_retry_interval = 1
rabbit_retry_backoff = 2
amqp_durable_queues = true
rabbit_ha_queues = true
[oslo_messaging_zmq]
[oslo_middleware]
[oslo_policy]
[quotas]
[ssl]
EOF
```

 - Cấu hình file ml2

```sh
cp /etc/neutron/plugins/ml2/ml2_conf.ini /etc/neutron/plugins/ml2/ml2_conf.ini.org
rm -rf /etc/neutron/plugins/ml2/ml2_conf.ini

cat << EOF >> /etc/neutron/plugins/ml2/ml2_conf.ini
[DEFAULT]
[l2pop]
[ml2]
type_drivers = flat,vlan,vxlan
tenant_network_types = vxlan
mechanism_drivers = linuxbridge,l2population
extension_drivers = port_security
[ml2_type_flat]
[ml2_type_geneve]
[ml2_type_gre]
[ml2_type_vlan]
network_vlan_ranges = provider
[ml2_type_vxlan]
vni_ranges = 1:1000
[securitygroup]
enable_ipset = True
EOF
```

 - Cấu hình file LB agent
 
```sh
cp /etc/neutron/plugins/ml2/linuxbridge_agent.ini /etc/neutron/plugins/ml2/linuxbridge_agent.ini.org 
rm -rf /etc/neutron/plugins/ml2/linuxbridge_agent.ini

# Can thay doi interface provider cho phu hop
cat << EOF >> /etc/neutron/plugins/ml2/linuxbridge_agent.ini
[DEFAULT]
[agent]
[linux_bridge]
physical_interface_mappings = provider:ens160
[network_log]
[securitygroup]
enable_security_group = true
firewall_driver = neutron.agent.linux.iptables_firewall.IptablesFirewallDriver
[vxlan]
enable_vxlan = true
local_ip = 172.16.4.240
l2_population = true
EOF
```

 - Cấu hình trên file l3 agent

```sh
cp /etc/neutron/l3_agent.ini /etc/neutron/l3_agent.ini.org
rm -rf /etc/neutron/l3_agent.ini

cat << EOF >> /etc/neutron/l3_agent.ini
[DEFAULT]
interface_driver = neutron.agent.linux.interface.BridgeInterfaceDriver
[agent]
[ovs]
EOF
```

 - Chỉnh sửa file /etc/nova/nova.conf 
 
```sh
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
service_metadata_proxy = true
metadata_proxy_shared_secret = Welcome123
```

 - Restart lại dv nova-api
```sh
systemctl restart openstack-nova-api.service
```
 - Phân quyền file cấu hình
```sh
chown -R root:neutron /etc/neutron/
```
 - Tạo liên kết
```sh
ln -s /etc/neutron/plugins/ml2/ml2_conf.ini /etc/neutron/plugin.ini
```
 - Sync db (bỏ qua các cảnh báo Warning)
```sh
su -s /bin/sh -c "neutron-db-manage --config-file /etc/neutron/neutron.conf --config-file /etc/neutron/plugins/ml2/ml2_conf.ini upgrade head" neutron
```
 - Enable và start dịch vụ 
```sh
systemctl restart openstack-nova-api.service openstack-nova-scheduler.service openstack-nova-consoleauth.service openstack-nova-conductor.service openstack-nova-novncproxy.service
systemctl enable neutron-server.service neutron-linuxbridge-agent.service neutron-l3-agent.service
systemctl start neutron-server.service neutron-linuxbridge-agent.service neutron-l3-agent.service
```

### 6.5. Cài đặt và cấu hình horizon

 - Tải packages

```sh
yum install openstack-dashboard -y
```

 - Tạo file direct
```sh 
filehtml=/var/www/html/index.html
touch $filehtml
cat << EOF >> $filehtml
<html>
<head>
<META HTTP-EQUIV="Refresh" Content="0.5; URL=http://172.16.4.240/dashboard">
</head>
<body>
<center> <h1>Redirecting to OpenStack Dashboard</h1> </center>
</body>
</html>
EOF
```

 - Backup cấu hình
```sh
cp /etc/openstack-dashboard/local_settings /etc/openstack-dashboard/local_settings.org
```

 - Thay đổi cấu hình trong file /etc/openstack-dashboard/local_settings
 ```sh
ALLOWED_HOSTS = ['*',]
OPENSTACK_API_VERSIONS = {
    "identity": 3,
    "image": 2,
    "volume": 2,
}
OPENSTACK_KEYSTONE_MULTIDOMAIN_SUPPORT = True
OPENSTACK_KEYSTONE_DEFAULT_DOMAIN = 'Default'
```

 - Lưu ý thêm SESSION_ENGINE vào trên dòng CACHE như bên dưới
```sh
SESSION_ENGINE = 'django.contrib.sessions.backends.cache'
CACHES = {
    'default': {
         'BACKEND':'django.core.cache.backends.memcached.MemcachedCache',
         'LOCATION': ['172.16.4.240:11211',],
    }
}
OPENSTACK_HOST = "172.16.4.240"
OPENSTACK_KEYSTONE_URL = "http://172.16.4.240:5000/v3"
OPENSTACK_KEYSTONE_DEFAULT_ROLE = "user"
```

 - Lưu ý: Nếu chỉ sử dụng provider, chỉnh sửa các thông số sau
```sh
OPENSTACK_NEUTRON_NETWORK = {
    'enable_router': False,
    'enable_quotas': False,
    'enable_ipv6': False,
    'enable_distributed_router': False,
    'enable_ha_router': False,
    'enable_fip_topology_check': False,
}
```
```
TIME_ZONE = "Asia/Ho_Chi_Minh"
```

 -  Thêm vào file /etc/httpd/conf.d/openstack-dashboard.conf
```sh
echo "WSGIApplicationGroup %{GLOBAL}" >> /etc/httpd/conf.d/openstack-dashboard.conf
```

 - Restart lại httpd
```
systemctl restart httpd.service memcached.service
```

### 6.6. Cài đặt và cấu hình Cinder

 - Tạo database cinder
 
```sh
mysql -u root -p0435626533a@
CREATE DATABASE cinder;
GRANT ALL PRIVILEGES ON cinder.* TO 'cinder'@'localhost' \
  IDENTIFIED BY 'Welcome123';
GRANT ALL PRIVILEGES ON cinder.* TO 'cinder'@'%' \
  IDENTIFIED BY 'Welcome123';  
exit
```

 - Tạo service, user và endpoint
```sh
openstack user create --domain default --password Welcome123 cinder
openstack role add --project service --user cinder admin
openstack service create --name cinderv2 --description "OpenStack Block Storage" volumev2
openstack service create --name cinderv3 --description "OpenStack Block Storage" volumev3

openstack endpoint create --region RegionOne volumev2 public http://172.16.4.240:8776/v2/%\(project_id\)s
openstack endpoint create --region RegionOne volumev2 internal http://172.16.4.240:8776/v2/%\(project_id\)s
openstack endpoint create --region RegionOne volumev2 admin http://172.16.4.240:8776/v2/%\(project_id\)s
openstack endpoint create --region RegionOne volumev3 public http://172.16.4.240:8776/v3/%\(project_id\)s
openstack endpoint create --region RegionOne volumev3 internal http://172.16.4.240:8776/v3/%\(project_id\)s
openstack endpoint create --region RegionOne volumev3 admin http://172.16.4.240:8776/v3/%\(project_id\)s
```

 - Tải package
```sh
yum install openstack-cinder targetcli python-keystone -y
```

 - Cài đặt và cấu hình cinder volume với LVM
```sh
yum install -y lvm2
```

 - Khởi động dịch vụ LVM và cho phép khởi động cùng hệ thống.
 
```sh
systemctl enable lvm2-lvmetad.service
systemctl start lvm2-lvmetad.service
```

 - Tạo LVM physical volume /dev/sdb
```sh
pvcreate /dev/sdb
```

 - Tạo LVM volume group `cinder-volumes`
```sh
vgcreate cinder-volumes /dev/sdb
```

 - Sửa file `/etc/lvm/lvm.conf`, để LVM chỉ scan ổ sdb cho block storage

devices {

```
filter = [ "a/sdb/", "r/.*/"]
```

 - Sửa cấu hình cinder
```sh 
cp /etc/cinder/cinder.conf /etc/cinder/cinder.conf.bak 
rm -rf /etc/cinder/cinder.conf

cat << EOF >> /etc/cinder/cinder.conf
[DEFAULT]
my_ip = 172.16.4.240
transport_url = rabbit://openstack:Welcome123@172.16.4.240:5672
auth_strategy = keystone
osapi_volume_listen = 172.16.4.240
enabled_backends = lvm
[backend]
[backend_defaults]
[barbican]
[brcd_fabric_example]
[cisco_fabric_example]
[coordination]
[cors]
[database]
connection = mysql+pymysql://cinder:Welcome123@172.16.4.240/cinder
[fc-zone-manager]
[healthcheck]
[key_manager]
[keystone_authtoken]
auth_uri = http://172.16.4.240:5000
auth_url = http://172.16.4.240:35357
memcached_servers = 172.16.4.240:11211
auth_type = password
project_domain_id = default
user_domain_id = default
project_name = service
username = cinder
password = Welcome123
[matchmaker_redis]
[nova]
[oslo_concurrency]
lock_path = /var/lib/cinder/tmp
[oslo_messaging_amqp]
[oslo_messaging_kafka]
[oslo_messaging_notifications]
[oslo_messaging_rabbit]
rabbit_retry_interval = 1
rabbit_retry_backoff = 2
amqp_durable_queues = true
rabbit_ha_queues = true
[oslo_messaging_zmq]
[oslo_middleware]
[oslo_policy]
[oslo_reports]
[oslo_versionedobjects]
[profiler]
[service_user]
[ssl]
[vault]
[lvm]
volume_driver = cinder.volume.drivers.lvm.LVMVolumeDriver
volume_group = cinder-volumes
iscsi_protocol = iscsi
iscsi_helper = lioadm
volume_backend_name = lvm
EOF
```

 - Phân quyền file cấu hình
```sh
chown root:cinder /etc/cinder/cinder.conf
```

 - Sync db
```sh
su -s /bin/sh -c "cinder-manage db sync" cinder
```

 - Chỉnh sửa file /etc/nova/nova.conf
```sh
[cinder]
os_region_name = RegionOne
```

 - Restart dịch vụ nova api 
```sh
systemctl restart openstack-nova-api.service
```

 - Enable va start dịch vụ
```sh
systemctl enable openstack-cinder-api.service openstack-cinder-volume.service openstack-cinder-scheduler.service
systemctl restart openstack-cinder-api.service openstack-cinder-volume.service openstack-cinder-scheduler.service
```