# 日常规划

数据结构和算法

Java语言基础和高级特性

JVM 深入

多线程和高并发

Spring 技术栈



## 网上课程

https://www.julyedu.com/  算法培训



## 关于不同语言或者程序之间调用，或者A语言给B语言提供接口

A语言给B语言提供接口是怎么实现的

JVM 是用C++语言实现，但是提供了Java API

JS 引擎是C++语言实现的，但是提供了 JavaScript API

很多框架都支持，多种语言，例如 Spark 、tersorflow等

---

以上是问题

----

下面是自己的理解，主要来自 https://zhuanlan.zhihu.com/p/63545981  浅谈 Spark 的多语言支持，所以Spark没有使用C++实现，所以他们不能很方便的集成tersorflow这类的框架，**好像不对** 等等，还需要自己看看文章；

文章是阿里团队的作品，好像是要重新写Spark，让他更好的支持机器学习等

在看看文章

大多数编程语言都有对C/C++的调用的支持

多以如果一个库如果使用C/C++编写，那他就是支持多语言的

Java golang swift dart JavaScript 