package com.active.qa.automation.web.test4fun.project.util;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by tchen on 1/18/2016.
 */
public class TCSVLoader {

  TCSVLoader _instance = null;

  public TCSVLoader getInstance() {
    if (null == _instance)
      _instance = new TCSVLoader();
    return _instance;
  }

  private static Logger logger = Logger.getLogger(TCSVLoader.class);
  public static String read_file_path = Util.CSV_DATA;
  public static String write_file_path = Util.CSV_OUTPUT;
  public static ArrayList<List<String>> t = new ArrayList<List<String>>();
  public static int colnum = 0;
  public static int rownum = 0;
  private CSVWriter writer = null;
  private CSVReader reader = null;

  public void loadFile(String path, String file_name) {
    try {
      this.filenotexist(path, file_name);
      List<String> list = new ArrayList<String>();
      reader = new CSVReader(new FileReader(path + File.separator + file_name));
      String[] nextLine = null;

      int i = 0;
      int k = 0;
      while ((nextLine = reader.readNext()) != null) {
        String[] stringArray = list.toArray(new String[nextLine.length]);
        for (int j = 0; j < nextLine.length; j++) {
          stringArray[j] = nextLine[j];
          k = j + 1;

        }
        List<String> t2 = Arrays.asList(stringArray);
        t.add(t2);
        i++;
      }
      reader.close();
      rownum = i;
      colnum = k;
      logger.info("Data of CSV file has been saved into the array:");
    } catch (IOException e) {
      logger.warn("Error" + e);
    }
  }

  public void loadFile(String file_name) {
    loadFile(read_file_path, file_name);

  }

  public void reloadFile(String path, String file_name) {
    t.clear();
    try {
      reader.close();
      writer.close();
      this.loadFile(path, file_name);
      this.writeFile(path, file_name);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public ArrayList<List<String>> Data() {
    return t;
  }

  public List<String> getRow(int i) {
    try {
      return t.get(i);
    } catch (IndexOutOfBoundsException e) {
      logger.warn("Error" + e);
      return null;
    }

  }

  public List<String> getCol(int i) {
    try {
      List<String> Col = new ArrayList<String>();
      for (int j = 0; j < rownum; j++) {
        Col.add(t.get(j).get(i));
      }
      return Col;
    } catch (IndexOutOfBoundsException e) {
      logger.warn("Error" + e);
      return null;
    }

  }


  public int getRowNum() {
    return rownum;
  }

  public int getColNum() {
    return colnum;
  }

  public List<String> getFieldName() {
    boolean haveField = TCSVUtil.containsField();
    String getfieldType = TCSVUtil.fieldType();
    if (haveField) {
      if (getfieldType.contains("col")) {
        System.out.println("1");
        return t.get(0);
      } else {
        List<String> fN = new ArrayList<String>();
        for (int i = 0; i < colnum; i++) {
          fN.add(t.get(i).get(0));
        }
        System.out.println("2");
        return fN;
      }
    } else {
      logger.warn("There is no field name saved in the CSV.");
      return null;
    }
  }


  public void writeFile(String path, String file_name, boolean append) {
    try {
      this.fileexist(path, file_name);
      writer = new CSVWriter(new FileWriter(path + File.separator + file_name, append), ',', '"', '"', "\n");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void writeFile(String path, String file_name) throws IOException {
    writeFile(path, file_name, false);
  }

  public void writeFile(String file_name) throws IOException {
    writeFile(write_file_path, file_name, false);
  }

  public void writeFile(String file_name, boolean append) {
    writeFile(write_file_path, file_name, append);
  }

  public void writeCSVPerCell(int col, int row, String value) {
    ArrayList<List<String>> od = (ArrayList<List<String>>) t.clone();
    ArrayList<List<String>> rs = new ArrayList<List<String>>();

    for (int i = 0; i < od.size(); i++) {
      if (i == row) {
        List<String> temp = new ArrayList<String>();
        for (int j = 0; j < od.get(i).size(); j++) {
          if (j == col) {
            temp.add(j, value);
          } else {
            temp.add(j, od.get(i).get(j));
          }
        }
//				temp.add("\n");
        rs.add(temp);
      } else {
        rs.add(t.get(i));
      }

    }

    List<String[]> rs_ = new ArrayList<String[]>();
    for (int i = 0; i < rs.size(); i++) {
      rs_.add(rs.get(i).toArray(new String[rs.get(i).size()]));

    }
    writer.writeAll(rs_);


  }

  public void writeCSVPerCol(int col, String[] value) {
    ArrayList<List<String>> od = (ArrayList<List<String>>) t.clone();
    ArrayList<List<String>> rs = new ArrayList<List<String>>();

    for (int i = 0; i < od.size(); i++) {
      List<String> temp = new ArrayList<String>();
      for (int j = 0; j < od.get(i).size(); j++) {
        if (j == col) {
          temp.add(j, value[i]);
        } else {
          temp.add(j, od.get(i).get(j));
        }


      }
//				temp.add("\n");
      rs.add(temp);
    }
    List<String[]> rs_ = new ArrayList<String[]>();
    for (int i = 0; i < rs.size(); i++) {
      rs_.add(rs.get(i).toArray(new String[rs.get(i).size()]));

    }


    try {
      writer.writeAll(rs_);
      writer.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void writeCSVAppendLine(String[] line) {
    StringWriter writer = new StringWriter();
    CSVWriter csvwriter = new CSVWriter(writer, '\n');
    csvwriter.writeNext(line);
    try {
      csvwriter.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void reWriteCSV(List<String[]> lines) {

    try {
      writer.writeAll(lines);
      writer.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  //insert sepcific Line
  public void finalize() {
    try {
      reader.close();
      writer.close();
      System.exit(0);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public void fileexist(String path, String file_name) {
    this.direxist(path);
    File file = new File(path + File.separator + file_name);
    if (!file.exists()) {
      try {
        logger.info("Create new file with " + file_name + " under " + path);
        file.createNewFile();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public void filenotexist(String path, String file_name) {
    File file = new File(path + File.separator + file_name);
    if (!file.exists()) {
      logger.warn("There is no file with " + file_name + " under " + path);
      System.exit(0);
    }
  }

  public void direxist(String path) {
    File file = new File(path);
    if (!file.exists() && !file.isDirectory()) {
      logger.warn("There is no directory with " + path);
      file.mkdir();
    } else {
      logger.info("The directory exists with " + path);
    }
  }

}


