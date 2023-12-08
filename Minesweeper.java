import java.util.ArrayList;
import java.util.HashSet;

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
    this.rand = rand;
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
    neighboringCells();
    placeMines();
  }
  public void placeMines() {
    ArrayList<Integer> placedMines = new ArrayList<>();
    int totalCells = rows * columns;

    while (placedMines.size() < this.bombCount) {
      int randomCell = rand.nextInt(totalCells);

      if (!placedMines.contains(randomCell)) {
        placedMines.add(randomCell);
        int row = randomCell / columns;
        int col = randomCell % columns;

        Cell cell = this.board.get(row).get(col);
        cell.hasBomb = true;
      }
    }
  }

  public void neighboringCells() {
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        Cell currentCell = this.board.get(i).get(j);
        currentCell.neighbors.clear();
          for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
              int neighborX = i + dx;
              int neighborY = j + dy;

              if (isValidPosition(neighborX, neighborY) && !(dx == 0 && dy == 0)) {
                Cell neighborCell = this.board.get(neighborX).get(neighborY);
                currentCell.neighbors.add(neighborCell);
              }
            }
          }
        }
      }
    }

    private boolean isValidPosition(int x, int y) {
      return x >= 0 && x < columns && y >= 0 && y < rows;
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
    World w = new StartingWorld(20, 10, 10);
    int worldWidth = 1000;
    int worldHeight = 600;
    double tickRate = 0.0357142857;
    return w.bigBang(worldWidth, worldHeight, tickRate);
  }
  boolean testNeighborCells3(Tester t) {
    GameWorld game = new GameWorld(20, 10, 10);
    Cell cell = game.board.get(0).get(0);

    Cell expectedNeighbor1 = game.board.get(0).get(1);
    Cell expectedNeighbor2 = game.board.get(1).get(0);
    Cell expectedNeighbor3 = game.board.get(1).get(1);

    ArrayList<Cell> expectedNeighbors = new ArrayList<>();
    expectedNeighbors.add(expectedNeighbor1);
    expectedNeighbors.add(expectedNeighbor2);
    expectedNeighbors.add(expectedNeighbor3);

    return t.checkExpect(cell.neighbors.size(), expectedNeighbors.size()) &&
      t.checkExpect(cell.neighbors.contains(expectedNeighbor1), true) &&
      t.checkExpect(cell.neighbors.contains(expectedNeighbor2), true) &&
      t.checkExpect(cell.neighbors.contains(expectedNeighbor3), true);
  }
  boolean testNeighborCells8(Tester t) {
    GameWorld game = new GameWorld(20, 10, 10);
    Cell cell = game.board.get(5).get(5);

    Cell expectedNeighbor1 = game.board.get(6).get(6);
    Cell expectedNeighbor2 = game.board.get(6).get(5);
    Cell expectedNeighbor3 = game.board.get(6).get(4);
    Cell expectedNeighbor4 = game.board.get(5).get(4);
    Cell expectedNeighbor5 = game.board.get(5).get(6);
    Cell expectedNeighbor6 = game.board.get(4).get(6);
    Cell expectedNeighbor7 = game.board.get(4).get(5);
    Cell expectedNeighbor8 = game.board.get(4).get(4);

    ArrayList<Cell> expectedNeighbors = new ArrayList<>();
    expectedNeighbors.add(expectedNeighbor1);
    expectedNeighbors.add(expectedNeighbor2);
    expectedNeighbors.add(expectedNeighbor3);
    expectedNeighbors.add(expectedNeighbor4);
    expectedNeighbors.add(expectedNeighbor5);
    expectedNeighbors.add(expectedNeighbor6);
    expectedNeighbors.add(expectedNeighbor7);
    expectedNeighbors.add(expectedNeighbor8);

    return t.checkExpect(cell.neighbors.size(), expectedNeighbors.size()) &&
      t.checkExpect(cell.neighbors.contains(expectedNeighbor1), true) &&
        t.checkExpect(cell.neighbors.contains(expectedNeighbor2), true) &&
          t.checkExpect(cell.neighbors.contains(expectedNeighbor3), true) &&
            t.checkExpect(cell.neighbors.contains(expectedNeighbor4), true) &&
              t.checkExpect(cell.neighbors.contains(expectedNeighbor5), true) &&
                t.checkExpect(cell.neighbors.contains(expectedNeighbor6), true) &&
                  t.checkExpect(cell.neighbors.contains(expectedNeighbor7), true) &&
                    t.checkExpect(cell.neighbors.contains(expectedNeighbor8), true);
  }

  boolean testPlacedMines(Tester t) {
    GameWorld game = new GameWorld(20, 10, 10);

    ArrayList<Cell> placedMines = new ArrayList<>();
    for (ArrayList<Cell> row : game.board) {
      for (Cell cell : row) {
        if (cell.hasBomb) {
          placedMines.add(cell);
        }
      }
    }
    for (int i = 0; i < game.rows; i++) {
      for (int j = 0; j < game.columns; j++) {
        Cell currentCell = game.board.get(i).get(j);
        if (currentCell.hasBomb) {
          System.out.println("Mine placed at: Row " + i + ", Column " + j);
        }
      }
    }
    System.out.println("Mine list size is: " + placedMines.size());
    // This test doesn't actually test anything, it's just for the sake of printing out the above stuff.
    // That's what I am really looking for. All mines, # of mines, and no duplicate mines.
    return t.checkExpect(placedMines.contains(game.board.get(1).get(1)), true);
  }
}

