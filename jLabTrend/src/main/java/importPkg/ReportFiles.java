package importPkg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mainPkg.LabTrend;
import utilsPkg.OpMsgLogger;
import utilsPkg.Utils;

public class ReportFiles {
   public ArrayList<ReportFile> reports = new ArrayList<ReportFile>();
   public ArrayList<Reading> readings = new ArrayList<Reading>();

   String importsFolder;
   
   public ReportFiles(String impFolder) {
      reports.clear();
      readings.clear();
      importsFolder = impFolder;
      String []fileList = Utils.FilesFinder(importsFolder,
                  new String[] {"*.mhtml", "*.htm*", "*.xlsx"});
      // single web pages (*.html) are Labcorp pre-2024
      // complete web pages (*.mhtml) are Labcorp beginning 2024
      // Quest Excel files (*.xlsx) are manually entered
      //  check each file for legitimacy and get its date in order to sort them
      String err = "";
      for (int f = 0; f < fileList.length; f++) {
         String rptName = importsFolder + "\\" + fileList[f];
         try {
            ReportFile rptTest = new ReportFile(rptName);
            rptTest.load(true);  // true->get date only
            if (rptTest.collectionDateTime == null) {
               err += "Ignoring file " + rptName +
                     ", no collection date found\n";
            } else {
               reports.add(rptTest);
            }
         } catch (Exception le) {
            err += "Ignoring file " + rptName +
               " due to exception\n" + Utils.ExceptionString(le) + "\n ";
         }
      }
   }
 
   public void LoadReports() {
      if (reports.size() > 1) {
         // sort by collection date/time, reverse chronology
         Collections.sort(reports);
      }
      int bp = 1;
      // now that file names are sorted, load the readings
      for (int r = 0; r < reports.size(); r++) {
         ReportFile rf = reports.get(r);
         try {
            rf.load(false);     // false => get readings
         } catch (Exception re) {
            LabTrend.OpMsgLog.LogMsg(Utils.ExceptionString(re) + "\nError loading readings from file: " +
                  rf.fileName + "\n", OpMsgLogger.LogLevel.ERROR, true);
         }
      }
      if (readings.size() > 1) {
         // sort the readings by name
         Collections.sort(readings, new Comparator<Reading>() {
            @Override
            public int compare(Reading t1, Reading t2) {
               String name1 = ((Reading)t1).data.name.trim().toLowerCase();
               String name2 = ((Reading)t2).data.name.trim().toLowerCase();
               //  f1.compareTo(f2) for increasing date/time, else decreasing
               return name1.compareTo(name2);
            }
         });
      }
//      if (reports.size() == 0) {
//         AppSettings.sData.SavedImportFilesList = null;
//      } else {
//         AppSettings.sData.SavedImportFilesList = new String[reports.size()];
//         for (int r = 0; r < reports.size(); r++) {
//            ReportFile rf = reports.get(r);
//            try {
//               rf.load(true);    // get date only for sorting
//            } catch (Exception re) {
//               LabTrend.OpMsgLog.LogMsg("Error reading collection date from report file:\n" +
//                     rf.fileName +
//                     "\nFile must conform to Labcorp format\n",
//                     OpMsgLogger.LogLevel.ERROR, true);
//               reports.remove(rf);
//            }
//         }
//    if (reports.size() > 1) {
//    // sort by collection date/time, reverse chronology
//    Collections.sort(reports);
// }
//         // now that file names are sorted, load the readings
//         for (int r = 0; r < reports.size(); r++) {
//            ReportFile rf = reports.get(r);
//            try {
//               rf.load(false);
//            } catch (Exception re) {
//                LabTrend.OpMsgLog.LogMsg(Utils.ExceptionString(re) + "\nError loading readings from file: " +
//                  rf.fileName + "\n", OpMsgLogger.LogLevel.ERROR, true);
//            }
//         }
//         if (readings.size() > 1) {
//            // sort the readings by name
//            Collections.sort(readings, new Comparator<Reading>() {
//               @Override
//               public int compare(Reading t1, Reading t2) {
//                  String name1 = ((Reading)t1).data.name.trim().toLowerCase();
//                  String name2 = ((Reading)t2).data.name.trim().toLowerCase();
//                  // f1.compareTo(f2) for increasing date/time, else decreasing
//                  return name1.compareTo(name2);
//               }
//            });
//         }
//         txtFocusFilesHdr.setText("      Collected              File");
//         focusFileSelectors = new FocusFileSelector[reports.size()];
//         for (int rfi = 0; rfi < reports.size(); rfi++) {
//            ReportFile rf = reports.get(rfi);
//            String id = rf.collectionDateTime + " " + rf.fileName;
//            focusFileSelectors[rfi] = new FocusFileSelector(cmpFileList, id, rfi);
////            new focusFileSelectors(cmpFileList, id, rfi);
////            for (int itm = 0; itm < reports.get(rfi).rptItems.size(); itm++) {
////               trending.AddTrendItem(rf, reports.get(rfi).rptItems.get(itm));
////            }
//         }
//         cmpFileList.pack();
//         cmpFileList.layout();
         /*
         System.out.println("Trend Items with parent files:");
         for (int tItm = 0; tItm < trending.size(); tItm++) {
            String il = trending.get(tItm).item.itemLine();
            il = il.substring(0, il.length()-1);
            System.out.println(il + ", file(s):");
            int nFiles = trending.get(tItm).parentFiles.size();
            for (int pf = 0; pf < nFiles; pf++) {
               System.out.println("   " + trending.get(tItm).
                                 parentFiles.get(pf).fileName);
            }
         }
         */
//      }     // end else reports size > 0
//      int bp=1;    // debug breakpoint
   }     // end loadReports


}
