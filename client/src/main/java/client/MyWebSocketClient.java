package client;

import javafx.application.Platform;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.function.Consumer;

public class MyWebSocketClient {
    private StompSession session;

    /**
     * connect to ws://localhost:8080/websocket
     * @param url the url
     */
    public void connect(String url) {
        var client = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());

        try {
            this.session = stomp.connectAsync(url, new WebSocketHttpHeaders(),
                    new StompSessionHandlerAdapter() {}).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * aa
     * @param destination a
     * @param type a
     * @param consumer a
     * @return a
     * @param <T> a
     */
    public <T> StompSession.Subscription subscribe(String destination, Class<T> type,
                                               Consumer<T> consumer) {
        session.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) { return type; }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                Platform.runLater(() -> consumer.accept((T) payload));
            }
        });
    }
}
