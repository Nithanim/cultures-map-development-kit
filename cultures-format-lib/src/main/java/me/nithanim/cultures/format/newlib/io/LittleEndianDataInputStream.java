package me.nithanim.cultures.format.newlib.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LittleEndianDataInputStream extends InputStream implements DataInput {
    private DataInputStream d; // to get at high level readFully methods of
    private InputStream in; // to get at the low-level read methods of
    private byte w[]; // work array for buffering input

    public LittleEndianDataInputStream(InputStream in) {
        this.in = in;
        this.d = new DataInputStream(in);
        w = new byte[8];
    }

    @Override
    public int available() throws IOException {
        return d.available();
    }

    @Override
    public short readShort() throws IOException {
        d.readFully(w, 0, 2);
        return (short) ((w[1] & 0xff) << 8
            | (w[0] & 0xff));
    }

    @Override
    public int readUnsignedShort() throws IOException {
        d.readFully(w, 0, 2);
        return ((w[1] & 0xff) << 8
            | (w[0] & 0xff));
    }

    @Override
    public char readChar() throws IOException {
        d.readFully(w, 0, 2);
        return (char) ((w[1] & 0xff) << 8
            | (w[0] & 0xff));
    }

    @Override
    public int readInt() throws IOException {
        d.readFully(w, 0, 4);
        return (w[3]) << 24
            | (w[2] & 0xff) << 16
            | (w[1] & 0xff) << 8
            | (w[0] & 0xff);
    }

    @Override
    public long readLong() throws IOException {
        d.readFully(w, 0, 8);
        return (long) (w[7]) << 56
            | (long) (w[6] & 0xff) << 48
            | (long) (w[5] & 0xff) << 40
            | (long) (w[4] & 0xff) << 32
            | (long) (w[3] & 0xff) << 24
            | (long) (w[2] & 0xff) << 16
            | (long) (w[1] & 0xff) << 8
            | (long) (w[0] & 0xff);
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    @Override
    public final void readFully(byte b[]) throws IOException {
        d.readFully(b, 0, b.length);
    }

    @Override
    public void readFully(byte b[], int off, int len) throws IOException {
        d.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return d.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return d.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return d.readByte();
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return d.readUnsignedByte();
    }

    @Deprecated
    @Override
    public String readLine() throws IOException {
        return d.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return d.readUTF();
    }

    @Override
    public void close() throws IOException {
        d.close();
    }
}
