package service.domain;

import java.util.ArrayList;
import java.util.List;

public class ParentBlock implements Block{

	protected  ArrayList<Node> block = new ArrayList<Node>();
	
	//돌렸을때 정합성 체크하기 위한 convert 블록
	protected ArrayList<Node> convertBlock = new ArrayList<Node>() ;
	
	
	public List<Node> getBlock() {
		// TODO Auto-generated method stub
		return block ;
	}

	
	//자식 클래스마다 Override로 생성해 사용
	public boolean createBlock() {
		
		return false;
	}

	public List<Node> convertBlock() {
		return block ;
	}

	
	public List<Node> downBlock() {
		for(Node n : block)
			n.setX(n.getX()+1);
		
		return block ;
	}

	
	public List<Node> leftMoveBlock() {
		convertBlock.clear();
		for(Node n : block )
			convertBlock.add(new Node(n.getX(), n.getY()));
		
		for(int i=0; i<block.size(); i++) {
			block.get(i).setY(block.get(i).getY()-1);
		}
		
		return block ;
	}

	
	public List<Node> rightMoveBlock() {
		convertBlock.clear();
		for(Node n : block )
			convertBlock.add(new Node(n.getX(), n.getY()));
		
		
			for(int i=0; i<block.size(); i++) 
				block.get(i).setY(block.get(i).getY()+1);
				
					
		return block ;
	}

	
	public void backupBlock() {
		block.clear();
		
		for(Node n : convertBlock )
			block.add(new Node(n.getX(), n.getY()));
		
	}

}
