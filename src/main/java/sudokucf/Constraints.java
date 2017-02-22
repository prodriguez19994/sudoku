package sudokucf;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
      // I could remove the potentiallyResolvedSquare from the list, but the remove member function will do nothing on it, so I don't mind.
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

  /**
   * If a given integer cannot be in all other squares nodes, then it means that it must be the result of the last node.
   * 
   * @param squares
   *          The squares we want to be tied with this constraint. It is the role of the client code to provide a smart set.
   */
  private static void addOnlyMeConstraints(Set<Square> squares) {
    for (Square square : squares) {
      Set<Square> otherSquares = squares.stream().filter(s -> !square.equals(s)).collect(Collectors.toSet());
      for (int k = 1; k < 10; k++) {
        final int k2 = k;
        CompletableFuture<Void>[] tmp = otherSquares.stream()
                                                    .map(os -> os.getImpossible(k2))
                                                    .toArray(CompletableFuture[]::new);
        CompletableFuture<Void> notTheOthers = CompletableFuture.allOf(tmp);
        notTheOthers.thenRun(() -> square.resolve(k2));
      }
    }
  }

  public static void addOnlyMeHorizontalConstraints(Grid grid) {
    Square.RANGE_1_9.stream().forEach(i -> {
      Set<Square> horizontalSquares = grid.getHorizontalSquares(i);
      addOnlyMeConstraints(horizontalSquares);
    });
  }

  public static void addOnlyMeVerticalConstraints(Grid grid) {
    Square.RANGE_1_9.stream().forEach(j -> {
      Set<Square> verticalSquares = grid.getVerticalSquares(j);
      addOnlyMeConstraints(verticalSquares);
    });
  }

  public static void addOnlyMeBlockConstraints(Grid grid) {
    Square.RANGE_1_3.stream().forEach(i -> {
      Square.RANGE_1_3.stream().forEach(j -> {
        Set<Square> blockSquares = grid.getBlockSquares(i, j);
        addOnlyMeConstraints(blockSquares);
      });
    });

  }

  public static void addConstraints(Grid grid) {
    addVerticalConstraints(grid);
    addHorizontalConstraints(grid);
    addBlockConstraints(grid);
    addOnlyMeVerticalConstraints(grid);
    addOnlyMeHorizontalConstraints(grid);
    addOnlyMeBlockConstraints(grid);
  }
}
