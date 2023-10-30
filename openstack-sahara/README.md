# Hướng dẫn cài đặt OpenStack Sahara

Giả sử ta đã cài đặt thành công Openstack. Ở đây mình cài bản Openstack Juno trên Ubuntu server 14.04.

## Các bước cài đặt
#### Cài các gói cần thiết

    apt-get install python-pip python-setuptools python-virtualenv python-dev
    virtualenv sahara-venv
    sahara-venv/bin/pip install sahara
Hoặc bạn có thể chọn các phiên bản khác tại: http://tarballs.openstack.org/sahara/

Năm lệnh dưới đây để khắc phục một số lỗi khi cài Sahara mà mình gặp phải. Rất mong nhận được phản hồi từ mọi người về các lỗi khác tại: https://github.com/datts68/openstack-sahara/issues

    sudo apt-get install libmysqlclient-dev
    sahara-venv/bin/pip install mysql-python
    cd sahara-venv
    source bin/activate
    easy_install MySQL-python
#### Cấu hình
Tạo thư mục chứa file config và file log của sahara:

    mkdir /etc/sahara
    mkdir /var/log/sahara

Có 2 file config mẫu là: sahara.conf.sample-basic và sahara.conf.sample. Ở đây mình dùng file sahara.conf.sample-basic:
    
    cp sahara-venv/share/sahara/sahara.conf.sample-basic /etc/sahara/sahara.conf

    nano /etc/sahara/sahara.conf
    
        [DEFAULT]
        ...
        use_neutron = true
        ...
        verbose = true
        ...
        debug = true
        …
        log_dir=/var/log/sahara
        log_file=sahara.log
        ...
        [database]
        connection = mysql://sahara:$pass_db_sahara@controller/sahara
        
        [keystone_authtoken]
        auth_uri = http://controller:5000/v2.0
        ...
        identity_uri = http://controller:35357
        ...
        admin_user = $keystone_account_username
        admin_password = $keystone_account_password
        admin_tenant_name = $keystone_service_account_tenant_name

Ở đây ta sẽ dùng MySQL. Nếu bạn muốn sử dụng SQLite có thể thay thế như sau:

    [database]
    connection = sqlite:////tmp/sahara.db

Sửa file my.sql

    nano /etc/mysql/my.cnf
        [mysqld]
        max_allowed_packet = 256M

    service mysql restart

#### Tạo database
    mysql -u root -p
        CREATE DATABASE sahara;
        GRANT ALL PRIVILEGES ON sahara.* TO 'sahara'@'localhost' IDENTIFIED BY $pass_db_sahara;
        GRANT ALL PRIVILEGES ON sahara.* TO 'sahara'@'%' IDENTIFIED BY $pass_db_sahara;

#### Đồng bộ, khởi tạo
    sahara-venv/bin/sahara-db-manage --config-file /etc/sahara/sahara.conf upgrade head

#### Tạo keystone
    keystone service-create --name sahara --type data_processing --description "Data processing service"

    keystone endpoint-create --service-id $(keystone service-list | awk '/ sahara / {print $2}') --publicurl http://controller:8386/v1.1/%\(tenant_id\)s --internalurl http://controller:8386/v1.1/%\(tenant_id\)s --adminurl http://controller:8386/v1.1/%\(tenant_id\)s --region regionOne

#### Cài đặt Sahara UI
    pip install sahara-dashboard
    service apache2 restart

#### Khởi động Sahara:
    sahara-venv/bin/sahara-all --config-file /etc/sahara/sahara.conf

#### Kiểm tra
Vào horizon kiểm tra. Ta sẽ thấy có thêm 1 tab là "Data Processing". Xong!

![Install Sahara Ok](https://raw.githubusercontent.com/datts68/openstack-sahara/master/images/datts68_sahara_01.png)
