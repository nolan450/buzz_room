package com.buzzroom.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class SseBroadcaster {

    private final Set<SseEmitter> emitters = new CopyOnWriteArraySet<>();

    public SseEmitter register() {
        SseEmitter emitter = new SseEmitter(0L); // pas de timeout
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }

    public void send(String event) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(event);
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
