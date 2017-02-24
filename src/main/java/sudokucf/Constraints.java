package sudokucf;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Constraints {

  // FIXME Factorize the addLineConstraints and the addBlockConstraints functions.
  /**
   * The sudoku rules says there shall not be the same number on vertical lines, horizontal lines and blocks. This function imposes this rule on all the verticals.
   * 
   * @param grid
   *          The sudoku grid.
   */
  public static void addLineConstraints(Grid grid) {
    for (Integer i : Square.RANGE_1_9) {
      for (Integer j : Square.RANGE_1_9) {
        // For each square of the grid that may be solved...
        Square potentiallyResolved = grid.getSquare(i, j);
        // ... then all the other squares on the same line...
        Set<Square> hollowCrossSquares = grid.getHollowCross(i, j);
        // ... cannot be solved with the same value.
        CompletableFuture<Integer> resolved = potentiallyResolved.getResolved();
        for (Square square : hollowCrossSquares) {
          resolved.thenAccept(square::remove);
        }
      }
    }
  }

  /**
   * The sudoku rules says there shall not be the same number on vertical lines, horizontal lines and blocks. This function imposes this rule on all the 3x4 blocks.
   * 
   * @param grid
   *          The sudoku grid.
   */
  public static void addBlockConstraints(Grid grid) {
    for (Integer i : Square.RANGE_1_9) {
      for (Integer j : Square.RANGE_1_9) {
        // For each square of the grid that may be solved...
        Square potentiallyResolved = grid.getSquare(i, j);
        // ... then all the other squares on the same line...
        Set<Square> hollowBlockSquares = grid.getHollowBlock(i, j);
        // ... cannot be solved with the same value.
        CompletableFuture<Integer> resolved = potentiallyResolved.getResolved();
        for (Square square : hollowBlockSquares) {
          resolved.thenAccept(square::remove);
        }
      }
    }
  }

  /**
   * If a given integer cannot be in all other squares nodes, then it means that it must be the result of the last node.
   * 
   * @param squares
   *          The squares we want to be tied with this constraint. It is the role of the client code to provide a smart set.
   */
  private static void addOnlyMeConstraints(Set<Square> squares) {
    for (Square square : squares) {
      Set<Square> otherSquares = squares.stream()
                                        .filter(s -> !square.equals(s))
                                        .collect(Collectors.toSet());
      for (Integer solution : Square.RANGE_1_9) {
        CompletableFuture<Integer>[] tmp = otherSquares.stream()
                                                       .map(os -> os.getImpossible(solution))
                                                       .toArray(CompletableFuture[]::new);
        CompletableFuture<Void> solutionImpossibleInTheOthers = CompletableFuture.allOf(tmp);
        solutionImpossibleInTheOthers.thenRun(() -> square.resolve(solution));
      }
    }
  }

  public static void addOnlyMeLineConstraints(Grid grid) {
    for (Integer k : Square.RANGE_1_9) {
      for (Grid.Direction d : Grid.Direction.values()) {
        Set<Square> lineSquares = grid.getLine(d, k);
        addOnlyMeConstraints(lineSquares);
      }
    }
  }

  public static void addOnlyMeBlockConstraints(Grid grid) {
    Square.RANGE_1_3.stream().forEach(i -> {
      Square.RANGE_1_3.stream().forEach(j -> {
        Set<Square> blockSquares = grid.getBlock(i, j);
        addOnlyMeConstraints(blockSquares);
      });
    });
  }

  // FIXME Factorise horizontal and vertical parts. Add tests! 
  public static void addBlockColumnInteractionConstraints(Grid grid) {
    // For all the 3x3 blocks
    for (Integer blockI : Square.RANGE_1_3) {
      for (Integer blockJ : Square.RANGE_1_3) {
        // For all the 3 layers/rows of the current block...
        for (Integer layer : Square.RANGE_1_3) {
          // ... then I consider the 2 other layers/rows...
          TreeSet<Integer> otherLayers = new TreeSet<>(Square.RANGE_1_3);
          otherLayers.remove(layer);

          // ... and I consider the 2 other 3x3 blocks in the same horizontal.
          TreeSet<Integer> otherBlockIs = new TreeSet<>(Square.RANGE_1_3);
          otherBlockIs.remove(blockI); // FIXME Move up in the loop.
          TreeSet<Integer> otherBlockJs = new TreeSet<>(Square.RANGE_1_3);
          otherBlockJs.remove(blockJ); // FIXME Move up in the loop.

          Set<Square> impossibleHorizontalSquares = grid.getHorizontalSubSquare(blockI, blockJ, otherLayers);
          Set<Square> otherHorizontalSquares = grid.getHorizontalSubSquare(otherBlockIs, blockJ, layer);

          Set<Square> impossibleVerticalSquares = grid.getVerticalSubSquare(blockI, blockJ, otherLayers);
          Set<Square> otherVerticalSquares = grid.getVerticalSubSquare(blockI, otherBlockJs, layer);

          for (Integer value : Square.RANGE_1_9) {
            CompletableFuture<Integer>[] horizontalImpossibles = impossibleHorizontalSquares.stream()
                                                                                            .map(
                                                                                                isq -> isq.getImpossible(
                                                                                                    value))
                                                                                            .toArray(
                                                                                                CompletableFuture[]::new);
            for (Square otherHorizontalSquare : otherHorizontalSquares) {
              CompletableFuture.allOf(horizontalImpossibles).thenRunAsync(() -> otherHorizontalSquare.remove(value));
            }
            CompletableFuture<Integer>[] verticalImpossibles = impossibleVerticalSquares.stream()
                                                                                        .map(
                                                                                            isq -> isq.getImpossible(
                                                                                                value))
                                                                                        .toArray(
                                                                                            CompletableFuture[]::new);
            for (Square otherVerticalSquare : otherVerticalSquares) {
              CompletableFuture.allOf(verticalImpossibles).thenRunAsync(() -> otherVerticalSquare.remove(value));
            }
          }
        }
      }
    }
  }

  public static void addConstraints(Grid grid) {
    addLineConstraints(grid);
    addBlockConstraints(grid);
    addOnlyMeLineConstraints(grid);
    addOnlyMeBlockConstraints(grid);
    addBlockColumnInteractionConstraints(grid);
  }
}
