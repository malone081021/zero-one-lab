## 虚拟机安装和配置

安装VMWare workstation

## 安装操作系统

重点在分区，自定义分区

/boot 200M

/swap 2048 M

/  用户其他剩余的所有空间

## 配置网络

1. vms 编辑 > 虚拟网络编辑器 > 选择NAT模式的网卡

![image-20200917092538383](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200917092538383.png)

![image-20200917092350114](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200917092350114.png)

> 上面是系统中网络配置的内容，其中删除了**网卡的物理地址**

需要注意，当前子网IP 需要和宿主机的在**同一个网段**，否则不能通过宿主机的**xshell登录虚拟机**

## 克隆更多的虚拟机

> 注意**关闭**当前已经创建的虚拟机

1. 先创建当前虚拟机的快照 base
2. 克隆虚拟机，修改名称 node02

1. 修改IP 和 hostname

   > 修改hostname `vi /etc/sysconfig/network`

2. 删除 base 中的网卡名和物理地址的映射文件，如下

   `rm -f /etc/udev/rules.d/70-persistent-cd.rules` 

   `reboot` 系统重新生成

