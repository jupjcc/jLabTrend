package utilsPkg;

public class UserInputFileSpec {
   public String prompt;
   public String folder;
   public String fileName = "";
   public String []fileNames = null;
   
   public UserInputFileSpec(String prmpt, String flder, String fn) {
      prompt = prmpt;
      folder = flder;
      fileName = fn;
   }
}
