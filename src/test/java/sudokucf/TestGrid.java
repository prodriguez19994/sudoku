package sudokucf;

import java.util.StringJoiner;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestGrid {

  @Test
  public void testGrid() {

    StringJoiner sj = new StringJoiner("");
    
    sj.add("..68....1");
    sj.add(".5..7..2.");
    sj.add("4.....3..");
    sj.add("6....4...");
    sj.add(".2..5..8.");
    sj.add("...6....5");
    sj.add("..7.....9");
    sj.add(".8..4..5.");
    sj.add("9....74..");
    
    Grid grid = Grid.build(sj.toString());
    
    Constraints.addConstraints(grid);
    
    Assert.assertTrue(grid.isResolved());
  }

  @Test
  public void testGrid2() {

    StringJoiner sj = new StringJoiner("");
    
    sj.add("2..8.4..7");
    sj.add("..6...5..");
    sj.add(".74...92.");
    sj.add("3...4...7");
    sj.add("...3.5...");
    sj.add("4...6...9");
    sj.add(".19...74.");
    sj.add("..8...2..");
    sj.add("5..6.8..1");
    
    Grid grid = Grid.build(sj.toString());
    
    Constraints.addConstraints(grid);
    
    Assert.assertTrue(grid.isResolved());
  }
}
