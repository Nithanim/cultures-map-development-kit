package me.nithanim.cultures.lsp.processor.services.clientpersistent;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import me.nithanim.cultures.lsp.processor.util.DiagnosticsCollector;
import me.nithanim.cultures.lsp.processor.util.MyDiagnostic;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.services.LanguageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ReturnValuesAreNonnullByDefault
@ParametersAreNonnullByDefault
public class DiagnosticsService
    extends GenericKeyPerSourceService<DiagnosticsService.Type, MyDiagnostic> {
  @Autowired private LanguageClient client;

  @Nullable
  public List<MyDiagnostic> getAllDiagnostics(SourceFile sourceFile) {
    return getAll(sourceFile);
  }

  public void updateDiagnostics(Type type, DiagnosticsCollector dc) {
    updateDiagnostics(type, dc.getDiagnostics());
  }

  public void updateDiagnostics(Type type, List<MyDiagnostic> diagnostics) {
    update(type, diagnostics);
  }

  public void updateDiagnostics(SourceFile sourceFile, Type type, List<MyDiagnostic> diagnostics) {
    update(sourceFile, type, diagnostics);
  }

  public void deleteDiagnostics(SourceFile sourceFile) {
    delete(sourceFile);
  }

  @Override
  protected void _publish(SourceFile sf, List<MyDiagnostic> things) {
    List<Diagnostic> l =
        things.stream().map(MyDiagnostic::toDiagnostic).collect(Collectors.toList());

    PublishDiagnosticsParams dipa = new PublishDiagnosticsParams();
    dipa.setDiagnostics(l);
    dipa.setUri(sf.getUri().toString());
    client.publishDiagnostics(dipa);
  }

  public enum Type {
    PARSING,
    CONSTANT_RESOLVER,
    PARAMETER_CHECK,
    COMMAND_CHECKER,
  }
}
