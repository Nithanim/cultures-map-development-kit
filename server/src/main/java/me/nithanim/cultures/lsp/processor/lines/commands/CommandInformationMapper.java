package me.nithanim.cultures.lsp.processor.lines.commands;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommandType;

public class CommandInformationMapper {
  public CommandInformation map(JsonCommandInformation src) {
    CulturesIniCategoryType category;
    if (src.getCategory().equals("<dummy>")) {
      category = null;
    } else {
      category = CulturesIniCategoryType.valueOf(src.getCategory().toUpperCase());
    }
    List<CommandInformation.ParameterInformation> parameters =
        src.getParameters().stream().map(this::map).collect(Collectors.toList());
    int parameterCount = src.getParameters().size();
    int parametersMin = src.getParametersMin() == null ? parameterCount : src.getParametersMin();
    int parametersMax = src.getParametersMax() == null ? parameterCount : src.getParametersMax();
    return new CommandInformation(
        src.getName(),
        src.getDisplayName() == null ? src.getName() : src.getDisplayName(),
        category,
        src.getDocumentation(),
        src.isSpecial(),
        parametersMin,
        parametersMax,
        parameters);
  }

  private CommandInformation.ParameterInformation map(
      JsonCommandInformation.JsonParameterInformation src) {
    return new CommandInformation.ParameterInformation(
        src.getName(),
        CommandInformation.ParameterInformation.Type.valueOf(src.getType().name()),
        src.getDocumentation(),
        map(src.getNumberRange()),
        src.getNumberHints(),
        src.getNumberHintsBitfield());
  }

  private CommandInformation.ParameterInformation.Range map(
      JsonCommandInformation.JsonParameterInformation.JsonRange src) {
    if (src == null) {
      return null;
    } else {
      return new CommandInformation.ParameterInformation.Range(src.getMin(), src.getMax());
    }
  }

  public CommandInformation map(
      String name,
      boolean special,
      CulturesIniCategoryType category,
      int paramMin,
      int paramMax,
      CulturesIniCommandType.ParameterInfo<?>... parameterTypes) {

    List<CommandInformation.ParameterInformation> parameters =
        Arrays.stream(parameterTypes)
            .map(
                p -> {
                  if (p instanceof CulturesIniCommandType.StringParameterInfo) {
                    return new CommandInformation.ParameterInformation(
                        p.getName(),
                        CommandInformation.ParameterInformation.Type.TYPE,
                        null,
                        null,
                        null,
                        null);
                  } else if (p instanceof CulturesIniCommandType.TypeParameterInfo) {
                    return new CommandInformation.ParameterInformation(
                        p.getName(),
                        CommandInformation.ParameterInformation.Type.TYPE,
                        null,
                        null,
                        null,
                        null);
                  } else if (p instanceof CulturesIniCommandType.BoundedNumberParameterInfo) {
                    var b = (CulturesIniCommandType.BoundedNumberParameterInfo) p;
                    return new CommandInformation.ParameterInformation(
                        p.getName(),
                        CommandInformation.ParameterInformation.Type.NUMBER,
                        null,
                        new CommandInformation.ParameterInformation.Range(b.getMin(), b.getMax()),
                        null,
                        null);
                  } else if (p instanceof CulturesIniCommandType.NumberParameterInfo) {
                    return new CommandInformation.ParameterInformation(
                        p.getName(),
                        CommandInformation.ParameterInformation.Type.NUMBER,
                        null,
                        null,
                        null,
                        null);
                  } else if (p instanceof CulturesIniCommandType.SpecificNumberParameterInfo) {
                    return new CommandInformation.ParameterInformation(
                        p.getName(),
                        CommandInformation.ParameterInformation.Type.NUMBER,
                        null,
                        null,
                        null,
                        null);
                  } else {
                    throw new IllegalStateException("wtf");
                  }
                })
            .collect(Collectors.toList());
    return new CommandInformation(
        name, name, category, null, special, paramMin, paramMax, parameters);
  }
}
