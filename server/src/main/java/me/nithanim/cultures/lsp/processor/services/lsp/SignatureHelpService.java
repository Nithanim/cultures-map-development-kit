package me.nithanim.cultures.lsp.processor.services.lsp;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.commands.CommandInformation;
import me.nithanim.cultures.lsp.processor.services.SourceCodeIntelligenceService;
import me.nithanim.cultures.lsp.processor.util.MyPosition;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import me.nithanim.cultures.lsp.processor.util.Uri;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.ParameterInformation;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SignatureHelpParams;
import org.eclipse.lsp4j.SignatureInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignatureHelpService {
  @Autowired SourceCodeIntelligenceService sourceCodeIntelligenceService;

  public CompletableFuture<SignatureHelp> generateSignatureHelp(SignatureHelpParams params) {
    CulturesIniCommand command =
        sourceCodeIntelligenceService.getByPositionOnlyCommand(
            new MyPosition(
                new SourceFile(Uri.of(params.getTextDocument().getUri())), params.getPosition()));
    if (command != null) {
      return CompletableFuture.completedFuture(
          new SignatureHelp(
              Collections.singletonList(
                  new SignatureInformation(
                      getSignatureLabel(command),
                      new MarkupContent(MarkupKind.MARKDOWN, getSignatureDocumentation(command)),
                      getSignatureParameters(command))),
              0,
              getActiveParameter(command, params.getPosition())));
    } else {
      List<SignatureInformation> signatures =
          Collections.emptyList(); // SHOULD BE NULL BY SPECIFICATION BUT SHITTY LIBRARY IS BUGGY
      return CompletableFuture.completedFuture(new SignatureHelp(signatures, 0, 0));
    }
  }

  private int getActiveParameter(CulturesIniCommand command, Position position) {
    int activeParameter = 0;
    for (var parameter : command.getParameters()) {
      var range = parameter.getOrigin().getRange();
      if (range.getStart().getCharacter() <= position.getCharacter()
          && position.getCharacter() <= range.getEnd().getCharacter()) {
        return activeParameter;
      }
      activeParameter++;
    }
    return -1;
  }

  private List<ParameterInformation> getSignatureParameters(CulturesIniCommand command) {
    return command.getCommandType().getCommandInformation().getParameters().stream()
        .map(this::toSignatureParameter)
        .collect(Collectors.toList());
  }

  private ParameterInformation toSignatureParameter(
      CommandInformation.ParameterInformation parameterInfo) {
    String docu =
        parameterInfo.getDocumentation() == null ? "" : ": " + parameterInfo.getDocumentation();
    return new ParameterInformation(
        parameterInfo.getName(),
        new MarkupContent(MarkupKind.MARKDOWN, "`" + parameterInfo.getName() + "`" + docu));
  }

  private String getSignatureDocumentation(CulturesIniCommand command) {
    return command.getCommandType().getCommandInformation().getDocumentation();
  }

  private String getSignatureLabel(CulturesIniCommand command) {
    var parameters = command.getCommandType().getCommandInformation().getParameters();
    StringBuilder sb = new StringBuilder();
    sb.append(command.getCommandType().getCommandInformation().getDisplayName());
    for (var parameter : parameters) {
      sb.append(' ').append('<').append(parameter.getName()).append('>');
    }
    return sb.toString();
  }
}
