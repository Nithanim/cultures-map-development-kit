package me.nithanim.cultures.format.newlib.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DirMetaCodec {
    public DirMeta unpack(LittleEndianDataInputStream dis) throws IOException {
        int nameLength = dis.readInt();
        byte[] chars = new byte[nameLength];
        dis.read(chars);
        int level = dis.readInt();
        return new DirMeta(new String(chars, StandardCharsets.ISO_8859_1), level);
    }
}
