  

zk中分布式锁，是为了解决透传，redis中，分布式协调



# zk 分布式协调

https://zookeeper.apache.org/

一下内容来自

https://zookeeper.apache.org/doc/r3.7.0/zookeeperOver.html

原语:zk提供的,可以完成那些问题

集群要不是分片集群或者有主的集群

zk是有主的集群

zk中的集群的数据一样

`主从集群`架构，主从集群，写发生在主，读发生在从上

选主，过程

> leader 挂，不可用，不可靠，`zk`集群及其高可用
>
> 如果有一种方式可以快速的恢复一个leader

两种状态

1. 可用
2. 不可用

> 恢复为可用状态越快越好



![image-20210330201226939](C:/Users/Administrator/AppData/Roaming/Typora/typora-user-images/image-20210330201226939.png)

官方压测，`200ms`恢复

![image-20210330201409962](https://i.loli.net/2021/03/30/Kh68g3bmPR5lytv.png)

**节点可以存储的数据为1M**，为了保证高性能

不把zk当做数据库用

zk是目录树结构；node可以存储数据1MB

1. 持久节点
2. 零时节点

zk 客户端有session关联；类似tomcat；session有创建和消亡



session 的目的是什么？

session 在锁就再，session不在锁就释放了，也就是节点，是零时节点

**两种节点**，都支持序列化



**顺序性**，简单地主从，主从模型保证

**最终一致性**

![image-20210330202514122](https://i.loli.net/2021/03/30/gnKoNTmPlkCG7va.png)



## 安装

![image-20210330203151258](https://i.loli.net/2021/03/30/AU3IyTkvo2PMYqR.png)



安装java 

jps

wget zk ；基于java

zoo.cfg  默认配置文件名

配置文件的详细说明

在配置文件中完整的配置，集群的所有节点，但是Redis是通过发布订阅的模式知道其他的节点的

初始选主是钱让出来的，选择编号最大的，过半参与就可以， 3 或者4

node1 配置好了，直接scp -p 到其他的节点

![image-20210330204714360](https://i.loli.net/2021/03/30/GbzfuX9ZoUq43Hk.png)

在持久化的目录中写，myid文件，填id号

实验中一共是四台机器

每个node的myid不一样

![image-20210330204555032](https://i.loli.net/2021/03/30/GmVQpvCnK5yJtIx.png)



添加zk到profile

![image-20210330204936004](C:/Users/Administrator/AppData/Roaming/Typora/typora-user-images/image-20210330204936004.png)

`source profile`



## 启动

按照 1  2 3  4 启动

`zkServer.sh help `

zkServer.sh start // 前台启动，产看日志方便

zkServer.sh status 可以产看是否leader

> 启动后和leader同步，4最后启动，编号最大，但是是f

zkclient 默认连接本地

help 查看帮助

ls /

create  /xxx  ""

ls /

get /xxx

set /xxxx "aaa"

get /xxxx

![image-20210330210015374](https://i.loli.net/2021/03/30/ysGEpPnaSrDA9vJ.png)



czxid:create  64字节

前32：leader的纪元

后32：事务id

mzxid:修改编号

pzxid: 记录当前节点下最后一个节点创建的编号

![image-20210330210626802](https://i.loli.net/2021/03/30/fgzduRVmHojpFwL.png)

create -e // 创建零时节点

![image-20210330211123882](https://i.loli.net/2021/03/30/1d5Qo6bI279GNLx.png)

owner id 和 session id 的关系

零时节点和session 同步，session消失，零时节点消失

 连接的sever 挂了，但是session继续存在，是因为session也是`统一视图`

> zk 内部的动作，比如同步也是消耗事务id的；连接同步session ，消耗id

session 也消耗 id

对一个节点创建

create -s  // 分布式下防止覆盖

![image-20210330213238595](https://i.loli.net/2021/03/30/NZJpsQ297S3K1vn.png)

`统一命名`

分布式id，分布式命名

rmr path

删除创建，继续递增

---



![image-20210330214121754](https://i.loli.net/2021/03/30/ZbxnXKVDSkmg5d3.png)







![image-20210330214716934](https://i.loli.net/2021/03/30/gWjqd7wIX4CrZvQ.png)

选举投票使用一个端口

![image-20210330215222530](https://i.loli.net/2021/03/30/vpnEhMKo1cdGjib.png)

