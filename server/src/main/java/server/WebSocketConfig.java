package server;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // this is where clients will connect
        registry.addEndpoint("/websocket");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // this way, the messages sent to something starting with "/topic" go to subscribed clients
        config.enableSimpleBroker("/topic");
    }
}