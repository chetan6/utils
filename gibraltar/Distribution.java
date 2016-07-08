package gibraltar;

public class Distribution {
	
	public int percentile;
	public double value;
	public double delta;
	public double dd;
	
	public String toString() {
		return "percentile: "+this.percentile+","+"value: "+this.value+","+"delta: "+this.delta+","+"dd: "+this.dd;
	}
	
	public void copy(Distribution x) {
		this.percentile = x.percentile;
		this.value = x.value;
		this.delta = x.delta;
		this.dd = x.dd;
	}

}
