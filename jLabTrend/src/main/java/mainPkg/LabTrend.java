package mainPkg;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swtchart.Chart;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.IAxisTick;
import org.eclipse.swtchart.ILegend;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.ISeries.SeriesType;
import org.eclipse.swtchart.ISeriesSet;
import org.eclipse.wb.swt.SWTResourceManager;

import chartsPkg.ChartMouseListener;
import configPkg.AppPaths;
import configPkg.AppSettings;
import dataEnumerations.Colors;
import importPkg.ImportFromHtml;
import importPkg.Reading;
import importPkg.ReportFile;
import trendListPkg.TrendItemSelector;
import trendListPkg.TrendListItem;
import trendListPkg.Trending;
//import importPkg.ImportFromHtml;
import utilsPkg.OpMsgLogger;
import utilsPkg.TextLogger;
import utilsPkg.Utils;

/*  LabTrend reads lab report file(s) to prepare trending (history) of items
 *  The report file starts out as a LabCorp file either :
 *    Prior to 2024 an .html file (typically LAB_4087.html
 *        Clicking on this file opens it as a web page, from which you can 
 *        right click then save as Webpage complete (*.htm)
 *  starting in 2024 - a .mhtml file saved from Blue Ridge Medical Portal Message
 *        Clicking on the selected Message to open, then right clicking to
 *        Save As Web Page .mhtml file
 * Decisions on what kind of file to use for input:
 *  Revisions:
 *  20250106: added import of xlsx file manually created from paper report 
 *  20240510 Added import of Labcorp files from Portal Messages
 *  20231207 Output text amounts to accounts section of SetupDisplay tab
 *  
 *  20240419: Labcorp changed the report layout, the new layout is imported:
 *  1. Log into Blue Ridge Premier Medicine / Athena Patient Portal
 *  2. Click on Messages
 *  3. Click the desired report to open; 
 *       e.g.,  CMP14+LP+CBC/D/plt+TSH+PSA+...-234582-U
 *  4. Right-click on empty space and Save as web page .mhtml
 *  5. Name the output file; e.g., c:\jLabTrend\importData\john\jcc20240408.mhtml
 *  Select that file in jLabTrend  
 *                
 */
public class LabTrend extends Shell {
   public static String PROG_ID = "jLabTrend History of Lab Results v3.0 20250214";
   final static int TAB_IMPORT_DATA = 0;
   final static int TAB_TRENDING = 1;
   final static int TAB_CHARTS = 2;
   static final String DESCRIBE_LAB_REPORT_FILES =
         "This app summarizes LABORATORY results from lab report file(s) " +
         "provided on the Blue Ridge Premier Medicine patient portal.\n" +
         "The files are imported to local storage via the Messages section\n" +
         "of the Patient Portal\n";
   public static ArrayList<ReportFile> reportFilesList = new ArrayList<ReportFile>();
   private static String []reportFileNames = null;
   public static ArrayList<Reading> readings = new ArrayList<Reading>();

   final static String TAB_IMPORT_DATA_STR = "ImportedData";
   final static String TAB_TRENDING_STR = "TrendingItems";
   final static String TAB_CHARTS_STR = "Charts";
   public static ImportFromHtml impFromHtml;
   public static OpMsgLogger OpMsgLog;
   public static AppSettings settings;
   public static Shell shlMain;
   protected static StyledText stxtOpMsgLog = null;
   static TextLogger sLog;
   public static Display MainDisplay;
   ArrayList<String> accountNames;
   ArrayList<String> amounts;
   static boolean captureOk = false;
   static boolean xlFileAbort = false;
   static Button []rdoSelFile;
   static Text txtFocusFilesHdr;
   static Button btnNewFile;
   static Button btnAddNewFiles;
   static Button btnClearFiles;
   static Button rdoAllTrending;
   static Button rdoAnyAbnormal;
   static Button rdoRecentAbnormal;
   static Button btnResetTrendDetails;
   static Button btnSelAllTrendDetails;
   static Button btnSaveTrendItemsList;
   static Button btnLoadTrendItemsList;
   static Button btnUpdateTrendData;
   static Button btnUpdateChart;
   static Label lblNumSel;
   static LocalDate refDate;
//   static WaitAndNotify xlFileOk = null;
   static TabFolder tabFolder;
   static TabItem tabImportData;
   static TabItem tabTrending;
   static TabItem tabCharts;
   static String prevTabItemName;
   
   static CmpItemDetails []cmpItm;
   static CmpItemDetails []cmpTrendItm;
   static ArrayList<CmpTrendItemDetails> cmpTrendItemDetails;
//   static ArrayList<CmpChart> cmpChartDetails;
   static boolean xlFileOk = false;
   static boolean importData = false;
   static Composite cmpCtrlDisp;
   Group grpItemSel;
   static Composite cmpChart;
   private Composite cmpDataDisp;
   
   static Chart chart;
   static List<TrendListItem> chartItems = new ArrayList<TrendListItem>();
   static ChartMouseListener mouseListener;

   static SashForm sashForm;
   private static Group grpSelectFiles;
   FormData fdTabFolder;
   FormData fd_sash;
   FormData fd_grpOpMsg;
   private static Label lblFocusReport;
   private static Group grpFocusReport;
   private Composite cmpSelectionBtns;

   private static FocusFileSelector []focusFileSelectors;
   private static TrendItemSelector []trendItemSelectors;
   private static Trending trending;
   
   //vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
   // following groups of three widgets support scrolled composites
   // the first composite is the container of the scrolled composite
   // the second is the scrolled composite
   // the third contains the contents
   private static Composite cmpRptItems;
   private static ScrolledComposite scmpFocusRpt;
   private static Composite cmpItemList;
   
   private static CmpSelectFocus cmpSelectFocus;
   private static ScrolledComposite scmpSelectFocus;   
   private static Composite cmpFileList;
   
   private Composite cmpTrendItems;
   private static ScrolledComposite scmpTrendItems;
   private static Composite cmpTrendList;
   
   private Composite cmpTrendDetailsContainer;
   private static Composite cmpTrendDetails;
   private static ScrolledComposite scmpTrendDetails;
   private static boolean deferringSelUpdate = false;
   private Composite cmpUpdateHistory;
   
   DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.ENGLISH);
   //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
   /**
    * Launch the application.
    * @param args
    */
   public static void main(String args[])
   {
      try
      {
         Display display = Display.getDefault();
         MainDisplay = display;
         shlMain = new LabTrend(display);
         // Create the child shell and the dispose listener
         shlMain.addDisposeListener(new DisposeListener() {
           public void widgetDisposed(DisposeEvent event) {
              AppSettings.WriteJsonObject();
           }
         });
         tabFolder.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
               String itemName = tabFolder.getSelection()[0].getText();
               if (!itemName.equals("")) {
                  selectTab(itemName);
               }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
         });
         shlMain.pack();
         shlMain.open();
         shlMain.layout();
         selectTab(TAB_IMPORT_DATA_STR);
         
         OpMsgLog.LogMsg(PROG_ID + "\n");
         
         //  load the focus file list with saved settings
         if (AppSettings.sData.SavedImportFilesList != null &&
               AppSettings.sData.SavedImportFilesList.length > 0 &&
               AppSettings.sData.SavedImportFilesList[0] != null) {
            OpMsgLog.LogMsg("Retrieving report file names from last session:\n",
                  OpMsgLogger.LogLevel.INFO, true);
            reportFileNames = AppSettings.sData.SavedImportFilesList; 
            for (int ifn = 0; ifn < reportFileNames.length; ifn++) {
               reportFilesList.add(new ReportFile(reportFileNames[ifn]));
            }
            loadReports();
            showFocusReport(reportFilesList.get(0));
            OpMsgLog.LogMsg("Click on file for (focus) details'\n",
                  OpMsgLogger.LogLevel.PROMPT, true);
            cmpCtrlDisp.setEnabled(true);   // enabled when file(s) selected
         } else {}
         tabFolder.setSelection(tabImportData);
         captureOk = false;
         while (!shlMain.isDisposed()) {
            if (!display.readAndDispatch()) {
               display.sleep();
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Create the shell.
    * @param display
    */
   public LabTrend(Display display) {
      super(display, SWT.SHELL_TRIM);
      setMinimumSize(new Point(500, 700));
      setSize(750, 700);
      setText(PROG_ID);
      
      createContents(display);
      sLog = new TextLogger("", AppPaths.BASE_PATH + "logs");
      OpMsgLog.SetFileLogger(sLog);
      settings = new AppSettings(OpMsgLog);
   }

   public static void SelectFocusFile(int fileNum) {
      showFocusReport(reportFilesList.get(fileNum));
   }
   
   private static void clearFileListPanel() {
      if (focusFileSelectors != null && 
            focusFileSelectors.length > 0) {
         for (int ff = 0; ff < focusFileSelectors.length; ff++) {
            focusFileSelectors[ff] = null;
         }
         Control []child = cmpFileList.getChildren();
         for (int c = 0; c < child.length; c++) {
            child[c].dispose();
         }
      }
   }
   private static void clearTrendItemSels() {
      if (cmpTrendList != null) {
         Control []child = cmpTrendList.getChildren();
         for (int c = 0; c < child.length; c++) {
            child[c].dispose();;
         }
         chartItems.clear();
      }
   }
   private static int numTrendSelections() {
      int ret = 0;
      for (int t = 0; t < trending.trendList.size(); t++) {
         if (trendItemSelectors[t].selected) {
            ret++;
         }
      }
      return ret;
   }
   public static void ShowTrendItemSelections() {
      if (!deferringSelUpdate) {
         int nsel = numTrendSelections(); 
         if ( nsel== 0) {
            btnSaveTrendItemsList.setEnabled(false);
            lblNumSel.setText("No Items Selected");
         } else {
            lblNumSel.setText(nsel + "/" + trending.trendList.size() +
                  " Item(s) Selected");
            btnSaveTrendItemsList.setEnabled(true);
         }
         btnUpdateTrendData.setEnabled(nsel > 0);
         btnSelAllTrendDetails.setEnabled(nsel > 0);
         btnResetTrendDetails.setEnabled(nsel > 0);
      }
   }
   
   private static void clearTrendingDetails() {
      if (cmpTrendItemDetails != null &&
            cmpTrendItemDetails.size() > 0) {
         Iterator<CmpTrendItemDetails> itd = cmpTrendItemDetails.iterator();
         while (itd.hasNext()) {
            itd.next().dispose();
         }
         btnSaveTrendItemsList.setEnabled(false);
      }
   }
   
//   private static void clearChartDetails() {
//      if (cmpChartDetails != null &&
//            cmpChartDetails.size() > 0) {
//         Iterator<CmpChart> itd = cmpChartDetails.iterator();
//         while (itd.hasNext()) {
//            itd.next().dispose();
//         }
//      }
//   }
//   
   private static void loadReports() {
      clearFileListPanel();
      if (trending != null) {
         trending = null;
      }
      if (cmpSelectFocus == null) {
         cmpSelectFocus = new CmpSelectFocus(grpSelectFiles, SWT.NONE);
      }
      if (reportFilesList.size() == 0) {
         AppSettings.sData.SavedImportFilesList = null;
      } else {
         AppSettings.sData.SavedImportFilesList = new String[reportFilesList.size()];
         for (int r = 0; r < reportFilesList.size(); r++) {
            ReportFile rf = reportFilesList.get(r);
            try {
               rf.load(true);    // get date only for sorting
            } catch (Exception re) {
               OpMsgLog.LogMsg("Error reading collection date from report file:\n" +
                     rf.fileName +
                     "\nFile must conform to Labcorp format\n",
                     OpMsgLogger.LogLevel.ERROR, true);
               reportFilesList.remove(rf);
            }
         }
         if (reportFilesList.size() > 1) {
            // sort by collection date/time, reverse chronology
            Collections.sort(reportFilesList);
         }
         // now that file names are sorted, load the readings
         for (int r = 0; r < reportFilesList.size(); r++) {
            ReportFile rf = reportFilesList.get(r);
            try {
               rf.load(false);
            } catch (Exception re) {
                OpMsgLog.LogMsg(Utils.ExceptionString(re) + "\nError loading readings from file: " +
                  rf.fileName + "\n", OpMsgLogger.LogLevel.ERROR, true);
            }
            AppSettings.sData.SavedImportFilesList[r] = rf.fileName;
         }
         if (readings.size() > 1) {
            // sort the readings by name
            Collections.sort(readings, new Comparator<Reading>() {
               @Override
               public int compare(Reading t1, Reading t2) {
                  String name1 = ((Reading)t1).data.name.trim().toLowerCase();
                  String name2 = ((Reading)t2).data.name.trim().toLowerCase();
                  // f1.compareTo(f2) for increasing date/time, else decreasing
                  return name1.compareTo(name2);
               }
            });
         }
         txtFocusFilesHdr.setText("      Collected              File");
         focusFileSelectors = new FocusFileSelector[reportFilesList.size()];
         for (int rfi = 0; rfi < reportFilesList.size(); rfi++) {
            ReportFile rf = reportFilesList.get(rfi);
            String id = rf.collectionDateTime + " " + rf.fileName;
            focusFileSelectors[rfi] = new FocusFileSelector(cmpFileList, id, rfi);
//            new focusFileSelectors(cmpFileList, id, rfi);
//            for (int itm = 0; itm < reportFilesList.get(rfi).rptItems.size(); itm++) {
//               trending.AddTrendItem(rf, reportFilesList.get(rfi).rptItems.get(itm));
//            }
         }
         cmpFileList.pack();
         cmpFileList.layout();
         trending = new Trending();
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
         cmpTrendList.setVisible(true);
         GridData gd_TrendItmList = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
         gd_TrendItmList.heightHint = 18;
         cmpTrendList.setLayoutData(gd_TrendItmList);
         
         trendItemSelectors = new TrendItemSelector[trending.trendList.size()];
         for (int t = 0; t < trending.trendList.size(); t++) {
            TrendListItem tli = trending.trendList.get(t);
            trendItemSelectors[t] = new TrendItemSelector(cmpTrendList, tli.grpName, t);
         }
         
         cmpTrendList.pack();
         cmpTrendList.layout();
      }     // end else reportFiles size > 0
//      int bp=1;    // debug breakpoint
   }     // end loadReports

//   private static void enableTrendingSelection() {
//      boolean enable;
//      if ((trendItemSelectors |= null) && trending.trendList.size() > 0) {
//         for (int t = 0; t < trending.trendList.size(); t++) {
//            enable = rdoShowAllTrending.getSelection();
//            if (rdoShowRecentAbnormal.getSelection())
//            TrendListItem tli = trending.trendList.get(t);
//            trendItemSelectors[t].SetSelection(enable);
//         }
//      }
//   }
   static void clearTrendSelectors() {
      if (trendItemSelectors != null) {
         for (int its = 0; its < trendItemSelectors.length; its++) {
            trendItemSelectors[its] = null;
         }
         trendItemSelectors = null;
         cmpTrendList.setVisible(false);
      }
   }
   public static void displayTrendItemDetails(int tiIndex) {
      TrendListItem tli = trending.trendList.get(tiIndex);
      if (cmpTrendItemDetails == null) {
         cmpTrendItemDetails = new ArrayList<CmpTrendItemDetails>();
      }
      cmpTrendItemDetails.add(new CmpTrendItemDetails(cmpTrendDetails, SWT.NONE, tli));
      btnSaveTrendItemsList.setEnabled(true);
   }
   
   private static boolean cmdDisplayTrendItem(int tli) {
      boolean ret = false;
      if (trendItemSelectors[tli].chkSel.getSelection()) {
         if (rdoAllTrending.getSelection()) {
            return true;
         } else if (rdoAnyAbnormal.getSelection()) {
            return trending.trendList.get(tli).nAbnormal > 0;
         } else {
            return !trending.trendList.get(tli).latestWithinNormal;
         }
      }
      return ret;
   }
   
   private static void paintTrendListDisplay() {
      clearTrendingDetails();
      for (int ti = 0; ti < trending.trendList.size(); ti++) {
         if (cmdDisplayTrendItem(ti)) {
            displayTrendItemDetails(ti);
         }
      }
      cmpTrendDetails.layout();
      cmpTrendDetails.pack();
   }

   private static void showFocusReport(ReportFile rpt) {
      grpFocusReport.setText("FocusReportFile: " + 
                  rpt.collectionDateTime + " " + rpt.fileName);
      if (cmpItm != null) {
         for (int c = 0; c < cmpItm.length; c++) {
            cmpItm[c].dispose();
         }
         cmpItm = null;
      }
      ArrayList<Reading> focusReadings = new ArrayList<Reading>();
      for (int ir = 0; ir < readings.size(); ir++) {
         Reading rd = readings.get(ir);
         if (rd.rptFile == rpt) {
            focusReadings.add(rd);
         }
      }
      cmpItm = new CmpItemDetails[focusReadings.size()];
      for (int fr = 0; fr < focusReadings.size(); fr++) {
         cmpItm[fr] = new CmpItemDetails(cmpItemList, SWT.NONE);
         // itemLine is in three parts to allow color set on value
//         cmpItm[frd].setLineText(focusReadings.data.rpt.rptItems.get(itm));
         cmpItm[fr].setLineText(focusReadings.get(fr).data);
      }
      cmpItemList.pack();
      cmpItemList.layout();
   }     // end showFocusReport
      
   /**
    * Create contents of the shell.
    */
   protected void createContents(Display disp) {
      setLayout(new FillLayout(SWT.HORIZONTAL));
      sashForm = new SashForm(this, SWT.VERTICAL);
      sashForm.setToolTipText("sash");
      sashForm.setSashWidth(5);
      sashForm.setBackground(Colors.BLACK);
      sashForm.setLayout(new FormLayout());
      
      Composite cmpTabs = new Composite(sashForm, SWT.NONE);
      cmpTabs.setLayout(new FillLayout(SWT.HORIZONTAL));
      
      tabFolder = new TabFolder(cmpTabs, SWT.NONE);
      prevTabItemName = TAB_IMPORT_DATA_STR;
      
      tabImportData = new TabItem(tabFolder, SWT.NONE);
      tabImportData.setText("ImportData");
      Composite cmpImportedData = new Composite(tabFolder, SWT.NONE);
      tabImportData.setControl(cmpImportedData);
      
      cmpImportedData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
      cmpImportedData.setLayout(new FormLayout());
      
      Group grpImports = new Group(cmpImportedData, SWT.NONE);
      FormData fd_imports = new FormData();
      fd_imports.left = new FormAttachment(0);
      fd_imports.right = new FormAttachment(100, 0);
      fd_imports.top = new FormAttachment(0, 1);
      fd_imports.bottom = new FormAttachment(100);
      grpImports.setLayoutData(fd_imports);
      grpImports.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
      grpImports.setText("Imported Data");
      FormLayout fl_grpImports = new FormLayout();
      grpImports.setLayout(fl_grpImports);
      
      //  group for specifying report files
      //vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv      
      grpSelectFiles = new Group(grpImports, SWT.NONE);
      grpSelectFiles.setLayout(new FormLayout());
      FormData fd_grpSelectFiles = new FormData();
      fd_grpSelectFiles.top = new FormAttachment(0);
      fd_grpSelectFiles.bottom = new FormAttachment(28);
      fd_grpSelectFiles.left = new FormAttachment(0);
      fd_grpSelectFiles.right = new FormAttachment(100);
      grpSelectFiles.setLayoutData(fd_grpSelectFiles);
      grpSelectFiles.setText("Loaded Report File(s)");
      
      btnAddNewFiles = new Button(grpSelectFiles, SWT.NONE);
      FormData fd_btnAddNewFiles = new FormData();
      fd_btnAddNewFiles.bottom = new FormAttachment(0, 18);
      fd_btnAddNewFiles.top = new FormAttachment(0);
      fd_btnAddNewFiles.left = new FormAttachment(0, 140);
      fd_btnAddNewFiles.right = new FormAttachment(0, 260);
      btnAddNewFiles.setLayoutData(fd_btnAddNewFiles);
      btnAddNewFiles.setTouchEnabled(true);
      btnAddNewFiles.setSize(50, 25);
      btnAddNewFiles.setToolTipText("Browse for existing file(s)");
      btnAddNewFiles.setText("Add new report File(s)");
      btnAddNewFiles.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            OpMsgLog.LogMsg("Select focus file by clicking its button\n", 
                        OpMsgLogger.LogLevel.PROMPT, true);
//            boolean filesSelected = false;
            FileDialog fd = new FileDialog(shlMain, SWT.MULTI);
            fd.setFilterPath(AppSettings.sData.importFilesFolder);
            fd.setText("Select file(s) containing lab results");
            fd.setFilterNames(new String[] {
                  "Webpage,single file(*.mhtml)", 
                  "Webpage,complete(*.htm*)",
                  "Excel from Quest(*.xlsx)"});
            fd.setFilterExtensions(new String[] {"*.mhtml",
                                                 "*.htm*",
                                                 "*.xlsx"});
            if (fd.open() != null) {
               txtFocusFilesHdr.setText("");
               String []fdFileNames = fd.getFileNames();
               AppSettings.sData.importFilesFolder = fd.getFilterPath();
               for (int f = 0; f < fdFileNames.length; f++) {
                  String newFileName = fd.getFilterPath() + "\\" + fdFileNames[f];
                  ListIterator<ReportFile> itr = reportFilesList.listIterator();
                  boolean loaded = false;
                  while (itr.hasNext()) {
                     String oldFileName = itr.next().fileName;
                     if (newFileName.equals(oldFileName)) {
                        OpMsgLog.LogMsg(oldFileName + " is already loaded\n");
                        loaded = true;
                        break;
                     }
                  }
                  if (!loaded) {
                     reportFilesList.add(new ReportFile(newFileName));
                  }
               }
//               filesSelected = true;
               loadReports();
            }
            if (reportFilesList.size() > 0) {
               showFocusReport(reportFilesList.get(0));
               // 20240624: note there is no way to grey out (disable) tabItems in SWT
               //  but you can dispose it without disposing its control
               if (tabTrending.isDisposed()) {
                  tabTrending = new TabItem(tabFolder, SWT.NONE, TAB_TRENDING);
                  tabTrending.setText("TrendingItems");
                  tabTrending.setControl(cmpCtrlDisp);
               }
            } else {
               MessageBox msgbox = 
                     new MessageBox(shlMain, SWT.ICON_ERROR | SWT.YES | SWT.NO);
               msgbox.setText("You haven't selected any reports");
               msgbox.setMessage("Do you want to retry?");
               int ret = msgbox.open();
               if (ret != SWT.YES) {
                  shlMain.dispose();
                  System.exit(0);
               }
            }
         }
      });
      btnClearFiles = new Button(grpSelectFiles, SWT.NONE);
      FormData fd_btnClearFiles = new FormData();
      fd_btnClearFiles.bottom = new FormAttachment(0, 18);
      fd_btnClearFiles.top = new FormAttachment(0);
      fd_btnClearFiles.left = new FormAttachment(0, 320);
      btnClearFiles.setLayoutData(fd_btnClearFiles);
      btnClearFiles.setTouchEnabled(true);
      btnClearFiles.setSize(50, 25);
      btnClearFiles.setToolTipText("Clear file selections");
      btnClearFiles.setText("Clear file selections");
      btnClearFiles.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            tabTrending.dispose();
            txtFocusFilesHdr.setText("none");
            clearTrendItemSels();
            clearFileListPanel();
            clearTrendingDetails();
            reportFilesList.clear();
            readings.clear();
         }
      });
      txtFocusFilesHdr = new Text(grpSelectFiles, SWT.BORDER | SWT.READ_ONLY);
      FormData fd_txtFocusFilesHdr = new FormData();
      fd_txtFocusFilesHdr.bottom = new FormAttachment(0, 36);
      fd_txtFocusFilesHdr.right = new FormAttachment(0, 542);
      fd_txtFocusFilesHdr.top = new FormAttachment(0, 18);
      fd_txtFocusFilesHdr.left = new FormAttachment(0);
      txtFocusFilesHdr.setLayoutData(fd_txtFocusFilesHdr);
      txtFocusFilesHdr.setFont(SWTResourceManager.getFont("Courier New", 9, SWT.NORMAL));
      txtFocusFilesHdr.setText("none");
  
      cmpSelectFocus = new CmpSelectFocus(grpSelectFiles, SWT.NONE);
      FormData fd_cmpSelectFocus = new FormData();
      fd_cmpSelectFocus.top = new FormAttachment(0, 36);
      fd_cmpSelectFocus.bottom = new FormAttachment(100);
      fd_cmpSelectFocus.left = new FormAttachment(0, 1);
      fd_cmpSelectFocus.right = new FormAttachment(100, -2);
      cmpSelectFocus.setLayoutData(fd_cmpSelectFocus);
      scmpSelectFocus = new ScrolledComposite(cmpSelectFocus, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
      scmpSelectFocus.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
      cmpFileList = new Composite(scmpSelectFocus, SWT.NONE);
      GridLayout gl_cmpFileList = new GridLayout(1, false);
      gl_cmpFileList.horizontalSpacing = 0;
      gl_cmpFileList.marginWidth = 0;
      gl_cmpFileList.marginHeight = 0;
      gl_cmpFileList.verticalSpacing = 0;
      gl_cmpFileList.marginLeft = 0;
      gl_cmpFileList.marginRight = 0;
      gl_cmpFileList.verticalSpacing = 0;
      cmpFileList.setLayout(gl_cmpFileList);      
      scmpSelectFocus.setContent(cmpFileList);
      
      //  group for displaying data in focus report file
      //vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
      grpFocusReport = new Group(grpImports, SWT.NONE);
      FormData fd_grpFocusReport = new FormData();
      fd_grpFocusReport.left = new FormAttachment(0);
      fd_grpFocusReport.right = new FormAttachment(100);
      fd_grpFocusReport.bottom = new FormAttachment(100);
      fd_grpFocusReport.top = new FormAttachment(28);
      grpFocusReport.setLayoutData(fd_grpFocusReport);
      grpFocusReport.setText("Focus Report File:");
      GridLayout gl_grpFocusReport = new GridLayout(1, false);
      gl_grpFocusReport.horizontalSpacing = 0;
      gl_grpFocusReport.marginWidth = 1;
      gl_grpFocusReport.marginHeight = 1;
      gl_grpFocusReport.verticalSpacing = 1;
      grpFocusReport.setLayout(gl_grpFocusReport);
      lblFocusReport = new Label(grpFocusReport, SWT.NONE);
      lblFocusReport.setFont(SWTResourceManager.getFont("Courier New", 9, SWT.NORMAL));
      lblFocusReport.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
      lblFocusReport.setText("                        Item   NormLo      Value     NormHi   Units ");
      
      cmpRptItems = new Composite(grpFocusReport, SWT.NONE);
      cmpRptItems.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
      GridLayout gl_cmpRptItems = new GridLayout(1, false);
      gl_cmpRptItems.horizontalSpacing = 0;
      gl_cmpRptItems.marginHeight = 0;
      gl_cmpRptItems.marginWidth = 0;
      gl_cmpRptItems.verticalSpacing = 0;
      cmpRptItems.setLayout(gl_cmpRptItems);
      
      scmpFocusRpt = new ScrolledComposite(cmpRptItems,  SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
      scmpFocusRpt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
      cmpItemList = new Composite(scmpFocusRpt, SWT.BORDER);
      GridLayout gl_cmpItemList = new GridLayout(1, false);
      gl_cmpItemList.horizontalSpacing = 0;
      gl_cmpItemList.marginWidth = 0;
      gl_cmpItemList.marginHeight = 0;
      gl_cmpItemList.verticalSpacing = 0;
      gl_cmpItemList.marginLeft = 0;
      gl_cmpItemList.marginRight = 0;
      gl_cmpItemList.verticalSpacing = 0;
      cmpItemList.setLayout(gl_cmpItemList);
      scmpFocusRpt.setContent(cmpItemList);     
      //    E N D   I M P O R T   T A B
      
      
      //*******************************************************************************
      //*******************************************************************************
      //     T A B   T O   S E L E C T  A N D  D I S P L A Y   T R E N D I N G
      // note that this tab depends on data read from excel template file
      //      so we disable it here and finish populating it after reading template 
      tabTrending = new TabItem(tabFolder, SWT.NONE);
      tabTrending.setText("TrendingItems");
      cmpCtrlDisp = new Composite(tabFolder, SWT.NONE);
      tabTrending.setControl(cmpCtrlDisp);
      cmpCtrlDisp.setLayout(new FormLayout());
//      cmpCtrlDisp.setEnabled(false);   // enabled when file(s) selected
      
      //vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
      //  show all trend items
      grpItemSel = new Group(cmpCtrlDisp, SWT.NONE);
      FormData fd_grpItemSel = new FormData();
      fd_grpItemSel.bottom = new FormAttachment(100, -1);
      fd_grpItemSel.right = new FormAttachment(35, -1);
      fd_grpItemSel.top = new FormAttachment(0);
      fd_grpItemSel.left = new FormAttachment(0, 1);
      grpItemSel.setLayoutData(fd_grpItemSel);
      grpItemSel.setText("Item Selection for trending display");
      GridLayout gl_grpItemSel = new GridLayout(2, false);
      gl_grpItemSel.verticalSpacing = 1;
      grpItemSel.setLayout(gl_grpItemSel);
      
      cmpSelectionBtns = new Composite(grpItemSel, SWT.NONE);
      cmpSelectionBtns.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
      GridLayout gl_cmpSelectionBtns = new GridLayout(2, false);
      gl_cmpSelectionBtns.marginHeight = 1;
      gl_cmpSelectionBtns.verticalSpacing = 1;
      cmpSelectionBtns.setLayout(gl_cmpSelectionBtns);
            
      lblNumSel = new Label(cmpSelectionBtns, SWT.CENTER);
      lblNumSel.setText("NoItemsSelected");
      lblNumSel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 2, 1));
      btnSelAllTrendDetails = new Button(cmpSelectionBtns, SWT.NONE);
      btnSelAllTrendDetails.setEnabled(true);
      btnSelAllTrendDetails.setText("Select All");
      btnSelAllTrendDetails.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            for (int t = 0; t < trending.trendList.size(); t++) {
               trendItemSelectors[t].SetSelection(true);
            }
            btnUpdateTrendData.setEnabled(true);
            lblNumSel.setText(trending.trendList.size() + " Items Selected");
            btnSaveTrendItemsList.setEnabled(true);
         }
      });
      btnLoadTrendItemsList = new Button(cmpSelectionBtns, SWT.NONE);
      btnLoadTrendItemsList.setText("Load from File");
      btnLoadTrendItemsList.setToolTipText("Read list of trend items from a file");
      btnLoadTrendItemsList.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         public void widgetSelected(
                  org.eclipse.swt.events.SelectionEvent e)
         {
            String fName = Utils.GetOpenFileName(shlMain,
                  "Trending items list", "list files(*.txt)",
                  "*.txt", AppSettings.sData.itemSelectionsListFolder);
            if (fName != null)
            {
               try
               {
                  FileReader fr = new FileReader(fName);
                  BufferedReader br = new BufferedReader(fr);
                  String rline;
                  deferringSelUpdate = true;
                  while ((rline = br.readLine()) != null) {
                     for (int tl = 0; tl < trending.trendList.size(); tl++) {
                        TrendListItem tli = trending.trendList.get(tl);
                        if (rline.trim().equalsIgnoreCase(tli.grpName)) {
                           trendItemSelectors[tl].SetSelection(true);
                           break;
                        }
                     }
                  }
                  br.close();
                  AppSettings.sData.itemSelectionsListFolder = new File(fName).getAbsolutePath();
                  OpMsgLog.LogMsg("Item selections recalled from " + fName + "\n");
               } catch (Exception ex) {
                  String err = Utils.ExceptionString(ex)
                           + "\nException recalling item selections from "
                           + fName + "\n";
                  MessageBox mb = new MessageBox(shlMain,
                           SWT.ICON_ERROR | SWT.OK);
                  mb.setText("ERROR");
                  mb.setMessage(err);
                  mb.open();
               }
               deferringSelUpdate = false;
               ShowTrendItemSelections();
            }
         }
      });
      
      btnResetTrendDetails = new Button(cmpSelectionBtns, SWT.NONE);
      btnResetTrendDetails.setEnabled(true);
      btnResetTrendDetails.setText("Reset");
      btnResetTrendDetails.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            deferringSelUpdate = true;
            for (int t = 0; t < trending.trendList.size(); t++) {
               trendItemSelectors[t].SetSelection(false);
            }
            lblNumSel.setText("No Items Selected");
            btnSaveTrendItemsList.setEnabled(false);
            deferringSelUpdate = false;
            ShowTrendItemSelections();
         }
      });
      btnSaveTrendItemsList = new Button(cmpSelectionBtns, SWT.NONE);
      btnSaveTrendItemsList.setEnabled(false);
      btnSaveTrendItemsList.setText("Save selections to File");
      btnSaveTrendItemsList.setToolTipText("Save list of currently selected items to a file");
      btnSaveTrendItemsList.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         public void widgetSelected(
                  org.eclipse.swt.events.SelectionEvent e)
         {
            String fName = Utils.GetSaveFileName(shlMain,
                     "Trending Items List", "text files(*.txt)",
                     "*.txt", AppPaths.CFG_PATH, true);
            if (fName != null)
            {
               try
               {
                  deferringSelUpdate = true;
                  File file = new File(fName);
                  PrintWriter pw = new PrintWriter(
                                             new FileOutputStream(file));
                  for (int it = 0; it < trendItemSelectors.length; it++) {
                     if (trendItemSelectors[it].selected) {
                        int trListSelIndex = trendItemSelectors[it].tli;
                        TrendListItem tli = trending.trendList.get(trListSelIndex);
                        String selName = tli.grpName;
                        pw.println(selName);
                     }
                  }
                  pw.close();
                  btnLoadTrendItemsList.setEnabled(true);
                  OpMsgLog.LogMsg("Trend item selections saved to " + fName + "\n");
               } catch (Exception ex) {
                  String err = Utils.ExceptionString(ex)
                           + "\nException saving trend item selections to "
                           + fName + "\n";
                  OpMsgLog.LogMsg(err, OpMsgLogger.LogLevel.ERROR);
                  MessageBox mb = new MessageBox(shlMain,
                           SWT.ICON_ERROR | SWT.OK);
                  mb.setText("ERROR");
                  mb.setMessage(err);
                  mb.open();
               }
               deferringSelUpdate = false;
               ShowTrendItemSelections();
            }
         }
      });
      Label lblTrendingDetailsTitle = new Label(cmpCtrlDisp, SWT.NONE);
      FormData fd_lblTrendingDetailsTitle = new FormData();
      fd_lblTrendingDetailsTitle.top = new FormAttachment(0, 1);
      fd_lblTrendingDetailsTitle.bottom = new FormAttachment(0, 16);
      fd_lblTrendingDetailsTitle.left = new FormAttachment(grpItemSel, 1);
      lblTrendingDetailsTitle.setLayoutData(fd_lblTrendingDetailsTitle);
      lblTrendingDetailsTitle.setText("Trending - history of selected readings");

      Label lblTrendingHdr = new Label(cmpCtrlDisp, SWT.NONE);
      FormData fd_lblTrendingHdr = new FormData();
      fd_lblTrendingHdr.top = new FormAttachment(0, 80);
      fd_lblTrendingHdr.bottom = new FormAttachment(0, 96);
      fd_lblTrendingHdr.left = new FormAttachment(grpItemSel, 1);
      lblTrendingHdr.setLayoutData(fd_lblTrendingHdr);
//      lblTrendingHdr.setFont(TrendFonts.FIXED_NORMAL); 
      lblTrendingHdr.setText("   Collection                      Min            Value                 Max          Units");
      //----------------------------------------------------------
      //  scrolling composite to show selected trend item details
      cmpTrendDetailsContainer = new Composite(cmpCtrlDisp, SWT.NONE);
      FormData fd_cmpTrendDetailsContainer = new FormData();
      fd_cmpTrendDetailsContainer.bottom = new FormAttachment(100);
      fd_cmpTrendDetailsContainer.top = new FormAttachment(0, 96);
      fd_cmpTrendDetailsContainer.right = new FormAttachment(100);
      fd_cmpTrendDetailsContainer.left = new FormAttachment(36);
      cmpTrendDetailsContainer.setLayoutData(fd_cmpTrendDetailsContainer);
      GridLayout gl_cmpTrendDetailsContainer = new GridLayout(1, false);
      gl_cmpTrendDetailsContainer.marginHeight = 0;
      gl_cmpTrendDetailsContainer.verticalSpacing = 0;
      cmpTrendDetailsContainer.setLayout(gl_cmpTrendDetailsContainer);
      scmpTrendDetails = new ScrolledComposite(cmpTrendDetailsContainer,
                                 SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
      scmpTrendDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
      cmpTrendDetails = new Composite(scmpTrendDetails, SWT.NONE);
      GridLayout gl_cmpTrendDetailsList = new GridLayout(1, false);
      gl_cmpTrendDetailsList.horizontalSpacing = 0;
      gl_cmpTrendDetailsList.marginWidth = 0;
      gl_cmpTrendDetailsList.marginHeight = 0;
      gl_cmpTrendDetailsList.verticalSpacing = 0;
      gl_cmpTrendDetailsList.marginLeft = 0;
      gl_cmpTrendDetailsList.marginRight = 0;
      gl_cmpTrendDetailsList.verticalSpacing = 0;
      cmpTrendDetails.setLayout(gl_cmpTrendDetailsList);
      cmpTrendDetails.setLayoutData(new GridData());
      scmpTrendDetails.setContent(cmpTrendDetails);
      new Label(grpItemSel, SWT.NONE);
    
      //--------------------------------------------------------
      //  scrolling composite to contain list of trending items
      cmpTrendItems = new Composite(grpItemSel, SWT.NONE);
      cmpTrendItems.setLayout(new GridLayout(1, false));
      cmpTrendItems.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
      scmpTrendItems = new ScrolledComposite(cmpTrendItems, SWT.BORDER | SWT.V_SCROLL);
      scmpTrendItems.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
      cmpTrendList = new Composite(scmpTrendItems, SWT.NONE);
      GridLayout gl_cmpTrendItemsList = new GridLayout(1, false);
      gl_cmpTrendItemsList.horizontalSpacing = 0;
      gl_cmpTrendItemsList.marginWidth = 0;
      gl_cmpTrendItemsList.marginHeight = 0;
      gl_cmpTrendItemsList.verticalSpacing = 0;
      gl_cmpTrendItemsList.marginLeft = 0;
      gl_cmpTrendItemsList.marginRight = 0;
      gl_cmpTrendItemsList.verticalSpacing = 0;
      cmpTrendList.setLayout(gl_cmpTrendItemsList);
      scmpTrendItems.setContent(cmpTrendList);
      

      //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
      
      //*******************************************************************************
      //*******************************************************************************
      //     T A B   T O   D I S P L A Y   C H A R T S
      tabCharts = new TabItem(tabFolder, SWT.NONE);
      tabCharts.setText("Charts");
      
      
      //=======================================================================================
      cmpDataDisp = new Composite(tabFolder, SWT.NONE);
      tabCharts.setControl(cmpDataDisp);
      cmpDataDisp.setLayout(new FormLayout());
      cmpChart = new Composite(cmpDataDisp, SWT.NONE);
      cmpChart.setLayout(new FillLayout(SWT.HORIZONTAL));
      FormData fd_cmpChart = new FormData();
      fd_cmpChart.top = new FormAttachment(grpItemSel);
      
      Group grpTrendingBtns = new Group(cmpCtrlDisp, SWT.NONE);
      grpTrendingBtns.setText("Only show when reading is");
      GridLayout gl_grpTrendingBtns = new GridLayout(4, false);
      gl_grpTrendingBtns.horizontalSpacing = 2;
      gl_grpTrendingBtns.verticalSpacing = 0;
      gl_grpTrendingBtns.marginHeight = 0;
      grpTrendingBtns.setLayout(gl_grpTrendingBtns);
      FormData fd_grpTrendingBtns = new FormData();
      fd_grpTrendingBtns.left = new FormAttachment(grpItemSel, 2);
      fd_grpTrendingBtns.bottom = new FormAttachment(0, 50);
      fd_grpTrendingBtns.top = new FormAttachment(0, 16);
      fd_grpTrendingBtns.right = new FormAttachment(100);
      grpTrendingBtns.setLayoutData(fd_grpTrendingBtns);
      rdoAllTrending = new Button(grpTrendingBtns, SWT.RADIO);
      rdoAllTrending.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
      rdoAllTrending.setSize(58, 16);
      rdoAllTrending.setText("Always");
      rdoAllTrending.setSelection(true);
      rdoRecentAbnormal = new Button(grpTrendingBtns, SWT.RADIO);
      rdoRecentAbnormal.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
      rdoRecentAbnormal.setSize(143, 16);
      rdoRecentAbnormal.setToolTipText("");
      rdoRecentAbnormal.setText("Latest Outside Normal");
      rdoAnyAbnormal = new Button(grpTrendingBtns, SWT.RADIO);
      rdoAnyAbnormal.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
      rdoAnyAbnormal.setSize(98, 16);
      rdoAnyAbnormal.setText("Any Outside Normal");
      new Label(grpTrendingBtns, SWT.NONE);
      
      cmpUpdateHistory = new Composite(cmpCtrlDisp, SWT.NONE);
      FormData fd_cmpUpdateHistory = new FormData();
      fd_cmpUpdateHistory.right = new FormAttachment(100);
      fd_cmpUpdateHistory.top = new FormAttachment(0, 52);
      fd_cmpUpdateHistory.left = new FormAttachment(grpItemSel, 2);
      cmpUpdateHistory.setLayoutData(fd_cmpUpdateHistory);
      GridLayout gl_cmpUpdateHistory = new GridLayout(2, false);
      gl_cmpUpdateHistory.marginHeight = 0;
      gl_cmpUpdateHistory.verticalSpacing = 0;
      cmpUpdateHistory.setLayout(gl_cmpUpdateHistory);
      btnUpdateTrendData = new Button(cmpUpdateHistory, SWT.NONE);
      btnUpdateTrendData.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
      btnUpdateTrendData.setSize(0, 20);
      btnUpdateTrendData.setText("Update Table Below");
      btnUpdateTrendData.setEnabled(false);
      btnUpdateChart = new Button(cmpUpdateHistory, SWT.NONE);
      btnUpdateChart.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
      btnUpdateChart.setSize(79, 20);
      btnUpdateChart.setText("Update Chart");
      btnUpdateChart.setEnabled(false);
      btnUpdateChart.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            showChart();
         }
      });
      btnUpdateTrendData.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            paintTrendListDisplay();
         }
      });
      rdoAnyAbnormal.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            paintTrendListDisplay();
         }
      });
      rdoRecentAbnormal.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            paintTrendListDisplay();
         }
      });
      rdoAllTrending.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            paintTrendListDisplay();
         }
      });
      
      fd_cmpChart.bottom = new FormAttachment(100);
      fd_cmpChart.right = new FormAttachment(100);
      fd_cmpChart.left = new FormAttachment(0);
      cmpChart.setLayoutData(fd_cmpChart);      
      
      //*******************************************************************************
      //     B O T T O M   S A S H - always displayed below tab folder
      //    display messages to operator
      //*******************************************************************************
      Group grpOpMsg = new Group(sashForm, SWT.NONE);
      fd_grpOpMsg = new FormData();
      fd_grpOpMsg.left = new FormAttachment(cmpTabs, 0, SWT.LEFT);
//      fd_grpOpMsg.top = new FormAttachment(sash, 1);
//      sash.setLayout(new FillLayout(SWT.HORIZONTAL));
      fd_grpOpMsg.top = new FormAttachment(cmpTabs, 10);
      fd_grpOpMsg.bottom = new FormAttachment(100, 0);
      grpOpMsg.setLayoutData(fd_grpOpMsg);
      grpOpMsg.setLayout(new FillLayout(SWT.HORIZONTAL));
      // **** end temporary
      
      stxtOpMsgLog = new StyledText(grpOpMsg, 
              SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
//      stxtOpMsgLog.setSize(553, 297);
      stxtOpMsgLog.setFont(SWTResourceManager.getFont("Courier New", 9, SWT.NORMAL));
      OpMsgLog = new OpMsgLogger(disp, stxtOpMsgLog);
      sashForm.setWeights(new int[] {300, 50});
      //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^            
   }     //    end createContents(Display disp)
   
   void setupFileSelection() {
      OpMsgLog.LogMsg("Choose report file from above and click 'AcceptFile'\n",
            OpMsgLogger.LogLevel.PROMPT);
      btnNewFile.setEnabled(true);
      btnAddNewFiles.setEnabled(true);
   }
   
   static public void addToChart(TrendListItem tli, boolean show) {
      if (show) {
         chartItems.add(tli);
      } else {
         for (int c = 0; c < chartItems.size(); c++) {
            if (tli == chartItems.get(c)) {
               chartItems.remove(c);
               return;
            }
         }
      }
      btnUpdateChart.setEnabled(show);
   }
   
   static void showChart() {
      final Color []LINE_COLORS = {Colors.BLACK, Colors.RED, Colors.BLUE,
                                   Colors.GREEN, Colors.ORANGE,
                                   Colors.PALE_BLUE, Colors.PALE_RED, Colors.YELLOW};
      final ILineSeries.PlotSymbolType []PLOT_SYMBOLS = 
                                  {ILineSeries.PlotSymbolType.CIRCLE,
                                        ILineSeries.PlotSymbolType.SQUARE,
                                        ILineSeries.PlotSymbolType.DIAMOND,
                                        ILineSeries.PlotSymbolType.TRIANGLE,
                                        ILineSeries.PlotSymbolType.INVERTED_TRIANGLE,
                                        ILineSeries.PlotSymbolType.CROSS,
                                        ILineSeries.PlotSymbolType.PLUS};
      // set up xSeries, an array of java.util.date to define x axis
      if (!(chart == null)) {
         chart.dispose();
      }
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      chart = new Chart(cmpChart, SWT.NONE);
      ILegend legend = chart.getLegend();
      legend.setPosition(SWT.TOP);
      ILineSeries<?> []scatterSeries = new ILineSeries[chartItems.size()];
      java.util.Date firstDate = Utils.convertToDateViaInstant(chartItems.get(0).
            trendData.get(0).rptFile.collectionDate);
      java.util.Date lastDate = firstDate;
      for (int ci = 0; ci < chartItems.size(); ci++) {
         TrendListItem tli = chartItems.get(ci); 
         java.util.Date []uDates = new java.util.Date[tli.trendData.size()];
         double []vals = new double[tli.trendData.size()];
         for (int it = 0; it < tli.trendData.size(); it++) {
            uDates[it] = Utils.convertToDateViaInstant(
                        tli.trendData.get(it).rptFile.collectionDate);
            if (uDates[it].before(firstDate)) {
               firstDate = uDates[it];
            } else if (uDates[it].after(lastDate)) {
               lastDate = uDates[it];
            }
            vals[it] = Double.parseDouble(tli.trendData.get(it).data.valStr);
         }
         String yAxisLabel = tli.trendData.get(0).data.units;
         ISeriesSet sSet = chart.getSeriesSet();
         IAxisSet axisSet = chart.getAxisSet();
         IAxis xAxis = axisSet.getXAxis(0);
         xAxis.getTitle().setText("Date");
         IAxis yAxis = axisSet.getYAxis(0);
         yAxis.getTitle().setText(yAxisLabel);
         scatterSeries[ci] = (ILineSeries<?>)sSet.createSeries(
                                      SeriesType.LINE, tli.grpName);
         scatterSeries[ci].setSymbolSize(4);
         scatterSeries[ci].setXDateSeries(uDates);
         scatterSeries[ci].setYSeries(vals);
         scatterSeries[ci].setLineColor(LINE_COLORS[ci % LINE_COLORS.length]);
         scatterSeries[ci].setSymbolType(PLOT_SYMBOLS[ci % PLOT_SYMBOLS.length]);
         axisSet.adjustRange();
         mouseListener = new ChartMouseListener(chart);

         IAxisTick xTick = axisSet.getXAxis(0).getTick();
         xTick.setFormat(new SimpleDateFormat("MM/YY"));
      }
      chart.getTitle().setText(String.format("%s thru %s",
            df.format(firstDate), df.format(lastDate)));
      mouseListener = new ChartMouseListener(chart);
   }
   
   private static void selectTab(String tabName) {
      if (tabName.equals(TAB_IMPORT_DATA_STR)) {
         sashForm.setWeights(new int[] {300, 50});
      } else if (tabName.equals(TAB_TRENDING_STR)) {
         sashForm.setWeights(new int[] {40, 30});
//         sashForm.setWeights(new int[] {70, 20});
      } else if (tabName.equals(TAB_CHARTS_STR)) {
         sashForm.setWeights(new int[] {60, 10});
      }
      if (!tabName.equals(prevTabItemName)) {
         sashForm.layout();
         prevTabItemName = tabName;
      }
   }   
   
   @Override
   protected void checkSubclass()
   {
      // Disable the check that prevents subclassing of SWT components
   }
}
