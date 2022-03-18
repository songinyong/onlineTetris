//실제 게이밍 구현
//소켓 관리 부분에서 게이밍 객체를 session 아이디와 함께 리스트에 저장하도록함

package service.web;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import service.domain.Block;
import service.domain.Node;
import service.domain.SquareBlock;
import service.domain.UBlock;



public class Gaming {
	
	
	Random random = new Random() ;
	
	private Block laningBlock ;
	
	//게임판으로 사용됨
	private  HashMap<Integer, Boolean> hash = new HashMap<Integer, Boolean>();
	

	
	//row 수
	private final int rows = 10;
	
	//columns 수
	private final int columns = 6;
	
	//점수
	private  int score = 0;
	
	//websocket에서 게임이 시작여부를 체크
	private boolean startCheck = false ;
	

	public Block getBlockInfo() {
		return laningBlock; 
	}
	
	public HashMap<Integer, Boolean> getHash() {
		return hash;
	}
	
	public int getColumns() {
		return columns;
	}
	
	public boolean getStartCheck() {
		return startCheck;
	}
	
	public int getScore() {
		return score;
	}
	
	public boolean gameStart() {
	
		//점수판으로 사용되는 hashMap 초기화
		for(int i =0; i<rows; i++) {
			for(int j=0;  j<columns; j++) {
			
				hash.put(i*columns+j, false);
			}
		}
		
		//게임판 초기화후 블록 생성
		makeBlock();
		startCheck = true;
		
		return true ;
	}
	
	private boolean makeBlock() {
		
		int rand =random.nextInt(2);
		
		
		
		try {
		if(rand ==0) {
			laningBlock = new UBlock(columns) ;
			laningBlock.createBlock();
		}
		else if(rand ==1) {
			laningBlock  = new SquareBlock(columns);
			laningBlock.createBlock();
		}
		
			return true ;
		}
		catch (Exception e) {
			return false;
		}
		
	}
	
	//블록 보드판에 착륙되었는지 확인 착륙시 true , 내려가는중이면 false
	private boolean checkBoard(Block block) {
		
		for(int i=0; i<block.getBlock().size(); i++) {
			//블록이 마지막 줄에 닿았을때
			
			if(block.getBlock().get(i).getX()*columns + block.getBlock().get(i).getY() > (rows-1)*columns -1) {
				for(int j=0; j<block.getBlock().size(); j++) {
					hash.put(block.getBlock().get(j).getX()*columns + block.getBlock().get(j).getY(), true);
				}
				return true ;
			}
			//활성화된 블록일경우 블록 현재 위치 칸들 전부 활성화
			else if(hash.get((block.getBlock().get(i).getX()+1)*columns + block.getBlock().get(i).getY())) {
				for(int j=0; j<block.getBlock().size(); j++) {
					hash.put(block.getBlock().get(j).getX()*columns + block.getBlock().get(j).getY(), true);
				}
				
				return true ;
			}
			
			else {
				
			}
			
		}
		//블록을 내림
		block.downBlock();
		//블록이 내려가는중
		return false;
		
	}
	
	//완성된 row가 있는지 체그 (완성된 로우가 있다면 스코어 계산후 모든 활성화 블록 아래도 내림)
	private void rowSuccess() {
			
		ArrayList<Integer> successLine = new ArrayList<Integer>();
		
		//완료된 행수 계산
		for(int i=0; i<rows; i++) {
			boolean check = true ;
			for(int j=0; j<columns; j++) {
				if(!hash.get(j + i*columns)) {
					check = false ;
					break;
					
				}		
			}
			
			//행이 완성될 경우 완료된 행 수 추가
			if(check) {
				successLine.add(i);
				
			}
					 
		}
		
		//완성된 줄은 인식
		//스코어 반영
		if(successLine.size() ==0) {
			
		}
		else if(successLine.size() ==1) {
			score += 1;
		}
		else {
			score += Math.pow(2 , successLine.size());
		}
		
		for(int i=successLine.size()-1; i>-1; i--) {
			int ten = successLine.get(i);
			for(int j =0 ; j < columns; j++) {
				hash.put(ten*columns+j, false);
			}	 
		}				
		
		
		//완성된 줄이 있다면 활성화 블록들을 아래로 내림
		// 두줄 이상일때, 한줄일때 구분
		if(successLine.size() >0) {		
			for(int i=successLine.size()-1; i>-1; i--) {	 
				for(int j=(rows-1)*columns-1; j>-1; j--) {
					if(!hash.get(j+columns).booleanValue() && hash.get(j).booleanValue()) {
						hash.put(j, false);
						hash.put(j+columns, true);
					}
				}
			}
		
		}
		//완성된 줄이 없다면 패스
		else {
			
		}
			
	}
		
	//게임이 끝났는지 확인 끝났으면 true, 안끝났으면 false
	private boolean endCheck() {
		for(int i=0; i<columns; i++) {
			if(hash.get(i).booleanValue()) 
				return true;			
		}
		//게임이 안끝남
		return false;
		
	}
	
	//컨트롤러로 받은 블럭 조작 신호를 소켓에 전달하는 함수
	public String sendClientSign(String str) {
		
		return str ;
	}
	
	
	//컨트롤러로 받은 블럭 조작 신호를 소켓에 전달하는 함수
	public boolean gaming() {
		if(checkBoard(laningBlock)) {
			rowSuccess();
			if(endCheck()) return false;
			else {
				//게임이 안끝났다면 신규 블록을 생성한다..
				makeBlock();
				return true;
			}
			
		}
		//게임이 안끝났다면 신규 블록을 생성한다.
		else return true ;
			
	}

	//블록 left 이동 이벤트
	public void leftMove() {
		for(Node n : laningBlock.leftMoveBlock()) {
			if(n.getY() >columns-1 || n.getY() <0 || n.getY() >rows*columns-1  || hash.get((n.getX())*columns+n.getY()) ) {
				laningBlock.backupBlock();
				
				break;
			}
		}
	}
	
	//블록 left 이동 이벤트
	public void rightMove() {
		for(Node n : laningBlock.rightMoveBlock()) {
			if(n.getY() >columns-1 || n.getY() <0 || n.getY() >rows*columns-1  || hash.get((n.getX())*columns+n.getY()) ) {
				laningBlock.backupBlock();
				
				break;
			}
		}
				
	}
	
	//블록 회전 이벤트
	public void convertBlock() {
		
		for(Node n : laningBlock.convertBlock()) {
			
			if(n.getX() <0 || n.getX() >rows*columns-1 || n.getY() >columns-1 || n.getY() <0 || n.getY() >rows*columns-1  || hash.get((n.getX())*columns+n.getY()) ) {
				laningBlock.backupBlock();
				
				break;
			}
			
		}
		
	}
	
	//현재 블록 위치 알려줌
	public HashMap getBlockLoc() {
		HashMap curLoc = new HashMap();
		int count =0;
		for (Node n :laningBlock.getBlock()) {
			curLoc.put(count, n.getX()*columns + n.getY());
			count++;
		}
		return curLoc;
	}
	
	//셰션 종료등으로 게임 강제 종료
	public void endGame() {
		startCheck =false ;
	}
	
}
