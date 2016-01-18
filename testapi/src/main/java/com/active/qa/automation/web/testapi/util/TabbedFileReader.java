package com.active.qa.automation.web.testapi.util;

import java.io.*;
import java.util.regex.Pattern;

/**
 * Reads lines from a tab-delimited file and parses them into columns (requires JDK 1.4).
 * Created by tchen on 1/11/2016.
 */
public class TabbedFileReader {

  /**
   * Store reference to file for future reference.
   */
  protected File file;

  /**
   * Input file.
   */
  private BufferedReader reader;

  /**
   * Current line number.
   */
  private int lineNumber = 0;

  /**
   * Regex pattern to find comments
   */
  private Pattern commentPattern = null;

  /**
   * Columns of data from last line read, one array element per column.  null if no data read or EOF
   */
  public String[] columns = null;

  /**
   * Creates a reader using the specified filename as input.
   *
   * @param filename the name of the file to read from
   * @throws FileNotFoundException if the named file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading.
   */
  public TabbedFileReader(String filename) throws FileNotFoundException {
    this(new File(filename));
  }

  /**
   * Creates a reader using the specified directorypath and filename as input.
   *
   * @param folderpath the path of the directory to read from
   * @param filename   the name of the file to read from
   * @throws FileNotFoundException if the named file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading.
   */
  public TabbedFileReader(String folderpath, String filename)
      throws FileNotFoundException {
    this(new File(folderpath, filename));
  }

  /**
   * Creates a reader using the specified file as input.
   *
   * @param filename the name of the file to read from
   * @throws FileNotFoundException if the named file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading.
   */
  public TabbedFileReader(File file) throws FileNotFoundException {
    this.file = file;
    reader = new BufferedReader(new FileReader(file));
    lineNumber = 0;
  }

  /**
   * Closes the reader.
   *
   * @throws IOException if an I/O error occurs
   */
  public void close() throws IOException {
    reader.close();
    lineNumber = 0;
  }

  /**
   * Reads the next line of text from the file and parses it into columns.
   *
   * @return true if more data can be read or false if the end of the file has been reached.
   * @throws IOException if an I/O error occurs
   */
  public boolean readNext() throws IOException {

    // Read the next line from the file
    String line = reader.readLine();
    lineNumber++;

    // Check for EOF
    if (line == null) {
      // EOF found
      columns = null;

      return false;
    } else {

      // Remove comments
      if (commentPattern != null) {
        line = commentPattern.matcher(line).replaceAll("");
      }

      //skip the DB check
      if (TestProperty.getProperty("db.check") != null
          && TestProperty.getProperty("db.check").toString().equals(
          "false") && line.matches("^orms\\.db\\.*")) {
        line = commentPattern.matcher(line).replaceAll("");
      }

      // Split data into columns
      columns = line.split("\t");

      return true;

    }

  }

  /**
   * Get the current line number.
   *
   * @return the current line number
   */
  public int getLineNum() {
    return lineNumber;
  }

  /**
   * Sets the regex pattern used to find comments.  Any string matching pattern is removed from input line before being parsed.
   */
  public void setCommentPattern(String commentPattern) {
    this.commentPattern = Pattern.compile(commentPattern);
  }

  // Test Code
  //	try {
  //
  //		TabbedFileReader in = new TabbedFileReader(fileName);
  //
  //		// comments are
  //		//   - # followed by any character zero or more times
  //		//   - // followed by any character zero or more times
  //		//   - /* followed by any character zero or more times then */
  //		in.setCommentPattern("#.*|/{2}.*|/\\*.*\\*/");
  //
  //		while (in.readNext()) {
  //
  //			System.out.println(in.getLineNum() + ": " + in.columns.length + " elements");
  //
  //			for (int i = 0; i < in.columns.length; i++) {
  //				System.out.println("  " + i + ": " + in.columns[i]);
  //			}
  //
  //		}
  //
  //		in.close();
  //
  public static void main(String[] args) {
    String s = "orms.dbc";
    if (s.matches("^orms\\.db.*")) {
      System.out.print("True");
    } else {
      System.out.print("False");
    }
  }
  //	}
  //	catch (Exception e) {
  //		System.out.println(e.toString());
  //	}

}

