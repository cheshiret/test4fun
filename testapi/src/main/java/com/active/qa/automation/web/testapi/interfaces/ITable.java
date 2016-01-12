package com.active.qa.automation.web.testapi.interfaces;

import java.util.List;

/**
 * Created by tchen on 1/11/2016.
 */
public interface ITable {
    public int rowCount();

    public int columnCount();

    public String getCellValue(int row,int col);

    public List<String> getCellValue(String pattern);

    public int findColumn(int row,Object value);

    public int findRow(int col, Object value);

    public int findRow(int startRow, int col, Object value);

    public int findColumn(int startCol, int row, Object value);

    public int[] findColumns(int startCol, int row, Object value);

    public List<String> getColumnValues(int col);

    public List<String> getRowValues(int row);

    public String[][] getTableValues();

    public int[] findRows(int col, Object value);

}

