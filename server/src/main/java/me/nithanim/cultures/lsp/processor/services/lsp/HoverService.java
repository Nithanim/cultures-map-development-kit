package me.nithanim.cultures.lsp.processor.services.lsp;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.services.SourceCodeIntelligenceService;
import me.nithanim.cultures.lsp.processor.services.lsp.helper.ActualParameterPair;
import me.nithanim.cultures.lsp.processor.services.lsp.helper.DocumentationService;
import me.nithanim.cultures.lsp.processor.util.MyPosition;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import me.nithanim.cultures.lsp.processor.util.Uri;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HoverService {
  @Autowired private SourceCodeIntelligenceService sourceCodeIntelligenceService;
  @Autowired private DocumentationService documentationService;

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
    MarkupContent contents =
        documentationService.createParameterDocumentation(command, parameterPair);
    return CompletableFuture.completedFuture(
        new Hover(contents, parameterPair.getParameterActual().getOrigin().getRange()));
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
    MarkupContent contents =
        documentationService.createCommandDocumentation(command.getCommandType());
    return CompletableFuture.completedFuture(
        new Hover(contents, command.getOriginBaseCommand().getRange()));
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
