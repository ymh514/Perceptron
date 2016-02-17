package nnhw1;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class Parameter extends VBox {
	protected Button chooseFile = new Button("Choose File");
	protected Button doAlgo = new Button("Go !");
	protected Text studyRateMsg = new Text("Studyrate : ");
	protected Text looptimesMsg = new Text("Looptimes : ");
	protected Text thresholdMsg = new Text("Threshold : ");
	protected TextField studyRateText;
	protected TextField looptimesText;
	protected TextField thresholdText;

	public Parameter() {
		this.setSpacing(10);
		this.setPadding(new Insets(15, 15, 15, 15));

		studyRateText = new TextField("0.8");
		looptimesText = new TextField("5000");
		thresholdText = new TextField("-1");

		this.getChildren().addAll(chooseFile, studyRateMsg, studyRateText, looptimesMsg, looptimesText, thresholdMsg,
				thresholdText, doAlgo);

	}

	public float getStudyRate() {
		return Float.parseFloat(studyRateText.getText());
	}

	public float getThreshold() {
		return Float.parseFloat(thresholdText.getText());
	}

	public int getLooptimes() {
		return Integer.parseInt(looptimesText.getText());
	}

}
