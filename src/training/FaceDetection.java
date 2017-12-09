package training;


/**.
 * The program implementing a controller:
 * The controller for our application, where the application logic is implemented.
 * It handles the button for starting/stopping the camera and the
 * acquired video stream.
 * @author checkoutwithyourface group members
 * @see https://github.com/opencv-java/face-detection
 */

/**.
 * Libraries Imported
 * (1) JavaFX - handles the application layout and functioning
 * (2) OpenCV - Java version of the C++ computer vision library
 */


import java.io.IOException;

import org.opencv.core.Core;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

/**
 * The main class: Face Detection
 */


public class FaceDetection extends Application {

	/**.
	 * Main function to load the program
	 * @param args
	 */
	
	public static void main(String[] args) {
		// Load the native OpenCV library into this program
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		// Load the FXML resource
		FXMLLoader loader = new FXMLLoader(getClass().getResource("FaceDetection.fxml"));
		
		// Store the root element for use by controllers
		BorderPane root = (BorderPane) loader.load();
		root.setStyle("-fx-background-color: blue;");
		
		//Create a scene
		Scene scene = new Scene(root, 750, 550);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		// Create the stage with the given title and the scene
		primaryStage.setTitle("Face Tracking");
		primaryStage.setScene(scene);
		
		// Show the GUI
		primaryStage.show();
		
		// Handle application closing properly
		FaceDetectionController controller = loader.getController();
		controller.init();
		primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
			public void handle(WindowEvent window) {
				controller.setClosed();
			}
		}));
	}
}
