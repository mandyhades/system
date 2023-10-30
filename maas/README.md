Maas
====

Tham gia phát triển dự án mã nguồn mở OpenStack Sahara - cung cấp một công cụ để đơn giản hóa việc tạo ra các Hadoop Cluster trên nền tảng đám mây IaaS OpenStack. Ý tưởng cho MHST 2014 là tạo ra một dịch vụ đám mây Mahout-as-a-Service dựa vào nền tảng dự án OpenStack Sahara.  Sản phẩm  hướng tới là một dịch vụ phân tích dữ liệu hỗ trợ nhiều môi trường và nền tảng khác nhau kể cả di động.

**Mã dự án:** 25

**Tên dự án:** Tham gia phát triển PMNM OpenStack Sahara: phát triển dịch vụ Mahout-as-a-Service

**Wiki ý tưởng:** http://wiki.vfossa.vn/mhst:ideas:mhst2014:maas

**Wiki dự án:** https://github.com/dattbbk/Maas/wiki

**Thời gian thực hiện:** Từ ngày 14/6/2014 đến hết ngày 26/9/2014

**Quản lý công việc tại:** https://trello.com/b/BEkARgZz/mhst-2014-icse-bach-khoa-ha-n-i

**Mailing list:** https://groups.google.com/forum/?hl=vi#!forum/mhst-2014

**Nhật ký dự án:** 
* https://github.com/dattbbk/Maas/blob/master/MHST2014_Maas_NhatKyDuAn.ods
* Hoặc xem online: https://docs.google.com/spreadsheets/d/1uGoAD6ve9Cg9I6lK9nfrzEfKqoXKx9xQNhNF-b-iDSY/edit#gid=0

**Kho mã nguồn và tài liệu:** https://github.com/dattbbk/Maas

**Cấu trúc thư mục docs:** https://github.com/dattbbk/Maas/tree/master/docs
* openstack: 
  - MHST2014_MaaS_Install_Openstack_Icehouse_ICSE: Hướng dẫn cài đặt hệ thống Openstack
* sahara: 
  - MHST2014_MaaS_Tong_quan_Sahara: Giới thiệu tổng quan Sahara
  - MHST2014_MaaS_Huong_dan_cai_dat_va_su_dung_Sahara: Hướng dẫn cài đặt, cấu hình, sử dụng Sahar và một số lỗi hay gặp phải
* api:
  - MHST2014_MaaS_lib-use: Danh sách các thư viện ngoài sử dụng trong dự án
  - MHST2014_MaaS_Api-service-mahout-jobs: Bản thiết kế dịch vụ web phục vụ cho việc quản lý và thực thi các công việc của Mahout
  - MHST2014_MaaS_Api-monitor-work-hadoop: Bản mô tả thiết kế api cho nhiệm vụ quản lý, giám sát các công việc trên Hadoop
* appweb:
  - MHST2014_MaaS_Web: Tài liệu hướng dẫn sử dụng ứng dụng trên web cho chức năng giám sát công việc của Mahout
* appmobile:
  - MHST2014_MaaS_Guide-use-app-mobie: Tài liệu hướng dẫn sử dụng ứng dụng trên thiết bị di động cho chức năng giám sát công việc của Mahout
* MHST2014_Maas_Test_case.ods: File test case các chức năng

**Cấu trúc thư mục code:** https://github.com/dattbbk/Maas/tree/master/code
* api:
  - MaaSAPI : Code api giúp quản lý, giám sát các công việc trên Hadoop và quản lý dữ liệu trên hệ thống file HDFS
  - MaaSServlet : Code webservice cung cấp dịch vụ thực thi các giải thuật của Mahout và dịch vụ quản lý dữ liệu kết quả của các công việc đã thực hiện
* web : Code web quản lý Mahout
* mobile : 
  - MaaSUI : ứng dụng trên thiết bị di động giúp giám sát các công việc thực hiện 

**Kết quả đạt được:**
* Dựng được hệ thống cloud Openstack
* Hướng dẫn cài đặt, cấu hình và sử dụng Sahara
* Triển khai Mahout
* Ứng dụng mobile 
* Ứng dụng web
* Các APIs

**Link video demo:** https://www.youtube.com/watch?v=rjUyvLjrfOo

**Hướng phát triển thời gian tới:**
* Tích hợp các chức năng quản lý Mahout vào Sahara Dashboard
* Phát triển thêm các APIs và chức năng khác

**Danh sách mentor:**
* Mentor: TS. Nguyễn Bình Minh. Giảng viên Viện công nghệ thông tin và truyền thông, Trường Đại học Bách Khoa Hà Nội. Email: minhnb@soict.hust.edu.vn. Homepage: http://is.hust.edu.vn/~minhnb
* Co-Mentor: TS. Trần Việt Trung. Giảng viên Viện công nghệ thông tin và truyền thông, Trường Đại học Bách Khoa Hà Nội. Email: trungtv@soict.hust.edu.vn. Homepage: http://is.hust.edu.vn/~trungtv/
* Co-Mentor: KS. Lê Quang Hiếu. Viện nghiên cứu và phát triển Viettel (Viettel R&D). Email: hieulq19@gmail.com

**Thành viên nhóm:**
* Trần Sỹ Đạt.			Email: dattbbk@gmail.com
* Lê Đại Cát.       Email: daicatqn892@gmail.com
* Lê Công Tuấn.     Email: tuancnttbk93@gmail.com
* Lê Quyết Thắng.   Email: thang.itbk@gmail.com

