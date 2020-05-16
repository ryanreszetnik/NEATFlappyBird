package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public abstract class Evaluator {

	public static final float DM = 10;
	public static final float C1 = 0.5f;
	public static final float C2 = 0.1f;
	public static final float C3 = 0.1f;

	public static final float mutationRate = 0.3f;
	public static final float addConnectionRate = 0.1f;
	public static final float addNodeRate = 0.2f;
	public static final int maxConnectionAttempts = 20;

	public Random r = new Random();

	public static Genome origin;

	public Counter connCount;
	public Counter nodeCount;

	public Genome topGenome;
	public float topFit = 0;

	private int popSize;
	private ArrayList<Network> networks = new ArrayList<>();
	private ArrayList<Genome> genomes = new ArrayList<>();
	private ArrayList<Genome> nextGenomes = new ArrayList<>();
	public ArrayList<Species> species = new ArrayList<>();
	private HashMap<Genome, Species> speciesMap = new HashMap<>();

	public Evaluator(Genome g, int popSize, Counter nodeCount, Counter connCount) {
		this.nodeCount = nodeCount;
		this.connCount = connCount;
		this.popSize = popSize;
		this.origin = g.copy();
		for (int i = 0; i < popSize; i++) {
			genomes.add(g.copy());
		}
	}

	public void evalutate() {
		// reset needed
		topFit = 0;
		nodeCount.reset();
		connCount.reset();
		networks.clear();
		// for (int i = 0; i < 5; i++) {
		// genomes.add(origin.copy());
		// }

		for (Species s : species) {
			s.reset(r);
		}

		// put genomes in species
		for (Genome g : genomes) {
			boolean putInSpecies = false;
			networks.add(new Network(g));
			g.reset();
			g.setFitness(0);
			g.setAdjustedFitness(0);
			for (Species s : species) {
				if (Genome.compatibilityDis(g, s.getMascot(), C1, C2, C3) < DM) {// in
																					// that
																					// species
					s.addGenome(g);
					speciesMap.put(g, s);
					putInSpecies = true;
					break;
				}
			}
			if (!putInSpecies) {// create a new species
				Species s = new Species(g);
				species.add(s);
				s.addGenome(g);
				speciesMap.put(g, s);
			}
		}

		// remove empty species
		for (int i = species.size() - 1; i >= 0; i--) {
			if (species.get(i).getMembers().size() == 0) {
				species.remove(i);
			}
		}

		// run round
		runRound(networks);
	}

	public void afterRound() {
		// adjust fitnesses
		for (Genome g : genomes) {
			float fit = getFitness(g);
			Species s = speciesMap.get(g);
			float adjustedFit = fit / s.getMembers().size();
			g.setAdjustedFitness(adjustedFit);

			s.addToAdjustedFit(adjustedFit);
			if (fit > topFit) {
				topFit = fit;
				topGenome = g;
			}
		}
		Collections.sort(species);
		// put best genomes from each species into next gen (maybe top 20 or
		// smth idk)
		int count = 0;
		for (Species s : species) {
			count++;
			if (count > 10) {
				break;
			}
			Genome next = s.bestGenome();
			nextGenomes.add(next);

		}

		// Breed
		int counter = 0;
		while (nextGenomes.size() <= popSize) {
			counter++;
			Species s = Species.weightedRandomSpecies(species, r);

			Genome a = s.getWeightedGenome(r);
			Genome b = s.getWeightedGenome(r);
			Genome child;
			if (a.getAdjustedFitness() >= b.getAdjustedFitness()) {
				child = Genome.crossOver(a, b, r);
			} else {
				child = Genome.crossOver(b, a, r);
			}
			if (r.nextFloat() < mutationRate) {
				child.mutateWeights(r);
			}
			if (r.nextFloat() < addConnectionRate) {
				child.newRandConnection(r, maxConnectionAttempts);
			}
			if (r.nextFloat() < addNodeRate) {
				child.newRandNode(r);
			}

			nextGenomes.add(child);

		}

		genomes = nextGenomes;
		nextGenomes = new ArrayList<>();

	}

	public abstract void runRound(ArrayList<Network> networks);

	public abstract float getFitness(Genome g);

}
