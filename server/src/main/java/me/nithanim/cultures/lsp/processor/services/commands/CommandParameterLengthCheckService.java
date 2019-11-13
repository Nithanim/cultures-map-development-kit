package me.nithanim.cultures.lsp.processor.services.commands;

import lombok.RequiredArgsConstructor;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCategory;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommandType;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniLine;
import me.nithanim.cultures.lsp.processor.services.clientpersistent.DiagnosticsService;
import me.nithanim.cultures.lsp.processor.util.DiagnosticsCollector;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandParameterLengthCheckService {
    private final DiagnosticsService diagnosticsService;

    public List<? extends CulturesIniLine> onData(List<? extends CulturesIniLine> all) {
        CulturesIniCategory currCat = null;

        List<CulturesIniLine> modified = new ArrayList<>(all.size());
        DiagnosticsCollector dc = new DiagnosticsCollector();
        for(CulturesIniLine line : all) {
            if(line instanceof CulturesIniCategory) {
                currCat = (CulturesIniCategory) line;
            } else if(line instanceof CulturesIniCommand) {
                CulturesIniCommand cmd = (CulturesIniCommand) line;
                boolean valid = true;
                if(currCat == null) {
                    dc.addError(cmd.getOriginAll(), "Command is outside a category!");
                    valid = false;
                } else {
                    if(currCat.getType() != cmd.getCommandType().getCategory()) {
                        dc.addError(cmd.getOriginAll(), "Command belongs to category " + cmd.getCommandType().getCategory().getRealname() + "!");
                        valid = false;
                    }
                }
                valid &= checkParameterLength(cmd, dc);

                if(!valid) {
                    modified.add(cmd.setInvalid());
                } else {
                    modified.add(cmd);
                }
            } else {
                modified.add(line);
            }
        }
        diagnosticsService.updateDiagnostics(DiagnosticsService.Type.PARAMETER_CHECK, dc);
        return modified;
    }

    private boolean checkParameterLength(CulturesIniCommand cmd, DiagnosticsCollector dc) {
        if (cmd.getCommandType() == null) {
            return false;
        } else if(cmd.getCommandType().isSpecial()) {
            return false;
        }
        boolean valid = true;

        // Handle excess
        if(cmd.getParameters().size() > cmd.getCommandType().getParamMax()) {
            for (int i = cmd.getCommandType().getParamMax(); i < cmd.getParameters().size(); i++) {
                dc.addError(cmd.getParameter(i).getOrigin(), "Excess parameter!");
                //valid = false;
            }
        }

        // Handle minimal
        List<? extends CulturesIniCommandType.ParameterInfo> parameterInfos = cmd.getCommandType().getParameterInfo();
        if (cmd.getParameters().size() < cmd.getCommandType().getParamMin()) {
            List<String> names = parameterInfos.stream().map(i -> i.getName()).collect(Collectors.toList());
            String msg = "Too few parameters!\n"
                    + "Expected: " + String.join(", ", names) + "\n"
                    + "Missing: " + names.stream().skip(cmd.getParameters().size()).collect(Collectors.joining(", "));
            dc.addError(cmd.getOriginAll(), msg);
            valid = false;
        }
        return valid;
    }
}
