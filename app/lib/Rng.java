package lib;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

public class Rng {
    private SecureRandom rng = new SecureRandom();

    public byte nextByte() {
        byte[] b = new byte[1];
        rng.nextBytes(b);
        return b[0];
    }

    public int nextInt() {
        byte[] b = new byte[4];
        rng.nextBytes(b);
        ByteBuffer bf = ByteBuffer.wrap(b);
        return bf.getInt();
    }

    public long nextLong() {
        byte[] b = new byte[8];
        rng.nextBytes(b);
        ByteBuffer bf = ByteBuffer.wrap(b);
        return bf.getLong();
    }

    public double nextDouble() {
        long v = nextLong();
        double a = (double)v - (double)Long.MIN_VALUE;
        double b = (double)Long.MAX_VALUE - (double)Long.MIN_VALUE;

        return a/b;
    }

    public boolean nextBoolean() {
        return (randInt(2) == 1) ? true : false;
    }

    public int log2(int v) {
        int res = 0;
        while (v > 0) {
            res++;
            v >>= 1;
        }
        return res;
    }

    public int randInt(int up) {
        if (up < 1)
            return -1;
        if ((up & (up - 1)) == 0)
            return (int) (nextLong() & (up - 1));

        int v, log;
        log = log2(up);
        while ((v = ((int)nextLong() & ((1 << log) - 1))) >= up);

        return v;
    }

    public int rand5() {
        return randInt(5);
    }

    public int rand2() {
        int v;
        while ((v = rand5()) == 2);
        return (v < 2) ? 0 : 1;
    }

    public int rand7() {
        long v = 0;
        for (int i = 0; i < 62; i++)
            v|= rand2() << i;
        return (int) ((v & (((long)1 << 62) - 1)) % 7);
    }

    public int rand7_2() throws Exception {
        int v;
        while ((v = rand2() << 2 | rand2() << 1 | rand2()) >= 7);
        return v;
    }

    public int rand3() throws Exception {
        int v;

        while ((v = rand5()) >= 3);
        return v;
    }

    public String genBase64String(int bytes) {
        SecureRandom rng = new SecureRandom();
        byte[] rndBytes = new byte[bytes];
        rng.nextBytes(rndBytes);
        return Base64.getEncoder().encodeToString(rndBytes);
    }
}
