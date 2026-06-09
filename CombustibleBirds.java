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
import java.util.ArrayList;
import java.util.Random;
import javafx.stage.Screen;
import javafx.scene.shape.Line;


public class CombustibleBirds extends Application {
    Random rand = new Random();
    long timer;
    double birdCooldown = 5*1e9;
    double bird2Cooldown = 10*1e9;
    Pane pane = new Pane();

    // THREE PARALLEL LISTS: Every bird shares the same index across these lists
    ArrayList<Circle> birdCircles = new ArrayList<>();
    ArrayList<Double> birdX = new ArrayList<>();
    ArrayList<Double> birdY = new ArrayList<>();

    // THREE PARALLEL LISTS: Every bird shares the same index across these lists
    ArrayList<Circle> bird2Circles = new ArrayList<>();
    ArrayList<Double> bird2X = new ArrayList<>();
    ArrayList<Double> bird2Y = new ArrayList<>();

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

                if (now - timer >= birdCooldown) {
                    Point2D spawnPoint = randScreenPoint(wnd.getWidth(), wnd.getHeight(), 75);
                    spawnBird(spawnPoint, mousePos);
                    timer = now;
                }

                for (int i = 0; i < birdCircles.size(); i++) {
                    Circle c = birdCircles.get(i);
                    double dx = birdX.get(i);
                    double dy = birdY.get(i);
                    
                    // Move the bird by its calculated step direction
                    c.setCenterX(c.getCenterX() + dx);
                    c.setCenterY(c.getCenterY() + dy);

                    // Clean up birds that exit the scene to prevent memory lag
                    if (c.getCenterX() < -100 || c.getCenterX() > (wnd.getWidth() + 100) || c.getCenterY() < -100 || c.getCenterY() > (wnd.getHeight() + 100)) {
                        
                        pane.getChildren().remove(c);
                        
                        // Remove from all three lists to keep indices aligned
                        birdCircles.remove(i);
                        birdX.remove(i);
                        birdY.remove(i);
                        
                        // Decrease index so we don't skip the next item after removal
                        i--; 
                    }
                }
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

    public void spawnBird(Point2D spawn, Point2D targetPosition) {
        Circle bird = new Circle();
        double rB = 37.5;

        bird.setCenterX(spawn.getX());
        bird.setCenterY(spawn.getY());
        bird.setRadius(rB);
        bird.setFill(Color.RED); 

        pane.getChildren().add(bird);

        // Calculate direction vector toward the player position at spawn time
        double mX = targetPosition.getX() - spawn.getX();
        double mY = targetPosition.getY() - spawn.getY();
        double distance = Math.sqrt(mX * mX + mY * mY);

        // Define movement speed per frame 
        double maxSpeed = 3;
        double dx = (mX / distance) * maxSpeed;
        double dy = (mY / distance) * maxSpeed;

        // Save attributes to the parallel lists
        birdCircles.add(bird);
        birdX.add(dx);
        birdY.add(dy);
    }
}