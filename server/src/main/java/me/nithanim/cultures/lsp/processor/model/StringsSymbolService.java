package me.nithanim.cultures.lsp.processor.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommandType;
import me.nithanim.cultures.lsp.processor.util.MyDiagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.springframework.stereotype.Service;

@Service
public class StringsSymbolService implements StringsSymbolTable {
  // StringsSymbolTable stringsSymbolTable = new StringsSymbolTableImpl();

  // @Autowired
  // private DisgnosticsService disgnosticsService;

  @Override
  public void getStrings(int id) {}

  @Override
  public void getString(int id, String lang) {}

  @Override
  public void replaceLanguage(List<CulturesIniCommand> commands) {
    Map<Boolean, List<CulturesIniCommand>> collect =
        commands.stream()
            .collect(
                Collectors.groupingBy(c -> c.getCommandType() == CulturesIniCommandType.STRINGN));

    // disgnosticsService.replaceDiagnostis();
    collect.get(Boolean.FALSE).stream()
        .map(
            c ->
                new MyDiagnostic(
                    c.getOriginAll(), DiagnosticSeverity.Error, "Only stringn allowed"))
        .collect(Collectors.toList());
  }
}
