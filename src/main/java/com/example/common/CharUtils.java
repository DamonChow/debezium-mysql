package com.example.common;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-24 18:59
 */
public class CharUtils {

    public static byte[] byteMergerAll(List<byte[]> list) {
        int length_byte = 0;
        for (int i = 0; i < list.size(); i++) {
            length_byte += list.get(i).length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < list.size(); i++) {
            byte[] b = list.get(i);
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) (value & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[3] = (byte) ((value >> 24) & 0xFF);
        return src;
    }

    public static byte[] getByte(ByteBuffer buf) {
        byte[] data = new byte[buf.remaining()];
        buf.get(data);
        return data;
    }
}
