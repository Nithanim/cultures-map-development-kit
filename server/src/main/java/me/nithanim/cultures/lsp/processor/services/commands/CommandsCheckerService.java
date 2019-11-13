package me.nithanim.cultures.lsp.processor.services.commands;

import me.nithanim.cultures.lsp.processor.events.ConstantsResolvedEvent;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.services.clientpersistent.DiagnosticsService;
import me.nithanim.cultures.lsp.processor.util.DiagnosticsCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommandsCheckerService {
    @Autowired
    DiagnosticsService diagnosticsService;
    @Autowired
    PlayerdataCommandsService playerdataCommandsService;

    @EventListener
    public void onData(ConstantsResolvedEvent evt) {
        playerdataCommandsService.reset();

        List<CulturesIniCommand> all = evt.getAllResolvedLines()
                .stream()
                .filter(l -> l instanceof CulturesIniCommand)
                .map(l -> (CulturesIniCommand) l)
                .collect(Collectors.toList());

        DiagnosticsCollector dc = new DiagnosticsCollector();
        for (CulturesIniCommand cmd : all) {
            if(cmd.isInvalid()) {
                continue;
            }
            switch (cmd.getCommandType().getCategory()) {
                case PLAYERDATA:
                case PLAYERMISC:
                    playerdataCommandsService.handle(cmd, dc);
                    break;
                case STATICOBJECTS:
                    //staticobjectsCommandsService.handle(cmd, dc);
                    break;
            }
        }

        playerdataCommandsService.postProcess(dc);
        diagnosticsService.updateDiagnostics(DiagnosticsService.Type.COMMAND_CHECKER, dc);
    }
}
