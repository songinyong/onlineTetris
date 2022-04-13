package service.webservice;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import service.domain.jpa.User;
import service.domain.jpa.UserRepository;
import service.domain.jpa.log.ConnLog;
import service.domain.jpa.log.ConnLogRepository;
import service.domain.redis.RedisUser;
import service.domain.redis.RedisUserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	public UserRepository userRepo ;
	
	@Autowired
	public ConnLogRepository connRepo;
	
	@Autowired
	public RedisUserRepository redisUserRepo;
	
	public void addUser(WebSocketSession session) {
        userRepo.save(User.builder()
        		.session(session.toString())
        		.userid(session.getId())
        		.build());
	}
	
	public void addConnLog(WebSocketSession session) {
		
		String ip = (String) session.getAttributes().get("ip") ;
		connRepo.save(ConnLog.builder()
				.session(session.toString())
				.ipAddress(ip)
				.build());
		
		//redis 저장 테스트
		redisUserRepo.save(RedisUser.builder()
				.session(session.toString())
				.userid(session.getId())
				.build());
	
		Optional<RedisUser> findEntity = redisUserRepo.findById(session.toString());
		
	}
	
	
}
