package service.domain;

import java.util.List;

public class UBlock extends ParentBlock implements Block{

	private int convertCount = 0;	
	private int cloumns = 0;
	
	
	public UBlock(int columns){
		this.cloumns = columns;
		
	}
	@Override
	public boolean createBlock() {
		block.add(new Node(1,3));
		block.add(new Node(0,2));
		block.add(new Node(0,3));
		block.add(new Node(0,4));
		
		return true;
	}
	

	@Override
	public List<Node> convertBlock() {
		
		if(convertCount ==0)
			convertCount+=1;
		
		//if(!convertBlock.isEmpty())
			convertBlock.clear();
		for(Node n : block )
			convertBlock.add(new Node(n.getX(), n.getY()));
		 
	
		if(convertCount ==1) {
			
			block.get(0).setX(block.get(0).getX()-1);
			block.get(0).setY(block.get(0).getY()-1);
			block.get(1).setX(block.get(1).getX()-1);
			block.get(1).setY(block.get(1).getY()+1);
			block.get(3).setX(block.get(3).getX()+1);
			block.get(3).setY(block.get(3).getY()-1);
			convertCount++;
		}
		else if(convertCount ==2) {
			block.get(0).setX(block.get(0).getX()-1);
			block.get(0).setY(block.get(0).getY()+1);
			block.get(1).setX(block.get(1).getX()+1);
			block.get(1).setY(block.get(1).getY()+1);
			block.get(3).setX(block.get(3).getX()-1);
			block.get(3).setY(block.get(3).getY()-1);
			convertCount++;
		}
		else if(convertCount ==3) {
			block.get(0).setX(block.get(0).getX()+1);
			block.get(0).setY(block.get(0).getY()+1);
			block.get(1).setX(block.get(1).getX()+1);
			block.get(1).setY(block.get(1).getY()-1);
			block.get(3).setX(block.get(3).getX()-1);
			block.get(3).setY(block.get(3).getY()+1);
			convertCount++;
		}
		else if(convertCount ==4) {
			block.get(0).setX(block.get(0).getX()+1);
			block.get(0).setY(block.get(0).getY()-1);
			block.get(1).setX(block.get(1).getX()-1);
			block.get(1).setY(block.get(1).getY()-1);
			block.get(3).setX(block.get(3).getX()+1);
			block.get(3).setY(block.get(3).getY()+1);
			convertCount=1;
		}
		
		
		return block ;
	
		
	}
	

}
