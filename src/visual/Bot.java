package visual;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.opencv.core.*;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class Bot {

	private static final double maxThreshHold = .35;
	private static final double threshHold = 50;
	private Robot robot;
	private Rectangle screen;

	private final boolean doOutPut = false;


	public static void main(String[] args) throws InterruptedException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// Mat img = Imgcodecs.imread("res/img.png"); 
		// Mat templ = Imgcodecs.imread("res/templ.png");
		// templateMatch(img, templ, Imgproc.TM_CCOEFF, false);
		Bot b = new Bot();
		b.botRunner();
	}

	public Bot() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		this.screen = new Rectangle(screen);
	}

	public void botRunner() throws InterruptedException {
		//Thread.sleep(1000);
		while (true) {
			System.out.println("Running");
			BufferedImage screenImg = robot.createScreenCapture(screen);
			
			Mat screenMat = bufferedImageToMat(screenImg);
			Mat waterCoin = Imgcodecs.imread("res/water_coins.png");
			waterShed(waterCoin);
			//cannyEdgeDetect(screenMat);
//			Map<Double, Point> match = templateMatchBest(screenMat, scuttle,
//					Imgproc.TM_CCOEFF_NORMED, doOutPut);
//			if (match.size() != 0) {
//				Double[] keys = match.keySet().toArray(
//						new Double[match.keySet().size()]);
//				Double bestMatch = keys[keys.length - 1];
//					Point bestFit = match.get(bestMatch);
//					System.out.println("Found match at " + bestFit.toString());
//				
//			}
			System.exit(0);
		}
	}
	
	public void showImage(Mat m) {
		showImage(m, "");
	}
	
	public void showImage(Mat m, String title) {
		BufferedImage img = new BufferedImage(m.cols(), m.rows(), BufferedImage.TYPE_3BYTE_BGR);
		if(m.channels() == 1) {
			for(int x = 0; x < m.cols(); x++) {
				for(int y = 0; y < m.rows(); y++) {
					double[] data = m.get(y, x);
					img.setRGB(x, y, new Color((int)data[0], (int)data[0], (int)data[0]).getRGB());
				} 
			}
		} else if(m.channels() == 3) {
			for(int x = 0; x < m.cols(); x++) {
				for(int y = 0; y < m.rows(); y++) {
					double[] data = m.get(y, x);
					img.setRGB(x, y, new Color((int)data[2], (int)data[1], (int)data[0]).getRGB());
				} 
			}
		}
		JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(img)), title , 1);
	}
	
	public void countourDetection(Mat img) {
		Mat shifted = new Mat();
	
		Imgproc.pyrMeanShiftFiltering(img, shifted, 21, 21);
		//showImage(shifted);
		Mat gray = new Mat();

		Imgproc.cvtColor(shifted, gray, Imgproc.COLOR_BGR2GRAY);
		Mat thresh = new Mat();
		Imgproc.threshold(gray, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
		//showImage(thresh);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(thresh.clone(), contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		System.out.println("found " + contours.size() + " countous");
		Mat out = thresh.clone();
		Imgproc.cvtColor(out, out, Imgproc.COLOR_GRAY2BGR);
		for(MatOfPoint m:contours) {
			//showImage(m);
			MatOfPoint2f newM = new MatOfPoint2f(m.toArray());
			Point p = new Point(0, 0);
			float[] raduis = new float[1];
			Imgproc.minEnclosingCircle(newM, p ,raduis);
			System.out.println("Point: " + p.toString() + " raduis: " + Arrays.toString(raduis));
			Imgproc.circle(out, p, (int) raduis[0], new Scalar(0, 255, 0), 5);
		}

		showImage(out);

	
	}
	
	
	public void waterShed(Mat img) {
		showImage(img, "original img");
		Mat shifted = new Mat();
		Imgproc.pyrMeanShiftFiltering(img, shifted, 1, 50);
		showImage(shifted, "mean shift filter");
		Mat gray = new Mat();
		ArrayList<Mat> channels = new ArrayList<Mat>();
		Core.split(shifted, channels);
		gray = channels.get(1);
		//Imgproc.cvtColor(shifted, gray, Imgproc.COLOR_BGR2GRAY);
		Core.normalize(gray, gray, 0, 255, Core.NORM_MINMAX);
		Mat thresh = new Mat();
		showImage(gray, "gray scale");
		Imgproc.threshold(gray, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

		Mat kernel = Mat.ones(3, 3, CvType.CV_8U);
		Mat openeing = new Mat();
		//opening = cv2.morphologyEx(thresh,cv2.MORPH_OPEN,kernel, iterations = 2)
		
		Imgproc.morphologyEx(thresh, openeing, Imgproc.MORPH_OPEN, kernel, new Point(-1, -1), 3); // changed from morhp_open
		Mat background = new Mat();
		Imgproc.dilate(openeing, background, kernel, new Point(-1, -1), 3);
		showImage(background, "background");
		
		Mat foreground = new Mat();
		Imgproc.distanceTransform(thresh, foreground, Imgproc.CV_DIST_L1, Imgproc.CV_DIST_MASK_PRECISE);
		Core.normalize(foreground, foreground, 0, 255, Core.NORM_MINMAX);
		//showImage(dist);
		
		
		MinMaxLocResult max = Core.minMaxLoc(foreground);
		Imgproc.threshold(foreground, foreground, .7 * max.maxVal, max.maxVal, Imgproc.THRESH_BINARY);
		showImage(foreground, "foreground");
		//showImage(foreground);
		
		Mat unknow = new Mat();
		foreground.convertTo(foreground, CvType.CV_8U);
		Core.subtract(background, foreground, unknow);
		showImage(unknow, "unknown"); 
		
		Mat labels = new Mat();
		foreground.convertTo(foreground, CvType.CV_8U);
		Imgproc.connectedComponents(foreground, labels);
		for(int x = 0; x < labels.cols(); x++) {
			for(int y = 0; y < labels.rows(); y++) {
				labels.put(y, x, labels.get(y, x)[0] + 1);
				if(unknow.get(y, x)[0] == 255) {
					labels.put(y, x, 0);
				}
			}
		}
		Imgproc.watershed(img, labels);
		for(int x = 0; x < labels.cols(); x++) {
			for(int y = 0; y < labels.rows(); y++) {
				if(labels.get(y, x)[0] == -1) {
					img.put(y, x, 0, 255, 0);
				}
			}
		}
		showImage(img, "final");
		
	}
	
	public void printMat(Mat m) {
		for(int y = 0; y < m.rows(); y++) {
			for(int x = 0; x < m.cols(); x++) {
				System.out.print(Arrays.toString(m.get(y, x)));
			}
			System.out.println();
		}
	}
	
	public Mat cannyEdgeDetect(Mat img) {
		
		Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
		//Imgproc.blur(img, img, new Size(3, 3));
		Imgproc.Canny(img, img, threshHold, threshHold * 3, 3, false);
		Mat dest = new Mat();
		Core.add(dest, Scalar.all(0), dest);
		img.copyTo(dest, img);
		Imgcodecs.imwrite("res/out.png", img);
		return dest;

	}

	public Mat bufferedImageToMat(BufferedImage img) {
		BufferedImage byteImage = new BufferedImage(img.getWidth(),
				img.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		byteImage.getGraphics().drawImage(img, 0, 0, null);
		// JOptionPane.showMessageDialog(null, new JLabel(new
		// ImageIcon(byteImage)));
		byte[] pixels = ((DataBufferByte) byteImage.getRaster().getDataBuffer())
				.getData();
		Mat m = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC3);
		// m.put(0, 0, pixels);
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				int index = 3 * (y * img.getWidth() + x);
				m.put(y, x, new byte[] { pixels[index], pixels[index + 1],
						pixels[index + 2] });
			}
		}

		return m;
	}
	
	public static Map<Double, Point> templateMatchBest(Mat img, Mat templ,
			int match_method, boolean writeToOut) {
		
		int result_cols = img.cols() - templ.cols() + 1;
		int result_rows = img.rows() - templ.rows() + 1;
		Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		MinMaxLocResult max = Core.minMaxLoc(result);
		System.out.println("Max val: " + max.maxVal);
		Map<Double, Point> matchesMap = new TreeMap<Double, Point>();
		if (max.maxVal > maxThreshHold) {
			matchesMap.put(max.maxVal, max.maxLoc);
		}
		return matchesMap;
	}

	public static Map<Double, Point> templateMatch(Mat img, Mat templ,
			int match_method, boolean writeToOut) {
		// System.out.println("types: " + img.type() + " " + templ.type());

		// / Create the result matrix
		int result_cols = img.cols() - templ.cols() + 1;
		int result_rows = img.rows() - templ.rows() + 1;
		Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		MinMaxLocResult mmr = Core.minMaxLoc(result);
		System.out.println("Max val: " + mmr.maxVal);
		Map<Double, Point> matchesMap = new TreeMap<Double, Point>();
		if (mmr.maxVal > maxThreshHold) {
			Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1,
					new Mat());

			// Imgcodecs.imwrite("res/out.png", result);
			// System.exit(0);
			// / Localizing the best match with minMaxLoc

			ArrayList<Point> matches = new ArrayList<Point>();
			
			for (int y = 0; y < result.rows(); y++) {
				for (int x = 0; x < result.cols(); x++) {
					// System.out.print(Arrays.toString(result.get(y, x)) +
					// " ");

					if (result.get(y, x)[0] >= threshHold) {

						matches.add(new Point(x, y));
						matchesMap.put(result.get(y, x)[0], new Point(x, y));
					}
				}

			}
			System.out.println("found size: " + matchesMap.size());
			// System.out.println(matchesMap.toString());

			// Point matchLoc;
			// if (match_method == Imgproc.TM_SQDIFF || match_method ==
			// Imgproc.TM_SQDIFF_NORMED) {
			// matchLoc = mmr.minLoc;
			// } else {
			// matchLoc = mmr.maxLoc;
			// }
			// / Show me what you got

			if (writeToOut) {
				for (Point p : matches) {
					Imgproc.rectangle(img, p, new Point(p.x + templ.cols(), p.y
							+ templ.rows()), new Scalar(0, 255, 0));
				}
				Imgcodecs.imwrite("res/out.png", img);

			}
		}
		return matchesMap;

	}

}