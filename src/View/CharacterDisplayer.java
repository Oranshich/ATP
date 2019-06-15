package View;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CharacterDisplayer extends Canvas {
    private int characterPositionRow = 0;
    private int characterPositionColumn = 0;
    private int lastCharacterPositionRow;
    private int lastCharacterPositionColumn;
    private int [][] maze;
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    private double zoom =1;
    private double minX;
    private double minY;

    public double getZoom() {
        return zoom;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public CharacterDisplayer(){
        widthProperty().addListener(evt -> drawCharacter());
        heightProperty().addListener(evt -> drawCharacter());
    }

    public void setCharacterPosition(int row, int column) {
        lastCharacterPositionColumn = characterPositionColumn;
        lastCharacterPositionRow = characterPositionRow;
        characterPositionRow = row;
        characterPositionColumn = column;
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

                double canvasHeight = super.getHeight();
                double canvasWidth = super.getWidth();
                double cellHeight = canvasHeight / maze.length*zoom;
                double cellWidth = canvasWidth / maze[0].length*zoom;
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                minX = characterPositionColumn * cellWidth;
                minY = characterPositionRow * cellHeight;
                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0,0,canvasWidth,canvasHeight);
                gc.drawImage(characterImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
            } catch(FileNotFoundException e){

            }
        }
    }
    public void clear(){
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0,0,getWidth(),getHeight());
    }
    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }
}
