package chapter11;

import org.apache.kafka.common.header.Header;

/**
 * @author 朱小厮
 * @date 2018/8/12.
 */
public class TTLHeader implements Header {
    /**
     * 超时时间，单位为秒
     */
    private long ttl;

    public TTLHeader(long ttl) {
        this.ttl = ttl;
    }

    @Override
    public String key() {
        return "ttl";
    }

    /**
     * 将long类型转成byte[]类型
     *
     * @return
     */
    @Override
    public byte[] value() {
        long res = this.ttl;
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            buffer[i] = (byte) ((res >> offset) & 0xff);
        }
        return buffer;
    }
}
