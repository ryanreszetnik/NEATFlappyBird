package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class Pipe {
public static final int pipeWidth = 80;
public static final int gapWidth = 100;//150
public static final int offScreen = 100;
public static final int betweenPipes = 290;
public static final int minHeightFromEdge = 60+80;
public static final int screenHeight = 650;

private int midHeight;
Rectangle top;
Rectangle bottom;
public boolean passed = false;
public static Image topPipe;
public static Image bottomPipe;
public ImageView topPipeView;
public ImageView bottomPipeView;
public static int score = 0;


public Pipe(int middle, Pane root){
	midHeight=middle;
	topPipeView = new ImageView(topPipe);
	bottomPipeView = new ImageView(bottomPipe);
	top= new Rectangle(pipeWidth,root.getHeight()+offScreen);
	top.setTranslateY((int)(-root.getHeight()-offScreen+middle-betweenPipes/2));
	bottom= new Rectangle(pipeWidth,root.getHeight()+offScreen);
	bottom.setTranslateY(middle+gapWidth/2);
	topPipeView.setTranslateX(root.getWidth());
	bottomPipeView.setTranslateX(root.getWidth());
	topPipeView.setTranslateY((int)(-1000+middle-betweenPipes/2));
	bottomPipeView.setTranslateY(middle+gapWidth/2);
	top.setTranslateX(root.getWidth());
	bottom.setTranslateX(root.getWidth());
	root.getChildren().addAll(topPipeView,bottomPipeView);
}
private void setMiddle(int middle){
	this.midHeight=middle;
	top.setTranslateY((int)(-screenHeight-offScreen+middle-betweenPipes/2));
	bottom.setTranslateY(middle+gapWidth/2);
	topPipeView.setTranslateY((int)(-1000+middle-betweenPipes/2));
	bottomPipeView.setTranslateY(middle+gapWidth/2);
}
public static int newMiddle(){
//	gapWidth = (int)(50+100.0/(1+(score+1)/100.0));
	System.out.println("Gap: " + gapWidth);
	return (int)(Math.random()*(screenHeight-2*minHeightFromEdge-gapWidth)+minHeightFromEdge+gapWidth/2);
}

public int getMidpoint(){
	return midHeight;
}
public void move(double velX){
	top.setTranslateX(top.getTranslateX()+velX);
	bottom.setTranslateX(bottom.getTranslateX()+velX);
	topPipeView.setTranslateX(top.getTranslateX()-2);
	bottomPipeView.setTranslateX(bottom.getTranslateX()-2);
	
	if(top.getTranslateX()<90-pipeWidth){
		if(!passed){
			score++;
		}
		passed = true;
	}
}
public void moveTo(int x){
	top.setTranslateX(x);
	bottom.setTranslateX(x);
	topPipeView.setTranslateX(x-2);
	bottomPipeView.setTranslateX(x-2);
	setMiddle(Pipe.newMiddle());
	passed=false;
}
public int getX(){
	return (int) top.getTranslateX();
}

public boolean isOffScreen(){
	return top.getTranslateX()<-2*pipeWidth;
}
public static int nextX(Pipe[] pipes){
	int maxX = 0;
	for(Pipe p:pipes){
		if(p.getX()>maxX){
			maxX=p.getX();
		}
	}
	return maxX+betweenPipes;
}
	
}
