



# 内核中PageCache、mmap作用、Java文件系统IO、NIO、内存中缓冲区作用

## 面试题：

epoll 是怎么知道数据到达的？ epoll  < 怎么知道数据到达 ,到最后也没有回答

## 前置知识



![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616091946299.png)

> 推荐图书 《深入理解linux内核》《深入理解计算机系统》

**线性地址和物理地址的映射**
程序在物理上是不连续的
程序在运行的时候有虚拟地址，是线性地址空间
映射依赖于CPU的MMU单元，以页（4KB）为单位
程序运行的时候，可以预分配一些空间，但不会做全量分配。如果程序跑着跑着，想要访问一个地址的时候发现没有，此时会产生缺页异常，一个软中断，CPU去把缺的页补上之后，从内核态回到用户态，才能继续运行。

**程序跑起来的步骤：**

- 程序是硬盘上的一个文件，二进制的，包括代码段，假设整个程序运行起来需要用到 `10*4kB `空间*
- *程序运行时，可能是先加载 `1*4kB` 进来，后面用到的时候再加载其他的 `4kB`（并不是一口气全部加载进来了）
- 两个进程同时访问同 1 个文件时，共享的是这 1 个文件的唯一的`pagecache`，只不过是两个进程里面的`fd`各自维护了自己的不同的访问位置偏移量。这样是比较省内存的。在fork子进程的时候，只需要开辟一个线性的地址空间，然后将PCB创建出来，实际上fd中指向的还是同一个文件。

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616091918200.png)

比如，bash 是一个程序

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616095152279.png)

**pagecache使用多大内存？是否淘汰？（一致性问题）是否延时/丢数据？**

## 搞一些事情

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616110251206.png)

查看pageCache默认的配置项

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616100151784.png)

**修改如下 重点理解**

```shell
vm.dirty_background_ratio = 90
(后台异步执行)当文件系统缓存脏页数量达到系统内存百分之多少时（如90%）就会触发pdflush/flush/kdmflush等后台回写进程运行，将一定缓存的脏页异步地刷入外存，由内核完成从内存到磁盘的写过程，才会真正写入硬盘

vm.dirty_ratio = 90
(前台执行)当文件系统缓存脏页数量达到系统内存百分之多少时（如90%），系统不得不开始处理缓存脏页（因为此时脏页数量已经比较多，为了避免数据丢失需要将一定脏页刷入外存）；在此过程中很多应用进程可能会因为系统转而处理文件IO而阻塞

上面这两个达到阈值之后，都会使用LRU策略进行淘汰

vm.dirty_expire_centisecs = 30000 过期时间，单位：10ms
vm.dirty_writeback_centisecs = 5000 脏页写回的时间频率，单位：10ms
```



`vm.dirty_background_ratio = 90`
(后台异步执行)当文件系统缓存脏页数量达到系统内存百分之多少时（如90%）就会触发pdflush/flush/kdmflush等后台回写进程运行，将一定缓存的`脏页`异步地刷入外存，由内核完成从内存到磁盘的写过程，才会真正写入硬盘

`vm.dirty_ratio = 90`
(前台执行)当文件系统缓存脏页数量达到系统内存百分之多少时（如90%），系统不得不开始处理缓存脏页（因为此时脏页数量已经比较多，为了避免数据丢失需要将一定脏页刷入外存）；在此过程中很多应用进程可能会因为系统转而处理文件IO而阻塞

上面这两个达到阈值之后，都会使用`LRU策略进行淘汰`

`vm.dirty_expire_centisecs = 30000` 过期时间，单位：10ms
`vm.dirty_writeback_centisecs = 5000` 脏页写回的时间频率，单位：10ms



修改完之后用`sysctl -p`让它生效
写一个mysh小脚本，待会儿使用的时候给 `$1` 传参数

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616103102610.png)

OSFileIO.java

```java 
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class OSFileIO {

    static byte[] data = "123456789\n".getBytes();
    static String path =  "/root/testfileio/out.txt";


    public static void main(String[] args) throws Exception {
        switch ( args[0]) {
            case "0" :
                testBasicFileIO();
                break;
            case "1":
                testBufferedFileIO();
                break;
            case "2" :
                testRandomAccessFileWrite();
            case "3":
//                whatByteBuffer();
            default:
        }
    }


    //最基本的file写
    public static  void testBasicFileIO() throws Exception {
        File file = new File(path);
        FileOutputStream out = new FileOutputStream(file);
        while(true){
            Thread.sleep(10);
            out.write(data);
        }
    }

    //测试buffer文件IO
    //  jvm  8kB   syscall  write(8KBbyte[])
    public static void testBufferedFileIO() throws Exception {
        File file = new File(path);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        while(true){
            Thread.sleep(10);
            out.write(data);
        }
    }
// ...
}
```

#### 第一次测试

执行刚才的shell脚本`mysh`，传入参数`0`

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616104117735.png)

只要内存够，cache就一直给你放到缓存

>  注：下面这个pcstat命令需要一个二进制文件pcstat，把它下载下来放进环境变量自带的 /bin 目录下就可以了

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616103514735.png)

此时强制关机拔电源

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/2020061610430271.png)

重启之后，刚才在缓存中的数据丢失了，因为并没有来得及刷写到磁盘中。

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616104714252.png)

简单面试题

1、BufferedIO 和普通的IO谁快？BufferedIO
2、BufferedIO 为什么快？BufferedIO 在 JVM 的进程中默认暂存一个 8kB 的数组，8kB满了之后，调用内核中的 syscall write，把这个数组写进去。BufferedIO 与普通的IO在内核中切换的次数不一样。

#### 第二次测试

执行刚才的shell脚本`mysh`，这次传入的参数是`1`
看文件增长的速度可以发现，BufferedIO 明显比第一次测试使用的普通 IO 文件增长的速度快，这也是为什么我们在使用 Java 进行文件IO的时候，一定要用 BufferedIO。

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616105136217.png)

超过90%内存的阈值后，一部分数据已经被持久化了

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616105154632.png)

将原有的 out.txt 改名为 ooxx.txt（修改文件名只是修改了元数据，不会影响缓冲区中的内容）
再重新跑起 mysh 脚本，我们发现，根据LRU策略，新创建的 out.txt 将原有 ooxx.txt 在缓冲区的数据淘汰掉了

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616110119617.png)

#### 第三次测试

我们的内存总共有3个多G
在 out.txt 大于 900M 的时候**关电源**

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616110400348.png)

再开机之后，out.txt 都丢了

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616110613851.png)

这么看来，如果你把redis或mysql的持久化规则设置为**跟随操作系统**的话，当你突然断电的时候，你会丢失很多数据。所以他们是怎么做的呢？？

//TODO

#### 我们的一些结论

**什么是脏数据？**
可以理解为**缓存中的数据**与**磁盘中的数据**不一致的时候，这个数据就是脏的

- 一个页刚被create的时候，是脏的
- 这个页被写到磁盘中后，就不是脏数据了
- 又修改了这个页中的内容，又回到了脏的状态

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616153405802.png)

硬件、内核、进程都有缓存，这三个缓存都有可能丢数据。

pagecache本来是用来优化IO的性能（优先走内存），但它的缺点是刷写硬盘不及时，在突然关机或异常断电时，有丢失数据的可能

## 为什么Java程序员不要使用直接IO，而要使用Buffered形式的IO？

**使用直接IO**
这里截取的是OSFileIO.java的一部分

```JAVA 
    //普通IO：最基本的file写
    public static  void testBasicFileIO() throws Exception {
        File file = new File(path);
        FileOutputStream out = new FileOutputStream(file);
        while(true){
            Thread.sleep(10);
            out.write(data);
        }
    }
```

查看strace产生的out文件，追踪的是应用程序到内核的系统调用。每一行都是用户态到内核态切换的过程。
所以为什么说直接IO比而要使用BufferedIO速度更慢，就是因为每次只写一个`123456789`，进行了过多的从用户态到内核态的切换。

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616121448203.png)

**使用BufferedOutputStream**
这里截取的是OSFileIO.java的一部分

```java
//测试buffer文件IO
//  jvm  8kB   syscall  write(8KBbyte[])
public static void testBufferedFileIO() throws Exception {
    File file = new File(path);
    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
    while(true){
        Thread.sleep(10);
        out.write(data);
    }
}
```
通过追踪可以看出，一次写一个缓冲区大小，减少了调用write的次数，只不过是每一次写入的数据量比较大。减少了用户态到内核态的来回切换。
因此我们说，BufferedOutputStream它之所以快，根本原因并不是因为使用了缓冲，而是因为使用Buffer让数据能够批量写入，**减少系统调用带来的内核切换导致的性能损耗**。

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616122045498.png)

## 测试 NIO

ByteBuffer
position:偏移量，默认指向0
flip: 让position归零，limit移到前面
limit：读状态时指向能读的最大位置；写状态时指向能写的最大位置
cap：总大小

进行buffer.put("123".getBytes());之后，pos向右移动了三个字节的位置。

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616140333592.png)

```java
    @Test
    public void whatByteBuffer() {

//        ByteBuffer buffer = ByteBuffer.allocate(1024);  // 这种方式 内存开销是在JVM中的
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);  // 堆外内存 开销在JVM之外，以就是系统级的内存分配


        System.out.println("postition: " + buffer.position());
        System.out.println("limit: " + buffer.limit());
        System.out.println("capacity: " + buffer.capacity());
        System.out.println("mark: " + buffer);

        buffer.put("123".getBytes());

        System.out.println("-------------put:123......");
        System.out.println("mark: " + buffer);

        buffer.flip();   //从读到写的交替

        System.out.println("-------------flip......");
        System.out.println("mark: " + buffer);

        buffer.get();

        System.out.println("-------------get......");
        System.out.println("mark: " + buffer);

        buffer.compact();  // 从写到读的交替

        System.out.println("-------------compact......");
        System.out.println("mark: " + buffer);

        buffer.clear();

        System.out.println("-------------clear......");
        System.out.println("mark: " + buffer);

    }
```

```java
postition: 0
limit: 1024
capacity: 1024
mark: java.nio.DirectByteBuffer[pos=0 lim=1024 cap=1024]
-------------put:123......
mark: java.nio.DirectByteBuffer[pos=3 lim=1024 cap=1024]
-------------flip......
mark: java.nio.DirectByteBuffer[pos=0 lim=3 cap=1024]
-------------get......
mark: java.nio.DirectByteBuffer[pos=1 lim=3 cap=1024]
-------------compact......
mark: java.nio.DirectByteBuffer[pos=2 lim=1024 cap=1024]
-------------clear......
mark: java.nio.DirectByteBuffer[pos=0 lim=1024 cap=1024]

Process finished with exit code 0
```



**测试文件NIO RandomAccessFile随机读写**

```java
  //测试文件NIO RandomAccessFile随机读写
    @Test
    public void testRandomAccessFileWrite() throws Exception {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");

        raf.write("hello mashibing\n".getBytes());  // 普通的写入
        raf.write("hello seanzhou\n".getBytes());
        System.out.println("write------------");
        System.in.read();  // 在这里阻塞的时候，去看文件是可以看到内容的，但是还没有刷写到磁盘上，只是在pagecache中

        raf.seek(4);  // RandomAccessFile的随机读写能力：可以修改指针的偏移为第4个字节
        raf.write("ooxx".getBytes());  // hello mashibing -> helooxxashibing
        System.out.println("seek---------");
        System.in.read();

        FileChannel rafchannel = raf.getChannel();
        //用mmap得到堆外的且和文件映射的ByteBuffer   是byte，not object(没有对象的概念)
        //文件被称为块设备。只有文件可以做内存映射，只有文件的Channel才会有mmap，其它流是没有的
        MappedByteBuffer map = rafchannel.map(FileChannel.MapMode.READ_WRITE, 0, 4096);

        map.put("@@@".getBytes());  //通过FileChannel得到的MappedByteBuffer调用put不是系统调用，但是数据会到达内核的pagecache，因为和内核进行了映射
        // 曾经我们是需要out.write()这样的系统调用，才能让程序的 data 进入内核的 pagecache。换言之，曾经必须有用户态内核态切换
        // 但是现在，如果有了MappedByteBuffer，就有了mmap的内存映射，数据可以直接到pagecache中。但是mmap的内存映射依然是内核的pagecache体系所约束的！换言之，还是会丢数据
        // 目前Java还没有能力去让你逃离pagecache。你可以去github上找一些 其他C程序员写的JNI扩展库，使用linux内核的Direct IO，
        // 直接IO是忽略linux的pagecache的。是把pagecache交给了程序自己开辟一个字节数组当作pagecache，动用代码逻辑来维护一致性、脏等等一系列复杂问题。
        // 相当于自己去维护pagecache，相比于直接去修改内核的pagecache配置来说，粒度更细一些。
        // 比如数据库一般会使用Direct IO。

        System.out.println("map--put--------");
        System.in.read();

//        map.force(); //  强制刷写flush

		// 这后面自己随便分配一个seek buffer，主要是为了演示ByteBuffer怎么使用的。
        raf.seek(0);

        ByteBuffer buffer = ByteBuffer.allocate(8192);  // 使用普通的 ByteBuffer，堆上的
//        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);  // 堆外的

        int read = rafchannel.read(buffer);   //buffer.put()
        System.out.println(buffer);
        buffer.flip();  // 翻转
        System.out.println(buffer);

        for (int i = 0; i < buffer.limit(); i++) {
            Thread.sleep(200);
            System.out.print(((char) buffer.get(i)));
        }
    }
```

执行到`map.put("@@@".getBytes());`时，文件大小4096

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616145009431.png)

- 多了一个mem内存映射，4086大小
- 另外仍然有一个文件描述符4
  你可以继续使用文件描述符4，或者用新开的内存映射，进行文件的读写。

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/2020061614534461.png)

## 总结一下

Java是用C开发的一个程序，它的级别是Linux下的进程。
进程会被分配一个堆，里面包含进程该有的一切。
txt文件会申请JVM分配一个heap。
如果你是用ByteBuffer的allocate这种方式，是将字节数组分配到了堆上，是JVM堆内的线性地址空间。
如果你是用allocateDirect这种方式，会将字节数组分配到JVM的堆外内存中，是C语言可以直接访问的。

mmap将用户的线性地址空间直接映射到了内核的pagecache地址，如果是脏的需要写的话，依然受pagecache的影响，才能最终刷写到磁盘中去。

![在这里插入图片描述](https://gitee.com/zilongcc/images/raw/master/20200616150904972.png)

操作系统没有绝对的数据可靠性。它什么要设计pagecache，是为了减少硬件的IO的调用，想要优先使用内存，这样能够提速。如果追求性能，就要在一致性、可靠性之间做出权衡。

**从大方面来看，在现在的分布式系统当中，如果你追求数据存储的可靠性（保持缓存和磁盘的强一致，对于每一次对数据的微小改变，都要去刷写磁盘），仍然避免不了单点故障的问题。单点故障会让你为了保持强一致而耗费的能损耗一毛钱收益都没有。**

**这就是为什么我们使用主从复制、主备HA**
**这就是为什么Kafka，ES都有副本的概念，而副本是从socket得到的。副本又分为同步的异步的区别，这些都是后话了，我们以后再讲…**