package me.nithanim.cultures.lsp.processor.util;

import lombok.NonNull;
import lombok.Value;
import org.eclipse.lsp4j.Range;

@Value
public class Origin {
    @NonNull
    SourceFile sourceFile;
    @NonNull
    Range range;
}
