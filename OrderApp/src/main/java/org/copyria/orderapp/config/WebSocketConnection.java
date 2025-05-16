package org.copyria.orderapp.config;

import lombok.RequiredArgsConstructor;
import org.copyria.orderapp.services.OrderService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WebSocketConnection extends TextWebSocketHandler {
    private final Map<String, Set<WebSocketSession>> viewCounts = new HashMap<>();
    private Set<WebSocketSession> usersView = new HashSet<>();
    private final OrderService orderService;
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String path = session.getUri().getPath();
        String orderId = path.substring(path.lastIndexOf('/') + 1);
        usersView.add(session);
        viewCounts.put(orderId,usersView);
        orderService.updateViewCount(Long.valueOf(orderId), viewCounts.get(orderId).size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String path = session.getUri().getPath();
        String orderId = path.substring(path.lastIndexOf('/') + 1);
        usersView.remove(session);
        viewCounts.put(orderId,usersView);
        orderService.updateViewCount(Long.valueOf(orderId), viewCounts.get(orderId).size());
    }
}
