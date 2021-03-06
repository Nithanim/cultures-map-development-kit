package me.nithanim.cultures.lsp.base;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.Setter;
import lombok.SneakyThrows;
import me.nithanim.cultures.lsp.processor.services.lsp.CommandExecutionService;
import org.eclipse.lsp4j.CodeLensOptions;
import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.DocumentLinkOptions;
import org.eclipse.lsp4j.ExecuteCommandOptions;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.SignatureHelpOptions;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.WorkspaceFoldersOptions;
import org.eclipse.lsp4j.WorkspaceSymbolParams;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CulturesIniLanguageServer implements LanguageServer, LanguageClientAware {
  private static final Logger logger = LoggerFactory.getLogger(CulturesIniLanguageServer.class);

  private LanguageClient client;

  @Setter private Runnable onExit;

  @Autowired private CulturesIniDocumentService documentService;

  @Autowired private me.nithanim.cultures.lsp.processor.services.WorkspaceService workspaceService;

  @Autowired private CommandExecutionService commandExecutionService;

  @Override
  @SneakyThrows
  public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
    logger.info("Initializing language server by stating capabilities");
    String workspaceUri = params.getRootUri();
    if (workspaceUri == null) {
      // single file
    } else {
      workspaceService.init(workspaceUri);
    }

    ServerCapabilities capabilities = new ServerCapabilities();
    capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
    capabilities.setCodeActionProvider(true);
    capabilities.setCompletionProvider(new CompletionOptions(false, null));
    capabilities.setDefinitionProvider(false);
    capabilities.setReferencesProvider(false);
    capabilities.setDocumentLinkProvider(new DocumentLinkOptions(true));
    capabilities.setFoldingRangeProvider(true);
    capabilities.setExecuteCommandProvider(
        new ExecuteCommandOptions(commandExecutionService.getCommands()));
    capabilities.setCodeLensProvider(new CodeLensOptions(false));

    capabilities.setTypeDefinitionProvider(false);
    capabilities.setSignatureHelpProvider(new SignatureHelpOptions(Collections.emptyList()));
    capabilities.setHoverProvider(true);

    WorkspaceFoldersOptions wfo = new WorkspaceFoldersOptions();
    wfo.setSupported(false);

    return CompletableFuture.completedFuture(new InitializeResult(capabilities));
  }

  @Override
  public CompletableFuture<Object> shutdown() {
    logger.info("Req shutdown");
    // Workaround when shutdown is called but exit is never received.
    // In some cases even the result of this method is never received because client already shut
    // down.
    // Therefore, we just exit based on timeout because we already got the command to shutdown
    // anyways and don't have to wait for something.
    Thread t =
        new Thread(
            () -> {
              try {
                Thread.sleep(2000);
              } catch (InterruptedException e) {
                // ignore
              }
              logger.info("Exit based on timeout");
              exit();
            });
    t.setDaemon(true); // Don't block needlessly if we get the exit command successfully.
    t.start();
    return CompletableFuture.completedFuture(new Object());
  }

  @Override
  public void exit() {
    logger.info("Req exit");
    onExit.run();
  }

  @Override
  public TextDocumentService getTextDocumentService() {
    return documentService;
  }

  @Override
  public WorkspaceService getWorkspaceService() {
    return new WorkspaceService() {
      @Override
      public CompletableFuture<List<? extends SymbolInformation>> symbol(
          WorkspaceSymbolParams params) {
        logger.info("Reqws symbol " + params.getQuery());
        client.logMessage(new MessageParams(MessageType.Log, "Received workspace symbol"));
        return null;
      }

      @Override
      public void didChangeConfiguration(DidChangeConfigurationParams params) {
        logger.info("Reqws didChangeConfiguration " + params.getSettings());
        client.logMessage(new MessageParams(MessageType.Log, "Received workspace didchange"));
      }

      @Override
      public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        logger.info("Reqws didChangeWatchedFiles " + params.getChanges());
        client.logMessage(
            new MessageParams(MessageType.Log, "Received workspace changewatchedfiles"));
      }

      @Override
      public CompletableFuture<Object> executeCommand(ExecuteCommandParams params) {
        logger.info("Reqws executeCommand " + params.getCommand() + " " + params.getArguments());
        return commandExecutionService.execute(params);
      }
    };
  }

  @Override
  public void connect(LanguageClient client) {
    this.client = client;
    commandExecutionService.setClient(client);
  }
}
