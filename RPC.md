RPC

## 关键词

1. 基础的网络知识
2. netty  IO thread 线程模型 异步 锁 队列 
3. reactor、 Flux、mono、
4. reactor-netty
5. 超时处理、责任链、SPI、上线文 、ThreadLocal、透传
6. 报文、协议、TCP、连接管理、连接池、
7. 负载均衡、路由、注册中心、命名服务
8. 参考 dubbo、sofarpc、grpc

## netty

RPC 包括网络通信和服务治理

网络通信的基础就是netty，所以netty是RPC的基础

### 现在了解的知识

1. 简单了解IO 模型

其中有EventLoop，代表一个Thread，一个Selector，管理IO事件，将channel注册到Selector上，Thread不停的轮训的获取已经发生事件的Channel，处理

> 1. 空轮训Bug没看懂，啥事空轮训，CPU还是100%
> 2. 一个Channel就是一个客户端连接，这个客户端连接建立之后，之后的所有的数据的读写都是在这个Channel上发生的，包括业务代码，注意当前是也线程，没有阻塞连接建立的线程。

### 希望了解的知识

1. IO 线程和业务线程之间的交互过程
2. netty启动的流程，已经了解，但是需要进一步总结
3. pipeline的构造和执行过程
4. netty中的定时任务
5. netty中IO事件和业务代码执行的过程

## reactor-netty

是在netty的基础上增加了响应式编程的功能，具体是怎么怎样的？

### 现在了解的知识

1. 基础是reactor框架，反应式编程框架的实现
2. 简答的翻阅了源码，能找到一些reactor和netty结合的影子

> 1. 知识在配置方面，能看到收集相关的信息，收集的方式比较奇特
> 2. 端口bind还没有分析

## 网络编程

设计Java基础的soket编程，NIO等

了解了NIO和BIO的区别，

1. 知道NIO是非阻塞同步IO，NIO代码不阻塞当前线程，同步是数据返回后还是需要用户自己完成后续的处理

2. 知道主要是通过Selector管理Channel，减少系统调用和数据复制提高效率

## RPC实现主要设计的技术

1. 接口、动态代理
2. 网络通信、消息的收发、报文解析，反射，接口实现调用
3. 序列化，网络传输的需要
4. 网络协议
5. 连接管理、超时管理

## 总结

netty 是重点，通信主要靠netty，通信是基础；

所以网络编程和多线程是基础

高阶是服务治理


## netty 启动

1. 阻塞 = 等待，非阻塞 = 不等待，已经准备好了
2. 异步，不是当前线程做
3. netty EventLoop 强调的是非阻塞，事件来自 selector，分发到一个线程处理

---

1. 创建配置
2. 创建Channel
3. 配置Channel，绑定EventLoop，绑定pipeLine
4. bind端口，监听客户端连接
5. 等待不能退出

---

eventLoop 事件循环

1. 循环selector中的事件 

   >  在run中selector.select

2. 将创建的Channel注册到eventLoop上

3. 处理IO事件和用户添加的事件

4. NioEventLoop 就是多线的，注册Channel到eventLoops时，会选择一个EventLoop注册

---

分析 Netty 死锁异常 BlockingOperationException

http://www.linkedkeeper.com/1027.html

---

Future 和 Promise 

1. 表示一个未来的结果

2. 可以在上注册感兴趣的事件

3. 可以获取异步任务的状态

> 底层就是线程通信，await notify，通过一个对象同步



---

看源码，抓住主线

说的很形象，但是不能落实到代码

reactor flux 的配置的过程，就是一个个参数嵌套 ？？ f(g(x))

对象是存储指令的容器  thread

生产者 和 消费者，之间增加 加工者，修饰的是生产者还是消费者

链式编程和函数式 只关注动作

链接受的解释自己，上下文统一

---

定义的接口和名词的意义是什么 ？ 目标，表达需求，模拟生活，怎么实现我不管，这样对我们有好处

具体怎么实现，需要编程功底，使用现有的编程技术

各种操作是怎么串联起来的

reactor 的push和pull是怎么实现的？

> push就是就是调用订阅者的onNext方法，push，但是pull呢，所以出现了subscription，中间的，代理publish和订阅者

函数、对象、接口的混合

# AtomicReferenceFieldUpdater 

的使用方法

**背压**是在生产端的

方法调用 vs 元素下发

-----



1. 因为阻塞所以多线程
2. 但是线程数有限，而且大部分时间都在空闲

> 如果继续提升应用的性能，让线程跑起来，不空闲

1. 异步非阻塞
2. 响应式

---

1. 反应式和异步的关系
2. 




## 知识点

1. 动态代理生成接口对象
2. RPCcontext，接受用户参数
3. 组织调用链，在真正出发代理对象前的其他的动作

## 为什么阅读源码

1. 深入了解，以防可能得不适，扩展
2. 深入了解，以防有bug

## 怎样阅读源码

1. 先看文档
2. 运行demo或者test
3. 有问题，从现在知识出发，不能明白或者对实现细节感兴趣

> 重点是了解整个框架的基本原理，和流程，
>
> 知道自己现在关心的问题出在那个阶段

