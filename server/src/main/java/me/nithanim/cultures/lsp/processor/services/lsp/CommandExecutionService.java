package me.nithanim.cultures.lsp.processor.services.lsp;

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
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.Setter;
import lombok.SneakyThrows;
import me.nithanim.cultures.format.newlib.io.writing.LibFileWriter;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniLine;
import me.nithanim.cultures.lsp.processor.model.DefinitionEnvironment;
import me.nithanim.cultures.lsp.processor.services.MapFileUtil;
import me.nithanim.cultures.lsp.processor.services.WorkspaceService;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.services.LanguageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
public class CommandExecutionService {
  private static final Logger logger = LoggerFactory.getLogger(CommandExecutionService.class);

  public static final String COMMAND_EXTRACT_MAP = "culures-ini.extract-c2m";
  public static final String COMMAND_PACKAGE = "cultures-ini.package-map-internal";
  public static final String COMMAND_PACKAGE_EXTERNAL = "cultures-ini.package-map-external";
  private static final Map<String, BiConsumer<CommandExecutionService, ExecuteCommandParams>>
      map;

  @Autowired private WorkspaceService workspaceService;
  @Autowired private DefinitionEnvironment definitionEnvironment;
  @Setter private LanguageClient client;

  static {
    Map<String, BiConsumer<CommandExecutionService, ExecuteCommandParams>> m = new HashMap<>();
    m.put(COMMAND_PACKAGE_EXTERNAL, CommandExecutionService::pack);
    m.put(COMMAND_EXTRACT_MAP, CommandExecutionService::extract);
    map = m;
  }

  public CompletableFuture<Object> execute(ExecuteCommandParams params) {
    BiConsumer<CommandExecutionService, ExecuteCommandParams> o = map.get(params.getCommand());
    try {
      o.accept(this, params);
      return CompletableFuture.completedFuture(null);
    } catch (Exception ex) {
      CompletableFuture<Object> future = new CompletableFuture<>();
      future.completeExceptionally(ex);
      return future;
    }
  }

  public List<String> getCommands() {
    return new ArrayList<>(map.keySet());
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
          client.showMessage(
              new MessageParams(
                  MessageType.Error, "Unable to write to map.dat: " + ex.getMessage()));
        }
      }
      try (InputStream in = Files.newInputStream(libRoot.resolve(MapFileUtil.FILENAME_INI))) {
        byte[] inibytes = StreamUtils.copyToByteArray(in);
        List<String> lines = MapFileUtil.decodeCif(inibytes);
        try {
          Files.write(maproot.resolve("extracted.ini"), lines, StandardCharsets.ISO_8859_1);
        } catch (IOException ex) {
          client.showMessage(
              new MessageParams(
                  MessageType.Error, "Unable to write to extracted.ini: " + ex.getMessage()));
        }
      }

      client.showMessage(new MessageParams(MessageType.Info, "Map successfully extracted!"));
    } catch (FileNotFoundException ex) {
      client.showMessage(
          new MessageParams(MessageType.Error, "$maproot$\\map.c2m does not exist!"));
    } catch (IOException ex) {
      client.showMessage(
          new MessageParams(MessageType.Error, "Unable to read map.c2m: " + ex.getMessage()));
    }
  }

  private void pack(ExecuteCommandParams params) {
    Path maproot = workspaceService.getWorkspacePath();
    Path targetFile = maproot.resolve("export.c2m");
    List<? extends CulturesIniLine> lines = definitionEnvironment.calculateAllLines();
    String mapIni =
        lines.stream().map(CulturesIniLine::printLine).collect(Collectors.joining("\r\n"));

    try (LibFileWriter lfw = new LibFileWriter()) {
      String prefix = "currentusermap\\";
      lfw.addFile(prefix + "map.ini", mapIni.getBytes(StandardCharsets.ISO_8859_1));
      lfw.addFile(prefix + "map.dat", maproot.resolve("map.dat"));

      Path textPath = maproot.resolve("text");
      Files.walk(textPath)
          .forEach(
              new Consumer<Path>() {
                @Override
                @SneakyThrows
                public void accept(Path p) {
                  if (Files.isRegularFile(p)) {
                    String internalPath = prefix + toInternalPath(maproot, p) + "\\";
                    lfw.addFile(internalPath, p);
                  }
                }
              });

      try (OutputStream out = Files.newOutputStream(targetFile)) {
        lfw.writeTo(out);
      }
      client.showMessage(
          new MessageParams(
              MessageType.Info,
              "Successfully exported map to external format! Put "
                  + targetFile
                  + " under <gameroot>\\usermaps\\"));
    } catch (Exception ex) {
      logger.error("Unable to export map to external format!", ex);
      client.showMessage(
          new MessageParams(MessageType.Error, "Unable to export map to external format!"));
    }
  }

  private String toInternalPath(Path root, Path file) {
    Path relative = root.relativize(file);
    return relative.toString().replace('/', '\\');
  }
}
