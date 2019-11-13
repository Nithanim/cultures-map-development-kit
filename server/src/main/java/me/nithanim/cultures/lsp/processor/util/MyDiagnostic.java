package me.nithanim.cultures.lsp.processor.util;

import lombok.*;
import me.nithanim.cultures.lsp.processor.services.clientpersistent.GenericKeyPerSourceService;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;

@Getter
@EqualsAndHashCode
@ToString
public class MyDiagnostic implements GenericKeyPerSourceService.Origination {
    @NonNull
    final Origin origin;
    final DiagnosticSeverity severity;
    String code;
    String source;
    @NonNull
    String message;

    @Builder
    public MyDiagnostic(@NonNull Origin origin, DiagnosticSeverity severity, @NonNull String message) {
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
