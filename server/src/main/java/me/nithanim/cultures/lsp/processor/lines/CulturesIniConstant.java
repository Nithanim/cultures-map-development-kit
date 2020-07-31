package me.nithanim.cultures.lsp.processor.lines;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.Origin;

@Value
@Builder
@AllArgsConstructor
public class CulturesIniConstant implements CulturesIniLine {
  Origin originAll;
  Origin originKey;
  Origin originValue;
  String name;
  String valueString;
  int valueInt;

  public CulturesIniConstant(
      Origin originAll, Origin originKey, Origin originValue, String name, String value) {
    this.originAll = originAll;
    this.originKey = originKey;
    this.originValue = originValue;
    this.name = name;
    this.valueString = value;
    this.valueInt = -1;
  }

  public CulturesIniConstant(
      Origin originAll, Origin originKey, Origin originValue, String name, int value) {
    this.originAll = originAll;
    this.originKey = originKey;
    this.originValue = originValue;
    this.name = name;
    this.valueString = null;
    this.valueInt = value;
  }

  /*public CulturesIniConstant(Origin origin, String name, String value) {
      this.origin = origin;
      this.name = name;

      boolean isInt = true;
      int v = 0;
      if(name.length() < 5) {
          for (int i = 0; i < name.length(); i++) {
              char c = name.charAt(i);
              if('0' <= c && c <= '9') {
                  v += (c - '0') * Math.pow(10, i);
              } else {
                  isInt = false;
                  break;
              }
          }
      }
      if(isInt) {
          valueString = null;
          valueInt = v;
      } else {
          valueString = value;
          valueInt = -1;
      }
  }*/
  public boolean isString() {
    return valueString != null;
  }

  public boolean isInteger() {
    return valueString == null;
  }
}
