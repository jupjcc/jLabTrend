package importPkg;

public class Reading {
   public ReportFile rptFile;
   public DataPoint data;
   public boolean inTrendList = false;
   
   public Reading(ReportFile rf, DataPoint dp) {
      rptFile = rf;
      data = dp;
   }
   
}
