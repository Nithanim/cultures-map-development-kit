package me.nithanim.cultures.lsp.processor.lines;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum CulturesIniCategoryType {
  LOGICCONTROL,
  MAP_TYPE,
  MISC_MAPTYPE,
  PLAYERDATA,
  PLAYERMISC,
  MISC_MAPNAME,
  STATICOBJECTS("StaticObjects"),
  MISSIONDATA("MissionData"),
  AIDATA("AIData"),
  SPECIALITEMS("specialItems"),
  ALLOWEDTHINGS,
  MULTIPLAYER,
  MISC_MUSIC,
  MISC_STARTPOSITION,
  MISC_HUMANGRAPHICS,
  MISC_HUMANNAMES,
  MISC_WEATHER,
  MISC_TRADEAGREEMENT,
  MISC_MULTIPLAYER_GOALS,
  TEXT,
  ;

  private static final Map<String, CulturesIniCategoryType> MAP =
      Arrays.stream(values()).collect(Collectors.toMap(t -> t.getRealname(), t -> t));

  public static CulturesIniCategoryType find(String s) {
    // return MAP.get(s.toUpperCase());
    // Category names seem to be case insensitive.
    // e.g.: in campaign: AIData; from editor: aidata
    try {
      return valueOf(s.toUpperCase());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private final String realname;

  CulturesIniCategoryType() {
    this.realname = name().toLowerCase();
  }

  CulturesIniCategoryType(String realCaseName) {
    this.realname = realCaseName;
  }

  public String getRealname() {
    return realname;
  }
}
