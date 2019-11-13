package me.nithanim.cultures.lsp.processor.services;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.nithanim.cultures.format.cif.CifFile;
import me.nithanim.cultures.format.cif.CifFileCodec;
import me.nithanim.cultures.format.newlib.io.reading.LibFileFileSystem;
import me.nithanim.cultures.format.newlib.io.reading.LibFileFileSystemProvider;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static me.nithanim.cultures.format.newlib.LibFormat.CULTURES2;

public class MapFileUtil {
    public static final String FILENAME_BINARY = "map.dat";
    public static final String FILENAME_INI = "map.cif";

    public static Path extractMapBinary(Path mapFile) throws IOException {
        LibFileFileSystemProvider fp = new LibFileFileSystemProvider();
        LibFileFileSystem fs = fp.newFileSystem(mapFile, Collections.singletonMap("type", CULTURES2));
        return fs.getPath("currentusermap");
    }

    public static List<String> decodeCif(byte[] bytes) throws IOException {
        ByteBuf buf = Unpooled.wrappedBuffer(bytes).order(ByteOrder.LITTLE_ENDIAN);

        CifFileCodec codec = new CifFileCodec();
        CifFile cifFile = codec.unpack(buf);
        return cifFile.getLines();
    }
}
