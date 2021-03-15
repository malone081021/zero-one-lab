X设计一致性

分区的意义

分区内部有序，分区外部无序

offset 标识消费到哪了

zookeeper 协调者，不是存储

协调选主的过程用到了zookeeper

produce 获取broker ip

多个produce

分区和consumer 

分组概念，组内是不能1 -> N

但是在不同组中，能将一个p 到不同的组的consumer


![image.png](https://gitee.com/zilongcc/images/raw/master/d34b5b8463aa40e5b71e647fc6350448~tplv-k3u1fbpfcp-watermark.image)

消费的端点，或者业务的部门 MYSQL 和  ES

在一个业务线，中不能有多个consumer

但是在不同业务线，可以重复利用，是在不同组上，但是在组内不能重复

---

**消费的进度，重复消费，数据丢失**

> 围绕的是offset出现

出现问题之前offset是怎么维护的，consumer在运行时在内存中维护了自己的offset,在内存中，**offset需要持久化**;

> 老版本维护到zk中，但是zk是协调，不维护业务内容

后期的版本中自己维护了一个topic，维护offset，自己维护offset的topic

offset存储的时机，节奏，导致不同的问题

**丢失**：offset存储成功，但是业务执行失败
**重复**：异步同步offset，重复消费，幂等解决，**但是幂等解决不了数据丢失**


![image.png](https://gitee.com/zilongcc/images/raw/master/40679634ba2c4e18aec639df694d8f7d~tplv-k3u1fbpfcp-watermark.image)