package com.active.qa.automation.web.testdriver.driver.selenium;

import java.util.ArrayList;
import java.util.List;

import com.active.qa.automation.web.testapi.ActionFailedException;
import com.active.qa.automation.web.testapi.interfaces.html.IHtmlTable;
import com.active.qa.automation.web.testapi.util.RegularExpression;
import com.active.qa.automation.web.testapi.util.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by tchen on 1/6/2016.
 */
class TableObject extends HtmlObject implements IHtmlTable {
    private boolean tableready;
    private List<List<Element>> tableGrid;
    private int rowSize;
    private int colSize;
    public TableObject(Element element, String... handler) {
        super(element,handler);
        tableready=false;
        this.tableGrid=new ArrayList<List<Element>>();
        rowSize=-1;
        colSize=-1;
    }

    protected List<List<Element>> getTable() {
        if(!tableready) {
            constructTable();
        }

        return tableGrid;
    }

    private void constructTable() {
//		TableObject t=this;
        if(element==null)
            return;

        if(!tableready) {
            List<Element> row=new ArrayList<Element>();
            Elements es=element.children();

            for(Element e:es) {
                String tag=e.tagName();
                if(tag.equalsIgnoreCase("tr")) {
                    row.add(e);
                } else if(tag.toUpperCase().matches("THEAD|TFOOT|TBODY")) {
                    Elements rows=e.children();
                    for(Element ee:rows) {
                        tag=ee.tagName();
                        if(tag.equalsIgnoreCase("tr")) {
                            row.add(ee);
                        }
                    }
                }
            }
            int currentColSize = 0;
            rowSize=row.size();
            for(int i=0;i<rowSize;i++) {
                Element aRow=row.get(i);
                Elements rowElements=aRow.children();
                currentColSize = rowElements.size();
                colSize=Math.max(colSize, currentColSize);
                tableGrid.add(rowElements);
            }
        }

        tableready=true;
    }


    @Override
    public int columnCount() {
        if(!tableready) {
            this.constructTable();
        }
        return colSize;
    }

    @Override
    public int rowCount() {
        if(!tableready) {
            this.constructTable();
        }
        return rowSize;
    }

    @Override
    public int findColumn(int row, Object value) {
        return findColumn(0,row,value);
    }

    @Override
    public int findColumn(int startCol, int row, Object text) {
        if(!tableready) {
            constructTable();
        }

        try {
            List<Element> aRow=tableGrid.get(row);
            int aRowSize=aRow.size();
            for (int i = startCol; i < aRowSize; i++) {
                Element cell = aRow.get(i);
                String cellTxt = StringUtil.convertSpaceUnicode2ASCII(cell.text());
                if (cell != null && RuntimeUtil.matchOrEqual(cellTxt.trim(), text)){
                    return i;
                }
            }
            return -1;
        } catch (Exception e) {
            throw new ActionFailedException(e);
        }
    }

    public int[] findColumns(int startCol, int row, Object text) {
        if(!tableready) {
            constructTable();
        }
        List<Integer> cols = new ArrayList<Integer>();
        try {
            List<Element> aRow=tableGrid.get(row);
            int aRowSize=aRow.size();
            for (int i = startCol; i < aRowSize; i++) {
                if(i>=aRow.size()) {
                    break;
                }
                Element cell = aRow.get(i);
                String cellTxt = StringUtil.convertSpaceUnicode2ASCII(cell.text());
                if (cell != null && RuntimeUtil.matchOrEqual(cellTxt.trim(), text)){
                    cols.add(i);
                }
            }
        } catch (Exception e) {
            throw new ActionFailedException(e);
        }

        int intCols[] = new int[cols.size()];
        for(int i = 0; i < cols.size(); i ++) {
            intCols[i] = cols.get(i);
        }

        return intCols;
    }

    public int[] findRows(int col, Object value) {
        return findRows(0, col, value);
    }

    public int[] findRows(int startRow, int col, Object value) {
        if(!tableready) {
            constructTable();
        }
        List<Integer> rows = new ArrayList<Integer>();
        for (int row = startRow; row < rowSize; row++) {
            List<Element> aRow=tableGrid.get(row);
            if(aRow.size()>col) {
                Element cell = aRow.get(col);

                String cellTxt = StringUtil.convertSpaceUnicode2ASCII(cell.text());
                if (cell != null && RuntimeUtil.matchOrEqual(cellTxt.trim(), value)) {
                    rows.add(row);
                }
            }
        }

        int intRows[] = new int[rows.size()];
        for(int i = 0; i < rows.size(); i ++) {
            intRows[i] = rows.get(i);
        }
        return intRows;
    }

    @Override
    public int findRow(int col, Object value) {
        return findRow(0, col, value);
    }

    @Override
    public int findRow(int startRow, int col, Object value) {
        if(!tableready) {
            constructTable();
        }
        for (int row = startRow; row < rowSize; row++) {
            List<Element> aRow=tableGrid.get(row);
            if(aRow.size()>col) {
                Element cell = aRow.get(col);

                String cellTxt = StringUtil.convertSpaceUnicode2ASCII(cell.text());
                if (cell != null && RuntimeUtil.matchOrEqual(cellTxt.trim(), value)) {
                    return row;
                }
            }
        }
        return -1;
    }

    @Override
    public String getCellValue(int row, int col) {
        if(!tableready) {
            constructTable();
        }
        List<Element> aRow=tableGrid.get(row);
        if(aRow.size()>col) {
            return StringUtil.convertSpaceUnicode2ASCII(aRow.get(col).text()).trim();
        } else {
            return null;
        }
    }

    @Override
    public List<String> getCellValue(String pattern) {
        if(!tableready) {
            constructTable();
        }

        ArrayList<String> values=new ArrayList<String>();
        for (int i = 0; i < rowSize; i++) {
            List<Element> aRow=tableGrid.get(i);
            int aRowSize=aRow.size();
            for (int j = 0; j < colSize; j++) {
                if(aRowSize>j) {
                    Element cell = aRow.get(j);
                    if (cell == null || cell.text().length() < 1){
                        continue;
                    }
                    String text=StringUtil.convertSpaceUnicode2ASCII(cell.text()).trim();
                    if (text.matches(pattern))
                        values.add(RegularExpression.getMatches(text, pattern)[0]);
                } else {
                    break;
                }
            }
        }
        return values;
    }

    @Override
    public List<String> getColumnValues(int col) {
        if(!tableready) {
            constructTable();
        }
        ArrayList<String> values = null;
        values = new ArrayList<String>(rowSize);
        for (int i = 0; i < rowSize; i++) {
            List<Element> aRow=tableGrid.get(i);
            if(aRow.size()-1<col){//Added for getColumnValue with table header which was less row than table body
                values.add("");
                continue;
            }
            Element cell=aRow.get(col);
            String text="";
            if(cell!=null ) {
                text=StringUtil.convertSpaceUnicode2ASCII(cell.text()).trim();
            }
            values.add(text);
        }

        return values;
    }

    @Override
    public List<String> getRowValues(int row) {
        if(!tableready) {
            constructTable();
        }
        ArrayList<String> values = null;
        values = new ArrayList<String>(colSize);
        List<Element> aRow=tableGrid.get(row);
        int aRowSize=aRow.size();
        for (int i = 0; i < aRowSize; i++) {
            Element cell=aRow.get(i);
            values.add(StringUtil.convertSpaceUnicode2ASCII(cell.text()).trim());
        }
        return values;
    }

    @Override
    public String[][] getTableValues() {
        if(!tableready) {
            constructTable();
        }

        String[][] tableValues = new String[rowSize][colSize];
        for (int r = 0; r < rowSize; r++) {
            List<Element> aRow=tableGrid.get(r);
            int aColSize=Math.min(colSize, aRow.size());
            for (int c = 0; c < aColSize; c++) {
                Element cell = aRow.get(c);
                tableValues[r][c] = StringUtil.convertSpaceUnicode2ASCII(cell.text().trim());
            }
        }

        return tableValues;
    }

}

