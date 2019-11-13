package me.nithanim.cultures.lsp.base.events;

import lombok.Value;
import org.springframework.context.ApplicationEvent;

@Value
public class ClientSourceFileOpenedEvent extends ApplicationEvent {
    String uri;
    String content;

    public ClientSourceFileOpenedEvent(Object source, String uri, String content) {
        super(source);
        this.uri = uri;
        this.content = content;
    }
}
