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
public enum CulturesMissionResultType implements CommandInformationHolder {
  OPEN_CLOSE_WALL_GATE,
  ACTIVATE_MISSION,
  ADD_GOODS_TO_ANY_STOCK,
  ADD_GOODS_TO_HOUSES,
  ADD_GOODS_TO_MAP_AREA,
  ADD_GOODS_TO_VEHICLE,
  ADD_TRIBUTE_GOODS,
  ALLOW_GOOD,
  ALLOW_HOUSE,
  ALLOW_JOB,
  ALLOW_MAP,
  ATTACH_HUMAN_TO_VEHICLE,
  CHANGE_ANIMAL_PLAYER_ID_IN_AREA,
  CHANGE_HOUSES_PLAYER_ID,
  CHANGE_HUMAN_OBJECT_ID_IN_AREA,
  CHANGE_HUMAN_PLAYER_ID,
  CHANGE_MISSION_ID_OF_HUMAN_IN_RANGE,
  CHANGE_MISSION_ID_OF_PLAYER,
  CHANGE_MISSION_ID_OF_PLAYERS_VEHICLES_ON_CONTINENT,
  CHANGE_MISSION_ID_OF_VEHICLES,
  CHANGE_MISSION_ID_OF_VEHICLES_IN_RANGE,
  CHANGE_PLAYER_ID_IN_AREA,
  CHANGE_PLAYER_PLAYER_ID,
  CHANGE_VEHICLES_PLAYER_ID,
  CLEAR_TRIBUTE,
  CLOSE_MAP,
  CREATE_TRIBUTE,
  DEACTIVATE_MISSION,
  DETACH_HUMAN_FROM_VEHICLE,
  DISABLE_ALL,

  DOCK_VEHICLE,
  ENABLE_GOOD,
  ENABLE_HOUSE,
  ENABLE_JOB,
  END_SUB_MISSION,
  EXIT,
  EXPLORE_AREA,
  HEAL_HUMANS_IN_AREA,
  INFO_CLEAR,
  INFO_COUNT_ANIMALS_IN_AREA,
  INFO_COUNT_GOODS_IN_AREA,
  INFO_COUNT_HOUSES_IN_AREA,
  INFO_COUNT_HUMEN_IN_AREA,
  INFO_COUNT_SOLDIERS_IN_AREA,
  INFO_SHOW_STRING,
  MISSION_QUIT_AND_PLAY_VIDEO,
  MISSION_FAILED,
  MISSION_WON,
  MOVE_HUMAN,
  MOVE_UNITS_IN_AREA,
  NONE,
  PLAY_CUTSCENE,
  PLAY_SOUND,
  REMOVE_ANIMALS,
  REMOVE_BLOCKER_LANDSCAPE_IN_AREA,
  REMOVE_F_X1_LANDSCAPE_IN_AREA,
  REMOVE_F_X2_LANDSCAPE_IN_AREA,
  REMOVE_F_X_SMOKE_LANDSCAPE_IN_AREA,
  REMOVE_F_X_WAVE_LANDSCAPE_IN_AREA,
  REMOVE_GOODS_FROM_MAP_AREA,
  REMOVE_HOUSES,
  REMOVE_H_PS_OF_HOUSES_IN_AREA,
  REMOVE_H_PS_OF_HOUSES_IN_AREA_X,

  REMOVE_HUMANS,
  REMOVE_HUMANS_NEAR_POS,
  REMOVE_LANDSCAPE,
  REMOVE_LANDSCAPES_IN_AREA,
  REMOVE_VEHICLES,
  REMOVE_VEHICLES_WITH_MISSION_ID,
  SELECT_HUMAN,
  SEND_HUMAN,
  SEND_VEHICLE,
  SET_ANIMAL,
  SET_CAMERA_POSITION,
  SET_DIPLOMACY,
  SET_DIPLOMACY_NOT_CHANGEABLE_FLAG,
  SET_EXTERNAL_FLAG,
  SET_GUI_MARKER,
  SET_HOUSE,
  SET_HOUSE_BEHAVIOUR_FLAG,
  SET_HOUSE_BUILD_FORBIDDEN_AREA,
  SET_HOUSE_EXTENSION_LEVEL,
  SET_HOUSE_OVERLAY_STATE,
  SET_HUMAN,
  SET_HUMAN_BEHAVIOUR_FLAG,
  SET_HUMAN_NAME,
  SET_HUMAN_X,
  SET_IMPORT_HUMAN_FLAG,
  SET_IMPORT_LANDSCAPE_MARKER,
  SET_LANDSCAPE,
  SET_MAP_AREA_MARKER,
  SET_MAP_AREA_MARKER_MAGIC,
  SET_PLAYER_BEHAVIOUR_FLAG,
  SET_RANDOM_CHEST_ON_POSITION,
  SET_RANDOM_CHEST_ON_RANDOM_POS,
  SET_VEHICLE,
  SET_VERTEX_COLOR,
  SET_VERTEX_COLOR_ON_LAND,
  SET_VISIBLE,
  SET_WEATHER,
  START_EARTH_QUAKE,
  START_SUB_MISSION,
  STOP_HUMAN_BY_PLAYER_ID;

  private static final Map<String, CulturesMissionResultType> LOOKUP_MAP;

  static {
    try {
      var m = new HashMap<String, CulturesMissionResultType>();
      for (CulturesMissionResultType goal : values()) {
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

  private static String convertEnumNameToFileName(CulturesMissionResultType goal) {
    return goal.name().toLowerCase().replace("_", "");
  }

  public static CulturesMissionResultType find(String s) {
    return LOOKUP_MAP.get(s);
  }

  @Getter private final CommandInformation commandInformation;

  CulturesMissionResultType() {
    try {
      String fileName =
          "commands/results/" + convertEnumNameToFileName(CulturesMissionResultType.this) + ".json";
      InputStream in = CulturesMissionResultType.class.getResourceAsStream(fileName);
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
