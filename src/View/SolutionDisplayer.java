package View;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class SolutionDisplayer extends Canvas {
    private int[][] maze;
    private int[][] solution;
    private int characterPositionRow;
    private int characterPositionColumn;
    private StringProperty ImageFileNameSolution = new SimpleStringProperty();

    public void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;
    }

    public String getImageFileNameSolution() {
        return ImageFileNameSolution.get();
    }

    public void setImageFileNameSolution(String imageFileNameSolution) {
        this.ImageFileNameSolution.set(imageFileNameSolution);
    }

    public void setSolution(int [][] maze,int [][] sol) {
        this.maze = maze;
        this.solution = sol;
        drawSolution();
    }

    public void drawSolution(){
        //Draw Character
        if (maze != null) {
            try {

                double canvasHeight = super.getHeight();
                double canvasWidth = super.getWidth();
                double cellHeight = canvasHeight / maze.length;
                double cellWidth = canvasWidth / maze[0].length;
                Image solutionImage = new Image(new FileInputStream(ImageFileNameSolution.get()));

                GraphicsContext gc = getGraphicsContext2D();

                gc.clearRect(0,0,canvasWidth,canvasHeight);
                for (int i = 0; i < solution.length; i++) {
                    for (int j = 0; j < solution[i].length; j++) {
                        if(solution[i][j] == 1)
                            gc.drawImage(solutionImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                    }
                }

            } catch(FileNotFoundException e){

            }
        }
    }


}
