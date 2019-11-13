package me.nithanim.cultures.lsp.processor.services;

import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class WorkspaceService {
    @Getter
    private String workspaceUri;
    @Getter
    private Path workspacePath;

    @SneakyThrows
    public boolean isPartOfWorkspace(String uri) {
        if (workspaceUri == null) {
            return false;
        } else {
            Path path = Paths.get(new URI(uri));
            return path.startsWith(workspacePath);
        }
    }

    @SneakyThrows
    public void init(String uri) {
        workspaceUri = uri;
        workspacePath = Paths.get(new URI(uri));
    }
}

