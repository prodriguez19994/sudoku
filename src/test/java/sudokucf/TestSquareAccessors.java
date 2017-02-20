package sudokucf;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestSquareAccessors {

  @Test
  public void testResolve() throws InterruptedException, ExecutionException {
    Assert.assertTrue(true);
    Square square = new Square();
    CompletableFuture<Integer> resolved = square.getResolved();
    final Integer solution = 5;
    final Integer notSolution = 6;

    square.remove(notSolution);
    Assert.assertEquals(resolved.isDone(), false);

    square.resolve(solution);
    Assert.assertEquals(resolved.isDone(), true);
    Assert.assertEquals(resolved.get(), solution);
  }

}
