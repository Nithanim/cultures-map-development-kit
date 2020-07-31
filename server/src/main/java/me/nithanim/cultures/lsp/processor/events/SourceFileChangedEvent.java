package me.nithanim.cultures.lsp.processor.events;

import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import org.springframework.context.ApplicationEvent;

@Value
public class SourceFileChangedEvent extends ApplicationEvent {
  SourceFile sourceFile;

  public SourceFileChangedEvent(Object source, SourceFile sourceFile) {
    super(source);
    this.sourceFile = sourceFile;
  }
}
