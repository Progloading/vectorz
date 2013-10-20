package mikera.matrixx.impl;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * A matrix implemented as a quadtree of submatrices. 
 * 
 * Useful for large matrices with heirarchical structure where large regions are either fully sparse
 * or of a specialised subtype (e.g. a diagonal matrix).
 * 
 * @author Mike
 *
 */
public class QuadtreeMatrix extends AMatrix implements ISparse {
	
	// Quadtree subcompoents
	private final AMatrix c00, c01, c10, c11;
	
	private final int rowSplit;
	private final int columnSplit;
	private final int rows;
	private final int columns;
	
	private QuadtreeMatrix(AMatrix c00, AMatrix c01, AMatrix c10, AMatrix c11) {
		this.c00=c00;
		this.c01=c01;
		this.c10=c10;
		this.c11=c11;
		this.rowSplit= c00.rowCount();
		this.columnSplit=c00.columnCount();
		this.rows=rowSplit+c10.rowCount();
		this.columns=rowSplit+c01.columnCount();
	}
	
	public static QuadtreeMatrix create(AMatrix c00, AMatrix c01, AMatrix c10, AMatrix c11) {
		if (c00.rowCount()!=c01.rowCount()) throw new IllegalArgumentException("Mismtached submatrix size");
		if (c10.rowCount()!=c11.rowCount()) throw new IllegalArgumentException("Mismtached submatrix size");
		if (c00.columnCount()!=c10.columnCount()) throw new IllegalArgumentException("Mismtached submatrix size");
		if (c01.columnCount()!=c11.columnCount()) throw new IllegalArgumentException("Mismtached submatrix size");
		return new QuadtreeMatrix(c00,c01,c10,c11);
	}
	
	@Override
	public boolean isFullyMutable() {
		return c00.isFullyMutable()&&c01.isFullyMutable()&&c10.isFullyMutable()&&(c11.isFullyMutable());
	}

	@Override
	public int rowCount() {
		return rows;
	}

	@Override
	public int columnCount() {
		return columns;
	}

	@Override
	public double get(int row, int column) {
		if ((row<0)||(row>=rows)||(column<0)||(column>=columns)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		}
		return unsafeGet(row,column);
	}

	@Override
	public void set(int row, int column, double value) {
		if ((row<0)||(row>=rows)||(column<0)||(column>=columns)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		}
		unsafeSet(row,column,value);
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		if (row<rowSplit) {
			if (column<columnSplit) {
				return c00.unsafeGet(row,column);
			} else {
				return c01.unsafeGet(row,column-columnSplit);		
			}
		} else {
			if (column<columnSplit) {
				return c10.unsafeGet(row-rowSplit,column);
			} else {
				return c11.unsafeGet(row-rowSplit,column-columnSplit);		
			}	
		}
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		if (row<rowSplit) {
			if (column<columnSplit) {
				c00.unsafeSet(row,column, value);
			} else {
				c01.unsafeSet(row,column-columnSplit, value);		
			}
		} else {
			if (column<columnSplit) {
				c10.unsafeSet(row-rowSplit,column, value);
			} else {
				c11.unsafeSet(row-rowSplit,column-columnSplit, value);		
			}	
		}	
	}
	

	@Override
	public void addAt(int row, int column, double value) {
		if (row<rowSplit) {
			if (column<columnSplit) {
				c00.addAt(row,column, value);
			} else {
				c01.addAt(row,column-columnSplit, value);		
			}
		} else {
			if (column<columnSplit) {
				c10.addAt(row-rowSplit,column, value);
			} else {
				c11.addAt(row-rowSplit,column-columnSplit, value);		
			}	
		}	
	}
	
	@Override
	public long nonZeroCount() {
		return c00.nonZeroCount()+c01.nonZeroCount()+c10.nonZeroCount()+c11.nonZeroCount();
	}
	
	@Override
	public double elementSum() {
		return c00.elementSum()+c01.elementSum()+c10.elementSum()+c11.elementSum();
	}
	
	@Override
	public void fill(double v) {
		c00.fill(v);
		c01.fill(v);
		c10.fill(v);
		c11.fill(v);
	}
	
	
	@Override
	public void add(double v) {
		c00.add(v);
		c01.add(v);
		c10.add(v);
		c11.add(v);
	}
	
	@Override
	public AVector getRow(int row) {
		if (row<rowSplit) {
			return c00.getRow(row).join(c01.getRow(row));
		} else {
			row-=rowSplit;
			return c10.getRow(row).join(c11.getRow(row));
		}
	}
	
	@Override
	public AVector getColumn(int col) {
		if (col<columnSplit) {
			return c00.getColumn(col).join(c10.getColumn(col));
		} else {
			col-=columnSplit;
			return c01.getColumn(col).join(c11.getColumn(col));
		}
	}

	@Override
	public AMatrix exactClone() {
		return new QuadtreeMatrix(c00.exactClone(),c01.exactClone(),c10.exactClone(),c11.exactClone());
	}

	@Override
	public double density() {
		return ((double)nonZeroCount())/((long)rows*(long)columns);
	}
}