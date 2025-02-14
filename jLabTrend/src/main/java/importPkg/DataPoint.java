package importPkg;

import java.util.ArrayList;
import java.util.List;

import utilsPkg.Utils;

public class DataPoint {
   public String name;
   public TrendItemData data;
   public String valStr = "--";
   public String minNormalStr = "--";
   public String maxNormalStr = "--";
   public String units;
   
   double minNormal = -999.;
   double maxNormal = -999.;
   String flag = "?";
   String status = "?";
   String performedBy = "?";;
   List<String> fields = new ArrayList<String>();
   
   public DataPoint(String htmLine) throws Exception {
      //  example of htmLine:
      // <td><font face="Arial" size="1">-RBC</font></td></tr></tbody></table></td><td></td><td nowrap="" valign="TOP"><font face="Arial" size="1">4.72</font></td><td></td><td colspan="3" nowrap="" valign="TOP"><table cellspacing="0" width="100%" bgcolor="FFFFFF"><tbody><tr><td><font face="Arial" size="1">4.14-5.80</font></td></tr></tbody></table></td><td></td><td nowrap="" valign="TOP"><table cellspacing="0" width="100%" bgcolor="FFFFFF"><tbody><tr><td><font face="Arial" size="1">x10E6/uL</font></td></tr></tbody></table></td><td></td><td></td><td></td><td></td><td></td><td nowrap="" valign="TOP"><table cellspacing="0" width="100%" bgcolor="FFFFFF"><tbody><tr><td><font face="Arial" size="1"><center>F</center></font></td></tr></tbody></table></td><td></td><td></td><td colspan="2" nowrap="" valign="TOP"><table cellspacing="0" width="100%" bgcolor="FFFFFF"><tbody><tr><td><font face="Arial" size="1">01</font></td></tr></tbody></table></td></tr>
      String []items = lineItems(htmLine);
      name = items[0].substring(1);
      valStr = numericStr(items[1]);
      String normalStr = items[2];
      int dashPos = normalStr.indexOf("-"); 
      if ( dashPos > 0) {
         String minStr = normalStr.substring(0, dashPos);
         String maxStr = normalStr.substring(dashPos + 1);
         minNormalStr = numericStr(minStr);
         maxNormalStr = numericStr(maxStr);
      }
      units = items[3];
      flag = items[4];
      data = new TrendItemData(minNormalStr, maxNormalStr, valStr, units);
   }
   public DataPoint(String pname,         String pvalStr,
                    String pminNormalStr, String pmaxNormalStr,
                    String punits) throws Exception {
      name = pname;
      valStr = pvalStr;
      minNormalStr = pminNormalStr;
      maxNormalStr = pmaxNormalStr;
      units = punits;
      data = new TrendItemData(name, valStr, "*",
            minNormalStr, maxNormalStr, units);
   }
   public DataPoint(String []lineItems) throws Exception {
      name = lineItems[0];
      valStr = lineItems[1].replace("&lt;", "<").replace("&gt;", ">");
      String []rngFields = lineItems[2].split("-");
      if (rngFields.length == 1) {
         if (rngFields[0].startsWith("&gt;")) {
            try {
               double min = Double.parseDouble(rngFields[0].substring(4));
               minNormalStr = Double.toString(min);
            } catch (Exception pe) {
               pe.printStackTrace();
            }
         } else if (rngFields[0].startsWith("&lt;")) {
            try {
               double max = Double.parseDouble(rngFields[0].substring(4));
               maxNormalStr = Double.toString(max);
            } catch (Exception pe) {
               pe.printStackTrace();
            }
         }
      } else {
         for (int f = 0; f < rngFields.length; f++) {
            try {
               Double.parseDouble(rngFields[f]);
               if (minNormalStr.equals("--")) {
                  minNormalStr = rngFields[f];
               } else if (maxNormalStr.equals("--")) {
                  maxNormalStr = rngFields[f];
               }
            } catch (Exception pe) {
               pe.printStackTrace();
            }
         }
      }
      units = lineItems[3];
      flag = lineItems[4];
      data = new TrendItemData(name, valStr, flag,
                               minNormalStr, maxNormalStr, units);
   }
   public boolean hasLimits() {
      if (Utils.LegitDoubleString(minNormalStr) ||
          Utils.LegitDoubleString(maxNormalStr)) {
         return true;
      } else {
         return false;
      }
   }
   public boolean withinLimits() {
      double val, min, max;
      if (Utils.LegitDoubleString(valStr)) {
         val = Double.parseDouble(valStr); 
         if (Utils.LegitDoubleString(minNormalStr)) {
            min = Double.parseDouble(minNormalStr);
            if (val >= min) {
               if (Utils.LegitDoubleString(maxNormalStr)) {
                  max = Double.parseDouble(maxNormalStr);
                  return val <= max;
               } else {
                  return true;
               }               
            }
         } else {
            // no min defined
            if (Utils.LegitDoubleString(maxNormalStr)) {
               max = Double.parseDouble(maxNormalStr);
               return val <= max;
            } else {
               // no min or max defined
               return true;
            }
         }
      }
      return false;
   }

   public String getName() {
      return name;
   }   
   public String itemLineLeft() {
      String ret = String.format("%24s %7s", name, minNormalStr);
      return ret;      
   }
   public String itemLineValue() {
      String ret = String.format("%7s ", valStr);
      return ret;      
   }
   public String itemLineRight() {
      String ret = String.format("%7s   %s", maxNormalStr, units);
      return ret;      
   }
   private String numericStr(String valueFld) {
      String numStr = "--";
      try {
         double value = Double.parseDouble(valueFld);
         numStr = String.format("%4.2f",  value);
      } catch (Exception pe) {}
      return numStr; 
   }
   
   public String itemLine() {
      String ret = String.format("%24s %6s %6s %6s %s",
               name, minNormalStr, valStr, maxNormalStr, units);
      return ret;
   }
   
   private String []lineItems(String line) {
      List<String> itms = new ArrayList<String>();
      String item;
      byte [] lb = new byte[line.length()];
      lb = line.getBytes();
      byte []bItm;
      // items on line are assumed to appear just before html </FONT>
      int posLine;
      int posItemEnd = -1;
      for (posLine = 7; posLine < line.length(); posLine++) {
         if (lb[posLine] == '<' &&
             lb[posLine+1] == '/' &&
             lb[posLine+2] == 'F' &&
             lb[posLine+3] == 'O' &&
             lb[posLine+4] == 'N' &&
             lb[posLine+5] == 'T' &&
             lb[posLine+6] == '>' ) {
            //  step backwards looking for > just before item
            posItemEnd = posLine - 1;
            for (int pos = posItemEnd; pos > 0; pos-- ) {
               if (lb[pos] == '>') {
                  bItm = new byte[posItemEnd - pos];
                  for (int ib = 0; ib < bItm.length; ib++) {
                     bItm[ib] = lb[ib + pos + 1];
                  }
                  item = new String(bItm);
                  itms.add(item);
                  break;
               }
            }

         }
      }
      String []ret = new String[itms.size()];
      for (int r = 0; r < ret.length; r++) {
         ret[r] = itms.get(r);
      }
      return ret;
   }
}
