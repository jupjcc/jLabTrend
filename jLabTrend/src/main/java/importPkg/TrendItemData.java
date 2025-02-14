package importPkg;

public class TrendItemData {
	
   public String nameStr;
   public String valueStr;
   public String flagStr;
   public String minNormalStr;
   public String maxNormalStr;
   public String units;
   
   public TrendItemData(String name, String valStr, String flag,
                        String minStr,String maxStr, String units) {
      nameStr = name;
      valueStr = valStr;
      flagStr = flag;
      minNormalStr = minStr;
      maxNormalStr = maxStr;
      this.units = units;
   }
   
   public TrendItemData(String minStr,String maxStr,String valStr, String units) {
      minNormalStr = minStr;
      maxNormalStr = maxStr;
      valueStr = valStr;
      this.units = units;
   }
}
