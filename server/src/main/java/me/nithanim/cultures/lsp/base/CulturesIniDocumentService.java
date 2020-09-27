package me.nithanim.cultures.lsp.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.SneakyThrows;
import me.nithanim.cultures.lsp.processor.services.lsp.DocumentLinkService;
import me.nithanim.cultures.lsp.processor.services.SourceFileContentService;
import me.nithanim.cultures.lsp.processor.services.lsp.CodeActionService;
import me.nithanim.cultures.lsp.processor.services.lsp.CompletionService;
import me.nithanim.cultures.lsp.processor.services.lsp.HoverService;
import me.nithanim.cultures.lsp.processor.services.lsp.SignatureHelpService;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import me.nithanim.cultures.lsp.processor.util.Uri;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.DocumentLink;
import org.eclipse.lsp4j.DocumentLinkParams;
import org.eclipse.lsp4j.FoldingRange;
import org.eclipse.lsp4j.FoldingRangeRequestParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SignatureHelpParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CulturesIniDocumentService extends FullTextDocumentService {
  private static final Logger logger = LoggerFactory.getLogger(CulturesIniDocumentService.class);

  @Autowired private DocumentLinkService documentLinkService;
  @Autowired private SourceFileContentService sourceFileContentService;
  @Autowired private SignatureHelpService signatureHelpService;
  @Autowired private HoverService hoverService;
  @Autowired private CodeActionService codeActionService;
  @Autowired private CompletionService completionService;

  @Override
  public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
      CompletionParams params) {
    logger.info("Req Completion for " + params.getTextDocument().getUri());
    return completionService.completion(params);
  }

  @Override
  public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>>
      definition(DefinitionParams params) {
    logger.info("Req Definition for " + params.getTextDocument().getUri());
    // Location defLoc = culturesModel.findDefinition(position.getTextDocument().getSourceFile(),
    // position.getPosition());
    List<Location> l = new ArrayList<>();
    // if (defLoc != null) {
    //    l.add(defLoc);
    // }
    return CompletableFuture.completedFuture(Either.forLeft(l));
  }

  @Override
  public CompletableFuture<List<DocumentLink>> documentLink(DocumentLinkParams params) {
    logger.info("Req Link for " + params.getTextDocument().getUri());
    SourceFile sf = new SourceFile(Uri.of(params.getTextDocument().getUri()));
    List<DocumentLink> links = documentLinkService.getLinks(sf);
    CompletableFuture<List<DocumentLink>> future = new CompletableFuture<>();
    future.complete(links);
    return future;

    /*List<DocumentLink> links = new ArrayList<>();
    TextDocumentItem doc = this.openDocuments.get(params.getTextDocument().getUri());
    String[] lines = doc.getText().split("\\r?\\n");
    // (?<=\s|"|\/)\$[a-z]+\$\\[a-zA-Z_\\-\\\.]+(?="|\s)
    Pattern p = Pattern.compile("(?<=\\s|\"|\\/)\\$[a-z]+\\$\\\\[a-zA-Z_\\\\-\\\\\\.]+(?=\"|\\s)");
    for (int i = 0; i < lines.length; i++) {
        String line = lines[i];
        Matcher m = p.matcher(line);
        while (m.find()) {
            String potentialPath = m.group();
            if (potentialPath.startsWith("$maproot$")) {
                if (params.getTextDocument().getUri().endsWith("/map.ini")) {
                    DocumentLink dl = new DocumentLink();
                    dl.setTarget(resolvePathToUri(potentialPath));
                    dl.setRange(new Range(new Position(i, m.start()), new Position(i, m.end())));
                    links.add(dl);
                }
            }
        }
    }

    return CompletableFuture.completedFuture(links);
    */
  }

  @Override
  @SneakyThrows
  public CompletableFuture<List<FoldingRange>> foldingRange(FoldingRangeRequestParams params) {
    logger.info("Req FoldingRange for " + params.getTextDocument().getUri());
    String text =
        sourceFileContentService.getContent(
            new SourceFile(Uri.of(params.getTextDocument().getUri())));
    String[] lines = text.split("\r?\n");

    List<FoldingRange> foldings = new ArrayList<>();
    int lastCatLine = -1;
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      if (line.startsWith("[")) {
        if (lastCatLine != -1) {
          foldings.add(new FoldingRange(lastCatLine, i - 1));
        }
        lastCatLine = i;
      }
    }
    if (lastCatLine != -1) {
      foldings.add(new FoldingRange(lastCatLine, lines.length - 1));
    }
    return CompletableFuture.completedFuture(foldings);
  }

  @Override
  public CompletableFuture<DocumentLink> documentLinkResolve(DocumentLink params) {
    throw new UnsupportedOperationException();
  }

  @Override
  public CompletableFuture<SignatureHelp> signatureHelp(SignatureHelpParams params) {
    logger.debug("Req SignatureHelp for " + params);
    return signatureHelpService.generateSignatureHelp(params);
  }

  @Override
  public CompletableFuture<Hover> hover(HoverParams params) {
    logger.debug("Req Hover for " + params.getTextDocument().getUri());
    return hoverService.generateHover(params);
  }

  @Override
  public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(CodeActionParams params) {
    logger.info("Req CodeAction for " + params.getTextDocument().getUri());
    return codeActionService.codeAction(params);
  }

  @SneakyThrows
  private void validateWorkspace() {
    /*CulturesModel mainModel = culturesModel = new CulturesModel();
    Path mapini = workspacePath.resolve("map.ini");
    if (!Files.exists(mapini)) {
        client.showMessage(new MessageParams(MessageType.Error, "No map.ini found!"));
        return;
    }
    Diagnostics diagnostics = new Diagnostics();

    Path langsDir = workspacePath.resolve("text");
    Files.walkFileTree(langsDir, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if (dir.equals(langsDir)) {
                return FileVisitResult.CONTINUE;
            }
            CulturesModel cm = new CulturesModel();
            Path p = dir.resolve("strings.ini");
            CulturesIniParsingContext pctx = new CulturesIniParsingContext(p, cm, false);
            if (Files.exists(p)) {
                Test.doWork(new ByteArrayInputStream(Files.readAllBytes(p)), diagnostics, p.toUri().toString(), pctx);
                mainModel.addStrings(dir.getFileName().toString(), cm);
            } else {
                client.showMessage(new MessageParams(MessageType.Warning, "No strings.ini in " + dir.getFileName().toString() + "!"));
            }
            return FileVisitResult.SKIP_SUBTREE;
        }
    });

    CulturesIniParsingContext pctx = new CulturesIniParsingContext(mapini, culturesModel, false);
    Test.doWork(getBytes(workspaceUri + "/map.init"), diagnostics, mapini.toUri().toString(), pctx);
     */

  }

  private void validateStandaloneDocument(String uri) throws IOException {
    /*CulturesIniParsingContext ctx = new CulturesIniParsingContext(workspacePath, new CulturesModel(), true);
    Diagnostics diagnostics = new Diagnostics();
    Test.doWork(new ByteArrayInputStream(openDocuments.get(uri).getText().getBytes()), diagnostics, uri, ctx);
    diagnostics.publish(client);
     */
  }
}
