package me.nithanim.cultures.format.newlib.io;

import lombok.Value;

@Value
public class FileMeta {
    String name;
    int pos;
    int len;

    public String getFileExtension() {
        int i = name.lastIndexOf('.');
        return i == -1 ? null : name.substring(i + 1);
    }
}
