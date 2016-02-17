package nnhw1;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Canvas extends Pane {

	public Canvas(float layoutX,float layoutY) {
		this.setPrefSize(layoutX, layoutY);
		this.setBackground(new Background(new BackgroundFill(Color.GAINSBORO, null, null)));
		drawCoordinateLine();
	}

	public void drawCoordinateLine() {

		Line xLine = new Line();
		xLine.setStroke(Color.BLACK);
		xLine.setStartX(300);
		xLine.setStartY(0);
		xLine.setEndX(300);
		xLine.setEndY(600);

		Line yLine = new Line();
		yLine.setStroke(Color.BLACK);
		yLine.setStartX(0);
		yLine.setStartY(300);
		yLine.setEndX(600);
		yLine.setEndY(300);
		this.getChildren().addAll(xLine, yLine);
	}

}
