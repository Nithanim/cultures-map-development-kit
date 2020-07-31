package me.nithanim.cultures.lsp.processor.util;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.eclipse.lsp4j.DiagnosticSeverity;

public class DiagnosticsCollector {
  @Getter List<MyDiagnostic> diagnostics = new ArrayList<>();

  public void add(MyDiagnostic diagnostic) {
    diagnostics.add(diagnostic);
  }

  public void addError(Origin o, String message) {
    add(new MyDiagnostic(o, DiagnosticSeverity.Error, message));
  }

  public void addError(Origin o, String message, Origin reference) {
    add(new MyDiagnostic(o, DiagnosticSeverity.Error, message));
  }

  public void addWarning(Origin o, String message) {
    add(new MyDiagnostic(o, DiagnosticSeverity.Warning, message));
  }

  public void addInformation(Origin o, String message) {
    add(new MyDiagnostic(o, DiagnosticSeverity.Information, message));
  }

  public void addHint(Origin o, String message) {
    add(new MyDiagnostic(o, DiagnosticSeverity.Hint, message));
  }
}
