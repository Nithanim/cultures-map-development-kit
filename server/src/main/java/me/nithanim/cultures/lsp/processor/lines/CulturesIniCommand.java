package me.nithanim.cultures.lsp.processor.lines;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.Origin;

@Value
public class CulturesIniCommand implements CulturesIniLine {
  Type lineType = Type.COMMAND;
  CulturesIniCommandType commandType;
  Origin originAll;
  Origin originBaseCommand;
  List<Parameter> parameters;
  boolean invalid;

  public CulturesIniCommand(
      CulturesIniCommandType commandType,
      Origin originAll,
      Origin originBaseCommand,
      List<Parameter> parameters) {
    this(commandType, originAll, originBaseCommand, parameters, false);
  }

  private CulturesIniCommand(
      CulturesIniCommandType commandType,
      Origin originAll,
      Origin originBaseCommand,
      List<Parameter> parameters,
      boolean invalid) {
    this.commandType = commandType;
    this.originAll = originAll;
    this.originBaseCommand = originBaseCommand;
    this.parameters = parameters;
    this.invalid = invalid;
  }

  public CulturesIniCommand setInvalid() {
    return new CulturesIniCommand(commandType, originAll, originBaseCommand, parameters, true);
  }

  public static ParsedCulturesIniCommandBuilder builder() {
    return new ParsedCulturesIniCommandBuilder();
  }

  public Parameter getParameter(int i) {
    return parameters.get(i);
  }

  @Override
  public String printLine() {
    List<String> parts = new ArrayList<>();
    parts.add(commandType.name().toLowerCase());
    for (Parameter p : parameters) {
      if (p.getType() == Parameter.Type.STRING || p.getType() == Parameter.Type.TYPE) {
        parts.add("\"" + p.getValue() + "\"");
      } else {
        parts.add(p.getValue());
      }
    }
    return String.join(" ", parts);
  }

  @Value
  public static class Parameter {
    String value;
    Type type;
    Origin origin;

    public int getValueAsInt() {
      return Integer.parseInt(value);
    }

    public enum Type {
      /** Some numeric value. */
      NUMBER,
      /** A string with user defined content. Used for debuginfo and string definitions. */
      STRING,
      /** Like a string but only accepts a limited set of values. */
      @Deprecated
      TYPE,
      /** Constant that can hold different values and has to be resolved later. */
      CONSTANT
    }
  }

  @Getter
  public static class ParsedCulturesIniCommandBuilder {
    private CulturesIniCommandType commandType;
    private Origin originAll;
    private Origin originBaseCommand;
    private final List<Parameter> parameters = new ArrayList<>();

    ParsedCulturesIniCommandBuilder() {}

    public ParsedCulturesIniCommandBuilder type(CulturesIniCommandType type) {
      this.commandType = type;
      return this;
    }

    public ParsedCulturesIniCommandBuilder originAll(Origin originAll) {
      this.originAll = originAll;
      return this;
    }

    public ParsedCulturesIniCommandBuilder originBaseCommand(Origin originBaseCommand) {
      this.originBaseCommand = originBaseCommand;
      return this;
    }

    public ParsedCulturesIniCommandBuilder addParameter(Parameter parameter) {
      this.parameters.add(parameter);
      return this;
    }

    public CulturesIniCommand build() {
      return new CulturesIniCommand(commandType, originAll, originBaseCommand, parameters);
    }

    public String toString() {
      return "CulturesIniCommand.ParsedCulturesIniCommandBuilder(commandType="
          + this.commandType
          + ", originAll="
          + this.originAll
          + ", originBaseCommand="
          + this.originBaseCommand
          + ", parameters="
          + this.parameters
          + ")";
    }
  }
}
