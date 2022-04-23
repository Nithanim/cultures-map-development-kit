package me.nithanim.cultures.format.newlib.io.reading;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// WHY IS THERE NO EXAMPLE READY TO USE IN A LIBRARY???
public class ReadonlyByteArraySeekableByteChannel implements SeekableByteChannel {
  private final ReadWriteLock rwlock = new ReentrantReadWriteLock();
  private final byte[] data;

  private volatile boolean closed;

  private int position;

  public ReadonlyByteArraySeekableByteChannel(byte[] data) {
    this.data = data;
  }

  @Override
  public int read(ByteBuffer dst) throws IOException {
    beginWrite();
    try {
      ensureOpen();
      if (position == data.length) {
        return -1;
      }
      int bytesToRead = Math.min(dst.remaining(), data.length - position);
      dst.put(data, position, bytesToRead);
      position += bytesToRead;
      return bytesToRead;
    } finally {
      endWrite();
    }
  }

  @Override
  public int write(ByteBuffer src) throws IOException {
    throw new NonWritableChannelException();
  }

  @Override
  public long position() throws IOException {
    beginRead();
    try {
      ensureOpen();
      return position;
    } finally {
      endRead();
    }
  }

  @Override
  public SeekableByteChannel position(long newPosition) throws IOException {
    if (newPosition < 0) {
      throw new IllegalArgumentException("New position " + newPosition + " must be bigger than 0!");
    }
    if (newPosition > data.length) {
      throw new IllegalArgumentException(
          "New position " + newPosition + " cannot exceed file length!");
    }

    beginWrite();
    try {
      ensureOpen();
      this.position = (int) newPosition;
      return this;
    } finally {
      endWrite();
    }
  }

  @Override
  public long size() throws IOException {
    return data.length;
  }

  @Override
  public SeekableByteChannel truncate(long size) throws IOException {
    throw new NonWritableChannelException();
  }

  @Override
  public boolean isOpen() {
    return !closed;
  }

  @Override
  public void close() throws IOException {
    closed = true;
  }

  private void ensureOpen() throws IOException {
    if (closed) {
      throw new ClosedChannelException();
    }
  }

  private void beginRead() {
    rwlock.readLock().lock();
  }

  private void endRead() {
    rwlock.readLock().unlock();
  }

  final void beginWrite() {
    rwlock.writeLock().lock();
  }

  final void endWrite() {
    rwlock.writeLock().unlock();
  }
}
