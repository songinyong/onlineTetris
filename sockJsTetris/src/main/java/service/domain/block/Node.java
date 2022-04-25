//위치 정보를 담는 블록
package service.domain.block;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Node implements Serializable {
	 private static final long serialVersionUID = 2327692830319429806L;
	
	private int x ;
	private int y ;
}
