#!/bin/bash -ex

## Khai bao bien
INTERFACE1=ens33
INTERFACE2=ens37

IP1=$2
IP2=$3

IP_GATEWAY=192.168.42.2
IP_NETMASK=24
IP_DNS=8.8.8.8

if [ $# -ne 3 ]
    then
        echo -e "Nhap du 3 thong so: \e[38;5;82m HOSTNAME IP_NIC1 IP_NIC2 \e[0m"
        echo ""
        echo -e "Vi du:\e[101mbash $0 ctl1 192.168.20.33 10.10.0.33 \e[0m"
        exit 1;
fi

echo "Cau hinh IP va hostname"
sleep 3

hostnamectl set-hostname $1
sudo systemctl restart NetworkManager

nmcli con modify $INTERFACE1 ipv4.addresses $IP1/$IP_NETMASK
nmcli con modify $INTERFACE1 ipv4.gateway $IP_GATEWAY
nmcli con modify $INTERFACE1 ipv4.dns $IP_DNS
nmcli con modify $INTERFACE1 ipv4.method manual
nmcli con modify $INTERFACE1 connection.autoconnect yes

nmcli con modify $INTERFACE2 ipv4.addresses $IP2/$IP_NETMASK
nmcli con modify $INTERFACE2 ipv4.method manual
nmcli con modify $INTERFACE2 connection.autoconnect yes


sed -i 's/SELINUX=enforcing/SELINUX=disabled/g' /etc/sysconfig/selinux
sed -i 's/SELINUX=enforcing/SELINUX=disabled/g' /etc/selinux/config
sudo systemctl disable firewalld
sudo systemctl stop firewalld
sudo systemctl stop NetworkManager
sudo systemctl disable NetworkManager
sudo systemctl enable network
sudo systemctl start network
init 6
