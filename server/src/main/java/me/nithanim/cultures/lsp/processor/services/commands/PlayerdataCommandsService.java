package me.nithanim.cultures.lsp.processor.services.commands;

import lombok.Data;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommandType;
import me.nithanim.cultures.lsp.processor.util.DiagnosticsCollector;
import me.nithanim.cultures.lsp.processor.util.Origin;
import org.springframework.stereotype.Service;

@Service
public class PlayerdataCommandsService {
  private Player[] players = new Player[20];

  public void handle(CulturesIniCommand cmd, DiagnosticsCollector dc) {
    if (cmd.getCommandType() == CulturesIniCommandType.PLAYER) {
      handlePlayer(cmd, dc);
    }
    if (cmd.getCommandType() == CulturesIniCommandType.DIPLOMACY) {
      handleDiplomacy(cmd, dc);
    }
  }

  private void handleDiplomacy(CulturesIniCommand cmd, DiagnosticsCollector dc) {
    int p1 = cmd.getParameter(0).getValueAsInt();
    if (p1 < 0 || p1 > 19) {
      dc.addError(cmd.getParameter(0).getOriginValue(), "Id must be in range (0,19)!");
      return;
    }
    if (!playerExists(p1)) {
      dc.addError(cmd.getParameter(0).getOriginValue(), "Player does not exist!");
    }

    int p2 = cmd.getParameter(1).getValueAsInt();
    if (p2 < 0 || p2 > 19) {
      dc.addError(cmd.getParameter(1).getOriginValue(), "Id must be in range (0,19)!");
      return;
    }
    if (!playerExists(p2)) {
      dc.addError(cmd.getParameter(1).getOriginValue(), "Player does not exist!");
    }

    if (p1 == p2) {
      dc.addError(
          cmd.getParameter(1).getOriginValue(), "Relation to oneself does not make any sense!");
    }

    int relation = cmd.getParameter(2).getValueAsInt();
    if (relation < 1 || relation > 3) {
      dc.addError(cmd.getParameter(2).getOriginValue(), "Relation must be in range (1,3)!");
    }

    byte existing = players[p1].getRelation()[p2];
    if (existing != 0) {
      dc.addError(
          cmd.getParameter(0).getOriginValue(), "Relation already defined as " + existing + "!");
    }
    players[p1].getRelation()[p2] = (byte) relation;
  }

  private void handlePlayer(CulturesIniCommand cmd, DiagnosticsCollector dc) {
    int id = cmd.getParameter(0).getValueAsInt();
    if (id < 0 || id > 19) {
      dc.addError(cmd.getParameter(0).getOriginValue(), "Id must be in range (0,19)!");
      return;
    }
    if (playerExists(id)) {
      dc.addError(cmd.getParameter(0).getOriginValue(), "Player already defined!");
    }

    int type = cmd.getParameter(1).getValueAsInt();
    if (type < 1 || type > 2) {
      dc.addError(cmd.getParameter(2).getOriginValue(), "Type must be in range (1,2)!");
    }

    int tribe = cmd.getParameter(2).getValueAsInt();
    if (tribe < 1 || tribe > 41) {
      dc.addError(cmd.getParameter(2).getOriginValue(), "Tribe must be in range (1,41)!");
    }
    if (tribe > 7 && tribe <= 41) {
      dc.addWarning(
          cmd.getParameter(2).getOriginValue(), "Only tribes in range of (1,7) are ever used!");
    }

    int color = cmd.getParameter(3).getValueAsInt();
    if (color < 0 || color > 9) {
      dc.addError(cmd.getParameter(2).getOriginValue(), "Color must be in range (0,9)!");
    }
    players[id] = new Player(cmd.getOriginBaseCommand(), id, type, tribe, color, new byte[20]);
  }

  public void postProcess(DiagnosticsCollector dc) {
    for (int p1 = 0; p1 < players.length; p1++) {
      Player p1Ref = players[p1];
      if (p1Ref == null) {
        continue;
      }
      byte[] rels = p1Ref.getRelation();
      for (int p2 = 0; p2 < rels.length; p2++) {
        if (p1 != p2 && rels[p2] == 0 && playerExists(p2)) {
          dc.addWarning(
              p1Ref.getOriginCommand(),
              "Relation to player " + p2 + " not defined, defaulting to neutral.");
        }
      }
    }
  }

  private boolean playerExists(int id) {
    return players[id] != null;
  }

  public void reset() {
    players = new Player[20];
  }

  @Data
  private class Player {
    final Origin originCommand;
    final int id;
    final int type;
    final int tribe;
    final int color;
    final byte[] relation;
  }
}
