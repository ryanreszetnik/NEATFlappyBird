package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Genome implements Comparable<Genome> {
	public Counter nodeCount;
	public Counter connCount;

	private static List<Integer> combinedList;

	private float fitness;
	private float adjustedFitness;

	public static final float weightRandomProbability = 0.01f;

	private HashMap<Integer, Connection> connections = new HashMap<>();
	private HashMap<Integer, Node> nodes = new HashMap<>();
	private ArrayList<Integer> connectionInn = new ArrayList<>();
	private ArrayList<Integer> nodeInn = new ArrayList<>();

	public Genome(Counter nodeCount, Counter connCount) {
		this.nodeCount = nodeCount;
		this.connCount = connCount;
	}

	public Genome(Counter nodeCount, Counter connCount, HashMap<Integer, Node> nodes,
			HashMap<Integer, Connection> connections) {
		this.nodeCount = nodeCount;
		this.connCount = connCount;
		for (Connection c : connections.values()) {
			addConnection(c.copy());
			connectionInn.add(c.getInnovation());
		}
		for (Node n : nodes.values()) {
			addNode(n.copy());
			nodeInn.add(n.getInn());
		}
	}

	public Genome copy() {
		return new Genome(nodeCount, connCount, nodes, connections);
	}

	public void setFitness(float fit) {
		this.fitness = fit;
	}

	public float getFitness() {
		return fitness;
	}

	public void setAdjustedFitness(float fit) {
		this.adjustedFitness = fit;
	}

	public float getAdjustedFitness() {
		return adjustedFitness;
	}

	public void addNode(Node n) {
		nodes.put(n.getInn(), n);
		nodeInn.add(n.getInn());
	}

	public void addConnection(Connection c) {
		connections.put(c.getInnovation(), c);
		connectionInn.add(c.getInnovation());
	}

	public HashMap<Integer, Connection> getConnections() {
		return connections;
	}

	public HashMap<Integer, Node> getNodes() {
		return nodes;
	}

	public ArrayList<Integer> getConnectionInn() {
		return connectionInn;
	}

	public ArrayList<Integer> getNodeInn() {
		return nodeInn;
	}

	public int getConnection(int nodea, int nodeb) {
		for (Connection c : connections.values()) {
			if (c.getInputNode() == nodea && c.getOutputNode() == nodeb
					|| c.getInputNode() == nodeb && c.getOutputNode() == nodea) {
				return c.getInnovation();
			}
		}
		return -1;
	}

	public boolean inFuture(int toFind, int current) {
		if (current == toFind) {
			return true;
		}
		for (Connection c : connections.values()) {
			if (c.getInputNode() == current) {
				if (inFuture(toFind, c.getOutputNode())) {
					return true;
				}
			}
		}
		return false;
	}

	public void newRandConnection(Random r, int maxAttempts) {
		// add Random Connection

		for (int i = 0; i < maxAttempts; i++) {
			Node node1 = nodes.get(nodeInn.get(r.nextInt(nodeInn.size())));
			Node node2 = nodes.get(nodeInn.get(r.nextInt(nodeInn.size())));
			if (node1.getInn() == node2.getInn()) {// chose same 2 nodes
				continue;
			}
			if (getConnection(node1.getInn(), node2.getInn()) != -1) {// already
																		// exists
				continue;
			}
			boolean flip = false;
			if (node1.getType() == Node.TYPE.OUTPUT && node2.getType() == Node.TYPE.INPUT) {// output
																							// -->
																							// input
				flip = true;
			} else if (node1.getType() == Node.TYPE.OUTPUT && node2.getType() == Node.TYPE.HIDDEN) {// output
																									// -->
																									// hidden
				flip = true;
			} else if (node1.getType() == Node.TYPE.HIDDEN && node2.getType() == Node.TYPE.INPUT) {// hidden-->
																									// input
				flip = true;
			} else if (node1.getType() == Node.TYPE.INPUT && node2.getType() == Node.TYPE.INPUT) {// both
																									// input
				continue;
			} else if (node1.getType() == Node.TYPE.OUTPUT && node2.getType() == Node.TYPE.OUTPUT) {// both
																									// output
				continue;
			} else if (node1.getType() == Node.TYPE.HIDDEN && node2.getType() == Node.TYPE.HIDDEN) {// both
																									// hidden
				// need to find out which one is "first"
				if (inFuture(node1.getInn(), node2.getInn())) {
					flip = true;
				}
			}
			Connection c;
			if (flip) {
				c = new Connection(node2.getInn(), node1.getInn(), connCount.getInn(node2.getInn(), node1.getInn()),
						r.nextFloat(), true);
			} else {
				c = new Connection(node1.getInn(), node2.getInn(), connCount.getInn(node1.getInn(), node2.getInn()),
						r.nextFloat(), true);
			}
			addConnection(c);
			return;
		}
	}

	public void newRandNode(Random r) {
		// add Random Node (Splits connection)
		if (connections.size() == 0) {
			return;
		}
		Connection c = connections.get(connectionInn.get(r.nextInt(connectionInn.size())));
		c.disable();
		Node between = new Node(Node.TYPE.HIDDEN, nodeCount.getInn(c.getInputNode(), c.getOutputNode()), r.nextFloat());
		addNode(between);
		addConnection(new Connection(c.getInputNode(), between.getInn(),
				connCount.getInn(c.getInputNode(), between.getInn()), 1, true));
		addConnection(new Connection(between.getInn(), c.getOutputNode(),
				connCount.getInn(between.getInn(), c.getOutputNode()), c.getWeight(), true));
		// System.out.println(c.isEnabled() + " " + between.getInn() );
	}

	public void mutateWeights(Random r) {
		for (Connection c : connections.values()) {
			if (r.nextFloat() < weightRandomProbability) {
				c.setWeight(r.nextFloat() * 4f - 2f);
			} else {
				c.setWeight(c.getWeight() * (r.nextFloat() * 4f - 2f));
			}
		}
		for (Node n : nodes.values()) {
			if (r.nextFloat() < weightRandomProbability) {
				n.setBiasWeight(r.nextFloat() * 4f - 2f);
			} else {
				n.setBiasWeight(n.getBiasWeight() * (r.nextFloat() * 4f - 2f));
			}
		}

	}

	public static float compatibilityDis(Genome g1, Genome g2, float c1, float c2, float c3) {
		int disjoint = 0;
		int excess = 0;
		int matching = 0;
		float weightDiff = 0;

		int maxInn1 = Collections.max(g1.getNodeInn());
		int maxInn2 = Collections.max(g2.getNodeInn());
		int maxInnNum = Math.max(maxInn1, maxInn2);
		int numOfGenes = Math.max(g1.getNodeInn().size(), g2.getNodeInn().size());
		Set<Integer> set = new LinkedHashSet<>(g1.getNodeInn());
		set.addAll(g2.getNodeInn());
		combinedList = new ArrayList<>(set);

		for (int i : combinedList) {
			if (!g1.getNodeInn().contains(i) && maxInn1 > i && g2.getNodeInn().contains(i)) {
				disjoint++;
			} else if (!g2.getNodeInn().contains(i) && maxInn2 > i && g1.getNodeInn().contains(i)) {
				disjoint++;
			} else if (!g1.getNodeInn().contains(i) && maxInn1 < i && g2.getNodeInn().contains(i)) {
				excess++;
			} else if (!g2.getNodeInn().contains(i) && maxInn2 < i && g1.getNodeInn().contains(i)) {
				excess++;
			}
		}
		set = new LinkedHashSet<>(g1.getConnectionInn());
		set.addAll(g2.getConnectionInn());
		combinedList = new ArrayList<>(set);

		if (g1.getConnectionInn().size() > 0 && g2.getConnectionInn().size() > 0) {
			maxInn1 = Collections.max(g1.getConnectionInn());
			maxInn2 = Collections.max(g2.getConnectionInn());
			maxInnNum = Math.max(maxInn1, maxInn2);
			for (int i : combinedList) {
				if (g1.getConnectionInn().contains(i) && g2.getConnectionInn().contains(i)) {
					if (!g1.getConnections().get(i).isEnabled() || !g2.getConnections().get(i).isEnabled()) {
						continue;
					}

					matching++;
					weightDiff += Math
							.abs(g1.getConnections().get(i).getWeight() - g2.getConnections().get(i).getWeight());
				} else if (!g1.getConnectionInn().contains(i) && maxInn1 > i && g2.getConnectionInn().contains(i)) {
					disjoint++;
				} else if (!g2.getConnectionInn().contains(i) && maxInn2 > i && g1.getConnectionInn().contains(i)) {
					disjoint++;
				} else if (!g1.getConnectionInn().contains(i) && maxInn1 < i && g2.getConnectionInn().contains(i)) {
					excess++;
				} else if (!g2.getConnectionInn().contains(i) && maxInn2 < i && g1.getConnectionInn().contains(i)) {
					excess++;
				}
			}
			if (matching != 0) {
				weightDiff /= matching;
			}
		} else if (g1.getConnectionInn().size() > 0 && g2.getConnectionInn().size() == 0) {
			excess += g1.getConnectionInn().size();
		} else if (g1.getConnectionInn().size() == 0 && g2.getConnectionInn().size() > 0) {
			excess += g2.getConnectionInn().size();
		}
		return excess * c1 / numOfGenes + disjoint * c2 / numOfGenes + c3 * weightDiff;
	}

	public void reset() {
		for (Node n : nodes.values()) {
			n.reset();
		}
	}

	public static Genome crossOver(Genome morefit, Genome lessFit, Random r) {
		Genome child = new Genome(morefit.nodeCount, morefit.connCount);
		for (Node n : morefit.nodes.values()) {// copy fitter nodes
			child.addNode(n.copy());
		}
		for (Connection cfit : morefit.connections.values()) {// loop through
																// fitter
																// connections
			if (lessFit.getConnections().containsKey(cfit.getInnovation())) {// if
																				// also
																				// in
																				// unfit
				// genes match history
				if (r.nextBoolean()) {
					child.addConnection(cfit.copy());
				} else {
					Connection temp = lessFit.getConnections().get(cfit.getInnovation()).copy();
					if (cfit.isEnabled()) {
						temp.reEnable();
					}
					child.addConnection(temp);
				}
			} else {
				child.addConnection(cfit.copy());
			}
		}
		return child;
	}

	@Override
	public int compareTo(Genome o) {
		// TODO Auto-generated method stub
		return (int) (this.getFitness() - o.getFitness());
	}
}
