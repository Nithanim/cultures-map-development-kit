package me.nithanim.cultures.lsp.processor.services.lsp;

import java.util.concurrent.CompletableFuture;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.services.SourceCodeIntelligenceService;
import me.nithanim.cultures.lsp.processor.util.MyPosition;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import me.nithanim.cultures.lsp.processor.util.Uri;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HoverService {
  @Autowired SourceCodeIntelligenceService sourceCodeIntelligenceService;

  public CompletableFuture<Hover> generateHover(HoverParams params) {
    CulturesIniCommand command =
        sourceCodeIntelligenceService.findForOnlyCommand(
            new MyPosition(
                new SourceFile(Uri.of(params.getTextDocument().getUri())), params.getPosition()));
    if (command == null) {
      return CompletableFuture.completedFuture(null);
    }
    StringBuilder sb = new StringBuilder();
    sb.append("***").append(command.getCommandType().getDisplayName()).append("***").append("\n\n");
    sb.append("**").append("Description").append("**").append("\n\n");
    if (command.getCommandType().getDescription() != null) {
      sb.append(command.getCommandType().getDescription()).append("\n\n");
    }
    sb.append("**").append("Params").append("**").append("\n\n");
    for (var parameterInfo : command.getCommandType().getParameterInfo()) {
      sb.append("* `").append(parameterInfo.getName()).append('`');
      if (parameterInfo.getMarkdownDocumentation() != null) {
        sb.append(": ").append(parameterInfo.getMarkdownDocumentation());
      }
      sb.append("\n");
    }

    return CompletableFuture.completedFuture(
        new Hover(new MarkupContent(MarkupKind.MARKDOWN, sb.toString())));
  }
}
