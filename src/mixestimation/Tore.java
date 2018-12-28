package mixestimation;

public class Tore {

	public double[][] table;
	public final int width;
	public final int length;
	private int currentIndex;
	
	/** Number of columns already used*/
	public int use;
	
	public Tore(int width, int length) {
		table = new double[width][length];
		this.width = width;
		this.length = length;
		currentIndex = 0;
		use = 0;
	}
	
	public void addValues(double[] values) {
		for (int i = 0 ; i < width ; i++) {
			table[i][currentIndex] = values[i];
		}
		
		currentIndex = (currentIndex + 1 + length) % length;
		if (use < length - 1) use++;
	}
	
	public double getValue(int indexA, int indexB) {
		int i = indexA;
		int j = (indexB + length) % length;
		return table[i][j];
	}
	
	public double[] getColumn(int i) {
		return table[(i + currentIndex + length) % length];
	}
	
	public double[][] getTable() {
		double[][] res = new double[width][length];
		for (int i=0; i < length ; i++) {
			for (int j=0; j < width ; j++) {
				res[j][i] = table[j][(i + currentIndex) % length];
			}
		}
		return res;
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}
	
}
