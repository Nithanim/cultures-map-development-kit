package me.nithanim.cultures.format.newlib.io.writing;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LittleEndianDataOutput implements DataOutput {
  private final DataOutputStream delegate;

  @Override
  public void write(int b) throws IOException {
    delegate.write(b);
  }

  @Override
  public void write(byte[] b) throws IOException {
    delegate.write(b);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    delegate.write(b, off, len);
  }

  @Override
  public void writeBoolean(boolean v) throws IOException {
    delegate.writeBoolean(v);
  }

  @Override
  public void writeByte(int v) throws IOException {
    delegate.writeByte(v);
  }

  @Override
  public void writeShort(int v) throws IOException {
    delegate.writeShort(((v & 0xFF) << 8) | ((v >> 8) & 0xFF));
  }

  @Override
  public void writeChar(int v) throws IOException {
    delegate.writeShort(((v & 0xFF) << 8) | ((v >> 8) & 0xFF));
  }

  @Override
  public void writeInt(int v) throws IOException {
    int a = ((v >> 8 * 3) & 0xFF) << 8 * 0;
    int b = ((v >> 8 * 2) & 0xFF) << 8 * 1;
    int c = ((v >> 8 * 1) & 0xFF) << 8 * 2;
    int d = ((v >> 8 * 0) & 0xFF) << 8 * 3;
    delegate.writeInt(a | b | c | d);
  }

  @Override
  public void writeLong(long v) throws IOException {
    long a = ((v >> 8 * 7) & 0xFF) << 8 * 0;
    long b = ((v >> 8 * 6) & 0xFF) << 8 * 1;
    long c = ((v >> 8 * 5) & 0xFF) << 8 * 2;
    long d = ((v >> 8 * 4) & 0xFF) << 8 * 3;
    long e = ((v >> 8 * 3) & 0xFF) << 8 * 4;
    long f = ((v >> 8 * 2) & 0xFF) << 8 * 5;
    long g = ((v >> 8 * 1) & 0xFF) << 8 * 6;
    long h = ((v >> 8 * 0) & 0xFF) << 8 * 7;
    delegate.writeLong(a | b | c | d | e | f | g | h);
  }

  @Override
  public void writeFloat(float v) throws IOException {
    writeInt(Float.floatToIntBits(v));
  }

  @Override
  public void writeDouble(double v) throws IOException {
    writeLong(Double.doubleToLongBits(v));
  }

  @Override
  public void writeBytes(String s) throws IOException {
    throw new UnsupportedOperationException(s);
  }

  @Override
  public void writeChars(String s) throws IOException {
    throw new UnsupportedOperationException(s);
  }

  @Override
  public void writeUTF(String s) throws IOException {
    throw new UnsupportedOperationException(s);
  }

  public int getBytesWritten() {
    return delegate.size();
  }
}
