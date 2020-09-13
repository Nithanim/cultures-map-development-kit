package me.nithanim.cultures.lsp.processor.services.lsp;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.services.SourceCodeIntelligenceService;
import me.nithanim.cultures.lsp.processor.services.lsp.helper.ActualParameterPair;
import me.nithanim.cultures.lsp.processor.util.MyPosition;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import me.nithanim.cultures.lsp.processor.util.Uri;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HoverService {
  @Autowired SourceCodeIntelligenceService sourceCodeIntelligenceService;

  public CompletableFuture<Hover> generateHover(HoverParams params) {
    CulturesIniCommand command =
        sourceCodeIntelligenceService.getByPositionOnlyCommand(
            new MyPosition(
                new SourceFile(Uri.of(params.getTextDocument().getUri())), params.getPosition()));
    if (command == null) {
      return CompletableFuture.completedFuture(null);
    }

    if (isHoverOnCommand(command, params)) {
      return processCommandHover(command);
    } else {
      ActualParameterPair parameterPair = isHoverOnParameter(command, params);
      if (parameterPair != null) {
        return processParameterWithNumberHintHover(command, params, parameterPair);
      } else {
        return CompletableFuture.completedFuture(null);
      }
    }
  }

  private CompletableFuture<Hover> processParameterWithNumberHintHover(
      CulturesIniCommand command, HoverParams hover, ActualParameterPair parameterPair) {
    StringBuilder sb = new StringBuilder();
    sb.append("***")
        .append(parameterPair.getParameterInformation().getName())
        .append("*** ")
        .append(" of ")
        .append(command.getCommandType().getCommandInformation().getDisplayName())
        .append("\n\n");
    sb.append("**Type**: `")
        .append(parameterPair.getParameterInformation().getType())
        .append("`\n\n");

    if (parameterPair.getParameterInformation().getDocumentation() != null) {
      sb.append("**Documentation**: \n\n")
          .append(parameterPair.getParameterInformation().getDocumentation())
          .append("\n\n");
    }

    if (parameterPair.getParameterInformation().getNumberHints() != null) {
      sb.append("**Values**: \n\n");
      List<String> numberHints = parameterPair.getParameterInformation().getNumberHints();
      for (int i = 0; i < numberHints.size(); i++) {
        String numberHintText = numberHints.get(i);
        if ("<NONE>".equals(numberHintText) || "<HIDE>".equals(numberHintText)) {
          continue;
        }
        sb.append("* ");
        String valueString = parameterPair.getParameterActual().getValue();
        try {
          int value = Integer.parseInt(valueString);
          if (value == i) {
            sb.append("\\> ");
          }
        } catch (NumberFormatException ex) {
        }
        sb.append('`').append(i).append("`: ").append(numberHintText).append('\n');
      }
    }

    return CompletableFuture.completedFuture(
        new Hover(
            new MarkupContent(MarkupKind.MARKDOWN, sb.toString()),
            parameterPair.getParameterActual().getOrigin().getRange()));
  }

  private ActualParameterPair isHoverOnParameter(CulturesIniCommand command, HoverParams hover) {
    List<ActualParameterPair> pairs = ActualParameterPair.of(command);
    for (ActualParameterPair pair : pairs) {
      Range commandRange = pair.getParameterActual().getOrigin().getRange();
      int hoverPosition = hover.getPosition().getCharacter();

      if (isHoverTarget(commandRange, hoverPosition)) {
        return pair;
      }
    }
    return null;
  }

  private CompletableFuture<Hover> processCommandHover(CulturesIniCommand command) {
    StringBuilder sb = new StringBuilder();
    sb.append("***")
        .append(command.getCommandType().getCommandInformation().getDisplayName())
        .append("***")
        .append("\n\n");
    sb.append("**").append("Description").append("**").append("\n\n");
    if (command.getCommandType().getCommandInformation().getDocumentation() != null) {
      sb.append(command.getCommandType().getCommandInformation().getDocumentation()).append("\n\n");
    }
    sb.append("**").append("Params").append("**").append("\n\n");
    for (var parameterInfo : command.getCommandType().getCommandInformation().getParameters()) {
      sb.append("* `").append(parameterInfo.getName()).append('`');
      if (parameterInfo.getDocumentation() != null) {
        sb.append(": ").append(parameterInfo.getDocumentation());
      }
      sb.append("\n");
    }

    return CompletableFuture.completedFuture(
        new Hover(
            new MarkupContent(MarkupKind.MARKDOWN, sb.toString()),
            command.getOriginBaseCommand().getRange()));
  }

  private boolean isHoverOnCommand(CulturesIniCommand command, HoverParams hover) {
    Range commandRange = command.getOriginBaseCommand().getRange();
    int hoverPosition = hover.getPosition().getCharacter();

    return isHoverTarget(commandRange, hoverPosition);
  }

  private boolean isHoverTarget(Range range, int position) {
    return range.getStart().getCharacter() <= position && position < range.getEnd().getCharacter();
  }
}
