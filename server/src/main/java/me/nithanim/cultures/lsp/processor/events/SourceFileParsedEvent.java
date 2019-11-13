package me.nithanim.cultures.lsp.processor.events;

import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import org.springframework.context.ApplicationEvent;

/**
 * Source file was parsed and unresolved lines are available.
 */
@Value
public class SourceFileParsedEvent extends ApplicationEvent {
    private SourceFile sourceFile;

    public SourceFileParsedEvent(Object source, SourceFile sourceFile) {
        super(source);
        this.sourceFile = sourceFile;
    }
}
