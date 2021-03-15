![image-20210307142314709](C:\Users\mark2\AppData\Roaming\Typora\typora-user-images\image-20210307142314709.png)

![image-20210307142323108](https://gitee.com/zilongcc/images/raw/master/image-20210307142323108.png)

![image-20210307142348953](C:\Users\mark2\AppData\Roaming\Typora\typora-user-images\image-20210307142348953.png)

![image-20210307142359276](https://gitee.com/zilongcc/images/raw/master/image-20210307142359276.png)

## 执行引擎执行字节码的方式

**字节码解释器**

Java代码 Java字节码  -> C++ 代码 -> 机器码

**模板解释器 JIT一部分**

Java 字节码 - > 机器码

> Java是半编译半解释型语言

触发及时编译，放在方法区，热点代码缓存

> 分层编译

![image-20210307143400318](https://gitee.com/zilongcc/images/raw/master/image-20210307143400318.png)



![image-20210307143444881](https://gitee.com/zilongcc/images/raw/master/image-20210307143444881.png)



![image-20210307143622249](https://gitee.com/zilongcc/images/raw/master/image-20210307143622249.png)



## 问题
1. 一个简单的多线程问题，没有按照我们预期执行，例如，多线程加数，但是给加数字段count添加volatile就可以了
2. 基于以上问题，知道了多线程中，可见性问题和重排序问题
3. `可见性`问题也是锁实现的基础,所以并发的基础就是了解可见性问题
4. 问题引入的原因，中和CPU和内存访问的速度差异，引入缓存，缓存导致数据的不一致性，尤其是在多核环境中


## 重排序的意义


## 内存屏障
CPU 内部的并发、缓存等问题，导致可见性问题

https://zhuanlan.zhihu.com/p/208788426

**内存屏障的由来**
1. 硬件的bug,优化过头，在某种场合和预期不一致；只能靠你弥补，问题是了解这个系统的运作的原理，以及补救措施
2. 解决一个问题，遇到了另一个问题。

对于CPU的写，目前主流策略有两种：

1、write back：即CPU向内存写数据时，先把真实数据放入store buffer中，待到某个合适的时间点，CPU才会将store buffer中的数据刷到内存中，而且这两个操作是异步的。这在多线程环境中，有些情况下是可以接受的，但是有些情况是不可接受的，为了让程序员有能力根据业务需要达到同步完成，就设计了内存屏障。

2、write through：即CPU向内存写数据时，同步完成写store buffer与内存。

当前CPU大多数采用的是write back策略。可能有童鞋要问了：为什么呢？因为大多数情况下，CPU异步完成写内存产生的部分延迟是可以接受的，而且这个延迟极短。只有在多线程环境下需要严格保证内存可见等极少数特殊情况下才需要保证CPU的写在外界看来是同步完成的，需要借助CPU提供的内存屏障实现。如果直接采用策略2：write through，那每次写内存都需要等待数据刷入内存，极大影响了CPU的执行效率。

`内存屏障`，多线程中让修改立即让其他线程可见，保证强一致性

 `重排序`，`禁止重排序`

 `三级缓存`

 `MESI 缓存一致性协议`

 `store buffer`

 内存屏障指令就是告诉CPU采取什么模型，运行行当前指令；`不能并发乱序执行了`

 线程是通过内存或主存储器交流信息，所以要可见性，执行保证写会主存储器


 ## 减少共享变量

## 工具

`hsdis`

机器码是如何运行的
字节码是如何运行的

执行引擎
垃圾回收器

`jclassLib`

硬编码 机器码

`lldb`

**C1编译器**，需要收集的数据比较少，编译优化做的比较浅
生成的代码执行效率不高
编译消耗CPU资源没有C2高

**C2编译器**，收集数据多，优化多，CPU消耗多，执行效率高

分层编译器，系统启动是使用C1,热机之后使用C2

阿里案例：热机迁移冷机案例

不能以热机能抵抗的流量估算冷机;因为热机已经触发了JIT

**触发JIT的条件**

- 方法的执行次数
	- server 编译器模式下，N 默认的值是 10000
    client 编译器模式下， N 默认的值 1500 
    Java -client -XX:+PrintFlagsFinal -version | grep CompilreThreshold
- 循环的执行次数

**热度衰减**
	执行之后，歇了一段时间，2倍衰减，3500 1700
    

**热点代码缓存 方法区**

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/fd3192aa4e56493e9fbda78c16fc590b~tplv-k3u1fbpfcp-watermark.image)

**及时编译器执行方式**

将这个及时编译请求写入队列， VM_THREAD,异步

有多少工作线程

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/4490f8bb147c40938729901a3afed4bc~tplv-k3u1fbpfcp-watermark.image)

**逃逸分析**

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/86b8545006174d3ea966fef1616acd74~tplv-k3u1fbpfcp-watermark.image)
**锁消除**

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/80aa30e59b644908b1f3cfd4f2684edf~tplv-k3u1fbpfcp-watermark.image)

**标量替换**
 标量，基本数据类型，不可再分
 聚合量，可再分，引用类型

 ![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/06beacce58e648a9b4ce2b1542999832~tplv-k3u1fbpfcp-watermark.image)

 **Java 中的数组是静态类型还是动态数据类型**

 newarray  生成基本类型数组 typeInstance
 anewarray 生成引用类型数组 typeObjectInstance

**栈上分配**

可以通过，HSDB 统计在对上创建的对象。但是对象受GC的影响

**HSDB**

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2cb6660fcdf04d999f51321492ebb197~tplv-k3u1fbpfcp-watermark.image)



## 参考

- https://www.jianshu.com/p/467141dd1fb9 JVM之模板解释器
- https://zhuanlan.zhihu.com/p/102214508 深入浅出 JVM 系列（一）初识 JVM
- 虚拟机设计与实现:以JVM为例 pdf
- https://gitee.com/luban-ziya 子牙老师 gitee
- https://www.douban.com/doulist/2545443/ **从表到里学习JVM实现**
- https://zhuanlan.zhihu.com/p/208788426  **Volatile：内存屏障原理应该没有比这篇文章讲的更清楚了**
- https://zhuanlan.zhihu.com/p/55767485 **为什么需要内存屏障**
- https://book.douban.com/subject/6957175/ **Is Parallel Programming Hard, And, If So, What Can You Do About It?**
- https://zhuanlan.zhihu.com/p/125737864 **内存屏障（Memory Barrier）究竟是个什么鬼？**
- https://xie.infoq.cn/article/02efccc7d86788e383dee8659 **从 JMM 透析 volatile 与 synchronized 原理**
- https://zhuanlan.zhihu.com/p/137193948 阿里面试官没想到，一个Volatile我能跟他扯半个小时
- https://zhuanlan.zhihu.com/p/102316535 JVM虚拟机栈执行原理深入详解
- https://blog.csdn.net/molixuebeibi/article/details/91867549 简析Go与Java内存管理的差异
- https://zhuanlan.zhihu.com/p/125737864 内存屏障（Memory Barrier）究竟是个什么鬼？
- https://mp.weixin.qq.com/s?__biz=MzU5MTg2OTc3Ng==&mid=2247483717&idx=1&sn=41f10e428eb6ee683f3b4dd9dd025742&chksm=fe29237ac95eaa6c9492ded3258a90de4f02343a2ee56839d4cdaa58f4427f84a622ba75770b&token=1601845131&lang=zh_CN#rd 带你了解缓存一致性协议 MESI
- https://www.jianshu.com/p/64240319ed60 **一文解决内存屏障**

> **序列缓冲区** 概念但是其他的文章并没有提到，其他

- https://www.bilibili.com/video/BV1e64y1T7J3 **CPU Cache（CPU 缓存）基础解析**
- https://www.bilibili.com/video/BV1X54y1Q75J **内存屏障及其在 JVM 内的应用**
- https://xie.infoq.cn/article/680fd531df57856ddcb532914 **JMM 的前世今生**
- https://xie.infoq.cn/article/8b877b5fbe755c382fcee8dddJava **并发编程之 JMM & volatile 详解**
- https://xie.infoq.cn/article/d9479ba8900bd7645c035d006 关于 Synchronized 的一个点，网上 99% 的文章都错了

![图片](https://gitee.com/zilongcc/images/raw/master/640)

指令重排序是为了防止CPU或者编译重排序，导致执行的结果和预期不一致



## 内存屏障

特殊的指令，功能是在这个指令之前的对内存变量的操作结果，要对其他的线程可见，也就是要刷新到内存中；原来是在buffer store中。

分几种；？？

出现的背景

多核CPU在多线程中，缓存不一致性



## MESI 

缓存一致性协议

![image-20210307184644958](https://gitee.com/zilongcc/images/raw/master/image-20210307184644958.png)

   

![image-20210307184730140](https://gitee.com/zilongcc/images/raw/master/image-20210307184730140.png)

![image-20210307184901073](https://gitee.com/zilongcc/images/raw/master/image-20210307184901073.png)

![image-20210307185842890](https://gitee.com/zilongcc/images/raw/master/image-20210307185842890.png)

![image-20210307190253422](https://gitee.com/zilongcc/images/raw/master/image-20210307190253422.png)

![image-20210307190842363](https://gitee.com/zilongcc/images/raw/master/image-20210307190842363.png)

![image-20210307191250000](https://gitee.com/zilongcc/images/raw/master/image-20210307191250000.png)

![image-20210307191422256](https://gitee.com/zilongcc/images/raw/master/image-20210307191422256.png)

![image-20210307191513891](https://gitee.com/zilongcc/images/raw/master/image-20210307191513891.png)

CPU0 可以读到CPU1 上的cache line

![image-20210307193401128](https://gitee.com/zilongcc/images/raw/master/image-20210307193401128.png)

![image-20210307193625846](https://gitee.com/zilongcc/images/raw/master/image-20210307193625846.png)

---

- 高级语言的原子性怎么保证

Happens-Before 规则如何理解 Happens-Before 呢？

如果望文生义（很多网文也都爱按字面意思翻译成“先行发生”），那就南辕北辙了，Happens-Before 并不是说前面一个操作发生在后续操作的前面，它真正要表达的是：前面一个操作的结果对后续操作是可见的。就像有心灵感应的两个人，虽然远隔千里，一个人心之所想，另一个人都看得到。
Happens-Before 规则就是要保证线程之间的这种“心灵感应”。所以比较正式的说法是：Happens-Before 约束了编译器的优化行为，虽允许编译器优化，但是要求编译器优化后一定遵守 Happens-Before 规则。

```java 

// 以下代码来源于【参考1】
class VolatileExample {
  int x = 0;
  volatile boolean v = false;
  public void writer() {
    x = 42;
    v = true;
  }
  public void reader() {
    if (v == true) {
      // 这里x会是多少呢？
    }
  }
}
```
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7d30288f0d5e45ec90a03eb4a797e751~tplv-k3u1fbpfcp-watermark.image)
# 课后思考
不久前听说小明要做一个询价应用，这个应用需要从三个电商询价，然后保存在自己的数据库里。核心示例代码如下所示，由于是串行的，所以性能很慢，你来试着优化一下吧。

```java 
// 向电商S1询价，并保存
r1 = getPriceByS1();
save(r1);
// 向电商S2询价，并保存
r2 = getPriceByS2();
save(r2);
// 向电商S3询价，并保存
r3 = getPriceByS3();
save(r3);
```

``` java 
	ExecutorService futuresPool = Executors.newFixedThreadPool(3);
        Future<Price> future1 = futuresPool.submit(this::getPriceByS1);
        Future<Price> future2 = futuresPool.submit(this::getPriceByS2);
        Future<Price> future3 = futuresPool.submit(this::getPriceByS3);

        ExecutorService saveThreadPool = Executors.newFixedThreadPool(3);
        futuresPool.execute(() -> {
            try {
                Price r1= future1.get();
                save(r1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        });
        futuresPool.execute(() -> {
            try {
                Price r2= future2.get();
                save(r2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        });
        futuresPool.execute(() -> {
            try {
                Price r3= future3.get();
                save(r3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
```