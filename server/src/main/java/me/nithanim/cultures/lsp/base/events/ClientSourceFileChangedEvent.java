package me.nithanim.cultures.lsp.base.events;

import lombok.Value;
import org.springframework.context.ApplicationEvent;

@Value
public class ClientSourceFileChangedEvent extends ApplicationEvent {
    String uri;
    String content;

    public ClientSourceFileChangedEvent(Object source, String uri, String content) {
        super(source);
        this.uri = uri;
        this.content = content;
    }
}
