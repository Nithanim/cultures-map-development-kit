package me.nithanim.cultures.lsp.config;


import me.nithanim.cultures.lsp.processor.util.MyDiagnostic ;

public class DiagnosticBinding extends PassthroughBinding<MyDiagnostic> {
  public DiagnosticBinding() {
    super(MyDiagnostic.class);
  }
}
