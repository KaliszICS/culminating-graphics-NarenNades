import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.animation.AnimationTimer;

public class HelloFX extends Application {

    @Override
    public void start(Stage stage) {
        Rectangle player = new Rectangle(75, 75);
        Pane game = new Pane();
        game.getChildren().add(player);
        Scene scene = new Scene(game, 640, 500);
        stage.setScene(scene);
        stage.show();

        AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.S) {
                player.setY(player.getY() + 10);
            }
            if (event.getCode() == KeyCode.A) {
                player.setX(player.getX() - 10);
            }
            if (event.getCode() == KeyCode.W) {
                player.setY(player.getY() - 10);
            }
            if (event.getCode() == KeyCode.D) {
                player.setX(player.getX() + 10);
            }
        });
    }
        };
        timer.start();
    }

    public static void main(String[] args) {
        launch();
    }

}