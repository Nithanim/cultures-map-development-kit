package me.nithanim.cultures.lsp.processor.services;

import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.services.LanguageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Service
public class LanguageServerCommandService {
    public static final String COMMAND_EXTRACT_MAP = "culures-ini.extract-c2m";
    public static final String COMMAND_PACKAGE = "cultures-ini.package-map";
    private static Map<String, BiConsumer<LanguageServerCommandService, ExecuteCommandParams>> map;

    @Autowired
    private WorkspaceService workspaceService;
    private LanguageClient client;

    static {
        Map<String, BiConsumer<LanguageServerCommandService, ExecuteCommandParams>> m = new HashMap<>();
        m.put(COMMAND_EXTRACT_MAP, LanguageServerCommandService::extract);
        map = m;
    }

    public List<String> getCommands() {
        return new ArrayList<>(map.keySet());
    }

    public CompletableFuture<Object> execute(ExecuteCommandParams params) {
        BiConsumer<LanguageServerCommandService, ExecuteCommandParams> o = map.get(params.getCommand());
        o.accept(this, params);
        return CompletableFuture.completedFuture(null);
    }

    private void extract(ExecuteCommandParams p) {
        Path maproot = workspaceService.getWorkspacePath();
        Path mapFile = maproot.resolve("map.c2m");
        try {
            Path libRoot = MapFileUtil.extractMapBinary(mapFile);

            try (InputStream in = Files.newInputStream(libRoot.resolve(MapFileUtil.FILENAME_BINARY))) {
                try (OutputStream out = Files.newOutputStream(maproot.resolve("map.dat"))) {
                    StreamUtils.copy(in, out);
                } catch (IOException ex) {
                    client.showMessage(new MessageParams(MessageType.Error, "Unable to write to map.dat: " + ex.getMessage()));
                }
            }
            try (InputStream in = Files.newInputStream(libRoot.resolve(MapFileUtil.FILENAME_INI))) {
                byte[] inibytes = StreamUtils.copyToByteArray(in);
                List<String> lines = MapFileUtil.decodeCif(inibytes);
                try {
                    Files.write(maproot.resolve("extracted.ini"), lines, StandardCharsets.ISO_8859_1);
                } catch (IOException ex) {
                    client.showMessage(new MessageParams(MessageType.Error, "Unable to write to extracted.ini: " + ex.getMessage()));
                }
            }

            client.showMessage(new MessageParams(MessageType.Info, "Map successfully extracted!"));
        } catch (FileNotFoundException ex) {
            client.showMessage(new MessageParams(MessageType.Error, "$maproot$\\map.c2m does not exist!"));
        } catch (IOException ex) {
            client.showMessage(new MessageParams(MessageType.Error, "Unable to read map.c2m: " + ex.getMessage()));
        }
    }

    public void setClient(LanguageClient client) {
        this.client = client;
    }
}
