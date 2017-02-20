package sudokucf;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Grid {

    private Map<Coordinate, Square> coordToSquare = new HashMap<>();
    
    public Grid() {
        // Building all the squares of the grid.
        IntStream.rangeClosed(1, 9).forEach(i -> {
            IntStream.rangeClosed(1, 9).forEach(j -> {
                this.coordToSquare.put(new Coordinate(i, j), new Square());
            });
        });
    }
    
    public Square getSquare(Integer i, Integer j){
        return this.coordToSquare.get(new Coordinate(i, j));
    }
    
    public String toString(){
        StringJoiner sj = new StringJoiner("\n");
        for (Integer i = 1; i < 10; i++){
            for (Integer j = 1; j < 10; j++){
                Square square = this.getSquare(i, j);
                String s = IntStream.rangeClosed(1, 9).mapToObj(k -> square.isPossible(k) ? String.valueOf(k) : ".")
                                                      .collect(Collectors.joining(""));
                sj.add("(" + i + ", " + j + ") " + s);
            }
        }
        return sj.toString();
    }

    public static void main(String[] args) {
        Grid grid = new Grid();
        String toString = grid.toString();
    }

}
