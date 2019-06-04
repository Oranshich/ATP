package View;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CharacterDisplayer extends Canvas {
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;
    private int [][] maze;
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();

    public CharacterDisplayer(){
        widthProperty().addListener(evt -> drawCharacter());
        heightProperty().addListener(evt -> drawCharacter());
    }
    public void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;
        drawCharacter();
    }


    public void setMaze(int [][] maze){
        this.maze = maze;
        drawCharacter();
    }
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    public void drawCharacter(){
        //Draw Character
        if (maze != null) {
            try {

                double canvasHeight = getHeight();
                double canvasWidth = getWidth();
                double cellHeight = canvasHeight / maze.length;
                double cellWidth = canvasWidth / maze[0].length;
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));

                GraphicsContext gc = getGraphicsContext2D();
                //gc.setFill(Color.RED);
                //gc.fillOval(characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
                gc.drawImage(characterImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
            } catch(FileNotFoundException e){
                //e.printStackTrace();
            }

        }
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }
}
