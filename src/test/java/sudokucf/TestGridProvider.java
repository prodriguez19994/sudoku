package sudokucf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.DataProvider;

public class TestGridProvider {

  public static final String NAME = "EASY";

  @DataProvider(name = NAME)
  public static Iterator<Grid> provideGrid() {

    List<String> gridStrings = new ArrayList<>();

    gridStrings.add("..68....1.5..7..2.4.....3..6....4....2..5..8....6....5..7.....9.8..4..5.9....74..");
    gridStrings.add("..4..6..7...1..2.51.95.74.84......2...2.6..939..2.3..6.81....5.7.53.2..46..9..1..");
    gridStrings.add("6..21873939........2.9.36.47...89....8...2........5.7291.3.45..2...5.4.65...9.2.3");
    gridStrings.add("9...312.4...7.....8.1..5..9..3.1...7.....465.4...57198.1286..4.....7...1.5.1..3..");
    gridStrings.add("53..7....6..195....98....6.8...6...34..8.3..17...2...6.6....28....419..5....8..79");

    List<Grid> grids = gridStrings.stream().map(Grid::build).collect(Collectors.toList());

    return grids.iterator();
  }

}