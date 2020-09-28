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
import static me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType.MISSIONDATA;
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
  MAPTYPE(),
  MAPCAMPAIGNID(),
  MAPNAMESTRINGID(),
  MAPDESCRIPTIONSTRINGID(),
  // Misc
  MUSICTYPE(),
  STARTPOSITION(),
  SETPALETTE(),
  SETRAINRECTANGLE(),
  SETSNOWRECTANGLE(),
  SETSANDRECTANGLE(),
  TRADEAGREEMENT(),
  // misc multiplayer
  GOALWONWHENGOODS(),
  GOALWONWHENINHABITANTS(),
  GOALWONBYMISSION(),
  GOALLOSTBYMISSION(),
  // Player
  PLAYER(),
  DIPLOMACY(),
  NAMETRIBE(),
  NAMETRIBESHORT(),
  PLAYERNEVERDIES(),
  NOSEENFIRSTMESSAGE(),
  RELATIONNOTCHANGEABLE(),
  RELATIONHIDE(),
  // Multiplayer
  PLAYERFIXCOLORS(),
  PLAYEROPTION(),
  PLAYERHIDEINMENU(),
  // Special items
  ADD(),
  // Allowed things
  FORBIDJOB(),
  FORBIDGOOD(),
  FORBIDHOUSE(),
  // StaticObjects
  SETHOUSE(),
  SETVEHICLE(),
  ADDGOODS(),
  SETHUMAN(),
  SETPRODUCEDGOOD(),
  SETNAME(),
  SETEXPIERENCE(),
  ATTACHTOHOUSE(),
  ATTACHTOVEHICLE(),
  MOVEINTOVEHICLE(),
  MARRY(),
  CHILDOFWOMAN(),
  SETANIMAL(),
  SETGUIDE(),
  // Text; Check that it is invalid string.ini file and others are not
  STRINGN(TEXT, pn("Id"), ps("String")),
  // Missions
  ACTIVE(),
  VISIBLE(),
  DESCRIPTION(),
  DEBUGINFO(),
  SUCCESSFULLIF(),
  GOAL(),
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
  AI_SOLDIERSDEFAULTPOSITION(),
  AI_MAINTASK_DEFEND(),
  AI_MAINTASK_ATTACK(),
  AI_MAINTASK_BUILDHOUSE(),
  AI_MAINTASK_CHANGEDIPLOMACY(),
  AI_MAINTASK_CREATECREATURES(),
  AI_MAINTASK_SELFDESTROYPLAYER(),
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
