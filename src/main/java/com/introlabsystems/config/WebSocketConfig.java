package com.introlabsystems.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //configuring a message broker that will be used to route messages from one client to another
//        messages whose destination starts with “/topic” should be routed to the message broker
//        Message broker broadcasts messages to all the connected clients who are subscribed to a particular channel.
        config.enableSimpleBroker("/topic");

        //messages whose destination starts with “/app” should be routed to message-handling methods   "/app/hello"
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override// we register a websocket endpoint that the clients will use to connect to our websocket server
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket").withSockJS();
    }

}
