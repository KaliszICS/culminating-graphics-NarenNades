import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.animation.AnimationTimer;
import javafx.scene.robot.Robot;
import javafx.geometry.Point2D;
import java.lang.Math;
import javafx.scene.transform.Rotate;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import java.util.Random;
import javafx.stage.Screen;


public class CombustibleBirds extends Application {
    //int maxCoin = 3;

    public static void main(String[] args) {
        launch();
    }

    public void start(Stage wnd) {
        Random r = new Random();

        wnd.setTitle("Combustible Birds!");
        double w = 500;
        double h = 500;

        wnd.setWidth(w);
        wnd.setHeight(h);

        Rectangle rectangle = new Rectangle();
        double wR = 100;
        double hR = 100;

        rectangle.setX(w/2 - wR/2);
        rectangle.setY(h/2 - hR/2);
        rectangle.setWidth(wR);
        rectangle.setHeight(hR);

        Rotate rotate = new Rotate();
        rectangle.getTransforms().add(rotate);

        Pane pane = new Pane();
        pane.getChildren().add(rectangle);
        //pane.getChildren().add(cloud());

        Scene scene = new Scene(pane/*, Color.SKYBLUE*/);
        wnd.setScene(scene);

        Robot robot = new Robot();

        AnimationTimer loop = new AnimationTimer() {
            public void handle(long now) {
                double mX = robot.getMouseX() - wnd.getX();
                double mY = robot.getMouseY() - wnd.getY();

                rectangle.setX(mX - 50);
                rectangle.setY(mY - 60);

                /*double dx = mX - w/2;
                double dy = mY - h/2;

                double angle = Math.toDegrees(Math.atan2(dy, dx));

                rotate.setPivotX(w/2);
                rotate.setPivotY(h/2);
                rotate.setAngle(angle);*/
            }
        };

        loop.start();
        wnd.show();
    }

    /*r.nextDouble(Screen.getPrimary().getBounds().getWidth()), r.nextDouble(Screen.getPrimary().getBounds().getHeight())*/

    /*public static Circle coin(double x, double y, int maxCoin) {
        int coinCount = 0;
        if (!(coinCount == maxCoin)) {
            Circle coin = new Circle(x,y,25);
            coinCount++;
        }
        return coin;
    }
    /*public static Shape[] cloud() {
        Circle baseLeft = new Circle(100, 150, 40);
        Circle topLeft = new Circle(130, 110, 50);
        Circle topRight = new Circle(180, 100, 60);
        Circle baseRight = new Circle(230, 140, 45);
        Circle bottomCenter = new Circle(165, 150, 45);

        Shape cloud = Shape.union(baseLeft, topLeft);
        cloud = Shape.union(cloud, topRight);
        cloud = Shape.union(cloud, baseRight);
        cloud = Shape.union(cloud, bottomCenter);

        Shape[] clud = new Shape[5];

        cloud.setFill(Color.WHITE);

        return clud;
    }*/

}