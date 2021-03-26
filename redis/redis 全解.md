磁盘格式化

随着文件表达，为什么访问会变慢

4可对齐为啥？

![image-20210326111229963](https://gitee.com/zilongcc/images/raw/master/image-20210326111229963.png)

##  数据库：表很大，性能下降

![image-20210326113159704](https://gitee.com/zilongcc/images/raw/master/image-20210326113159704.png)



## redis介绍

https://db-engines.com/en/

## redis ops

![image-20210326112002881](https://gitee.com/zilongcc/images/raw/master/image-20210326112002881.png)

https://redis.io/

http://www.redis.cn/

```
Redis 是一个开源（BSD许可）的，内存中的数据结构存储系统，它可以用作数据库、缓存和消息中间件。 它支持多种类型的数据结构，如 字符串（strings）， 散列（hashes）， 列表（lists）， 集合（sets）， 有序集合（sorted sets） 与范围查询， bitmaps， hyperloglogs 和 地理空间（geospatial） 索引半径查询。 Redis 内置了 复制（replication），LUA脚本（Lua scripting）， LRU驱动事件（LRU eviction），事务（transactions） 和不同级别的 磁盘持久化（persistence）， 并通过 Redis哨兵（Sentinel）和自动 分区（Cluster）提供高可用性（high availability）。
```

value的数据类型

![image-20210326112358426](https://gitee.com/zilongcc/images/raw/master/image-20210326112358426.png)

**mc没有类型** JSON,

## value类型的意义是什么？

![image-20210326112808425](https://gitee.com/zilongcc/images/raw/master/image-20210326112808425.png)



**计算向数据移动**，计算方法在数据，只是返回少数的数据

> 1. memcache返回的value是所有的数据到客户端，server 网卡IO，client要有你实现的的代码去解码
> 2. 类型不是很重要，redis的server中对每种类型都有自己的方法，index，lpop

## install

```
wget  http://download.redis.io/releases/redis-6.0.6.tar.gz
```

README ，查看安装方法

> 教程中使用5.X, centos6.x 当前不能安装6.x,报错；跳过，直接，yum install

参考 https://blog.csdn.net/luChenH/article/details/89947971

>  `tar  -vxf `; 缺少v,可以规避IO



redis 单进程 单线程 单实例

## **多个链接，一个线程能处理过来吗？**

连接数据走网卡，读写和内存差10w倍，所以除非每秒钟超过上线10W,所以一般是能处理的

如果算上走网卡，可能到7,8W

## 查看系统调用

`man 2 socket `

`yum install man man-page`

## redis 中的 epoll

bio，调用bio阻塞线程，如果有多个请求，阻塞多个线程，等待客户端的数据发送；线程的成本1MB,

1. 调度成本
2. 内存成本

![image-20210326134700994](https://gitee.com/zilongcc/images/raw/master/image-20210326134700994.png)

> 现在系统的条件都是有一下连个条件
>
> 1. 冯诺依曼架构
>
> 2. TCP和IP协议
>
>    以上限制，导致各种技术的出现



socket 可以是noblocking的，IO ，调用之后不阻塞，还可以继续执行，一个线程就可以完成多个fd的读取；

在用户空间寻轮完成，多个fd的读写

![image-20210326134723956](https://gitee.com/zilongcc/images/raw/master/image-20210326134723956.png)

**NIO ，同步非阻塞**

问题：socket调用不阻塞，nobloking

但是**系统调用**次数太多，内核发展，轮训放在内核中，select系统调用 【NIO问题】

`man 2 select `

![](image-20210326134723956.png)

select：`统一将所有的fds，传递给内核，返回有数据的fd，减少系统调用`；多路复用；减少内核态和用户态调用

问题:**内核和洪用户态调用，需要cp数据**【select 问题】

mmp【系统调用】，**内核和用户态共享，节省cp**

![image-20210326132320986](https://gitee.com/zilongcc/images/raw/master/image-20210326132320986.png)呜## 



## sendfile



![image-20210326132519516](https://gitee.com/zilongcc/images/raw/master/image-20210326132519516.png)

零CP的概念，**就是data不经过用户空间，直接在内核空间完成**

> 可以详细画图说明

## kafka 

![image-20210326132755675](https://gitee.com/zilongcc/images/raw/master/image-20210326132755675.png)

1. 进入的数据使用mmap，将内核空间的数据映射到用户空间，节省cp的时间

2. 写出的时候使用的是sendfile（fd，fd2）**？？**

   > 可以详细了解这些系统调用

   



## redis 架构

![image-20210326133115082](https://gitee.com/zilongcc/images/raw/master/image-20210326133115082.png)

![image-20210326135510073](image-20210326135510073.png)

redis单线程，从客户端发送的数据，会**被顺序的**执行；

**每连接内**，`多线程不能保证顺序`,对于一个key的操作在一个链接中完成

## 使用

默认16个库，0 1 2 使用  -n  配置链接的库



![image-20210326135817293](https://gitee.com/zilongcc/images/raw/master/image-20210326135817293.png)



![image-20210326135837147](https://gitee.com/zilongcc/images/raw/master/image-20210326135837147.png)



![image-20210326135914060](https://gitee.com/zilongcc/images/raw/master/image-20210326135914060.png)



**help + tab** 查看帮助

![image-20210326140751954](https://gitee.com/zilongcc/images/raw/master/image-20210326140751954.png)

**NX:没有出现才设置**

**XX：只能更新**

多个线程使用一个redis的连接；

mset 

mget

**正反向索引**



![image-20210326141157898](https://gitee.com/zilongcc/images/raw/master/image-20210326141157898.png)



![image-20210326141235602](https://gitee.com/zilongcc/images/raw/master/image-20210326141235602.png)



![image-20210326141340640](https://gitee.com/zilongcc/images/raw/master/image-20210326141340640.png)

![image-20210326141453318](https://gitee.com/zilongcc/images/raw/master/image-20210326141453318.png)

**key中有value的类型**

![image-20210326141604424](https://gitee.com/zilongcc/images/raw/master/image-20210326141604424.png)

查看encoding

![image-20210326141722015](https://gitee.com/zilongcc/images/raw/master/image-20210326141722015.png)

object encoding k1

![image-20210326141850932](https://gitee.com/zilongcc/images/raw/master/image-20210326141850932.png)

![image-20210326141930108](https://gitee.com/zilongcc/images/raw/master/image-20210326141930108.png)

![image-20210326142110370](https://gitee.com/zilongcc/images/raw/master/image-20210326142110370.png)

## 二进制安全

redis存储的是字节数据，没有编码信息 ？？

**字节**

https://www.jianshu.com/p/3e9ba97572ae/

https://segmentfault.com/a/1190000023130486

## GETSET

![image-20210326144130572](https://gitee.com/zilongcc/images/raw/master/image-20210326144130572.png)

 ## MSETNX

![image-20210326144451212](https://gitee.com/zilongcc/images/raw/master/image-20210326144451212.png)

原子，一个失败，所有失败

多连接会怎样

## MYSQL

MYSQL 是使用BIO，磁盘IO 是瓶颈，所以不能让多个请求进入

## bitmap

![image-20210326150611639](https://gitee.com/zilongcc/images/raw/master/image-20210326150611639.png)

## 为啥是ASCII，不是UTF-8 ??

![image-20210326150852473](https://gitee.com/zilongcc/images/raw/master/image-20210326150852473.png)



## redis-cli -raw 

似乎是使用客户端的编码解析字节数组，否则使用ascii

## BITCOUNT 

统计1出现次数，start和end指字节，字节

1出现的次数

![image-20210326151701246](https://gitee.com/zilongcc/images/raw/master/image-20210326151701246.png)

## bitops

![image-20210326151550317](https://gitee.com/zilongcc/images/raw/master/image-20210326151550317.png)

返回二进制位，是全量的，第一个出现的位置

## bitop

![image-20210326151934459](https://gitee.com/zilongcc/images/raw/master/image-20210326151934459.png)

![image-20210326152023436](https://gitee.com/zilongcc/images/raw/master/image-20210326152023436.png)

## bitmap的应用场景

`setbit` `bitcount` `bitops` `bitop` 操作

1. 有用户系统，统计用户登录天数，且窗口随机

MYSQL:

一次登录8个字节

Redis：统计用户登录

![image-20210326152517965](https://gitee.com/zilongcc/images/raw/master/image-20210326152517965.png)

**用户为key**

618做活动，登录送礼物，，怎么备货

**活跃用户统计**，一段时间登录人

![image-20210326153336583](https://gitee.com/zilongcc/images/raw/master/image-20210326153336583.png)

**key是天**

![image-20210326153630366](https://gitee.com/zilongcc/images/raw/master/image-20210326153630366.png)

直接使用redis的位操作，代替MYSQL的记录，

1. 减少存储的量
2. 位运算速度快

## incr

![image-20210326154051810](https://gitee.com/zilongcc/images/raw/master/image-20210326154051810.png)



incr 这种操作，可以在redis中完成，防止请求到达数据库，较少事务

## 了解不同的存储方式

![image-20210326154236111](image-20210326154236111.png)

## 总结

主要还是关于String相关的操作

bitmap的应用

## 第三课 list类型

value类型就是list

双向无环链表

`help @list`



## lrem

![image-20210326163441438](https://gitee.com/zilongcc/images/raw/master/image-20210326163441438.png)



## linsert

![image-20210326163542049](https://gitee.com/zilongcc/images/raw/master/image-20210326163542049.png)

## llen

# Blpop

多个链接到一个server，如果执行blpop，如果不存在会阻塞

![image-20210326163838012](https://gitee.com/zilongcc/images/raw/master/image-20210326163838012.png)

**支持，单播订阅**，FIFO

![image-20210326163957469](https://gitee.com/zilongcc/images/raw/master/image-20210326163957469.png)

## LTRIM

删除两端的元素

# hash

对象存储

![image-20210326164304258](https://gitee.com/zilongcc/images/raw/master/image-20210326164304258.png)

![image-20210326164656627](https://gitee.com/zilongcc/images/raw/master/image-20210326164656627.png)

![image-20210326164854333](https://gitee.com/zilongcc/images/raw/master/image-20210326164854333.png)

应用场景，需要计算的



# redis 主要应用

## 利用redis的sorted set实现排行榜功能

http://www.majianwei.com/archives/9279

https://zhuanlan.zhihu.com/p/29665317

http://blog.720ui.com/2016/redis_action_05_used/

## 常见问题

## 常见面试题

- http://www.gameboys.cn/article/57
- https://zhuanlan.zhihu.com/p/91539644 敖丙系列
- https://www.imooc.com/article/36399
- https://github.com/Snailclimb/JavaGuide/blob/master/docs/database/Redis/redis-all.md Javaguide

## 学习资料

https://kaiwu.lagou.com/course/courseInfo.htm?courseId=6&sid=60-h5Url-0&buyFrom=2&pageId=1pz4#/content?courseId=6&isShowSections=true