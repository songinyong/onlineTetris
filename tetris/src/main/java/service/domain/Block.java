/*Block임*/

package service.domain;

import java.util.List;

public interface Block {

	//block 상태 알려줌
	List<Node> getBlock() ;
	
	boolean createBlock();
	
	List<Node> convertBlock();
	
	List<Node> downBlock();
	
	List<Node> leftMoveBlock();
	
	List<Node> rightMoveBlock();
	
	 void backupBlock() ;
}
