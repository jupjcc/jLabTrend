package importPkg;

public class ImportItem implements Comparable<ImportItem> {
   String name;
   double value;
   boolean isGroupHeader = false;
   boolean isSelected = false;
   
   public ImportItem(String n, double v, boolean hdr) {
      name = n;
      value = v;
      isGroupHeader = hdr;
   }
   
   public ImportItem(String n, double v) {
      name = n;
      value = v;
   }
   
   // performing sort in decreasing values
   public int compareTo(ImportItem item1) {
      if (value == item1.value) {
         return 0;
      } else if (value > item1.value) {
         return -1;
      } else {
         return 1;
      }
   }
}
