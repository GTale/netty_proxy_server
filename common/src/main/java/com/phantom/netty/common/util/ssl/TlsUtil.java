package com.phantom.netty.common.util.ssl;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.AllArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.net.ssl.SSLException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 可以阅读netty源码SelfSignedCertificate,BouncyCastleSelfSignedCertGenerator
 * <p>
 * 证书使用方法：https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/securechat
 */
public class TlsUtil {

    private static final Provider PROVIDER = new BouncyCastleProvider();

    public static SslContext forService(SslConfig config) throws Exception {
        Cert cert = getCert(config);
        return SslContextBuilder.forServer(cert.keyPair.getPrivate(), cert.certificate).build();

    }

    public static SslContext forClient() throws SSLException {
        return SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
    }

    /**
     * 签发证书
     */
    private static Cert getCert(SslConfig config) throws CertificateException {
        try {
            Date before = Date.from(Instant.now());
            Date expire = Date.from(
                    Year.now()
                            .plus(3, ChronoUnit.YEARS)
                            .atDay(1)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
            );

            // 获取Ca证书
            X509CertificateHolder caCert = getFromFile(config.getCertFilePath());

            // 获取Ca的密钥对
            PEMKeyPair pemKeyPair = getFromFile(config.getKeyFilePath());
            KeyPair    keyPair    = new JcaPEMKeyConverter().setProvider(PROVIDER).getKeyPair(pemKeyPair);
            PrivateKey key        = keyPair.getPrivate();
            PublicKey  pubKey     = keyPair.getPublic();

            // 生成证书请求csr,由于是自签名,复用CA的密钥对
            X500Name owner = new X500Name("CN=" + config.getHost());
            X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                    caCert.getSubject(),
                    new BigInteger(64, new SecureRandom()),
                    before,
                    expire,
                    owner,
                    pubKey
            );

            // 签名方法
            ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(key);

            // 签名并获取里面的证书
            X509CertificateHolder certHolder = builder.build(signer);
            X509Certificate       cert       = new JcaX509CertificateConverter().setProvider(PROVIDER).getCertificate(certHolder);

            // 验证证书
            cert.verify(keyPair.getPublic());
            return new Cert(keyPair, cert);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <T> T getFromFile(String filePath) throws IOException {
        try (PEMParser pemParser = new PEMParser(new FileReader(Thread.currentThread().getContextClassLoader().getResource(filePath).getPath()))) {
            Object o = pemParser.readObject();
            @SuppressWarnings("unchecked")
            T t = (T) o;
            return t;
        }
    }

    @AllArgsConstructor
    private static class Cert {
        private KeyPair         keyPair;
        private X509Certificate certificate;
    }
}
