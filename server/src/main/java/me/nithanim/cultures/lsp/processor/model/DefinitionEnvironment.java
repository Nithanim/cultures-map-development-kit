package me.nithanim.cultures.lsp.processor.model;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import me.nithanim.cultures.lsp.processor.events.ConstantsResolvedEvent;
import me.nithanim.cultures.lsp.processor.events.SourceFileChangedEvent;
import me.nithanim.cultures.lsp.processor.events.SourceFileDeletedEvent;
import me.nithanim.cultures.lsp.processor.events.SourceFileParsedEvent;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniInclude;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniLine;
import me.nithanim.cultures.lsp.processor.services.ConstantsResolverService;
import me.nithanim.cultures.lsp.processor.services.PathResolverService;
import me.nithanim.cultures.lsp.processor.services.SourceFileContentService;
import me.nithanim.cultures.lsp.processor.services.SourceFileParsingService;
import me.nithanim.cultures.lsp.processor.services.clientpersistent.DiagnosticsService;
import me.nithanim.cultures.lsp.processor.services.commands.CommandParameterLengthCheckService;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import me.nithanim.cultures.lsp.processor.util.Uri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ReturnValuesAreNonnullByDefault
@ParametersAreNonnullByDefault
public class DefinitionEnvironment {
  /** Holds the parsed lines in the same order as in the file. */
  private final Map<SourceFile, List<? extends CulturesIniLine>> sourceFileToLines =
      new HashMap<>();
  /** Holds the inclusion references from one file to all other files */
  private final Map<SourceFile, List<SourceFile>> sourceFileDependencies = new HashMap<>();

  @Autowired private ApplicationEventPublisher publisher;
  @Autowired private SourceFileContentService sourceFileContentService;
  @Autowired private SourceFileParsingService sourceFileParsingService;
  @Autowired private DiagnosticsService diagnosticsService;
  @Autowired private PathResolverService pathResolverService;
  @Autowired private ConstantsResolverService constantsResolverService;
  @Autowired private CommandParameterLengthCheckService commandParameterLengthCheckService;

  private List<? extends CulturesIniLine> allLinesRaw = Collections.emptyList();

  private void updateFile(SourceFile sf, List<? extends CulturesIniLine> lines) {
    sourceFileToLines.put(sf, lines);
    List<SourceFile> includes =
        lines.stream()
            .filter(l -> l instanceof CulturesIniInclude)
            .map(l -> (CulturesIniInclude) l)
            .map(
                l ->
                    pathResolverService.resolvePartialPathToUri(
                        l.getOriginPath().getSourceFile(), l.getPath()))
            .map(SourceFile::new)
            .collect(Collectors.toList());
    sourceFileDependencies.put(sf, includes);
  }

  public void removeFile(SourceFile sf) {
    sourceFileToLines.remove(sf);
    sourceFileDependencies.remove(sf);
  }

  public List<? extends CulturesIniLine> getLines(SourceFile sf) {
    List<? extends CulturesIniLine> lines = sourceFileToLines.get(sf);
    if (lines != null) {
      return lines;
    } else {
      try {
        // Workaround for e.g. when server connects after client already opened files
        onSourceFileChanged(new SourceFileChangedEvent(this, sf));
        return sourceFileToLines.get(sf);
      } catch (IOException ex) {
        throw new UncheckedIOException(ex);
      }
    }
  }

  public List<? extends CulturesIniLine> getAllLinesRaw() {
    return allLinesRaw;
  }

  public List<? extends CulturesIniLine> getAllLinesResolved() {
    return constantsResolverService.getResolvedLines();
  }

  public List<? extends CulturesIniLine> calculateAllLines() {
    Set<SourceFile> seen = new HashSet<>();

    List<CulturesIniLine> result = new ArrayList<>();
    Backlog<CulturesIniLine> backlog = new Backlog<>();
    List<? extends CulturesIniLine> entryLines =
        sourceFileToLines.getOrDefault(pathResolverService.getEntry(), Collections.emptyList());
    backlog.insert(entryLines);

    while (backlog.hasNext()) {
      CulturesIniLine line = backlog.next();
      if (line instanceof CulturesIniInclude) {
        CulturesIniInclude inc = (CulturesIniInclude) line;
        Uri resolved =
            pathResolverService.resolvePartialPathToUri(
                inc.getOriginPath().getSourceFile(), inc.getPath());
        SourceFile sf = new SourceFile(resolved);

        if (seen.add(sf)) {
          List<? extends CulturesIniLine> l = getLinesAndInitIfNeeded(sf);
          backlog.insert(l);
        } else {
          // show error
        }
      } else {
        result.add(line);
      }
    }
    return result;
  }

  /** Workaround method if we need the content of includes but they have not been touched before. */
  private List<? extends CulturesIniLine> getLinesAndInitIfNeeded(SourceFile sourceFile) {
    List<? extends CulturesIniLine> l = sourceFileToLines.get(sourceFile);
    if (l == null) {
      try {
        String content = sourceFileContentService.getContent(sourceFile);
        SourceFileParsingService.ParsingResult r =
            sourceFileParsingService.parseSourceFile(sourceFile, content);
        diagnosticsService.updateDiagnostics(
            sourceFile, DiagnosticsService.Type.PARSING, r.getDiagnostics());
        return r.getLines();
      } catch (IOException ex) {
        // we only care for the data here, not displaying exceptions
        return Collections.emptyList();
      }
    } else {
      return l;
    }
  }

  @EventListener
  public void onSourceFileChanged(SourceFileChangedEvent evt) throws IOException {
    SourceFile sourceFile = evt.getSourceFile();
    SourceFileParsingService.ParsingResult r =
        sourceFileParsingService.parseSourceFile(
            sourceFile, sourceFileContentService.getContent(sourceFile));
    diagnosticsService.updateDiagnostics(
        sourceFile, DiagnosticsService.Type.PARSING, r.getDiagnostics());
    updateFile(sourceFile, r.getLines());

    allLinesRaw = calculateAllLines();
    publisher.publishEvent(new SourceFileParsedEvent(this, sourceFile));
    constantsResolverService.updateAllLines(allLinesRaw);

    List<? extends CulturesIniLine> allLines =
        commandParameterLengthCheckService.onData(getAllLinesResolved());
    publisher.publishEvent(new ConstantsResolvedEvent(this, sourceFile, allLines));

    diagnosticsService.publishAll();
  }

  @EventListener
  public void onSourceFileDeleted(SourceFileDeletedEvent evt) {
    SourceFile sourceFile = evt.getSourceFile();
    removeFile(sourceFile);
  }

  private static class Backlog<T> {
    private ArrayDeque<ArrayDeque<T>> all = new ArrayDeque<>();

    public void insert(List<? extends T> t) {
      if (t.isEmpty()) {
        return; // empties do not add any value but increase complexity
      }
      ArrayDeque<T> n = new ArrayDeque<>(t.size());
      n.addAll(t);
      all.addFirst(n);
    }

    public T next() {
      ArrayDeque<T> f = all.getFirst();
      T e = f.remove();
      if (f.isEmpty()) {
        all.remove();
      }
      return e;
    }

    public boolean hasNext() {
      return !all.isEmpty();
    }
  }
}
