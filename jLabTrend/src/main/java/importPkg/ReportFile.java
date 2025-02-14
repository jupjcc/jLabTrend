package importPkg;
/*  import measured lab parameters from html file 
 *  
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;

import mainPkg.LabTrend;
import utilsPkg.OpMsgLogger;
import utilsPkg.Utils;

public class ReportFile implements Comparable<ReportFile> {
//public class ReportFile {
   final String COLLECTION_DATE_TIME = "COLLECTION DATE / TIME";
   
   public static XSSFWorkbook Wkbook=null;
   static CellStyle dateStyle;
   static CellStyle amountStyle;
   static CellStyle boldStyle;
   static XSSFSheet importSht = null;
   static CreationHelper createHelper;
      
   public String collectionDateTime = "none";
   public String headerTitle = "";
   public LocalDate collectionDate;
   DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
   public String fileName;
   public boolean isLoaded = false;
   
   String htmlStr;  
   int fileIndex = 0;
   
//   public boolean equals(Object obj) {
//      return ((ReportFile) obj).collectionDate.equals(collectionDate);
//   }
   @Override
   public int compareTo(ReportFile rf) {
      return rf.collectionDate.compareTo(collectionDate);
   };
   
   public ReportFile(String fileName) {
      // 20250105: add reading of Quest report files which are manauly prepeared from
      //  paper copies provied by Dr. Kocharian
      /*  20240319:
       * after monkeying around with reading and decoding 
       *    html, mhtml, pdf, and txt files - decided that best/easiest approach
       *    is to open the labxx.html lab report file then save into .html
       *    
       *    update 20240409: BRPM changed to Anthem for electronic records
       *    Lab reports are much more user-friendly to read, but require
       *    a different method to read
       *  as of 20240513 the preferred method is:
       *  1. Log into Blue Ridge Premier Medicine / Athena Patient Portal
       *  2. Click on Test Results
       *  3. Click on the desired report; 
       *       e.g.,  CMP14+LP+CBC/D/plt+TSH+PSA+...-234582-U
       *  4. Click on "Save as ..." and specify the output file name
       *      e.g., c:\jLabTrend\importData\jcc20240408.mhtml
       */
      this.fileName = fileName;
   }
   
   public void load(boolean getDateOnly)  throws Exception {
      if (!isLoaded) {
//         LabTrend.OpMsgLog.LogMsg(fileName + " is already loaded\n", OpMsgLogger.LogLevel.INFO);
//         return;
//      }
         BufferedReader in = null;
         try {
            if (fileName.endsWith("xlsx")) {
               readQuestXls(getDateOnly);
            } else if (fileName.endsWith("mhtml")) {
               readMhtml(getDateOnly);
            } else {
               //  input file was saved as .pdf, pre-2024 Labcorp file
               in = new BufferedReader(new FileReader(fileName));
               String line;
               String prevLine = "";
   //            String panelName = "";
               collectionDateTime = null;
               while ((line = in.readLine()) != null) {
                  if (collectionDateTime == null) {
                     if (prevLine.contains(COLLECTION_DATE_TIME)) {
                        int fontPos = line.indexOf("</FONT>");
                        collectionDateTime = line.substring(fontPos-19, fontPos);
                        collectionDate = LocalDate.parse(collectionDateTime, dateFormatter);
                        if (getDateOnly) {
                           return;
                        }
                     }
                  } else if (line.contains(">-")) {
                     if (!getDateOnly) {
                        LabTrend.readings.add(new Reading(this, new DataPoint(line)));
                     }
                  } else {
                     int boldStart = line.indexOf("<B>" + 3);
                     if (boldStart > 3) {
//                        int boldEnd = line.substring(boldStart).indexOf("</B>");
   //                     panelName = line.substring(boldStart, boldStart + boldEnd);
                     }
                  }
                  prevLine = line;
               }
               isLoaded = true;
            }
         } catch (IOException e) {
            System.out.println(Utils.ExceptionString(e) + 
                  "\nError reading report file");
         } catch (Exception ere) {
            throw new Exception(ere);
   //         LabTrend.OpMsgLog.LogMsg("Unable to read " + fileName + " is already loaded\n", OpMsgLogger.LogLevel.INFO);
   //         return;
         } finally {
            if (in != null) {
               in.close();
            }
         }
      }
   }
   
   static String getCellString(int rowNum, int colNum) {
      String ret = null;
      XSSFRow row = importSht.getRow(rowNum);
      Cell c = row.getCell(colNum);
      CellType ct;
      if (c != null) {
         ct = c.getCellType();
         if (ct == CellType.STRING) {
            ret = c.getStringCellValue();
         }
      }
      return ret;
   }
   
   static String getCellAmt(int rowNum, int colNum) {
      String ret = null;
      XSSFRow row = importSht.getRow(rowNum);
      Cell c = row.getCell(colNum);
      CellType ct;
      if (c != null) {
         ct = c.getCellType();
         if (ct == CellType.NUMERIC) {
            double amt = c.getNumericCellValue();
            ret = String.format("%6.2f", amt);
         } else if (ct == CellType.STRING) {
            ret = c.getStringCellValue();
         }
      }
      return ret;
   }
   
   static void initWorkbook() throws Exception {
      boldStyle = Wkbook.createCellStyle();
      Font f = Wkbook.createFont();
      f.setBold(true);
      boldStyle.setFont(f);
      amountStyle = Wkbook.createCellStyle();
      CreationHelper createHelper = Wkbook.getCreationHelper();
      amountStyle.setDataFormat(
            createHelper.createDataFormat().getFormat("#,##0"));
      amountStyle.setAlignment(HorizontalAlignment.CENTER);
      importSht = Wkbook.getSheet("Sheet1");
   }

   private void readQuestXls(boolean getDateOnly) throws Exception {
      FileInputStream fis = new FileInputStream(fileName);
//    stLog.LogMsg("Opening workbook for [" + inputFileName + "]\n");
      Wkbook = XSSFWorkbookFactory.createWorkbook(fis);
      fis.close();
      initWorkbook();
      String questRpt = getCellString(0, 0);
      if (questRpt == null || !questRpt.startsWith("Quest")) {
         throw new Exception("File is not a Quest lab report");
      }
      collectionDateTime = getCellString(2, 2).trim();
      if (collectionDateTime == null) {
         throw new Exception("Report File is missing collection date/time");
      }
      collectionDateTime = collectionDateTime.substring(0, 17).replace(",", "") + ":00";
      collectionDate = LocalDate.parse(collectionDateTime, dateFormatter);
      if (getDateOnly) {
         return;
      }
      DataPoint d;
      for (int r = 6; r < importSht.getLastRowNum(); r++) {
         String labCorpName = getCellString(r, 0);
         if (labCorpName != null) {
            d = new DataPoint(labCorpName, getCellAmt(r, 2),   // name, valStr
                                        getCellAmt(r, 3), getCellAmt(r, 4), // min, max
                                        getCellString(r, 5));  // units
            LabTrend.readings.add(new Reading(this, d));
         } else {
            String questName = getCellString(r, 1);
            if (questName != null) {
               d = new DataPoint(questName, getCellAmt(r, 2),   // name, valStr
                                            getCellAmt(r, 3), getCellAmt(r, 4), // min, max
                                            getCellString(r, 5));  // units
               LabTrend.readings.add(new Reading(this, d));
            }
         }
         
      }    // for the rows
   }
   
   private void readMhtml(boolean getDateOnly) throws Exception {
      final String FIELD_MATCH = "<td class=3D[^>]*>";
      // typical .mhtml line; "abnormal result" may also be ""
      // <td class=3D"abnormalresult" rowspan=3D"2">HEMOGLOBIN A1C</td>
      
      String rpt = Files.readString(Path.of(fileName));
      
      //  lab reportheader
      final String HEADER_TITLE = "<div class=3D\"headertitle\">";
      int headerIndex = rpt.indexOf(HEADER_TITLE) + HEADER_TITLE.length();
      int headerEnd = rpt.indexOf("<", headerIndex);
      headerTitle = rpt.substring(headerIndex, headerEnd).trim();

      //  specimen collection data; mhtml contents are :
      final String COLLECTION_DATE = "<th>Specimen Coll. Date</th>";
//      <th>Specimen Coll. Date</th>
//      <td bgcolor=3D"white" nowrap=3D"">04/08/2024 09:50</td>
      int collDateIndex = rpt.indexOf(COLLECTION_DATE);
      if (collDateIndex < 0) {
         throw new Exception("Can't find collection date in file");
      }
      int collDateBegIndex = rpt.indexOf("\"\">", collDateIndex) + 3;
      int collDateEndIndex = rpt.indexOf("</td", collDateBegIndex);
      collectionDateTime = rpt.substring(collDateBegIndex, collDateEndIndex) + ":00";
      collectionDate = LocalDate.parse(collectionDateTime, dateFormatter);
      if (getDateOnly) {
         return;
      }
      int beginItemIndex;
      int itemEnd;
      String remainder = rpt;
      int nOnTheLine = 0;
      String []lineItems = new String[7];
      ArrayList<String> newNames = new ArrayList<String>();;
      int nDups = 0;
      while (true) {
         Pattern pattern = Pattern.compile(FIELD_MATCH);
         Matcher matcher = pattern.matcher(remainder);
         if (!matcher.find()) {
//            throw new Exception("Can't find item field in report file");
            break;
         }
         beginItemIndex = matcher.end();
         if (beginItemIndex < 0) {
            break;
         }
//         // some item name lines contain multispan flags, like:
//         // <td class=3D"" rowspan=3D"2">VITAMIN D, 25-HYDROXY</td>
//         beginItemIndex = remainder.indexOf(">", beginItemIndex + 1) + 1;
         itemEnd = remainder.indexOf("<", beginItemIndex);
         String item = remainder.substring(beginItemIndex, itemEnd).trim();
         lineItems[nOnTheLine] = item;
         if (++nOnTheLine == 7) {
            //  jcc 20240510 don't know why but mhtml file contains 2 sets of
            //          apparently? identical data
            DataPoint newItem = new DataPoint(lineItems);
            int nDupsStart = nDups;
            for (int iNew = 0; iNew < newNames.size(); iNew++) {
               if (lineItems[0].equalsIgnoreCase(newNames.get(iNew))) {
                  nDups++;
               }
            }
            if (nDups == nDupsStart) {
               if (!getDateOnly) {
                  LabTrend.readings.add(new Reading(this, newItem));
                  newNames.add(lineItems[0]);
               }
            }
            nOnTheLine = 0;
         }
//         System.out.println("[" + item + "]");
         remainder = remainder.substring(itemEnd + 1);
//         System.out.println("remainder length=" + remainder.length());
      }
      if (nDups > 0) {
         LabTrend.OpMsgLog.LogMsg("Ignoring " + nDups + " duplicate reading(s) imported from:\n" +
               fileName + "\n", OpMsgLogger.LogLevel.INFO);
      }
      isLoaded = true;
      /*  debug output
      int bp=12;
      String pwf = fileName + ".txt";
      try
      {
         PrintWriter out = new PrintWriter(pwf);
         out.println("rpt items size=" + rptItems.size() + "\n" + result);
         out.close();
      } catch (Exception pe) {
         System.out.println(Utils.ExceptionString(pe));
      }
      */
   }
      
}
