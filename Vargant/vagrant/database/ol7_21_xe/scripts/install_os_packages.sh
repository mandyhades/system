echo "******************************************************************************"
echo "Prepare yum with the latest repos." `date`
echo "******************************************************************************"
echo "nameserver 8.8.8.8" >> /etc/resolv.conf

yum install -y yum-utils zip unzip

echo "******************************************************************************"
echo "Install Oracle prerequisite package." `date`
echo "Not necessary, but oracle OS user has no home directory if this is not run first."
echo "******************************************************************************"
yum install -y oracle-database-preinstall-21c

echo "******************************************************************************"
echo "Install Oracle RPM." `date`
echo "******************************************************************************"
yum -y localinstall /vagrant/software/oracle-database-xe-21c-1.0-1.ol7.x86_64.rpm
#yum update -y
