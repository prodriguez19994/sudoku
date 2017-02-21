package sudokucf;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class Constraints {

  private static void addConstraint(Set<Square> squares) {
    for (Square potentiallyResolvedSquare : squares) {
      CompletableFuture<Integer> resolved = potentiallyResolvedSquare.getResolved();
      for (Square square : squares) {
        resolved.thenAccept(solution -> square.remove(solution));
      }
    }
  }

  public static void addVerticalConstraints(Grid grid) {
    Square.RANGE_1_9.stream().forEach(j -> {
      Set<Square> verticalSquares = grid.getVerticalSquares(j);
      addConstraint(verticalSquares);
    });
  }

  public static void addHorizontalConstraints(Grid grid) {
    Square.RANGE_1_9.stream().forEach(i -> {
      Set<Square> verticalSquares = grid.getHorizontalSquares(i);
      addConstraint(verticalSquares);
    });
  }

  public static void addBlockConstraints(Grid grid) {
    Square.RANGE_1_3.stream().forEach(i -> {
      Square.RANGE_1_3.stream().forEach(j -> {
        Set<Square> blockSquares = grid.getBlockSquares(i, j);
        addConstraint(blockSquares);
      });
    });
  }

}
