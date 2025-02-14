package importPkg;
/*  import measured lab parameters from html file 
 *  
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import utilsPkg.OpMsgLogger;

public class ImportFromHtml {
   final String COLLECTION_DATE_TIME = "COLLECTION DATE/TIME";
   public String collectionDateTime = "none";
   
   // import items is built as a list then converted to array
   public ImportItem []ImpItems;
   ArrayList<ImportItem> impItems = new ArrayList<ImportItem>();
   ArrayList<Double> insVals = new ArrayList<Double>();
   double []insertVals;  // in order of xl file
   
   static OpMsgLogger stLog;
   String htmlStr;  
   int fileIndex = 0;
   
   public ImportFromHtml(OpMsgLogger log) {
      stLog = log;      
   }
   
   public void ReadReportFile(String fileName) throws Exception {
//      StringBuilder contentBuilder = new StringBuilder();
//      try {
//          BufferedReader in = new BufferedReader(new FileReader(
//                "c:\\jLabTrend\\importdata\\lab_4087 (6).mhtml"));
//          String str;
//          while ((str = in.readLine()) != null) {
//              contentBuilder.append(str);
//          }
//          in.close();
//      } catch (IOException e) {
//         System.out.println("shit");
//      }
//      String content = contentBuilder.toString();      
//      File f = new File(fileName);
//      BufferedReader rdr = new BufferedReader(new FileReader(f));
      BufferedReader reader = new BufferedReader(new FileReader(fileName));
      StringBuilder sb = new StringBuilder();
      String line = null;
      String ls = System.getProperty("line.separator");
      while ((line = reader.readLine()) != null) {
         sb.append(line);
         sb.append(ls);
      }
      // delete the last new line separator
      sb.deleteCharAt(sb.length() - 1);
      reader.close();
      htmlStr = sb.toString();  
      impItems.clear();
   }
}
