  

zk中分布式锁，是为了解决透传，redis中，分布式协调

# zk 分布式协调

https://zookeeper.apache.org/

一下内容来自

https://zookeeper.apache.org/doc/r3.7.0/zookeeperOver.html

原语:zk提供的,可以完成那些问题

集群要不是`分片集群`或者`有主的集群`

`zk`是有主的集群【leader】

`zk`中的集群的数据一样

`主从集群`架构，主从集群，写发生在【主】，读发生在从上，扩大读的能力,还有observe节点，不参与选举

选主，过程

> leader 挂，不可用，不可靠，`zk`集群及其高可用
>
> 如果有一种方式可以快速的恢复一个leader

两种状态

1. 可用
2. 不可用

> 恢复为可用状态越快越好，200ms可恢复，zk是强一致性，但是可用性也还行，是因为在不可用时能很快恢复为可用



官方压测，`200ms`恢复为可用状态

![image-20210330201409962](https://i.loli.net/2021/03/30/Kh68g3bmPR5lytv.png)

zk的目录结构树，**节点可以存储的数据为1M**，为了保证高性能

不把zk当做数据库用

zk是目录树结构；node可以存储数据1MB

1. 持久节点
2. 零时节点

zk 客户端有session关联；类似tomcat；session有创建和消亡



session 的目的是什么？

session 在锁【就是节点】就再，session不在锁就释放了，也就是节点，是零时节点

**两种节点**，都支持序列化

**顺序性**，简单地主从，主从模型保证

**最终一致性**

![image-20210330202514122](https://i.loli.net/2021/03/30/gnKoNTmPlkCG7va.png)



## 安装

![image-20210330203151258](https://i.loli.net/2021/03/30/AU3IyTkvo2PMYqR.png)



安装java  https://www.jianshu.com/p/fe69558cb504 

> scp -r jdk-8u281-linux-x64.rpm root@node4:'pwd' 
>
> 复制 
>
> 所有窗口同时发送  `rpm -ivh jdk-8u281-linux-x64.rpm`
>
> `scp -r profile root@node4:/etc ` 复制 profile
>
> 所有窗口同时发送  `source /etc/profile`
>
> 所有窗口同时发送  `jps`

jps 

wget zk ；基于java

zoo.cfg  默认配置文件名

配置文件的详细说明

在配置文件中有集群所有节点信息，集群的所有节点，**但是Redis是通过发布订阅的模式知道其他的节点的**

初始选主是谦让出来的，选择编号最大的，过半参与就可以， 3 或者4，但是不过半，是不能选举出的，**所以整个集群不可用**

node1 配置好了，直接scp -p 到其他的节点

`scp -r 目录 nodeName:`pwd`



![image-20210330204714360](https://i.loli.net/2021/03/30/GbzfuX9ZoUq43Hk.png)

在持久化的目录中写，myid文件，填id号

`echo 2 /var/malone/myid`

实验中一共是四台机器

每个node的myid不一样

![image-20210330204555032](https://i.loli.net/2021/03/30/GmVQpvCnK5yJtIx.png)



将ZK_HOME添加和bin添加到PATH中，添加zk到profile中

`source profile`

![image-20210402161039393](https://gitee.com/zilongcc/images/raw/master/image-20210402161039393.png)

靠人规划集群

3888：建立链接，通信，投票

2888，对外通信

https://blog.csdn.net/pengshengli/article/details/84564832

![image-20210402164243280](https://gitee.com/zilongcc/images/raw/master/image-20210402164243280.png)

## 启动

按照 1  2 3  4 启动

`zkServer.sh help ` 查看前台启动，方便查看日志

zkServer.sh start-foreground // 前台启动，产看日志方便 

zkServer.sh status 可以产看是否leader

> 启动后和leader同步，4最后启动，编号最大，但是是follower

在zkclient.sh 默认连接本地

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

再同一个节点下创建，对一个节点创建

create -s  // 分布式下防止覆盖,有序节点，分布式锁实现

![image-20210330213238595](https://i.loli.net/2021/03/30/NZJpsQ297S3K1vn.png)

`统一命名`

分布式id，分布式命名

rmr path

删除创建，继续递增

---



![image-20210330214121754](https://i.loli.net/2021/03/30/ZbxnXKVDSkmg5d3.png)







![image-20210330214716934](https://i.loli.net/2021/03/30/gWjqd7wIX4CrZvQ.png)

`netstat -natp |egrep `(2888|3888)``



选举投票使用一个端口

3888:选主投票使用

2888：leader 接受write请求使用

![image-20210330215222530](https://i.loli.net/2021/03/30/vpnEhMKo1cdGjib.png)

<<<<<<< HEAD
两两链接，通信完成leader的选择

# 参考

- https://gitbook.cn/books/5ef47a1690c794640abd37d4/index.html gitchat



**主要内容**

- paxzo

- zab

- > 选主模型，数据同步

- watch

- API 开发 

- callback reactive 更充分的压榨OS hw 资源 性能



定位 分布式协调 扩展性 可靠性 时序性  快

Observers

架构  

- 角色 

  - leader 写

  - flower 读  之后follower 才能参与选举 Observer 不能

  - observer 扩展性 读写分离 发达查询能力

    - zoo.cfg

      ![image-20210331204918395](https://i.loli.net/2021/03/31/k1fj5eyWd3FiOZE.png)

可靠性

- 攘其外必先安其内 

- 快速回复，有不可用，但是能快速选主

- 快速选出leader

- 数据的可靠性 可用 一致性 

  - 攘其外 一致性 
  - 最终一致性
  - 过程中 节点是否对外提供服务 ？？

  - 都是分布式相关的问题

**paxos** https://www.douban.com/note/208430424/

基于消息传递的一致性算法

没有拜占庭将军问题，相信网络问题，在一个科信环境

**过半通过；两阶段提交**

**异常，同时提议，解决冲突**

写都是需要leader，而且是两阶段提交

![image-20210331211210579](https://i.loli.net/2021/03/31/xWYOy62ZrmeIVhS.png)

zab协议，原子广播协议 有leade 时 paxos的精简版

写操作转发到leader

创建事务id Zxid 

​	原子：成功或者失败，没有中间状态 （对列 +2PC）

​	广播协议：分布式，不能保证所有都能收到

​	队列：FIFO,顺序

zk的数据是在内存的

4.1 发起写日志的事，两阶段的第一阶段 ，在leader中维护发送队列

4.1 - ok

4.2  write，队列中保存动作，需要follower消费，恢复

最终给客户端ok

![image-20210331214236590](https://i.loli.net/2021/03/31/WkNi3GOCJaVLZgq.png)



简化paxos，主挂，停止服务

两阶段，**写日志，写内存**



1:24











=======
zk的选举过程

1. 3888 两两通信
2. 只要任何人投投票，都会触发那个准leader发起自己的投票
3. 推选制：先比较zxid，如果zxid相同，在提交myid

> 不考虑网络演示，大部分是网络没有延迟，只选择myid最大的
>
> 如果因为技术而技术很难设计出来



zk 2 node 不可以对外服务，leader 选不出，整个集群不可用



# watch

统一视图，数据一致

目录结构

同步两个客户端，代替心跳

通过session，消失，产生事件，create delete change children ，回到wathc的callback

> 方向性和时效性 -> 比心跳及时

**但是怎么实现的呢 ？**

![image-20210402091111189](https://gitee.com/zilongcc/images/raw/master/image-20210402091111189.png)



**线程池和连接池的关系**

1. 验证，链接node，挂掉，session迁移，session id 不变，watch是session级别，所有事件都能监控到
2. wath 一次性的，需要重复注册
3. 回调方法，快速通过，再回调



两链接，通信完成leader的选择

**主要内容**

- paxzo

- zab

- > 选主模型，数据同步

- watch

- API 开发 

- callback reactive 更充分的压榨OS hw 资源 性能



定位 分布式协调 扩展性 可靠性 时序性  快

Observers

架构  

- 角色 

  - leader 写

  - flower 读  之后follower 才能参与选举 Observer 不能

  - observer 扩展性 读写分离 发达查询能力

    - zoo.cfg

      ![image-20210331204918395](k1fj5eyWd3FiOZE.png)

可靠性

- 攘其外必先安其内 

- 快速回复，有不可用，但是能快速选主

- 快速选出leader

- 数据的可靠性 可用 一致性 

  - 攘其外 一致性 
  - 最终一致性
  - 过程中 节点是否对外提供服务 ？？

  - 都是分布式相关的问题

**paxos** https://www.douban.com/note/208430424/

基于消息传递的一致性算法

没有拜占庭将军问题，相信网络问题，在一个科信环境

**过半通过；两阶段提交**

**异常，同时提议，解决冲突**

写都是需要leader，而且是两阶段提交

![image-20210331211210579](xWYOy62ZrmeIVhS.png)

zab协议，原子广播协议 有leade 时 paxos的精简版

写操作转发到leader

创建事务id Zxid 

​	原子：成功或者失败，没有中间状态 （对列 +2PC）

​	广播协议：分布式，不能保证所有都能收到

​	队列：FIFO,顺序

zk的数据是在内存的

4.1 发起写日志的事，两阶段的第一阶段 ，在leader中维护发送队列

4.1 - ok

4.2  write，队列中保存动作，需要follower消费，恢复

最终给客户端ok

![image-20210331214236590](WkNi3GOCJaVLZgq.png)



简化paxos，主挂，停止服务

两阶段，**写日志，写内存**



zk的选举过程

1. 3888 两两通信
2. 只要任何人投投票，都会触发那个准leader发起自己的投票
3. 推选制：先比较zxid，如果zxid相同，在提交myid

> 不考虑网络演示，大部分是网络没有延迟，只选择myid最大的
>
> 如果因为技术而技术很难设计出来



zk 2 node 不可以对外服务，leader 选不出，





# 分布式协调

> 分布式配置，分布式锁【热点】
>
> 通过分布式式锁，考察能力，很多的知识点，

## 分布式配置

> 配置变更
>
> 大量获取配置信息

![image-20210402100409317](https://gitee.com/zilongcc/images/raw/master/image-20210402100409317.png)

1. get
2. watch，变化回调



![image-20210402101459846](image-20210402101459846.png)

回调嵌套

![image-20210402103623308](https://gitee.com/zilongcc/images/raw/master/image-20210402103623308.png)



回调

删除node，回调，数据置空，cd初始化，客户端继续等待数据

喷喷

**分布式锁**

>  两个节点，两个线程，同步执行

分布式定时任务，redis 是有单点问题，持久化，导致慢等等

争抢锁，一个能获得所

> watch 



# 参考

- https://gitbook.cn/books/5ef47a1690c794640abd37d4/index.html gitchat
>>>>>>> 60e01bc062d9585f3ef6321e8947082dfffa3e04
