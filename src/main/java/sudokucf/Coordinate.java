package sudokucf;

public class Coordinate {
    /**
     * Horizontal coordinate
     */
    private Integer i = 0;

    /**
     * Vertival coordinate
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
}
