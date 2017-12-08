package training;

import java.io.IOException;

import org.opencv.core.Core;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

public class FaceDetection extends Application {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("FaceDetection.fxml"));
		BorderPane root = (BorderPane) loader.load();
		root.setStyle("-fx-background-color: blue;");
		Scene scene = new Scene(root, 750, 550);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setTitle("Face Tracking");
		primaryStage.setScene(scene);
		primaryStage.show();
		FaceDetectionController controller = loader.getController();
		controller.init();
		primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
			public void handle(WindowEvent window) {
				controller.setClosed();
			}
		}));
	}
}
