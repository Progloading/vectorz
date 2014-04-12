package mikera.matrixx;

import mikera.vectorz.Vector;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestMatrix {
  @Test
  public void testIdentity() {
    Matrix A = Matrix.createIdentity(4);
    assertEquals(4, A.rowCount());
    assertEquals(4, A.columnCount());

    assertEquals(4, A.elementSum(), 0);

    A = Matrix.createIdentity(4, 6);
    assertEquals(4, A.rowCount());
    assertEquals(6, A.columnCount());

    assertEquals(4, A.elementSum(), 0);
  }
  
  @Test 
  public void testSetColumn() {
	  Matrix m=Matrix.create(new double[][] {{1,2},{3,4}});
	  
	  m.setColumn(1, Vector.of(7,8));
	  
	  assertEquals(Matrix.create(new double[][] {{1,7},{3,8}}),m);
  }
}
