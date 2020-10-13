package me.nithanim.cultures.lsp.processor.services.lsp;

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
import me.nithanim.cultures.lsp.processor.services.SourceFileContentService;
import me.nithanim.cultures.lsp.processor.services.lsp.helper.DocumentationService;
import me.nithanim.cultures.lsp.processor.services.lsp.helper.ParameterService;
import me.nithanim.cultures.lsp.processor.util.MyPosition;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import me.nithanim.cultures.lsp.processor.util.Uri;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.xtext.xbase.lib.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompletionService {
  @Autowired private SourceCodeIntelligenceService sourceCodeIntelligenceService;
  @Autowired private SourceFileContentService sourceFileContentService;
  @Autowired private DocumentationService documentationService;
  @Autowired private ParameterService parameterService;

  public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
      CompletionParams params) {
    MyPosition cursorPosition =
        new MyPosition(
            new SourceFile(Uri.of(params.getTextDocument().getUri())), params.getPosition());

    CulturesIniLine line = sourceCodeIntelligenceService.getByPosition(cursorPosition);
    if (isPosInParameterSpace(cursorPosition.getPosition(), line)) {
      return handleParamComplete(cursorPosition, line);
    } else {
      return handleCommandComplete(params, cursorPosition, line);
    }
  }

  private CompletableFuture<Either<List<CompletionItem>, CompletionList>> handleCommandComplete(
      CompletionParams params, MyPosition cursorPosition, CulturesIniLine line) {
    String name;
    Range commandRange;
    boolean hasExistingParams = false;
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
      hasExistingParams = command.getParameters().size() > 0;
    } else {
      return CompletableFuture.completedFuture(null);
    }

    CulturesIniCategoryType cat =
        sourceCodeIntelligenceService.getCurrentCategoryType(cursorPosition);
    if (cat == null) {
      return CompletableFuture.completedFuture(null);
    }

    String searchString =
        name.toUpperCase()
            .substring(0, Math.min(name.length(), params.getPosition().getCharacter()));
    List<CulturesIniCommandType> possibleCommandTypes =
        cat.getCommandTypes().stream()
            .filter(t -> t.name().startsWith(searchString))
            .collect(Collectors.toList());

    List<CompletionItem> completionItems = new ArrayList<>();
    for (CulturesIniCommandType commandType : possibleCommandTypes) {
      var commandInformation = commandType.getCommandInformation();
      CompletionItem ci = createCompletionItem(commandRange, commandInformation, hasExistingParams);
      completionItems.add(ci);
    }
    return CompletableFuture.completedFuture(Either.forRight(new CompletionList(completionItems)));
  }

  private boolean isPosInParameterSpace(Position cursor, CulturesIniLine line) {
    if (!(line instanceof CulturesIniCommand)) {
      return false;
    }
    var cmd = (CulturesIniCommand) line;

    Range r = cmd.getOriginBaseCommand().getRange();
    return r.getEnd().getCharacter() < cursor.getCharacter();
  }

  private CompletionItem createCompletionItem(
      Range commandRange, CommandInformation commandInformation, boolean hasExistingParams) {
    CompletionItem ci = new CompletionItem();
    ci.setKind(CompletionItemKind.Function);
    ci.setLabel(commandInformation.getDisplayName());
    ci.setTextEdit(getTextEdit(commandRange, commandInformation, hasExistingParams));
    ci.setDocumentation(documentationService.createCommandDocumentation(commandInformation));
    return ci;
  }

  private TextEdit getTextEdit(
      Range range, CommandInformation commandInformation, boolean hasExistingParams) {
    String text;
    if (!hasExistingParams && commandInformation.getParametersMinimum() > 0) {
      text = commandInformation.getDisplayName() + " ";
    } else {
      text = commandInformation.getDisplayName();
    }
    TextEdit te = new TextEdit();
    te.setRange(range);
    te.setNewText(text);
    return te;
  }

  private CompletableFuture<Either<List<CompletionItem>, CompletionList>> handleParamComplete(
      MyPosition cursorPosition, CulturesIniLine line) {
    var cmd = (CulturesIniCommand) line;
    var paramPair = getParameterOnCursor(cmd, cursorPosition.getPosition());
    if(paramPair == null) {
      return CompletableFuture.completedFuture(null);
    }

    return CompletableFuture.completedFuture(null);
  }

  private Pair<CulturesIniCommand.Parameter, CommandInformation.ParameterInformation>
      getParameterOnCursor(CulturesIniCommand cmd, Position cursor) {
    CommandInformation ci = parameterService.getCraftedCommandInformation(cmd);
    int maxCommonParameterIndex = Math.min(cmd.getParameters().size(), ci.getParameters().size());

    // block for handling for existing parameters
    for (int i = 0; i < maxCommonParameterIndex; i++) {
      var metaParam = ci.getParameter(i);
      var actualParam = cmd.getParameter(i);

      Range r = actualParam.getOrigin().getRange();
      if (r.getStart().getCharacter() <= cursor.getCharacter()
          && cursor.getCharacter() <= r.getEnd().getCharacter()) {
        return new Pair<>(actualParam, metaParam);
      }
    }

    // block for handling not-yet specified parameters
    // (completion without any base to complete from)
    if (isMissingParameterValues(cmd, ci)) {
      return new Pair<>(null, ci.getParameter(maxCommonParameterIndex + 1));
    }

    // only when all params specified (?)
    return null;
  }

  private boolean isMissingParameterValues(CulturesIniCommand cmd, CommandInformation ci) {
    return cmd.getParameters().size() < ci.getParameters().size();
  }
}
