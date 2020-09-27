package me.nithanim.cultures.lsp.processor.services.lsp.helper;

import java.util.List;
import me.nithanim.cultures.lsp.processor.lines.commands.CommandInformation;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.springframework.stereotype.Service;

@Service
public class DocumentationService {
  public MarkupContent createCommandDocumentation(CommandInformation actualCommandInformation) {
    StringBuilder sb = new StringBuilder();
    sb.append("***").append(actualCommandInformation.getDisplayName()).append("***").append("\n\n");
    sb.append("**").append("Description").append("**").append("\n\n");
    if (actualCommandInformation.getDocumentation() != null) {
      sb.append(actualCommandInformation.getDocumentation()).append("\n\n");
    }
    sb.append("**").append("Params").append("**").append("\n\n");
    for (var parameterInfo : actualCommandInformation.getParameters()) {
      sb.append("* `").append(parameterInfo.getName()).append('`');
      if (parameterInfo.getDocumentation() != null) {
        sb.append(": ").append(parameterInfo.getDocumentation());
      }
      sb.append("\n");
    }

    return new MarkupContent(MarkupKind.MARKDOWN, sb.toString());
  }

  public MarkupContent createParameterDocumentation(
      CommandInformation commandInformation, ActualParameterPair parameterPair) {
    StringBuilder sb = new StringBuilder();
    sb.append("***")
        .append(parameterPair.getParameterInformation().getName())
        .append("*** ")
        .append(" of ")
        .append(commandInformation.getDisplayName())
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
          // Don't care
        }
        sb.append('`').append(i).append("`: ").append(numberHintText).append('\n');
      }
    }

    return new MarkupContent(MarkupKind.MARKDOWN, sb.toString());
  }
}
