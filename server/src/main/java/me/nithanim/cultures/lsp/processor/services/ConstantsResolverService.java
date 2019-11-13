package me.nithanim.cultures.lsp.processor.services;

import edu.umd.cs.findbugs.annotations.Nullable;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniConstant;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniLine;
import me.nithanim.cultures.lsp.processor.services.clientpersistent.DiagnosticsService;
import me.nithanim.cultures.lsp.processor.util.MyDiagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConstantsResolverService {
    @Autowired
    DiagnosticsService diagnosticsService;

    private Map<String, CulturesIniConstant> nameToConstant = Collections.emptyMap();
    private List<? extends CulturesIniLine> resolvedLines = Collections.emptyList();

    public void updateAllLines(List<? extends CulturesIniLine> prev) {
        List<CulturesIniLine> next = new ArrayList<>();
        Map<String, CulturesIniConstant> constants = new HashMap<>();
        List<MyDiagnostic> diagnostics = new ArrayList<>();

        for (CulturesIniLine prevLine : prev) {
            if (prevLine instanceof CulturesIniConstant) {
                CulturesIniConstant constant = (CulturesIniConstant) prevLine;

                CulturesIniConstant old = constants.put(constant.getName(), constant);
                if(old != null) {
                    diagnostics.add(MyDiagnostic.builder().message("Definition is overridden later!").origin(old.getOriginAll()).severity(DiagnosticSeverity.Warning).build());
                    diagnostics.add(MyDiagnostic.builder().message("Overrides previous definition!").origin(constant.getOriginAll()).severity(DiagnosticSeverity.Warning).build());
                }
            } else if (prevLine instanceof CulturesIniCommand) {
                CulturesIniCommand cmd = (CulturesIniCommand) prevLine;
                //TODO
                next.add(cmd);
            } else {
                next.add(prevLine);
            }
        }
        diagnosticsService.updateDiagnostics(DiagnosticsService.Type.CONSTANT_RESOLVER, diagnostics);

        nameToConstant = constants;
        resolvedLines = next;
    }

    @Nullable
    public CulturesIniConstant getConstantByName(String name) {
        return nameToConstant.get(name);
    }

    public List<? extends CulturesIniLine> getResolvedLines() {
        return resolvedLines;
    }
}
