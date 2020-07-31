package me.nithanim.cultures.lsp.processor.events;

import lombok.EqualsAndHashCode;
import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import org.springframework.context.ApplicationEvent;

/** Source file was parsed and unresolved lines are available. */
@Value
@EqualsAndHashCode(callSuper = true)
public class SourceFileParsedEvent extends ApplicationEvent {
  SourceFile sourceFile;

  public SourceFileParsedEvent(Object source, SourceFile sourceFile) {
    super(source);
    this.sourceFile = sourceFile;
  }
}
