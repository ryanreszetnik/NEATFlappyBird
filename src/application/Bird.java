package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Bird {

	public static final float gravity = 0.1f;
	public static final float jumpSpeed = 5;
	public static final float maxYVel = 5;
	public static int screenHeight;
	private float yVel = 0;
	public int topScore = 0;
	Network net;
	Rectangle hitBox = new Rectangle(45, 45);
	public boolean isAlive = true;
	public static Image BirdImage;
	public ImageView birdView;
	private int dis;

	public Bird(Pane root) {
		birdView = new ImageView(BirdImage);
		root.getChildren().add(birdView);
		hitBox.setTranslateY(root.getHeight() / 2);
		hitBox.setTranslateX(100);
		birdView.setTranslateY((root.getHeight() / 2));
		birdView.setTranslateX(100);

		screenHeight = (int) root.getHeight();

	}

	public void run(float[] inputs) {
		net.run(inputs);
		float val = net.outputs.get(0).getOutput();
		// System.out.print(inputs[0] +" --> " + val + " || ");
		if (val > 0.5) {
			jump();
		}

	}

	public void kill() {
		hitBox.setFill(Color.RED);
		birdView.setVisible(false);
		isAlive = false;
	}

	public void reset() {
		isAlive = true;
		topScore = 0;
		birdView.setVisible(true);
		hitBox.setTranslateY(screenHeight / 2);
	}

	public int getHeight() {
		return (int) (hitBox.getTranslateY() + hitBox.getHeight() / 2);
	}

	public void jump() {
		yVel = -jumpSpeed;
	}

	public void move() {
		yVel = Math.min(yVel + gravity, maxYVel);
		hitBox.setTranslateY(hitBox.getTranslateY() + yVel);
		birdView.setTranslateY(hitBox.getTranslateY() + yVel);
		if (yVel > maxYVel - 1) {
			birdView.setRotate((yVel - 4) * 20);
		} else {
			birdView.setRotate(-20);
		}
		if (hitBox.getTranslateY() > screenHeight) {
			kill();
		}
		if (isAlive) {
			if (Pipe.score == 0) {
				dis++;
			}
//			dis/=1000;
			topScore = dis;
		}
	}

}
