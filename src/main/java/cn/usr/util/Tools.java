package cn.usr.util;

import io.netty.buffer.ByteBuf;

import java.util.Random;

/**
 * @author Administrator
 */
public class Tools {

    public static int getCurrentSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }


    public static boolean isStringEmpty(String str) {
        if (str == null || str.trim().length() == 0)
            return true;
        return false;
    }


    /**
     * int ת byte[]
     *
     * @param res
     * @return
     */
    public static byte[] intToBytes(int res) {
        byte[] targets = new byte[4];
        targets[0] = (byte) (res & 0xff);
        targets[1] = (byte) ((res >> 8) & 0xff);
        targets[2] = (byte) ((res >> 16) & 0xff);
        targets[3] = (byte) (res >>> 24);
        return targets;
    }


    /**
     * ��4�ֽڵ�byte����ת��intֵ
     *
     * @param b
     * @return
     */
    public static int bytesToInt(byte[] b) {

        int v0 = (b[0] & 0xff) << 24;
        int v1 = (b[1] & 0xff) << 16;
        int v2 = (b[2] & 0xff) << 8;
        int v3 = (b[3] & 0xff);
        return v0 + v1 + v2 + v3;
    }


    /**
     * �ֽ����� ת��Ϊ16����
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv + " ");
        }
        return stringBuilder.toString();
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }

        if (hexString.length() % 2 != 0) {
            StringBuilder stringBuilder = new StringBuilder(hexString);
            stringBuilder.insert(hexString.length() - 1, "0");
            hexString = stringBuilder.toString();
        }

        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static boolean isRightHexStr(String str) {
        String reg = "^[0-9a-fA-F]+$";
        return str.matches(reg);
    }


    public static int getRandom(int max, int min) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }


    public static byte[] getByteArrayFromByteBuf(ByteBuf byteBuf) {
        if (byteBuf.hasArray()) {
            return byteBuf.array();
        } else {
            byte[] data = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(byteBuf.readerIndex(), data);
            return data;
        }
    }

    public static String formatComPassword(String pasd) {
        String z = "00000000";
        return z.substring(0, z.length() - pasd.length()) + pasd;
    }


    public static byte[] getPayloadToV2(String account, String devId, byte[] originPayload) {
        //格式：length + account+,+deviceId+payload
        //length:account.length+","+deviceId.length
        StringBuilder sb = new StringBuilder(account).append(",").append(devId);
        byte[] lastPayload = new byte[sb.toString().getBytes().length + 1 + originPayload.length];
        lastPayload[0] = (byte) sb.length();
        System.arraycopy(sb.toString().getBytes(), 0, lastPayload, 1, sb.toString().length());
        System.arraycopy(originPayload, 0, lastPayload, sb.toString().getBytes().length + 1, originPayload.length);
        return lastPayload;
    }


}
