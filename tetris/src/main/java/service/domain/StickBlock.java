package service.domain;

import java.util.List;

public class StickBlock extends ParentBlock implements Block{

	private int convertCount = 0;
	private int cloumns = 0;
	
	
	public StickBlock(int columns){
		this.cloumns = columns;
		
	}
	
	@Override
	public boolean createBlock() {
		block.add(new Node(0,1));
		block.add(new Node(0,2));
		block.add(new Node(0,3));
		block.add(new Node(0,4));
		
		return true;
	}
	
	@Override
	public List<Node> convertBlock() {
		
		block.clear();
		for(Node n : convertBlock )
			block.add(new Node(n.getX(), n.getY()));
		 
		return block ;
	
	}
	
	@Override
	public List<Node> priorConvertBlock() {
		convertBlock.clear();
		for(Node n : block )
			convertBlock.add(new Node(n.getX(), n.getY()));
		
		return convertBlock(convertBlock) ;
	}
	
	private List<Node> convertBlock(List<Node> blocks) {

		if(convertCount ==0)
			convertCount+=1;
		
		if(convertCount ==1) {
			blocks.get(1).setX(blocks.get(1).getX()+1);
			blocks.get(1).setY(blocks.get(1).getY()-1);
			blocks.get(2).setX(blocks.get(2).getX()+2);
			blocks.get(2).setY(blocks.get(2).getY()-2);
			blocks.get(3).setX(blocks.get(3).getX()+3);
			blocks.get(3).setY(blocks.get(3).getY()-3);
			convertCount++;
		}
		else if(convertCount ==2) {
			blocks.get(1).setX(blocks.get(1).getX()-1);
			blocks.get(1).setY(blocks.get(1).getY()+1);
			blocks.get(2).setX(blocks.get(2).getX()-2);
			blocks.get(2).setY(blocks.get(2).getY()+2);
			blocks.get(3).setX(blocks.get(3).getX()-3);
			blocks.get(3).setY(blocks.get(3).getY()+3);
			convertCount=1;
		}

		
		return blocks ;
	}
	
	//회전 실패시 convertCount 되돌림
	@Override
	public void reConvert() {
		if(convertCount == 2  )
			convertCount--;
		else
			convertCount=2;
	}
}
