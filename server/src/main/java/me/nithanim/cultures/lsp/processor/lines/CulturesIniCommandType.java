package me.nithanim.cultures.lsp.processor.lines;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.Value;
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType.AIDATA;
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType.ALLOWEDTHINGS;
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType.MISC_HUMANGRAPHICS;
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType.MISC_HUMANNAMES;
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType.MISC_MAPNAME;
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType.MISC_MAPTYPE;
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType.MISC_MUSIC;
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType.MISC_STARTPOSITION;
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType.MISC_TRADEAGREEMENT;
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType.MISC_WEATHER;
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType.MISSIONDATA;
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType.PLAYERMISC;
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType.SPECIALITEMS;
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType.TEXT;
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand.Parameter.Type.NUMBER;
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand.Parameter.Type.STRING;
import me.nithanim.cultures.lsp.processor.lines.commands.CommandInformation;
import me.nithanim.cultures.lsp.processor.lines.commands.CommandInformationMapper;
import me.nithanim.cultures.lsp.processor.lines.commands.JsonCommandInformation;
import org.h2.util.IOUtils;

/**
 * Definitions of all commands available. Parameter names, types and basic validation are in place.
 */
public enum CulturesIniCommandType {
  VERSION(),
  MAPSIZE(),
  MAPGUID(),
  // Map
  MAPTYPE(MISC_MAPTYPE, pbn("Maptype", 1, 6)),
  MAPNAMESTRINGID(MISC_MAPNAME, pn("Mapname string")),
  MAPDESCRIPTIONSTRINGID(MISC_MAPNAME, pn("Mapdescription string")),
  // Misc
  MUSICTYPE(MISC_MUSIC, pbn("Music", 2, 38)),
  SETARTPOSITION(MISC_STARTPOSITION, pplayer(), pn("PosX"), pn("PosY")),
  SETPALETTE(MISC_HUMANGRAPHICS, pn("Human id"), pt("Palette name")),
  SETRAINRECTANGLE(
      MISC_WEATHER,
      pn("TopleftX"),
      pn("TopleftY"),
      pn("Bottomright"),
      pn("Bottomright"),
      pbn("Strength (100-5000)", 100, 5000)),
  TRADEAGREEMENT(
      MISC_TRADEAGREEMENT,
      pn("House id"),
      pn("Good type want"),
      pn("Amount want"),
      pn("Good type give"),
      pn("Amount give")),
  // GOALWONWHENGOODS(MISC_MULTIPLAYER_GOALS, pn("Good type"), pn("Amount type")),
  // goalwonwheninhabitants
  // goalwonbymission
  // goallostbymission
  // Player
  PLAYER(),
  DIPLOMACY(),
  NAMETRIBE(PLAYERMISC, pplayer(), pn("String")),
  NAMETRIBESHORT(PLAYERMISC, pplayer(), pn("String")),
  PLAYERNEVERDIES(PLAYERMISC, pplayer()),
  NOSEENFIRSTMESSAGE(PLAYERMISC, pplayerA(), pplayerB()),
  RELATIONNOTCHANGEABLE(PLAYERMISC, pplayerA(), pplayerB()),
  RELATIONHIDE(PLAYERMISC, pplayerA(), pplayerB()),
  // Multiplayer
  PLAYERFIXCOLORS(),
  PLAYEROPTION(),
  PLAYERHIDEINMENU(),
  // Special items
  ADD(SPECIALITEMS, pplayer(), pbn("Letter type", 2, 4), pn("House type")),
  // Allowed things
  FORBIDJOB(ALLOWEDTHINGS, pplayer(), pbn("Tribetype human", 0, 7), pbn("Job type", 1, 55)),
  FORBIDGOOD(ALLOWEDTHINGS, pplayer(), pbn("Tribetype human", 0, 7), pbn("Good type", 1, 63)),
  FORBIDHOUSE(ALLOWEDTHINGS, pplayer(), pbn("Tribetype human", 0, 7), pbn("House type", 0, 54)),
  // StaticObjects
  SETHOUSE(),
  SETVEHICLE(),
  ADDGOODS(),
  SETHUMAN(),
  SETPRODUCEDGOOD(),
  SETNAME(MISC_HUMANNAMES, pn("Human Id"), pn("String Id")),
  SETEXPIERENCE(),
  ATTACHTOHOUSE(),
  // SETPRODUCEGOOD(STATICOBJECTS, pt("Good type")), //?
  ATTACHTOVEHICLE(),
  MOVEINTOVEHICLE(),
  MARRY(),
  CHILDOFWOMAN(),
  SETANIMAL(),
  SETGUIDE(),
  // Text; Check that it is invalid string.ini file and others are not
  STRINGN(TEXT, pn("Id"), ps("String")),
  // Missions
  ACTIVE(MISSIONDATA, pbn("Is endabled on map start", 0, 1)),
  VISIBLE(MISSIONDATA, pbn("Is shown in goals", 0, 1)),
  DESCRIPTION(MISSIONDATA, pn("String")),
  DEBUGINFO(MISSIONDATA, ps("String")),
  GOAL(MISSIONDATA),
  RESULT(MISSIONDATA),
  // aidata
  AI_DISABLE(),
  HAI_DISABLE(),
  AI_UNITLIMIT(),
  AI_MAXUNITLIMIT(),
  AI_SETCONDITION_TRUE(),
  AI_SETCONDITION_ONTIME(),
  AI_SETCONDITION_ONCONDITIONS(),
  AI_SETCONDITION_ONCONDITIONSCHANGEDELAYED(),
  AI_SETCONDITION_ONDIPLOMACYCHANGE(),
  AI_SETCONDITION_ONCREATUREINRANGE(),
  AI_SETCONDITION_ONHOUSEINRANGE(),
  AI_SETCONDITION_ONPLAYERSEEN(),
  AI_SETCONDITION_ONPLAYERDEAD(),
  AI_SETCONDITION_ONNUMBEROFSOLDIERS(),
  AI_SETCONDITION_ONEXTERNAL(),
  AI_SETCONDITION_ONTIMER(),
  // AI TASKS
  AI_SOLDIERSDEFAULTPOSITION(AIDATA),
  AI_MAINTASK_DEFEND(AIDATA),
  AI_MAINTASK_ATTACK(AIDATA),
  AI_MAINTASK_BUILDHOUSE(AIDATA),
  AI_MAINTASK_CHANGEDIPLOMACY(AIDATA),
  AI_MAINTASK_CREATECREATURES(AIDATA),
  AI_MAINTASK_SELFDESTROYPLAYER(AIDATA),
  ;

  private static final Map<String, CulturesIniCommandType> LOWERCASE_MAP;

  static {
    HashMap<String, CulturesIniCommandType> m = new HashMap<>();

    for (CulturesIniCommandType c : values()) {
      m.put(c.name(), c);
    }

    LOWERCASE_MAP = Collections.unmodifiableMap(m);
  }

  public static CulturesIniCommandType find(String command) {
    // return LOWERCASE_MAP.get(command);
    try {
      return valueOf(command.toUpperCase());
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }

  @Getter private final CommandInformation commandInformation;

  @SneakyThrows
  CulturesIniCommandType() {
    try {
      String fileName = "commands/" + name().toLowerCase() + ".json";
      InputStream in = CulturesIniCommandType.class.getResourceAsStream(fileName);
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

  CulturesIniCommandType(CulturesIniCategoryType category, ParameterInfo<?>... parameterTypes) {
    this(false, category, parameterTypes.length, parameterTypes.length, parameterTypes);
  }

  CulturesIniCommandType(
      CulturesIniCategoryType category,
      int paramMin,
      int paramMax,
      ParameterInfo<?>... parameterTypes) {
    this(false, category, paramMin, paramMax, parameterTypes);
  }

  CulturesIniCommandType(CulturesIniCategoryType category) {
    this(true, category, -1, -1);
  }

  CulturesIniCommandType(
      boolean special,
      CulturesIniCategoryType category,
      int paramMin,
      int paramMax,
      ParameterInfo<?>... parameterTypes) {
    this.commandInformation =
        new CommandInformationMapper()
            .map(name(), special, category, paramMin, paramMax, parameterTypes);
  }

  /** Sets the parameter as number. */
  private static ParameterInfo<Integer> pn() {
    return new NumberParameterInfo("Number");
  }

  /** Sets the parameter as number. */
  private static ParameterInfo<Integer> pn(String name) {
    return new NumberParameterInfo(name);
  }

  /**
   * Sets the parameter as number where only a specific range is accepted.
   *
   * @param name the parameter name
   * @param min the minimum number (inclusive)
   * @param max the maximum number (inclusive)
   */
  private static ParameterInfo<Integer> pbn(String name, int min, int max) {
    return new BoundedNumberParameterInfo(name, min, max);
  }

  /**
   * Sets the parameter as specific number which allows only values given by the array.
   *
   * @param name the parameter name
   * @param numbers the numbers that are accepted
   */
  private static ParameterInfo<Integer> psn(String name, int... numbers) {
    return new SpecificNumberParameterInfo(name, NUMBER, numbers);
  }

  private static ParameterInfo<String> pt(String name) {
    return new TypeParameterInfo(name);
  }

  /** Sets the parameter as string. */
  private static ParameterInfo<String> ps(String name) {
    return new StringParameterInfo(name);
  }

  private static ParameterInfo<Integer> pplayerA() {
    return pbn("Player this", 0, 20);
  }

  private static ParameterInfo<Integer> pplayerB() {
    return pbn("Player other", 0, 20);
  }

  private static ParameterInfo<Integer> pplayer() {
    return pbn("Player", 0, 20);
  }

  /**
   * Describes an expected parameter.
   *
   * @param <T>
   */
  public interface ParameterInfo<T> {
    String getName();

    CulturesIniCommand.Parameter.Type getType();
  }

  @Value
  public static class StringParameterInfo implements ParameterInfo<String> {
    String name;
    CulturesIniCommand.Parameter.Type type = STRING;
  }

  @Value
  public static class TypeParameterInfo implements ParameterInfo<String> {
    String name;
    CulturesIniCommand.Parameter.Type type = CulturesIniCommand.Parameter.Type.TYPE;
  }

  @RequiredArgsConstructor
  @Getter
  @EqualsAndHashCode
  @ToString
  public static class NumberParameterInfo implements ParameterInfo<Integer> {
    private final String name;
    private final CulturesIniCommand.Parameter.Type type = CulturesIniCommand.Parameter.Type.NUMBER;
  }

  @Getter
  @EqualsAndHashCode(callSuper = true)
  @ToString(callSuper = true)
  public static class BoundedNumberParameterInfo extends NumberParameterInfo {
    int min;
    int max;

    public BoundedNumberParameterInfo(String name, int min, int max) {
      super(name);
      this.min = min;
      this.max = max;
    }

    private Integer validate(int v) throws ValidationException {
      if (min <= v && v <= max) {
        return v;
      } else {
        throw new ValidationException(getName() + " must be between " + min + " and " + max + "!");
      }
    }
  }

  @Value
  public static class SpecificNumberParameterInfo implements ParameterInfo<Integer> {
    String name;
    CulturesIniCommand.Parameter.Type type;
    int[] expected;
  }

  public static class ValidationException extends Exception {
    public ValidationException(String message) {
      super(message);
    }
  }
}
