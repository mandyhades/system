## Prometheus
https://funix.udemy.com/course/prometheus/learn/lecture/16871790#overview

Giám sát Kubernetes với Prometheus và Grafana

## Sử dụng các công cụ giám sát :Prometheus, Grafana

## Sử dụng Công cụ Phân tích Nhật ký
Phân tích thông điệp nhật ký do  dịch vụ của bạn tạo ra và xác định các vấn đề . ELK (Elaticsearch, Logstash, Kibana)

## Thiết lập Cảnh báo và Thông báo
Thiết lập cảnh báo và thông báo có thể giúp đảm bảo rằng các bên liên quan được thông báo khi các vấn đề phát sinh, cho phép chúng được giải quyết nhanh chóng. Slack,Teams, Email, SMS.



##  Automatically discovers scraping targets in Kubernetes

● Tự động phát hiện các scraping targets trong Kubernetes
● Làm việc với các label Kubernetes cho các truy vấn linh hoạt
● Khả năng quan sát toàn diện của các dịch vụ vi mô trong Kubernetes
● Tích hợp với Grafana để hiển thị và Alertmanager để thông báo



Cài đặt Prometheus

Trỏ Một Tên Miền

Proxy đảo ngược Prometheus với Nginx

Thêm SSL vào Prometheus Reverse Proxy

Thêm Xác thực Cơ bản

## Scrape Target , label k8s
```
global:
  scrape_interval: 15s  # Tần suất scrape dữ liệu (15 giây)

scrape_configs:
  - job_name: 'example-target'
    static_configs:
      - targets:
        - 'example.com:9100'  # Địa chỉ và cổng của target

  - job_name: 'another-target'
    static_configs:
      - targets:
        - 'another-example.com:9100'
```

bạn cần "scrape" (cào dữ liệu) từ các "targets" (đích) mà bạn muốn giám sát. Scrape trong ngữ cảnh này đề cập đến việc thu thập dữ liệu từ các endpoints HTTP hoặc các dịch vụ khác để sử dụng dữ liệu này trong việc giám sát.

Dưới đây là một số khái niệm quan trọng khi bạn muốn scrape một target trong Prometheus:

*Targets* (Đích): Đây là các endpoint HTTP hoặc dịch vụ mà bạn muốn thu thập thông tin từ đó. Điều này có thể là ứng dụng, máy chủ, hoặc bất kỳ dịch vụ nào có thể cung cấp thông tin về hiệu suất hoặc trạng thái.

*Scraping Configuration* (Cấu hình Scrape): Đây là cấu hình được định nghĩa trong Prometheus để chỉ định các targets mà bạn muốn scrape. Cấu hình này bao gồm thông tin về URL, các quy tắc giám sát, tần suất scrape, và các thông số khác.

*HTTP Endpoints* (Điểm kết thúc HTTP): Đây là các URL mà bạn cần scrape dữ liệu từ. Prometheus sẽ gửi các yêu cầu HTTP đến các điểm kết thúc này để lấy thông tin.

*Metrics (Chỉ số)*: Đây là các thông tin về hiệu suất và trạng thái của target mà bạn muốn giám sát. Metrics thường được xuất bằng cách sử dụng một ngôn ngữ đặc biệt như Prometheus Metrics Exporter.

Để scrape target trong Prometheus, bạn cần cấu hình file cấu hình Prometheus để xác định các targets bạn muốn giám sát và cách scrape dữ liệu từ chúng. Sau đó, Prometheus sẽ tự động scrape dữ liệu từ các targets này theo tần suất được thiết lập trong cấu hình. Dữ liệu này sau đó có thể được sử dụng để tạo các biểu đồ, cảnh báo, và phân tích hiệu suất.


Install an External Node Exporter

Deleting a Time Series

PromQL Example Queries

## Recording Rules (YML) - thiết lập rule ghi log
`cd /etc/prometheus`
`sudo nano prometheus_rules.yml`
```
groups:
  - name: custom_rules
    rules:
      - record: node_memory_MemFree_percent
        expr: 100 - (100 * node_memory_MemFree_bytes / node_memory_MemTotal_bytes)
        
```	
`promtool check rules prometheus_rules.yml`
```
sudo service prometheus restart
sudo service prometheus status
```
Làm mới giao diện người dùng Prometheus và kiểm tra trình đơn thả xuống
## Alerting Rules (YML) - thiết lập rule cảnh báo
```
  - name: alert_rules
    rules:
      - alert: InstanceDown
        expr: up == 0
        for: 1m
        labels:
            severity: critical
        annotations:
            summary: 'Instance {{ $labels.instance }} down'
            description: '{{ $labels.instance }} of job {{ $labels.job }} has been down for more than 1 minute.'
```
Lưu nó và kiểm tra nó vớipromtool


`./promtool check rules prometheus_rules.yml`

Nếu mọi thứ đều ổn, hãy khởi động lại Prometheus.

```
sudo service prometheus restart
sudo service prometheus status
```

Tiếp theo chúng tôi tạo một quy tắc khác sử dụng một trong các quy tắc Ghi âm mà chúng tôi đã tạo trong bài học trước, node_filesystem_free_percent

```
      - alert: DiskSpaceFree10Percent
        expr: node_filesystem_free_percent <= 10
        labels:
            severity: warning
        annotations:
            summary: 'Instance {{ $labels.instance }} has 10% or less Free disk space'
            description: '{{ $labels.instance }} has only {{ $value }}% or less free.'
```




Install Prometheus Alertmanager
Configure Prometheus Alertmanager ( canh bao qua email SMTP)

Install Grafana

## Thiết lập Prometheus Datasource:
Prometheus Datasource là một cấu hình hoặc kết nối được thiết lập trong một hệ thống quản lý quan trọng như một bảng điều khiển giám sát (monitoring dashboard) hoặc một công cụ phân tích dữ liệu để liên kết và lấy dữ liệu từ một máy chủ Prometheus. 

Khi bạn thiết lập một Prometheus Datasource, bạn có thể truy vấn dữ liệu từ Prometheus Server và sử dụng nó để tạo biểu đồ, cảnh báo, phân tích hiệu suất, và thực hiện nhiều công việc quan trọng khác liên quan đến giám sát hệ thống.

Dưới đây là các bước cơ bản để thiết lập một Prometheus Datasource trong một công cụ quản lý quan trọng như Grafana:

Mở trình quản lý quan trọng (ví dụ: Grafana): Đầu tiên, mở trình quản lý quan trọng mà bạn muốn kết nối với Prometheus.

Thêm Datasource: Trong giao diện của công cụ quản lý quan trọng (ví dụ: Grafana), tìm mục để thêm Datasource và chọn Prometheus làm loại Datasource.

Cấu hình Datasource: Điền thông tin cấu hình cho Prometheus Datasource. Thông thường, bạn sẽ cần cung cấp địa chỉ của Prometheus Server (ví dụ: http://your-prometheus-server:9090).

Lưu và Kiểm tra kết nối: Sau khi cấu hình, lưu thông tin Datasource và kiểm tra kết nối với Prometheus Server để đảm bảo rằng kết nối được thiết lập thành công.

Sử dụng Datasource: Bây giờ bạn có thể sử dụng Prometheus Datasource để truy vấn dữ liệu từ Prometheus Server và tạo biểu đồ, cảnh báo, và phân tích hiệu suất trong công cụ quản lý quan trọng của mình.

Khi Datasource đã được thiết lập và kết nối thành công với Prometheus Server, bạn có thể truy vấn và tận dụng dữ liệu từ Prometheus để giám sát và quản lý hệ thống của mình thông qua giao diện người dùng của công cụ quản lý quan trọng (ví dụ: Grafana).

I create a new Prometheus Data source using the Grafana user interface.

I connect to my Prometheus URL, which also uses SSL and has basic auth configured.

## Thiết lập Prometheus dashboards:
https://grafana.com/grafana/dashboards/
Hoàn thành và các bước tiếp theo



