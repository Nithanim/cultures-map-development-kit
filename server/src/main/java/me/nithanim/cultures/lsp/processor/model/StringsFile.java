package me.nithanim.cultures.lsp.processor.model;

import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.Origin;

import java.util.Map;

public class StringsFile {
    private String language;
    private Map<Integer, StringDefinition> idToStringMap;

    @Value
    public class StringDefinition {
        int id;
        String language;
        Origin origin;
    }
}
