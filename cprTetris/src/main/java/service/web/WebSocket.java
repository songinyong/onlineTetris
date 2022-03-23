package service.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import service.domain.Node;

@Component
@ServerEndpoint("/websocket")
public class WebSocket {

	public WebSocket() {};
    /**
     * 서버에 접속한 웹소캣 목록 저장
     */
    private static ArrayList<Session> sessionList = new ArrayList<Session>();
    
    private static ArrayList<Integer> roomList = new ArrayList<Integer>();
    /**
     * 서버에 접속한 웹소캣별 게이밍 진행상태 저장
     */
    
    //멀티모드 방 테스트
    //private static ArrayList<HashMap<Session, Gaming>> gamgingList = new ArrayList<HashMap<Session, Gaming>>();
    
    //세션이 들어간 방 정보
    private static HashMap<Session, Integer> userRoomInfo = new HashMap<Session, Integer>();
    
    //방 리스트
    private static HashMap<Integer, Gaming> roomInfo = new HashMap<Integer, Gaming>();
    //현재 진행중인 솔로모드용 게이밍 리스트
    //private static HashMap<Session, Gaming> soloMap = new HashMap<Session, Gaming>();
    /**
     * 웹소켓 사용자 연결 성립하는 경우 호출
     */
    @OnOpen
    public void handleOpen(Session session) {
        if (session != null) {
            String sessionId = session.getId();

            
            sessionList.add(session);
            roomList.add(0);
            userRoomInfo.put(session, 0 );
            
            if(roomInfo.isEmpty()) {
            	roomInfo.put(0, new Gaming());
            }
         
           /* HashMap<Session, Gaming> map = new HashMap<Session, Gaming>();
              map.put(session, new Gaming());
              gamgingList.add(map);
                
            for(int i = 0; i < gamgingList.size(); i++){
                //arraylist �궗�씠利� 留뚰겮 for臾몄쓣 �떎�뻾�빀�땲�떎.
                System.out.println("list �닚�꽌 " + i + "踰덉��");
                for( Entry<Session, Gaming> elem : gamgingList.get(i).entrySet() ){
                    // list 媛곴컖 hashmap諛쏆븘�꽌 異쒕젰�빀�땲�떎.
                    System.out.println( String.format("�궎 : %s, 媛� : %s", elem.getKey(), elem.getValue().getStartCheck()) );
                }
            }
            */

        }
       
    }
    
    /**
     * 웹소켓 메시지(From Client) 수신하는 경우 호출
     */
    @OnMessage
    public void handleMessage(String message, Session session) {

        if (session != null) {
            String sessionId = session.getId();
           // System.out.println("message is arrived. sessionId == [" + sessionId + "] / message == [" + message + "]");
            //System.out.println("message is arrived. sessionId == [" + session + "] / message == [" + message + "]");
            
            if(message.equals("start")) {
            	//처음 시작할때
            	if(!roomInfo.get(userRoomInfo.get(session)).getStartCheck())
            		
            		roomInfo.get(userRoomInfo.get(session)).gameStart();
            	else {
            		
            		//재시작할때
            		roomInfo.put(userRoomInfo.get(session), new Gaming() );
            		roomInfo.get(userRoomInfo.get(session)).gameStart();
            	}
            }
            else if(message.equals("left") && roomInfo.get(userRoomInfo.get(session)).getMakedBlock() ) {
	            if(roomInfo.get(userRoomInfo.get(session)).getStartCheck()) {
	            	roomInfo.get(userRoomInfo.get(session)).leftMove();
	            	session.getAsyncRemote().sendText(makeJson(session));
	            }
            }
            else if(message.equals("right") && roomInfo.get(userRoomInfo.get(session)).getMakedBlock() ) {
            	if(roomInfo.get(userRoomInfo.get(session)).getStartCheck()) {
            		roomInfo.get(userRoomInfo.get(session)).rightMove();
            		session.getAsyncRemote().sendText(makeJson(session));
            	}
            }
            else if(message.equals("convert") && roomInfo.get(userRoomInfo.get(session)).getMakedBlock() ) {
            	if(roomInfo.get(userRoomInfo.get(session)).getStartCheck())  {        	
            		roomInfo.get(userRoomInfo.get(session)).convertBlock();
            		session.getAsyncRemote().sendText(makeJson(session));
            	}
            }
            else {

            }     
            
        }

       
    }
    

    /**
     * 웹소켓 사용자 연결 해제하는 경우 호출
     */
    @OnClose
    public void handleClose(Session session) {
        if (session != null) {
            String sessionId = session.getId();
            //System.out.println("client is disconnected. sessionId == [" + sessionId + "]");
            
            //사람이 나가면 진행중인 게임 종료후 세션 리스트에서 제거
            //roomInfo.get(0).endGame();
            sessionList.remove(session);

        }
    }

    
    /**
     * 웹소켓 에러 발생하는 경우 호출
     */
    @OnError
    public void handleError(Throwable t) {
        t.printStackTrace();
    }
    
    
    /**
     * 웹소켓 연결된 모든 게임 데이터에 1초마다 블럭 위치 계산
     */
    /*플레이어 화면에 데이터 전달*/
    private boolean sendMessageToAll(String message) {
        if (sessionList == null) {
            return false;
        }

        int sessionCount = sessionList.size();
        if (sessionCount < 1) {
            return false;
        }

        Session singleSession = null;

        for (int i = 0; i < sessionCount; i++) {
            singleSession = sessionList.get(i);
            if (singleSession == null) {
                continue;
            }

            if (!singleSession.isOpen()) {
                continue;
            }

            sessionList.get(i).getAsyncRemote().sendText(message);
        }

        return true;
    }
    
      
  //스케줄러로 1초마다 gaming 및 블록 렌딩을 호출하여 게임상태 점검
	@Scheduled(cron = "0/1 * * * * ?")
	private void gameCheck() throws JsonProcessingException{
		
		for(int k=0; k<sessionList.size(); k++) {
			Integer romInfo = userRoomInfo.get(sessionList.get(k));
			try {
			if(roomInfo.get(romInfo).getStartCheck()) {
		//게임 엔드일경우
		if(!roomInfo.get(romInfo).gaming()) {
			
			//게임 엔드일 경우 패스
		}
		else {
			//현재 진행중인 게임판 데이터 전달
			HashMap curHash = roomInfo.get(romInfo).getHash();
			curHash.put("block", roomInfo.get(romInfo).getBlockLoc() );
			curHash.put("score", roomInfo.get(romInfo).getScore() );
			curHash.put("numOfUser", sessionList.size() );
			curHash.put("userId", sessionList.get(k).getId() );
			curHash.put("nextBlock", roomInfo.get(romInfo).getNextBlock() );
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(curHash);

			sessionList.get(k).getAsyncRemote().sendText(json);

		}
	
		} else {
			
		//게임 시작 안한 객체는 바로 패스
		}
			} //중간에 로그아웃으로 nullPoint exception이 발생할 수 있음
			catch (NullPointerException e) {
				
			}
			
		}
		
	}
	
	//현재 게임 상태 객체로 만들어서 반환
	public String makeJson(Session session) {
		HashMap curHash = roomInfo.get(0).getHash();
		curHash.put("block", roomInfo.get(0).getBlockLoc() );
		curHash.put("score", roomInfo.get(0).getScore() );
		curHash.put("numOfUser", sessionList.size() );
		curHash.put("userId", session.getId() );
		curHash.put("nextBlock", roomInfo.get(0).getNextBlock() );
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(curHash);
			return json ;
		} catch (JsonProcessingException e) {
			
			return "";
		}
	}
	
	
	}	

