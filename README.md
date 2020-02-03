# netty_proxy_server
基于Netty实现的内网穿透[类似与Ngrok]

使用方法：

1、在内网电脑登录Client端

2、通过在Server配置好要访问的端口

3、便可以在外网电脑访问内网电脑的内部网站或内部端口。

流程：
```
登录流程：
内网电脑 ----> 客户端(登录、心跳包) ----> 通知服务端电脑上线
```

```
数据转发：
用户(外网用户)  ----> 访问特定端口 ---->服务端  ---->  传输数据   ----> 客户端  ----> 远程服务器

用户(外网用户)  <---- 访问特定端口 <----服务端  <----  传输数据   <---- 客户端  <---- 远程服务器
```

打包方法：
```
    - 使用maven打包
    - 部署server的war包到服务器上,默认的用户名和密码是 admin/123
    - 运行client的jar包,java -jar client.jar 输入用户名和密码
    - 网页上打开server端,配置和端口后,运行即可,如果需要使用http代理,需要设置ie的代理服务
```

截图：
 ![image](https://github.com/GTale/netty_proxy_server/blob/master/screenshot/01.png)
 ![image](https://github.com/GTale/netty_proxy_server/blob/master/screenshot/02.png)
 ![image](https://github.com/GTale/netty_proxy_server/blob/master/screenshot/03.png)
 ![image](https://github.com/GTale/netty_proxy_server/blob/master/screenshot/NettyProxyServer.png)
