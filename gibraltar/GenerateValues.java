package gibraltar;

import org.apache.commons.math3.stat.*;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import java.util.*;
import java.io.*;

public class GenerateValues {
	
	// Test driver
	public static void main(String [] a) {
		
		double[] values = GenerateValues.populateValues("HRJ_Jan", false);
		
		// extract the delta value and remove it from the population
		double [] procValues = new double[values.length-1];
		for (int i = 1; i < values.length; i++) procValues[i-1] = values[i];
		int delta = (int) values[0];
		
		System.out.println("Population Size is: "+ procValues.length);
		System.out.println("Requested Increment is: "+ delta);
		
		
		//get percentiles
		double[] percentiles = GenerateValues.getPercentiles(delta, procValues);
		
		//display percentiles
		System.out.println("Percentiles are...");
		for (int i = 0; i < percentiles.length; i++) System.out.println(i+":"+percentiles[i]);
		
		// get delta values
		Distribution[] deltaValues = GenerateValues.getDeltaValues(delta, percentiles);
		
		// display delta values
		System.out.println("Delta values are...");
		for (int i=0; i<deltaValues.length;i++) System.out.println(deltaValues[i]);
		
		// get eligible threshold values
		double [] eligibleThresholdValues = GenerateValues.getEligibleThresholds(deltaValues, 3, 5, 74);
		
		// display eligible threshold values
		System.out.println("Eligible values are...;");
		for (int i=0; i<eligibleThresholdValues.length;i++) System.out.println(eligibleThresholdValues[i]);
		
	} 
	
	public static double[] populateValues(String fileName, boolean inverted) {
		
		double returnValues[] = null;
		
		try {
		System.out.println(GenerateValues.class.toString() + "  launched..");
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		//BufferedReader reader = new BufferedReader(new FileReader(fileName));
		//BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\code\\nt\\messages\\IFML711_"+a[0]+tokens[0]+".xml"));
				
		double value;
		List<Double> values = new ArrayList<>();
		value = Double.parseDouble(reader.readLine());
		values.add(value);
		
		while (value > Double.NEGATIVE_INFINITY) {
			String temp = reader.readLine();
			value = ((temp == null)?Double.NEGATIVE_INFINITY:Double.parseDouble(temp));
			if (value != Double.NEGATIVE_INFINITY) values.add(value);
		}
			//writer.close();
		reader.close();
		
		// invert the dataset?
		double invert = 1.0;
		if (inverted) invert = -1.0;
		returnValues = new double[values.size()];
		for (int i = 0; i < values.size(); i++) returnValues[i] = invert*((values.get(i)).doubleValue());
			
		} catch (Exception e) {
			System.out.println(e.getMessage());			
		}

		return returnValues;
	}
	
	public static double[] getPercentiles(int delta, double[] values) {
		
		int index = 0;
		double percentiles[] = new double[(100/delta)+1];
		int perc = delta;
		percentiles[index++] = StatUtils.min(values);
		Percentile p = new Percentile();
		p = p.withEstimationType(Percentile.EstimationType.R_7);
		while (perc <= 100) {
			percentiles[index++]= p.evaluate(values, perc);
			perc = perc + delta;
		}
		
		return percentiles;
	}
	
	
	public static Distribution[] getDeltaValues(int delta, double[] percentiles) {
		
		// ensure they are still sorted
		int perc = 0;
		Arrays.sort(percentiles);
		
		Distribution[] deltaValues = new Distribution[percentiles.length];
		Distribution prev = new Distribution();
		prev.percentile = -1;
		prev.value = percentiles[0];
		prev.delta = 0;
		
		for (int i = 0; i < percentiles.length; i++) {
			Distribution d = new Distribution();
			d.percentile = perc;
			d.value = percentiles[i];
			d.delta = (prev.value == 0)?Double.POSITIVE_INFINITY:(((d.value - prev.value) / (prev.value))*100.0);
			d.dd = (d.delta - prev.delta);
			perc = perc + delta;
			deltaValues[i] = d;
			prev = d;
		}
		prev = null;
		return deltaValues;
	}
	
	
	public static double[] getEligibleThresholds(Distribution [] deltaValues, int num, int jump, int cutoff) {
		
		/* Caution : This method assumes that the dataset is sorted by percentiles */
		
		double [] eligibleThresholds = new double[num];
		int numPercentiles = deltaValues.length-1;
		int start = numPercentiles;
		int end = 0;
		boolean valuesIdentified = false;
		boolean specificIterationValueIdentified = false;
				
			
			for (int index=0; index < num; index++) {
				
			  for(int i=start; (i > 0 && i >= cutoff); i--) {
				if (deltaValues[i].dd <= (jump*1.0)) {
					eligibleThresholds[index] = deltaValues[i].value;
					valuesIdentified = true;
					specificIterationValueIdentified = true;
					end = i;
					break;
				}
				
			  }
			
			  for(int i=end; i > 0; i--)	{
					if (deltaValues[i].dd > (jump*1.0)) {  
						start = i;
						break;
					}
				}
			  if (!specificIterationValueIdentified) eligibleThresholds[index] = deltaValues[cutoff].value;
			  specificIterationValueIdentified = false;
			}
			
			if (!valuesIdentified) eligibleThresholds[0] = deltaValues[cutoff].value; 
			
		/*int index = 0;	
		for(int i=numPercentiles; ((i > 0)&&(index<num)); i--) {
			if(deltaValues[i].dd < jump) eligibleThresholds[index++] = deltaValues[i].value;
		}*/
			
		return eligibleThresholds;
	}
	
	public static double[] getEEligibleThresholds(Distribution [] deltaValues, int num, int jump, int cutoff) {
		
		/* Caution : This method assumes that the dataset is sorted by percentiles */
		
		double [] eligibleThresholds = new double[num];
		int numPercentiles = deltaValues.length-1;
		int start = numPercentiles;
		int end = 0;
		boolean valuesIdentified = false;
				
			
			for (int index=0; index < num; index++) {
				
			  for(int i=start; (i > 0 && i >= cutoff); i--) {
				if (deltaValues[i].dd >= (jump*1.0)) {
					eligibleThresholds[index] = deltaValues[i].value;
					valuesIdentified = true;
					end = i;
					break;
				}
				
			  }
			
			  for(int i=end; i > 0; i--)	{
					if (deltaValues[i].dd < (jump*1.0)) {  
						start = i;
						break;
					}
				}
			}
			
			if (!valuesIdentified) eligibleThresholds[0] = deltaValues[cutoff].value; 
			
		/*int index = 0;	
		for(int i=numPercentiles; ((i > 0)&&(index<num)); i--) {
			if(deltaValues[i].dd < jump) eligibleThresholds[index++] = deltaValues[i].value;
		}*/
			
		return eligibleThresholds;
	}	
	

}