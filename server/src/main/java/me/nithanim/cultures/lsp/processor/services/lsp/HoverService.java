package me.nithanim.cultures.lsp.processor.services.lsp;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.commands.CommandInformation;
import me.nithanim.cultures.lsp.processor.services.SourceCodeIntelligenceService;
import me.nithanim.cultures.lsp.processor.services.lsp.helper.ActualParameterPair;
import me.nithanim.cultures.lsp.processor.services.lsp.helper.DocumentationService;
import me.nithanim.cultures.lsp.processor.services.lsp.helper.ParameterService;
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
  @Autowired private ParameterService parameterService;

  public CompletableFuture<Hover> generateHover(HoverParams hoverParameters) {
    CulturesIniCommand command = getCommandOnPosition(hoverParameters);
    if (command == null) {
      return CompletableFuture.completedFuture(null);
    }

    var commandInformation = parameterService.getCraftedCommandInformation(command);
    var actualParameters = command.getParameters();

    if (isHoverOnCommand(command, hoverParameters)) {
      return processCommandHover(command, commandInformation);
    } else {
      ActualParameterPair parameterPair =
          isHoverOnParameter(commandInformation.getParameters(), actualParameters, hoverParameters);
      if (parameterPair != null) {
        return processParameterWithNumberHintHover(commandInformation, parameterPair);
      } else {
        return CompletableFuture.completedFuture(null);
      }
    }
  }

  private CulturesIniCommand getCommandOnPosition(HoverParams hoverParameters) {
    return sourceCodeIntelligenceService.getByPositionOnlyCommand(
        new MyPosition(
            new SourceFile(Uri.of(hoverParameters.getTextDocument().getUri())),
            hoverParameters.getPosition()));
  }

  private CompletableFuture<Hover> processParameterWithNumberHintHover(
      CommandInformation commandInformation, ActualParameterPair parameterPair) {
    MarkupContent contents =
        documentationService.createParameterDocumentation(commandInformation, parameterPair);
    return CompletableFuture.completedFuture(
        new Hover(contents, parameterPair.getParameterActual().getOriginAll().getRange()));
  }

  private ActualParameterPair isHoverOnParameter(
      List<CommandInformation.ParameterInformation> parameterInformation,
      List<CulturesIniCommand.Parameter> actualParameters,
      HoverParams hover) {
    List<ActualParameterPair> pairs =
        ActualParameterPair.of(actualParameters, parameterInformation);
    for (ActualParameterPair pair : pairs) {
      Range commandRange = pair.getParameterActual().getOriginAll().getRange();
      int hoverPosition = hover.getPosition().getCharacter();

      if (isHoverTarget(commandRange, hoverPosition)) {
        return pair;
      }
    }
    return null;
  }

  private CompletableFuture<Hover> processCommandHover(
      CulturesIniCommand command, CommandInformation commandInformation) {
    MarkupContent contents = documentationService.createCommandDocumentation(commandInformation);
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
