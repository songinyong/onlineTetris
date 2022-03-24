package service.web;

import java.util.ArrayList;
import java.util.HashMap;

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

import service.domain.Room;

@Component
@ServerEndpoint("/websocket")
public class WebSocket {

	public WebSocket() {};
    /**
     * 서버에 접속한 웹소캣 목록 저장
     */
    private static ArrayList<Session> sessionList = new ArrayList<Session>();
    
    private static ArrayList<Room> roomList = new ArrayList<Room>();
    /**
     * 서버에 접속한 웹소캣별 게이밍 진행상태 저장
     */
    
    //멀티모드 방 테스트
    //private static ArrayList<HashMap<Session, Gaming>> gamgingList = new ArrayList<HashMap<Session, Gaming>>();
    
    //세션이 들어간 방 정보
    private static HashMap<Session, Integer> userRoomInfo = new HashMap<Session, Integer>();
    

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
            //빈방 있는지 검색후 방이 있을시 그 방에 들어감
            for(Room r : roomList) {
            	if(r.userAllJoin()) ;
            	else {
            		r.userJoin(session);
            		userRoomInfo.put(session, r.getRoomId());
            	}
            	
            }
            
            //방이 없을시 새로운 방을 만듬
            roomList.add(new Room());
            int roomId = roomList.size()-1;
            //방에 아이디 부여
            roomList.get(roomId).setRoomId(roomId);
            //방에 들어감
            roomList.get(roomId).userJoin(session);
            //user가 들어간 방 정보 등록
            userRoomInfo.put(session, roomId);
            
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
            Integer romid = userRoomInfo.get(session);
            
            if(message.equals("start")) {
            	roomList.get(romid).userclickStart(session);
            	
            	//시작 안했으면 시작
            	if(!roomList.get(romid).getGaming().getStartCheck() && roomList.get(romid).userAllReStart())
            		
            		roomList.get(romid).getGaming().gameStart();
            	else {
            		
            		//재시작할때
            		if(roomList.get(romid).userAllReStart()) {
            			roomList.get(romid).setGaming(new Gaming());
            			roomList.get(romid).getGaming().gameStart();
            		}
            	}
            }
            else if(message.equals("left") && roomList.get(romid).getGaming().getMakedBlock() ) {
	            if(roomList.get(romid).getGaming().getStartCheck()) {
	            	roomList.get(romid).getGaming().leftMove();
	            	session.getAsyncRemote().sendText(makeJson(session));
	            }
            }
            else if(message.equals("right") && roomList.get(romid).getGaming().getMakedBlock() ) {
            	if(roomList.get(romid).getGaming().getStartCheck()) {
            		roomList.get(romid).getGaming().rightMove();
            		session.getAsyncRemote().sendText(makeJson(session));
            	}
            }
            else if(message.equals("convert") && roomList.get(romid).getGaming().getMakedBlock() ) {
            	if(roomList.get(romid).getGaming().getStartCheck())  {        	
            		roomList.get(romid).getGaming().convertBlock();
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
            
            //세션리스트에서 제거
            sessionList.remove(session);
            int roomId = userRoomInfo.get(session);
            //방에서 제거
            roomList.get(roomId).userOut(session);

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
			Integer romid = userRoomInfo.get(sessionList.get(k));
			try {
				
				//게임을 시작한 객체일시 gaming 연산(블럭 다운, 줄 체크 등) 시작
			if(roomList.get(romid).getGaming().getStartCheck()) {
				//게임 엔드일경우
				
				//처음으로 게이밍 체크하는 경우
				if(!roomList.get(romid).isGamingCheck()) {
					//게이밍 연산 실행
					roomList.get(romid).getGaming().gaming();
					//게이밍 정보 전달
					sessionList.get(k).getAsyncRemote().sendText(makeJson(sessionList.get(k)));
					
					roomList.get(romid).setGamingCheck(true);
					
				}
				else {
					//현재 진행중인 게임판 데이터 전달
					sessionList.get(k).getAsyncRemote().sendText(makeJson(sessionList.get(k)));
					roomList.get(romid).setGamingCheck(false);
				}
	
		} else {
			//게임 시작 안한 객체는 바로 패스
		  }
			} //중간에 로그아웃으로 nullPoint exception이 발생할 수 있음
			catch (NullPointerException e) {
				
			}
			
		}
		
		//추가해야할것
		// 게임 스탯 정보 ready,gaming 두개로 구분 
		
		
	}
	
	//현재 게임 상태 객체로 만들어서 반환
	public String makeJson(Session session) {
		
		Integer romid = userRoomInfo.get(session);
		
		HashMap curHash = roomList.get(romid).getGaming().getHash();
		curHash.put("block", roomList.get(romid).getGaming().getBlockLoc() );
		curHash.put("score", roomList.get(romid).getGaming().getScore() );
		curHash.put("numOfUser", sessionList.size() );
		curHash.put("userId", session.getId() );
		curHash.put("nextBlock", roomList.get(romid).getGaming().getNextBlock() );
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(curHash);
			return json ;
		} catch (JsonProcessingException e) {
			
			return "";
		}
	}
	
	
	}	

