package com.active.qa.automation.web.test4fun.project.util;

import com.active.qa.automation.web.testapi.util.TestProperty;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

/**
 * Created by tchen on 1/18/2016.
 */
public class TCSVUtil extends Util {


  public static void initTestProperty() {
    TestProperty.load(Util.TEST_PROPERTY);
    if (TestProperty.getProperty("target_env").equalsIgnoreCase("live")) {
      //load the production sanity test information
      loadLiveInformation();
    }

    TestProperty.putProperty("property.folder", PROPERTY_PATH);

  }

  public static boolean containsField() {
    boolean containsfields = TestProperty.getBooleanProperty("csv.contain.field", true);
    if (containsfields) {
      return true;
    } else {
      return false;
    }
  }

  public static boolean returnResult() {
    boolean returnresult = TestProperty.getBooleanProperty("csv.result.return", true);
    if (returnresult) {
      return true;
    } else {
      return false;
    }
  }

  public static String fieldType() {
    String type = TestProperty.getProperty("csv.field.type");
    if (type.contains("col")) {
      return "column";
    } else {
      return "row";
    }
  }

  public static void xlsxToCSV(File inputFile, File outputFile) {
    // For storing data into CSV files
    StringBuffer data = new StringBuffer();

    try {
      FileOutputStream fos = new FileOutputStream(outputFile);
      // Get the workbook object for XLSX file
      XSSFWorkbook wBook = new XSSFWorkbook(new FileInputStream(inputFile));
      // Get first sheet from the workbook
      XSSFSheet sheet = wBook.getSheetAt(0);
      Row row;
      Cell cell;
      // Iterate through each rows from first sheet
      Iterator<Row> rowIterator = sheet.iterator();

      while (rowIterator.hasNext()) {
        row = rowIterator.next();

        // For each row, iterate through each columns
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {

          cell = cellIterator.next();

          switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
              data.append(cell.getBooleanCellValue() + ",");
              data.append("\r\n");
              break;
            case Cell.CELL_TYPE_NUMERIC:
              data.append(cell.getNumericCellValue() + ",");
              data.append("\r\n");
              break;
            case Cell.CELL_TYPE_STRING:
              data.append(cell.getStringCellValue() + ",");
              data.append("\r\n");
              break;

            case Cell.CELL_TYPE_BLANK:
              data.append("" + ",");
              data.append("\r\n");
              break;
            default:
              data.append(cell + ",");
              data.append("\r\n");
          }
        }
      }

      fos.write(data.toString().getBytes());
      fos.close();

    } catch (Exception ioe) {
      ioe.printStackTrace();
    }
  }

  public static void csvToXlsx(File inputFile, File outputFile) {

  }

  public String csvCompare(File f1, File f2) {
    String result = null;
    //CSVComparator
    //loop to run CSVComparator

    //way 1
//		if [ ! -f runresults.txt ]; then
//	    touch runresults.txt
//	fi
//
//	for i in {1..10}
//	  do
//	    echo "Running... $i"
//	    java -jar myjar.jar >> runresults.txt &
//	    echo "Sleeping 2 seconds..."
//	    sleep 2
//	done


    //way2
//		for /L %%i in (1 1 %numToRun%) do java -jar Lab1.jar %numParam% %strParam%
//		If you want to use multiple lines, then you must either use line continuation
//
//		for /L %%i in (1 1 %numToRun%) do ^
//		  java -jar Lab1.jar %numParam% %strParam%
//		or parentheses
//
//		for /L %%i in (1 1 %numToRun%) do (
//		  java -jar Lab1.jar %numParam% %strParam%
//		  REM parentheses are more convenient for multiple commands within the loop
//		)
    return result;
  }

  public String[] csvCompareDetails(File f1, File f2) {
    String[] result = null;
//CSVComparator
    return result;
  }


}

