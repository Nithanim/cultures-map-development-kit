package me.nithanim.cultures.lsp.processor.services.lsp.helper;

import java.util.ArrayList;
import java.util.List;
import lombok.Value;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.commands.CommandInformation;

@Value
public class ActualParameterPair {
  CulturesIniCommand.Parameter parameterActual;
  CommandInformation.ParameterInformation parameterInformation;

  public static List<ActualParameterPair> of(CulturesIniCommand command) {
    return of(
        command.getParameters(), command.getCommandType().getCommandInformation().getParameters());
  }

  public static List<ActualParameterPair> of(
      List<CulturesIniCommand.Parameter> parameterActual,
      List<CommandInformation.ParameterInformation> parameterInformation) {
    int n = Math.min(parameterActual.size(), parameterInformation.size());
    ArrayList<ActualParameterPair> pairs = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      pairs.add(new ActualParameterPair(parameterActual.get(i), parameterInformation.get(i)));
    }
    return pairs;
  }
}
