package nnhw1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

//import nnhw1.Parameter;
//import nnhw1.Canvas;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class nnhw1 extends Application {

	public Canvas canvas;
	public Parameter parameter;

	public ArrayList<float[]> inputArray = new ArrayList<float[]>();
	public ArrayList<float[]> trainArray = new ArrayList<float[]>();
	public ArrayList<float[]> testArray = new ArrayList<float[]>();

	public float[] weight;// for only 2 dimemtion
	
	public int dataRatio = 200;
	
	private int sortedNewDesire = 0;
	private float layoutX = 600;
	private float layoutY = 600;
	private float eastxbound = layoutX/2;
	private float westxbound = (layoutX/2)*-1;


	private float studyRate;
	private float threshold;
	private int looptimes;
	private float x0 = -1;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Neural Network homework 1");
		BorderPane nnhw1Pane = new BorderPane();

		parameter = new Parameter();
		canvas = new Canvas(layoutX, layoutY);

		nnhw1Pane.setRight(canvas);
		nnhw1Pane.setLeft(parameter);

		parameter.chooseFile.setOnMouseClicked(event -> {
			inputArray = new ArrayList<float[]>();
			trainArray = new ArrayList<float[]>();
			testArray = new ArrayList<float[]>();

			// maybe can pack to a function call repaint
			canvas.repaint();
			
			try {
				inputFileChoose(null);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.exit(0);
				e1.printStackTrace();
			}

			normalizeData(inputArray);
			desireNormalize(inputArray);

			Collections.shuffle(inputArray);
			separateData(inputArray);
			
			drawDataPoints(trainArray);


		});

		parameter.doAlgo.setOnMouseClicked(event -> {

			getInitialParameter();

			getInitialWeight();

			doAlgo(trainArray);

		});

		Scene primaryScene = new Scene(nnhw1Pane);
		primaryStage.setScene(primaryScene);
		primaryStage.setResizable(false);
		primaryStage.show();

	}

	public void desireNormalize(ArrayList<float[]> array){
		int reference = (int) array.get(0)[array.get(0).length - 1];
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i)[array.get(i).length - 1] == reference) {
				array.get(i)[array.get(i).length - 1] = 1;
			} else {
				array.get(i)[array.get(i).length - 1] = -1;
			}
		} // 假裝是對的有時候要改 -1 1 記註記住
	}
	
	public void getInitialWeight() {
		
		weight = new float[trainArray.get(0).length - 1];

		Random rand = new Random();
		for (int i = 0; i < weight.length; i++) {
			if (Math.random() > 0.5) {
				weight[i] = rand.nextFloat() + 0f;
			} else {
				weight[i] = rand.nextFloat() - 1f;
			}
		}
	}

	public void doAlgo(ArrayList<float[]> array) {
		int correctFlag = 0;
		int correctCount = 0;
		int dataAmount = trainArray.size();
		int xn=0;
		
		while(looptimes != 0){
			float sum = 0f;
			for(int i=0;i<array.get(xn).length-1;i++){
				sum += weight[i]*array.get(xn)[i];
			}
			sum += x0*threshold;
			
			sum = Math.signum(sum);
			
			if (sum != trainArray.get(xn)[trainArray.get(xn).length - 1] && sum > 0) {
				for (int w = 0; w < (trainArray.get(xn).length - 1); w++) {// 要用的只有前兩個
																			// 最後一個是desire
					weight[w] -= studyRate * trainArray.get(xn)[w];
				}
				threshold -= studyRate * x0;
				correctCount = 0;
				
			} else if (sum != trainArray.get(xn)[trainArray.get(xn).length - 1] && sum < 0) {
				for (int w = 0; w < (trainArray.get(0).length - 1); w++) {// 要用的只有前兩個
																			// 最後一個是desire
					weight[w] += studyRate * trainArray.get(xn)[w];
				}
				threshold += studyRate * x0;
				correctCount = 0;
				
			} else {
				correctCount++;
			}
			
			if (correctCount == dataAmount - 1) {
				correctFlag = 1;
				break;
			} // 當趨近於收斂時給一個correctflag = 1 讓後面break之後的印可以印正確的資訊

			if (xn == dataAmount - 1) {
				xn = 0;
			} else {
				xn++;
			} // xn 歸零重頭開始算
			looptimes--;// looptimes countdown
			
		}
		
		flagdecide(correctFlag);// call function to decide

		canvas.repaint();
		drawDataPoints(trainArray);

		float line1EndY = getY(weight[0], weight[1], threshold, eastxbound);
		float line2EndY = getY(weight[0], weight[1], threshold, westxbound);
		
		
		Line classifyLine = new Line();
		classifyLine.setStroke(Color.CHARTREUSE);
		classifyLine.setStrokeWidth(2);
		classifyLine.setStartX(layoutX);
		classifyLine.setStartY(eastxbound+(-line1EndY*dataRatio));
		classifyLine.setEndX(0);
		classifyLine.setEndY(eastxbound+(-line2EndY*dataRatio));
		canvas.getChildren().add(classifyLine);

	}
	
	public void flagdecide(int correctFlag) {
		if (correctFlag == 1) {
			System.out.println("-----------------------");
			System.out.println("Find a good solution");
			System.out.println("-----------------------");
		} else {
			System.out.println("-----------------------");
			System.out.println("Sorry, out of looptimes");
			System.out.println("-----------------------");
		}
	}

	public float getY(float w0, float w1, float threshold, float x) {
		float y;
		y = (threshold / w1) - (w0 / w1) * (x / dataRatio);
		return y;
	}

	public void separateData(ArrayList<float[]> tempArray) {
		int totalamount = tempArray.size();
		int tocalamount = Math.round((float) (totalamount * 2) / 3);
		int totestamount = totalamount - tocalamount;

		while (tocalamount != 0) {
			trainArray.add(tempArray.get(0));
			tempArray.remove(0);
			tocalamount--;
		}
		System.out.println("train amount : " + trainArray.size());
		while (totestamount != 0) {
			testArray.add(tempArray.get(0));
			tempArray.remove(0);
			totestamount--;
		}
		System.out.println("test amount : " + testArray.size());
	}


	public void drawDataPoints(ArrayList<float[]> array) {

		for (int j = 0; j < array.size(); j++) {
			if (array.get(j)[array.get(j).length - 1] == -1) {
				Circle circle = new Circle();
				circle.setCenterX(array.get(j)[0] * dataRatio + (layoutX / 2));
				circle.setCenterY((-array.get(j)[1]) * dataRatio + (layoutY / 2));
				circle.setRadius(2);
				circle.setFill(Color.RED);
				canvas.getChildren().add(circle);
			}
			else{
				Circle circle = new Circle();
				circle.setCenterX(array.get(j)[0] * dataRatio + (layoutX / 2));
				circle.setCenterY((-array.get(j)[1]) * dataRatio + (layoutY / 2));
				circle.setRadius(2);
				circle.setFill(Color.BLUE);
				canvas.getChildren().add(circle);

			}
		}
	}

	private void normalizeData(ArrayList<float[]> array) {
		/*
		 * idea: find the biggest number(no matter positive or negative ,set it
		 * as denominator
		 */
		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;

		for (int i = 0; i < array.size(); i++) {
			if (Math.abs(array.get(i)[0]) > maxX) {
				maxX = Math.abs(array.get(i)[0]);
			}
			if (Math.abs(array.get(i)[1]) > maxY) {
				maxY = Math.abs(array.get(i)[1]);
			}
		}
		for (int i = 0; i < array.size(); i++) {
			array.get(i)[0] /= maxX;
			array.get(i)[1] /= maxY;
		}
	}

	public void inputFileChoose(String[] args) throws IOException {
		/*
		 * show a file stage for choose file
		 */

		Stage fileStage = new Stage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.setInitialDirectory(new File("src/dataset"));

		File file = fileChooser.showOpenDialog(fileStage);
		// System.out.println(file);

		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);// 在br.ready反查輸入串流的狀況是否有資料

		parameter.chooseFile.setText(file.getName());

		String txt;
		while ((txt = br.readLine()) != null) {
			/*
			 * If there is space before split(), it will cause the error So, we
			 * could to use trim() to remove the space at the beginning and the
			 * end. Then split the result, which doesn't include the space at
			 * the beginning and the end. "\\s+" would match any of space, as
			 * you don't have to consider the number of space in the string
			 */
			String[] token = txt.trim().split("\\s+");// <-----背起來
			// String[] token = txt.split(" ");//<-----original split
			float[] token2 = new float[token.length];// 宣告float[]

			try {
				for (int i = 0; i < token.length; i++) {
					token2[i] = Float.parseFloat(token[i]);
				} // 把token(string)轉乘token2(float)
				inputArray.add(token2);// 把txt裡面內容先切割過在都讀進array內
			} catch (NumberFormatException ex) {
				System.out.println("Sorry Error...");
			}
		}
		fr.close();// 關閉檔案
	}

	public void printArrayData(ArrayList<float[]> showArray) {

		for (int i = 0; i < showArray.size(); i++) {
			for (int j = 0; j < showArray.get(i).length; j++) {
				System.out.print(showArray.get(i)[j] + "\t");
			}
			System.out.println("");
		}
		System.out.println("");
	}

	public void getInitialParameter() {
		this.setStudyRate(this.parameter.getStudyRate());
		this.setLooptimes(this.parameter.getLooptimes());
		this.setThreshold(this.parameter.getThreshold());
	}

	public void setStudyRate(float studyRate) {
		this.studyRate = studyRate;
	}

	public void setLooptimes(int looptimes) {
		this.looptimes = looptimes;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
}
