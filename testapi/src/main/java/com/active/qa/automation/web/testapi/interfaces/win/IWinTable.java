package com.active.qa.automation.web.testapi.interfaces.win;

import com.active.qa.automation.web.testapi.interfaces.ITable;

/**
 * Created by tchen on 1/11/2016.
 */
public interface IWinTable extends ITable, IWinObject {
  public String getSelectedRowValue(int identifyColumn);

  public int getSelectedRowNumber(int identifyColumn);

  public void select(String rowOption, int identifyColumn);

  public void select(int row, int identifyColumn);

  public void select(int row, String columnName);

  public void select(int row);
}

