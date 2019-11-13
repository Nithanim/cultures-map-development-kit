package me.nithanim.cultures.format.newlib.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileMetaCodec {
    public FileMeta unpack(LittleEndianDataInputStream dis) throws IOException {
        int nameLength = dis.readInt();
        byte[] chars = new byte[nameLength];
        dis.read(chars);
        String name = new String(chars, StandardCharsets.ISO_8859_1);
        int pos = dis.readInt();
        int len = dis.readInt();
        return new FileMeta(name, pos, len);
    }
}
