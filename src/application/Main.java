package application;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class Main extends Application {
	AnimationTimer timer;
//	public static Pipe previous = null;
	public static Pipe next = null;
	public static ArrayList<Bird> birds = new ArrayList<>();
	public static float[] inputs = new float[2];
	public static Pipe[] pipes;
	public static boolean running = false;
	public static final int popSize = 200;
	public static int aliveCount = popSize;
	public static boolean nextMove = false;
	public static int roundCount = 0;
	

	@Override
	public void start(Stage primaryStage) {
		try {
			Pane root = new Pane();
			Scene scene = new Scene(root, 480, 650, Color.rgb(84,201,208));
			root.setBackground(new Background(new BackgroundFill(Color.rgb(84,201,208),CornerRadii.EMPTY, Insets.EMPTY)));
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Label score = new Label();
			Label round = new Label();
			score.setId("scoreText");
			score.getStyleClass().add("outline");
			Bird.BirdImage= new Image(getClass().getResource("Bird.png").toExternalForm());
			for (int i = 0; i < popSize; i++) {
				Bird b = new Bird(root);
				birds.add(b);
			}
			Image floor= new Image(getClass().getResource("Floor.png").toExternalForm());
			ImageView floorView = new ImageView(floor);
			Pipe.topPipe= new Image(getClass().getResource("Pipe_Top.png").toExternalForm());
			Pipe.bottomPipe= new Image(getClass().getResource("Pipe_Bottom.png").toExternalForm());
			floorView.setTranslateY(root.getHeight()-80);
//			Color c = new Color();
//			System.out.println(c.getRed() +" " +c.getGreen() + " " + c.getBlue());Color.rgb(84,201,208)

			Counter nodeCount = new Counter();
			Counter connCount = new Counter();
			Genome a = new Genome(nodeCount, connCount);
			a.addNode(new Node(Node.TYPE.INPUT, nodeCount.getNextCount(), 0.5f));
//			a.addNode(new Node(Node.TYPE.INPUT, nodeCount.getNextCount(), 0.5f));
			a.addNode(new Node(Node.TYPE.INPUT, nodeCount.getNextCount(), 0.5f));
//			a.addNode(new Node(Node.TYPE.INPUT, nodeCount.getNextCount(), 0.5f));
			// a.addNode(new Node(Node.TYPE.INPUT, nodeCount.getNextCount(),
			// 0.5f));
			a.addNode(new Node(Node.TYPE.OUTPUT, nodeCount.getNextCount(), 1f));
//			a.addConnection(new Connection(1, 3, connCount.getNextCount(), 1f, true));
//			a.addConnection(new Connection(2, 3, connCount.getNextCount(), 1f, true));
//			a.addConnection(new Connection(3, 4, connCount.getNextCount(), 1f, true));
			// a.addConnection(new Connection(2, 4, connCount.getNextCount(),
			// 5f, true));
			// a.addConnection(new Connection(3, 4, connCount.getNextCount(), f,
			// true));

			Evaluator eval = new Evaluator(a, popSize, nodeCount, connCount) {

				@Override
				public void runRound(ArrayList<Network> networks) {
					// TODO Auto-generated method stub
					for (int i = 0; i < birds.size(); i++) {
						birds.get(i).net = networks.get(i);
					}
					running = true;
					System.out.println("start");

				}

				@Override
				public float getFitness(Genome g) {
					// TODO Auto-generated method stub
					return g.getFitness();
				}

			};
			score.setAlignment(Pos.TOP_CENTER);
			score.setTranslateX((root.getWidth()-score.getWidth())/2);
			score.setTranslateY(root.getHeight()-60);
			pipes = new Pipe[(int) (root.getWidth() / (Pipe.betweenPipes) + 2)];
			for (int i = 0; i < pipes.length; i++) {
				pipes[i] = new Pipe((int) (Math.random() * (root.getHeight() - 2 * Pipe.minHeightFromEdge)
						+ Pipe.minHeightFromEdge), root);
				pipes[i].moveTo((int) root.getWidth() + (i + 1) * Pipe.betweenPipes);
			}

			// Bird b = new Bird(root);
			root.getChildren().add(floorView);
			root.getChildren().addAll(score, round);

			timer = new AnimationTimer() {// what constantly runs
				@Override
				public void handle(long now) {
					score.setText(Pipe.score + " ");
					round.setText(roundCount + "");
//					if(Pipe.score>10){
//						for(Bird b:birds){
//							b.kill();
//						}
//					}
					
					if (running) {
//						System.out.println("Try");
						// if (nextMove) {
						// System.out.println("move");
						move();
						if (aliveCount == 0) {
							running = false;
						}
						// nextMove = false;
						// }
					} else {
						System.out.println("Done");
						for (Bird b : birds) {
							b.reset();
						}
						Pipe.score = 0;
						for (int i = 0; i < pipes.length; i++) {
							pipes[i].moveTo((int) root.getWidth() + (i + 1) * Pipe.betweenPipes);
						}
						eval.afterRound();
						eval.evalutate();
//						Testing.show(eval.topGenome, "Round: " + roundCount);
						roundCount++;
					}
				}
			};

			scene.setOnKeyPressed(e -> {
				switch (e.getCode()) {
				case UP:
					// b.jump();
					break;

				case R:
					running = true;
					break;
				case N:
					eval.evalutate();
					timer.start();
					break;
				case P:
					nextMove = true;
				}
			});
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static boolean intersects(Rectangle a, Rectangle b) {
		if (a.getTranslateX() > b.getTranslateX() + b.getWidth()) {// a right of
																	// b
			return false;
		}
		if (b.getTranslateX() > a.getTranslateX() + a.getWidth()) {// a left of
																	// b
			return false;
		}
		if (a.getTranslateY() > b.getTranslateY() + b.getHeight()) {// a down b
			return false;
		}
		if (b.getTranslateY() > a.getTranslateY() + a.getHeight()) {// a up b
			return false;
		}

		return true;
	}

	public static void move() {
		if (!running) {
			return;
		}
		next = pipes[Pipe.score % pipes.length];
		for (Pipe p : pipes) {
			p.move(-3);
			

			if (p.isOffScreen()) {
				p.moveTo(Pipe.nextX(pipes));

			}
			for (Bird bird : birds) {
				bird.move();
				if (intersects(bird.hitBox, p.top) || intersects(bird.hitBox, p.bottom)
						|| bird.hitBox.getTranslateY() < -100) {

					bird.kill();
				}
			}

		}

		// inputs: bird height, relative height to next pipe center, x dis to
		// next pipe
		// inputs[0] = next.getX() - 100;
		aliveCount = 0;
		for (Bird bird : birds) {
			if (bird.isAlive) {
				aliveCount++;
//				inputs[0] = bird.getHeight() * 1f / Bird.screenHeight;
//				inputs[1] = (next.getMidpoint() - bird.getHeight());
				inputs[0] = bird.getHeight();
				inputs[1] = next.getMidpoint();
//				inputs[2] = next.getX();
				// System.out.println(inputs[0]);
				// inputs[2] = next.getMidpoint() - inputs[1];

				bird.run(inputs);
			} else if (bird.net.gene.getFitness() == 0) {
				bird.net.gene.setFitness(bird.topScore);
			}
		}
	}
}
