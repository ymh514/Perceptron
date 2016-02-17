package nnhw1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
//import nnhw1.Parameter;
//import nnhw1.Canvas;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class nnhw1 extends Application {

	public Canvas canvas;
	public Parameter parameter;

	public ArrayList<float[]> inputArray = new ArrayList<float[]>();
	public ArrayList<float[]> sortedArray = new ArrayList<float[]>();
	public ArrayList<float[]> trainArray = new ArrayList<float[]>();
	public ArrayList<float[]> testArray = new ArrayList<float[]>();
	public ArrayList<Color> colorArray = new ArrayList<Color>();

	public int sortedNewDesire = 0;
	public int classAmount;
	public int dataRatio = 200;
	public float layoutX = 600;
	public float layoutY = 600;

	private float studyRate;
	private float threshold;
	private int looptimes;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Neural Network homework 1");
		BorderPane nnhw1Pane = new BorderPane();

		parameter = new Parameter();
		canvas = new Canvas(layoutX,layoutY);

		nnhw1Pane.setRight(canvas);
		nnhw1Pane.setLeft(parameter);

		parameter.chooseFile.setOnMouseClicked(event ->{
			inputArray = new ArrayList<float[]>();
			sortedArray = new ArrayList<float[]>();
			trainArray = new ArrayList<float[]>();
			testArray = new ArrayList<float[]>();
			
			// maybe can pack to a function call repaint
			canvas.getChildren().remove(0,canvas.getChildren().size());
			canvas.drawCoordinateLine();
			
			try {
				inputFileChoose(null);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.exit(0);
				e1.printStackTrace();
			}
			
			normalizeData(inputArray);
			sortInputArray(inputArray);
			classAmount = sortedNewDesire + 1;
//			System.out.println(classAmount);
			colorType();
			drawDataPoints();

			Collections.shuffle(sortedArray);
			separateData(sortedArray);

		});
		
		parameter.doAlgo.setOnMouseClicked(event ->{
			getInitialParameter();
			
		});
		
		Scene primaryScene = new Scene(nnhw1Pane);
		primaryStage.setScene(primaryScene);
		primaryStage.setResizable(false);
		primaryStage.show();

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

	public void colorType() {
		/*
		 * fist 3 class is r b g , if there's more class random generate color
		 */
		
		colorArray.add(Color.RED);
		colorArray.add(Color.BLUE);
		colorArray.add(Color.GREEN);


		if (classAmount > 3) {
			float colorR = 100;
			float colorG = 100;
			float colorB = 100;

			for (int i = 3; i < classAmount; i++) {

				colorR += 10;
				if (colorR > 255) {
					colorR -= 255;
				}

				colorG += 30;
				if (colorG > 255) {
					colorG -= 255;
				}

				colorB += 40;
				if (colorB > 255) {
					colorB -= 255;
				}

				Color colorType = new Color(colorR, colorG, colorB, 1.0f);
				colorArray.add(colorType);
			}
		}
	}
	
	public void drawDataPoints() {
		
		for (int i = 0; i < classAmount; i++) {
			for (int j = 0; j < sortedArray.size(); j++) {
				if (sortedArray.get(j)[sortedArray.get(j).length - 1] == i) {
					Circle circle = new Circle();
					circle.setCenterX(sortedArray.get(j)[0] * dataRatio + (layoutX / 2));
					circle.setCenterY((-sortedArray.get(j)[1]) * dataRatio + (layoutY / 2));
					circle.setRadius(2);
					circle.setFill(colorArray.get(i));
					canvas.getChildren().add(circle);
				}
			}
		}
	}

	
	private void normalizeData(ArrayList<float[]> array) {
		/*
		 * idea: find the biggest number(no matter positive or negative ,set it
		 * 		 as denominator
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

	public void sortInputArray(ArrayList<float[]> inputArray) {
		/*
		 * 1. set loop times = inputArray's dataamount 2. in while loop we have
		 * to dynamic change loop times cause we had remove some data in the
		 * array to reduce loop times 3. set a variable-standardDesire is mean
		 * the first data's desire , then use it to check one by one ,if found
		 * someone is as same as the standardDesire, put this data to
		 * sortedArray, so on ,we can get a sorted array which's desire is from
		 * 1 to number of class 4. everytime move a item to sortedArray , raise
		 * iRestFlag and set i to 0, then it will run loop from beginning 5.
		 * when inputarray left only 1 item must set as -1, or the last data's
		 * desire will be set one more number
		 * 
		 */

		int iRestFlag = 0;
		sortedNewDesire = 0;
//		System.out.println("--------- Start sort ---------");

		whileloop: while (true) {

			// set the first one's desire as standard
			int standardDesire = (int) inputArray.get(0)[inputArray.get(0).length - 1];
//			System.out.println("Now the standartDesire is  : " + standardDesire);

			for (int i = 0; i < inputArray.size(); i++) {
				if (iRestFlag == 1) {
					i = 0;
				}
				if ((int) inputArray.get(i)[inputArray.get(i).length - 1] == standardDesire) {
					inputArray.get(i)[inputArray.get(i).length - 1] = sortedNewDesire;
					sortedArray.add(inputArray.get(i));
					inputArray.remove(i);
					iRestFlag = 1;
				} else {
					iRestFlag = 0;
				}
				if (inputArray.size() == 1) {// the last data need set i=-1 to
												// prevent after forloop's i++
					i = -1;
				}
			}
			if (inputArray.size() == 0) {
//				System.out.println("--------- Sort done! ---------");
//				System.out.println("");
				break whileloop;
			} else {
				sortedNewDesire++;// count desire
			}
		}
//		System.out.println("The max sorted desire : " + sortedNewDesire);
	}
	
	public void inputFileChoose(String[] args) throws IOException {
		/*
		 * show a file stage for choose file
		 */
		
		Stage fileStage = new Stage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.setInitialDirectory(new File("D:\\NCU 1041\\NN\\dataset1"));

		File file = fileChooser.showOpenDialog(fileStage);
//		System.out.println(file);

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
