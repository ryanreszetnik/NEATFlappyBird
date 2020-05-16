package application;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;

public class Testing {
	static BufferedImage output = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
	static Graphics2D gr = (Graphics2D) output.getGraphics();
	static Random r = new Random();
	// public static int counter = 0;
	public static int[][] testCases = { { 0, 1 }, { 1, 0 }, { 1, 1 }, { 0, 0 } };

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int successful = 0;
		for (int runCount = 0; runCount < 100; runCount++) {

			Counter nodeCount = new Counter();
			Counter connCount = new Counter();
			Genome a = new Genome(nodeCount, connCount);
			a.addNode(new Node(Node.TYPE.INPUT, nodeCount.getNextCount(), 0.5f));
			a.addNode(new Node(Node.TYPE.INPUT, nodeCount.getNextCount(), 0.5f));
			a.addNode(new Node(Node.TYPE.OUTPUT, nodeCount.getNextCount(), 5f));
			a.addConnection(new Connection(1, 3, connCount.getNextCount(), 5f, true));
			a.addConnection(new Connection(2, 3, connCount.getNextCount(), 5f, true));

			test(new Network(a), false);
//			System.out.println(a.getFitness() + " first");

			Evaluator eval = new Evaluator(a, 200, nodeCount, connCount) {

				@Override
				public float getFitness(Genome g) {
					// TODO Auto-generated method stub
					// System.out.print(g.getFitness() + " ");
					return g.getFitness();
				}

				@Override
				public void runRound(ArrayList<Network> networks) {
					// TODO Auto-generated method stub
					for (Network n : networks) {
						test(n, false);
					}

				}

			};
			for (int i = 0; i <= 200; i++) {

				eval.evalutate();
				if (eval.topFit > 3.9) {
					System.out.println("Success");
					successful++;
					break;
				}
				// if (i % 100 == 0) {
				// show(eval.topGenome, "Round " + i);
				// test(new Network(eval.topGenome), true);
				// System.out.println(eval.topGenome.getFitness());
				// }
				// System.out.println("Round: " + i + " Top Fit: " + eval.topFit
				// + " NumSpecies: " + eval.species.size()
				// + " NumConn: " + eval.connCount.getCount() + " NumNodes: " +
				// eval.nodeCount.getCount()
				// + "Active Conn:" + eval.connCount.previous.size() + "Active
				// Node:"
				// + eval.nodeCount.previous.size());
				if (i == 200) {
					System.out.println("Nope");
				}
			}
			// Genome best = eval.topGenome;
			// test(new Network(best), true);
			// System.out.println(best.getFitness());
			
		}
		System.out.println("Successful: " + successful);
	}

	public static void show(Genome g, String name) {
		int yposInput = 20;
		int yposOutput = 20;
		gr.setComposite(AlphaComposite.Src);
		gr.setColor(new Color(0x00FFFFFF, true));
		gr.setBackground(new Color(0x00FFFFFF, true));
		gr.fillRect(0, 0, output.getWidth(), output.getHeight());
		int[][] nodePos = new int[g.nodeCount.getCount() + 10][2];
		int c2 = 0;
		for (Node n : g.getNodes().values()) {
			c2++;
			int xpos = 0;
			int ypos = 0;
			if (n.getType() == Node.TYPE.INPUT) {
				xpos = 20;
				ypos = yposInput;
				yposInput += 150;
			} else if (n.getType() == Node.TYPE.OUTPUT) {
				xpos = 900;
				ypos = yposOutput;
				yposOutput += 150;
			} else {
				xpos = (int) (Math.random() * 800 + 100);
				ypos = (int) (Math.random() * 200 + 100);
			}
			nodePos[n.getInn() - 1][0] = xpos;
			nodePos[n.getInn() - 1][1] = ypos;
			gr.setColor(Color.BLACK);
			gr.drawOval(xpos, ypos, 40, 40);
			gr.setColor(Color.BLACK);
			gr.setFont(new Font("Arial Black", Font.PLAIN, 20));
			gr.drawString(n.getInn() + "", xpos + 15, ypos + 25);

			String s = n.getInn() + " Bias: " + n.getBiasWeight();
			gr.drawString(s, 500, c2 * 30 + 500);
		}
		double counter = 0;
		c2 = 0;
		for (Connection c : g.getConnections().values()) {
			c2++;
			int x1 = nodePos[c.getInputNode() - 1][0];
			int y1 = nodePos[c.getInputNode() - 1][1];
			int x2 = nodePos[c.getOutputNode() - 1][0];
			int y2 = nodePos[c.getOutputNode() - 1][1];
			if (c.isEnabled()) {
				gr.setColor(Color.BLACK);
			} else {
				gr.setColor(Color.RED);
			}
			try {
				gr.setFont(new Font("Arial Black", Font.PLAIN, 15));
				gr.drawString(Math.round(c.getWeight() * 100) / 100.0 + "", (x1 + x2) / 2, (y1 + y2) / 2);
				String s = c.getInputNode() + " to " + c.getOutputNode() + " weight: " + c.getWeight();
				gr.drawString(s, 150, c2 * 30 + 500);
				gr.drawLine(x1 + 40, y1 + 20, x2, y2 + 20);
				counter += c.getWeight();
			} catch (NullPointerException e) {
				System.out.println("oh no" + c + " or " + g);
			}
		}
		gr.drawString("Final Sum: " + counter, 20, 500);

		File outputfile = new File(name);
		try {
			ImageIO.write(output, "png", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void test(Network n, boolean print) {
		n.gene.setFitness(0);
		boolean bad = false;
		for (int i = 0; i < 4; i++) {
			n.reset();

			// System.out.println();
			// System.out.println("Test "+ i);
			float[] inputs = new float[2];
			inputs[0] = testCases[i][0];
			inputs[1] = testCases[i][1];
			float expectedOutput = 1;
			if (inputs[0] == 0 && inputs[1] == 0 || inputs[0] == 1 && inputs[1] == 1) {
				expectedOutput = 0;
			}
			n.run(inputs);
			float out = n.outputs.get(0).getOutput();
			float actualOut = out;
			if (out > 0.5) {
				out = 1;
			} else {
				out = 0;
			}
			if (print) {
				System.out.println(expectedOutput + " " + actualOut);
			}

			float diff = Math.abs(actualOut - expectedOutput);
			if (diff < 0.5) {
				n.gene.setFitness(n.gene.getFitness() + 1);
			} else if (expectedOutput == 0) {
				n.gene.setFitness(n.gene.getFitness() + 0.2f / (actualOut + 2.5f));
			} else {
				n.gene.setFitness(n.gene.getFitness() + 0.2f / (Math.abs(actualOut - 3.5f)));
			}
//			if (diff > 0.45) {
//				n.gene.setFitness(n.gene.getFitness() - 1f);
//			}
//			if (diff > 0.9) {
//				bad = true;
//			}
		}
		if (n.gene.getFitness() <= 0 || bad) {
			n.gene.setFitness(0.01f);
		}
		// System.out.println(n.gene.getFitness());
	}

	/*
	 * Genome a = new Genome(nodeCount, connCount); a.addNode(new
	 * Node(Node.TYPE.INPUT, nodeCount.getNextCount(), 0.5f)); a.addNode(new
	 * Node(Node.TYPE.INPUT, nodeCount.getNextCount(), 0.5f)); a.addNode(new
	 * Node(Node.TYPE.HIDDEN, nodeCount.getNextCount(), -10f)); a.addNode(new
	 * Node(Node.TYPE.HIDDEN, nodeCount.getNextCount(), 30f)); a.addNode(new
	 * Node(Node.TYPE.OUTPUT, nodeCount.getNextCount(), -30f));
	 * a.addConnection(new Connection(1,3,connCount.getNextCount(),20f, true));
	 * a.addConnection(new Connection(2,3,connCount.getNextCount(),20f, true));
	 * a.addConnection(new Connection(1,4,connCount.getNextCount(),-20f, true));
	 * a.addConnection(new Connection(2,4,connCount.getNextCount(),-20f, true));
	 * a.addConnection(new Connection(3,5,connCount.getNextCount(),20f, true));
	 * a.addConnection(new Connection(4,5,connCount.getNextCount(),20f, true));
	 * 
	 * 
	 * 
	 * a.addNode(new Node(Node.TYPE.INPUT, nodeCount.getNextCount(), 0.5f));
	 * a.addNode(new Node(Node.TYPE.INPUT, nodeCount.getNextCount(), 0.5f));
	 * a.addNode(new Node(Node.TYPE.HIDDEN, nodeCount.getNextCount(), 5f));
	 * a.addNode(new Node(Node.TYPE.HIDDEN, nodeCount.getNextCount(), 5f));
	 * a.addNode(new Node(Node.TYPE.OUTPUT, nodeCount.getNextCount(), 5f));
	 * a.addConnection(new Connection(1,3,connCount.getNextCount(),5f, true));
	 * a.addConnection(new Connection(2,3,connCount.getNextCount(),5f, true));
	 * a.addConnection(new Connection(1,4,connCount.getNextCount(),5f, true));
	 * a.addConnection(new Connection(2,4,connCount.getNextCount(),5f, true));
	 * a.addConnection(new Connection(3,5,connCount.getNextCount(),5f, true));
	 * a.addConnection(new Connection(4,5,connCount.getNextCount(),5f, true));
	 * 
	 * 
	 */
}
