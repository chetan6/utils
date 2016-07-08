package gibraltar;

import java.io.FileOutputStream;
import java.util.HashMap;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.charts.AxisCrosses;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.ss.usermodel.charts.DataSources;
import org.apache.poi.ss.usermodel.charts.LineChartData;
import org.apache.poi.ss.usermodel.charts.ValueAxis;


public class ExcelCreator {
	
	public static String filesFolder = "C:\\code\\gibraltar\\deliverables\\tm\\thresholdValues\\exceptions\\";
	
	public static void main(String [] a) {
		
		// create test data
		double[] test = new double[10];
		for (int i=0; i<test.length; i++) test[i] = i*1.0;
		
		// dump the test data
		writeData(filesFolder+"poi.xls", null, test, 0);
		
	}
	
	
	public static void writeData(String fileName, Distribution [] distribution, double[] chosenValues, int startIndex) {
		
		try {
			FileOutputStream out = new FileOutputStream(fileName);
			// create a new workbook
			Workbook wb = new XSSFWorkbook();
			// create a new sheet
			Sheet s = wb.createSheet();
			wb.setSheetName(0,"Distribution");
			
			DataFormat df = wb.createDataFormat();
			
			CellStyle intFormat1 = wb.createCellStyle();
			intFormat1.setDataFormat(df.getFormat("#,##0"));
			intFormat1.setFillForegroundColor(HSSFColor.WHITE.index);
			intFormat1.setFillPattern(CellStyle.SOLID_FOREGROUND);
			intFormat1.setBorderBottom(CellStyle.BORDER_THIN);
			intFormat1.setBorderLeft(CellStyle.BORDER_THIN);
			intFormat1.setBorderRight(CellStyle.BORDER_THIN);
			intFormat1.setBorderTop(CellStyle.BORDER_THIN);			
			
			CellStyle intFormat2 = wb.createCellStyle();
			intFormat2.setDataFormat(df.getFormat("#,##0"));
			intFormat2.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
			intFormat2.setFillPattern(CellStyle.SOLID_FOREGROUND);
			intFormat2.setBorderBottom(CellStyle.BORDER_THIN);
			intFormat2.setBorderLeft(CellStyle.BORDER_THIN);
			intFormat2.setBorderRight(CellStyle.BORDER_THIN);
			intFormat2.setBorderTop(CellStyle.BORDER_THIN);			
			
			CellStyle doubleFormat = wb.createCellStyle();
			doubleFormat.setDataFormat(df.getFormat("#,##0.00"));
			
			CellStyle percentageFormat1 = wb.createCellStyle();
			percentageFormat1.setDataFormat(df.getFormat("#,##0.00%"));
			percentageFormat1.setFillForegroundColor(HSSFColor.WHITE.index);
			percentageFormat1.setFillPattern(CellStyle.SOLID_FOREGROUND);
			percentageFormat1.setBorderBottom(CellStyle.BORDER_THIN);
			percentageFormat1.setBorderLeft(CellStyle.BORDER_THIN);
			percentageFormat1.setBorderRight(CellStyle.BORDER_THIN);
			percentageFormat1.setBorderTop(CellStyle.BORDER_THIN);			
			
			CellStyle percentageFormat2 = wb.createCellStyle();
			percentageFormat2.setDataFormat(df.getFormat("#,##0.00%"));
			percentageFormat2.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
			percentageFormat2.setFillPattern(CellStyle.SOLID_FOREGROUND);
			percentageFormat2.setBorderBottom(CellStyle.BORDER_THIN);
			percentageFormat2.setBorderLeft(CellStyle.BORDER_THIN);
			percentageFormat2.setBorderRight(CellStyle.BORDER_THIN);
			percentageFormat2.setBorderTop(CellStyle.BORDER_THIN);
			

			
			Row r = null;
			Cell c = null;
						
			for (int i=startIndex; i<distribution.length; i++) {
				r = s.createRow(i-startIndex);
				
				// save percentile number
				c = r.createCell(0);
				if(i%2==0) {
					c.setCellStyle(intFormat2);
				} else {
					c.setCellStyle(intFormat1);
					
				}
				c.setCellValue(distribution[i].percentile);
				
				// save percentile value
				c = r.createCell(1);
				if(i%2==0) {
					c.setCellStyle(intFormat2);
				} else {
					c.setCellStyle(intFormat1);
					
				}				
				c.setCellValue(distribution[i].value);
				
				// save delta value
				c = r.createCell(2);
				if(i%2==0) {
					c.setCellStyle(percentageFormat2);
				} else {
					c.setCellStyle(percentageFormat1);
					
				}				
				c.setCellValue(distribution[i].delta/100.0);
				
				// save dd value
				c = r.createCell(3);
				if(i%2==0) {
					c.setCellStyle(percentageFormat2);
				} else {
					c.setCellStyle(percentageFormat1);
					
				}				
				c.setCellValue(distribution[i].dd/100.0);
			}
			
			//create one for final values
			Sheet s1 = wb.createSheet();
			wb.setSheetName(1, "Final");
					
			for (int i=0; i < chosenValues.length; i++) {
				r = s1.createRow(i);
				c = r.createCell(0);
				c.setCellStyle(intFormat2);
				c.setCellValue(chosenValues[i]);
			}					
			
			Drawing xlsx_drawing = s.createDrawingPatriarch();
			ClientAnchor anchor = xlsx_drawing.createAnchor(20, 20, 20, 20, 5, 5, 15, 15);
			Chart my_line_chart = xlsx_drawing.createChart(anchor);
			LineChartData data = my_line_chart.getChartDataFactory().createLineChartData();
            ChartAxis bottomAxis = my_line_chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
            ValueAxis leftAxis = my_line_chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);  		
            //exception patch
            ChartDataSource<Number> xs = DataSources.fromNumericCellRange(s, new CellRangeAddress(0, 14, 0, 0));
            ChartDataSource<Number> ys1 = DataSources.fromNumericCellRange(s, new CellRangeAddress(0, 14, 2, 2));
            data.addSeries(xs, ys1);
            my_line_chart.plot(data, new ChartAxis[] { bottomAxis, leftAxis });			
			
			wb.write(out);
			out.close();
			wb.close();
				
		} catch (Exception e) {
			
		}
			
			
	}
	
	public static void writeData(String outputFileName, HashMap<String, Distribution[]> cDistributions, double[][] allValues, double[] finalValues, int startIndex) {
		
		try {
			FileOutputStream out = new FileOutputStream(outputFileName);
			// create a new workbook
			Workbook wb = new XSSFWorkbook();		
			DataFormat df = wb.createDataFormat();
			
			CellStyle intFormat1 = wb.createCellStyle();
			intFormat1.setDataFormat(df.getFormat("#,##0"));
			intFormat1.setFillForegroundColor(HSSFColor.WHITE.index);
			intFormat1.setFillPattern(CellStyle.SOLID_FOREGROUND);
			intFormat1.setBorderBottom(CellStyle.BORDER_THIN);
			intFormat1.setBorderLeft(CellStyle.BORDER_THIN);
			intFormat1.setBorderRight(CellStyle.BORDER_THIN);
			intFormat1.setBorderTop(CellStyle.BORDER_THIN);			
			
			CellStyle intFormat2 = wb.createCellStyle();
			intFormat2.setDataFormat(df.getFormat("#,##0"));
			intFormat2.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
			intFormat2.setFillPattern(CellStyle.SOLID_FOREGROUND);
			intFormat2.setBorderBottom(CellStyle.BORDER_THIN);
			intFormat2.setBorderLeft(CellStyle.BORDER_THIN);
			intFormat2.setBorderRight(CellStyle.BORDER_THIN);
			intFormat2.setBorderTop(CellStyle.BORDER_THIN);			
			
			CellStyle doubleFormat = wb.createCellStyle();
			doubleFormat.setDataFormat(df.getFormat("#,##0.00"));
			
			CellStyle percentageFormat1 = wb.createCellStyle();
			percentageFormat1.setDataFormat(df.getFormat("#,##0.00%"));
			percentageFormat1.setFillForegroundColor(HSSFColor.WHITE.index);
			percentageFormat1.setFillPattern(CellStyle.SOLID_FOREGROUND);
			percentageFormat1.setBorderBottom(CellStyle.BORDER_THIN);
			percentageFormat1.setBorderLeft(CellStyle.BORDER_THIN);
			percentageFormat1.setBorderRight(CellStyle.BORDER_THIN);
			percentageFormat1.setBorderTop(CellStyle.BORDER_THIN);			
			
			CellStyle percentageFormat2 = wb.createCellStyle();
			percentageFormat2.setDataFormat(df.getFormat("#,##0.00%"));
			percentageFormat2.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
			percentageFormat2.setFillPattern(CellStyle.SOLID_FOREGROUND);
			percentageFormat2.setBorderBottom(CellStyle.BORDER_THIN);
			percentageFormat2.setBorderLeft(CellStyle.BORDER_THIN);
			percentageFormat2.setBorderRight(CellStyle.BORDER_THIN);
			percentageFormat2.setBorderTop(CellStyle.BORDER_THIN);
			
			
			// determine how many worksheets do we need to create
			String [] sheetNames = (String []) cDistributions.keySet().toArray(new String[0]);
			
			for (int a=0; a<sheetNames.length; a++) {
				// create a new sheet
				Sheet s = wb.createSheet();
				wb.setSheetName(a, sheetNames[a]);
				
				//create a new chart
                
				Row r = null;
				Cell c = null;
				
				Distribution[] distribution = cDistributions.get(sheetNames[a]);
							
				for (int i=startIndex; i<distribution.length; i++) {
					r = s.createRow(i-startIndex);
					
					// save percentile number
					c = r.createCell(0);
					if(i%2==0) {
						c.setCellStyle(intFormat2);
					} else {
						c.setCellStyle(intFormat1);
						
					}
					c.setCellValue(distribution[i].percentile);
					
					// save percentile value
					c = r.createCell(1);
					if(i%2==0) {
						c.setCellStyle(intFormat2);
					} else {
						c.setCellStyle(intFormat1);
						
					}				
					c.setCellValue(distribution[i].value);
					
					// save delta value
					c = r.createCell(2);
					if(i%2==0) {
						c.setCellStyle(percentageFormat2);
					} else {
						c.setCellStyle(percentageFormat1);
						
					}				
					c.setCellValue(distribution[i].delta/100.0);
					
					// save dd value
					c = r.createCell(3);
					if(i%2==0) {
						c.setCellStyle(percentageFormat2);
					} else {
						c.setCellStyle(percentageFormat1);
						
					}				
					c.setCellValue(distribution[i].dd/100.0);
				}
				
				Drawing xlsx_drawing = s.createDrawingPatriarch();
				ClientAnchor anchor = xlsx_drawing.createAnchor(20, 20, 20, 20, 5, 5, 15, 15);
				Chart my_line_chart = xlsx_drawing.createChart(anchor);
				LineChartData data = my_line_chart.getChartDataFactory().createLineChartData();
                ChartAxis bottomAxis = my_line_chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
                ValueAxis leftAxis = my_line_chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
                leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);  		
                //exception patch
                ChartDataSource<Number> xs = DataSources.fromNumericCellRange(s, new CellRangeAddress(0, 14, 0, 0));
                ChartDataSource<Number> ys1 = DataSources.fromNumericCellRange(s, new CellRangeAddress(0, 14, 2, 2));
                data.addSeries(xs, ys1);
                my_line_chart.plot(data, new ChartAxis[] { bottomAxis, leftAxis });
                
			}
			
			//create one for consolidated values
			Sheet s = wb.createSheet();
			wb.setSheetName(sheetNames.length, "Consolidated");
					
			Row r = null;
			Cell c = null;
			
						
			for (int i=0; i < allValues.length; i++) {
				r = s.createRow(i);
				//c = r.createCell(0);
				//c.setCellValue(sheetNames[i]);
				for (int j=0; j<allValues[i].length; j++) {
					c = r.createCell(j);
					c.setCellStyle(intFormat2);
					c.setCellValue(allValues[i][j]);
				}
			}
			
			//create one for final values
			Sheet s1 = wb.createSheet();
			wb.setSheetName(sheetNames.length+1, "Final");
					
			for (int i=0; i < finalValues.length; i++) {
				r = s1.createRow(i);
				c = r.createCell(0);
				c.setCellStyle(intFormat2);
				c.setCellValue(finalValues[i]);
			}			
					
			wb.write(out);
			out.close();
			wb.close();
				
		} catch (Exception e) {
			
		}
			
			
	}
	
	
	
}
