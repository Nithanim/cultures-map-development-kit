package me.nithanim.cultures.lsp.processor.services.lsp.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import me.nithanim.cultures.lsp.processor.lines.CommandInformationHolder;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommandType;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniLine;
import me.nithanim.cultures.lsp.processor.lines.CulturesMissionGoalType;
import me.nithanim.cultures.lsp.processor.lines.CulturesMissionResultType;
import me.nithanim.cultures.lsp.processor.lines.commands.CommandInformation;
import me.nithanim.cultures.lsp.processor.util.MyPosition;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.xtext.xbase.lib.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompletionForParametersService {
  @Autowired private ParameterService parameterService;
  @Autowired private DocumentationService documentationService;

  public CompletableFuture<Either<List<CompletionItem>, CompletionList>> handleParamComplete(
      MyPosition cursorPosition, CulturesIniLine line) {
    var cmd = (CulturesIniCommand) line;
    var paramPair = getParameterOnCursor(cmd, cursorPosition.getPosition());
    if (paramPair == null) {
      return CompletableFuture.completedFuture(null);
    }

    if ((cmd.getCommandType() == CulturesIniCommandType.GOAL
            || cmd.getCommandType() == CulturesIniCommandType.RESULT)
        && paramPair.getValue().getIndex() == 0) {
      String currentText = (paramPair.getKey() == null) ? "" : paramPair.getKey().getValue();
      int cursorPositionRelativeParamStart =
          getCursorPositionOnParamValue(cursorPosition, paramPair);
      String searchString = getSearchString(currentText, cursorPositionRelativeParamStart);
      List<CommandInformationHolder> possibleCommandTypes =
          Arrays.stream(getAllGoalsOrResults(cmd))
              .filter(t -> autocompleteValueFilter(t, searchString))
              .collect(Collectors.toList());
      List<CompletionItem> completionItems = new ArrayList<>();
      for (var subCommandType : possibleCommandTypes) {
        var subCommandInformation =
            ((CommandInformationHolder) subCommandType).getCommandInformation();
        // VS Code REALLY does NOT like having the quotes in the range; it just discards entries
        // silently
        Range paramRange =
            (paramPair.getKey() == null)
                ? createEmptyRange(cursorPosition)
                : paramPair.getKey().getOriginValue().getRange();
        var completionItem =
            createCompletionItemParameterSpecial(
                paramRange, subCommandInformation, paramPair.getKey() == null);
        completionItems.add(completionItem);
      }
      return CompletableFuture.completedFuture(
          Either.forRight(new CompletionList(completionItems)));
    } else {
      return CompletableFuture.completedFuture(null);
    }
  }

  private Range createEmptyRange(MyPosition cursorPosition) {
    return createEmptyRange(cursorPosition.getPosition());
  }

  private Range createEmptyRange(Position cursorPosition) {
    return new Range(cursorPosition, cursorPosition);
  }

  private boolean autocompleteValueFilter(
      CommandInformationHolder commandInformationHolder, String searchString) {
    return commandInformationHolder
        .getCommandInformation()
        .getDisplayName()
        .toUpperCase()
        .startsWith(searchString);
  }

  private CommandInformationHolder[] getAllGoalsOrResults(CulturesIniCommand cmd) {
    return CulturesIniCommandType.GOAL == cmd.getCommandType()
        ? CulturesMissionGoalType.values()
        : CulturesMissionResultType.values();
  }

  private Pair<CulturesIniCommand.Parameter, CommandInformation.ParameterInformation>
      getParameterOnCursor(CulturesIniCommand cmd, Position cursor) {
    CommandInformation ci = parameterService.getCraftedCommandInformation(cmd);
    int commonParameterCount = Math.min(cmd.getParameters().size(), ci.getParameters().size());

    // block for handling for existing parameters
    for (int i = 0; i < commonParameterCount; i++) {
      var metaParam = ci.getParameter(i);
      var actualParam = cmd.getParameter(i);

      Range r = actualParam.getOriginAll().getRange();
      if (r.getStart().getCharacter() <= cursor.getCharacter()
          && cursor.getCharacter() <= r.getEnd().getCharacter()) {
        return new Pair<>(actualParam, metaParam);
      }
    }

    // block for handling not-yet specified parameters
    // (completion without any base to complete from)
    if (isMissingParameterValues(cmd, ci)) {
      return new Pair<>(
          null,
          ci.getParameter(
              commonParameterCount /* is already the index because size is one higher than max index */));
    }

    // only when all params specified (?)
    return null;
  }

  private CompletionItem createCompletionItemParameterSpecial(
      Range commandRange, CommandInformation commandInformation, boolean addQuotes) {
    CompletionItem ci = new CompletionItem();
    ci.setKind(CompletionItemKind.EnumMember);
    ci.setLabel(commandInformation.getDisplayName());
    ci.setTextEdit(Either.forLeft(getTextEditParameterString(commandRange, commandInformation, addQuotes)));
    ci.setDocumentation(documentationService.createCommandDocumentation(commandInformation));
    return ci;
  }

  private int getCursorPositionOnParamValue(
      MyPosition cursorPosition,
      Pair<CulturesIniCommand.Parameter, CommandInformation.ParameterInformation> paramPair) {
    if (paramPair.getKey() == null) {
      // In case param is missing completely
      return 0;
    }
    return Math.max(
        0,
        cursorPosition.getPosition().getCharacter()
            - paramPair.getKey().getOriginValue().getRange().getStart().getCharacter());
  }

  private boolean isMissingParameterValues(CulturesIniCommand cmd, CommandInformation ci) {
    return cmd.getParameters().size() < ci.getParameters().size();
  }

  private TextEdit getTextEditParameterString(
      Range range, CommandInformation commandInformation, boolean addQuotes) {
    TextEdit te = new TextEdit();
    te.setRange(range);
    if (addQuotes) {
      te.setNewText('\"' + commandInformation.getDisplayName() + '\"');
    } else {
      te.setNewText(commandInformation.getDisplayName());
    }
    return te;
  }

  private String getSearchString(String currentText, int cursorPositionRelativeParamStart) {
    return currentText
        .toUpperCase()
        .substring(0, Math.min(currentText.length(), cursorPositionRelativeParamStart));
  }
}
