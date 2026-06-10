/*
  Author - Naren Nades
  Date created - June 1st, 2026
  Date last modified - June 10th 2026
  File Name - CombustibleBirds.java

*/

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Light.Point;
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
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import java.lang.Math;
import javafx.scene.transform.Rotate;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Random;
import javafx.stage.Screen;
import javafx.scene.shape.Line;


public class CombustibleBirds extends Application {
    Random rand = new Random();

    long lastBirdSpawnTime; // the spawntime of the last bird created
    long lastBird2SpawnTime; // the spawntime of the last bird (the ones chasing user)
    double birdCooldown = 3*1e9; // this is cooldown between spawning birds
    double bird2Cooldown = 7*1e9; // this is cooldown between spawning birds (chasing)

    Pane pane = new Pane();
    Scene scene;
    AnimationTimer loop;

    long gameStartTime;
    int currentScore = 0; 
    Label scoreLabel = new Label("Score: 0");

    Circle coin = new Circle();
    int countCoin = 0;

    boolean isCoinOnScreen = false;

    // THREE PARALLEL LISTS: Every bird shares the same index across these lists
    ArrayList<Circle> birdCircles = new ArrayList<>();
    ArrayList<Double> birdX = new ArrayList<>();
    ArrayList<Double> birdY = new ArrayList<>();

    // THREE PARALLEL LISTS: Every bird (chasing) shares the same index across these lists
    ArrayList<Circle> bird2Circles = new ArrayList<>();
    ArrayList<Double> bird2Speeds = new ArrayList<>();
    ArrayList<Long> bird2SpawnTimes = new ArrayList<>(); // Tracks when each bird was born
    double bird2LifeSpan = 5*1e9; //Lifespan of bird (chasing)

    public static void main(String[] args) {
        launch();
    }

    public void start(Stage wnd) {
        wnd.setTitle("Combustible Birds!");
        double w = 700;
        double h = 700;

        // Menu
        VBox menuLayout = new VBox(20);
        menuLayout.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Combustible Birds!");
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setFont(new Font("Arial", 40));

        Button startButton = new Button("Start Game");
        startButton.setFont(new Font("Arial", 20));
        startButton.setPrefWidth(200);
        startButton.setPrefHeight(50);

        menuLayout.getChildren().addAll(titleLabel, startButton); // menu

        scene = new Scene(menuLayout, w, h); // starts the game at the menu
        wnd.setScene(scene);

        wnd.setWidth(w);
        wnd.setHeight(h);

        Rectangle rectangle = new Rectangle(); // player
        Image Plane = new Image("Plane.png"); // sprite for the player

        ImagePattern imagePatternPlane = new ImagePattern(Plane, 0, 0, 1, 1, true);

        rectangle.setFill(imagePatternPlane);

        double wR = 50;
        double hR = 50;

        rectangle.setX(w/2 - wR/2);
        rectangle.setY(h/2 - hR/2);
        rectangle.setWidth(wR);
        rectangle.setHeight(hR);

        // Timer for how long you last
        scoreLabel.setFont(new Font("Arial", 24));
        scoreLabel.setTextFill(Color.BLACK);
        scoreLabel.setLayoutX(20);
        scoreLabel.setLayoutY(20);
        
        pane.getChildren().addAll(rectangle, scoreLabel);

        // gameover screen
        VBox gameOverLayout = new VBox(20);
        gameOverLayout.setAlignment(Pos.CENTER);

        Label gameOverTitle = new Label("GAME OVER");
        gameOverTitle.setTextFill(Color.BLACK);
        gameOverTitle.setFont(new Font("Arial", 50));

        Label finalScoreLabel = new Label("Final Score: 0");
        finalScoreLabel.setTextFill(Color.GOLD);
        finalScoreLabel.setFont(new Font("Arial", 30));

        Button restartButton = new Button("Play Again");
        restartButton.setFont(new Font("Arial", 20));
        restartButton.setPrefSize(200, 50);

        gameOverLayout.getChildren().addAll(gameOverTitle, finalScoreLabel, restartButton); //gameover screen

        Robot robot = new Robot(); // robot used to track cursor

        loop = new AnimationTimer() { // main game loop
            public void handle(long now) {
                currentScore = (int) ((now - gameStartTime)/1e9); // display the time elapsed
                scoreLabel.setText("Score: " + currentScore);

                //Tracks the cursors postion
                double mouseX = robot.getMouseX() - wnd.getX();
                double mouseY = robot.getMouseY() - wnd.getY();
                Point2D mousePos = new Point2D(mouseX, mouseY);

                // sets the player to the cursor
                rectangle.setX(mouseX - 25); // 25px to the x axis to move the cursor to the middle
                rectangle.setY(mouseY - 40); // 40px to the y axis to move the cursor to the middle

                if (isCoinOnScreen == false) { //spawns coin on the screen
                    spawnRandomCoin(wnd.getWidth(), wnd.getHeight());
                }

                // if the rectangle is collected the coin gets deleted and spawns a new coin
                if (isCoinOnScreen == true && rectangle.getBoundsInParent().intersects(coin.getBoundsInParent())) {
                    pane.getChildren().remove(coin);
                    isCoinOnScreen = false; // Mark as collected so a new one spawns next frame
                    
                    countCoin = countCoin + 1; // counts the amount of coins collected
                }

                // spawn bird
                if (now - lastBirdSpawnTime >= birdCooldown) { //spawns a bird after cooldown
                    Point2D spawnPoint = randSpawnPoint(wnd.getWidth(), wnd.getHeight(), 75);
                    spawnBird(spawnPoint, mousePos);
                    lastBirdSpawnTime = now;
                }

                 // Spawn chasing bird
                if (now - lastBird2SpawnTime >= bird2Cooldown) { //spawns a bird (chasing) after cooldown
                    Point2D spawnPoint = randSpawnPoint(wnd.getWidth(), wnd.getHeight(), 75);
                    spawnBird2(spawnPoint, now);
                    lastBird2SpawnTime = now;
                }

                for (int i = 0; i < birdCircles.size(); i++) {
                    Circle bird = birdCircles.get(i);
                    double dx = birdX.get(i);
                    double dy = birdY.get(i);
                    
                    // Move the bird by its calculated direction
                    bird.setCenterX(bird.getCenterX() + dx);
                    bird.setCenterY(bird.getCenterY() + dy);

                    if (bird.getBoundsInParent().intersects(rectangle.getBoundsInParent())) { // if the player collides with the bird they lose and go to the lose screen
                        finalScoreLabel.setText("Final Score: " + (currentScore + (countCoin*3)));
                        scene.setRoot(gameOverLayout);
                        countCoin = 0;
                        loop.stop();
                    }
                    else if (bird.getCenterX() < -100 || bird.getCenterX() > (wnd.getWidth() + 100) || bird.getCenterY() < -100 || bird.getCenterY() > (wnd.getHeight() + 100)) {
                        // if the bird moves off screen it deletes itself
                        pane.getChildren().remove(bird);
                        
                        // Remove the bird from all three lists to keep the arraylist accurate
                        birdCircles.remove(i);
                        birdX.remove(i);
                        birdY.remove(i);
                        
                        // Decrease index so we don't skip the next item after removal
                        i--; 
                    }
                }

                for (int i2 = 0; i2 < bird2Circles.size(); i2++) {
                    Circle bird2 = bird2Circles.get(i2);
                    long birthday = bird2SpawnTimes.get(i2);

                    // Check if the bird's (chaser) time limit has run out
                    if (now - birthday >= bird2LifeSpan) { // the bird (chaser) will remove itself after lifespan 
                        pane.getChildren().remove(bird2);

                        bird2Circles.remove(i2);
                        bird2Speeds.remove(i2);
                        bird2SpawnTimes.remove(i2);

                        i2--; // Adjust index because item was deleted
                    } else {
                        if (bird2.getBoundsInParent().intersects(rectangle.getBoundsInParent())) { // if the player collides with the bird (chaser) they lose and go to the lose screen
                            finalScoreLabel.setText("Final Score: " + (currentScore + (countCoin*3)));
                            scene.setRoot(gameOverLayout);
                            countCoin = 0;
                            loop.stop();
                        } else {
                            // The bird is still alive, so calculate movement towards player
                        double mX = mousePos.getX() - bird2.getCenterX();
                        double mY = mousePos.getY() - bird2.getCenterY();
                        double distance = Math.sqrt(mX * mX + mY * mY);

                        // Prevent dividing by zero if the bird perfectly hits the cursor
                        if (distance > 0) {
                            double speed = bird2Speeds.get(i2);
                            double stepX = (mX / distance) * speed;
                            double stepY = (mY / distance) * speed;

                            // Apply the movement step toward player
                            bird2.setCenterX(bird2.getCenterX() + stepX);
                            bird2.setCenterY(bird2.getCenterY() + stepY);
                        }
                        }
                    }
                }
            }
        };
        // if you click the start or restart button runs the startGame() and the game loop
        startButton.setOnAction(e -> {
            startGame();
            loop.start();
        });
        restartButton.setOnAction(e -> {
            startGame();
            loop.start();
        });
        wnd.show();
    }

    public void startGame() { // sets up and prepares to start the game loop
        pane.getChildren().removeAll(birdCircles);
        pane.getChildren().removeAll(bird2Circles);
        birdCircles.clear();
        birdX.clear();
        birdY.clear();
        bird2Circles.clear();
        bird2Speeds.clear();
        bird2SpawnTimes.clear();

        scene.setRoot(pane);
        
        gameStartTime = System.nanoTime();
        lastBirdSpawnTime = System.nanoTime();
        lastBird2SpawnTime = System.nanoTime();
    }

    public Point2D randSpawnPoint(double width, double height, double buffer) { // picks a random point outside the screen for the birds
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

    public Point2D randCoinSpawn(double width, double height) { // picks a random point on screen for the coin
        double x = rand.nextDouble() * width;
        double y = rand.nextDouble() * height;
        
        return new Point2D(x,y);
    }

    public void spawnRandomCoin(double width, double height) { // spawn the coin
        coin = new Circle();
        coin.setRadius(12.0);
        coin.setFill(Color.GOLD);

        double padding = 60;
        double randomX = padding + (rand.nextDouble() * (width - (padding * 2)));
        double randomY = padding + (rand.nextDouble() * (height - (padding * 2)));

        coin.setCenterX(randomX);
        coin.setCenterY(randomY);

        pane.getChildren().add(coin);
        
        isCoinOnScreen = true;
    }

    public void spawnBird(Point2D spawn, Point2D targetPosition) { // spawn the bird
        Circle bird = new Circle();
        double rB = 37.5;

        bird.setCenterX(spawn.getX());
        bird.setCenterY(spawn.getY());
        bird.setRadius(rB);
        Image Bird1 = new Image("Bird1.png");

        ImagePattern imagePatternBird1 = new ImagePattern(Bird1, 0, 0, 1, 1, true);

        bird.setFill(imagePatternBird1);

        pane.getChildren().add(bird);

        // Calculate direction toward the player position at spawn time
        double mX = targetPosition.getX() - spawn.getX();
        double mY = targetPosition.getY() - spawn.getY();
        double distance = Math.sqrt(mX * mX + mY * mY);

        // Define movement speed per frame 
        double maxSpeed = 3.0;
        double dx = (mX / distance) * maxSpeed;
        double dy = (mY / distance) * maxSpeed;

        // Save attributes to the parallel lists
        birdCircles.add(bird);
        birdX.add(dx);
        birdY.add(dy);
    }

    public void spawnBird2(Point2D spawn, long spawnTime) { // spawn the bird (chaser)
        Circle bird2 = new Circle();
        bird2.setCenterX(spawn.getX());
        bird2.setCenterY(spawn.getY());
        bird2.setRadius(25); 
        Image Bird2 = new Image("Bird2.png");

        ImagePattern imagePatternBird2 = new ImagePattern(Bird2, 0, 0, 1, 1, true);

        bird2.setFill(imagePatternBird2);

        pane.getChildren().add(bird2);

        bird2Circles.add(bird2);
        bird2Speeds.add(5.0);
        bird2SpawnTimes.add(spawnTime);
    }
}