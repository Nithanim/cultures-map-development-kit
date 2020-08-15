package me.nithanim.cultures.lsp.processor.services.clientpersistent;

import java.util.List;
import me.nithanim.cultures.lsp.db.public_.tables.Codelens;
import me.nithanim.cultures.lsp.db.public_.tables.records.CodelensRecord;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CodeLensService {
  @Autowired private DSLContext jooq;

  public void update(CodeLensService.Type type, List<MyCodeLense> collect) {
    jooq.deleteFrom(Codelens.CODELENS).where(Codelens.CODELENS.TYPE.eq(type.name())).execute();
    InsertValuesStep3<CodelensRecord, String, String, MyCodeLense> stmt =
        jooq.insertInto(
            Codelens.CODELENS,
            Codelens.CODELENS.TYPE,
            Codelens.CODELENS.SOURCE_FILE,
            Codelens.CODELENS.DATA);
    for (MyCodeLense cl : collect) {
      stmt.values(type.name(), cl.getOrigin().getSourceFile().getUri().toString(), cl);
    }
    stmt.execute();
  }

  public List<MyCodeLense> getAll(SourceFile sourceFile) {
    return jooq.select(Codelens.CODELENS.DATA)
        .from(Codelens.CODELENS)
        .where(Codelens.CODELENS.SOURCE_FILE.eq(sourceFile.getUri().toString()))
        .fetch(0, MyCodeLense.class);
  }

  public enum Type {
    MISSION_ID,
    SETHUMAN
  }
}
