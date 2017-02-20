package sudokucf;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestGrid {

    @Test
    public void testGrid(){
        Grid grid = new Grid();
        
        Assert.fail(grid.toString());
    }
    
}
