package me.nithanim.cultures.lsp.processor.services.commands;

import java.util.ArrayList;
import java.util.List;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommandType;
import me.nithanim.cultures.lsp.processor.services.clientpersistent.MyCodeLense;
import org.springframework.stereotype.Service;

@Service
public class StaticobjectsCommandsService {
  public MyCodeLense handle(CulturesIniCommand cmd) {
    if (cmd.getCommandType() == CulturesIniCommandType.SETHUMAN) {
      return handleHuman(cmd);
    } else {
      return null;
    }
  }

  private MyCodeLense handleHuman(CulturesIniCommand cmd) {
    if (cmd.getParameters().size() < 7) {
      return null;
    }

    int behaviour = cmd.getParameter(6).getValueAsInt();
    List<String> trans = new ArrayList<>();
    c(0b1, behaviour, "NoNeeds", trans);
    c(0b10, behaviour, "NoJob", trans);
    c(0b100, behaviour, "NoFight", trans);
    c(0b1000, behaviour, "Immortal", trans);
    c(0b10000, behaviour, "NoMsg", trans);
    c(0b100000, behaviour, "NoControl", trans);
    c(0b1000000, behaviour, "NoChangeJob", trans);
    c(0b10000000, behaviour, "Important", trans);
    c(0b100000000, behaviour, "SectorPath", trans);
    c(0b1000000000, behaviour, "Slow", trans);
    c(0b10000000000, behaviour, "NoChangePal", trans);
    c(0b100000000000, behaviour, "NoExpGain", trans);
    c(0b1000000000000, behaviour, "NoShoeWear", trans);
    c(0b10000000000000, behaviour, "AnimalIgnored", trans);
    c(0b100000000000000, behaviour, "Ghost", trans);
    c(0b1000000000000000, behaviour, "NoHelp", trans);
    c(0b10000000000000000, behaviour, "Defend", trans);
    c(0b100000000000000000, behaviour, "Fast", trans);
    c(0b1000000000000000000, behaviour, "NoAge", trans);
    c(0b10000000000000000000, behaviour, "Hero", trans);

    return MyCodeLense.of(
        cmd.getParameter(6).getOrigin(), "Behaviours: [" + String.join(", ", trans) + "]");
  }

  private void c(int b, int behaviour, String s, List<String> trans) {
    if ((behaviour & b) != 0) {
      trans.add(s);
    }
  }
}
