![image-20200911131339643](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200911131339643.png)

![image-20200911131713569](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200911131713569.png)

**面向连接**

三次握手，

---

面向连接，应用层先不要发，由连接控制层创建连接，在开发是发，三次握手，开辟对对方服务的资源，

可靠性， 发送过程确认机制，体现可靠性，发送确定机制

---

四次分手，

netstat  -natp 

![image-20200911133751572](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200911133751572.png)

socket  套接字 ip:port, 65535

服务端不收端口号的限制

客户端占用端口，分手，释放端口

**四次分手**

1 fin 2

1 ack 2

1 fin 2

1 ack 2

---

确认四次，

![image-20200911134402961](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200911134402961.png)

客户端端口监听，演示tcp通信，**三次握手，四次分手**

---

**传输控制层，在内核**，是为应用层服务，创建连接

传输完成交给应用层，HTTP 等解析使用

route -n 路由表

---

服务端监听端口，listen accept ，等待客户端的链接，系统调用 **sokect bind listen accept**

当客户端链接到来是，返回fd 一个新的文件描述符，read系统调用读取文件

通过时通过多线程，来一个客户端链接，抛出一个线程，read

![image-20200911141050387](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200911141050387.png)



man 2 read  查看文档

非阻塞，不阻塞主线程，

select 在内核循环那些客户端可用，返回可用fd，

同步模型，自己发起的，自己处理

如果自己发起，内核处理，异步，就是直接等结果

多路复用，多个客户端直接由一个线程处理