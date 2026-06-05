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
import javafx.scene.shape.Line;


public class CombustibleBirds extends Application {
    Random rand = new Random();
    long timer;
    double birdCooldown = 5*1e9;

    public static void main(String[] args) {
        launch();
    }

    public void start(Stage wnd) {
        wnd.setTitle("Combustible Birds!");
        double w = 700;
        double h = 700;

        timer = System.nanoTime();

        wnd.setWidth(w);
        wnd.setHeight(h);

        Rectangle rectangle = new Rectangle();
        double wR = 50;
        double hR = 50;

        rectangle.setX(w/2 - wR/2);
        rectangle.setY(h/2 - hR/2);
        rectangle.setWidth(wR);
        rectangle.setHeight(hR);

        Rotate rotate = new Rotate();
        rectangle.getTransforms().add(rotate);

        Pane pane = new Pane();
        pane.getChildren().add(rectangle);

        Scene scene = new Scene(pane/*, Color.SKYBLUE*/);
        wnd.setScene(scene);

        Robot robot = new Robot();

        AnimationTimer loop = new AnimationTimer() {
            public void handle(long now) {
                double mX = robot.getMouseX() - wnd.getX();
                double mY = robot.getMouseY() - wnd.getY();
                Point2D mousePos = new Point2D(mX, mY);

                rectangle.setX(mX - 25);
                rectangle.setY(mY - 40);

                if (timer - now >= birdCooldown) {
                    bird(randScreenPoint(wnd.getX(), wnd.getY(), 75));
                    timer = now;
                }

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

    public Point2D randScreenPoint(double width, double height, double buffer) {
        int side = rand.nextInt(4);
        double x = 0;
        double y = 0;

        if (side == 0) {
            x = rand.nextDouble() * width;
            y = -buffer;
        }
        if (side == 1) {
            x = rand.nextDouble() * width;
            y = height + buffer;
        }
        if (side == 2) {
            x = -buffer;
            y = rand.nextDouble() * height;
        }
        if (side == 3) {
            x = width + buffer;
            y = rand.nextDouble() * height;
        }
        
        return new Point2D(x, y);
    }

    public Circle bird(Point2D spawn, Point2D mousePostion) {
        Circle bird = new Circle();
        double rB = 37.5;

        bird.setCenterX(spawn.getX());
        bird.setCenterY(spawn.getY());
        bird.setRadius(rB);

        //double mX = mousePostion.getX();
        //double mY = mousePostion.getY();

        double m = (mousePostion.getY() - spawn.getY())/(mousePostion.getX() - spawn.getX());

        //Line line = new Line(spawn.getX(), spawn.getY(), mousePostion.getX(), mousePostion.getY());

        return bird;
    }
}