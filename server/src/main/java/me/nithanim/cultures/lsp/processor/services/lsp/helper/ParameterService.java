package me.nithanim.cultures.lsp.processor.services.lsp.helper;

import java.util.ArrayList;
import java.util.List;
import me.nithanim.cultures.lsp.processor.lines.CommandInformationHolder;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommandType;
import me.nithanim.cultures.lsp.processor.lines.CulturesMissionGoalType;
import me.nithanim.cultures.lsp.processor.lines.CulturesMissionResultType;
import me.nithanim.cultures.lsp.processor.lines.commands.CommandInformation;
import org.springframework.stereotype.Service;

@Service
public class ParameterService {

  /**
   * Resolver for handling dynamic parameters of special commands, e.g. goal and result. Returns the
   * command information augmented with the parameters that the selected subcommand needs.
   */
  public CommandInformation getCraftedCommandInformation(CulturesIniCommand cmd) {
    if (cmd.getCommandType() == CulturesIniCommandType.GOAL
        || cmd.getCommandType() == CulturesIniCommandType.RESULT) {
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
        CulturesIniCommand.Parameter typeParam = actualParameters.get(0);
        CommandInformationHolder commandInformationHolder;
        if (cmd.getCommandType() == CulturesIniCommandType.GOAL) {
          commandInformationHolder = CulturesMissionGoalType.find(typeParam.getValue());
        } else {
          commandInformationHolder = CulturesMissionResultType.find(typeParam.getValue());
        }
        CommandInformation commandInformation;
        if (commandInformationHolder == null) {
          return cmd.getCommandType().getCommandInformation();
        } else {
          commandInformation = commandInformationHolder.getCommandInformation();
        }

        parametersMinimum = 1 + commandInformation.getParametersMinimum();
        parametersMaximum = 1 + commandInformation.getParametersMaximum();

        parametersInformation = new ArrayList<>();
        var originalParameter = baseInfo.getParameters().get(0);
        var subcommandCommandInformation = commandInformation;
        var interpolatedParameter =
            new CommandInformation.ParameterInformation(
                originalParameter.getName(),
                0,
                originalParameter.getType(),
                subcommandCommandInformation.getDocumentation(),
                originalParameter.getRange(),
                originalParameter.getNumberHints(),
                originalParameter.getNumberHintsBitfield());
        parametersInformation.add(interpolatedParameter);
        parametersInformation.addAll(commandInformation.getParameters());
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
