package trendListPkg;

import java.util.ArrayList;

import importPkg.Reading;

public class TrendListItem {
   public String grpName = "?";
   public ArrayList<Reading> trendData = new ArrayList<Reading>();
   public boolean displayed = false;
   public boolean latestWithinNormal = true;
   public int nAbnormal = 0;
   String prevName = "?";
   
   private void addToList(Reading rd) {
      if (!rd.data.withinLimits()) {
         nAbnormal++;
      }
      rd.inTrendList = true;
      trendData.add(rd);
   }
   
   public TrendListItem(String name, Reading rd) {
      latestWithinNormal = rd.data.withinLimits();
      grpName = name;
      addToList(rd);
   }

   public void addTrendItem(Reading rd) {
      rd.inTrendList = true;
      addToList(rd);
   }
}
