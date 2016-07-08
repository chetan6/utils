package general;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;



public class GenerateProjectilePath {
	
	public static String fileName = "C:\\code\\tableau\\SampleData.csv";
	public static String outputFileName = "C:\\code\\tableau\\SampleOutputData.csv";
	public static String delim = ",";
	
	public static void main(String [] a) {
	
		// read the file Names
		try {			
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
			
			
			//create a header for output file
			writer.write("X, Y, Path_ID, Path_Order, Magnitude"+System.lineSeparator());
			
			String line = reader.readLine();
			
			// read the file and segregate it into respective buckets by category and month
			while (line != null) {
				
				String [] s = line.split(",", 4);
				String path_id = s[0].trim();
				int start = (Integer.parseInt(s[1]));
				int tenure = ((Integer.parseInt(s[2])) - start);
				int age = Integer.parseInt(s[3]);
				
				
				// compute intermediate points based on the start year and tenure
				int pathOrder = 1;
				double x = 0;
				double y = 0;
				//double vy = 4.9*tenure;
				double v0 = tenure / (age*1.0) ; 
				double vy = v0*Math.sin(Math.toRadians(90-age));
				double vx = v0*Math.cos(Math.toRadians(90-age));
				
				//writer.write((x)+delim+y+delim+path_id+delim+(pathOrder++)+delim+magnitude+System.lineSeparator());
				
				for (int i = 0; i <= (tenure); i++,pathOrder++) {
					x = i*vx;
					y = 0.0 + (vy*i) - (4.9*i*i);
					writer.write((x)+delim+y+delim+path_id+delim+pathOrder+delim+age+System.lineSeparator());
				}
				
				line = reader.readLine();
				
			}
			reader.close();
			writer.close();
			
		
		} catch (Exception e) {
			System.out.println(e.getMessage());			
		}	
	}
		
	}
