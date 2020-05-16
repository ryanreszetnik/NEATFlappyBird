package application;

public class Connection {

	private int input;
	private int output;
	private int innovation;
	private boolean enabled = true;
	
	private float weight;

	public Connection(int input, int output, int innovation, float weight, boolean enabled){
		this.innovation= innovation;
		this.input=input;
		this.output=output;
		this.weight=weight;
		this.enabled=enabled;
	}
	
	public int getInputNode() {
		return input;
	}
	public int getOutputNode() {
		return output;
	}
	public int getInnovation() {
		return innovation;
	}
	public boolean isEnabled(){
		return enabled;
	}
	public void disable(){
		enabled = false;
	}
	public Connection copy(){
		return new Connection(this.input,this.output,this.innovation,this.weight,this.enabled);
	}
	public float getWeight(){
		return this.weight;
	}

	public void reEnable(){
		this.enabled = true;
	}
	public void setWeight(float f) {
		this.weight=f;
	}
}
