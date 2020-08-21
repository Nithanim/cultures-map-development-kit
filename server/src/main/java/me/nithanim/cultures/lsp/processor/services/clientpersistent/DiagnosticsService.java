package me.nithanim.cultures.lsp.processor.services.clientpersistent;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import static me.nithanim.cultures.lsp.db.public_.tables.Diagnostic.DIAGNOSTIC;
import me.nithanim.cultures.lsp.db.public_.tables.records.DiagnosticRecord;
import me.nithanim.cultures.lsp.processor.util.DiagnosticsCollector;
import me.nithanim.cultures.lsp.processor.util.MyDiagnostic;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.services.LanguageClient;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ReturnValuesAreNonnullByDefault
@ParametersAreNonnullByDefault
public class DiagnosticsService {
  /** Marks source files where we updated something so we need to send the data for it. */
  private final Set<SourceFile> dirty = new HashSet<>();
  /**
   * Stores for which files we sent data so we know if we can just overwrite it or need to delete it
   * if we have empty data.
   */
  private final Set<SourceFile> sent = new HashSet<>();

  @Autowired private DSLContext jooq;

  @Autowired private LanguageClient client;

  public void updateDiagnostics(Type type, DiagnosticsCollector dc) {
    updateDiagnostics(type, dc.getDiagnostics());
  }

  public void updateDiagnostics(Type type, List<MyDiagnostic> diagnostics) {
    update(type, diagnostics);
  }

  public void updateDiagnostics(SourceFile sourceFile, Type type, List<MyDiagnostic> diagnostics) {
    update(sourceFile, type, diagnostics);
  }

  public void update(Type type, List<MyDiagnostic> diagnostics) {
    dirty.addAll(
        diagnostics.stream().map(d -> d.getSourceFile()).distinct().collect(Collectors.toList()));
    jooq.deleteFrom(DIAGNOSTIC).where(DIAGNOSTIC.TYPE.eq(type.name())).execute();
    _update(type, diagnostics);
  }

  public void update(SourceFile sourceFile, Type type, List<MyDiagnostic> diagnostics) {
    dirty.add(sourceFile);
    jooq.deleteFrom(DIAGNOSTIC)
        .where(
            DIAGNOSTIC
                .TYPE
                .eq(type.name())
                .and(DIAGNOSTIC.SOURCE_FILE.eq(sourceFile.getUri().toString())))
        .execute();
    _update(type, diagnostics);
  }

  private void _update(Type type, List<MyDiagnostic> diagnostics) {
    InsertValuesStep3<DiagnosticRecord, String, String, MyDiagnostic> base =
        jooq.insertInto(DIAGNOSTIC)
            .columns(DIAGNOSTIC.SOURCE_FILE, DIAGNOSTIC.TYPE, DIAGNOSTIC.DATA);
    for (MyDiagnostic diagnostic : diagnostics) {
      base.values(diagnostic.getSourceFile().getUri().toString(), type.name(), diagnostic);
    }
    base.execute();
  }

  public void publishAll() {
    /*List<String> sourceFilesWithData =
        jooq.select(DIAGNOSTIC.SOURCE_FILE)
            .distinctOn(DIAGNOSTIC.SOURCE_FILE)
            .from(DIAGNOSTIC)
            .fetch(DIAGNOSTIC.SOURCE_FILE);
    Map<String, SourceFile> m =
        sent.stream().collect(Collectors.toMap(d -> d.getUri().toString(), d -> d));
    sourceFilesWithData.forEach(m::remove);
    //m.values().forEach(this::publish); // send empty diagnostics for files previously having diagnostics
    sent.removeAll(m.values());
    !actually handled when writing empty diagnostics (from on change) which marks file as dirty and is considered below; nice!
     */

    dirty.forEach(this::publish);
    dirty.clear();
  }

  private void publish(SourceFile dirty) {
    List<MyDiagnostic> diagnostics =
        jooq.select(DIAGNOSTIC.DATA)
            .from(DIAGNOSTIC)
            .where(DIAGNOSTIC.SOURCE_FILE.eq(dirty.getUri().toString()))
            .fetch(DIAGNOSTIC.DATA);

    client.publishDiagnostics(
        new PublishDiagnosticsParams(
            dirty.getUri().toString(),
            diagnostics.stream().map(MyDiagnostic::toDiagnostic).collect(Collectors.toList())));
    sent.add(dirty);
  }

  public enum Type {
    PARSING,
    CONSTANT_RESOLVER,
    PARAMETER_CHECK,
    COMMAND_CHECKER,
  }
}
