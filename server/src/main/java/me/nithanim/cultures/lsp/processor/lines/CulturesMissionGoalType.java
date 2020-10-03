package me.nithanim.cultures.lsp.processor.lines;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.nithanim.cultures.lsp.processor.lines.commands.CommandInformation;
import me.nithanim.cultures.lsp.processor.lines.commands.CommandInformationMapper;
import me.nithanim.cultures.lsp.processor.lines.commands.JsonCommandInformation;
import org.h2.util.IOUtils;

@Slf4j
public enum CulturesMissionGoalType implements CommandInformationHolder {
  ANIMALS_DIED,
  BUILD_HOUSE_ON_CONTINENT,
  BUILD_HOUSES,
  BUILD_HUMANS,
  BUILD_VEHICLES,
  CHECK_HUMAN_JOB,
  CHECK_MISSION,
  CHECK_NUMBER_OF_WILD_ANIMALS,
  CHEST_NEAR_POS,
  DETECT_GUIDE,
  DIPLOMACY_STATE,
  FIND_ANIMALS,
  FIND_HOUSES,
  FIND_HOUSES_BY_HUMANS,
  FIND_HOUSES_BY_VEHICLES,
  FIND_HUMANS,
  FIND_HUMANS_BY_HUMANS,
  FIND_HUMANS_BY_PLAYERS_M_M,
  FIND_HUMANS_BY_VEHICLES,
  FIND_POS,
  FIND_POS_BY_HUMANS,
  FIND_POS_BY_PLAYERS_MAP_MOVEABLE,
  FIND_POS_BY_VEHICLES,
  FIND_VEHICLES,
  FIND_VEHICLES_BY_VEHICLES,
  GOOD_PRODUCEABLE,
  GOODS_GLOBAL,
  GOODS_IN_HOUSES,
  GOODS_IN_VEHICLES,
  HOUSES_DIED,
  HUMAN_ATTACHED_TO_WORK_HOUSE,
  HUMAN_IS_ON_CONTINENT,
  HUMANS_DIED,
  HUMANS_WITH_HOME,
  IF_MISSION_IS_ACTIVE,
  IS_ANY_LANDSCAPE_ON_POINT,
  IS_HUMAN_IN_VEHICLE,
  IS_MISSION_DONE,
  JOB_ENABLED,
  NUMBER_OF_ANIMALS,
  NUMBER_OF_ANIMALS_IN_AREA,
  NUMBER_OF_GOODS_IN_AREA,
  NUMBER_OF_GOODS_IN_HOUSES_IN_AREA,
  NUMBER_OF_GOODS_IN_VEHICLES_IN_AREA,
  NUMBER_OF_GOODS_TRADED,
  NUMBER_OF_HOUSES_IN_AREA,
  NUMBER_OF_HUMANS_DIED,
  NUMBER_OF_HUMANS_KILLED,
  NUMBER_OF_SOLDIERS,
  NUMBER_OF_SOLDIERS_NEAR_POS,
  NUMBER_OF_VEHICLES_IN_AREA,
  PAY_TRIBUTE,
  PLAYER_ATTACKED_BY_PLAYER,
  PLAYER_DIED,
  PLAYER_SEEN,
  POPULATION,
  RANDOM_TIME_GONE,
  SOLDIERS_DIED,
  TIME_GONE,
  TRUE,
  VEHICLES_DIED;

  private static final Map<String, CulturesMissionGoalType> LOOKUP_MAP;

  static {
    try {
      var m = new HashMap<String, CulturesMissionGoalType>();
      for (CulturesMissionGoalType goal : values()) {
        try {
          m.put(goal.getCommandInformation().getName(), goal);
        } catch (Exception ex) {
          throw new IllegalStateException("Unable to load definition for command goal " + goal);
        }
      }
      LOOKUP_MAP = Collections.unmodifiableMap(m);
    } catch (Exception ex) {
      log.error("Unable to load command goal definitions!", ex);
      throw ex;
    }
  }

  private static String convertEnumNameToFileName(CulturesMissionGoalType goal) {
    return goal.name().toLowerCase().replace("_", "");
  }

  public static CulturesMissionGoalType find(String s) {
    return LOOKUP_MAP.get(s);
  }

  @Getter private final CommandInformation commandInformation;

  CulturesMissionGoalType() {
    try {
      String fileName =
          "commands/goals/" + convertEnumNameToFileName(CulturesMissionGoalType.this) + ".json";
      InputStream in = CulturesMissionGoalType.class.getResourceAsStream(fileName);
      if (in == null) {
        throw new IllegalStateException(
            "Unable to find definition for command " + name(), new FileNotFoundException(fileName));
      }
      this.commandInformation =
          new CommandInformationMapper()
              .map(new Gson().fromJson(IOUtils.getReader(in), JsonCommandInformation.class));

    } catch (Exception ex) {
      throw new IllegalStateException("Unable to find definition for command " + name(), ex);
    }
  }
}
