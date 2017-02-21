package sudokucf;

import java.util.Objects;

public class Coordinate {
  /**
   * Horizontal coordinate
   */
  private Integer i = 0;

  /**
   * Vertical coordinate
   */
  private Integer j = 0;

  public Coordinate(Integer i, Integer j) {
    this.i = i;
    this.j = j;
  }

  public Integer getI() {
    return this.i;
  }

  public Integer getJ() {
    return this.j;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof Coordinate) {
      Coordinate otherCoordinate = (Coordinate) other;
      return this.i.equals(otherCoordinate.getI()) && this.j.equals(otherCoordinate.getJ());
    }
    else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.i, this.j);
  }
}
