package me.nithanim.cultures.lsp.processor.services.lsp.helper;

import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommandType;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.springframework.stereotype.Service;

@Service
public class DocumentationService {
  public MarkupContent createCommandDocumentation(CulturesIniCommandType commandType) {
    StringBuilder sb = new StringBuilder();
    sb.append("***")
        .append(commandType.getCommandInformation().getDisplayName())
        .append("***")
        .append("\n\n");
    sb.append("**").append("Description").append("**").append("\n\n");
    if (commandType.getCommandInformation().getDocumentation() != null) {
      sb.append(commandType.getCommandInformation().getDocumentation()).append("\n\n");
    }
    sb.append("**").append("Params").append("**").append("\n\n");
    for (var parameterInfo : commandType.getCommandInformation().getParameters()) {
      sb.append("* `").append(parameterInfo.getName()).append('`');
      if (parameterInfo.getDocumentation() != null) {
        sb.append(": ").append(parameterInfo.getDocumentation());
      }
      sb.append("\n");
    }

    return new MarkupContent(MarkupKind.MARKDOWN, sb.toString());
  }
}
