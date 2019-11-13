package me.nithanim.cultures.lsp.processor.services.clientpersistent;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import lombok.NonNull;
import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.Origin;
import me.nithanim.cultures.lsp.processor.util.SourceFile;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ReturnValuesAreNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class GenericKeyPerSourceService<KEY_TYPE extends Enum, DATA_TYPE extends GenericKeyPerSourceService.Origination> {
    private Map<SourceFile, Set<Key>> keys = new HashMap<>();
    private Map<Key, List<DATA_TYPE>> things = new HashMap<>();

    /**
     * Marks source files where we update something so we need to send the dater for it.
     */
    private Set<SourceFile> dirty = new HashSet<>();

    /**
     * Stores for which files we sent date so we know if we can just overwrite it or need to delete it if we have empty data.
     */
    private Set<SourceFile> sent = new HashSet<>();

    @Nullable
    public List<DATA_TYPE> getAll(SourceFile sourceFile) {
        Set<Key> keys = this.keys.get(sourceFile);
        if (keys != null) {
            return keys
                    .stream()
                    .flatMap(k -> things.get(k).stream())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /**
     * Updates
     * @param type
     * @param things
     */
    public void update(KEY_TYPE type, List<DATA_TYPE> things) {
        delete(type); //new would overwrite old but if there are none they would never be removed
        if(things.isEmpty()) {
            // TODO
        }
        insert(type, things);
    }

    private void insert(KEY_TYPE type, List<DATA_TYPE> things) {
        Map<SourceFile, List<DATA_TYPE>> bySourceFile = things
                .stream()
                .collect(Collectors.groupingBy(d -> d.getOrigin().getSourceFile()));
        bySourceFile.forEach((k, v) -> update(k, type, v));
    }

    private void delete(KEY_TYPE type) {
        List<Key> allAffected = keys.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(k -> k.getType() == type)
                .collect(Collectors.toList());
        allAffected.forEach(this::removeKey);
    }

    private void removeKey(Key key) {
        keys.get(key.getSourceFile()).remove(key);
        things.remove(key);
    }


    public void update(SourceFile sourceFile, KEY_TYPE type, List<DATA_TYPE> diagnostics) {
        dirty.add(sourceFile);
        Key key = new Key(sourceFile, type);
        keys.computeIfAbsent(sourceFile, sf -> new HashSet<>()).add(key);
        this.things.put(key, diagnostics);
        sent.add(sourceFile);
    }

    public void delete(SourceFile sourceFile) {
        Set<Key> keys = this.keys.remove(sourceFile);
        keys.forEach(k -> things.remove(k));
        dirty.add(sourceFile);
        sent.remove(sourceFile);
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

        List<DATA_TYPE> ds = keys.stream()
                .map(things::get)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        _publish(dirty, ds);
    }

    protected abstract void _publish(SourceFile sf, List<DATA_TYPE> things);


    @Value
    private class Key {
        SourceFile sourceFile;
        KEY_TYPE type;
    }

    public interface Origination {
        Origin getOrigin();
    }
}
