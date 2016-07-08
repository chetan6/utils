package gibraltar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;



public class ThresholdValuesDriver {
	
	public static String filesFolder = "C:\\code\\gibraltar\\deliverables\\tm\\thresholdValues\\";
	// exception patch
	public static int jump = -2;
	public static int numValues = 3;
	
	// exception patch
	//public static int cutoff = 74;
	public static int cutoff = 0;
	public static int sparseData = 100;
	
	public static void main(String [] a) {
		
		System.out.println(ThresholdValuesDriver.class.toString() + "  launched..");
		String files[] = null;
		boolean isSparse = false;
		int sparseDelta = 0;
		int sparsePopulationSize = 0;
		String outputFileName = "";
		
		// read the file Names
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(filesFolder+a[0]));
			outputFileName = reader.readLine();
			int numFiles = Integer.parseInt(reader.readLine());
			String fileName = ""+numFiles;
			files = new String[numFiles];
			int index = 0;
			
			while (fileName != null) {
				files[index++] = reader.readLine();
			}
			
			reader.close();
			
		} catch (Exception e) {
				System.out.println(e.getMessage());			
		}
		
		double allValues[][] = new double[files.length][numValues];
		
		// just in case if we need to do a full population distribution in the event of sparse data.
		double [][] totalPopulation = new double[files.length][];
		
		
		// collection of data
		HashMap<String, Distribution[]> cDistributions = new HashMap<String, Distribution[]>();
		
		for (int k = 0; k < files.length; k++) {
			
			System.out.println("The file being processed is: "+files[k]);
			
			//Exception patch
			double[] values = GenerateValues.populateValues(filesFolder+files[k], false);
			
			// extract the delta value and remove it from the population
			double [] procValues = new double[values.length-1];
			for (int i = 1; i < values.length; i++) procValues[i-1] = values[i];
			int delta = Math.abs((int) values[0]);
			
			System.out.println("Population Size is: "+ procValues.length);
			System.out.println("Requested Increment is: "+ delta);
			
			totalPopulation[k] = procValues;
			sparsePopulationSize = sparsePopulationSize + procValues.length;
			
			
			if (procValues.length < sparseData) { isSparse = true; sparseDelta = delta; }
			
			if (!isSparse) {
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
					// exception patch
					double [] eligibleThresholdValues = GenerateValues.getEligibleThresholds(deltaValues, numValues, jump, cutoff/delta);
					
					for (int j=0; j < numValues; j++) allValues[k][j] = eligibleThresholdValues[j];
					
					//save it to persist it later
					cDistributions.put(files[k], deltaValues);
					
					//ExcelCreator.writeData(filesFolder+files[k]+".xls", deltaValues, eligibleThresholdValues, 35);
										
			}
		}
			
		if (!isSparse) {
			// display all eligible values
			System.out.println("\n**Consolidated Values..");
			for (int i = 0; i < files.length; i++)	{ 
				for(int j =0; j < numValues; j++)	{ System.out.println(allValues[i][j]); }
				System.out.println("\n");
			}
			
			// derive the final values by taking a minimum across
			double [] finalValues = new double[numValues];
			for (int col=0; col < numValues;col++) {
				double min = Double.POSITIVE_INFINITY;
				for (int row=0; row<files.length;row++) {
					min = (allValues[row][col] < min)?allValues[row][col]:min;  		
				}
				finalValues[col]=min;
			}
			
			//display final values
			System.out.println("**Final Values..");
			for (int i=0; i < numValues; i++) System.out.println(finalValues[i]);
			
			//save final values
			//persistResults(finalValues, outputFileName, false);
			
			//save final values in excel
			ExcelCreator.writeData(filesFolder+outputFileName+".xlsx", cDistributions, allValues, finalValues,35);
			EmailUtil.sendEmail("chetan.shah@protiviti.com", filesFolder, outputFileName+".xlsx", "File/s is/are attached");
			
			
		} else {
	
			// consolidate all the values in one array
			double [] sparseValues = new double[sparsePopulationSize];
			
			int index = 0;
			for (int i=0; i<files.length;i++) 
				for (int j=0; j<totalPopulation[i].length;j++)
					sparseValues[index++]=totalPopulation[i][j];
			
			if (sparsePopulationSize < sparseData) {
				System.out.println("Cannot derive threshold values as population too sparse. Total population size: "+sparsePopulationSize);
				System.out.println("Generating percentiles for reference purposes only..");
				//get percentiles
				double[] percentiles = GenerateValues.getPercentiles(1, sparseValues);
				
				//save it to excel
				persistResults(percentiles, filesFolder, outputFileName+".csv", true, sparsePopulationSize);
				EmailUtil.sendEmail("chetan.shah@protiviti.com", filesFolder, outputFileName+".csv", "Percentiles were generated for reference purposes only");
				System.exit(0);
			}			
			
			//get percentiles
			double[] percentiles = GenerateValues.getPercentiles(sparseDelta, sparseValues);
			
			//display percentiles
			System.out.println("Percentiles are...");
			for (int i = 0; i < percentiles.length; i++) System.out.println(i+":"+percentiles[i]);
			
			// get delta values
			Distribution[] deltaValues = GenerateValues.getDeltaValues(sparseDelta, percentiles);
			
			
			// display delta values
			System.out.println("Delta values are...");
			for (int i=0; i<deltaValues.length;i++) System.out.println(deltaValues[i].value);
			
						
			// get eligible threshold values
			//** Caution harded code to 2!! *******************/
			// exception patch
			double [] eligibleThresholdValues = GenerateValues.getEligibleThresholds(deltaValues, numValues, jump, cutoff/2);			
			
			//display final values
			System.out.println("**Sparse DATA - Final Value!");
			for (int i=0; i < 1; i++) System.out.println(eligibleThresholdValues[i]);
			
			//save final values
			//persistResults(eligibleThresholdValues, outputFileName, true);
			
			//save it to excel
			//exception patch
			ExcelCreator.writeData(filesFolder+outputFileName+".xlsx", deltaValues, eligibleThresholdValues,35);
			EmailUtil.sendEmail("chetan.shah@protiviti.com", filesFolder, outputFileName+".xlsx", "**Sparse Dataset was observed");
		}
			
	}
	
	public static void silentDriver(double[][] valuesCollection, String outputFileName, String destinationFolder) {
		
		System.out.println(ThresholdValuesDriver.class.toString() + "  launched with *silent* switch");
		boolean isSparse = false;
		int sparseDelta = 0;
		int sparsePopulationSize = 0;
		
			
		double allValues[][] = new double[valuesCollection.length][numValues];
		
		// just in case if we need to do a full population distribution in the event of sparse data.
		double [][] totalPopulation = new double[valuesCollection.length][];
		
		
		// collection of data
		HashMap<String, Distribution[]> cDistributions = new HashMap<String, Distribution[]>();
		
		for (int k = 0; k < valuesCollection.length; k++) {
			
			System.out.println("The month being processed is: "+k);
			
			//Exception patch
			double[] values = valuesCollection[k];
			
			
			// extract the delta value and remove it from the population
			double [] procValues = new double[values.length-1];
			for (int i = 1; i < values.length; i++) procValues[i-1] = values[i];
			int delta = Math.abs((int) values[0]);
			
			System.out.println("Population Size is: "+ procValues.length);
			System.out.println("Requested Increment is: "+ delta);
			
			totalPopulation[k] = procValues;
			sparsePopulationSize = sparsePopulationSize + procValues.length;
			
			
			if (procValues.length < sparseData) { isSparse = true; sparseDelta = delta; }
			
			if (!isSparse) {
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
					// exception patch
					double [] eligibleThresholdValues = GenerateValues.getEligibleThresholds(deltaValues, numValues, jump, cutoff/delta);
					
					for (int j=0; j < numValues; j++) allValues[k][j] = eligibleThresholdValues[j];
					
					//save it to persist it later
					cDistributions.put(""+k, deltaValues);
					
					//ExcelCreator.writeData(filesFolder+files[k]+".xls", deltaValues, eligibleThresholdValues, 35);
										
			}
		}
			
		if (!isSparse) {
			// display all eligible values
			System.out.println("\n**Consolidated Values..");
			for (int i = 0; i < valuesCollection.length; i++)	{ 
				for(int j =0; j < numValues; j++)	{ System.out.println(allValues[i][j]); }
				System.out.println("\n");
			}
			
			// derive the final values by taking a minimum across
			double [] finalValues = new double[numValues];
			for (int col=0; col < numValues;col++) {
				double min = Double.POSITIVE_INFINITY;
				for (int row=0; row<valuesCollection.length;row++) {
					min = (allValues[row][col] < min)?allValues[row][col]:min;  		
				}
				finalValues[col]=min;
			}
			
			//display final values
			System.out.println("**Final Values..");
			for (int i=0; i < numValues; i++) System.out.println(finalValues[i]);
			
			//save final values
			//persistResults(finalValues, outputFileName, false);
			
			//save final values in excel
			ExcelCreator.writeData(destinationFolder+outputFileName+".xlsx", cDistributions, allValues, finalValues,35);
			EmailUtil.sendEmail("chetan.shah@protiviti.com", destinationFolder, outputFileName+".xlsx", "File/s is/are attached");
			
			
		} else {
	
			// consolidate all the values in one array
			double [] sparseValues = new double[sparsePopulationSize];
			
			int index = 0;
			for (int i=0; i<valuesCollection.length;i++) 
				for (int j=0; j<totalPopulation[i].length;j++)
					sparseValues[index++]=totalPopulation[i][j];
			
			if (sparsePopulationSize < sparseData) {
				System.out.println("Cannot derive threshold values as population too sparse. Total population size: "+sparsePopulationSize);
				System.out.println("Generating percentiles for reference purposes only..");
				//get percentiles
				double[] percentiles = GenerateValues.getPercentiles(1, sparseValues);
				
				//save it to excel
				persistResults(percentiles, destinationFolder, outputFileName+".csv", true, sparsePopulationSize);		
				EmailUtil.sendEmail("chetan.shah@protiviti.com", destinationFolder, outputFileName+".csv", "");
				return;
			}			
			
			//get percentiles
			double[] percentiles = GenerateValues.getPercentiles(sparseDelta, sparseValues);
			
			//display percentiles
			System.out.println("Percentiles are...");
			for (int i = 0; i < percentiles.length; i++) System.out.println(i+":"+percentiles[i]);
			
			// get delta values
			Distribution[] deltaValues = GenerateValues.getDeltaValues(sparseDelta, percentiles);
			
			
			// display delta values
			System.out.println("Delta values are...");
			for (int i=0; i<deltaValues.length;i++) System.out.println(deltaValues[i].value);
			
						
			// get eligible threshold values
			//** Caution harded code to 2!! *******************/
			// exception patch
			double [] eligibleThresholdValues = GenerateValues.getEligibleThresholds(deltaValues, numValues, jump, cutoff/2);			
			
			//display final values
			System.out.println("**Sparse DATA - Final Value!");
			for (int i=0; i < 1; i++) System.out.println(eligibleThresholdValues[i]);
			
			//save final values
			//persistResults(eligibleThresholdValues, outputFileName, true);
			
			//save it to excel
			//exception patch
			ExcelCreator.writeData(destinationFolder+outputFileName+".xlsx", deltaValues, eligibleThresholdValues,35);		
			EmailUtil.sendEmail("chetan.shah@protiviti.com", destinationFolder, outputFileName+".xlsx", "File/s is/are attached");
		}
			
	}	
	
	
	
	public static void persistResults(Distribution [] eligibleThresholdValues, String outputFilename, boolean isSparse) {
		
		try {
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(filesFolder+outputFilename));
			
			if (isSparse) writer.write("**Sparse DATA - Final Value!");
			for (int i=0; i < eligibleThresholdValues.length; i++) writer.write(eligibleThresholdValues[i]+"\n");			
			
			writer.close();
			
		} catch (Exception e) {
				System.out.println(e.getMessage());			
		}		
		
	}
	
	public static void persistResults(double [] eligibleThresholdValues, String destinationFolder, String outputFilename, boolean isSparse, int sparsePopulationSize) {
		
		try {
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFolder+outputFilename));
			
			if (isSparse) writer.write("**Sparse DATA - Final Value! \n"+sparsePopulationSize+"\n");
			for (int i=0; i < eligibleThresholdValues.length; i++) writer.write(eligibleThresholdValues[i]+"\n");			
			
			writer.close();
			
		} catch (Exception e) {
				System.out.println(e.getMessage());			
		}		
		
	}	

}
