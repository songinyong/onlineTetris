# onlineTetris

웹 기반 테트리스 구현

**💡개발동기**
웹게임 프로토타입으로써 친숙하고 직관적인 테트리스를 웹기반으로 구현해보기로 하였습니다.

번거로운 절차없이 접속 즉시 다양한 사람들과 플레이할 수 있습니다.

**🛠️개발환경**

스프링 부트 2.6.4, JQuery


webSocket 기반으로 만들고 있습니다.
모드 상관없이 세션을 아이디로 서버에서 현재 진행중인 게임 상태를 받아와 화면에 출력합니다.
현재는 기능 구현에 집중하고 있어 프론트 화면은 서버 렌더링 작업없이 JQuery로 간단하게 구현해둔 상태입니다.


**soloMode**


  

**협력모드**

![협력모드 설명창0](https://user-images.githubusercontent.com/30370933/160237915-d7e9fcdc-cd82-4889-bdda-9f93ba9cf34b.PNG)

![협력모드 설명창](https://user-images.githubusercontent.com/30370933/160237920-5f99d703-53a8-49c2-bf20-4f149df27de0.PNG)


테스트용 플레이 링크:
http://3.35.55.132:8081/


개발이력:
- 웹게임 프로토타입으로 javax.websocket과 Jquery을 이용해 테트리스 솔로모드, 협력모드 구현(22.03)


**현재 javax.websocket대신 스프링에서 지원하는 웹소켓을 이용하여 SockJs, STOMP, RabbitMq를 이용해
  웹게임을 동작되도록 수정중입니다.**
