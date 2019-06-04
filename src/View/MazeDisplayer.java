package View;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
public class MazeDisplayer extends Canvas{

    private int[][] maze;

    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    //private StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    private static MediaPlayer regular_Play;
    private static MediaPlayer player_Win;
    private static String music1 = MazeDisplayer.class.getResource("...").toString();
    private static String music2 = MazeDisplayer.class.getResource("...").toString();

    public double getCanvasHeight() {
        return canvasHeight;
    }

    public double getCanvasWidth() {
        return canvasWidth;
    }

    private double canvasHeight;
    private double canvasWidth;

    public MazeDisplayer() {
        // Redraw canvas when size changes.
        widthProperty().addListener(evt -> redraw());
        heightProperty().addListener(evt -> redraw());
    }

    public void setMaze(int[][] maze) {
        this.maze = maze;

        redraw();
    }





    public void redraw() {

        if (maze != null) {
            //setWidth(maze[0].length*10);
            //setHeight(maze.length*10);
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.length;
            double cellWidth = canvasWidth / maze[0].length;

            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                //Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));

                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());
                System.out.println("maze cell height and canvas height:" + cellHeight +", " + getHeight());
                System.out.println("maze cell width and canvas width:" + cellWidth + ", " + getWidth());
                //Draw Maze
                for (int i = 0; i < maze.length; i++) {
                    for (int j = 0; j < maze[i].length; j++) {
                        if (maze[i][j] == 1) {
                            //gc.fillRect(i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                            gc.drawImage(wallImage, j * cellWidth, i*cellHeight, cellWidth, cellHeight);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }
        }
    }

    public void setResize(double height, double width){
        setHeight(height);
        setWidth(width);
        redraw();
    }
    //region Properties
    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }


    //endregion

    //Control songs
    public void controlSongs(){

    }
}
