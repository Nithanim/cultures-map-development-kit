package me.nithanim.cultures.lsp.processor.services;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import me.nithanim.cultures.lsp.db.public_.tables.Intelligence;
import me.nithanim.cultures.lsp.db.public_.tables.records.IntelligenceRecord;
import me.nithanim.cultures.lsp.processor.events.SourceFileParsedEvent;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCategory;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniLine;
import me.nithanim.cultures.lsp.processor.lines.UnknownCulturesIniLine;
import me.nithanim.cultures.lsp.processor.model.DefinitionEnvironment;
import me.nithanim.cultures.lsp.processor.util.MyPosition;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/** Holds information about what command an parameters are where defined. */
// The whole thing has the shortcoming that it has no ordering of files TODO
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
                Intelligence.INTELLIGENCE.CATEGORY_TYPE,
                Intelligence.INTELLIGENCE.CATEGORY_NUMBER,
                Intelligence.INTELLIGENCE.DATA);

    var numberOccurrences =
        new EnumMap<CulturesIniCategoryType, Integer>(CulturesIniCategoryType.class);
    for (CulturesIniLine line : lines) {
      String uri = line.getOriginAll().getSourceFile().getUri().toString();
      int lineNumber = line.getOriginAll().getRange().getStart().getLine();
      String lineType = line.getLineType().name();

      if (line instanceof CulturesIniCommand) {
        CulturesIniCommand command = (CulturesIniCommand) line;

        CulturesIniCategoryType categoryType =
            command.getCommandType().getCommandInformation().getCategory();
        Integer categoryNumber = numberOccurrences.getOrDefault(categoryType, 0);
        insertStatement.values(
            uri, lineNumber, lineType, categoryType.name(), categoryNumber, line);
      } else if (line instanceof CulturesIniCategory) {
        CulturesIniCategory cat = (CulturesIniCategory) line;
        if (cat.getCategoryType() != null) { // an invalid category has no type
          Integer categoryNumber =
              numberOccurrences.compute(cat.getCategoryType(), (k, v) -> (v == null) ? 0 : v + 1);

          insertStatement.values(
              uri, lineNumber, lineType, cat.getCategoryType().name(), categoryNumber, line);
        }
      } else if (line instanceof UnknownCulturesIniLine) {
        insertStatement.values(uri, lineNumber, lineType, "", 0, line);
      }
    }
    insertStatement.execute();
  }

  public CulturesIniCategoryType getCurrentCategoryType(MyPosition position) {
    var category =
        jooq.select(Intelligence.INTELLIGENCE.DATA)
            .from(Intelligence.INTELLIGENCE)
            .where(
                Intelligence.INTELLIGENCE
                    .SOURCE_FILE
                    .eq(position.getSourceFile().getUri().toString())
                    .and(Intelligence.INTELLIGENCE.LINE_NUMBER.lt(position.getPosition().getLine()))
                    .and(
                        Intelligence.INTELLIGENCE.LINE_TYPE.eq(
                            CulturesIniLine.Type.CATEGORY.name())))
            .orderBy(Intelligence.INTELLIGENCE.LINE_NUMBER.desc())
            .limit(1)
            .fetchOne(0, CulturesIniCategory.class);

    if (category == null) {
      return null;
    } else {
      return category.getCategoryType();
    }
  }

  public List<List<CulturesIniCommand>> getAllCommandsInSingleCategoryDefinition(
      CulturesIniCategoryType categoryType) {
    var records =
        jooq.select()
            .from(Intelligence.INTELLIGENCE)
            .where(Intelligence.INTELLIGENCE.CATEGORY_TYPE.eq(categoryType.name()))
            .orderBy(Intelligence.INTELLIGENCE.LINE_NUMBER.asc())
            .fetchInto(Intelligence.INTELLIGENCE);

    List<List<CulturesIniCommand>> map = new ArrayList<>();
    List<CulturesIniCommand> currentList = null;
    int previousCategoryNumber = -1;
    for (IntelligenceRecord record : records) {
      int currentCategoryNumber = record.getCategoryNumber();

      if (previousCategoryNumber != currentCategoryNumber) {
        if (currentList != null) {
          map.add(currentList);
        }
        currentList = new ArrayList<>();
        previousCategoryNumber = currentCategoryNumber;
      }

      CulturesIniCommand command = (CulturesIniCommand) record.getData();
      currentList.add(command);
    }
    if (currentList != null) {
      map.add(currentList);
    }

    return map;
  }

  public List<CulturesIniCommand> getByPositionAllCommandsInSingleCategoryDefinition(
      MyPosition position) {
    IntelligenceRecord anyRecord = getByPositionInternal(position);
    if (!(anyRecord.getData() instanceof CulturesIniCommand)) {
      return null;
    }

    List<CulturesIniCommand> commands =
        jooq.select(Intelligence.INTELLIGENCE.DATA)
            .from(Intelligence.INTELLIGENCE)
            .where(
                Intelligence.INTELLIGENCE
                    .CATEGORY_TYPE
                    .eq(anyRecord.getCategoryType())
                    .and(
                        Intelligence.INTELLIGENCE.CATEGORY_NUMBER.eq(
                            anyRecord.getCategoryNumber())))
            .orderBy(Intelligence.INTELLIGENCE.LINE_NUMBER.asc())
            .fetch(0, CulturesIniCommand.class);

    if (commands == null) {
      return null;
    } else {
      return commands;
    }
  }

  public CulturesIniCommand getByPositionOnlyCommand(MyPosition position) {
    CulturesIniLine r = getByPosition(position);
    if (r instanceof CulturesIniCommand) {
      return (CulturesIniCommand) r;
    } else {
      return null;
    }
  }

  public CulturesIniLine getByPosition(MyPosition position) {
    IntelligenceRecord cilRecord = getByPositionInternal(position);
    if (cilRecord == null) {
      return null;
    } else {
      return cilRecord.getData();
    }
  }

  private IntelligenceRecord getByPositionInternal(MyPosition position) {
    return jooq.select()
        .from(Intelligence.INTELLIGENCE)
        .where(
            Intelligence.INTELLIGENCE
                .SOURCE_FILE
                .eq(position.getSourceFile().getUri().toString())
                .and(Intelligence.INTELLIGENCE.LINE_NUMBER.eq(position.getPosition().getLine())))
        .fetchOneInto(Intelligence.INTELLIGENCE);
  }
}
