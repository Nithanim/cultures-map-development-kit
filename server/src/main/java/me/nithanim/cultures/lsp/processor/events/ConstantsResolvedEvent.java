package me.nithanim.cultures.lsp.processor.events;

import lombok.Value;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniLine;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * All constants were resolved after parsing source files.
 */
@Value
public class ConstantsResolvedEvent extends ApplicationEvent {
    private SourceFile sourceFile;
    private final List<? extends CulturesIniLine> allResolvedLines;

    public ConstantsResolvedEvent(Object source, SourceFile sourceFile, List<? extends CulturesIniLine> allResolvedLines) {
        super(source);
        this.sourceFile = sourceFile;
        this.allResolvedLines = allResolvedLines;
    }
}
