package service.web;

import java.util.ArrayList;
import java.util.HashMap;
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
    
    /**
     * 서버에 접속한 웹소캣별 게이밍 진행상태 저장
     */
    
    //후에 방게임 확장성을 위해 테스트용으로 남김
    private static ArrayList<HashMap<Session, Gaming>> gamgingList = new ArrayList<HashMap<Session, Gaming>>();
    
    //현재 진행중인 솔로모드용 게이밍 리스트
    private static HashMap<Session, Gaming> soloMap = new HashMap<Session, Gaming>();
    /**
     * 웹소켓 사용자 연결 성립하는 경우 호출
     */
    @OnOpen
    public void handleOpen(Session session) {
        if (session != null) {
            String sessionId = session.getId();
            
            System.out.println("client is connected. sessionId == [" + sessionId + "]");
            System.out.println("새로운 인원 들어옴 현재 접속자수"+ sessionList.size());
            sessionList.add(session);
            soloMap.put(session, new Gaming() );
         
            
           /* HashMap<Session, Gaming> map = new HashMap<Session, Gaming>();
              map.put(session, new Gaming());
              gamgingList.add(map);
                
            for(int i = 0; i < gamgingList.size(); i++){
                //arraylist 사이즈 만큼 for문을 실행합니다.
                System.out.println("list 순서 " + i + "번쨰");
                for( Entry<Session, Gaming> elem : gamgingList.get(i).entrySet() ){
                    // list 각각 hashmap받아서 출력합니다.
                    System.out.println( String.format("키 : %s, 값 : %s", elem.getKey(), elem.getValue().getStartCheck()) );
                }
            }
            */

        
            // 웹소켓 연결 성립되어 있는 모든 사용자에게 메시지 전송
            //sendMessageToAll("“***** [USER-" + sessionId + "] is connected. *****");
            
        }
       
    }
    

    /**
     * 웹소켓 메시지(From Client) 수신하는 경우 호출
     */
    @OnMessage
    public String handleMessage(String message, Session session) {
        if (session != null) {
            String sessionId = session.getId();
            System.out.println("message is arrived. sessionId == [" + sessionId + "] / message == [" + message + "]");
            System.out.println("message is arrived. sessionId == [" + session + "] / message == [" + message + "]");
            
            if(message.equals("start")) {
            	System.out.println("  "+message);
            soloMap.get(session).gameStart();
            }
            else if(message.equals("left")) {
	            if(soloMap.get(session).getStartCheck())
	            	soloMap.get(session).leftMove();
            }
            else if(message.equals("right")) {
            	if(soloMap.get(session).getStartCheck())	
            		soloMap.get(session).rightMove();
            }
            else if(message.equals("convert")) {
            	if(soloMap.get(session).getStartCheck())          	
            		soloMap.get(session).convertBlock();
            }
            else {

            }
            // 웹소켓 연결 성립되어 있는 모든 사용자에게 메시지 전송
            //sendMessageToAll("[USER-" + sessionId + "] " + message);
           
            
        }

        return null;
    }
    

    /**
     * 웹소켓 사용자 연결 해제하는 경우 호출
     */
    @OnClose
    public void handleClose(Session session) {
        if (session != null) {
            String sessionId = session.getId();
            System.out.println("client is disconnected. sessionId == [" + sessionId + "]");
            
            //사람이 나가면 진행중인 게임 종료후 세션 리스트에서 제거
            soloMap.get(session).endGame();
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
	private String gameCheck() throws JsonProcessingException{
		
		for(int k=0; k<sessionList.size(); k++) {
			if(soloMap.get(sessionList.get(k)).getStartCheck()) {
			
		if(!soloMap.get(sessionList.get(k)).gaming()) {
			for(int i=0; i<10; i++) {
				for(int j=0; j<6; j++) {
					if(soloMap.get(sessionList.get(k)).getHash().get(i*6 +j)) {
						System.out.print("*");
					}
					else {
						System.out.print(" ");
					}
				}
				System.out.println();
			}
			System.out.println("게임끝");

			return "";
		}
		else {
			//현재 진행중인 게임판 데이터 전달
			HashMap curHash = soloMap.get(sessionList.get(k)).getHash();
			curHash.put("block", soloMap.get(sessionList.get(k)).getBlockLoc() );
			curHash.put("score", soloMap.get(sessionList.get(k)).getScore() );
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(curHash);
			System.out.println(json);

			sessionList.get(k).getAsyncRemote().sendText(json);
			//gameing.leftMove();
			
			for(Node i: soloMap.get(sessionList.get(k)).getBlockInfo().getBlock()) {
				
				
				System.out.println("블록위치" + (i.getX()*soloMap.get(sessionList.get(k)).getColumns() + i.getY()));
			}
			
			for(int i=0; i<10; i++) {
				for(int j=0; j<6; j++) {
					if(soloMap.get(sessionList.get(k)).getHash().get(i*6 +j)) {
						System.out.print("*");
					}
					else {
						System.out.print(" ");
					}
				}
				System.out.println();
			}
			
			//rest컨트롤러 별개로 만들것
			//스코어 점수 파라미터에 추가
			
			//블록판 상태 갱신 
		}

		return "" ;
		
		} else {
			System.out.println("시작안함 패스");
			//게이밍 시작안한곳은 패스
		}
			
		}

		return null;

		
	}
	}	

