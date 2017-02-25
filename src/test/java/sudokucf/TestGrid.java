package sudokucf;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestGrid {

  public static boolean isValid(Grid grid) { // FIXME Move somewhere else.

    for (Integer i : Square.RANGE_1_9) {
      for (Integer j : Square.RANGE_1_9) {
        Square square = grid.getSquare(i, j);
        if (!square.isResolved()) { return false; }
      }
    }

    // Extracts all the values of the squares and compares them with the [1;9] range.
    Predicate<Set<Square>> isLineValid = squares -> {
      Set<Integer> lineResults = squares.stream().map(s -> s.getResolved().getNow(-1)).collect(Collectors.toSet());
      return Square.RANGE_1_9.equals(lineResults);
    };

    for (Integer l : Square.RANGE_1_9) {
      if (!isLineValid.test(grid.getVerticalLine(l))) { return false; }
      if (!isLineValid.test(grid.getHorizontalLine(l))) { return false; }
    }

    for (Integer blockI : Square.RANGE_1_3) {
      for (Integer blockJ : Square.RANGE_1_3) {
        if (!isLineValid.test(grid.getBlock(blockI, blockJ))) { return false; }
      }
    }

    return true;
  }

  @Test(dataProviderClass = TestGridProvider.class, dataProvider = TestGridProvider.NAME)
  public void testResolve(Grid grid) {
    Constraints.addConstraints(grid);

    TestConstraints.checkGrid(grid);
  }
}
