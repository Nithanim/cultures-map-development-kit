package me.nithanim.cultures.lsp.base;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import me.nithanim.cultures.lsp.base.events.ClientSourceFileChangedEvent;
import me.nithanim.cultures.lsp.base.events.ClientSourceFileClosedEvent;
import me.nithanim.cultures.lsp.base.events.ClientSourceFileOpenedEvent;
import me.nithanim.cultures.lsp.base.events.ClientSourceFileSavedEvent;
import me.nithanim.cultures.lsp.processor.services.clientpersistent.CodeLensService;
import me.nithanim.cultures.lsp.processor.services.clientpersistent.MyCodeLens;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import me.nithanim.cultures.lsp.processor.util.Uri;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CodeLensParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.DocumentFormattingParams;
import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.DocumentOnTypeFormattingParams;
import org.eclipse.lsp4j.DocumentRangeFormattingParams;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.DocumentSymbolParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.ReferenceParams;
import org.eclipse.lsp4j.RenameParams;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentPositionParams;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

abstract class FullTextDocumentService implements TextDocumentService {
  private static final Logger logger = LoggerFactory.getLogger(FullTextDocumentService.class);

  @Autowired private CodeLensService codeLensService;

  @Autowired private ApplicationEventPublisher applicationEventPublisher;

  @Override
  public CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem unresolved) {
    throw new UnsupportedOperationException();
  }

  @Override
  public CompletableFuture<Hover> hover(TextDocumentPositionParams position) {
    logger.info("Req Hover for " + position.getTextDocument().getUri());
    throw new UnsupportedOperationException();
  }

  @Override
  public CompletableFuture<SignatureHelp> signatureHelp(TextDocumentPositionParams position) {
    throw new UnsupportedOperationException();
  }

  @Override
  public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>>
      definition(TextDocumentPositionParams position) {
    logger.info("Req Definition for " + position.getTextDocument().getUri());
    throw new UnsupportedOperationException();
  }

  @Override
  public CompletableFuture<List<? extends Location>> references(ReferenceParams params) {
    logger.info("Req References for " + params.getTextDocument().getUri());
    throw new UnsupportedOperationException();
  }

  @Override
  public CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(
      TextDocumentPositionParams position) {
    logger.info("Req Highlight for " + position.getTextDocument().getUri());
    throw new UnsupportedOperationException();
  }

  @Override
  public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(
      DocumentSymbolParams params) {
    logger.info("Req Symbol for " + params.getTextDocument().getUri());
    throw new UnsupportedOperationException();
  }

  @Override
  public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(CodeActionParams params) {
    logger.info("Req CodeAction for " + params.getTextDocument().getUri());
    throw new UnsupportedOperationException();
  }

  /** Can show additional information over some thing. Like references/last changed/... */
  @Override
  public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params) {
    logger.info("Req CodeLens for " + params.getTextDocument().getUri());
    SourceFile sourceFile = new SourceFile(Uri.of(params.getTextDocument().getUri()));

    List<CodeLens> result;
    List<MyCodeLens> ir = codeLensService.getAll(sourceFile);
    if (ir == null) {
      result = null;
    } else {
      result = ir.stream().map(i -> i.getCodeLens()).collect(Collectors.toList());
    }

    return CompletableFuture.completedFuture(result);
  }

  @Override
  public CompletableFuture<CodeLens> resolveCodeLens(CodeLens unresolved) {
    throw new UnsupportedOperationException();
  }

  @Override
  public CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams params) {
    throw new UnsupportedOperationException();
  }

  @Override
  public CompletableFuture<List<? extends TextEdit>> rangeFormatting(
      DocumentRangeFormattingParams params) {
    throw new UnsupportedOperationException();
  }

  @Override
  public CompletableFuture<List<? extends TextEdit>> onTypeFormatting(
      DocumentOnTypeFormattingParams params) {
    throw new UnsupportedOperationException();
  }

  @Override
  public CompletableFuture<WorkspaceEdit> rename(RenameParams params) {
    logger.info("Req Rename for " + params.getTextDocument().getUri());
    throw new UnsupportedOperationException();
  }

  @Override
  @SneakyThrows
  public void didOpen(DidOpenTextDocumentParams params) {
    logger.info("Req DidOpen for " + params.getTextDocument().getUri());
    String uri = params.getTextDocument().getUri();
    applicationEventPublisher.publishEvent(
        new ClientSourceFileOpenedEvent(this, uri, params.getTextDocument().getText()));
  }

  @Override
  public void didChange(DidChangeTextDocumentParams params) {
    logger.info("Req DidChange for " + params.getTextDocument().getUri());
    String uri = params.getTextDocument().getUri();
    for (TextDocumentContentChangeEvent changeEvent : params.getContentChanges()) {
      // Will be full update because we specified that is all we support
      if (changeEvent.getRange() != null) {
        throw new UnsupportedOperationException("Range should be null for full document update.");
      }
      if (changeEvent.getRangeLength() != null) {
        throw new UnsupportedOperationException(
            "RangeLength should be null for full document update.");
      }

      applicationEventPublisher.publishEvent(
          new ClientSourceFileChangedEvent(this, uri, changeEvent.getText()));
    }
  }

  @Override
  public void didClose(DidCloseTextDocumentParams params) {
    logger.info("Req DidClose for " + params.getTextDocument().getUri());
    String uri = params.getTextDocument().getUri();
    applicationEventPublisher.publishEvent(new ClientSourceFileClosedEvent(this, uri));
  }

  @Override
  public void didSave(DidSaveTextDocumentParams params) {
    logger.info("Req DidSave for " + params.getTextDocument().getUri());
    applicationEventPublisher.publishEvent(
        new ClientSourceFileSavedEvent(this, params.getTextDocument().getUri(), params.getText()));
  }
}
