package me.nithanim.cultures.format.newlib;

import java.util.List;
import lombok.Value;
import me.nithanim.cultures.format.newlib.io.DirMeta;
import me.nithanim.cultures.format.newlib.io.FileMeta;

@Value
public class LibFileInfo {
    private final LibFormat format;
    private final List<DirMeta> dirMetas;
    private final List<FileMeta> fileMetas;

    public LibFileInfo(LibFormat format, List<DirMeta> dirMetas, List<FileMeta> fileMetas) {
        this.format = format;
        this.dirMetas = dirMetas;
        this.fileMetas = fileMetas;
    }
}
