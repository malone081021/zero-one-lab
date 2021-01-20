在 Dubbo 中会抽象出一个“端点（Endpoint）”的概念，我们可以通过一个 ip 和 port 唯一确定一个端点，两个端点之间会创建 TCP 连接，可以双向传输数据。Dubbo 将 Endpoint 之间的 TCP 连接抽象为通道（Channel），将发起请求的 Endpoint 抽象为客户端（Client），将接收请求的 Endpoint 抽象为服务端（Server）。这些抽象出来的概念，也是整个 dubbo-remoting-api 模块的基础，下面我们会逐个进行介绍。



```java 
@SPI("netty")
public interface Transporter {
    @Adaptive({Constants.SERVER_KEY, Constants.TRANSPORTER_KEY})
    RemotingServer bind(URL url, ChannelHandler handler) 
        throws RemotingException;
    @Adaptive({Constants.CLIENT_KEY, Constants.TRANSPORTER_KEY})
    Client connect(URL url, ChannelHandler handler) 
        throws RemotingException;
}
```



`@Adaptive` 注解的出现表示动态生成适配器类

`@SPI`注解，它是一个扩展接口

![Lark20200922-162354.png](https://s0.lgstatic.com/i/image/M00/55/09/CgqCHl9ptlyABsjpAAGGk7pFIzQ293.png)

---



堆外内存被JVM 回收的过程 ？？

ByteBuf  引用计数 内存释放过程及问题？？

netty 解决的问题都是性能的问题 ？？ 线程模型、buffer、内存管理

并发编程 + 网络 + JVM

--------------



```java
Future FutureTask
```















