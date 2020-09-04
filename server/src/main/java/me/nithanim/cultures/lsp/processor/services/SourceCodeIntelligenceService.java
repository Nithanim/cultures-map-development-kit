package me.nithanim.cultures.lsp.processor.services;

import java.util.List;
import me.nithanim.cultures.lsp.db.public_.tables.Intelligence;
import me.nithanim.cultures.lsp.db.public_.tables.records.IntelligenceRecord;
import me.nithanim.cultures.lsp.processor.events.SourceFileParsedEvent;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniLine;
import me.nithanim.cultures.lsp.processor.model.DefinitionEnvironment;
import me.nithanim.cultures.lsp.processor.util.MyPosition;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/** Holds information about what command an parameters are where defined. */
@Service
public class SourceCodeIntelligenceService {
  @Autowired private DefinitionEnvironment definitionEnvironment;
  @Autowired private DSLContext jooq;

  @EventListener
  public void onSourceFileParsed(SourceFileParsedEvent evt) {
    List<? extends CulturesIniLine> lines = definitionEnvironment.getLines(evt.getSourceFile());
    jooq.deleteFrom(Intelligence.INTELLIGENCE)
        .where(Intelligence.INTELLIGENCE.SOURCE_FILE.eq(evt.getSourceFile().getUri().toString()))
        .execute();
    var insertStatement =
        jooq.insertInto(Intelligence.INTELLIGENCE)
            .columns(
                Intelligence.INTELLIGENCE.SOURCE_FILE,
                Intelligence.INTELLIGENCE.LINE_NUMBER,
                Intelligence.INTELLIGENCE.LINE_TYPE,
                Intelligence.INTELLIGENCE.DATA);

    for (CulturesIniLine line : lines) {
      if (line instanceof CulturesIniCommand) {
        CulturesIniCommand command = (CulturesIniCommand) line;

        insertStatement.values(
            command.getOriginAll().getSourceFile().getUri().toString(),
            command.getOriginAll().getRange().getStart().getLine(),
            command.getLineType().name(),
            line);
      }
    }
    insertStatement.execute();
  }

  public CulturesIniLine findFor(MyPosition position) {
    IntelligenceRecord cilRecord =
        jooq.select(Intelligence.INTELLIGENCE.DATA)
            .from(Intelligence.INTELLIGENCE)
            .where(
                Intelligence.INTELLIGENCE
                    .SOURCE_FILE
                    .eq(position.getSourceFile().getUri().toString())
                    .and(
                        Intelligence.INTELLIGENCE.LINE_NUMBER.eq(position.getPosition().getLine())))
            .fetchOneInto(Intelligence.INTELLIGENCE);
    if (cilRecord == null) {
      return null;
    } else {
      return cilRecord.getData();
    }
  }

  public CulturesIniCommand findForOnlyCommand(MyPosition position) {
    CulturesIniLine r = findFor(position);
    if (r instanceof CulturesIniCommand) {
      return (CulturesIniCommand) r;
    } else {
      return null;
    }
  }
}
