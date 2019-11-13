package me.nithanim.cultures.lsp.processor.services.commands;

import lombok.Value;
import me.nithanim.cultures.lsp.processor.events.SourceFileParsedEvent;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCategory;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniLine;
import me.nithanim.cultures.lsp.processor.model.DefinitionEnvironment;
import me.nithanim.cultures.lsp.processor.services.WorkspaceService;
import me.nithanim.cultures.lsp.processor.services.clientpersistent.CodeLensService;
import me.nithanim.cultures.lsp.processor.services.clientpersistent.MyCodeLens;
import me.nithanim.cultures.lsp.processor.util.Origin;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CodeInsightService {
    @Autowired
    private DefinitionEnvironment definitionEnvironment;
    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private CodeLensService codeLensService;

    @Autowired
    StaticobjectsCommandsService staticobjectsCommandsService;

    @EventListener
    public void onSourceFileParsed(SourceFileParsedEvent evt) {
        List<MyCodeLens> staticobjects = new ArrayList<>();
        ArrayList<Mission> missions = new ArrayList<>();
        int c = 0;
        List<? extends CulturesIniLine> allLines = definitionEnvironment.getAllLinesRaw();
        for (CulturesIniLine l : allLines) {
            if (l instanceof CulturesIniCategory) {
                CulturesIniCategory cat = (CulturesIniCategory) l;
                if (cat.getType() == CulturesIniCategoryType.MISSIONDATA) {
                    missions.add(new Mission(c++, cat.getOriginAll()));
                }
            }
            if (l instanceof CulturesIniCommand) {
                CulturesIniCommand cmd = (CulturesIniCommand) l;
                switch (cmd.getCommandType().getCategory()) {
                    case STATICOBJECTS:
                        MyCodeLens socl = staticobjectsCommandsService.handle(cmd);
                        if (socl != null) {
                            staticobjects.add(socl);
                        }
                }
            }
        }

        codeLensService.update(
                CodeLensService.Type.MISSION_ID,
                missions.stream().map(m -> createCodeLens(m)).collect(Collectors.toList())
        );
        codeLensService.update(
                CodeLensService.Type.SETHUMAN,
                staticobjects
        );
    }

    private MyCodeLens createCodeLens(Mission m) {
        CodeLens o = new CodeLens(m.getOrigin().getRange(), new Command("Mission id " + m.getId(), ""), null);
        return new MyCodeLens(m.getOrigin(), o);
    }

    @Value
    private static class Mission {
        int id;
        Origin origin;
    }

    private static class CodeInsightContext {
        //private List<CodeLens>
    }
}
