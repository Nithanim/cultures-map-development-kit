package me.nithanim.cultures.lsp.processor.lines;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.Origin;

import java.util.ArrayList;
import java.util.List;

@Value
public final class CulturesIniCommand implements CulturesIniLine {
    private final CulturesIniCommandType commandType;
    private final Origin originAll;
    private final Origin originBaseCommand;
    private final List<Parameter> parameters;
    private final boolean invalid;

    public CulturesIniCommand(CulturesIniCommandType commandType, Origin originAll, Origin originBaseCommand, List<Parameter> parameters) {
        this(commandType, originAll, originBaseCommand, parameters, false);
    }

    private  CulturesIniCommand(CulturesIniCommandType commandType, Origin originAll, Origin originBaseCommand, List<Parameter> parameters, boolean invalid) {
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

    @Value
    public static final class Parameter {
        private final String value;
        private final Type type;
        private final Origin origin;

        public int getValueAsInt() {
            return Integer.parseInt(value);
        }

        public enum Type {
            /**
             * Some numeric value.
             */
            NUMBER,
            /**
             * A string with user defined content. Used for debuginfo and string
             * definitions.
             */
            STRING,
            /**
             * Like a string but only accepts a limited set of values.
             */
            @Deprecated
            TYPE,
            /**
             * Constant that can hold different values and has to be resolved later.
             */
            CONSTANT
        }
    }

    @Getter
    public static class ParsedCulturesIniCommandBuilder {
        private CulturesIniCommandType commandType;
        private Origin originAll;
        private Origin originBaseCommand;
        private List<Parameter> parameters = new ArrayList<>();

        ParsedCulturesIniCommandBuilder() {
        }

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
            return "CulturesIniCommand.ParsedCulturesIniCommandBuilder(commandType=" + this.commandType + ", originAll=" + this.originAll + ", originBaseCommand=" + this.originBaseCommand + ", parameters=" + this.parameters + ")";
        }
    }
}
