package gibraltar;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.record.chart.LineFormatRecord;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.charts.AxisCrosses;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.ss.usermodel.charts.ChartLegend;
import org.apache.poi.ss.usermodel.charts.DataSources;
import org.apache.poi.ss.usermodel.charts.LegendPosition;
import org.apache.poi.ss.usermodel.charts.LineChartData;
import org.apache.poi.ss.usermodel.charts.ValueAxis;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ChartTest {
	 public static void main(String[] args) throws Exception {
	        Workbook wb = new XSSFWorkbook();
	        Sheet sheet = wb.createSheet("linechart");
	        final int NUM_OF_ROWS = 3;
	        final int NUM_OF_COLUMNS = 10;

	        // Create a row and put some cells in it. Rows are 0 based.
	        Row row;
	        Cell cell;
	        for (int rowIndex = 0; rowIndex < NUM_OF_ROWS; rowIndex++) {
	            row = sheet.createRow((short) rowIndex);
	            for (int colIndex = 0; colIndex < NUM_OF_COLUMNS; colIndex++) {
	                cell = row.createCell((short) colIndex);
	                cell.setCellValue(colIndex * (rowIndex + 1));
	            }
	        }

	        Drawing drawing = sheet.createDrawingPatriarch();
	        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 5, 0, 5, 10, 25);

	        Chart chart = drawing.createChart(anchor);
	        ChartLegend legend = chart.getOrCreateLegend();
	        legend.setPosition(LegendPosition.TOP_RIGHT);

	        
	        List records = new ArrayList();
	        LineChartData data = chart.getChartDataFactory().createLineChartData();
	        LineFormatRecord lfr = new LineFormatRecord();
	        lfr.setLineColor(HSSFColor.DARK_RED.index);
	        lfr.setLinePattern(LineFormatRecord.LINE_PATTERN_DASH_DOT);
	        records.add(lfr);


	        // Use a category axis for the bottom axis.
	        ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
	        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
	        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

	        ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(0, 0, 0, NUM_OF_COLUMNS - 1));
	        ChartDataSource<Number> ys1 = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(1, 1, 0, NUM_OF_COLUMNS - 1));
	        ChartDataSource<Number> ys2 = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(2, 2, 0, NUM_OF_COLUMNS - 1));


	        data.addSeries(xs, ys1);
	        data.addSeries(xs, ys2);

	        chart.plot(data, bottomAxis, leftAxis);

	        // Write the output to a file
	        FileOutputStream fileOut = new FileOutputStream("C:\\code\\gibraltar\\deliverables\\tm\\thresholdValues\\ooxml-line-chart.xlsx");
	        wb.write(fileOut);
	        wb.close();
	        fileOut.close();
	    }
}
