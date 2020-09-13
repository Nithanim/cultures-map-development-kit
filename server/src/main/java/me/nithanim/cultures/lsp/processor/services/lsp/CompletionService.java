package me.nithanim.cultures.lsp.processor.services.lsp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import me.nithanim.cultures.lsp.processor.services.SourceCodeIntelligenceService;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompletionService {
  @Autowired SourceCodeIntelligenceService sourceCodeIntelligenceService;

  public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
      CompletionParams params) {
    CompletionItem ci = new CompletionItem();
    ci.setLabel("TextCompletion");
    ci.setKind(CompletionItemKind.Interface);
    ci.setData("testdata");
    List<CompletionItem> cis = new ArrayList<>();
    cis.add(ci);
    return CompletableFuture.completedFuture(Either.forRight(new CompletionList(cis)));
  }
}
