package me.nithanim.cultures.lsp.processor.services;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Value;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniLine;
import me.nithanim.cultures.lsp.processor.parsing.CulturesIniErrorListener;
import me.nithanim.cultures.lsp.processor.parsing.CulturesIniParserListener;
import me.nithanim.cultures.lsp.processor.util.DiagnosticsCollector;
import me.nithanim.cultures.lsp.processor.util.MyDiagnostic;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import me.nithanim.cultures.processor.CulturesIniLexer;
import me.nithanim.cultures.processor.CulturesIniParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.stereotype.Service;

@Service
@ReturnValuesAreNonnullByDefault
@ParametersAreNonnullByDefault
public class SourceFileParsingService {
  public ParsingResult parseSourceFile(SourceFile sf, String c) throws IOException {
    String sourceName = sf.getUri().getPath().getFileName().toString();

    CodePointCharStream stream = CharStreams.fromReader(new StringReader(c), sourceName);
    DiagnosticsCollector diagnostics = new DiagnosticsCollector();
    CulturesIniLexer lexer = new CulturesIniLexer(stream);

    lexer.removeErrorListeners();
    lexer.addErrorListener(new CulturesIniErrorListener(sf, diagnostics));

    CulturesIniParser parser = new CulturesIniParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    parser.addErrorListener(new CulturesIniErrorListener(sf, diagnostics));
    CulturesIniParser.FullfileContext fullFile = parser.fullfile();

    CulturesIniParserListener parserListener = new CulturesIniParserListener(sf, diagnostics);
    ParseTreeWalker.DEFAULT.walk(parserListener, fullFile);

    List<? extends CulturesIniLine> cils = parserListener.getLines();
    return new ParsingResult(sf, cils, diagnostics.getDiagnostics());
  }

  @Value
  public static class ParsingResult {
    SourceFile sourceFile;
    List<? extends CulturesIniLine> lines;
    List<MyDiagnostic> diagnostics;
  }
}
