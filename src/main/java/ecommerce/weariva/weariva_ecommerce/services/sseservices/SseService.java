package ecommerce.weariva.weariva_ecommerce.services.sseservices;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final List<SseEmitter> notificationEmitters = new CopyOnWriteArrayList<>();

    public SseEmitter createEmitter(String sessionId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.put(sessionId, emitter);

        emitter.onCompletion(() -> {
            emitters.remove(sessionId);
        });
        emitter.onTimeout(() -> {
            emitters.remove(sessionId);
        });
        return emitter;
    }

    public SseEmitter createNotificationEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        notificationEmitters.add(emitter);

        emitter.onCompletion(() -> {
            notificationEmitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            notificationEmitters.remove(emitter);
        });
        emitter.onError(e -> {
            notificationEmitters.remove(emitter);
        });
        return emitter;
    }

    public void sendEmitterEvent(String sessionId, String name, String message) {
        SseEmitter emitter = emitters.get(sessionId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().id(sessionId).name(name).data(message).build());
            } catch (Exception e) {
                emitter.completeWithError(e);
                emitters.remove(sessionId);
            }
        }
    }

    public void sendNewOrderNotification(String message) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        notificationEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("order")
                        .data(message));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            } catch (Exception ex) {
                System.out.println("SSE client disconnected or closed.");
                deadEmitters.add(emitter);
            }
        });
        notificationEmitters.removeAll(deadEmitters);
    }

    @Scheduled(fixedRate = 10000) // every 10 seconds
    public void sendKeepAlive() {
        notificationEmitters.forEach(emitter -> {
            System.out.println("Running... Keep Live Emitter.");
            try {
                emitter.send(SseEmitter.event().name("ping").data("keep-alive"));
            } catch (IOException e) {
                notificationEmitters.remove(emitter);
            } catch (Exception ex) {
                System.out.println("Connection closed.");
            }
        });
    }

}