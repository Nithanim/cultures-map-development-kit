package me.nithanim.cultures.lsp.processor.services.clientpersistent;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import org.springframework.stereotype.Service;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ReturnValuesAreNonnullByDefault
@ParametersAreNonnullByDefault
@Service
public class CodeLensService extends GenericKeyPerSourceService<CodeLensService.Type, MyCodeLens> {
    @Override
    protected void _publish(SourceFile sf, List<MyCodeLens> things) {
        throw new UnsupportedOperationException();
    }

    public enum Type {
        MISSION_ID,
        SETHUMAN
    }
}
