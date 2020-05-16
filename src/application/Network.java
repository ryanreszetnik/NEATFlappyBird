package application;

import java.util.ArrayList;

public class Network {

	Genome gene;
	ArrayList<Node> inputs = new ArrayList<>();
	ArrayList<Node> outputs = new ArrayList<>();
	public Network(Genome g){
		gene = g;
		for(Node n :g.getNodes().values()){
			if(n.getType()==Node.TYPE.INPUT){
				inputs.add(n);
			}else if(n.getType()==Node.TYPE.OUTPUT){
				outputs.add(n);
			}
		}
	}
	public float[] run(float[] in){
		if(in.length!=this.inputs.size()){
			System.out.println("Mismatch Size");
			System.out.println("Mismatch SizeError");
			return null;
		}
		gene.reset();
		for(int i = 0; i < in.length; i++){
			inputs.get(i).setOutput(in[i]);
		}
		float[] output = new float[outputs.size()];
		int counter = 0;
		for(Node out: outputs){
			output[counter] = out.calculate(gene.getNodes(), gene.getConnections());
			
			counter++;
		}
		
		return output;
	}

	public void reset(){
		gene.reset();
	}
	
	
}
