package Model;
import Server.*;
import algorithms.mazeGenerators.Maze;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import java.util.Observable;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import Client.Client;
import Client.IClientStrategy ;
import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.String;

 public class MyModel extends Observable implements IModel  {
    public Server mazeGeneratingServer;
    public Server solveMazeServer;
    private int characterRow;
    private int characterColumn;
    private Maze maze = null;
    private Solution solution = null;
    private int lastCharRow;
    private int lastCharCol;
     protected static Properties prop;

    public MyModel() {
        mazeGeneratingServer = new Server(5400,1000,new ServerStrategyGenerateMaze());
        solveMazeServer = new Server(5401,1000,new ServerStrategySolveSearchProblem());
    }

    public void startServers() {
        mazeGeneratingServer.start();
        solveMazeServer.start();
    }

    public void stopServers() {
        mazeGeneratingServer.stop();
        solveMazeServer.stop();
    }

    @Override
    public void generateMaze(int rows, int columns) {
        try{
            CommunicateWithServer_MazeGenerating(rows,columns);
            Thread.sleep(1000);
            characterRow = maze.getStartPosition().getRowIndex();
            characterColumn = maze.getStartPosition().getColumnIndex();

        } catch (InterruptedException e){
        }
        setChanged();
        notifyObservers(1);
    }

    @Override
    public void moveCharacter(KeyCode movement) {
        boolean change= false;
        switch (movement){
            case UP:
            case NUMPAD8:
                if(maze.isLegal(characterRow -1,characterColumn)){
                    change=true;
                    characterRow--;
                }break;
            case DOWN:
            case NUMPAD2:
                if(maze.isLegal(characterRow +1,characterColumn)) {
                    characterRow++;
                    change = true;
                }break;
            case RIGHT:
            case NUMPAD6:
                if(maze.isLegal(characterRow ,characterColumn+1)){
                    characterColumn++;
                    change=true;}
                break;
            case LEFT:
            case NUMPAD4:
                if(maze.isLegal(characterRow ,characterColumn-1)) {
                    change=true;
                    characterColumn--;
                } break;
             //diagonals
            //down right
            case NUMPAD3:
                if(maze.isLegal(characterRow+1 ,characterColumn+1)) {
                    if((maze.isLegal(characterRow+1 ,characterColumn))|| (maze.isLegal(characterRow,characterColumn+1))) {
                        change=true;
                        characterColumn++;
                        characterRow++;
                    }
                }
                break;
                //down left
            case NUMPAD1:
                if(maze.isLegal(characterRow+1 ,characterColumn-1)) {
                    if((maze.isLegal(characterRow ,characterColumn+1))|| (maze.isLegal(characterRow ,characterColumn-1))) {
                        change=true;
                        characterColumn--;
                        characterRow++;
                    }
                }
                break;
                //up right
            case NUMPAD9:
                if(maze.isLegal(characterRow-1 ,characterColumn+1)) {
                    if((maze.isLegal(characterRow-1 ,characterColumn))|| (maze.isLegal(characterRow ,characterColumn+1))) {
                        change=true;
                        characterColumn++;
                        characterRow--;
                    }
                }
                break;
                //up left
            case NUMPAD7:
                 if(maze.isLegal(characterRow-1 ,characterColumn-1)) {
                     if((maze.isLegal(characterRow-1 ,characterColumn))|| (maze.isLegal(characterRow ,characterColumn-1))) {
                         change=true;
                         characterColumn--;
                         characterRow--;
                     }
                  }
            break;
        }
        if (change==true) {
            setChanged();
            notifyObservers(3);
        }
    }

    @Override
    public int[][] getMaze() {
        return  maze.getMaze();
    }

    @Override
    public int getCharacterPositionRow() {
        return characterRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterColumn;
    }


     @Override
     public void solveMaze() {
         CommunicateWithServer_SolveSearchProblem(maze);
         setChanged();
         notifyObservers(2);
     }

     @Override
     public int [][] getSolution() {
        int [][] board = null;
        if(solution != null){
            board = new int[maze.getRows()][maze.getCols()];
            ArrayList<AState> path = solution.getSolutionPath();
            for (AState state: path) {
                MazeState mState = (MazeState)state;
                board[mState.getRow()][mState.getCol()] = 1;
            }
        }
         return board;
     }

     public  void setSolutionNull(){
        solution = null;
     }

    @Override
    public void shutdown() {
        stopServers();
        setChanged();
//        notifyObservers("Shutdown servers");
    }

     //Save maze to file
     public void save() throws IOException {
         //file chooser
         FileChooser fc = new FileChooser();
         fc.setTitle("Save Maze to File");
         FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text files", "*.txt");
         fc.getExtensionFilters().add(extFilter);
         Window primaryStage = null;
         File f = fc.showSaveDialog(primaryStage);
         if(f != null) {
             maze.setPlayerPosition(characterRow,characterColumn);
             ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
             //write the maze to file
             oos.writeObject(maze);
             oos.close();
         }
     }

     //load maze from file
     public void load() throws IOException, ClassNotFoundException {
         FileChooser fc = new FileChooser();
         fc.setTitle("Open Maze from File");
         FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text files", "*.txt");
         fc.getExtensionFilters().add(extFilter);
         Window primaryStage = null;
         File f = fc.showOpenDialog(primaryStage);
         if (f!=null){
             ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(f));
             Maze myMaze=(Maze) inputStream.readObject();
             if (myMaze!=null) {
                 this.maze =myMaze;
                 setSolutionNull();
                 inputStream.close();
                 maze.setPlayerPosition(myMaze.getPlayerPosition().getRowIndex(),myMaze.getPlayerPosition().getColumnIndex());
                 characterRow = myMaze.getPlayerPosition().getRowIndex();
                 characterColumn = myMaze.getPlayerPosition().getColumnIndex();
                 setChanged();
                 notifyObservers(4);
             }
         }
     }

     public String getProperties(){
        BufferedReader reader;
        //Server.Configurations.set
         //prop.setProperty("generateMaze","MyMazeGenerator");
        String returnText = "The configuration properties file:\n";
        try{
            reader = new BufferedReader(new FileReader("resources/config.properties"));
            java.lang.String line;
            line = reader.readLine();
            returnText += line + " - Our maze generating using DFS algorithm\n";
            line = reader.readLine();
            returnText += line + " - Solving the maze using BestFirstSearch algorithm\n";
            line = reader.readLine();
            returnText += line + " - Using Threadpool of size 10";
        }
        catch (IOException e){

        }
        return returnText;
     }

     private void CommunicateWithServer_MazeGenerating(int rows, int cols) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, cols};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[10000]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        maze = new Maze(decompressedMaze);
                        //maze.print();
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
           // e.printStackTrace();
        }
    }

    private void CommunicateWithServer_SolveSearchProblem(Maze MyMaze) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        Maze maze = MyMaze;
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        Solution mazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();
                        for (int i = 0; i < mazeSolutionSteps.size(); i++) {
                         //   System.out.println(String.format("%s. %s", i, mazeSolutionSteps.get(i).toString()));
                        }
                        solution=mazeSolution;
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
           // e.printStackTrace();
        }
    }

     public int getLastCharRow() {
         return lastCharRow;
     }

     public void setLastCharRow(int lastCharRow) {
         this.lastCharRow = lastCharRow;
     }

     public int getLastCharCol() {
         return lastCharCol;
     }

     public void setLastCharCol(int lastCharCol) {
         this.lastCharCol = lastCharCol;
     }

     public int getGoalRow(){ return maze.getGoalPosition().getRowIndex();}

     public int getGoalColumn(){ return maze.getGoalPosition().getColumnIndex();}

     @Override
     public void setToZero() {
         characterColumn = 0;
         characterRow = 0;
         setChanged();
         notifyObservers(3);
     }
 }
