package sudokucf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.DataProvider;

public class TestGridProvider {

  public static final String NAME = "GRID_PROVIDER";

  // http://www2.warwick.ac.uk/fac/sci/moac/people/students/peter_cock/python/sudoku/
  
  // from http://la-conjugaison.nouvelobs.com/sudoku/grille.php?niveau=difficile&grille=1
  public static List<String> provideDiffcultGrids(){
    List<String> gridStrings = new ArrayList<>();

    // FIXME use resources file and download from some web site.
    gridStrings.add("16..3.7..57....1....34...9.93.1.4......8.9......3.2.79.1...34....8....35..6.4..12");
    gridStrings.add("2.5..1..68.7...91....64.....2.1..3.8.........1.9..6.5.....72....32...6.95..8..2.1");
    gridStrings.add(".7.1.....1...4.5..58.7.......7.31..52.5...6.19..45.8.......2.19..3.9...2.....4.6.");
    gridStrings.add("7..9..8....6..8.979...36.52.5.19.................84.3.12.37...486.2..9....4..9..1");

    return gridStrings;    
  }
  
  // from http://la-conjugaison.nouvelobs.com/sudoku/grille.php?niveau=diabolique&grille=1
  public static List<String> provideDiabolicGrids(){
    List<String> gridStrings = new ArrayList<>();

    //gridStrings.add("..8.....2.6.2.1.4.7...6..9....1725....9...4....1349....4..1...6.5.8.6.1.1.....2..");
    gridStrings.add(".1..6..7........522..35...1..762......68.51......746..4...19..889........5..8..4.");
    gridStrings.add("6.......44...5..9.9.2..4.6...94.1...8.3...9.6...3.82...7.2..4.9.2..3...85.......3");
    //gridStrings.add("3..8.5..2.7..23.......7...3..97..52...2...8...16..23..6...4.......29..4.9..5.1..6");

    return gridStrings;    
  }
  
  public static List<String> provideEasyGrids(){
    List<String> gridStrings = new ArrayList<>();

    gridStrings.add("..68....1.5..7..2.4.....3..6....4....2..5..8....6....5..7.....9.8..4..5.9....74..");
    //gridStrings.add("..4..6..7...1..2.51.95.74.84......2...2.6..939..2.3..6.81....5.7.53.2..46..9..1..");
    gridStrings.add("6..21873939........2.9.36.47...89....8...2........5.7291.3.45..2...5.4.65...9.2.3");
    gridStrings.add("9...312.4...7.....8.1..5..9..3.1...7.....465.4...57198.1286..4.....7...1.5.1..3..");
    gridStrings.add("53..7....6..195....98....6.8...6...34..8.3..17...2...6.6....28....419..5....8..79");

    return gridStrings;    
  }
  
  
  @DataProvider(name = NAME)
  public static Iterator<Grid> solveGrids() {
    List<String> gridStrings = new ArrayList<>();
    
    gridStrings.addAll(provideEasyGrids());
    gridStrings.addAll(provideDiffcultGrids());
    gridStrings.addAll(provideDiabolicGrids());
    
    List<Grid> grids = gridStrings.stream().map(Grid::build).collect(Collectors.toList());

    return grids.iterator();
  }

}