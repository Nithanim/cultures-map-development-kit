package me.nithanim.cultures.lsp.processor.services.lsp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommandType;
import me.nithanim.cultures.lsp.processor.lines.commands.CommandInformation;
import me.nithanim.cultures.lsp.processor.services.SourceCodeIntelligenceService;
import me.nithanim.cultures.lsp.processor.services.SourceFileContentService;
import me.nithanim.cultures.lsp.processor.services.lsp.helper.DocumentationService;
import me.nithanim.cultures.lsp.processor.util.MyPosition;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import me.nithanim.cultures.lsp.processor.util.Uri;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompletionService {
  @Autowired private SourceCodeIntelligenceService sourceCodeIntelligenceService;
  @Autowired private SourceFileContentService sourceFileContentService;
  @Autowired private DocumentationService documentationService;

  public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
      CompletionParams params) {

    if (params.getPosition().getCharacter()
        != 0) { // For simplicity only support empty line for now
      return CompletableFuture.completedFuture(null);
    }

    CulturesIniCategoryType cat =
        sourceCodeIntelligenceService.getCurrentCategoryType(
            new MyPosition(
                new SourceFile(Uri.of(params.getTextDocument().getUri())), params.getPosition()));
    if (cat == null) {
      return CompletableFuture.completedFuture(null);
    }

    List<CompletionItem> completionItems = new ArrayList<>();
    for (CulturesIniCommandType commandType : cat.getCommandTypes()) {
      var commandInformation = commandType.getCommandInformation();
      CompletionItem ci = new CompletionItem();
      ci.setKind(CompletionItemKind.Function);
      ci.setLabel(commandInformation.getDisplayName());
      ci.setInsertText(getTextToInsert(commandInformation));
      ci.setDocumentation(documentationService.createCommandDocumentation(commandType));
      completionItems.add(ci);
    }
    return CompletableFuture.completedFuture(Either.forRight(new CompletionList(completionItems)));
  }

  private String getTextToInsert(CommandInformation commandInformation) {
    if (commandInformation.getParametersMinimum() > 0) {
      return commandInformation.getDisplayName() + " ";
    } else {
      return commandInformation.getDisplayName();
    }
  }
}
