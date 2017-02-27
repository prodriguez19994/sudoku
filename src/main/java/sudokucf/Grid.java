package sudokucf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Grid {

  private final List<Square> squares = new ArrayList<>();

  private CompletableFuture<Void> resolved = null;

  public Grid() {
    // Building all the squares of the grid.
    Square.RANGE_1_9.stream().forEach(i -> {
      Square.RANGE_1_9.stream().forEach(j -> {
        this.squares.add(new Square(i, j));
      });
    });

    CompletableFuture[] squaresResolved = this.squares.stream()
                                                      .map(Square::getResolved)
                                                      .toArray(CompletableFuture[]::new);
    this.resolved = CompletableFuture.allOf(squaresResolved);
  }

  /**
   * Builds a grid from a string. The string shall look like <br>
   * 2..8.4..7..6...5.. </br>
   * concatenated as a String of length 9x9=81.
   * 
   * @param input
   *          The input string
   * @return The grid ready for resolution.
   */
  public static Grid build(String input) {
    assert (input.length() == 81);
    Grid grid = new Grid();

    char[] startingPoints = input.toCharArray();
    for (int a = 0; a < 81; a++) {
      final int j = 9 - a / 9;
      final int i = a - 9 * (a / 9) + 1;

      final char s = startingPoints[a];
      try {
        Integer value = Integer.parseInt(String.valueOf(s));
        assert (Square.RANGE_1_9.contains(value));
        grid.getSquare(i, j).resolve(value);
      }
      catch (Exception e) {
        // Left blank
      }
    }

    return grid;
  }

  public Square getSquare(Integer i, Integer j) {
    Square[] oneSquare = this.squares.stream()
                                     .filter(square -> square.getI().equals(i) && square.getJ().equals(j))
                                     .toArray(Square[]::new);
    assert (oneSquare.length == 1);
    return oneSquare[0];
  }

  public String list() {
    StringJoiner sj = new StringJoiner("\n");

    Square.RANGE_1_9.stream().forEach(i -> {
      Square.RANGE_1_9.stream().forEach(j -> {
        Square square = this.getSquare(i, j);
        String s = Square.RANGE_1_9.stream()
                                   .map(k -> square.isPossible(k) ? String.valueOf(k) : ".")
                                   .collect(Collectors.joining(""));
        sj.add("(" + i + ", " + j + ") " + s);
      });
    });

    return sj.toString();

  }

  @Override
  public String toString() {

    StringJoiner sj = new StringJoiner("\n");

    for (int j = 9; j > 0; j--) {

      StringJoiner line = new StringJoiner("");

      for (int i = 1; i < 10; i++) {
        Square square = this.getSquare(i, j);
        for (int k = 7; k < 10; k++) {
          line.add(square.isPossible(k) ? String.valueOf(k) : ".");
        }
        line.add("|");
      }
      line.add("\n");

      for (int i = 1; i < 10; i++) {
        Square square = this.getSquare(i, j);
        for (int k = 4; k < 7; k++) {
          line.add(square.isPossible(k) ? String.valueOf(k) : ".");
        }
        line.add("|");
      }
      line.add("\n");

      for (int i = 1; i < 10; i++) {
        Square square = this.getSquare(i, j);
        for (int k = 1; k < 4; k++) {
          line.add(square.isPossible(k) ? String.valueOf(k) : ".");
        }
        line.add("|");
      }

      sj.add(line.toString());
      sj.add("------------------------------------");
    }

    return sj.toString();
  }

  /**
   * Returns the squares matching <b>predicate</b>.
   * 
   * @param predicate
   *          The predicate that will select the square.
   * @return The selected squares.
   */
  private Set<Square> getSquares(Predicate<Square> predicate) {
    return this.squares.stream()
                       .filter(predicate)
                       .collect(Collectors.toSet());
  }

  public Set<Square> getLine(Direction direction, Integer k) {
    assert (Square.RANGE_1_9.contains(k));
    if (Direction.HORIZONTAL.equals(direction)) {
      return getSquares(square -> square.getJ().equals(k));
    }
    else { // i.e. Direction.VERTICAL
      return getSquares(square -> square.getI().equals(k));
    }
  }

  /**
   * Returns the vertical line of the grid.
   * 
   * @param i
   *          The abscissa of the line.
   * @return The squares.
   */
  public Set<Square> getVerticalLine(Integer i) {
    return getLine(Direction.VERTICAL, i);
  }

  /**
   * Returns the horizontal line of the grid.
   * 
   * @param j
   *          The ordinate of the line.
   * @return The squares.
   */
  public Set<Square> getHorizontalLine(Integer j) {
    return getLine(Direction.HORIZONTAL, j);
  }

  /**
   * Returns all the squares on the same line of the (<b>i</b>, <b>j</b>) square, but not the (<b>i</b>, <b>j</b>) square itself. This member function is designed to be used to build the constraint
   * that there is one and only one value per line.
   * 
   * @param i
   *          The abscissa of the square.
   * @param j
   *          The ordinate of the square.
   * @return The squares.
   */
  public Set<Square> getHollowCross(Integer i, Integer j) {
    return this.getSquares(square -> {
      boolean h = !(square.getI().equals(i)) && square.getJ().equals(j);
      boolean v = !(square.getJ().equals(j)) && square.getI().equals(i);
      return v || h;
    });
  }

  /**
   * Get the squares from a block.
   * 
   * @param blockI
   *          The block horizontal coordinate (must be in [1:3])
   * @param blockJ
   *          The block vertical coordiante (must be in [1:3]).
   * @return The squares making the block
   */
  public Set<Square> getBlock(Integer blockI, Integer blockJ) {
    assert (Square.RANGE_1_3.contains(blockI));
    assert (Square.RANGE_1_3.contains(blockJ));
    return getSquares(square -> {
      int blockCoordI = (square.getI() + 2) / 3;
      int blockCoordJ = (square.getJ() + 2) / 3;
      return blockI.equals(blockCoordI) && blockJ.equals(blockCoordJ);
    });
  }

  public Set<Square> getHollowBlock(Integer i, Integer j) {
    final int blockI = (i + 2) / 3;
    final int blockJ = (j + 2) / 3;
    return getSquares(square -> {
      final int sqi = square.getI();
      final int sqj = square.getJ();
      int blockCoordI = (sqi + 2) / 3;
      int blockCoordJ = (sqj + 2) / 3;
      return blockI == blockCoordI && blockJ == blockCoordJ && !(sqi == i && sqj == j);
    });
  }

  /**
   * Returns the squares <b>layer</b>th length-3 line of the (i, j) block. in case <b>direction</b> is horizontal, then the <b>layer</b> will be horizontal. Vertical in otherwise.
   * 
   * @param direction
   *          The layer direction.
   * @param blockI
   *          The 3x3 block coordinate (must be in [1;3]).
   * @param blockJ
   *          The 3x3 block coordinate (must be in [1;3]).
   * @param layer
   *          The row we want (must be in [1;3]).
   * @return The matching squares.
   */
  public Set<Square> getSubSquare(Direction direction, Integer blockI, Integer blockJ, Integer layer) {
    return getSquares(square -> {
      int blockCoordI = (square.getI() + 2) / 3;
      int blockCoordJ = (square.getJ() + 2) / 3;
      int internalRelativeLayerCoord;
      if (Direction.HORIZONTAL.equals(direction)) {
        internalRelativeLayerCoord = square.getJ() - 3 * (blockCoordJ - 1);
      }
      else {
        internalRelativeLayerCoord = square.getI() - 3 * (blockCoordI - 1);
      }
      return blockI.equals(blockCoordI) && blockJ.equals(blockCoordJ) && layer.equals(internalRelativeLayerCoord);
    });
  }

  public Set<Square> getSubSquare(Direction direction, Integer blockI, Integer blockJ, Set<Integer> layers) {
    Set<Square> result = layers.stream()
                               .map(layer -> getSubSquare(direction, blockI, blockJ, layer))
                               .flatMap(l -> l.stream())
                               .collect(Collectors.toSet());
    return result;
  }

  public Set<Square> getSubSquare(Direction direction, Set<Integer> blockIs, Integer blockJ, Integer layer) {
    Set<Square> result = blockIs.stream()
                                .map(i -> this.getSubSquare(direction, i, blockJ, layer))
                                .flatMap(l -> l.stream())
                                .collect(Collectors.toSet());
    return result;
  }

  public Set<Square> getSubSquare(Direction direction, Integer blockI, Set<Integer> blockJs, Integer layer) {
    Set<Square> result = blockJs.stream()
                                .map(blockJ -> this.getSubSquare(direction, blockI, blockJ, layer))
                                .flatMap(l -> l.stream())
                                .collect(Collectors.toSet());
    return result;
  }

  /**
   * Is the square resolved.
   * 
   * @return See description.
   */
  public boolean isDone() {
    return this.resolved.isDone();
  }

  public CompletableFuture<Void> getResolved() {
    return this.resolved;
  }

}
