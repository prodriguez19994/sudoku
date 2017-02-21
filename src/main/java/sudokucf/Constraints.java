package sudokucf;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class Constraints {

  /**
   * The sudoku rules says there shall not be the same number on vertical lines, horizontal lines and blocks. This functions will impose this rule on the <b>squares</b>.
   * 
   * @param squares
   *          The squares we want to be tied with the sudoku rule. It is the role of the client code to provide a smart set.
   */
  private static void addConstraint(Set<Square> squares) {
    for (Square potentiallyResolvedSquare : squares) {
      CompletableFuture<Integer> resolved = potentiallyResolvedSquare.getResolved();
      for (Square square : squares) {
        resolved.thenAccept(solution -> square.remove(solution));
      }
    }
  }

  /**
   * Vertical sudoku rule: no square shall have the same number on the same line.
   * 
   * @param grid
   *          The sudoku grid
   */
  public static void addVerticalConstraints(Grid grid) {
    Square.RANGE_1_9.stream().forEach(j -> {
      Set<Square> verticalSquares = grid.getVerticalSquares(j);
      addConstraint(verticalSquares);
    });
  }

  /**
   * Horizontal sudoku rule: no square shall have the same number on the same line.
   * 
   * @param grid
   *          The sudoku grid
   */
  public static void addHorizontalConstraints(Grid grid) {
    Square.RANGE_1_9.stream().forEach(i -> {
      Set<Square> verticalSquares = grid.getHorizontalSquares(i);
      addConstraint(verticalSquares);
    });
  }

  /**
   * Block sudoku rule: no square shall have the same number in the same block.
   * 
   * @param grid
   *          The sudoku grid
   */
  public static void addBlockConstraints(Grid grid) {
    Square.RANGE_1_3.stream().forEach(i -> {
      Square.RANGE_1_3.stream().forEach(j -> {
        Set<Square> blockSquares = grid.getBlockSquares(i, j);
        addConstraint(blockSquares);
      });
    });
  }
}
