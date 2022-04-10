package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import lombok.RequiredArgsConstructor;
import service.web.handler.ChatHandler;
//import service.web.WebSocket;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class Config implements WebSocketConfigurer{

	/* javax websocket를 사용할때 설정 코드
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
	  return new ServerEndpointExporter();
	}
	@Autowired
	public void setUserService(UserService userService) {
		WebSocket.userService = userService ;
	}
	*/
    private final ChatHandler chatHandler;

    
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    
        registry.addHandler(chatHandler, "websocket").setAllowedOrigins("*");
    }
	
}
