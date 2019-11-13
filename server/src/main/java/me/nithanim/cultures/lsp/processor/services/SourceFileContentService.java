package me.nithanim.cultures.lsp.processor.services;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import me.nithanim.cultures.lsp.processor.util.SourceFile;
import me.nithanim.cultures.lsp.base.events.ClientSourceFileChangedEvent;
import me.nithanim.cultures.lsp.base.events.ClientSourceFileClosedEvent;
import me.nithanim.cultures.lsp.base.events.ClientSourceFileOpenedEvent;
import me.nithanim.cultures.lsp.base.events.ClientSourceFileSavedEvent;
import me.nithanim.cultures.lsp.processor.events.SourceFileChangedEvent;
import me.nithanim.cultures.lsp.processor.util.Uri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
@ReturnValuesAreNonnullByDefault
@ParametersAreNonnullByDefault
public class SourceFileContentService {
    private final Map<SourceFile, String> openedFileContent = new HashMap<>();

    @Autowired
    private ApplicationEventPublisher publisher;


    public String getContent(SourceFile sourceFile) throws IOException {
        String content = openedFileContent.get(sourceFile);
        if(content == null) {
            byte[] bytes = Files.readAllBytes(Paths.get(sourceFile.getUri().toOldUri()));
            content = new String(bytes, StandardCharsets.ISO_8859_1);
        }
        return content;
    }

    @EventListener
    public void onFileOpened(ClientSourceFileOpenedEvent evt) {
        SourceFile sf = new SourceFile(Uri.of(evt.getUri()));
        openedFileContent.put(sf, evt.getContent());
    }

    @EventListener
    public void onFileChanged(ClientSourceFileChangedEvent evt) {
        SourceFile sf = new SourceFile(Uri.of(evt.getUri()));
        openedFileContent.put(sf, evt.getContent());
        publisher.publishEvent(new SourceFileChangedEvent(this, sf));
    }

    @EventListener
    public void onFileClosed(ClientSourceFileClosedEvent evt) {
        SourceFile sf = new SourceFile(Uri.of(evt.getUri()));
        openedFileContent.remove(sf);
    }

    @EventListener
    public void onFileSaved(ClientSourceFileSavedEvent evt) {
        SourceFile sf = new SourceFile(Uri.of(evt.getUri()));
        openedFileContent.put(sf, evt.getContent());
        publisher.publishEvent(new SourceFileChangedEvent(this, sf));
    }
}
