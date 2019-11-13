package me.nithanim.cultures.lsp.processor.parsing;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import me.nithanim.cultures.lsp.processor.util.DiagnosticsCollector;
import me.nithanim.cultures.lsp.processor.util.Origin;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import me.nithanim.cultures.lsp.processor.lines.*;
import me.nithanim.cultures.processor.CulturesIniBaseListener;
import me.nithanim.cultures.processor.CulturesIniParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public class CulturesIniParserListener extends CulturesIniBaseListener {
    private final SourceFile sourceFile;
    private final DiagnosticsCollector diagnostics;
    private CulturesIniCommand.Parameter.Type parameterType;

    private final List<CulturesIniLine> lines = new ArrayList<>();

    public List<? extends CulturesIniLine> getLines() {
        return lines;
    }

    @SneakyThrows
    public CulturesIniParserListener(SourceFile sourceFile, DiagnosticsCollector diagnostics) {
        this.sourceFile = sourceFile;
        this.diagnostics = diagnostics;
    }


    private CulturesIniCommand.ParsedCulturesIniCommandBuilder commandBuilder;
    private CulturesIniConstant.CulturesIniConstantBuilder constantBuilder;


    @Override
    public void enterCategoryname(CulturesIniParser.CategorynameContext ctx) {
        CulturesIniCategoryType ct = CulturesIniCategoryType.find(ctx.getText());
        if (ct == null) {
            diagnostics.addError(getOrigin(ctx), "No such category type!");
        }
        lines.add(new CulturesIniCategory(ct, getOrigin(ctx)));
    }

    @Override
    public void enterCommandline(CulturesIniParser.CommandlineContext ctx) {
        commandBuilder = CulturesIniCommand.builder();
        commandBuilder.originAll(getOrigin(ctx));
    }

    @Override
    public void enterCommandname(CulturesIniParser.CommandnameContext ctx) {
        CulturesIniCommandType type = CulturesIniCommandType.find(ctx.getChild(0).getText());
        if (type == null) {
            diagnostics.addError(getOrigin(ctx), "Unknown command!");
            return;
        }
        commandBuilder.type(type).originBaseCommand(getOrigin(ctx));
    }

    @Override
    public void enterCommandarg(CulturesIniParser.CommandargContext ctx) {
        if (commandBuilder.getCommandType() == null) {
            return; //cannot do anything useful without knowing the command
        }
    }

    @Override
    public void enterConstantarg(CulturesIniParser.ConstantargContext ctx) {
        parameterType = CulturesIniCommand.Parameter.Type.CONSTANT;
    }

    @Override
    public void enterNumberarg(CulturesIniParser.NumberargContext ctx) {
        parameterType = CulturesIniCommand.Parameter.Type.NUMBER;
    }

    @Override
    public void enterStringarg(CulturesIniParser.StringargContext ctx) {
        parameterType = CulturesIniCommand.Parameter.Type.STRING;
    }

    @Override
    public void exitCommandarg(CulturesIniParser.CommandargContext ctx) {
        if (parameterType != null) {
            commandBuilder.addParameter(new CulturesIniCommand.Parameter(ctx.getText(), parameterType, getOrigin(ctx)));
        } else {
            diagnostics.addError(getOrigin(ctx), "Unknown parameter type!");
        }
    }

    @Override
    public void exitCommandline(CulturesIniParser.CommandlineContext ctx) {
        if (commandBuilder.getCommandType() == null) {
            return;
        } else if(commandBuilder.getCommandType().isSpecial()) {
            lines.add(commandBuilder.build());
        } else {
            lines.add(commandBuilder.build());
        }
        commandBuilder = null;
    }

    @Override
    public void enterDefineline(CulturesIniParser.DefinelineContext ctx) {
        constantBuilder = CulturesIniConstant.builder().originAll(getOrigin(ctx));
    }

    @Override
    public void enterDefinename(CulturesIniParser.DefinenameContext ctx) {
        constantBuilder.originKey(getOrigin(ctx)).name(ctx.getText());
    }

    @Override
    public void enterDefinenumber(CulturesIniParser.DefinenumberContext ctx) {
        constantBuilder.originValue(getOrigin(ctx)).valueInt(Integer.parseInt(ctx.getText()));
    }

    @Override
    public void enterDefinestring(CulturesIniParser.DefinestringContext ctx) {
        constantBuilder.originValue(getOrigin(ctx)).valueString(ctx.getText());
    }

    @Override
    public void exitDefineline(CulturesIniParser.DefinelineContext ctx) {
        lines.add(constantBuilder.build());
        constantBuilder = null;
    }

    @Override
    public void enterIncludepath(CulturesIniParser.IncludepathContext ctx) {
        CulturesIniInclude line = new CulturesIniInclude(
                getOrigin(ctx.getParent()),
                getOriginWithCutQuotes(ctx),
                ctx.getText().substring(1, ctx.getText().length() - 1)
        );
        lines.add(line);
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        Token s = node.getSymbol();
        Range range = new Range(
                new Position(s.getLine() - 1, s.getCharPositionInLine()),
                new Position(s.getLine() - 1, s.getCharPositionInLine() + s.getText().length())
        );

        diagnostics.addError(new Origin(sourceFile, range), "visitErrorNode");
    }

    private Origin getOrigin(ParserRuleContext ctx) {
        return new Origin(sourceFile, rangeFromContext(ctx));
    }

    private static Range rangeFromContext(ParserRuleContext ctx) {
        return new Range(
                new Position(ctx.getStart().getLine() - 1, ctx.getStart().getCharPositionInLine()),
                new Position(ctx.getStop().getLine() - 1, ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length())
        );
    }

    private Origin getOriginWithCutQuotes(ParserRuleContext ctx) {
        return new Origin(sourceFile, rangeFromContextWithCutQuotes(ctx));
    }

    private static Range rangeFromContextWithCutQuotes(ParserRuleContext ctx) {
        return new Range(
                new Position(ctx.getStart().getLine() - 1, ctx.getStart().getCharPositionInLine() + 1),
                new Position(ctx.getStop().getLine() - 1, ctx.getStop().getCharPositionInLine() + 1 + ctx.getStop().getText().length() - 1 - 1)
        );
    }
}
