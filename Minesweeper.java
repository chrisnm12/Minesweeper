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
  AWorld(int bombCount) {
    this.bombCount = bombCount;
  }
}

class StartingWorld extends AWorld {
  Random rand;
  StartingWorld(int bombCount) {this(bombCount, new Random());}
  StartingWorld(int bombCount, Random rand) {
    super(bombCount);
    this.rand = rand;
  }
  public World onMouseClicked (Posn pos, String buttonname) {
    if (buttonname.equals("LeftButton")) {
      return new GameWorld(bombCount);
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
  GameWorld(int bombCount) {
    this(bombCount, new Random());
  }
  GameWorld(int bombCount, Random rand) {
    super(bombCount);
    this.rand = rand;
  }
  @Override
  public WorldScene makeScene() {
    WorldScene scene = getEmptyScene();
    IGamePieces text = new StartingText("GAME", 50);
    IGamePieces mine = new Mine();
    IGamePieces cell = new Cell();
    IGamePieces flag = new Flag();
    IGamePieces number = new Numbers(3);
    WorldScene scene1 = scene.placeImageXY(mine.draw(), 50, 50);
    WorldScene scene2 = scene1.placeImageXY(cell.draw(), 300, 100);
    WorldScene scene3 = scene2.placeImageXY(flag.draw(), 100, 100);
    WorldScene scene4 = scene3.placeImageXY(number.draw(), 270, 200);
    return scene4.placeImageXY(text.draw(), 250, 150);
  }
}





class ExamplesMinesweeper {
  boolean testBigBang(Tester t) {
    World w = new StartingWorld(20, new Random());
    int worldWidth = 500;
    int worldHeight = 300;
    double tickRate = 0.0357142857;
    return w.bigBang(worldWidth, worldHeight, tickRate);
  }
}

