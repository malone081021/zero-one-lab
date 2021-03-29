## 数据存储的发展

寻址或者读取速度常识：

- 磁盘：1，寻址：ms 2，带宽：G/M

- 内存：1，寻址：ns  2，带宽：很大

秒>毫秒>微秒>纳秒 `磁盘`比`内存`在寻址上慢了10W倍

I/O buffer：成本问题

磁盘与磁道，扇区，一扇区 `512Byte`带来一个成本变大：索引

`4K` 操作系统，无论你读多少，都是最少`4k`从磁盘拿，减少`IO`

> 4k 对齐的意义 ？？
>
> 8字节对齐的意义

>  磁盘格式化

## 文件系统 - MSYQL - redis

1. 文件系查询，需要全量便利整个文件

2. MYSQL创建索引，索引数据量较少，定位数据后才加载真正需要的数据，减少IO

3. Redis直接使用内存，存储，不需要IO；

4. 如果能将整个数据在内存中存储，且可以SQL存储，节省IO时间，比较完美，但是较贵，实例，HANA数据库

   > 但是redis只能存储部分的数据，没有全量的数据，所以不能使用SQL

### 随着文件变大，为什么访问会变慢（MYSQL）

> MYSQL 关系型数据库：创建表需要指定schema，提前指定了数据的存储字节宽度，存储倾向于行级存储。
>
> 1. 预占领空间，节省重新分配的时间
> 2. 构建索引，加速查询
> 3. 一次加载`4K`的数据

![image-20210329151328031](https://gitee.com/zilongcc/images/raw/master/image-20210329151328031.png)

如果有索引，增删变慢，但是查询速度不一定

1. 如果是少量的查询，依然比较快
2. 但是大的并发下磁盘的带宽是瓶颈；【也就是请求到磁盘开始排队】

## 各种数据库介绍和排名的网站

![image-20210329151612167](https://gitee.com/zilongcc/images/raw/master/image-20210329151612167.png)

**分类排名**

![image-20210329151715002](https://gitee.com/zilongcc/images/raw/master/image-20210329151715002.png)

**详细介绍，各种参数，各种特性**

标书内容可以参考

**例如redis**

![image-20210326112002881](https://gitee.com/zilongcc/images/raw/master/image-20210326112002881.png)

**每秒10w/ops**

##  redis 重要参考资料

https://redis.io/

http://www.redis.cn/

```
Redis 是一个开源（BSD许可）的，内存中的数据结构存储系统，它可以用作数据库、缓存和消息中间件。 它支持多种类型的数据结构，如 字符串（strings）， 散列（hashes）， 列表（lists）， 集合（sets）， 有序集合（sorted sets） 与范围查询， bitmaps， hyperloglogs 和 地理空间（geospatial） 索引半径查询。 Redis 内置了 复制（replication），LUA脚本（Lua scripting）， LRU驱动事件（LRU eviction），事务（transactions） 和不同级别的 磁盘持久化（persistence）， 并通过 Redis哨兵（Sentinel）和自动 分区（Cluster）提供高可用性（high availability）。
```

redis的数据类型指，key-value中value的数据类型

![image-20210326112358426](https://gitee.com/zilongcc/images/raw/master/image-20210326112358426.png)

> memacache 只有String一种数据类型，所以他存储的是JSON??

## Redis中value类型的意义是什么？

![image-20210326112808425](https://gitee.com/zilongcc/images/raw/master/image-20210326112808425.png)

`memcache`中获取数据需要将整个数据返回（必须经过`网络IO`），客户端解码，获取部分数据

但是`Redis`是将命令发送到`server`，`server`执行完，直接返回结果；

类型不重要，重要的是类型提供的方法

总结：`计算向数据移动`，计算方法在数据，只是返回少数的数据

> 1. memcache返回的value是所有的数据到客户端，server 网卡IO，client要有你实现的的代码去解码
> 2. 类型不是很重要，redis的server中对每种类型都有自己的方法，index，lpop

## Redis安装install

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

>  查看系统调用
>
> `man 2 socket `
>
> `yum install man man-page`

## Redis 中的 epoll

bio，调用bio阻塞线程，如果有多个请求，阻塞多个线程，等待客户端的数据发送；线程的成本1MB,

问题：

1. 调度成本
2. 内存成本

![image-20210326134700994](https://gitee.com/zilongcc/images/raw/master/image-20210326134700994.png)

> 现在系统的条件都是有一下两个条件
>
> 1. 冯诺依曼架构
>
> 2. TCP和IP协议
>
>    以上限制，导致各种技术的出现



Socket 可以是`noblocking`的，IO ，调用之后不阻塞，还可以继续执行，一个线程就可以完成多个`fd`的读取；

在用户空间寻轮完成，多个`fd`的读写

![image-20210326134723956](https://gitee.com/zilongcc/images/raw/master/image-20210326134723956.png)

**NIO ，同步非阻塞**

问题：socket调用不阻塞，nobloking

但是**系统调用**次数太多，内核发展，轮训放在内核中，select系统调用 【NIO问题】

**select** 系统调用，多路复用

`man 2 select `

select：`统一将所有的fds，传递给内核，返回有数据的fd，减少系统调用`；多路复用；减少内核态和用户态调用

问题:**内核和用户态调用，需要cp数据**【select 问题】

mmp【系统调用】，**内核和用户态共享，节省cp**

![image-20210326132320986](https://gitee.com/zilongcc/images/raw/master/image-20210326132320986.png) 



## sendfile



![image-20210326132519516](https://gitee.com/zilongcc/images/raw/master/image-20210326132519516.png)

零CP的概念，**就是data不经过用户空间，直接在内核空间完成**

> 可以详细画图说明

## kafka 

![image-20210326132755675](https://gitee.com/zilongcc/images/raw/master/image-20210326132755675.png)

1. 进入的数据使用mmap，将内核空间的数据映射到用户空间，节省cp的时间

2. 写出的时候使用的是sendfile（fd，fd2）**？？**

   > 可以详细了解这些系统调用

   

## Redis 架构

![image-20210326133115082](https://gitee.com/zilongcc/images/raw/master/image-20210326133115082.png)

`Redis`单线程，从客户端发送的数据，会**被顺序的**执行；

**每连接内**，`多线程不能保证顺序`,对于一个key的操作在一个链接中完成

## 使用案例

默认16个库，0 1 2 使用  -n  配置链接的库

``` shell
[root@sec ~]# redis-cli -p 6379
127.0.0.1:6379> exit # 退出
[root@sec ~]# redis-cli 
127.0.0.1:6379> exit
[root@sec ~]# redis-cli
127.0.0.1:6379> set k1 aaa
OK
127.0.0.1:6379> get k1
"aaa"
127.0.0.1:6379> select 8 #选择8数据库
OK
127.0.0.1:6379[8]> get k1 # 在8 中获取不到k1
(nil)
127.0.0.1:6379[8]>
```



```shell
127.0.0.1:6379> help # 获取帮助
redis-cli 3.2.12
To get help about Redis commands type:
      "help @<group>" to get a list of commands in <group>
      "help <command>" for help on <command>
      "help <tab>" to get a list of possible help topics
      "quit" to exit

To set redis-cli perferences:
      ":set hints" enable online hints
      ":set nohints" disable online hints
Set your preferences in ~/.redisclirc

```

**help + tab** 查看帮助

获取分组的所有命令

```shell
127.0.0.1:6379> help @string

  APPEND key value
  summary: Append a value to a key
  since: 2.0.0
```

获取命令的帮助

```shell
127.0.0.1:6379> help GET

  GET key
  summary: Get the value of a key
  since: 1.0.0
  group: string
```



**NX后缀:没有出现才设置**     **XX后缀：只能更新**

多个线程使用一个redis的连接；

mset /mget

**正反向索引**

```shell
127.0.0.1:6379> get k1
"hello"
127.0.0.1:6379> APPEND k1 world
(integer) 10
127.0.0.1:6379> get k1
"helloworld"
127.0.0.1:6379> GETRANGE k1 0 -1
"helloworld"
127.0.0.1:6379> GETRANGE k1 5 -1
"world"
127.0.0.1:6379> 
```



```shell
127.0.0.1:6379> SETRANGE k1 5 " xiaoming"
(integer) 14
127.0.0.1:6379> get k1
"hello xiaoming"
127.0.0.1:6379
```





![image-20210326141340640](https://gitee.com/zilongcc/images/raw/master/image-20210326141340640.png)

![image-20210326141453318](https://gitee.com/zilongcc/images/raw/master/image-20210326141453318.png)

**key中有value的类型**

```shell
127.0.0.1:6379> STRLEN k1
(integer) 14
127.0.0.1:6379> get k1
"hello xiaoming"
127.0.0.1:6379> type k1
string
127.0.0.1:6379> set k2 99
OK
127.0.0.1:6379> type k2
string
127.0.0.1:6379>
```

注意：类型都是String

查看encoding

```shell
127.0.0.1:6379> object encoding k2
"int"
127.0.0.1:6379> object encoding k1
"raw"
127.0.0.1:6379>
```

>  object encoding k1

```shell
127.0.0.1:6379> INCRBY k2 1
(integer) 100
127.0.0.1:6379> get k2
"100"
127.0.0.1:6379> DECR k2
(integer) 99
127.0.0.1:6379> INCRBYFLOAT k2 0.3
"99.3"
127.0.0.1:6379> get k2
"99.3"
127.0.0.1:6379> 

```

https://blog.csdn.net/yunzhaji3762/article/details/109402082

![image-20210326141930108](https://gitee.com/zilongcc/images/raw/master/image-20210326141930108.png)

![image-20210326142110370](https://gitee.com/zilongcc/images/raw/master/image-20210326142110370.png)

## 二进制安全[??]

redis存储的是字节数据，没有编码信息 ？？

**字节**

https://www.jianshu.com/p/3e9ba97572ae/

https://segmentfault.com/a/1190000023130486

 ## MSETNX

```shell
127.0.0.1:6379> MSETNX k1 aa k3 44
(integer) 0
127.0.0.1:6379> get k1
"hello xiaoming"
127.0.0.1:6379> get k3
(nil)
127.0.0.1:6379>
```

>  原子，一个失败，所有失败

多连接会怎样 ??

## MYSQL

>  MYSQL 是使用BIO，磁盘IO 是瓶颈，所以不能让多个请求进入

## bitmap

![image-20210326150611639](https://gitee.com/zilongcc/images/raw/master/image-20210326150611639.png)

## 为啥是ASCII，不是UTF-8 ??

``` shell
127.0.0.1:6379> get k1
(nil)
127.0.0.1:6379> setbit k1 1 1 # 设置第一位，为1
(integer) 0
127.0.0.1:6379> get k1
"@"
127.0.0.1:6379> setbit k1 7 1 # 设置第七位，为1
(integer) 0
127.0.0.1:6379> get k1
"A"
127.0.0.1:6379> set k1 9 1
(error) ERR syntax error
127.0.0.1:6379> setbit  k1 9 1 # 设置第九位，为1
(integer) 0
127.0.0.1:6379> get k1
"A@"
127.0.0.1:6379>
```

除了字符集 `ascii`，其他一般叫做扩展字符集

扩展： 其他字符集不在对`ascii`重编码 `0xxxxxxx`

你自己写一个程序，字节流读取，每字节判断

## redis-cli -raw 

似乎是使用客户端的编码解析字节数组，否则使用ascii

## BITCOUNT 

统计1出现次数，start和end指字节，字节 1出现的次数

```shell
127.0.0.1:6379> BITCOUNT k1 0 1
(integer) 3
127.0.0.1:6379> get k1
"A@"
127.0.0.1:6379> BITCOUNT k1 0 0
(integer) 2
127.0.0.1:6379> BITCOUNT k1 1 1
(integer) 1
127.0.0.1:6379> BITCOUNT k1 1 0
(integer) 0
127.0.0.1:6379> BITCOUNT k1 0 1
(integer) 3
127.0.0.1:6379> BITCOUNT k1 0 0
(integer) 2
```

## bitops

![image-20210326151550317](https://gitee.com/zilongcc/images/raw/master/image-20210326151550317.png)

返回二进制位，是全量的，第一个出现的位置

## bitop

```shell
127.0.0.1:6379> HELP BITOP

  BITOP operation destkey key [key ...]
  summary: Perform bitwise operations between strings
  since: 2.6.0
  group: string

127.0.0.1:6379> setbit k2 2 1
(integer) 0
127.0.0.1:6379> BITOP and k3 k1 k2
(integer) 2
127.0.0.1:6379> get k3
"\x00\x00"
127.0.0.1:6379
```

![image-20210329165733213](https://gitee.com/zilongcc/images/raw/master/image-20210329165733213.png)

## bitmap的应用场景

`setbit` `bitcount` `bitops` `bitop` 操作

1. 有用户系统，统计用户登录天数，且窗口随机

MYSQL: 一次登录日志记录至少需要8个字节

Redis：统计用户登录

```shell
127.0.0.1:6379> setbit sean 1 1 # 第一天
(integer) 0
127.0.0.1:6379> setbit sean 7 1 # 第七天
(integer) 0
127.0.0.1:6379> setbit sean 320 1 # 第320天
(integer) 0
127.0.0.1:6379> STRLEN sean
(integer) 41
127.0.0.1:6379> BITCOUNT sean -30 -1 # 后30天出勤天数
(integer) 1
```

**用户为key**

618做活动，登录送礼物，怎么备货

**活跃用户统计**，一段时间登录人

**日期为key**，每个人都是一个bit

```shell
127.0.0.1:6379> setbit 20200101 1 1 # 20200101 第一位表示的人出现
(integer) 0
127.0.0.1:6379> setbit 20200102 1 1 # 20200102 第一位表示的人出现
(integer) 0
127.0.0.1:6379> setbit 20200102 7 1 # 20200102 第七位表示的人出现
(integer) 0
127.0.0.1:6379> bitop or des 20200101 20200102 # 20200101 20200102 出现的人
(integer) 1
127.0.0.1:6379> BITCOUNT des
(integer) 2
127.0.0.1:6379> 
```

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

``` shell
127.0.0.1:6379> lpush kk 2 a 
(integer) 2
127.0.0.1:6379> LRANGE kk 0 -1
1) "a"
2) "2"
127.0.0.1:6379> help LREM

  LREM key count value
  summary: Remove elements from a list
  since: 1.0.0
  group: list

127.0.0.1:6379> LREM kk 1 a
(integer) 1
127.0.0.1:6379> LRANGE kk 0 -1
1) "2"
127.0.0.1:6379>
```

## linsert

``` shell
127.0.0.1:6379> LRANGE kk 0 -1
1) "2"
127.0.0.1:6379> LINSERT kk after  2 00
(integer) 2
127.0.0.1:6379> LRANGE kk 0 -1
1) "2"
2) "00"
127.0.0.1:6379>
```

## llen

``` shell
127.0.0.1:6379> llen kk
(integer) 2
127.0.0.1:6379>
```

# Blpop

多个链接到一个server，如果执行blpop，如果不存在会阻塞

**支持，单播订阅**，FIFO

![image-20210326163957469](https://gitee.com/zilongcc/images/raw/master/image-20210326163957469.png)



一个客户端

``` shell
127.0.0.1:6379> help blpop

  BLPOP key [key ...] timeout
  summary: Remove and get the first element in a list, or block until one is available
  since: 2.0.0
  group: list

127.0.0.1:6379> blpop cc 0
1) "cc"
2) "22"
(4.94s)
127.0.0.1:6379>
```

另一个客户端

``` shell
127.0.0.1:6379> lpush cc 22
(integer) 1
```



## LTRIM

删除两端的元素

# hash

对象存储

```shell
127.0.0.1:6379> set cc:name malome
OK
127.0.0.1:6379> set cc:age 99
OK
127.0.0.1:6379> get cc:name
"malome"
127.0.0.1:6379> get cc:age
"99"
127.0.0.1:6379> keys cc*
1) "cc:age"
2) "cc:name"
127.0.0.1:6379> hset cc name malone
(integer) 1
127.0.0.1:6379> hset cc age 22
(integer) 1
127.0.0.1:6379> hmget name age 
1) (nil)
127.0.0.1:6379> hmget cc age name
1) "22"
2) "malone"
127.0.0.1:6379> hkeys cc
1) "name"
2) "age"
127.0.0.1:6379> HVALS cc
1) "malone"
2) "22"
127.0.0.1:6379> hgetall cc
1) "name"
2) "malone"
3) "age"
4) "22"
127.0.0.1:6379> HINCRBYFLOAT cc age 0.5
"22.5"
127.0.0.1:6379> HVALS cc
1) "malone"
2) "22.5"
127.0.0.1:6379> HINCRBYFLOAT cc age -1
"21.5"
127.0.0.1:6379> hget cc age 
"21.5"
127.0.0.1:6379>
```

**应用场景，需要计算的**

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