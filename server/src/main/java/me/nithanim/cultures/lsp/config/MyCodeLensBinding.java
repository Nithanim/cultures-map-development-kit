package me.nithanim.cultures.lsp.config;

import me.nithanim.cultures.lsp.processor.services.clientpersistent.MyCodeLense;

public class MyCodeLensBinding extends PassthroughBinding<MyCodeLense> {
  public MyCodeLensBinding() {
    super(MyCodeLense.class);
  }
}
