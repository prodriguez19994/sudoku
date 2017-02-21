package sudokucf;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Grid {

  private final Map<Coordinate, Square> coordToSquare = new HashMap<>();

  public Grid() {
    // Building all the squares of the grid.
    Square.RANGE_1_9.stream().forEach(i -> {
      Square.RANGE_1_9.stream().forEach(j -> {
        this.coordToSquare.put(new Coordinate(i, j), new Square());
      });
    });
  }

  public Square getSquare(Integer i, Integer j) {
    return this.coordToSquare.get(new Coordinate(i, j));
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
  private Set<Square> getSquares(Predicate<Entry<Coordinate, Square>> predicate) {
    return this.coordToSquare.entrySet()
                             .stream()
                             .filter(predicate)
                             .map(e -> e.getValue())
                             .collect(Collectors.toSet());
  }

  public Set<Square> getHorizontalSquares(Integer i) {
    assert (Square.RANGE_1_9.contains(i));
    return getSquares(e -> e.getKey().getI().equals(i));
  }

  public Set<Square> getVerticalSquares(Integer j) {
    assert (Square.RANGE_1_9.contains(j));
    return getSquares(e -> e.getKey().getJ().equals(j));
  }

  /**
   * Get the squares from a block.
   * 
   * @param i
   *          The block horizontal coordinate (must be in [1:3])
   * @param j
   *          The block vertical coordiante (must be in [1:3]).
   * @return The squares making the block
   */
  public Set<Square> getBlockSquares(Integer i, Integer j) {
    assert (Square.RANGE_1_3.contains(i));
    assert (Square.RANGE_1_3.contains(j));
    return getSquares(e -> {
      int blockCoordI = (e.getKey().getI() + 2) / 3;
      int blockCoordJ = (e.getKey().getJ() + 2) / 3;
      return i.equals(blockCoordI) && j.equals(blockCoordJ);
    });
  }

  /**
   * Is the square resolved.
   * 
   * @return See description.
   */
  public boolean isResolved() {
    return this.coordToSquare.values().stream().map(Square::isResolved).allMatch(b -> b);
  }

  public static void main(String[] args) {
    Grid grid = new Grid();

    Constraints.addVerticalConstraints(grid);
    Constraints.addHorizontalConstraints(grid);
    Constraints.addBlockConstraints(grid);

    grid.getSquare(1, 1).resolve(9);
    grid.getSquare(6, 1).resolve(7);
    grid.getSquare(7, 1).resolve(4);

    grid.getSquare(2, 2).resolve(8);
    grid.getSquare(5, 2).resolve(4);
    grid.getSquare(8, 2).resolve(5);

    grid.getSquare(3, 3).resolve(7);
    grid.getSquare(9, 3).resolve(9);

    grid.getSquare(4, 4).resolve(6);
    grid.getSquare(9, 4).resolve(5);

    grid.getSquare(2, 5).resolve(2);
    grid.getSquare(5, 5).resolve(5);
    grid.getSquare(8, 5).resolve(8);

    grid.getSquare(1, 6).resolve(6);
    grid.getSquare(6, 6).resolve(4);

    grid.getSquare(1, 7).resolve(4);
    grid.getSquare(7, 7).resolve(3);

    grid.getSquare(2, 8).resolve(5);
    grid.getSquare(5, 8).resolve(7);
    grid.getSquare(8, 8).resolve(2);

    grid.getSquare(3, 9).resolve(6);
    grid.getSquare(4, 9).resolve(8);
    grid.getSquare(9, 9).resolve(1);

    System.out.println(grid.toString());
    System.out.println(grid.isResolved() ? "Grid is resolved" : "Grid is _not_ resolved.");

  }

}
