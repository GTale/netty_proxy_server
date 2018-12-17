package com.phantom.netty.client.listener;

import org.springframework.context.ApplicationEvent;

public class CloseEvent extends ApplicationEvent {

    public CloseEvent(Object source) {
        super(source);
    }
}
