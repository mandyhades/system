## Hướng dẫn Zabbix 6
https://sbcode.net/zabbix/create-initial-database/
https://news.cloud365.vn/zabbix-giam-sat-windows-process/

## zabbix setup
- Cung cấp máy chủ Linux
- Tải xuống và cài đặt Kho lưu trữ Zabbix
- Cài đặt Zabbix Server, Frontend và Agent

## zabbix - mysql
- Tạo cơ sở dữ liệu ban đầu 
(Tôi cần cài đặt cơ sở dữ liệu MySQL: username-password)
(create database zabbix character set utf8mb4 collate utf8mb4_bin; )

## zabbix configure
- Đăng nhập và cấu hình giao diện người dùng máy chủ Zabbix
- Cấu hình tên miền cho Zabbix Server
- Định cấu hình SSL cho Giao diện người dùng máy chủ Zabbix  (congigure)

## Zabbix Agent
Zabbix Agent là một thành phần phần mềm được cài đặt trên các máy chủ hoặc thiết bị cần được giám sát trong hệ thống Zabbix. Nó chịu trách nhiệm thu thập thông tin về hiệu suất, tài nguyên hệ thống, và các chỉ số quan trọng khác từ máy chủ hoặc thiết bị và gửi thông tin này đến Zabbix Server hoặc Zabbix Proxy để được xử lý và phân tích.


- Cài đặt Zabbix Agent trên Ubuntu trên cùng mạng với máy chủ Zabbix ( zabbix client )
- Cài đặt Zabbix Agent (Hoạt động) trên Máy chủ Windows phía sau Tường lửa ( zabbix client )
- Cài đặt Zabbix Agent trên CentOS trên Nhà cung cấp đám mây khác ( zabbix client )

## zabbix proxy
 Zabbix Proxy là một thành phần quan trọng trong hệ thống giám sát Zabbix. Nó là một phần mềm độc lập được cài đặt tại các địa điểm vật lý hoặc mạng khác nhau để giảm tải và tối ưu hóa việc thu thập dữ liệu từ các thiết bị và máy chủ mục tiêu.
- Đăng ký tự động đại lý Zabbix
- Cài đặt và cấu hình proxy Zabbix
- Định cấu hình Tác nhân Zabbix trên Proxy Zabbix
- Cấu hình lại Zabbix Agent để sử dụng Zabbix Proxy
- Cài đặt Zabbix Agent trên macOS Behind Proxy



## zabbix monitoring
- Tình trạng máy chủ Zabbix
- Sức khỏe proxy Zabbix

## zabbix PSK Encryption (Mã hóa), uthentication (Xác thực)
 
- Kích hoạt mã hóa PSK cho đại lý Zabbix
- Kích hoạt mã hóa PSK cho Zabbix Proxy

## zabbix agent ping
- Tạo các mục máy chủ

## host triggers zabbix ( ping 120s ko duoc)
Một trigger bao gồm các thành phần chính sau:

Expression (Biểu thức): Đây là phần quan trọng nhất của trigger. Nó định nghĩa điều kiện mà trigger sẽ kiểm tra. Biểu thức này được xây dựng dựa trên các thông số hoặc mô-đun đã được cấu hình.
Severity (Mức độ nghiêm trọng): Xác định mức độ nghiêm trọng của trigger khi kích hoạt. Có thể là Information, Warning, Average, High, hoặc Disaster.
Comments (Bình luận): Được sử dụng để mô tả trigger và để làm rõ tại sao trigger được thiết lập.
Dependent Items (Các mục phụ thuộc): Các mục mà trigger phụ thuộc để xác định trạng thái của trigger.
-Tạo trình kích hoạt máy chủ
- Định cấu hình loại phương tiện email

## CPU 85% MAU DO , RAM 85% MAU DO
- Tạo đồ thị máy chủ
- Chuyển đổi các mục máy chủ, trình kích hoạt và đồ thị thành mẫu
- Trang tổng quan mẫu
## tạo dashboard tuỳ chỉnh cho từng máy chủ
- Bảng điều khiển giám sát

## tạo network map 
- Tạo bản đồ mạng

## đọc event viewer window - cảnh báo email khi có những log trùng lặp
- Đọc nhật ký sự kiện Windows

- Tiền xử lý mục với Regex
- Tiền xử lý mục bằng JavaScript
- Nhân bản mục để tạo mẫu Windows PCI DSS
- Nhập/Xuất mẫu

## slack - telegram - BOT CHAT - API KẾT NỐI

- Loại phương tiện Slack
- Loại phương tiện truyền thông Telegram
- Tùy chỉnh thông báo cảnh báo kích hoạt bằng macro
- Thêm lịch sử dung lượng ổ đĩa vào mẫu OS Linux
- Nguyên mẫu kích hoạt và kích hoạt trong phạm vi
Định cấu hình Trình kích hoạt 'Ok Tạo sự kiện' để giảm thiểu việc gõ cảnh báo

## Giám sát
Giám sát HTTP từ xa bằng Kịch bản Web
Giám sát API JSON với Tác nhân HTTP
Giám sát tệp nhật ký - Mã trạng thái HTTP Nginx Proxy
Mục phụ thuộc
Thực thi tệp Bat trên máy chủ Windows từ xa với Zabbix Agent
Thực thi Python Script trên máy chủ Linux từ xa với Zabbix Agent
Thực thi Shell Script bằng Zabbix Agent
Thông số người dùng
Tập lệnh quản trị
Thực thi tập lệnh PowerShell để kiểm tra cập nhật Windows
Mục được tính toán

## LLD ZABBIX
LLD (Low-Level Discovery) là một tính năng quan trọng trong Zabbix cho phép bạn tự động khám phá và giám sát các tài nguyên mạng một cách động. Quy tắc LLD tùy chỉnh trong Zabbix giúp bạn xác định cách khám phá và tự động thêm các tài nguyên vào giám sát.

Tạo quy tắc LLD tùy chỉnh - Phần 1
Tạo quy tắc LLD tùy chỉnh - Phần 2

## thiết bị mạng zabbix
SNMP (Simple Network Management Protocol) là một giao thức được sử dụng để quản lý và giám sát các thiết bị mạng và các hệ thống. SNMP được thiết kế để cung cấp một phương tiện tiêu chuẩn để lấy thông tin từ các thiết bị mạng, cấu hình chúng và giám sát hoạt động của chúng.

**
Định cấu hình máy chủ SNMP trong Zabbix
Truy vấn máy chủ SNMP bằng OID
Truy vấn máy chủ SNMP bằng MIB
Khám phá mạng và hành động để tự động định cấu hình thiết bị SNMP
Bẫy SNMP
Kích hoạt bẫy SNMP
Sử dụng lệnh Nhận Zabbix
Người gửi và người đánh bẫy Zabbix
Định cấu hình giám sát cơ sở dữ liệu MySQL
Người dùng, Nhóm & Vai trò
Plugin Grafana Zabbix
Nhà xuất khẩu nút Prometheus
Giới thiệu và ví dụ về API Zabbix
Công cụ kiểm tra API Zabbix
Ví dụ Python API Zabbix
Quyền của người dùng API Zabbix
Chạy các lệnh Docker với tập lệnh quản trị
Định cấu hình Khám phá LLD và Hành động để Tự động Định cấu hình Trình xuất nút Prometheus
Định cấu hình Máy chủ SNMPv3 trong Zabbix
Chuyển đổi mẫu SNMPWALK sang Zabbix
Giám sát JMX
Giám sát cơ sở dữ liệu PostgreSQL
Bảng tính IPTables

setup:
https://sbcode.net/zabbix/installing-zabbix-from-packages/
