package me.nithanim.cultures.lsp.processor.lines;

import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.Origin;

@Value
public class CulturesIniInclude implements CulturesIniLine {
    private Origin originAll;
    private Origin originPath;
    private String path;
}
