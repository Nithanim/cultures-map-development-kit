package me.nithanim.cultures.lsp.processor.services.clientpersistent;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import org.eclipse.lsp4j.CodeLens;
import org.springframework.stereotype.Service;

@Service
public class CodeLensServiceOld {
  private final Map<SourceFile, List<CodeLens>> cache = new HashMap<>();

  public void updateCodeLens(SourceFile sf, List<CodeLens> cls) {
    cache.put(sf, cls);
  }

  public List<CodeLens> getCodeLenses(SourceFile sf) {
    return cache.getOrDefault(sf, Collections.emptyList());
  }
}
