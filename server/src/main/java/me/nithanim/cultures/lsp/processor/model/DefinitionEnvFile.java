package me.nithanim.cultures.lsp.processor.model;

import me.nithanim.cultures.lsp.processor.lines.CulturesIniLine;

import java.util.ArrayList;
import java.util.List;

public class DefinitionEnvFile {
    List<? extends CulturesIniLine> lines = new ArrayList<>();

    public void update(List<? extends CulturesIniLine> lines) {
        this.lines = lines;
    }
}
