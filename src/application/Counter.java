package application;

import java.util.HashMap;

public class Counter {

	private int count = 0;
	HashMap<String, Integer> previous = new HashMap<>();
	
	public void reset(){
		previous.clear();
	}
	
	public void addToCount(){
		count++;
	}
	public int getCount(){
		return count;
	}
	public int getNextCount(){
		addToCount();
		return getCount();
	}
	
	public int getInn(int nodein, int nodeout){
		String s = nodein + ", "+nodeout;
		if(previous.get(s)!=null){
			return previous.get(s);
		}else{
			int inn = getNextCount();
			previous.put(s, inn);
			return inn;
		}
		
	}
	
	
}
