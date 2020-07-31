package me.nithanim.cultures.lsp.processor.parsing;

import lombok.AllArgsConstructor;
import me.nithanim.cultures.lsp.processor.util.DiagnosticsCollector;
import me.nithanim.cultures.lsp.processor.util.MyDiagnostic;
import me.nithanim.cultures.lsp.processor.util.Origin;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

@AllArgsConstructor
public class CulturesIniErrorListener extends BaseErrorListener {
  private final SourceFile sourceFile;
  private final DiagnosticsCollector diagnostics;

  @Override
  public void syntaxError(
      Recognizer<?, ?> recognizer,
      Object offendingSymbol,
      int line,
      int charPositionInLine,
      String msg,
      RecognitionException e) {
    Range range =
        new Range(
            new Position(line - 1, charPositionInLine),
            new Position(line - 1, charPositionInLine + 10));
    diagnostics.add(
        new MyDiagnostic(
            new Origin(sourceFile, range), DiagnosticSeverity.Error, "Parse-error: " + msg));
  }
}
