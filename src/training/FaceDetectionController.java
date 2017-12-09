package training;

/**.
 * The program to be run.
 * The main class for the JavaFX frame for the face Detection program.
 * This creates the main window, provides a button to switch on the camera
 * and display the video stream.
 * @author checkoutwithyourface group members
 * @see https://github.com/opencv-java/face-detection
 */

/**.
 * Libraries Imported
 * (1) JavaFX - handles the application layout and functioning
 * (2) OpenCV - Java version of the C++ computer vision library
 * (3) Java.util - the Java collections framework
 */


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import utils.Utils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * The main class: FaceDetectionController
 */


public class FaceDetectionController {
	
	// the FXML camera button
	@FXML
	private Button cameraButton;
	
	// the FXML save button
	@FXML
	private Button saveButton;
	
	// the FXML text field that saves the name
	@FXML
	private TextField name;
	
	// the FXML image view
	@FXML
	private ImageView originalFrame;
	
	 // Classifier for human faces
	@FXML
	private CheckBox haarClassifier;
	
	 // Classifier for other stuff like silverware
	@FXML
	private CheckBox lbpClassifier;
	
	// Video input timer and capture
	
	// A timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that realizes the video capture
	private VideoCapture capture;
	// a flag that contains camera state
	private boolean cameraActive;
	
	private CascadeClassifier faceCascade;
	private int absoluteFaceSize;

	/**.
	 * Function to start the controller
	 * 
	 */
	protected void init() {
		// Initialize new objects
		this.capture = new VideoCapture();
		this.faceCascade = new CascadeClassifier();
		this.absoluteFaceSize = 0;
		
		// Set frame
		originalFrame.setFitWidth(600);
		originalFrame.setPreserveRatio(true);
	}
	
	/**.
	 * Function which implements function
	 * of start button in FXML
	 */
	@FXML
	protected void startCamera() {
		
		if (!this.cameraActive) {
			this.haarClassifier.setDisable(true);
			this.lbpClassifier.setDisable(true);
			this.saveButton.setDisable(false);
			
			// Start the camera
			this.capture.open(0);
			if (!this.capture.isOpened()) {
				this.cameraActive = false;
				System.err.println("Camera Connection Failed");
				
			} else {
				// Update frame with time
				this.cameraActive = true;
				Runnable frameGrabber = new Runnable() {
					
					public void run() {
						Mat frame = grabFrame();
						Image imageToShow = Utils.mat2Image(frame);
						updateImageView(originalFrame, imageToShow);
					}
					
				};
				
				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 60, TimeUnit.MILLISECONDS);
				this.cameraButton.setText("Stop Camera");
				this.saveButton.setText("Save");
			}
		} else {
			this.cameraActive = false;
			this.cameraButton.setText("Start Camera");
			this.saveButton.setText("Save");
			this.haarClassifier.setDisable(false);
			this.lbpClassifier.setDisable(false);
			this.saveButton.setDisable(true);
			this.stopAcquisition();
		}
	}
	
	/**.
	 * Function implementing the function
	 * of the save button in FXML
	 */
	@FXML
	protected void save() {
		if (cameraActive) {
			Mat frame = grabFrame();
			Image imageToShow = Utils.mat2Image(frame);
			Imgcodecs.imwrite("Faces/" + name.getText() + ".png", frame );
			updateImageView(originalFrame, imageToShow);
		}
	}
	
	/**.
	 * Function that grabs each frame
	 * @return
	 */
	private Mat grabFrame() {
		Mat frame = new Mat();
		if (this.capture.isOpened()) {
			this.capture.read(frame);
			if (!frame.empty()) {
				this.detectAndDisplay(frame);
			}
		}
		return frame;
	}
	/**.
	 * Function that detects a face
	 * and draws a square around it
	 * @param frame
	 */
	private void detectAndDisplay(Mat frame) {
		MatOfRect faces = new MatOfRect();
		Mat grayFrame = new Mat();
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(grayFrame, grayFrame);
		if (this.absoluteFaceSize == 0) {
			int height = grayFrame.rows();
			if (Math.round(height * 0.2f) > 0) {
				this.absoluteFaceSize = Math.round(height * 0.2f);
			}
		}
		this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
		Rect[] facesArray = faces.toArray();
		for (int i = 0; i < facesArray.length; i++) {
			Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(255, 255, 0), 3);
		}
	}
	
	/**.
	 * Function that implements the function of
	 * the haarClassifier button
	 * @param event
	 */
	@FXML
	protected void haarSelected(Event event) {
		if (this.lbpClassifier.isSelected()) {
			this.lbpClassifier.setSelected(false);
		}
		this.checkboxSelection("resources/haarcascades/haarcascade_frontalface_alt.xml");
	}
	
	/**.
	 * Function that implements the function of
	 * the lbpClassifier button
	 * @param event
	 */
	@FXML
	protected void lbpSelected(Event event) {
		if (this.haarClassifier.isSelected()) {
			this.haarClassifier.setSelected(false);
		}
		this.checkboxSelection("resources/lbpcascades/lbpcascade_frontalface.xml");
	}
	
	/**.
	 * Function implementing another specific function
	 * depending on checkbox selection
	 * @param classifier
	 */
	private void checkboxSelection(String classifier) {
		this.faceCascade.load(classifier);
		this.cameraButton.setDisable(false);
	}
	
	/**.
	 * Function to stop taking in video stream
	 * through camera
	 */
	private void stopAcquisition() {
		if (this.timer != null && !this.timer.isShutdown()) {
			try {
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				System.err.println("Camera Frame Error" + e);
			}
		}
		if (this.capture.isOpened()) {
			this.capture.release();
		}
	}

	/**.
	 * Function to update each frame into
	 * the ImageView
	 * @param view
	 * @param image
	 */
	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}

	protected void setClosed() {
		this.stopAcquisition();
	}
}