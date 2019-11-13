package me.nithanim.cultures.format.newlib.io;

import java.io.IOException;
import me.nithanim.cultures.format.newlib.LibFormat;

public class HeaderCodec {
    public Header unpack(LittleEndianDataInputStream dis, LibFormat format) throws IOException {
        int unknown = dis.readInt();
        int dirCount;
        if (format == LibFormat.CULTURES2) {
            dirCount = dis.readInt();
        } else {
            dirCount = -1;
        }
        int fileCount = dis.readInt();
        return new Header(unknown, dirCount, fileCount);
    }
}
