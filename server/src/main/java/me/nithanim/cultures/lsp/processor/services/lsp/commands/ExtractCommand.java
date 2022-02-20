package me.nithanim.cultures.lsp.processor.services.lsp.commands;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.nithanim.cultures.lsp.processor.services.MapFileUtil;
import me.nithanim.cultures.lsp.processor.services.WorkspaceService;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.services.LanguageClient;

@Slf4j
@RequiredArgsConstructor
public class ExtractCommand {
  private final LanguageClient client;
  private final WorkspaceService workspaceService;

  public void extract(ExecuteCommandParams p) {
    try {
      Path maproot = workspaceService.getWorkspacePath();
      Path mapFile = maproot.resolve("map.c2m");
      Path libRoot;
      try {
        libRoot = MapFileUtil.extractMapBinary(mapFile);
      } catch (FileNotFoundException ex) {
        throw new IOException("$maproot$\\map.c2m does not exist!", ex);
      } catch (IOException ex) {
        throw new IOException("Unable to read c2m file", ex);
      }

      extractMapDat(maproot, libRoot.resolve(MapFileUtil.FILENAME_BINARY));
      extractMapIni(maproot, libRoot);

      client.showMessage(new MessageParams(MessageType.Info, "Map successfully extracted!"));
    } catch (IOException ex) {
      log.error("Unable to extract map.c2m", ex);
      client.showMessage(
          new MessageParams(MessageType.Error, "Unable to extract map.c2m: " + ex.getMessage()));
    }
  }

  private void extractMapIni(Path maproot, Path libRoot) throws IOException {
    List<String> mapIniLines;
    try {
      byte[] iniBytes = Files.readAllBytes(libRoot.resolve(MapFileUtil.FILENAME_INI));
      mapIniLines = MapFileUtil.decodeCif(iniBytes);
    } catch (IOException ex) {
      throw new IOException("Unable to read map.ini from c2m");
    }
    try {
      Files.write(maproot.resolve("extracted.ini"), mapIniLines, StandardCharsets.ISO_8859_1);
    } catch (IOException ex) {
      throw new IOException("Unable to write extracted.ini");
    }
  }

  private void extractMapDat(Path maproot, Path libRoot) throws IOException {
    try (InputStream in = Files.newInputStream(libRoot);
        OutputStream out = Files.newOutputStream(maproot.resolve("map.dat"))) {
      in.transferTo(out);
    } catch (IOException ex) {
      throw new IOException("Unable to extract map.dat");
    }
  }
}
