package me.nithanim.cultures.lsp.processor.services.lsp;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniLine;
import me.nithanim.cultures.lsp.processor.services.SourceCodeIntelligenceService;
import me.nithanim.cultures.lsp.processor.services.lsp.helper.CompletionForCommandsService;
import me.nithanim.cultures.lsp.processor.services.lsp.helper.CompletionForParametersService;
import me.nithanim.cultures.lsp.processor.util.MyPosition;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import me.nithanim.cultures.lsp.processor.util.Uri;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompletionService {
  @Autowired private SourceCodeIntelligenceService sourceCodeIntelligenceService;
  @Autowired private CompletionForParametersService completionForParametersService;
  @Autowired private CompletionForCommandsService completionForCommandsService;

  public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
      CompletionParams params) {
    MyPosition cursorPosition =
        new MyPosition(
            new SourceFile(Uri.of(params.getTextDocument().getUri())), params.getPosition());

    System.out.println(System.currentTimeMillis());
    if (1 == 0) {
      CompletionItem ci = new CompletionItem("test");
      ci.setInsertText("test");
      return CompletableFuture.completedFuture(Either.forLeft(Collections.singletonList(ci)));
    }

    CulturesIniLine line = sourceCodeIntelligenceService.getByPosition(cursorPosition);
    if (isPosInParameterSpace(cursorPosition.getPosition(), line)) {
      return completionForParametersService.handleParamComplete(cursorPosition, line);
    } else {
      return completionForCommandsService.handleCommandComplete(params, cursorPosition, line);
    }
  }

  private boolean isPosInParameterSpace(Position cursor, CulturesIniLine line) {
    if (!(line instanceof CulturesIniCommand)) {
      return false;
    }
    var cmd = (CulturesIniCommand) line;

    Range r = cmd.getOriginBaseCommand().getRange();
    return r.getEnd().getCharacter() < cursor.getCharacter();
  }
}
