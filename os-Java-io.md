

## 课程代码地址

https://github.com/bjmashibing/bjmashbing-sysio

https://medium.com/@copyconstruct/nonblocking-i-o-99948ad7c957

https://medium.com/@copyconstruct/the-method-to-epolls-madness-d9d2d6378642  重点辅助资料



## 寻找面试题



## 内容 

关键字：MMU，

**VFS** ，虚拟的文件系统，类似接口，将上次应用访问存储和实际的存储硬件隔离或者解耦，因为不同的存储系统需要不同的驱动。

**文件系统**，就是应用访问存储设备的接口，是由操作系统实现，应用系统使用的。数据库是这样吗？

VFS 树 inode

1. pageCahe【页缓存】，内存的管理，分页，缓存系统加载的内容，同一份数据，供多个应用访问

2. 从硬盘中加载的数据放置在pagecache中，程序写文件也是先写入到pagecache中，之后根据配置刷新到硬盘中 page的大小为4K,

3. 多个线程共享 pagecache，也就是文件缓存

4. 脏缓存【修改从硬盘中加载的数据】，Flush，什么时候和硬盘进行同步。脏页同步到硬盘，有可能丢数据，即没有处罚阈值和时间

5. linux 系统中将所有设备都抽象成文件，对设备的读写等于对文件的读写，例如网络和硬盘，监听和接受连接都是创建文件fd，文件df，有偏移

6. 文件描述符中维护这对文件操作的状态，例如文件读写的位置等等seek

7. 分区可以挂在到不同的文件夹中【需要详细的实验】，

   1. 分区挂在到一个文件夹，读写
   2. 读写满之后，换一个更大的

8. 几种常见的文件类型

   1. **-：普通文件（可执行，图片，文本）REG**

      d: 目录

      l：连接

      b：块设备，硬盘可以双向读写，

      c：字符设备 CHR，单向，不能来回

      s：socket，

      p: pipeline
      
      [epoolevent]
      
   2. 软连接和硬链接

      1. ln -s

      2. ln  

         > 双方修改都可以看到变化，
         >
         > stat 文件名，查看链接和inode的号

   3. ![image-20200916170750093](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200916170750093.png)

   4. ![image-20200916170858023](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200916170858023.png)

   5. ![image-20200916171013200](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200916171013200.png)

   6. `whereis bash`

   `dd `

   > 运维中的命令

   `ldd bash`

   > 查看bash相关的依赖

   `lsof -op $$ ` 

   > 查看当前进程依赖的文件，打开的文件文件描述符 FD

   ![image-20200917100902719](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200917100902719.png)

   > DF中维护一个seek，指针，每个进程都有，表示文件读到哪了，帮助理解Java中文件读写
   >
   > 一个进程中DF不能重复

   > 每个线程都有以下文件描述符
   >
   > 0 标准输入 1 标准输出 2 错误输出

9. **/proc** 

   `/proc/$$`

   `$$` 表示 当前`bash`的`pid`

    `$BASHPID` 同样表示当前

   `cd /proc/$$/fd`   命令

   `lsof -op $$` 查看当前进程打开的文件

   重定向：不是命令，机制

   `& ` 的作用

   ​		输入，输出 I/O

   ​		<          >

   管道符  | 

一切皆文件，各种文件，文件描述符，socket也是

![image-20200917131220913](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200917131220913.png)

`lsof -op $$` 当前进程的df的详情

`/proc`  **映射内核参数变量**在内存中，一切皆文件

$$ 当前bash的pid

/proc/$$/fd 当前进程的文件描述符

**重定向：不是命令，机制**

`ls ./  1> ~/ls.out`

`read a 0<  cat.out`

`ls ./ /xxxxx 1>ls01.out 2> ls02.out`

`ls ./ /xxx 2>& 1  1> ls04.out`

`ls ./ /xxx   1> ls04.out 2>& 1`

**管道**

`head -1 test.txt`

`tail -1 test.txt`

`head -8 test.txt | tail -1 ` // 获取第8行

**父子进程**

pstree

父子进程访问变量，隔离

{很多指令} // 多恨的指令 ；

![image-20200917133558736](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200917133558736.png)

子进程  | 子进程

管道

进程隔离 ，a 父进程 ，子进程不能影响

![image-20200917133754647](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200917133754647.png)

$$ 的优先级高，比 | 高

$BASHPID进程并不高，正常执行，启动两个子进程

**验证管道类型** 文件类型

![image-20200917134432970](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200917134432970.png)

管道两边会启动两个子进程，上面语句会输出一个pid

在另一个ssh中，cd /proc/pid/df 查看管道

另一个是通过 lsof -op pid 查看pipe

![image-20200917134807285](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200917134807285.png)





![image-20200815075623758](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200815075623758.png)



![image-20200815075853165](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200815075853165.png)

中断，int80 ，态的切换，执行callback，保护现场

pstree

pcstat  /bin/bash

同一程序，多个实例，在page是一份

文件也是共享，多个实例，但是不同的线程，维护自己的seek【fd】，偏移量

**Page cache 是内核维护的中间层**，使用多大内存，是否淘汰，是否延时，是否丢数据

**IO 是依赖pageCache的**，**优化IO性能，但是会丢失数据**,设计内核和硬盘的同步

sysctl -a | grep dirty // 查看配置，os关于刷磁盘的配置

```
vm.dirty_background_ratio = 0
vm.dirty_background_bytes = 1048576
vm.dirty_ratio = 0
vm.dirty_bytes = 1048576
vm.dirty_writeback_centisecs = 5000
vm.dirty_expire_centisecs = 30000
```

**系统文件都是先写内存，再写硬盘**，

**bufferFile 为啥比普通快**

JVM 的维护一个数组8KB，调用一个syscall ，减少系统调用做到的

**page cache 脏页什么时候写入磁盘**

达到系统设置的阈值

**脏page 的淘汰机制**

**strace ，追踪内核调用**

**块设备，文件，map ，来回读，字符设备不能**

**文件系统的IO**

## 第三次 网络IO TCP 参数

```
nc

lsof -p 【】 // 文件描述符

netstat -natp // 

tcpdump 

jps

```

### 抓取数据包的命令

![image-20200815160104045](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200815160104045.png)

客户端和服务端链接

**四元组唯一定义一个TCP 链接**

**客户端链接到服务端，服务端没有接受客户端链接之前，内核已经完成三次握手**

**ServerSoceck 的 accept 是为了在应用层拿到内核分配给链接的FD；用于读写网络数据**



![image-20200815162924041](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200815162924041.png)

**参数backlog**，listen，配置能够接受的链接，不被应用接受，**很重要**

**timeout**，异常，继续，等待客户端，超时，放弃，重新等待

内核参数的配置

**MTU ，TCP 报文大小**

**拥塞控制，窗口，发太多，数据丢失**

简单说，就是双方通信的过程中，会告诉对方，自己还能接受多少，如果太多，可能消息会被丢弃

**keepalive**

## 第三次 网络IO 变化 模型

![image-20200815170309648](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200815170309648.png)

通过strace方式跟踪程序的系统调用

man man

man 2 socket

## 第四次 NIO C10K

route 网络路由

**非阻塞的服务端和客户端的连接**

首先接受客户端的连接不是阻塞的，accept直接返回 fd，但是fd有可能为空，

返回的df也就是客户端，保存，便利读取数据

`strace -ff -o out  java test`  追踪sc

![image-20200815182251291](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200815182251291.png)

既能接受，也能读取

**这些阻塞都是和系统调用有关系的** accept 和 read 阻塞， 不返回

调用阻塞是怎么做到的？？

**阻塞和飞阻塞IO 系统做了什么** 

答:**中断相关**

1. 使用各种IO模型测试

openfile  // 一个进程可以打开的文件

ulimit -a

ulimit -SHn 50000 // 设置每个

---

**都是和OS相关的**

各种IO 模型和客户端简历连接

BIO NIO 多路复用 

总结：

主要是通过演示从客户端发起11W个连接，到服务端，服务端，使用不同网络IO 模型和客户端创建连接，感性的看看创建连接的快慢，比较不同的网络

BIO : 阻塞当前线程accept和read ，所以需要多线程

NIO ：不阻塞当前线程，直接返回，client，accept，便利创建的client，读取数据

多路复用  ：还未讲，但是最快，基于事件或者epoll模型的

NIO 问题在哪

## 第五次 网络编程之多路复用器及Epoll精讲

NIO 的问题在哪

一次系统调用，知道多路IO的情况

**同步转异步**

总结

从底层，阐述多路复用的原理，简单总结

就是将原来的用户控件做的，关于便利，那个fd有事件过来的动作，放到了内核态

包括在内核中维护，关于df的红黑树，cp有状态的df，等待app 调用 epool_wait ，获取有状态的fd

接着，继续执行rw

这个阶段叫做IO thread ，读到数据之后，可以使用其他的线程的处理

## 第六次 全手写急速理解Netty模型及IO模型应用实战



HMS 

hilink



## 第七次 Netty之IO模型开发本质手写部分实现推导篇 

ByteBuf

什么是selector 

channel

**听不太明白**



## 第八次 全手写基于Netty的RPC框架自定义协议，连接池

























--------



简历可以添加精通IO





##  问题



### 内存碎片化是什么意思

### MMU page 映射 

### 缺页异常处理过程

### 80 中断、上下文切换具体做了什么

### 操作加载和执行程序的过程



中断之后，内核处理，增加虚拟和物理的映射，用户态继续执行





## 推荐书籍

![image-20200815095312005](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200815095312005.png)