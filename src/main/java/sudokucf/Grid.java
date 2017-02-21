package sudokucf;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
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

  @Override
  public String toString() {
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

  public static void main(String[] args) {
    Grid grid = new Grid();
    String toString = grid.toString();
    System.out.println(toString);
    Square square = grid.getSquare(9, 9);
    square.remove(5);
    System.out.println(grid.toString());
    square.resolve(4);
    System.out.println(grid.toString());
  }

}
