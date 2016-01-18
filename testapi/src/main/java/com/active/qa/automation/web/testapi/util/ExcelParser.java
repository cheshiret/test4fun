package com.active.qa.automation.web.testapi.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.active.qa.automation.web.testapi.exception.ActionFailedException;
import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

/**
 * Created by tchen on 1/11/2016.
 */
public class ExcelParser {
    private AutomationLogger logger=AutomationLogger.getInstance();
    private HSSFWorkbook wb;
    private HSSFSheet sheet;
    private String filePath;

    public ExcelParser(String xlsFile) throws IOException {
        this.filePath = xlsFile;
        FileInputStream filein=new FileInputStream(xlsFile);
        wb=new HSSFWorkbook(new POIFSFileSystem(filein));

        filein.close();
    }

    public int getNumberOfSheets() {
        return wb.getNumberOfSheets();
    }

    protected HSSFWorkbook getWorkBook() {
        return wb;
    }

    protected HSSFSheet getSheet(int index){
        return wb.getSheetAt(index);
    }

    public String getCellTypeName(int type) {
        String name="";
        switch (type) {
            case Cell.CELL_TYPE_NUMERIC:
                name="NUMERIC";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                name="BOOLEAN";
                break;
            case Cell.CELL_TYPE_ERROR:
                name="ERROR";
                break;
            case Cell.CELL_TYPE_FORMULA:
                name="FORMULA";
                break;
            case Cell.CELL_TYPE_BLANK:
                name="BLANK";
                break;
            case Cell.CELL_TYPE_STRING:
                name="STRING";
                break;
            default:
        }

        return name;
    }

    public List<String> match(ExcelParser target) {
        List<String> msgs=new ArrayList<String>();
        HSSFWorkbook wbt=target.getWorkBook();

		/*matching general information*/
        short numFonts=wb.getNumberOfFonts();
        short numFontst=wbt.getNumberOfFonts();
        if(numFonts != numFontst) {
            String msg="Number of fonts#"+numFontst+" should be "+numFonts;
            logger.warn(msg);
        }

        int numName=wb.getNumberOfNames();
        int numNamet=wbt.getNumberOfNames();
        if(numName != numNamet) {
            String msg="Number of names#"+numNamet+" should be "+numName;
            logger.error(msg);
            msgs.add(msg);
        }

//		int numStyples=wb.getNumCellStyles();
//		int numStyplest=wbt.getNumCellStyles();
//		if(numStyples != numStyplest) {
//			String msg="Number of styles#"+numStyplest+" should be "+numStyples;
//			logger.error(msg);
//			msgs.add(msg);
//		}

        int numSheets=wb.getNumberOfSheets();
        int numSheetst=wbt.getNumberOfSheets();
        if(numSheets != numSheetst) {
            String msg="Number of sheets#"+numSheetst+" should be "+numSheets;
            logger.fatal(msg);
            msgs.add("Fatal: "+msg);

            return msgs;
        }

		/*matching information for each sheet*/
        for(int i=0;i<numSheets;i++) {
            HSSFSheet st=wb.getSheetAt(i);
            HSSFSheet stt=wbt.getSheetAt(i);

            msgs.addAll(matchSheet(st,stt));
        }

        return msgs;
    }

    public String getMergedRegionString(HSSFSheet sheet) {
        int numMerge=sheet.getNumMergedRegions();
        StringBuffer regionsText=new StringBuffer();
        for(int i=0;i<numMerge;i++) {
            if(i!=0) {
                regionsText.append(",");
            }
            regionsText.append(sheet.getMergedRegion(i).formatAsString());
        }

        return regionsText.toString();
    }

    public List<String> matchSheet(HSSFSheet from,HSSFSheet to) {
        List<String> msgs=new ArrayList<String>();

        String name=from.getSheetName();
        String nameT=to.getSheetName();
        if(!name.equals(nameT)) {
            String msg="Sheet name \""+nameT+"\" should be \""+name+"\"";
            logger.fatal(msg);
            msgs.add(msg);

            return msgs;
        }

        String merge=getMergedRegionString(from);
        String mergeT=getMergedRegionString(to);
        if(compareMerge(merge,mergeT) !=true ) {
            String msg="Sheet ["+name+"] - merged regions \""+mergeT+"\" should be \""+merge+"\"";
            logger.error(msg);
            msgs.add(msg);
        }

        int rows=from.getLastRowNum();
        int rowsT=to.getLastRowNum();
        if(rows!=rowsT) {
            String msg="Sheet ["+name+"] - row number#"+rowsT+" should be "+rows;
            logger.fatal(msg);
            msgs.add(msg);

            return msgs;
        }

		/*matching each row */
        Iterator<Row> ri=from.rowIterator();
        Iterator<Row> rit=to.rowIterator();
        while(ri.hasNext()) {
            Row row=ri.next();
            Row rowT=rit.next();
            msgs.addAll(matchRow(row,rowT));
        }

        return msgs;
    }

    private boolean compareMerge(String mergeFrom, String mergeTo) {
        if(mergeFrom.equals(mergeTo)) {
            return true;
        } else {
            String[] mf=mergeFrom.split(",");
            String[] mt=mergeTo.split(",");
            if(mf.length!=mt.length) {
                return false;
            } else {
                List<String> mfl=Arrays.asList(mf);
                List<String> mtl=Arrays.asList(mt);

                Collections.sort(mfl);
                Collections.sort(mtl);
                return mfl.equals(mtl);
            }
        }
    }

    public List<String> matchRow(Row from, Row to) {
        List<String> msgs=new ArrayList<String>();

//		short height=from.getHeight();
//		short heightT=to.getHeight();
//		String msgPrefix="Sheet ["+from.getSheet().getSheetName()+"] - Row#"+from.getRowNum()+"'s ";
//		if(Math.abs(height-heightT)>2) {
//			String msg=msgPrefix+"height "+heightT+" should be "+height;
//			logger.error(msg);
//			msgs.add(msg);
//		}

        int col=from.getLastCellNum();
        int colT=to.getLastCellNum();
        int row=from.getRowNum();
        if(col!=colT) {
            logger.warn("Sheet ["+from.getSheet().getSheetName()+"] - row#"+row+" cell number "+colT+" is not expected "+col);
        }
        int colSize=Math.max(col, colT);

		/*match each cell*/
        for(int i=0;i<colSize;i++){
            Cell cell=from.getCell(i);
            Cell cellT=to.getCell(i);

            if(cell!=null && cellT!=null) {
                msgs.addAll(matchCell(cell,cellT));
            } else if(cell==null && cellT!=null && cellT.getCellType()!=Cell.CELL_TYPE_BLANK) {
                String msg="Sheet ["+from.getSheet().getSheetName()+"] - cell("+row+","+i+") value "+cellT.toString()+" is not expected null";
                logger.error(msg);
                msgs.add(msg);
            } else if(cell!=null && cellT==null && cell.getCellType()!=Cell.CELL_TYPE_BLANK) {
                String msg="Sheet ["+from.getSheet().getSheetName()+"] - cell("+row+","+i+") null/blank is not expected "+cell.toString();
                logger.error(msg);
                msgs.add(msg);
            }
        }

        return msgs;
    }

    public List<String> matchCell(Cell from, Cell to) {
        List<String> msgs=new ArrayList<String>();
        boolean match=false;
        String cellText=null;
        String cellTextT=null;

        int type=from.getCellType();
        int typeT=to.getCellType();
        if(type!=typeT) {
            //when cell value is "0", cell type can be either "NUMERIC" or "STRING", this is ok for us
            if(type == Cell.CELL_TYPE_NUMERIC && typeT == Cell.CELL_TYPE_STRING) {
                double num=from.getNumericCellValue();
                String numT=to.getStringCellValue();
                if(Math.abs(num-0.0)<0.001 && numT.equalsIgnoreCase("0")) {
                    match=true;
                } else {
                    cellText=Double.toString(num);
                    cellTextT=numT;
                }
            } else if( type == Cell.CELL_TYPE_STRING && typeT == Cell.CELL_TYPE_NUMERIC) {
                double numT=to.getNumericCellValue();
                String num=from.getStringCellValue();
                if(Math.abs(numT-0.0)<0.001 && num.equalsIgnoreCase("0")) {
                    match=true;
                } else {
                    cellTextT=Double.toString(numT);
                    cellText=num;
                }
            } else {
                String msg="Sheet ["+from.getSheet().getSheetName()+"] - cell("+from.getRowIndex()+","+from.getColumnIndex()+")'s type "+ getCellTypeName(typeT)+" "+to.toString()+" should be "+getCellTypeName(type)+" "+from.toString();
                logger.fatal(msg);
                msgs.add("Fatal: "+msg);

                return msgs;
            }
        } else {
            switch (type) {
                case Cell.CELL_TYPE_NUMERIC:
                    double num=from.getNumericCellValue();
                    double numT=to.getNumericCellValue();
                    if(Math.abs(num-numT)<0.001) {
                        match=true;
                    } else {
                        cellText=Double.toString(num);
                        cellTextT=Double.toString(numT);
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    boolean value=from.getBooleanCellValue();
                    boolean valueT=to.getBooleanCellValue();
                    if(value==valueT) {
                        match=true;
                    } else {
                        cellText=Boolean.toString(value);
                        cellTextT=Boolean.toString(valueT);
                    }
                    break;
                case Cell.CELL_TYPE_ERROR:
                    byte er=from.getErrorCellValue();
                    byte erT=to.getErrorCellValue();
                    if(er==erT) {
                        match=true;
                    } else {
                        cellText=Byte.toString(er);
                        cellTextT=Byte.toString(erT);
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    String str=from.getStringCellValue();
                    String strT=to.getStringCellValue();
                    if((str.startsWith("Run Date and Time") && strT.startsWith("Run Date and Time"))||(str.startsWith("RUN DATE AND TIME") && strT.startsWith("RUN DATE AND TIME"))) {
                        match=true;
                    } else if(str.equals(strT)) {
                        match=true;
                    } else {
                        cellText=str;
                        cellTextT=strT;
                    }
                    break;
                case Cell.CELL_TYPE_BLANK:
                    match=true;
                    break;
                default:
                    logger.warn("Cell is skipped due to type value \""+type+"\"");
                    match=true;
            }
        }
        if(!match) {
            String msg="Sheet ["+from.getSheet().getSheetName()+"] - cell("+from.getRowIndex()+","+from.getColumnIndex()+")'s cell value \""+cellTextT+"\" should be \""+cellText+"\"";
            logger.fatal(msg);
            msgs.add(msg);

            return msgs;
        }

        msgs.addAll(matchCellStyle(from, to));

        return msgs;
    }

    public List<String> matchCellStyle(Cell from, Cell to) {
        List<String> msgs=new ArrayList<String>();

        CellStyle st=from.getCellStyle();
        CellStyle stT=to.getCellStyle();

        String msgPrefix="Sheet ["+from.getSheet().getSheetName()+"] - cell("+from.getRowIndex()+","+from.getColumnIndex()+") has unexpected ";
        if(st.getAlignment()!=stT.getAlignment()) {
            String msg=msgPrefix+"alignment";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getVerticalAlignment()!=stT.getVerticalAlignment()) {
            String msg=msgPrefix+"vertical alignment";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getBorderBottom()!=stT.getBorderBottom()) {
            String msg=msgPrefix+"bottom border";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getBorderLeft()!=stT.getBorderLeft()) {
            String msg=msgPrefix+"left border";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getBorderRight()!=stT.getBorderRight()) {
            String msg=msgPrefix+"right border";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getBorderTop()!=stT.getBorderTop()) {
            String msg=msgPrefix+"top border";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getTopBorderColor()!=stT.getTopBorderColor()) {
            String msg=msgPrefix+"top border color";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getBottomBorderColor()!=stT.getBottomBorderColor()) {
            String msg=msgPrefix+"bottom border color";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getLeftBorderColor()!=stT.getLeftBorderColor()) {
            String msg=msgPrefix+"left border color";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getRightBorderColor()!=stT.getRightBorderColor()) {
            String msg=msgPrefix+"right border color";
            logger.error(msg);
            msgs.add(msg);
        }

//		if(st.getDataFormat()!=stT.getDataFormat()) {
//			String msg=msgPrefix+"data format "+stT.getDataFormat()+"("+st.getDataFormat()+")";
//			logger.warn(msg);
//		}

        if(st.getFillBackgroundColor()!=stT.getFillBackgroundColor()) {
            String msg=msgPrefix+"background color";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getFillForegroundColor()!=stT.getFillForegroundColor()) {
            String msg=msgPrefix+"foreground color";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getFillPattern()!=stT.getFillPattern()) {
            String msg=msgPrefix+"fill pattern";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getFontIndex()!=stT.getFontIndex()) {
            String msg=msgPrefix+"font";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getRotation()!=stT.getRotation()) {
            String msg=msgPrefix+"rotation";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getIndention()!=stT.getIndention()) {
            String msg=msgPrefix+"indention";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getWrapText()!=stT.getWrapText()) {
            String msg=msgPrefix+"wrap text setting";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getLocked()!=stT.getLocked()) {
            String msg=msgPrefix+"locked setting";
            logger.error(msg);
            msgs.add(msg);
        }

        if(st.getHidden()!=stT.getHidden()) {
            String msg=msgPrefix+"hidden setting";
            logger.error(msg);
            msgs.add(msg);
        }

        return msgs;
    }

    public List<String> getRowInfo(int sheetNum,int rowNum){
        sheet = getSheet(sheetNum);

        Row row = sheet.getRow(rowNum);
        List<String> values = new ArrayList<String>();
        Iterator<Cell> iter = row.cellIterator();

        while(iter.hasNext()){
            values.add(getCellValue((Cell)iter.next()));
        }
        return values;
    }

    public List<String> getColumnInfo(int sheetNum,int colNum){
        sheet = getSheet(sheetNum);

        List<String> values = new ArrayList<String>();
        int totalRowNum = sheet.getLastRowNum();
        for(int i=0;i<totalRowNum;i++){
            values.add(getCellValue(sheet.getRow(i).getCell(colNum)));
        }

        return values;
    }

    public int getTotalRowNum(int sheetNum){
        sheet = getSheet(sheetNum);
        return sheet.getLastRowNum();
    }

    public int findRow(int sheetNum,int colNum,String value){
        List<String> values = getColumnInfo(sheetNum, colNum);
        int rowNum = -1;
        for(int i=0;i<values.size();i++){
            if(StringUtil.notEmpty(values.get(i))&&values.get(i).equals(value)){
                rowNum = i;
                break;
            }
        }
        return rowNum;
    }

    public int finColumn(int sheetNum,int rowNum,String value){
        List<String> values = getRowInfo(sheetNum, rowNum);
        int colNum = -1;
        for(int i=0;i<values.size();i++){
            if(StringUtil.notEmpty(values.get(i))&&values.get(i).equals(value)){
                colNum = i;
                break;
            }
        }
        return colNum;
    }

    public void setStringCellValue(int sheetNum,int rowNum,int colNum, String value){
        setCellValue(sheetNum, rowNum, colNum, value);
    }

    public void write(){
        try {
            FileOutputStream output = new FileOutputStream(filePath);
            wb.write(output);
            output.close();
        } catch (FileNotFoundException  e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setCellValue(int sheetNum,int rowNum,int colNum,Object value){
        HSSFSheet sheet = getSheet(sheetNum);
        HSSFRow row = sheet.getRow(rowNum);
        if(row==null){
            row = sheet.createRow(rowNum);
        }

        setCellValue(row.createCell(colNum), value);
    }

    protected void setCellValue(Cell cell, Object value){
        if(cell==null){
            throw new NullPointerException("Given Cell "+cell+" is null!");
        }
        if(value instanceof Boolean){
            cell.setCellValue((Boolean)value);
        }else if(value instanceof String){
            cell.setCellValue(value.toString());
        }else if(value instanceof Double){
            cell.setCellValue((Double)value);
        }else{
            throw new ItemNotFoundException("Not Handled Object type.");
        }
        write();
    }

    /**
     * This method used to get cell value from given index, and all index start from 0
     * @param sheetNum
     * @param rowNum
     * @param colNum
     * @return
     */
    public String getCellValue(int sheetNum,int rowNum,int colNum){
        sheet = getSheet(sheetNum);
        return getCellValue(sheet.getRow(rowNum).getCell(colNum));
    }

    protected String getCellValue(Cell cell){
        if(null==cell){
            return null;
        }
        int type = cell.getCellType();
        String cellText = null;
        switch(type){
            case Cell.CELL_TYPE_NUMERIC:
                double num=cell.getNumericCellValue();
                cellText=Double.toString(num);
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                boolean value=cell.getBooleanCellValue();
                cellText=Boolean.toString(value);
                break;
            case Cell.CELL_TYPE_ERROR:
                byte er=cell.getErrorCellValue();
                cellText=Byte.toString(er);
                break;
            case Cell.CELL_TYPE_STRING:
                String str=cell.getStringCellValue();
                cellText=str;
                break;
            case Cell.CELL_TYPE_BLANK:
                break;
            default:
                logger.warn("Cell is skipped due to type value \""+type+"\"");
        }
        return cellText;
    }

    /**
     * The method used to write given list values into specific excel file
     * @param path
     * @param values
     */
    public void writeExcel(String path,List<String[]> values){
        logger.info("Write Excel...");
        sheet = getSheet(0);
        for(int rowNum=0;rowNum<values.size();rowNum++){
            HSSFRow row = sheet.createRow(rowNum);
            String[] strs = values.get(rowNum);
            for(int colNum=0;colNum<strs.length;colNum++){
                row.createCell(colNum).setCellValue(strs[colNum]);
            }
        }
        FileOutputStream fileOut;
        try {
            fileOut = new FileOutputStream(path);
            wb.write(fileOut);
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Write excel successfully....");
    }

    public static List<String> match(ExcelParser from, ExcelParser to) {
        return from.match(to);
    }

    public static List<String> match(String fromFile, String toFile) {
        ExcelParser from;
        try {
            from = new ExcelParser(fromFile);
            ExcelParser to=new ExcelParser(toFile);
            return from.match(to);
        } catch (IOException e) {
            throw new ActionFailedException("Failed due to "+e.getMessage());
        }


    }
}

