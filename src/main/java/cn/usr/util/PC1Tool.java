package cn.usr.util;

import java.nio.ByteBuffer;

public final class PC1Tool {
    private static int inter, cfc, cfd, si, x1a2;
    private static int[] x1a0 = new int[9];
    private static int[] DecryptKey = new int[16];

    private static int[] PC1_APPKEY = new int[]{0x66, 0x75, 0x63, 0x6B, 0x20, 0x67, 0x66, 0x77, 0x66, 0x75, 0x63,
            0x6B, 0x20, 0x67, 0x66, 0x77};

    private static void InitPC1Key(int[] Pointer) {
        si = 0;
        x1a2 = 0;
        // 这个是copy 我。。。。不多说了
        System.arraycopy(Pointer, 0, DecryptKey, 0, Pointer.length);
    }

    private static void EncryptBlock(int[] SourceBuf) {
        for (int n = 0; n < SourceBuf.length; n++) {
            SourceBuf[n] = PC1Enc128Byte(SourceBuf[n]);
        }
    }

    private static void DecryptBlock(int[] SourceBuf) {
        // 50次
        for (int n = 0; n < SourceBuf.length; n++) {
            SourceBuf[n] = PC1Dec128Byte(SourceBuf[n]);
        }
    }

    private static void PC1assemble128() {

        int ax, bx, cx, dx, tmp;
        inter = 0;
        x1a0[0] = 0;

        for (int i = 0; i < 8; i++) {
            x1a0[i + 1] = x1a0[i] ^ ((DecryptKey[i * 2] * 256) + DecryptKey[i * 2 + 1]);
            dx = x1a2 + i;
            ax = x1a0[i + 1];
            cx = 0x015A;
            bx = 0x4E35;

            // exchange(@ax, @si);
            tmp = ax;
            ax = si;
            si = tmp;
            // exchange(@ax, @dx);
            tmp = ax;
            ax = dx;
            dx = tmp;
            if (ax != 0)
                ax = ax * bx;
            // exchange(@ax, @cx);
            tmp = ax;
            ax = cx;
            cx = tmp;
            if (ax != 0) {
                ax = ax * si;
                cx = ax + cx;
            }
            // exchange(@ax, @si);
            tmp = ax;
            ax = si;
            si = tmp;
            ax = ax * bx;
            dx = cx + dx;
            ax = ax + 1;
            x1a2 = dx;
            x1a0[i + 1] = ax;
            inter = inter ^ (ax ^ dx);
        }
    }

    private static int PC1Enc128Byte(int c) {
        PC1assemble128();
        cfc = inter >> 8;
        cfd = inter & 0xff;
        for (int i = 0; i < 16; i++)
            DecryptKey[i] = (DecryptKey[i] ^ c) & 0xFF;
        c = (c ^ (cfc ^ cfd)) & 0xFF;
        return c;
    }

    private static int PC1Dec128Byte(int c) {
        PC1assemble128();
        cfc = inter >> 8;
        cfd = inter & 0xff;
        c = (c ^ (cfc ^ cfd)) & 0xFF;
        for (int i = 0; i < 16; i++)
            DecryptKey[i] = (DecryptKey[i] ^ c) & 0xFF;
        return c;
    }

    private static int[] EncryptInt(int[] ABuf) {
        int[] Result = new int[ABuf.length];
        for (int i = 0; i < ABuf.length; i++)
            Result[i] = ABuf[ABuf.length - 1 - i];
        InitPC1Key(PC1_APPKEY);
        EncryptBlock(Result);
        return Result;
    }

    private static int[] DecryptInt(int[] ABuf) {
        InitPC1Key(PC1_APPKEY);
        DecryptBlock(ABuf);
        return ABuf;
    }

    private static int byteToInt(byte val) {
        if (val < 0) {
            return (val & 0x7f) + 128;
        } else {
            return val;
        }
    }

    /**
     * 加密
     *
     * @param code
     */
    public static void encrypt(byte[] code) {
        int[] ABuf = new int[code.length];
        for (int i = 0; i < code.length; i++) {
            ABuf[i] = byteToInt(code[i]);
        }
        ABuf = EncryptInt(ABuf);
        for (int i = 0; i < ABuf.length; i++) {
            code[i] = (byte) ABuf[i];
        }
    }

    /**
     * 解密
     *
     * @param code
     */
    public synchronized static void decrypt(byte[] code) {
        // synchronized 参数不要去掉否则会导致 大量高并发的时候解析混乱
        // 这个PC1解密方法是真的服气，由delphi语言转的，你们要是想研究慢慢看吧，不优化了
        int[] ABuf = new int[code.length];

        for (int i = 0; i < code.length; i++) {
            // 这个不是将byte[]转换为int[] 只是吧byte[]中的负数取反，我的乖乖这个操作真是神了，他奶奶的腿的
            ABuf[i] = byteToInt(code[i]);
        }
        ABuf = DecryptInt(ABuf);
        //这里不需要倒叙
//		for (int i = 0; i < code.length; i++) {
//			code[i] = (byte) ABuf[code.length - i - 1];
//		}

        for (int i = 0; i < code.length; i++) {
            code[i] = (byte) ABuf[i];
        }
    }

}
