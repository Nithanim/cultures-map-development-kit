package me.nithanim.cultures.lsp.processor.services.clientpersistent;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.DiagnosticsCollector;
import me.nithanim.cultures.lsp.processor.util.MyDiagnostic;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.services.LanguageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@ReturnValuesAreNonnullByDefault
@ParametersAreNonnullByDefault
public class DiagnosticsServiceOld {
    private Map<SourceFile, Set<Key>> keys = new HashMap<>();
    private Map<Key, List<MyDiagnostic>> diagnostics = new HashMap<>();

    private Set<SourceFile> dirty = new HashSet<>();
    @Autowired
    private LanguageClient client;

    @Nullable
    public List<MyDiagnostic> getAllDiagnostics(SourceFile sourceFile) {
        Set<Key> keys = this.keys.get(sourceFile);
        if (keys != null) {
            return keys
                    .stream()
                    .flatMap(k -> diagnostics.get(k).stream())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public void updateDiagnostics(Type type, DiagnosticsCollector dc) {
        updateDiagnostics(type, dc.getDiagnostics());
    }

    public void updateDiagnostics(Type type, List<MyDiagnostic> diagnostics) {
        deleteDiagnostics(type); //new would overwrite old but if there are none they would never be removed
        insertDiagnostics(type, diagnostics);
    }

    private void insertDiagnostics(Type type, List<MyDiagnostic> diagnostics) {
        Map<SourceFile, List<MyDiagnostic>> bySourceFile = diagnostics.stream().collect(Collectors.groupingBy(d -> d.getOrigin().getSourceFile()));
        bySourceFile.forEach((k, v) -> {
            updateDiagnostics(k, type, v);
        });
    }

    private void deleteDiagnostics(Type type) {
        List<Key> allAffected = keys.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(k -> k.getType() == type)
                .collect(Collectors.toList());
        allAffected.forEach(this::removeKey);
    }

    private void removeKey(Key key) {
        keys.get(key.getSourceFile()).remove(key);
        diagnostics.remove(key);
    }


    public void updateDiagnostics(SourceFile sourceFile, Type type, List<MyDiagnostic> diagnostics) {
        dirty.add(sourceFile);
        Key key = new Key(sourceFile, type);
        keys.computeIfAbsent(sourceFile, sf -> new HashSet<>()).add(key);
        this.diagnostics.put(key, diagnostics);
    }

    public void deleteDiagnostics(SourceFile sourceFile) {
        Set<Key> keys = this.keys.remove(sourceFile);
        keys.forEach(k -> diagnostics.remove(k));
        dirty.add(sourceFile);
    }

    public void publishAll() {
        for (SourceFile dirty : this.dirty) {
            publish(dirty);
        }
    }

    private void publish(SourceFile dirty) {
        Set<Key> keys = this.keys.get(dirty);
        if (keys == null) {
            keys = Collections.emptySet();
        }

        PublishDiagnosticsParams dipa = new PublishDiagnosticsParams();
        List<Diagnostic> ds = keys.stream()
                .map(diagnostics::get)
                .flatMap(List::stream)
                .map(MyDiagnostic::toDiagnostic)
                .collect(Collectors.toList());
        dipa.setDiagnostics(ds);
        dipa.setUri(dirty.getUri().toString());
        client.publishDiagnostics(dipa);
    }

    public enum Type {
        PARSING,
        CODE_LENS,
        CONSTANT_RESOLVER,
        PARAMETER_CHECK,
        COMMAND_CHECKER;
    }

    @Value
    private static class Key {
        SourceFile sourceFile;
        Type type;
    }
}
