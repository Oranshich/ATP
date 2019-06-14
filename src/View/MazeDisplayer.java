package View;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.media.MediaPlayer.*;

public class MazeDisplayer extends Canvas{

    private int[][] maze;
    private static MediaPlayer start_play;
    private static MediaPlayer maze_play;
    private static MediaPlayer win_play;
    private static String music1 = MazeDisplayer.class.getResource("/music/Bassa.mp3").toString();
    private static String music2 = MazeDisplayer.class.getResource("/music/Nana Banana.mp3").toString();
    private static String music3 = MazeDisplayer.class.getResource("/music/win.mp3").toString();
    private double zoom = 1;
    private double canvasHeight;
    private double canvasWidth;
    private int goalRow;
    private int goalColumn;
    @FXML
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameEnd = new SimpleStringProperty();
    private GraphicsContext gc;


    public MazeDisplayer() {
        // Redraw canvas when size changes.
        widthProperty().addListener(evt -> redraw());
        heightProperty().addListener(evt -> redraw());
    }

    public void setMaze(int[][] maze, int goalRow, int goalColumn) {
        this.maze = maze;
        this.goalRow=goalRow;
        this.goalColumn=goalColumn;
        gc = getGraphicsContext2D();
        redraw();
    }


    public void redraw() {
        if (maze != null) {
            //setWidth(maze[0].length*10);
            //setHeight(maze.length*10);
            double minSize = Math.min(getHeight(),getWidth());
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.length*zoom;
            double cellWidth = canvasWidth / maze[0].length*zoom;

            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image endImage = new Image(new FileInputStream(ImageFileNameEnd.get()));

                //GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());
                //Draw Maze
                for (int i = 0; i < maze.length; i++) {
                    for (int j = 0; j < maze[i].length; j++) {

                        if (maze[i][j] == 1) {
                            gc.drawImage(wallImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }else{

                        }
                    }
                }
                gc.drawImage(endImage, goalColumn * cellWidth, goalRow * cellHeight, cellWidth, cellHeight);
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }
        }
    }

    public void clear() {
        if (maze != null) {
            //GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());
        }
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public double getCanvasHeight() {
        return canvasHeight;
    }

    public double getCanvasWidth() {
        return canvasWidth;
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

    public String getImageFileNameEnd() {
        return ImageFileNameEnd.get();
    }

    public void setImageFileNameEnd(String imageFileNameEnd) {
        this.ImageFileNameEnd.set(imageFileNameEnd);
    }



    //Control songs
    public static void ControlSong(String command) {
        if (command.equals("mute")){
            if (maze_play!=null) maze_play.pause();

        }
        else if (command.equals("resume")) {
            if (maze_play != null) maze_play.play();
        }
        else if (command.equals("stop")) {
            if (start_play!=null)
                start_play.stop();
            if (maze_play != null)
                maze_play.stop();
            if (win_play!=null)
                win_play.stop();
        }
        else if (command.equals("start")) {
            if (win_play!=null) win_play.stop();
            if (start_play != null) start_play.stop();
            else
                start_play = new MediaPlayer(new Media(music1));
            start_play.setCycleCount(MediaPlayer.INDEFINITE); //loop forever
            start_play.play();
        }
        else if (command.equals("play")){
            if (start_play!=null) start_play.stop();
            if ( maze_play == null)
                maze_play = new MediaPlayer(new Media(music2));
            else
                maze_play.stop();
            maze_play.setCycleCount(MediaPlayer.INDEFINITE); //loop forever
            maze_play.play();
        }
        else if (command.equals("win")){
            if ( maze_play!=null) maze_play.stop();
            win_play=new MediaPlayer(new Media(music3));
            win_play.setCycleCount(MediaPlayer.INDEFINITE); //loop forever
            win_play.play();
        }
    }


}
