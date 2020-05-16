package application;

import java.util.HashMap;

public class Node {

	enum TYPE {
		INPUT, HIDDEN, OUTPUT;
	}
	private TYPE type;
	private int innovation;
	private float biasWeight;
	private float bias = 1f;
	private boolean isCalculated = false;
	private float output = 0f;
	
	public Node(TYPE t, int innovation, float biasWeight){
		this.type=t;
		this.innovation=innovation;
		this.biasWeight=biasWeight;
	}
	
	public void setBiasWeight(float weight){
		this.biasWeight=weight;
	}
	public float getBiasWeight(){
		return this.biasWeight;
	}
	
	public TYPE getType(){
		return type;
	}
	public int getInn(){
		return innovation;
	}
	public Node copy(){
		return new Node(this.type,this.innovation, this.biasWeight);
	}
	public boolean isCalculated(){
		return isCalculated;
	}
	public void reset(){
		isCalculated = false;
	}
	public float calculate(HashMap<Integer, Node> nodes, HashMap<Integer, Connection> connections){
		if(isCalculated){
			return output;
		}
		float totalInputs = 0f;
		for(Connection c: connections.values()){
			if(c.getOutputNode() == this.innovation && c.isEnabled()){
				totalInputs+=nodes.get(c.getInputNode()).calculate(nodes, connections)*c.getWeight();
			}
		}
		totalInputs+=bias*biasWeight;
		output=activation(totalInputs);
		isCalculated = true;
		return output;
	}
	public float activation(float in) {
		return (float) (1f / (1f + Math.pow(Math.E, -4.9 * in)));
	}
	public void setOutput(float out){
		isCalculated = true;
		output = out;
	}
	public float getOutput(){
		return output;
	}
	

}
