package me.nithanim.cultures.lsp.processor.services;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniInclude;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniLine;
import me.nithanim.cultures.lsp.processor.model.DefinitionEnvironment;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import me.nithanim.cultures.lsp.processor.util.Uri;
import org.eclipse.lsp4j.DocumentLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentLinkService {
  @Autowired private DefinitionEnvironment definitionEnvironment;
  @Autowired private PathResolverService pathResolverService;

  public List<DocumentLink> getLinks(SourceFile sourceFile) {
    List<? extends CulturesIniLine> allLines = definitionEnvironment.getLines(sourceFile);

    return allLines.stream()
        .filter(l -> l instanceof CulturesIniInclude)
        .map(i -> (CulturesIniInclude) i)
        .map(
            i -> {
              Uri uri = pathResolverService.resolvePartialPathToUri(sourceFile, i.getPath());
              if (uri != null) {
                return new DocumentLink(i.getOriginPath().getRange(), uri.toString());
              } else {
                return null;
              }
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
