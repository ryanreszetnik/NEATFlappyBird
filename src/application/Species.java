package application;

import java.util.ArrayList;
import java.util.Random;

public class Species implements Comparable< Species >{

	public double sumAdjustedFit;
	private Genome mascot;
	private ArrayList<Genome> members = new ArrayList<>();
	
	public Species(Genome mascot){
		this.mascot=mascot;
	}
	public ArrayList<Genome> getMembers(){
		return members;
	}
	
	public void addGenome(Genome g){
		members.add(g);
	}
	public void addToAdjustedFit(float fit){
		sumAdjustedFit+=fit;
	}
	
	public double getSumFit(){
		return sumAdjustedFit;
	}
	
	public void reset(Random r){
		mascot = members.get(r.nextInt(members.size()));
		members.clear();
		sumAdjustedFit = 0;
	}
	
	public Genome getWeightedGenome(Random r){
		double totalFit = 0;
		for(Genome g:members){
			totalFit+=g.getAdjustedFitness();
		}
		double rand = r.nextDouble()*totalFit;
		double counter = 0;
		for(Genome g:members){
			counter+=g.getAdjustedFitness();
			if(counter>=rand){
				return g;
			}
		}
		System.out.println("Weighted Genome ERROR");
		return null;
	}
	
	public Genome getMascot(){
		return mascot;
	}
	
	public static Species weightedRandomSpecies(ArrayList<Species> species, Random r){
		double totalFit = 0;
//		System.out.println(species.get(0).getSumFit());
		for(Species s:species){
			totalFit+=s.getSumFit();
		}
		double rand = r.nextDouble()*totalFit;
		double counter = 0;
		for(Species s:species){
			counter+=s.getSumFit();
			if(counter>=rand){
				return s;
			}
		}
		System.out.println("Weighted Species ERROR: " + species.size() + " "+counter +" " + rand);
		return null;
	}
	
	public Genome bestGenome(){
		Genome best = mascot;
		for(Genome g: members){
			if(g.getFitness()>best.getFitness()){
				best = g;
			}
		}
		return best;	
	}
	@Override
	public int compareTo(Species o) {
		// TODO Auto-generated method stub
		
		if(o.bestGenome().getFitness()>this.bestGenome().getFitness()){
			return 1;
		}else if(o.bestGenome().getFitness()<this.bestGenome().getFitness()){
			return -1;
		}else{
			return 0;
		}
		
//		return (int)(o.bestGenome().getFitness()-this.bestGenome().getFitness());
	}
	
}
