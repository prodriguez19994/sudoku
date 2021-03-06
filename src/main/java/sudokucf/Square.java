package sudokucf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * Represents a sudoku square. Mainly dealing with the possible choices still available. This class does not deal with the position of the square.
 */
public class Square {

  /**
   * All the possible values (i.e. [1;9]). It is a SortedSet for displaying purpose: I like the squares to be in the correct order.
   */
  public static final SortedSet<Integer> RANGE_1_9 = new TreeSet<>();
  public static final SortedSet<Integer> RANGE_1_3 = new TreeSet<>(); // FIXME Should be somewhere else

  static {
    IntStream.rangeClosed(1, 9).forEach(RANGE_1_9::add);
    IntStream.rangeClosed(1, 3).forEach(RANGE_1_3::add);
  }

  /**
   * The current possible choices for the square. It starts being the full [1;9] range, and hopefully ends with a single value: the result!
   */
  private final Set<Integer> possibilities = new HashSet<>();

  /**
   * A mapping between integer values and some CF. These CF will be triggered when it appears that an integer value cannot be the resolved value of this Square. While it may sound redundant, the CF
   * will return the impossible Integer in order to ease the job downstream.
   */
  private final Map<Integer, CompletableFuture<Integer>> impossibilities = new HashMap<>();

  /**
   * A CF that triggers when the Square is resolved. The future value is the Integer value of the Square.
   */
  private final CompletableFuture<Integer> resolved = new CompletableFuture<>();

  /**
   * Horizontal coordinate
   */
  private Integer i;

  /**
   * Vertical coordinate
   */
  private Integer j;

  public Integer getI() {
    return this.i;
  }

  public Integer getJ() {
    return this.j;
  }

  public Square(Integer i, Integer j) {
    this.i = i;
    this.j = j;
    IntStream.rangeClosed(1, 9).forEach(this.possibilities::add);
    IntStream.rangeClosed(1, 9).forEach(k -> this.impossibilities.put(k, new CompletableFuture<>()));
  }

  /**
   * Getter to {@link #resolved}.
   */
  public CompletableFuture<Integer> getResolved() {
    return this.resolved;
  }

  /**
   * Is the square resolved.
   * 
   * @return See description.
   */
  public boolean isResolved() {
    return this.possibilities.size() == 1;
  }

  /**
   * Returns the trigger associated to <b>impossible</b> that will fire when <b>impossible</b> becomes impossible.
   * 
   * @param impossible
   *          The integer for which we want the impossibility trigger.
   * @return The CF trigger.
   */
  public CompletableFuture<Integer> getImpossible(Integer impossible) {
    assert (RANGE_1_9.contains(impossible));
    return this.impossibilities.get(impossible);
  }

  /**
   * Checks that one unique value remains in {@link #possibilities} and returns this value.
   * 
   * @return The solution for this square.
   */
  private Integer getResolvedValue() {
    assert (this.possibilities.size() == 1);
    Integer resolvedValue = this.possibilities.iterator().next();
    return resolvedValue;
  }

  /**
   * Completes the {@link #resolved} CF with the unique remaining values from the {@link #possibilities}.
   */
  private void completeResolved() {
    if (!this.resolved.isDone()) {
      this.resolved.complete(this.getResolvedValue());
    }
  }

  /**
   * Checks if the square is resolved before completing {@link #resolved}.
   */
  private void completeResolvedIfNeeded() {
    if (this.possibilities.size() == 1) {
      this.completeResolved();
    }
  }

  /**
   * Defines the <b>solution</b> for the square.
   * 
   * @param solution
   *          The solution of the square in the sudoku.
   */
  public void resolve(Integer solution) {
    assert (!this.resolved.isDone()); // FIXME I shall check that value is the same and log a possible error...
    assert (this.possibilities.contains(solution));

    RANGE_1_9.stream().filter(i -> !i.equals(solution)).forEach(this::remove);

    assert (this.getResolvedValue().equals(solution));
  }

  /**
   * Removes the <b>impossible</b> choice from the possibilities of this square. Fires the {@link #resolved} if it has to.
   * 
   * @param impossible
   *          The integer that cannot be part of the solution in this square.
   */
  public void remove(Integer impossible) {
    if (this.possibilities.size() != 1) {
      this.possibilities.remove(impossible);
      this.impossibilities.get(impossible).complete(impossible);
      this.completeResolvedIfNeeded();
    }
  }

  /**
   * Checks if the integer <b>i</b> is still possible for the square.
   * 
   * @param i
   *          The integer we want to check.
   * @return See description.
   */
  public boolean isPossible(Integer i) {
    return this.possibilities.contains(i);
  }
}
