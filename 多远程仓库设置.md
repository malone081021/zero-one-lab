# zero-one-lab
zeroOneLab

## 测试多个远程仓库

1. 创建gitee和github
2. 配置ssh
   1.  生成key `ssh-keygen -t rsa -C "mark2007081021@163.com"`   
   2. 在远程仓库中的设置远程访问ssh中设置public key
   3. ![image](https://user-images.githubusercontent.com/43690259/71441343-0db97380-273c-11ea-9804-3fc23cba3d42.png)
3. 添加远程仓库地址到 `git remote set-url --add origin git@gitee.com:zilongcc/zero-one-lab.git`
4. 推送到远程

## 参考

- https://juejin.im/post/5d48624c5188250571067e50

