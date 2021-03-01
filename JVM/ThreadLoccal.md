# 背景

1. ThreadLocal 只是一个工具类
2. 完成将一个值或者对象，和线程绑定，获取时只获取和这个线程绑定的值
3. Thread也是一个对象

![image-20210206125128260](https://gitee.com/zilongcc/images/raw/master/image-20210206125128260.png)

1. 其他线程是否可以修改当前线程的值，也就是执行set方法，传值

