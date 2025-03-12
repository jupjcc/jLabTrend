package trendListPkg;

import java.util.ArrayList;

import importPkg.Reading;
import mainPkg.LabTrend;

public class Trending {
   // readings are grouped by parent report file
   // trendList items are grouped by item name 
   public ArrayList<TrendListItem> trendList = new ArrayList<TrendListItem>();
//   public ArrayList<Reading> itemList = new ArrayList<grpName();
   
   public Trending() {
      String prevName = "?";
      System.out.println("Trending List contains " + 
                        LabTrend.Imports.readings.size() + " readings");
      trendList.clear();
      TrendListItem tli = null;
      for (int re = 0; re < LabTrend.Imports.readings.size(); re++) {
         Reading rding = LabTrend.Imports.readings.get(re);
         if (!rding.data.name.equalsIgnoreCase(prevName)) {
            // a name not encountered before; add new item
            tli = new TrendListItem(rding.data.name, rding);
            trendList.add(tli);
            prevName = rding.data.name; 
         } else {
            tli.addTrendItem(rding);
         }
      }
//      System.out.println("trendList:");
//      for (int tl = 0; tl < trendList.size(); tl++) {
//         TrendListItem tlip = trendList.get(tl); 
//         System.out.println(tl + ". " + tlip.grpName + ":");
//         for (int td = 0; td < tlip.trendData.size(); td++) {
//            Reading tdr = tlip.trendData.get(td);
//            System.out.println("  " + tdr.data.name +
//                  ", file " + tdr.rptFile.fileName);
//         }
//      }
      int bp=1;
   }
   
//   public void showList(String title) {
//      System.out.println("\nTrend Items " + title + "\n");
//      for (int i = 0; i < trendList.size(); i++) {
//         TrendListItem tli = trendList.get(i);
//         System.out.println(i + ". " + tli.grpName + ":\n");
//         for (int m = 0; m< tli.trendData.size(); m++) {
//            Reading rd = tli.trendData.get(m);
//            System.out.println("   " + m + ". " + rd.data.itemLineValue());
//         }
//      }
//      int bp=1;
//   }
}
