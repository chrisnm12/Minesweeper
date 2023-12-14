import java.util.ArrayList;

import javalib.funworld.World;
import javalib.funworld.WorldScene;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import java.util.Random;

import javalib.worldimages.*;

/*
 * fields:
 * ...this.mineCount...--int
 * ...this.rows...--int
 * ...this.columns...--int
 *
 * methods:
 *
 *
 *
 *
 * */


abstract class AWorld extends World {
  int mineCount;
  int rows;
  int columns;
  AWorld(int mineCount, int rows, int columns) {
    this.mineCount = mineCount;
    this.rows = rows;
    this.columns = columns;
  }
}

/*
 *
 * To create the initial scene of the text that appears click to start
 *
 * fields:
 *
 * ...this.rand...-- Random
 *  ...this.mineCount...--int
 * ...this.rows...--int
 * ...this.columns...--int
 *
 * methods:
 * // Starts the game when the player left clicks.
 * onMouseClicked()-- World
 *
 * // shows the initial scene of the game which is a Text telling the player to click to start
 * makeScene()-- WorldScene
 *
 *
 * */


class StartingWorld extends AWorld {
  Random rand;
  StartingWorld(int mineCount, int rows, int columns) {this(mineCount, rows, columns, new Random());}
  StartingWorld(int mineCount, int rows, int columns, Random rand) {
    super(mineCount, rows, columns);
    this.rand = rand;
  }
  // Start the game when the player left clicks.
  public World onMouseClicked (Posn pos, String buttonname) {
    if (buttonname.equals("LeftButton")) {
      return new GameWorld(mineCount, rows, columns);
    } else {
      return this;
    }
  }

  // shows the initial scene of the game which is a Text telling the player to click to start
  public WorldScene makeScene() {
    WorldScene scene = getEmptyScene();
    IGamePieces text = new StartingText("Click to Start", 50);
    IGamePieces mineText = new StartingText("Mines: " + this.mineCount, 25);
    IGamePieces gridText = new StartingText("Grid Size: " + this.columns + " x " + this.rows, 25);
    WorldScene scene1 = scene.placeImageXY(text.draw(), 500, 300);
    WorldScene scene2 = scene1.placeImageXY(gridText.draw(), 850,580);
    return scene2.placeImageXY(mineText.draw(), 70,580);
  }
}


/*
*
* The actual world of the game.
*
* fields:
*
* ...this.rand...--Random
* ...board...--ArrayList<Cell>
* ...this.mineCount...--int
* ...this.rows...--int
* ...this.columns...--int
*
* methods:
*
* // counts the amount of cells that are revealed.
* isRevealed()--int
*
* // checks to see if the game is at an end state
* onTick()--World
*
* // processes the different clicks that could occur in the game.
* onMouseClicked()
*
* // This goes through the board and adds a mines according to the mineCount amount.
  // EFFECT: this mutates a random cell on the board and sets the hasBomb to true.
* placeMines()--void
*
* // Gives each cell its list of neighbors.
* // EFFECT: a Cell is given all adjacent cells.
* neighboringCells()--void
*
* // Verifies that the neighbor cell is an actual/valid cell.
* private isValidPosition(int x, int y)-- boolean
*
* // Creates the game board for the World
* makeBoard()--WorldImage
*
* // creates the scene for the game world.
* makeScene()--WorldScene
*
*
*
* */

class GameWorld extends AWorld {
  Random rand;
  ArrayList<ArrayList<Cell>> board;
  GameWorld(int mineCount, int rows, int columns) {
    this(mineCount, rows, columns, new Random());
  }
  GameWorld(int mineCount, int rows, int columns, Random rand) {
    super(mineCount, rows, columns);
    this.rand = rand;
    this.board = new ArrayList<>();

    // This creates the board.
    // using a double for loop to get grid style board for the game
    // this is initializing the columns in a row and then putting the row into the board and continues on until there are no more rows to initialize.
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

  // counts the amount of cells that are revealed.
  public int isRevealedCounter() {
    int count = 0;

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        Cell currentCell = this.board.get(i).get(j);
        if (currentCell.isRevealed) {
          count++;
        }
      }
    }
    return count;
  }
  // checks to see if the game is at an end state
  public World onTick() {
    if (this.isRevealedCounter() == ((rows * columns) - mineCount)) {
      return new EndWorld(mineCount, rows, columns, new Random(), 1, this.board);
    }
    return this;
  }

  // processes the different clicks that could occur in the game.
  public World onMouseClicked(Posn pos, String button) {
    if (button.equals("RightButton")) {
      int mouseX = pos.x;
      int mouseY = pos.y;
      int cellWidth = (1000 / columns);
      int cellHeight = (600 / rows);
      int clickedRow = (rows - 1) - (mouseY / cellHeight);
      int clickedCol = (columns - 1) - (mouseX / cellWidth);

      if (isValidPosition(clickedRow, clickedCol)) {
        Cell clickedCell = board.get(clickedRow).get(clickedCol);

        if (clickedCell.isRevealed) {
          return this;
        }
        clickedCell.hasFlag = !clickedCell.hasFlag;
      }
    }
    else if (button.equals("LeftButton")) {
      int mouseX = pos.x;
      int mouseY = pos.y;
      int cellWidth = (1000 / columns);
      int cellHeight = (600 / rows);
      int clickedRow = (rows - 1) - (mouseY / cellHeight);
      int clickedCol = (columns - 1) - (mouseX / cellWidth);

      if (isValidPosition(clickedRow, clickedCol)) {
        Cell clickedCell = board.get(clickedRow).get(clickedCol);
        if (clickedCell.hasFlag) {
          return this;
        }
        else if (clickedCell.hasBomb) {
          clickedCell.isRevealed = true;
          return new EndWorld(mineCount, rows, columns, new Random(), 0, this.board);
        }
        clickedCell.isRevealed = true;
        if (clickedCell.countNeighboringMines() == 0) {
          clickedCell.neighborBombs();
        }
      }
    }
    return this;
  }

  // This goes through the board and adds a mine according to the mineCount amount.
  // EFFECT: this mutates a random cell on the board and sets the hasBomb to true.
  public void placeMines() {
    ArrayList<Integer> placedMines = new ArrayList<>();
    int totalCells = rows * columns;

    while (placedMines.size() < this.mineCount) {
      int randomCell = rand.nextInt(totalCells);

      if (!placedMines.contains(randomCell)) {
        placedMines.add(randomCell);
        int row = randomCell / columns;
        int col = randomCell % columns;

        Cell cell = this.board.get(row).get(col);
        cell.giveBomb();
      }
    }
  }

  // Gives each cell its list of neighbors.
  // EFFECT: a Cell is given all adjacent cells.
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
              currentCell.addNeighbor(neighborCell);
            }
          }
        }
      }
    }
  }

  // Verifies that the neighbor cell is an actual/valid cell.
  private boolean isValidPosition(int x, int y) {
    return x >= 0 && x < columns && y >= 0 && y < rows;
  }

  // Creates the game board for the World
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

  // creates the scene for the game world.
  public WorldScene makeScene() {
    WorldScene scene = getEmptyScene();
    WorldImage gameBoardImage = this.makeBoard();
    return scene.placeImageXY(gameBoardImage, 500, 300);
  }
}
/*
 *
 * Fields:
 *
 * ...this.mineCount...--int
 * ...this.rows...--int
 * ...this.columns...--int
 * this.rand...--Random
 *
 * methods:
 *
 * // returns the end scene based on this.winner status which could either be 1 = win or 0 = loser.
 * makeScene()--WorldScene
 *
 * // creates the final board from the user's finished game
 * makeBoard()--WorldImage
 *
 *
 * */

class EndWorld extends AWorld {
  Random rand;
  int winner;
  ArrayList<ArrayList<Cell>> board;
  EndWorld(int mineCount, int rows, int columns) {this(mineCount, rows, columns, new Random(), 0, new ArrayList<>());}
  EndWorld(int mineCount, int rows, int columns, Random rand, int winner, ArrayList<ArrayList<Cell>> board) {
    super(mineCount, rows, columns);
    this.rand = rand;
    this.winner = winner;
    this.board = board;
  }

  // Creates the game board for the World
  public WorldImage makeBoard() {
    int cellWidth = 1000 / columns;
    int cellHeight = 600 / rows;

    WorldImage baseImage = new EmptyImage();

    int yOffset = 0;

    for (ArrayList<Cell> row : this.board) {
      int xOffset = 0;
      for (Cell cell : row) {
        WorldImage cellImage = cell.draw().movePinholeTo(new Posn(xOffset, yOffset));
        if (cell.hasBomb) {
          cellImage = new Mine(cell.height / 3).draw().movePinholeTo(new Posn(xOffset, yOffset));
        }
        baseImage = new OverlayImage(baseImage, cellImage);
        xOffset += cellWidth;
      }
      yOffset += cellHeight;
    }
    return baseImage.movePinholeTo(new Posn(0,0));
  }

  // returns the end scene based on this.winner status which could either be 1 = win or 0 = loser.
  public WorldScene makeScene() {
    WorldScene scene = getEmptyScene();
    WorldImage gameBoard = this.makeBoard();
    WorldImage gameOver = new TextImage("Game Over!", 50, FontStyle.BOLD_ITALIC, Color.BLACK);
    WorldImage gameWinner = new TextImage("You Win!", 50, FontStyle.BOLD_ITALIC, Color.BLACK);
    WorldScene scene1 = scene.placeImageXY(gameBoard, 500, 300);
    if (this.winner == 0) {
      return scene1.placeImageXY(gameOver, 500, 300);
    }
    return scene1.placeImageXY(gameWinner, 500, 300);
  }
}




class ExamplesMinesweeper {
  boolean testBigBang(Tester t) {
    World w = new StartingWorld(10, 10, 10);
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
    Cell expectedNeighbor4 = game.board.get(0).get(2);

    ArrayList<Cell> expectedNeighbors = new ArrayList<>();
    expectedNeighbors.add(expectedNeighbor1);
    expectedNeighbors.add(expectedNeighbor2);
    expectedNeighbors.add(expectedNeighbor3);

    return t.checkExpect(cell.neighbors.size(), expectedNeighbors.size()) &&
      t.checkExpect(cell.neighbors.contains(expectedNeighbor1), true) &&
      t.checkExpect(cell.neighbors.contains(expectedNeighbor2), true) &&
      t.checkExpect(cell.neighbors.contains(expectedNeighbor3), true) &&
      t.checkExpect(cell.neighbors.contains(expectedNeighbor4), false);
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
    System.out.println("Number of nearby mines for cell(1,1) is: " + game.board.get(1).get(1).countNeighboringMines());
    // This test doesn't actually test anything, it's just for the sake of printing out the above stuff.
    // That's what I am really looking for. All mines, # of mines, no duplicate mines, and if neighboringMines works.
    return t.checkExpect(placedMines.contains(game.board.get(1).get(1)), true);
  }
}
