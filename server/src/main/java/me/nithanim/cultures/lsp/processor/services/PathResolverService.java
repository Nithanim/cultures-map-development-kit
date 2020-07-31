package me.nithanim.cultures.lsp.processor.services;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import java.nio.file.Path;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import me.nithanim.cultures.lsp.processor.util.Uri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@ReturnValuesAreNonnullByDefault
@ParametersAreNonnullByDefault
public class PathResolverService {
  @Autowired private WorkspaceService workspaceService;

  @Nullable
  public Uri resolvePartialPathToUri(SourceFile sf, String partialPath) {
    String rel = StringUtils.replace(partialPath, "\\", "/");
    Uri uri = replace(rel, "$maproot$", () -> workspaceService.getWorkspacePath());
    if (uri == null) {
      uri = replace(rel, "$gameroot$", this::getGameRoot);
      if (uri == null) {
        uri = replace(rel, "$local$", () -> sf.getUri().getPath());
      }
    }

    return uri;
  }

  @Nullable
  private Uri replace(String partialPath, String variable, Supplier<Path> replacement) {
    if (partialPath.startsWith(variable)) {
      String rel = partialPath.substring(variable.length() + 1);
      return Uri.of(replacement.get().resolve(rel));
    } else {
      return null;
    }
  }

  private Path getGameRoot() {
    return workspaceService.getWorkspacePath().getParent().getParent().getParent();
  }

  public SourceFile getEntry() {
    return new SourceFile(Uri.of(workspaceService.getWorkspacePath().resolve("map.ini")));
  }
}
