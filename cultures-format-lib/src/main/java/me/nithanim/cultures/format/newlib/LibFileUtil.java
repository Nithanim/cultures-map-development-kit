package me.nithanim.cultures.format.newlib;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import me.nithanim.cultures.format.newlib.io.DirMeta;
import me.nithanim.cultures.format.newlib.io.DirMetaCodec;
import me.nithanim.cultures.format.newlib.io.FileMeta;
import me.nithanim.cultures.format.newlib.io.FileMetaCodec;
import me.nithanim.cultures.format.newlib.io.Header;
import me.nithanim.cultures.format.newlib.io.HeaderCodec;
import me.nithanim.cultures.format.newlib.io.LittleEndianDataInputStream;

@UtilityClass
public class LibFileUtil {
    public static LibFileInfo read(InputStream is, LibFormat format) throws IOException {
        LittleEndianDataInputStream dis = new LittleEndianDataInputStream(is);
        Header header = new HeaderCodec().unpack(dis, format);

        List<DirMeta> dirMetas;
        if (format == LibFormat.CULTURES2) {
            DirMetaCodec dirMetaCodec = new DirMetaCodec();

            dirMetas = new ArrayList<>();
            for (int i = 0; i < header.getDirCount(); i++) {
                dirMetas.add(dirMetaCodec.unpack(dis));
            }
        } else {
            dirMetas = null;
        }

        FileMetaCodec fileMetaCodec = new FileMetaCodec();
        List<FileMeta> fileMetas = new ArrayList<>();
        for (int i = 0; i < header.getFileCount(); i++) {
            fileMetas.add(fileMetaCodec.unpack(dis));
        }
        
        is.close();
        return new LibFileInfo(format, dirMetas, fileMetas);
    }
}
