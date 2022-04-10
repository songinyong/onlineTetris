package service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
//JPA Auditing 기능 활성화하여 CreateTime, ModifiedTime를 관리하도록 위임 

public class TetrisApplication  {

	public static void main(String[] args) {
		SpringApplication.run(TetrisApplication.class, args);
		//socketConn();
	}
	

}
