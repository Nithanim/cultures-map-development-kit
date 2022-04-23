package me.nithanim.cultures.lsp.processor.util;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import me.nithanim.cultures.lsp.processor.services.clientpersistent.Origination;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;

@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class MyDiagnostic implements Origination {
  @NonNull final Origin origin;
  final DiagnosticSeverity severity;
  String code;
  String source;
  @NonNull String message;

  @Builder
  public MyDiagnostic(
      @NonNull Origin origin, DiagnosticSeverity severity, @NonNull String message) {
    this.origin = origin;
    this.severity = severity;
    this.message = message;
  }

  public SourceFile getSourceFile() {
    return origin.getSourceFile();
  }

  public Diagnostic toDiagnostic() {
    Diagnostic d = new Diagnostic();
    d.setRange(origin.getRange());
    d.setSeverity(severity);
    d.setCode(code);
    d.setSource(source);
    d.setMessage(message);
    return d;
  }
}
