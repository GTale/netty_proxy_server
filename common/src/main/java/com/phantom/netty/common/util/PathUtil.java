package com.phantom.netty.common.util;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathUtil {

    private static final Pattern PATH_PATTERN = Pattern.compile("(https?)://([a-zA-Z0-9\\.\\-]+)(:(\\d+))?(/.*)");
    private static final Pattern TUNNEL_ADDR_PATTERN = Pattern.compile("^([a-zA-Z0-9\\.\\-_]+):(\\d+)");


    public static PathUtil.FullPath resolveHttpProxyPath(String fullPath) {
        Matcher matcher = PATH_PATTERN.matcher(fullPath);
        if (matcher.find()) {
            String scheme = matcher.group(1);
            String host = matcher.group(2);
            int port = resolvePort(scheme, matcher.group(4));
            String path = matcher.group(5);
            return new PathUtil.FullPath(scheme, host, port, path);
        } else {
            throw new IllegalStateException("Illegal http proxy path: " + fullPath);
        }
    }

    public static int resolvePort(String scheme, String port) {
        if (StringUtils.isBlank(port)) {
            return "https".equals(scheme) ? 443 : 80;
        }
        return Integer.parseInt(port);
    }


    public static FullPath resolveTunnelAddr(String addr) {
        Matcher matcher = TUNNEL_ADDR_PATTERN.matcher(addr);
        if (matcher.find()) {
            return new FullPath(matcher.group(1), Integer.parseInt(matcher.group(2)));
        } else {
            throw new IllegalStateException("Illegal tunnel addr: " + addr);
        }
    }

    @Getter
    public static class FullPath {
        private String scheme;
        private String host;
        private int port;
        private String path;

        public FullPath(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public FullPath(String scheme, String host, int port, String path) {
            this.scheme = scheme;
            this.host = host;
            this.port = port;
            this.path = path;
        }

        @Override
        public String toString() {
            return "FullPath{" +
                    "scheme='" + scheme + '\'' +
                    ", host='" + host + '\'' +
                    ", port=" + port +
                    ", path='" + path + '\'' +
                    '}';
        }
    }
}
