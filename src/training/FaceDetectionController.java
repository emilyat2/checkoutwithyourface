package training;

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

public class FaceDetectionController {
	@FXML
	private Button cameraButton;
	@FXML
	private Button saveButton;
	@FXML
	private TextField name;
	@FXML
	private ImageView originalFrame;
	@FXML
	private CheckBox haarClassifier; // for human faces
	@FXML
	private CheckBox lbpClassifier; // for cats and stuff
	
	// Video input timer and capture
	private ScheduledExecutorService timer;
	private VideoCapture capture;
	private boolean cameraActive;
	
	private CascadeClassifier faceCascade;
	private int absoluteFaceSize;

	// start the controller
	protected void init() {
		this.capture = new VideoCapture();
		this.faceCascade = new CascadeClassifier();
		this.absoluteFaceSize = 0;
		// set frame
		originalFrame.setFitWidth(600);
		originalFrame.setPreserveRatio(true);
	}
	
	// Implementation of start button in FXML
	@FXML
	protected void startCamera() {
		if (!this.cameraActive) {
			this.haarClassifier.setDisable(true);
			this.lbpClassifier.setDisable(true);
			this.saveButton.setDisable(false);
			// start the camera
			this.capture.open(0);
			if (!this.capture.isOpened()) {
				this.cameraActive = false;
				System.err.println("Camera Connection Failed");
			} else {
				// update frame with time
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

	@FXML
	protected void save() {
		if (cameraActive) {
			Mat frame = grabFrame();
			Image imageToShow = Utils.mat2Image(frame);
			Imgcodecs.imwrite("Faces/" + name.getText() + ".png", frame );
			updateImageView(originalFrame, imageToShow);
		}
	}
	
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

	@FXML
	protected void haarSelected(Event event) {
		if (this.lbpClassifier.isSelected()) {
			this.lbpClassifier.setSelected(false);
		}
		this.checkboxSelection("resources/haarcascades/haarcascade_frontalface_alt.xml");
	}

	@FXML
	protected void lbpSelected(Event event) {
		if (this.haarClassifier.isSelected()) {
			this.haarClassifier.setSelected(false);
		}
		this.checkboxSelection("resources/lbpcascades/lbpcascade_frontalface.xml");
	}

	private void checkboxSelection(String classifier) {
		this.faceCascade.load(classifier);
		this.cameraButton.setDisable(false);
	}

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

	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}

	protected void setClosed() {
		this.stopAcquisition();
	}
}