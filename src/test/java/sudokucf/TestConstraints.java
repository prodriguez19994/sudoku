package sudokucf;

import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestConstraints {

  @Test
  public static void testCompletedPuzzle(){
    // from http://sudopedia.enjoysudoku.com/Valid_Test_Cases.html
    String gridString = "974236158638591742125487936316754289742918563589362417867125394253649871491873625";
    Grid grid = Grid.build(gridString);
    
    checkGrid(grid);
  }

  @Test static void testDiagonal(){
    StringJoiner sj = new StringJoiner("");

    sj.add(".74236158");
    sj.add("6.8591742");
    sj.add("12.487936");
    sj.add("316.54289");
    sj.add("7429.8563");
    sj.add("58936.417");
    sj.add("867125.94");
    sj.add("2536498.1");
    sj.add("49187362.");
    
    Grid grid = Grid.build(sj.toString());
    
    Constraints.addLineConstraints(grid);
    
    checkGrid(grid);
  }
  

  @Test static void testVertical(){
    StringJoiner sj = new StringJoiner("");

    sj.add(".........");
    sj.add("638591742");
    sj.add("125487936");
    sj.add("316754289");
    sj.add("742918563");
    sj.add("589362417");
    sj.add("867125394");
    sj.add("253649871");
    sj.add("491873625");
    
    Grid grid = Grid.build(sj.toString());
    
    Constraints.addLineConstraints(grid);
    
    checkGrid(grid);
  }

  @Test
  public static void testHorizontral(){
    StringJoiner sj = new StringJoiner("");
    
    sj.add(".74236158");
    sj.add(".38591742");
    sj.add(".25487936");
    sj.add(".16754289");
    sj.add(".42918563");
    sj.add(".89362417");
    sj.add(".67125394");
    sj.add(".53649871");
    sj.add(".91873625");
    
    Grid grid = Grid.build(sj.toString());
    
    Constraints.addLineConstraints(grid);
    
    checkGrid(grid);
  } 
  
  @Test
  public static void testNakedSingles(){
    // from http://sudopedia.enjoysudoku.com/Valid_Test_Cases.html
    String gridString = "3.542.81.4879.15.6.29.5637485.793.416132.8957.74.6528.2413.9.655.867.192.965124.8";
    Grid grid = Grid.build(gridString);
    
    // FIXME multiple other constraints may be all alone able to solve this puzzle.
    Constraints.addLineConstraints(grid);
    
    checkGrid(grid);
  }

  @Test
  public static void testHiddenSingles(){
    // from http://sudopedia.enjoysudoku.com/Valid_Test_Cases.html
    String gridString = "..2.3...8.....8....31.2.....6..5.27..1.....5.2.4.6..31....8.6.5.......13..531.4..";
    Grid grid = Grid.build(gridString);
    
    // FIXME multiple other constraints may be all alone able to solve this puzzle.
    Constraints.addLineConstraints(grid);
    Constraints.addBlockConstraints(grid);
    Constraints.addOnlyMeLineConstraints(grid);
    Constraints.addOnlyMeBlockConstraints(grid);
    
    checkGrid(grid);
  }


  
  // FIXME Move and use this function.
  public static void checkGrid(Grid grid) {
    try {
      CompletableFuture<Void> resolved = grid.getResolved();
      // Waiting a maximum of 640 milliseconds ought to be enough for anybody.
      resolved.get(640, TimeUnit.MILLISECONDS);
      Assert.assertTrue(TestGrid.isValid(grid), grid.toString());
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      Assert.fail();
    }
  }

}
