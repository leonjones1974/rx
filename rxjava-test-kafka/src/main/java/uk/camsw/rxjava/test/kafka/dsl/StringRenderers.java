package uk.camsw.rxjava.test.kafka.dsl;

import kafka.message.MessageAndMetadata;

import java.io.UnsupportedEncodingException;

public class StringRenderers {

    public static String keyAndMessage(MessageAndMetadata<byte[], byte[]> mamd) {
        return asUtf8(mamd.key()) + "=" + asUtf8(mamd.message());
    }

    public static String messageAndOffset(MessageAndMetadata<byte[], byte[]> mamd) {
        return asUtf8(mamd.message()) + "@offset" + mamd.offset();
    }

    public static String messageAndPartition(MessageAndMetadata<byte[], byte[]> mamd) {
        return asUtf8(mamd.message()) + "@part" + mamd.partition();
    }

    public static String messageAnd(MessageAndMetadata<byte[], byte[]> mamd, String additional) {
        return asUtf8(mamd.message()) + "@" + additional;
    }

    public static String asUtf8(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);

        }
    }


}

