package mikera.vectorz.ops;

import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Ops;
import mikera.vectorz.util.DoubleArrays;

public final class Power extends Op {
	private final double exponent;
	private final Op inverse;
	
	private Power(double d) {
		exponent=d;
		inverse=new Power(1.0/d,this);
	}
	
	private Power(double d, Op inv) {
		exponent=d;
		inverse=inv;
	}
	
	public static Op create(double exponent) {
		if (exponent==0) return Constant.ONE;
		if (exponent==1) return Identity.INSTANCE;
		if (exponent==2) return Ops.SQUARE;
		return new Power(exponent);
	}
	
	@Override 
	public double minDomain() {
		if (exponent!=(long)exponent) return 0.0;
		return super.minDomain();
	}
	
	@Override
	public double apply(double x) {
		return Math.pow(x, exponent);
	}
	
	@Override
	public void applyTo(INDArray a) {
		a.pow(exponent);
	}
	
	@Override
	public void applyTo(AVector a) {
		a.pow(exponent);
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		DoubleArrays.pow(data, start, length,exponent);
	}
	
	@Override
	public double applyInverse(double x) {
		return Math.pow(x, 1.0/exponent);
	}
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	
	
	@Override
	public double derivative(double x) {
		return exponent*Math.pow(x, exponent-1);
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return y*Math.pow(y, 1.0/exponent)/exponent;
	}
	
	@Override
	public Op getDerivativeOp() {
		return Ops.product(Constant.create(exponent), Power.create(exponent-1.0));
	}
	
	@Override
	public boolean hasInverse() {
		return true;
	}
	
	@Override
	public Op getInverse() {
		return inverse;
	}

	@Override
	public double averageValue() {
		return 1;
	}

	public double getExponent() {
		return exponent;
	}

}
