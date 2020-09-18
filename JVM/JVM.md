## JVM 概述

![image-20200906094958623](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906094958623.png)

![image-20200906095750558](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906095750558.png)

![image-20200906100113848](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906100113848.png)



## Class File Format

![image-20200906101940892](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906101940892.png)

## jclasslib

![image-20200906102311611](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906102311611.png)**重点使用**，查看class 文件的信息，

## 常量池 是什么， 存储的内容是什么

# 第二课

![image-20200906122831010](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906122831010.png)

![image-20200906123001449](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906123001449.png)

![image-20200906123102832](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906123102832.png)

## 类加载的过程

![image-20200906140446187](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906140446187.png)

## 为什么需要双亲委派

1. 安全问题
2. 资源浪费

## lautcher 类

1. 各种JDK自带的加载器， app ext等等
2. 

## 自定义类加载器

> 可以使用classloader **加载资源**，文件

热部署，自动加载

重写**findclass**，

![image-20200906143451375](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906143451375.png)

## 双亲委培被破坏【百度】

## 懒加载

![image-20200906145019021](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906145019021.png)

加载的时机 ？？

**通过中例子观察类被加载**

## 编译



![image-20200906145421832](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906145421832.png)

编译和启动的模式不同

`-Xcomp `纯编译执行

`-Xint ` 解释执行

## 什么时候注入的加载器的父加载器

重写构造函数

**metespace中的class对象也是会被回收**



## 关于热点代码，检测参数

![image-20200906150733192](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906150733192.png)

# 第三课

![image-20200906152755852](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906152755852.png)

类加载，一个类的多个版本的加载需要重写**classload**方法

## class 的加载，初始化



![image-20200906153903606](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906153903606.png)

**重点是，prepare，初始值  和 initial，初始化 ，**

## new Object（）问题

1. 内存初始化，默认值

2. 初始化

   > 例子，单例，双检查

![image-20200906154718901](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906154718901.png)

## 硬件层数据一致性

![image-20200906155240017](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906155240017.png)

![image-20200906155412356](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906155412356.png)

为了解决，读取速度的差异，引入缓存，**但是导致了数据不一致问题**



![image-20200906155845456](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906155845456.png)

MESI ,缓存锁

总线锁

## 缓存行

![image-20200906160210729](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906160210729.png)

缓存行对齐，浪费空间，**但是避免假共享**

## 合并写

## 乱序执行

## 内存屏障

![image-20200906164958848](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906164958848.png)

![image-20200906165106301](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906165106301.png)

**硬件级别是如何保证有序执行** :内存屏障、lock

**JVM 是如何规定有序执行**

# 第四课

硬件中是，x86中有序性的实现

**重点理解内存屏障意思**

![image-20200906170617395](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906170617395.png)

## volitile 是怎么实现的有序性

![image-20200906171106560](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906171106560.png)

**规范，抽象**，都是从实践中来，底层的实现可以变化

## volatile的实现细节

1. 字节码层面 ACC_VOLATILE

2. JVM层面 volatile内存区的读写 都加屏障

   > StoreStoreBarrier
   >
   > volatile 写操作
   >
   > StoreLoadBarrier

   > LoadLoadBarrier
   >
   > volatile 读操作
   >
   > LoadStoreBarrier

3. OS和硬件层面 https://blog.csdn.net/qq_26222859/article/details/52235930 hsdis - HotSpot Dis Assembler windows lock 指令实现 | MESI实现

## synchronized实现细节

1. 字节码层面 ACC_SYNCHRONIZED monitorenter monitorexit
2. JVM层面 C C++ 调用了操作系统提供的同步机制
3. OS和硬件层面 X86 : lock cmpxchg / xxx [https](https://blog.csdn.net/21aspnet/article/details/88571740)[://blog.csdn.net/21aspnet/article/details/](https://blog.csdn.net/21aspnet/article/details/88571740)[88571740](https://blog.csdn.net/21aspnet/article/details/88571740)



规范，但是如何实现，就是还是使用CPU提供能力，**在有写时候需要防止乱序执行**

![image-20200906173727265](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906173727265.png)

![image-20200906174012538](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200906174012538.png)



----



抽象的好处

- 屏蔽一些细节
- 好管理
- 好理解，总体



# 参考

# 工具

1. `jclasslib` idea  plugin 观察class 文件内容
2. 