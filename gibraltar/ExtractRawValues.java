package gibraltar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ExtractRawValues {
	
	public static String filesFolder = "C:\\code\\gibraltar\\deliverables\\tm\\thresholdValues\\exceptions\\";
	public static int delta = 2;
	
	public static void main(String [] a) {
	
		// read the file Names
		try {			
			BufferedReader reader = new BufferedReader(new FileReader(filesFolder+a[0]));
			boolean isSilent = (a[1].equalsIgnoreCase("silent"))?true:false;
			boolean isInverted = (a[2].equalsIgnoreCase("yes"))?true:false;
			
			HashMap<String, List<AmountCount>> bagOfGoodies = new HashMap<String, List<AmountCount>>();
			
			String line = reader.readLine();
			List<AmountCount> values;
			
			// read the file and segregate it into respective buckets by category and month
			while (line != null) {
				
				String [] s = line.split("\\^", 50);
				String identifier = s[0].trim()+'_'+s[3].trim();
				
				if (bagOfGoodies.get(identifier) == null) {
					values = new ArrayList<>();
				} else {
					values = bagOfGoodies.get(identifier);							
				}
				
				values.add(new AmountCount(Double.valueOf(s[1]), Integer.valueOf(s[2])));
				bagOfGoodies.put(identifier, values);
				line = reader.readLine();
				
			}
			reader.close();
			
			// determine how many many do we need to create
			String [] fileNames = (String []) bagOfGoodies.keySet().toArray(new String[0]);
			
			if (isSilent) silent(fileNames,bagOfGoodies, isInverted);  else verbose(fileNames,bagOfGoodies);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());			
		}	
	}
		
	public static void verbose(String fileNames[], HashMap<String, List<AmountCount>> bagOfGoodies ) {
		
		List<AmountCount> values;
		try {
			for (int i=0; i<fileNames.length;i++)	{
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(filesFolder+fileNames[i]+".csv"));
				writer.write(""+delta+"\n");
				values = bagOfGoodies.get(fileNames[i]);
				//
				for (int j=0; j < values.size(); j++) writer.write(values.get(j).amount+"\n");
				//for (int j=0; j < values.size(); j++) writer.write(values.get(j).count+"\n");
				writer.close();
				
			}
		} catch (Exception e) {
			System.out.println("Could not create all the files..");
		}
	}
	
	public static void silent(String fileNames[], HashMap<String, List<AmountCount>> bagOfGoodies, boolean isInverted ) {

		HashMap<String, List<String>> bagOfTransactionTypes = new HashMap<String, List<String>>();
		List <String> months; 
		double multiplier = (isInverted)?-1.0:1.0;
		
		for (int i = 0; i < fileNames.length; i++) {
			String [] s = fileNames[i].split("\\_", 50);
			if (bagOfTransactionTypes.get(s[0]) == null) {
				months = new ArrayList<>();					
			} else {
				months = bagOfTransactionTypes.get(s[0]);
			}
			
			months.add(s[1]);
			bagOfTransactionTypes.put(s[0], months);
		}
		
		//perform quantitative analysis
		String [] transactionTypes = (String []) bagOfTransactionTypes.keySet().toArray(new String[0]);
		
		//call the distribution functions for each transaction type
		for (int i=0; i<transactionTypes.length;i++) {
			
			// determine the number of months
			months = bagOfTransactionTypes.get(transactionTypes[i]);
			months.sort(null);
			double valuesCollection[][] = new double[months.size()][];
			List<AmountCount> extractedValues;
			
			// extract values for each month
			for (int j =0; j < months.size();j++) {
				extractedValues = bagOfGoodies.get(transactionTypes[i]+"_"+months.get(j));
				double [] tempValues = new double[extractedValues.size()+1];
				tempValues[0] = delta;
				
				for (int k=0; k < extractedValues.size();k++) {
					
					tempValues[k+1] = multiplier*(extractedValues.get(k).amount);
				}
				valuesCollection[j] = tempValues;
			}
			
			ThresholdValuesDriver.silentDriver(valuesCollection, transactionTypes[i], filesFolder);
			
		}		
	}
	

}
