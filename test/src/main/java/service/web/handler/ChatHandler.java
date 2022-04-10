package service.web.handler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import service.domain.Room;
import service.web.Gaming;

@Component
public class ChatHandler extends TextWebSocketHandler  {

	private static List<WebSocketSession> sessionList = new ArrayList<>();
	

    
    //세션이 들어간 방 정보
    private static HashMap<WebSocketSession, Integer> userRoomInfo = new HashMap<WebSocketSession, Integer>();
	
	/*client가 서버에게 메시지 보냄*/
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        
        for(WebSocketSession sess: sessionList) {
            sess.sendMessage(message);
        }
       

    }

    /* Client가 접속 시 호출되는 메서드 */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        for(WebSocketSession sess: sessionList) {
            sess.sendMessage(new TextMessage("유저 들어옴"));
        }
       
    }

    /* Client가 접속 해제 시 호출되는 메서드드 */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    	
        for(WebSocketSession sess: sessionList) {
            sess.sendMessage(new TextMessage("유저 나감"));
        }
    }
    
    
}