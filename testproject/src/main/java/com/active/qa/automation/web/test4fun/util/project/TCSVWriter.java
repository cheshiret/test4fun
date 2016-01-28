package com.active.qa.automation.web.test4fun.util.project;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


/**
 * Created by tchen on 1/18/2016.
 */
public class TCSVWriter {
  TCSVWriter _instance = null;

  public TCSVWriter getInstance() {
    if (null == _instance)
      _instance = new TCSVWriter();
    return _instance;
  }

  private static Logger logger=Logger.getLogger(TCSVWriter.class);
  private static final String COMMA_DELIMITER = ",";
  private static final String NEW_LINE_SEPARATOR = "\n";



  public void CSVWriter(String contract, String nameSpace, List<String[]> list) {
    File file = new File(Util.CSV_DATA+File.separator+contract+".data.csv");
    FileWriter fileWriter = null;

    try {
      fileWriter = new FileWriter(file, true);
      fileWriter.append(nameSpace);
      fileWriter.append(NEW_LINE_SEPARATOR);

      for(int i = 0; i<list.size();i++){
        for(int j=0; j<list.get(i).length; j++) {
          if(j<list.get(i).length-1){
            fileWriter.append(list.get(i)[j]);
            fileWriter.append(COMMA_DELIMITER);
          }
          else{
            fileWriter.append(list.get(i)[j]);
            fileWriter.append(NEW_LINE_SEPARATOR);
          }
        }
      }

    } catch (IOException e) {
      logger.warn("Error" + e);
    }
  }

}
