package me.nithanim.cultures.lsp.processor.services.commands;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniLine;

@UtilityClass
class Util {
  public List<? extends CulturesIniCommand> filterByCategory(
      List<? extends CulturesIniLine> commands, CulturesIniCategoryType cat) {
    return commands.stream()
        .filter(l -> l instanceof CulturesIniCommand)
        .map(l -> (CulturesIniCommand) l)
        .filter(c -> c.getCommandType().getCommandInformation().getCategory() == cat)
        .collect(Collectors.toList());
  }

  public List<? extends CulturesIniCommand> filterByCategory(
      List<? extends CulturesIniLine> commands, EnumSet<CulturesIniCategoryType> cats) {
    return commands.stream()
        .filter(l -> l instanceof CulturesIniCommand)
        .map(l -> (CulturesIniCommand) l)
        .filter(c -> cats.contains(c.getCommandType().getCommandInformation().getCategory()))
        .collect(Collectors.toList());
  }
}
