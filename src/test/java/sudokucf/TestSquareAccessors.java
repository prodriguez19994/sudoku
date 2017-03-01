package sudokucf;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestSquareAccessors {

  @Test
  public void testResolve() throws InterruptedException, ExecutionException {
    Square square = new Square(1, 2);
    CompletableFuture<Integer> resolved = square.getResolved();
    final Integer solution = 5;
    final Integer notSolution = 6;

    square.remove(notSolution);
    Assert.assertEquals(resolved.isDone(), false);

    square.resolve(solution);
    Assert.assertEquals(resolved.isDone(), true);
    Assert.assertEquals(resolved.get(), solution);
  }

  @Test
  public void testSingleSquareAccesor() {
    Grid grid = new Grid();
    final Integer i = 5;
    final Integer j = 6;
    Square square = grid.getSquare(i, j);
    Assert.assertEquals(square.getI(), i);
    Assert.assertEquals(square.getJ(), j);
  }

  @Test
  public void testLineAccessors() {
    Grid grid = new Grid();
    final Integer i = 5;
    final Integer j = 6;

    Set<Square> horizontalSquares = grid.getHorizontalLine(j);
    Assert.assertEquals(horizontalSquares.size(), 9);
    Set<Integer> horizontalIs = horizontalSquares.stream().map(Square::getI).collect(Collectors.toSet());
    Assert.assertEquals(horizontalIs, Square.RANGE_1_9);
    Set<Integer> horizontalJs = horizontalSquares.stream().map(Square::getJ).collect(Collectors.toSet());
    Assert.assertEquals(horizontalJs.size(), 1);
    Assert.assertTrue(horizontalJs.contains(j));

    Set<Square> verticalSquares = grid.getVerticalLine(i);
    Assert.assertEquals(verticalSquares.size(), 9);
    Set<Integer> verticalIs = verticalSquares.stream().map(Square::getI).collect(Collectors.toSet());
    Assert.assertEquals(verticalIs.size(), 1);
    Assert.assertTrue(verticalIs.contains(i));
    Set<Integer> verticalJs = verticalSquares.stream().map(Square::getJ).collect(Collectors.toSet());
    Assert.assertEquals(verticalJs, Square.RANGE_1_9);
  }

  @Test
  public void testBlockAccessors() {
    Grid grid = new Grid();
    final Integer blockI = 1;
    final Integer blockJ = 1;

    Set<Square> blockSquares = grid.getBlock(blockI, blockJ);
    Assert.assertEquals(blockSquares.size(), 9);
    Set<Integer> is = blockSquares.stream().map(Square::getI).collect(Collectors.toSet());
    Set<Integer> js = blockSquares.stream().map(Square::getJ).collect(Collectors.toSet());
    Assert.assertEquals(is, Square.RANGE_1_3);
    Assert.assertEquals(js, Square.RANGE_1_3);
    Set<Square> hollowBlockSquares = grid.getHollowBlock(5, 6);
    Assert.assertEquals(hollowBlockSquares.size(), 8);
  }

  @Test
  public void testSubLinesAccessors() {
    Grid grid = new Grid();
    final Integer blockI = 3;
    final Integer blockJ = 2;
    final Integer layer = 2;

    Set<Square> horizontalSquares = grid.getSubSquare(blockI, blockJ, Direction.HORIZONTAL, layer);
    Assert.assertEquals(horizontalSquares.stream().filter(s -> s.getI().equals(7) && s.getJ().equals(5)).count(), 1L);
    Assert.assertEquals(horizontalSquares.stream().filter(s -> s.getI().equals(8) && s.getJ().equals(5)).count(), 1L);
    Assert.assertEquals(horizontalSquares.stream().filter(s -> s.getI().equals(9) && s.getJ().equals(5)).count(), 1L);

    Set<Square> verticalSquares = grid.getSubSquare(blockI, blockJ, Direction.VERTICAL, layer);
    Assert.assertEquals(verticalSquares.stream().filter(s -> s.getI().equals(8) && s.getJ().equals(4)).count(), 1L);
    Assert.assertEquals(verticalSquares.stream().filter(s -> s.getI().equals(8) && s.getJ().equals(5)).count(), 1L);
    Assert.assertEquals(verticalSquares.stream().filter(s -> s.getI().equals(8) && s.getJ().equals(6)).count(), 1L);
  }

}
