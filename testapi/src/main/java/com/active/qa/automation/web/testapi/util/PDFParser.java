package com.active.qa.automation.web.testapi.util;

import com.active.qa.automation.web.testapi.exception.ErrorOnDataException;
import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfContentReaderTool;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by tchen on 1/11/2016.
 */
public class PDFParser {
  private static AutomationLogger logger = AutomationLogger.getInstance();

  private static int currentPageNumber = -1;

  private static final int MAX_ERROR_COUNT = 50;

  private static float positionThresholdX = 5.0f;
  private static float positionThresholdY = 5.0f;

  private static class PDFPageInfo {
    private float x;
    private float y;
    private float width;
    private float height;

    private Hashtable<String, PDFFontInfo> fontInfos = new Hashtable<String, PDFFontInfo>();
    private Hashtable<PDFStringMatrixInfo, PDFStringInfo> stringInfosByPosition = new Hashtable<PDFStringMatrixInfo, PDFStringInfo>();
    private Hashtable<String, List<PDFStringInfo>> stringInfosByContent = new Hashtable<String, List<PDFStringInfo>>();
    private ArrayList<PDFLineInfo> lineInfos = new ArrayList<PDFLineInfo>();

    public Hashtable<PDFStringMatrixInfo, PDFStringInfo> getStringInfosByPosition() {
      return stringInfosByPosition;
    }

    public void addLineInfo(PDFLineInfo lineInfo) {
      if (lineInfos == null) {
        lineInfos = new ArrayList<PDFLineInfo>();
      }
      lineInfos.add(lineInfo);
    }

    public int getLineCount() {
      if (lineInfos == null) {
        return 0;
      }
      return lineInfos.size();
    }

    public PDFLineInfo getLineInfo(int idx) {
      if (lineInfos == null || idx >= lineInfos.size()) {
        return null;
      }
      return (PDFLineInfo) lineInfos.get(idx);

    }

    public void addStringInfo(PDFStringInfo stringInfo) {
      if (stringInfosByPosition == null) {
        stringInfosByContent = new Hashtable<String, List<PDFStringInfo>>();
        stringInfosByPosition = new Hashtable<PDFStringMatrixInfo, PDFStringInfo>();
      }
      stringInfosByPosition.put(stringInfo.matrixInfo, stringInfo);
      if (!stringInfosByContent.containsKey(stringInfo.stringContent)) {
        ArrayList<PDFStringInfo> allStringWithSameContent = new ArrayList<PDFStringInfo>();
        stringInfosByContent.put(stringInfo.stringContent,
            allStringWithSameContent);
      }
      ArrayList<PDFStringInfo> allStringWithSameContent = (ArrayList<PDFStringInfo>) stringInfosByContent
          .get(stringInfo.stringContent);
      allStringWithSameContent.add(stringInfo);
    }

    public PDFStringInfo findSimilarStringInfo(PDFStringInfo stringInfo) {
      // check by position
      if (stringInfosByPosition.containsKey(stringInfo.matrixInfo)) {
        return (PDFStringInfo) stringInfosByPosition
            .get(stringInfo.matrixInfo);
      }

      // check by content
      if (stringInfosByContent.containsKey(stringInfo.stringContent)) {
        ArrayList<PDFStringInfo> allStringWithSameContent = (ArrayList<PDFStringInfo>) stringInfosByContent
            .get(stringInfo.stringContent);
        for (int i = 0; i < allStringWithSameContent.size(); i++) {
          PDFStringInfo toCompare = (PDFStringInfo) allStringWithSameContent
              .get(i);
          // check if the position similar
          if (toCompare.matrixInfo.isSimilarTo(stringInfo.matrixInfo)) {
            return toCompare;
          }
        }
      }

      return null;
    }

    public void addFontInfo(String fontID, PDFFontInfo fontInfo) {
      if (fontInfos == null) {
        fontInfos = new Hashtable<String, PDFFontInfo>();
      }
      fontInfos.put(fontID, fontInfo);
    }

    public PDFFontInfo getFontInfo(String fontID) {
      if (fontInfos == null) {
        return null;
      }
      return (PDFFontInfo) fontInfos.get(fontID);

    }
  }

  private static class PDFStringMatrixInfo implements
      Comparable<PDFStringMatrixInfo> {
    private float matrix_a;
    private float matrix_b;
    private float matrix_c;
    private float matrix_d;
    private float matrix_x;
    private float matrix_y;

    public boolean isSimilarTo(PDFStringMatrixInfo other) {

      if ((Math.abs(other.matrix_x - this.matrix_x) <= positionThresholdX)
          && (Math.abs(other.matrix_y - this.matrix_y) <= positionThresholdY)) {
        return true;
      }
      return false;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof PDFStringMatrixInfo) {
        PDFStringMatrixInfo other = (PDFStringMatrixInfo) obj;
        if (compareTwoFloatValueEquals(this.matrix_a, other.matrix_a)
            && compareTwoFloatValueEquals(this.matrix_b,
            other.matrix_b)
            && compareTwoFloatValueEquals(this.matrix_c,
            other.matrix_c)
            && compareTwoFloatValueEquals(this.matrix_d,
            other.matrix_d)
            && compareTwoFloatValueEquals(this.matrix_x,
            other.matrix_x)
            && compareTwoFloatValueEquals(this.matrix_y,
            other.matrix_y)) {
          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      int code = (int) (this.matrix_a + this.matrix_b + this.matrix_c
          + this.matrix_d + this.matrix_x + this.matrix_y);
      return code;
    }

    @Override
    public int compareTo(PDFStringMatrixInfo other) {

      if (other.matrix_y > this.matrix_y) {
        return 1;
      } else if (other.matrix_y < this.matrix_y) {
        return -1;
      } else {
        if (other.matrix_x > this.matrix_x) {
          return -1;
        } else if (other.matrix_x < this.matrix_x) {
          return 1;
        } else {
          return 0;
        }
      }

    }
  }

  private static class PDFStringInfo {
    private String stringContent = null;
    private PDFStringMatrixInfo matrixInfo = null;
    private float fontSize;
    private PDFFontInfo fontInfo = null;
    private float colorR;
    private float colorG;
    private float colorB;
  }

  @SuppressWarnings("unused")
  private static class PDFFontInfo {

    private String fontID = null;
    private String lastChar = null;
    private String baseFont = null;
    private String subType = null;
    private String encoding = null;
    private String firstChar = null;
  }

  private static class PDFLineInfo {
    private float colorR;
    private float colorG;
    private float colorB;
    private float source_x;
    private float source_y;
    private float destination_x;
    private float destination_y;
    private float lineWidth;

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof PDFLineInfo) {
        PDFLineInfo other = (PDFLineInfo) obj;
        if (this.colorR == other.colorR
            && this.colorG == other.colorG
            && this.colorB == other.colorB
            && compareTwoFloatValueEquals(this.lineWidth,
            other.lineWidth)
            && compareTwoFloatValueEquals(this.source_x,
            other.source_x)
            && compareTwoFloatValueEquals(this.source_y,
            other.source_y)
            && compareTwoFloatValueEquals(this.destination_x,
            other.destination_x)
            && compareTwoFloatValueEquals(this.destination_y,
            other.destination_y)) {
          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      int code = (int) (this.colorR + this.colorG + this.colorB
          + this.destination_x + this.destination_y + this.source_x
          + this.source_y + this.lineWidth);
      return code;
    }
  }

  /**
   * Compare two float value, if they are equals return true,else return false
   *
   * @param a
   * @param b
   * @return
   */
  private static boolean compareTwoFloatValueEquals(float a, float b) {
    return Math.abs(a - b) < 0.000001;
  }

  /**
   * compare two PDF file and return the difference
   *
   * @param fromPath the base PDF file path
   * @param toPath   the PDF file to compare
   * @return
   * @throws IOException
   */
  public static List<String> comparePDFFile(String fromPath, String toPath)
      throws IOException {
    List<String> msgs = new ArrayList<String>();
    PdfReader fromReader = new PdfReader(fromPath);
    PdfReader toReader = new PdfReader(toPath);
    int numberOfPages = fromReader.getNumberOfPages();
    // check page count
    if (numberOfPages != toReader.getNumberOfPages()) {
      String msg = "page count not equal: " + numberOfPages + " VS "
          + toReader.getNumberOfPages();
      logger.error(msg);
      msgs.add(msg);
      return msgs;
    }
    for (int i = 0; i < numberOfPages; i++) {
      currentPageNumber = i;
      byte[] readData = convertPDFPageToByteArray(fromReader, i + 1);
      byte[] readData1 = convertPDFPageToByteArray(toReader, i + 1);

      List<String> errorMsgs = comparePDFPage(processPDFPage(readData),
          processPDFPage(readData1));
      msgs.addAll(errorMsgs);
      int errorNum = 0;
      errorNum += errorMsgs.size();
      if (errorNum > MAX_ERROR_COUNT) {
        break;
      }
    }
    return msgs;
  }

  /**
   * Convert a PDF page to a Byte Array
   *
   * @param reader
   * @param pageNum
   * @return
   * @throws IOException
   */
  private static byte[] convertPDFPageToByteArray(PdfReader reader,
                                                  int pageNum) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(baos);
    PdfContentReaderTool.listContentStreamForPage(reader, pageNum, pw);
    byte[] readData = baos.toByteArray();
    return readData;
  }

  /**
   * compare two page info objects and return the difference
   *
   * @param pageInfo1
   * @param pageInfo2
   * @return
   */
  private static List<String> comparePDFPage(PDFPageInfo pageInfo1,
                                             PDFPageInfo pageInfo2) {
    List<String> msgs = new ArrayList<String>();
    // check page size
    if (!compareTwoFloatValueEquals(pageInfo1.x, pageInfo2.x)
        || !compareTwoFloatValueEquals(pageInfo1.y, pageInfo2.y)
        || !compareTwoFloatValueEquals(pageInfo1.width, pageInfo2.width)
        || !compareTwoFloatValueEquals(pageInfo1.height,
        pageInfo2.height)) {
      String msg = "page size not equal at page[" + currentPageNumber
          + "]: ";
      msg += "[" + pageInfo1.x + "," + pageInfo1.y + ","
          + pageInfo1.width + "," + pageInfo1.height + "] VS ["
          + pageInfo2.x + "," + pageInfo2.y + "," + pageInfo2.width
          + "," + pageInfo2.height + "]";
      logger.error(msg);
      msgs.add(msg);
    }

    // check string info
    Enumeration<PDFStringMatrixInfo> keys = pageInfo1
        .getStringInfosByPosition().keys();
    while (keys.hasMoreElements()) {
      PDFStringMatrixInfo key = (PDFStringMatrixInfo) keys.nextElement();
      PDFStringInfo stringInfo1 = (PDFStringInfo) pageInfo1
          .getStringInfosByPosition().get(key);
      PDFStringInfo stringInfo2 = pageInfo2
          .findSimilarStringInfo(stringInfo1);
      // PDFStringInfo stringInfo2 =
      // pageInfo2.getStringInfosByPosition().get(key);

      // use position as key to find it if exist in another document
      if (stringInfo2 == null) {
        if (stringInfo1.stringContent.startsWith("Run Date and Time:") || stringInfo1.stringContent.startsWith("Report Pulled:") ||
            stringInfo1.stringContent.startsWith("RUN DATE AND TIME:")) {
          continue;
        }
        String msg = "string in document1 ["
            + stringInfo1.stringContent + "] at position ["
            + stringInfo1.matrixInfo.matrix_x + ","
            + stringInfo1.matrixInfo.matrix_y
            + "] not found in document2 in page "
            + currentPageNumber;
        msgs.add(msg);
        logger.error(msg);
        continue;
      }

      // check base font
      if (!stringInfo1.fontInfo.baseFont
          .equals(stringInfo2.fontInfo.baseFont)) {
        String msg = "base font for '" + stringInfo1.stringContent
            + "' not equal:[" + stringInfo1.fontInfo.baseFont
            + "] VS [" + stringInfo2.fontInfo.baseFont + "]in page"
            + currentPageNumber;
        msgs.add(msg);
        logger.error(msg);
        continue;
      }
      // check encoding
      if (!stringInfo1.fontInfo.encoding
          .equals(stringInfo2.fontInfo.encoding)) {
        String msg = "font encoding for '" + stringInfo1.stringContent
            + "' not equal:[" + stringInfo1.fontInfo.encoding
            + "] VS [" + stringInfo2.fontInfo.encoding + "]in page"
            + currentPageNumber;
        msgs.add(msg);
        logger.error(msg);
        continue;
      }

      // check color
      if (stringInfo1.colorR != stringInfo2.colorR
          || stringInfo1.colorG != stringInfo2.colorG
          || stringInfo1.colorB != stringInfo2.colorB) {
        String msg = "string [" + stringInfo1.stringContent
            + "] color not equal:";
        msg += "[" + stringInfo1.colorR + "," + stringInfo1.colorG
            + "," + stringInfo1.colorB + "] VS ["
            + stringInfo2.colorR + "," + stringInfo2.colorG + ","
            + stringInfo2.colorB + "]in page" + currentPageNumber;
        msgs.add(msg);
        logger.error(msg);
        continue;
      }

      // check fontSize
      if (!compareTwoFloatValueEquals(stringInfo1.fontSize,
          stringInfo2.fontSize)) {
        String msg = "string [" + stringInfo1.stringContent
            + "] font size not equal:";
        msg += "[" + stringInfo1.fontSize + "] VS ["
            + stringInfo2.fontSize + "]in page" + currentPageNumber;
        msgs.add(msg);
        logger.error(msg);
        continue;
      }

      // check content
      if (!stringInfo1.stringContent.equals(stringInfo2.stringContent)) {
        if ((stringInfo1.stringContent.startsWith("Run Date and Time:")
            && stringInfo2.stringContent
            .startsWith("Run Date and Time:")) || (stringInfo1.stringContent.startsWith("RUN DATE AND TIME:")
            && stringInfo2.stringContent
            .startsWith("RUN DATE AND TIME:")) ||
            (stringInfo1.stringContent.startsWith("Report Pulled:")
                && stringInfo2.stringContent
                .startsWith("Report Pulled:")) ||
            (stringInfo1.stringContent.startsWith("REPORT PULLED:")
                && stringInfo2.stringContent
                .startsWith("REPORT PULLED:"))) {
          continue;
        }
        if (stringInfo2.stringContent.contains(DateFunctions
            .formatDate(DateFunctions.getToday(), "MM/dd/yyyy"))) {
          continue;
        }
        String msg = "string [" + stringInfo1.stringContent
            + "] content not equal:";
        msg += "[" + stringInfo1.stringContent + "] VS ["
            + stringInfo2.stringContent + "] in page"
            + currentPageNumber;
        msgs.add(msg);
        logger.error(msg);
      }
    }

    // check line info
    for (int i = 0; i < pageInfo1.getLineCount(); i++) {
      PDFLineInfo line1 = pageInfo1.getLineInfo(i);
      if (!pageInfo2.lineInfos.contains(line1)) {

        String msg = "line not equal in page" + currentPageNumber;
        msgs.add(msg);
        logger.error(msg);
      }
    }
    return msgs;
  }

  /**
   * This method used to parse a PDF Page
   *
   * @param pageData
   * @return PDF Page Info
   * @throws IOException
   */
  private static PDFPageInfo processPDFPage(byte[] pageData)
      throws IOException {
    PDFPageInfo pageInfo = new PDFPageInfo();

    BufferedReader br = new BufferedReader(new InputStreamReader(
        new ByteArrayInputStream(pageData)));
    String sLine = br.readLine();
    while (sLine != null) {
      if (sLine.startsWith("===")) {
        // page start flag
        // System.out.println(sLine);
        sLine = br.readLine();
      } else if (sLine.startsWith("- - - - - Dictionary")) {
        // dictionary part start
        ArrayList<String> list = new ArrayList<String>();
        sLine = br.readLine();
        while (sLine != null) {
          if (sLine.startsWith("- - - - - ")) {
            break;
          }
          list.add(sLine);
          sLine = br.readLine();
        }
        processDictionary(list, pageInfo);
      } else if (sLine.startsWith("- - - - - XObject Summary")) {
        // XObject Summary part start,skip this,currently not use
        sLine = br.readLine();
      } else if (sLine.startsWith("- - - - - Content Stream")) {
        // dictionary part start
        ArrayList<String> list = new ArrayList<String>();
        sLine = br.readLine();
        while (sLine != null) {
          if (sLine.startsWith("- - - - - ")) {
            break;
          }
          list.add(sLine);
          sLine = br.readLine();
        }
        processContent(list, pageInfo);
      } else if (sLine.startsWith("- - - - - Text Extraction")) {
        // not care this time
        // skip to an empty line
        sLine = br.readLine();
        while (sLine != null) {
          if (sLine.length() == 0) {
            sLine = br.readLine();
            break;
          }
          sLine = br.readLine();
        }
      } else {
        sLine = br.readLine();
      }
    }
    return pageInfo;
  }

  /**
   * Parse PDF dictionary to get page size, font info, and store them into a
   * PDF Page Info
   *
   * @param data
   * @param pageInfo
   */
  private static void processDictionary(List<String> data,
                                        PDFPageInfo pageInfo) {

    String sCurrentFontID = null;

    for (int i = 0; i < data.size(); i++) {
      String sLine = data.get(i).toString();
      sLine = sLine.trim();
      // analayze page info line
      if (sLine.startsWith("(")) {
        StringTokenizer st = new StringTokenizer(sLine, "()/=[]");
        while (st.hasMoreElements()) {
          String sCheck = st.nextToken();
          if (sCheck.equals("MediaBox")) {
            String sPageInfo = st.nextToken();
            String[] infos = sPageInfo.split(", ");
            pageInfo.x = Float.parseFloat(infos[0]);
            pageInfo.y = Float.parseFloat(infos[1]);
            pageInfo.width = Float.parseFloat(infos[2]);
            pageInfo.height = Float.parseFloat(infos[3]);
            break;
          }
        }
      } else if (sLine.startsWith("Subdictionary")) {
        sLine = sLine.substring(14);
        if (sLine.startsWith("/Parent = ")) {
          // skip this line , not useful
        } else if (sLine.startsWith("/Resources = ")) {
          // skip this line , not useful
        } else if (sLine.startsWith("/XObject = ")) {
          // skip this line , not useful
        } else if (sLine.startsWith("/Font = ")) {
          // skip this line , not useful
        } else if (sLine.startsWith("/FontDescriptor = ")) {
          // skip this line,currently not use this
        } else if (sLine.startsWith("/F")) {
          // analyze font info line
          StringTokenizer st = new StringTokenizer(sLine, "()");
          while (st.hasMoreElements()) {
            String sInfoPart = st.nextToken();
            // font id part
            if (sInfoPart.endsWith("= ")) {
              String sFontID = sInfoPart.substring(1,
                  sInfoPart.length() - 2).trim();
              sCurrentFontID = sFontID;
              PDFFontInfo fontInfo = new PDFFontInfo();
              fontInfo.fontID = sFontID;
              pageInfo.addFontInfo(sFontID, fontInfo);
            } else {
              PDFFontInfo fontInfo = (PDFFontInfo) pageInfo
                  .getFontInfo(sCurrentFontID);
              String[] parts = sInfoPart.split(",");
              for (int j = 0; j < parts.length; j++) {
                String singlePart = parts[j].trim();
                if (singlePart.startsWith("/LastChar")) {
                  fontInfo.lastChar = singlePart
                      .substring("/LastChar".length() + 1);
                } else if (singlePart.startsWith("/BaseFont")) {
                  fontInfo.baseFont = singlePart
                      .substring("/BaseFont".length() + 2);
                } else if (singlePart.startsWith("/Type")) {
                  // skip not care
                } else if (singlePart.startsWith("/Subtype")) {
                  fontInfo.subType = singlePart
                      .substring("/Subtype".length() + 2);
                } else if (singlePart.startsWith("/Encoding")) {
                  fontInfo.encoding = singlePart
                      .substring("/Encoding".length() + 2);
                } else if (singlePart.startsWith("/FirstChar")) {
                  fontInfo.firstChar = singlePart
                      .substring("/FirstChar".length() + 1);
                } else if (singlePart
                    .startsWith("/FontDescriptor")) {
                  // skip not care
                } else if (singlePart.startsWith("/Widths")) {
                  // skip not used
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * Parse PDF content stream to get detail PDF string info and store them
   * into PDF Page Info
   *
   * @param data
   * @param pageInfo
   */
  private static void processContent(List<String> data, PDFPageInfo pageInfo) {

    PDFStringInfo currentStringInfo = null;
    PDFLineInfo currentLineInfo = null;
    float currentColorR = 0;
    float currentColorG = 0;
    float currentColorB = 0;
    float currentLineWidth = 0;

    for (int i = 0; i < data.size(); i++) {
      String sLine = data.get(i).toString().trim();
      if (sLine.endsWith(" rg")) {
        String[] colors = sLine.split(" ");
        currentColorR = Float.parseFloat(colors[0]);
        currentColorG = Float.parseFloat(colors[1]);
        currentColorB = Float.parseFloat(colors[2]);
        // System.out.println("change non stroking color to:" +
        // sLine.substring( 0 , sLine.length() - 3 ));
      } else if (sLine.endsWith(" RG")) {
        String[] colors = sLine.split(" ");
        currentColorR = Float.parseFloat(colors[0]);
        currentColorG = Float.parseFloat(colors[1]);
        currentColorB = Float.parseFloat(colors[2]);
        // System.out.println("change non stroking color to:" +
        // sLine.substring( 0 , sLine.length() - 3 ));
      } else if (sLine.endsWith(" re")) {
        // System.out.println("append rectangle:" + sLine.substring( 0 ,
        // sLine.length() - 3 ));
      } else if (sLine.equals("q")) {
        // System.out.println("save current status into stack");
      } else if (sLine.equals("S")) {
        // System.out.println("stroke path");
      } else if (sLine.equals("Q")) {
        // System.out.println("restore status from stack");
      } else if (sLine.equals("W")) {
        // System.out.println("set clipping path");
      } else if (sLine.equals("n")) {
        // System.out.println("end path");
      } else if (sLine.endsWith(" g")) {
        // System.out.println("set graystyle to:" + sLine.substring( 0 ,
        // sLine.length() - 2 ));
      } else if (sLine.equals("f")) {
        // System.out.println( "fill path" );
      } else if (sLine.endsWith("BT")) {
        // start a pdf string
        currentStringInfo = new PDFStringInfo();
        currentStringInfo.colorR = currentColorR;
        currentStringInfo.colorG = currentColorG;
        currentStringInfo.colorB = currentColorB;
      } else if (sLine.endsWith(" Tf")) {
        // set string font info
        String[] fontInfos = sLine.split(" ");
        PDFFontInfo fontInfo = pageInfo.getFontInfo(fontInfos[0]
            .substring(1));
        currentStringInfo.fontInfo = fontInfo;
        currentStringInfo.fontSize = Float.parseFloat(fontInfos[1]);
      } else if (sLine.endsWith(" Tm")) {
        // set string font matrix info
        String[] matrixInfos = sLine.split(" ");
        PDFStringMatrixInfo matrixInfo = new PDFStringMatrixInfo();
        matrixInfo.matrix_a = Float.parseFloat(matrixInfos[0]);
        matrixInfo.matrix_b = Float.parseFloat(matrixInfos[1]);
        matrixInfo.matrix_c = Float.parseFloat(matrixInfos[2]);
        matrixInfo.matrix_d = Float.parseFloat(matrixInfos[3]);
        matrixInfo.matrix_x = Float.parseFloat(matrixInfos[4]);
        matrixInfo.matrix_y = Float.parseFloat(matrixInfos[5]);
        currentStringInfo.matrixInfo = matrixInfo;
      } else if (sLine.endsWith("Tj")) {
        // set string font matrix info
        currentStringInfo.stringContent = sLine.substring(1, sLine
            .length() - 3);
      } else if (sLine.equals("ET")) {
        // set string font matrix info
        pageInfo.addStringInfo(currentStringInfo);
      } else if (sLine.endsWith(" d")) {
        // set dash style
        // System.out.println("set dash style to:" + sLine.substring(
        // sLine.length() - 2 ));
      } else if (sLine.endsWith(" w")) {
        // set string font matrix info
        currentLineWidth = Float.parseFloat(sLine.substring(0, sLine
            .length() - 2));
      } else if (sLine.endsWith(" m")) {
        // set line source info
        String[] sourceInfo = sLine.split(" ");
        currentLineInfo = new PDFLineInfo();
        currentLineInfo.source_x = Float.parseFloat(sourceInfo[0]);
        currentLineInfo.source_y = Float.parseFloat(sourceInfo[1]);
        currentLineInfo.lineWidth = currentLineWidth;
        currentLineInfo.colorR = currentColorR;
        currentLineInfo.colorG = currentColorG;
        currentLineInfo.colorB = currentColorB;
        pageInfo.addLineInfo(currentLineInfo);
      } else if (sLine.endsWith(" l")) {
        // set line destination info;
        String[] destInfo = sLine.split(" ");
        currentLineInfo.destination_x = Float.parseFloat(destInfo[0]);
        currentLineInfo.destination_y = Float.parseFloat(destInfo[1]);
      } else if (sLine.endsWith("Do Q")) {
        // xobj related not care this time
      }
    }
  }

  /**
   * Verify Report Run Date Format is Correct
   *
   * @param file PDF file you want to verify
   */
  public static boolean verifyRptRunDate(String filePath) {
    File file = new File(filePath);
    PDDocument document = null;
    PDFTextStripper stripper = null;
    String content = "";
    boolean isCorrect = true;
    try {
      document = PDDocument.load(file);
      stripper = new PDFTextStripper();
      content = stripper.getText(document);
      String prefix = "Run Date and Time:";
      String prefix1 = "Report Pulled:";
      if (content.contains(prefix) || content.contains(prefix.toUpperCase()) || content.contains(prefix1) || content.contains(prefix1.toUpperCase())) {
        if (Pattern
            .compile(
                "Run Date and Time: [a-zA-Z]{3} [0-9]{1,2} [0-9]{4} [0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2} [A|P]M [EDT|EST|PDT|PST|CST|CDT|MDT]", Pattern.CASE_INSENSITIVE)
            .matcher(content).find() ||
            Pattern
                .compile(
                    "Report Pulled: [a-zA-Z]{3} [a-zA-Z]{3} [0-9]{1,2} [0-9]{4}", Pattern.CASE_INSENSITIVE)
                .matcher(content).find()) {
          logger.info(file.getAbsolutePath()
              + "'s Run Date format is OK!");
        } else {
          logger.error(file.getAbsolutePath()
              + "'s Run Date format is Wrong!!!");
          isCorrect = false;
        }
      } else {
        logger.debug("No Report Run Date And Time OR Report Pulled.");
      }
    } catch (IOException e) {
      e.printStackTrace();
      isCorrect = false;
    } finally {
      if (null != document) {
        try {
          document.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return isCorrect;
  }

  /**
   * This method used to get content from a pdf report
   *
   * @param file -PDF File
   * @return content of the pdf report
   */
  public static String retrievePDFContent(File file) {
    PDDocument document = null;
    PDFTextStripper stripper = null;
    String content = "";

    if (file.getName().endsWith(".PDF") || file.getName().endsWith(".pdf") || file.getName().endsWith(".pdf.do")) { //Lesley[20130912]: some print file on Web with .do file type,but can be read by PDF
      try {
        document = PDDocument.load(file);
        stripper = new PDFTextStripper();
        content = stripper.getText(document);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (null != document) {
          try {
            document.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    } else {
      throw new ErrorOnDataException("File Format Not Correct.");
    }
    return content;
  }

  /**
   * Convert a pdf file to image file
   *
   * @param path      pdf file's path
   * @param imageType
   * @return new generated image's full path
   */
  public List<String> convert2Image(String path, String imageType) {
    PDDocument document = null;
    ImageOutputStream output = null;
    ImageWriter imageWriter = null;
    File file = new File(path);
    String srcName = path.substring(0, path.indexOf("."));
    List<String> imageNames = new ArrayList<String>();
    if (file.exists()) {
      try {
        document = PDDocument.load(file);
        int startPage = 1;
        int endPage = document.getNumberOfPages();
        if (endPage > 1) {
          logger.info("There will have " + endPage
              + " images in all!");
        } else
          logger.info("There is " + endPage + " image");

        List<?> pages = document.getDocumentCatalog().getAllPages();
        for (int i = startPage - 1; i < endPage && i < pages.size(); i++) {
          PDPage page = (PDPage) pages.get(i);
          BufferedImage image = page.convertToImage();

          String fileName = srcName + (i + 1) + "." + imageType;
          logger.info("Writing:" + fileName);
          output = ImageIO
              .createImageOutputStream(new File(fileName));
          boolean foundWriter = false;
          Iterator<ImageWriter> writerIter = ImageIO
              .getImageWritersByFormatName(imageType);
          while (writerIter.hasNext() && !foundWriter) {
            imageWriter = (ImageWriter) writerIter.next();
            ImageWriteParam writerParams = imageWriter
                .getDefaultWriteParam();
            if (writerParams.canWriteCompressed()) {
              writerParams
                  .setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
              writerParams.setCompressionQuality(1.0f);
              writerParams.setCompressionType("FILTERED");
            }

            imageWriter.setOutput(output);
            imageWriter.write(null,
                new IIOImage(image, null, null), writerParams);
            foundWriter = true;
          }
          imageNames.add(fileName);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (null != imageWriter) {
          imageWriter.dispose();
        }
        if (null != output) {
          try {
            output.flush();
            output.close();
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
        if (null != document) {
          try {
            document.close();
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
        boolean success = file.delete();
        if (!success) {
          throw new RuntimeException("Failed to delete " + path);
        }
      }
    } else {
      logger.error(path + " is not exists!");
      throw new ItemNotFoundException(path + " not found");
    }
    return imageNames;
  }

  private static List<PDFStringInfo> retrievePDFContentInOrder(String fromPath)
      throws IOException {
    PdfReader fromReader = new PdfReader(fromPath);
    int numberOfPages = fromReader.getNumberOfPages();
    List<PDFStringInfo> values = new ArrayList<PDFStringInfo>();
    for (int i = 0; i < numberOfPages; i++) {
      currentPageNumber = i;
      byte[] readData = convertPDFPageToByteArray(fromReader, i + 1);
      PDFPageInfo page = processPDFPage(readData);
      Enumeration<PDFStringMatrixInfo> keys = page
          .getStringInfosByPosition().keys();
      List<PDFStringMatrixInfo> keyvalue = new ArrayList<PDFStringMatrixInfo>();
      while (keys.hasMoreElements()) {
        PDFStringMatrixInfo key = (PDFStringMatrixInfo) keys
            .nextElement();
        keyvalue.add(key);
      }
      PDFStringMatrixInfo[] value = (PDFStringMatrixInfo[]) keyvalue
          .toArray(new PDFStringMatrixInfo[]{});
      Arrays.sort(value);
      for (PDFStringMatrixInfo v : value) {
        PDFStringInfo stringInfo = (PDFStringInfo) page
            .getStringInfosByPosition().get(v);
        values.add(stringInfo);
      }

    }
    return values;
  }

  private static List<List<PDFStringInfo>> retrievePDFContentInPageOrder(String fromPath)
      throws IOException {
    PdfReader fromReader = new PdfReader(fromPath);
    int numberOfPages = fromReader.getNumberOfPages();
    List<List<PDFStringInfo>> values = new ArrayList<List<PDFStringInfo>>();

    for (int i = 0; i < numberOfPages; i++) {
      List<PDFStringInfo> tmp = new ArrayList<PDFStringInfo>();
      currentPageNumber = i;
      byte[] readData = convertPDFPageToByteArray(fromReader, i + 1);
      PDFPageInfo page = processPDFPage(readData);
      Enumeration<PDFStringMatrixInfo> keys = page
          .getStringInfosByPosition().keys();
      List<PDFStringMatrixInfo> keyvalue = new ArrayList<PDFStringMatrixInfo>();
      while (keys.hasMoreElements()) {
        PDFStringMatrixInfo key = (PDFStringMatrixInfo) keys
            .nextElement();
        keyvalue.add(key);
      }
      PDFStringMatrixInfo[] value = (PDFStringMatrixInfo[]) keyvalue
          .toArray(new PDFStringMatrixInfo[]{});
      Arrays.sort(value);
      for (PDFStringMatrixInfo v : value) {
        PDFStringInfo stringInfo = (PDFStringInfo) page
            .getStringInfosByPosition().get(v);
        tmp.add(stringInfo);
      }
      values.add(tmp);

    }
    return values;
  }


  public static List<String> getPDFContentInOrder(String fromPath) {
    List<PDFStringInfo> stringInfos = null;
    try {
      stringInfos = retrievePDFContentInOrder(fromPath);
    } catch (IOException e) {
      logger.error("IO Error:" + e.getMessage());
    }

    List<String> value = new ArrayList<String>();
    for (PDFStringInfo stringInfo : stringInfos) {
      value.add(stringInfo.stringContent);
    }

    return value;
  }

  public static List<String> getPDFContentInRow(String fromPath, String value) {
    List<PDFStringInfo> stringInfos = null;
    try {
      stringInfos = retrievePDFContentInOrder(fromPath);
    } catch (IOException e) {
      logger.error("IO Error:" + e.getMessage());
    }

    List<Float> row = new ArrayList<Float>();
    for (PDFStringInfo stringInfo : stringInfos) {
      if (value.equals(stringInfo.stringContent.trim())) {
        row.add(stringInfo.matrixInfo.matrix_y);
      }
    }

    List<String> rowValue = new ArrayList<String>();
    for (Float num : row) {
      String values = "";
      for (PDFStringInfo stringInfo : stringInfos) {
        if (Double.compare(Math.floor(num), Math
            .floor(stringInfo.matrixInfo.matrix_y)) == 0) {
          values += stringInfo.stringContent + ";";
        }
      }
      rowValue.add(values);
    }
    return rowValue;
  }

  /*return result divided by pages*/
  public static List<String> getPDFContentInRowOfPages(String fromPath, String value) {
    List<List<PDFStringInfo>> stringInfos = null;
    try {
      stringInfos = retrievePDFContentInPageOrder(fromPath);
    } catch (IOException e) {
      logger.error("IO Error:" + e.getMessage());
    }

    List<Float> row = new ArrayList<Float>();
    List<String> rowValue = new ArrayList<String>();
    int pageNum = 1;
    for (List<PDFStringInfo> pageInfo : stringInfos) {
      logger.info("Searching string '" + value + "' in Page " + pageNum);
      String values = "";
      row.clear();

      for (PDFStringInfo eachInfo : pageInfo) {
        if (value.equals(eachInfo.stringContent.trim())) {
          row.add(eachInfo.matrixInfo.matrix_y);
        }
      }

      if (row.size() > 0) {
        logger.info("Found string '" + value + "' in Page " + pageNum);
        for (Float num : row) {

          for (PDFStringInfo stringInfo : pageInfo) {
            if (Double.compare(Math.floor(num), Math
                .floor(stringInfo.matrixInfo.matrix_y)) == 0) {
              values += stringInfo.stringContent + ";";
            }
          }
          values += "||";//if multiple line found in a page, we divide them with '||'.
        }
        rowValue.add(values); //result of multiple pages.
      }
      pageNum++;
    }


    return rowValue;
  }

  public static List<String> getPDFContentInCol(String fromPath, String value) {
    List<PDFStringInfo> stringInfos = null;

    try {
      stringInfos = retrievePDFContentInOrder(fromPath);
    } catch (IOException e) {
      logger.error("IO Error:" + e.getMessage());
    }

    List<Float> col = new ArrayList<Float>();
    for (PDFStringInfo stringInfo : stringInfos) {
      if (value.equals(stringInfo.stringContent.trim())) {
        col.add(stringInfo.matrixInfo.matrix_x);
      }
    }

    List<String> colValue = new ArrayList<String>();
    for (Float num : col) {
      String values = "";
      for (PDFStringInfo stringInfo : stringInfos) {
        if (Double.compare(Math.floor(num), Math
            .floor(stringInfo.matrixInfo.matrix_x)) == 0) {
          values += stringInfo.stringContent + ";";
        }
      }
      colValue.add(values);
    }
    return colValue;
  }

}
