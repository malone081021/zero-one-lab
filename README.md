# zero-one-lab
zeroOneLab

## 测试多个远程仓库

1. 创建gitee和github
2. 配置ssh
   1.  生成key `ssh-keygen -t rsa -C "mark2007081021@163.com"`   
   2. 在远程仓库中的设置远程访问ssh中设置public key
   3. ![配置远程仓库host和私钥](https://user-gold-cdn.xitu.io/2019/12/25/16f3c5d5933c1095?w=508&h=306&f=png&s=19389)
3. 添加远程仓库地址到 `git remote set-url --add origin git@gitee.com:zilongcc/zero-one-lab.git`
4. 推送到远程

