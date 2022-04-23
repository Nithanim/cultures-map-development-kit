package me.nithanim.cultures.lsp.processor.services.lsp.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommandType;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniLine;
import me.nithanim.cultures.lsp.processor.lines.UnknownCulturesIniLine;
import me.nithanim.cultures.lsp.processor.lines.commands.CommandInformation;
import me.nithanim.cultures.lsp.processor.services.SourceCodeIntelligenceService;
import me.nithanim.cultures.lsp.processor.util.MyPosition;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompletionForCommandsService {
  @Autowired private SourceCodeIntelligenceService sourceCodeIntelligenceService;
  @Autowired private DocumentationService documentationService;

  public CompletableFuture<Either<List<CompletionItem>, CompletionList>> handleCommandComplete(
      CompletionParams params, MyPosition cursorPosition, CulturesIniLine line) {
    String name;
    Range commandRange;
    if (line == null) {
      name = "";
      commandRange = new Range(params.getPosition(), params.getPosition());
    } else if (line instanceof UnknownCulturesIniLine) {
      var command = (UnknownCulturesIniLine) line;
      name = command.getName();
      commandRange = command.getOriginBaseCommand().getRange();
    } else if (line instanceof CulturesIniCommand) {
      var command = (CulturesIniCommand) line;
      name = command.getCommandType().name();
      commandRange = command.getOriginBaseCommand().getRange();
    } else {
      return CompletableFuture.completedFuture(null);
    }

    CulturesIniCategoryType cat =
        sourceCodeIntelligenceService.getCurrentCategoryType(cursorPosition);
    if (cat == null) {
      return CompletableFuture.completedFuture(null);
    }

    String searchString = getSearchString(name, params.getPosition().getCharacter());
    List<CulturesIniCommandType> possibleCommandTypes =
        cat.getCommandTypes().stream()
            .filter(t -> t.name().startsWith(searchString))
            .collect(Collectors.toList());

    List<CompletionItem> completionItems = new ArrayList<>();
    for (CulturesIniCommandType commandType : possibleCommandTypes) {
      var commandInformation = commandType.getCommandInformation();
      CompletionItem ci = createCompletionItemCommand(commandRange, commandInformation);
      completionItems.add(ci);
    }
    return CompletableFuture.completedFuture(Either.forRight(new CompletionList(completionItems)));
  }

  private CompletionItem createCompletionItemCommand(
      Range commandRange, CommandInformation commandInformation) {
    CompletionItem ci = new CompletionItem();
    ci.setKind(CompletionItemKind.Function);
    ci.setLabel(commandInformation.getDisplayName());
    ci.setTextEdit(Either.forLeft(getTextEditCommand(commandRange, commandInformation)));
    ci.setDocumentation(documentationService.createCommandDocumentation(commandInformation));
    return ci;
  }

  private String getSearchString(String currentText, int cursorPositionRelativeParamStart) {
    return currentText
        .toUpperCase()
        .substring(0, Math.min(currentText.length(), cursorPositionRelativeParamStart));
  }

  private TextEdit getTextEditCommand(Range range, CommandInformation commandInformation) {
    TextEdit te = new TextEdit();
    te.setRange(range);
    te.setNewText(commandInformation.getDisplayName());
    return te;
  }
}
