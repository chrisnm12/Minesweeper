import java.util.ArrayList;

import javalib.funworld.World;
import javalib.funworld.WorldScene;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import java.util.Random;

import javalib.worldimages.*;
public class Minesweeper {

}

abstract class AWorld extends World {
  int bombCount;
  int rows;
  int columns;
  AWorld(int bombCount, int rows, int columns) {
    this.bombCount = bombCount;
    this.rows = rows;
    this.columns = columns;
  }
}

class StartingWorld extends AWorld {
  Random rand;
  StartingWorld(int bombCount, int rows, int columns) {this(bombCount, rows, columns, new Random());}
  StartingWorld(int bombCount, int rows, int columns, Random rand) {
    super(bombCount, rows, columns);
    this.rand = rand;
  }
  public World onMouseClicked (Posn pos, String buttonname) {
    if (buttonname.equals("LeftButton")) {
      return new GameWorld(bombCount, rows, columns);
    } else {
      return this;
    }
  }

  @Override
  public WorldScene makeScene() {
    WorldScene scene = getEmptyScene();
    IGamePieces text = new StartingText("Click Start", 50);
    IGamePieces bombText = new StartingText("Bombs: " + this.bombCount, 25);
    WorldScene scene1 = scene.placeImageXY(text.draw(), 250, 150);
    return scene1.placeImageXY(bombText.draw(), 70,280);
  }
}

class GameWorld extends AWorld {
  Random rand;
  ArrayList<ArrayList<Cell>> board;
  GameWorld(int bombCount, int rows, int columns) {
    this(bombCount, rows, columns, new Random());
  }
  GameWorld(int bombCount, int rows, int columns, Random rand) {
    super(bombCount, rows, columns);
    this.board = new ArrayList<>();

    for (int i = 0; i < rows; i++) {
      ArrayList<Cell> row = new ArrayList<>();
      int cellWidth =  (1000 / columns) - 10;
      int cellHeight = (600 / rows) - 10;

      for (int j = 0; j < columns; j++) {
        row.add(new Cell(cellHeight, cellWidth));
      }
      this.board.add(row);
    }

    this.rand = rand;
  }
  public WorldImage makeBoard() {
    int cellWidth = 1000 / columns;
    int cellHeight = 600 / rows;

    WorldImage baseImage = new EmptyImage();

    int yOffset = 0;

    for (ArrayList<Cell> row : this.board) {
      int xOffset = 0;
      for (Cell cell : row) {
        WorldImage cellImage = cell.draw().movePinholeTo(new Posn(xOffset, yOffset));
        baseImage = new OverlayImage(baseImage, cellImage);
        xOffset += cellWidth;
      }
      yOffset += cellHeight;
    }
    return baseImage.movePinholeTo(new Posn(0,0));
  }
  @Override
  public WorldScene makeScene() {
    WorldScene scene = getEmptyScene();
    WorldImage gameBoardImage = this.makeBoard();
    WorldScene scene1 = scene.placeImageXY(gameBoardImage, 500, 300);
    IGamePieces text = new StartingText("GAME", 50);
    IGamePieces mine = new Mine();
    Cell cell = new Cell(10, 10);
    IGamePieces flag = new Flag();
    IGamePieces number = new Numbers(3);
    return scene1;
  }
}

class EndWorld extends AWorld {
  Random rand;
  EndWorld(int bombCount, int rows, int columns) {this(bombCount, rows, columns, new Random());}
  EndWorld(int bombCount, int rows, int columns, Random rand) {
    super(bombCount, rows, columns);
    this.rand = rand;
  }
  @Override
  public WorldScene makeScene() {
    return null;
  }
}



class ExamplesMinesweeper {
  boolean testBigBang(Tester t) {
    World w = new StartingWorld(20, 10, 10, new Random());
    int worldWidth = 1000;
    int worldHeight = 600;
    double tickRate = 0.0357142857;
    return w.bigBang(worldWidth, worldHeight, tickRate);
  }
}

