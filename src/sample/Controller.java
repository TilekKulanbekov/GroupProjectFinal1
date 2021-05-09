package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private final Double venomSize = 50.;
    private Rectangle venomHead;
    private Rectangle venomTail_1;
    double xPos;
    double yPos;

    Food food;

    private Direction direction;

    private final List<Position> positions = new ArrayList<>();

    private final ArrayList<Rectangle> snakeBody = new ArrayList<>();

    private int gameTicks;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button startButton;

    Timeline timeline;

    private boolean canChangeDirection;

    @FXML
    void start(MouseEvent event) {

        for (Rectangle venom : snakeBody) {
            anchorPane.getChildren().remove(venom);
        }

        gameTicks = 0;
        positions.clear();
        snakeBody.clear();
        venomHead = new Rectangle(250, 250, venomSize, venomSize);
        venomTail_1 = new Rectangle(venomHead.getX() - venomSize, venomHead.getY(), venomSize, venomSize);
        xPos = venomHead.getLayoutX();
        yPos = venomHead.getLayoutY();
        direction = Direction.RIGHT;
        canChangeDirection = true;
        food.moveFood();

        snakeBody.add(venomHead);
        venomHead.setFill(Color.rgb(173,255,47));
        venomTail_1.setFill(Color.rgb(255,255,0));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        snakeBody.add(venomTail_1);

        anchorPane.getChildren().addAll(venomHead, venomTail_1);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.3), e -> {
            positions.add(new Position(venomHead.getX() + xPos, venomHead.getY() + yPos));
            moveSnakeHead(venomHead);
            for (int i = 1; i < snakeBody.size(); i++) {
                moveSnakeTail(snakeBody.get(i), i);
            }
            canChangeDirection = true;
            eatFood();
            gameTicks++;
            if(checkIfGameIsOver(venomHead)){
                timeline.stop();
            }
        }));
        food = new Food(-50,-50,anchorPane,venomSize);
    }

    @FXML
    void moveSquareKeyPressed(KeyEvent event) {
        if(canChangeDirection){
            if (event.getCode().equals(KeyCode.W) && direction != Direction.DOWN) {
                direction = Direction.UP;
            } else if (event.getCode().equals(KeyCode.S) && direction != Direction.UP) {
                direction = Direction.DOWN;
            } else if (event.getCode().equals(KeyCode.A) && direction != Direction.RIGHT) {
                direction = Direction.LEFT;
            } else if (event.getCode().equals(KeyCode.D) && direction != Direction.LEFT) {
                direction = Direction.RIGHT;
            }
            canChangeDirection = false;
        }
    }

    @FXML
    void addBodyPart(ActionEvent event) {
        addSnakeTail();
    }

    private void moveSnakeHead(Rectangle snakeHead) {
        if (direction.equals(Direction.RIGHT)) {
            xPos = xPos + venomSize;
            snakeHead.setTranslateX(xPos);
        } else if (direction.equals(Direction.LEFT)) {
            xPos = xPos - venomSize;
            snakeHead.setTranslateX(xPos);
        } else if (direction.equals(Direction.UP)) {
            yPos = yPos - venomSize;
            snakeHead.setTranslateY(yPos);
        } else if (direction.equals(Direction.DOWN)) {
            yPos = yPos + venomSize;
            snakeHead.setTranslateY(yPos);
        }
    }

    private void moveSnakeTail(Rectangle snakeTail, int tailNumber) {
        double yPos = positions.get(gameTicks - tailNumber + 1).getYPos() - snakeTail.getY();
        double xPos = positions.get(gameTicks - tailNumber + 1).getXPos() - snakeTail.getX();
        snakeTail.setTranslateX(xPos);
        snakeTail.setTranslateY(yPos);
    }

    private void addSnakeTail() {
        Rectangle rectangle = snakeBody.get(snakeBody.size() - 1);
        Rectangle snakeTail = new Rectangle(
                snakeBody.get(1).getX() + xPos + venomSize,
                snakeBody.get(1).getY() + yPos,
                venomSize, venomSize);
        snakeBody.add(snakeTail);
        anchorPane.getChildren().add(snakeTail);
    }

    public boolean checkIfGameIsOver(Rectangle snakeHead) {
        if (xPos > 550 || xPos < -200||yPos < -200 || yPos > 300) {
            System.out.println("Game_over");
            return true;
        } else if(snakeHitItSelf()){
            return true;
        }
        return false;
    }

    public boolean snakeHitItSelf(){
        int size = positions.size() - 1;
        if(size > 2){
            for (int i = size - snakeBody.size(); i < size; i++) {
                if(positions.get(size).getXPos() == (positions.get(i).getXPos())
                        && positions.get(size).getYPos() == (positions.get(i).getYPos())){
                    System.out.println("Hit");
                    return true;
                }
            }
        }
        return false;
    }

    private void eatFood(){
        if(xPos + venomHead.getX() == food.getPosition().getXPos() && yPos + venomHead.getY() == food.getPosition().getYPos()){
            System.out.println("Eat food");
            foodCantSpawnInsideSnake();
            addSnakeTail();
        }
    }

    private void foodCantSpawnInsideSnake(){
        food.moveFood();
        while (isFoodInsideSnake()){
            food.moveFood();
        }


    }

    private boolean isFoodInsideSnake(){
        int size = positions.size();
        if(size > 2){
            for (int i = size - snakeBody.size(); i < size; i++) {
                if(food.getPosition().getXPos() == (positions.get(i).getXPos())
                        && food.getPosition().getYPos() == (positions.get(i).getYPos())){
                    System.out.println("Inside");
                    return true;
                }
            }
        }
        return false;
    }


}