package me.nithanim.cultures.lsp.base.events;

import lombok.Value;
import org.springframework.context.ApplicationEvent;

@Value
public class ClientSourceFileClosedEvent extends ApplicationEvent {
    String uri;

    public ClientSourceFileClosedEvent(Object source, String uri) {
        super(source);
        this.uri = uri;
    }
}
