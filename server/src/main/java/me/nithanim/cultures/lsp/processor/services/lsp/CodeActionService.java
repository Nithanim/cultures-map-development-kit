package me.nithanim.cultures.lsp.processor.services.lsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.commands.CommandInformation;
import me.nithanim.cultures.lsp.processor.services.SourceCodeIntelligenceService;
import me.nithanim.cultures.lsp.processor.services.lsp.helper.ActualParameterPair;
import me.nithanim.cultures.lsp.processor.util.MyPosition;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import me.nithanim.cultures.lsp.processor.util.Uri;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeActionService {
  @Autowired SourceCodeIntelligenceService sourceCodeIntelligenceService;

  public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(CodeActionParams params) {
    Range selection = params.getRange();
    if (selection.getStart().getLine() != selection.getEnd().getLine()
        || selection.getStart().getCharacter() != selection.getEnd().getCharacter()) {
      // for now we just ignore multi-line and generally all "selections"
      return CompletableFuture.completedFuture(null);
    }

    CulturesIniCommand command = getCommandOnLine(params);
    if (command == null) {
      return CompletableFuture.completedFuture(null);
    }
    if (command.getCommandType().getCommandInformation().isSpecial()) {
      // Weird commands let the code below explode because it depends on exact param matches
      return CompletableFuture.completedFuture(null);
    }

    List<Either<Command, CodeAction>> actions = new ArrayList<>();

    List<ActualParameterPair> parameterPairs = ActualParameterPair.of(command);
    for (ActualParameterPair parameterPair : parameterPairs) {

      Range parameterRange = parameterPair.getParameterActual().getOrigin().getRange();
      if (contains(parameterRange, selection.getStart().getCharacter())) {
        if (parameterPair.getParameterInformation().getNumberHintsBitfield() != null) {

          actions.addAll(
              generateNumberHintsBitfieldActions(
                  params.getTextDocument().getUri(),
                  parameterPair.getParameterActual(),
                  parameterPair.getParameterInformation()));
        }
      }
    }

    return CompletableFuture.completedFuture(actions.isEmpty() ? null : actions);
  }

  private CulturesIniCommand getCommandOnLine(CodeActionParams params) {
    return sourceCodeIntelligenceService.getByPositionOnlyCommand(
        new MyPosition(
            new SourceFile(Uri.of(params.getTextDocument().getUri())),
            params.getRange().getStart()));
  }

  private List<Either<Command, CodeAction>> generateNumberHintsBitfieldActions(
      String documentUri,
      CulturesIniCommand.Parameter parameterActual,
      CommandInformation.ParameterInformation parameterMeta) {
    int currentValue = parameterActual.getValueAsInt();

    List<Either<Command, CodeAction>> actions = new ArrayList<>();

    List<String> bitfieldHints = parameterMeta.getNumberHintsBitfield();
    for (int i = 0; i < bitfieldHints.size(); i++) {
      CodeAction codeAction = new CodeAction();
      int updatedValue;
      if ((currentValue & (1 << i)) == 0) {
        codeAction.setTitle("Behaviours: Add " + bitfieldHints.get(i));
        updatedValue = currentValue | (1 << i);
      } else {
        codeAction.setTitle("Behaviours: Remove " + bitfieldHints.get(i));
        updatedValue = currentValue & (~(1 << i));
      }
      codeAction.setKind(CodeActionKind.QuickFix);
      Range parameterActualRange = parameterActual.getOrigin().getRange();
      codeAction.setEdit(
          singleEdit(documentUri, String.valueOf(updatedValue), parameterActualRange));
      actions.add(Either.forRight(codeAction));
    }

    return actions;
  }

  private WorkspaceEdit singleEdit(String documentUri, String change, Range parameterActualRange) {
    TextEdit edit = new TextEdit(parameterActualRange, change);
    Map<String, List<TextEdit>> changes = singleChange(documentUri, edit);
    return new WorkspaceEdit(changes);
  }

  private Map<String, List<TextEdit>> singleChange(String uri, TextEdit edit) {
    return Collections.singletonMap(uri, Collections.singletonList(edit));
  }

  public boolean isOverlappedBy(Range a, Range b) {
    return contains(b, a.getStart().getCharacter())
        || contains(b, a.getEnd().getCharacter())
        || contains(a, b.getStart().getCharacter());
  }

  public boolean contains(Range r, int x) {
    return r.getStart().getCharacter() <= x && x <= r.getEnd().getCharacter();
  }
}
