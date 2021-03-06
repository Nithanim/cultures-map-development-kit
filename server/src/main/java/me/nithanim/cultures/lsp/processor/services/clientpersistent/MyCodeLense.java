package me.nithanim.cultures.lsp.processor.services.clientpersistent;

import java.io.Serializable;
import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.Origin;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Command;

@Value
public class MyCodeLense implements Origination, Serializable {
  Origin origin;
  CodeLens codeLens;

  public static MyCodeLense of(Origin o, String text) {
    Command c = new Command();
    c.setCommand("");
    c.setTitle(text);
    CodeLens cl = new CodeLens();
    cl.setCommand(c);
    cl.setRange(o.getRange());
    return new MyCodeLense(o, cl);
  }
}
