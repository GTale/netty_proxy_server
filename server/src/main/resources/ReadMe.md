生成ssl证书的方法

1、安装openssl

2、生成Ca
```$xslt
mkdir key
cd key
openssl genrsa -out ca.pem 2048
openssl req -new -x509 -days 365 -key ca.pem -out ca.crt
```

3、生成server证书
```$xslt
openssl genrsa -out server.key 2048
openssl req -new -days 365 -key server.key -out server.csr
openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.pem -CAcreateserial -days 1000 -out server.crt
```

4、查看证书内容
```
openssl x509 -in server.crt -text
```