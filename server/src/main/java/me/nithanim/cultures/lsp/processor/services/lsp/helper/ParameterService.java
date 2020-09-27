package me.nithanim.cultures.lsp.processor.services.lsp.helper;

import java.util.ArrayList;
import java.util.List;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommandType;
import me.nithanim.cultures.lsp.processor.lines.CulturesMissionGoalType;
import me.nithanim.cultures.lsp.processor.lines.commands.CommandInformation;
import org.springframework.stereotype.Service;

@Service
public class ParameterService {

  /**
   * Resolver for handling dynamic parameters of special commands, e.g. goal and result. Returns the
   * command information augmented with the parameters that the selected subcommand needs.
   */
  public CommandInformation getCraftedCommandInformation(CulturesIniCommand cmd) {
    if (cmd.getCommandType() == CulturesIniCommandType.GOAL) {
      int parametersMinimum;
      int parametersMaximum;
      List<CommandInformation.ParameterInformation> parametersInformation;
      List<CulturesIniCommand.Parameter> actualParameters = cmd.getParameters();
      CommandInformation baseInfo = cmd.getCommandType().getCommandInformation();

      if (actualParameters.isEmpty()) {
        parametersMinimum = 1;
        parametersMaximum = 1;
        parametersInformation = baseInfo.getParameters();
      } else {
        CulturesIniCommand.Parameter goalTypeParam = actualParameters.get(0);
        CulturesMissionGoalType goalType = CulturesMissionGoalType.find(goalTypeParam.getValue());
        if (goalType == null) {
          return cmd.getCommandType().getCommandInformation();
        }

        parametersMinimum = 1 + goalType.getCommandInformation().getParametersMinimum();
        parametersMaximum = 1 + goalType.getCommandInformation().getParametersMaximum();

        parametersInformation = new ArrayList<>();
        var originalParameter = baseInfo.getParameters().get(0);
        var subcommandCommandInformation = goalType.getCommandInformation();
        var interpolatedParameter =
            new CommandInformation.ParameterInformation(
                originalParameter.getName(),
                originalParameter.getType(),
                subcommandCommandInformation.getDocumentation(),
                originalParameter.getRange(),
                originalParameter.getNumberHints(),
                originalParameter.getNumberHintsBitfield());
        parametersInformation.add(interpolatedParameter);
        parametersInformation.addAll(goalType.getCommandInformation().getParameters());
      }

      return new CommandInformation(
          baseInfo.getName(),
          baseInfo.getDisplayName(),
          baseInfo.getCategory(),
          baseInfo.getDocumentation(),
          baseInfo.isSpecial(),
          parametersMinimum,
          parametersMaximum,
          parametersInformation);
    } else {
      return cmd.getCommandType().getCommandInformation();
    }
  }
}
