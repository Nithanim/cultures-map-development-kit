package me.nithanim.cultures.format.newlib.io.reading;

import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends InputStream {
    private final InputStream in;
    private final long length;
    private long read;

    public LimitedInputStream(InputStream in, long length) {
        this.in = in;
        this.length = length;
    }

    @Override
    public int available() throws IOException {
        int a = in.available();
        a = (int) (a > length ? length : a); //crop to filesize
        a -= read;
        return a;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }

    @Override
    public int read() throws IOException {
        if (read >= length) {
            return -1;
        }
        if (read + 1 > length) {
            return -1;
        }
        read++;
        return in.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (read >= length) {
            return -1;
        }
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (read >= length) {
            return -1;
        }
        if (read + len > length) {
            byte[] buf = new byte[(int) (length - read)];
            int newRead = in.read(buf);
            read += newRead;
            System.arraycopy(buf, 0, b, off, newRead);
            return newRead;
        } else {
            int newRead = in.read(b, off, len);
            read += newRead;
            return newRead;
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        in.reset();
    }

    @Override
    public long skip(long n) throws IOException {
        if (read + n > length) {
            throw new IllegalArgumentException("Unable to skip " + n + " bytes! Read already " + read + " from all " + length);
        }
        return in.skip(n);
    }
}
