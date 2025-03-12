package configPkg;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import utilsPkg.OpMsgLogger;
//import utilsPkg.UserInputFileSpec;
import utilsPkg.Utils;

public class AppSettings {
   public static SettingsData sData;
   static String settingsFileName;
   
   //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
   
   private static OpMsgLogger st;
//   private ProgSettings instance;
   private String initMsg;
   
   public AppSettings(OpMsgLogger s) {
      st = s;
      sData = new SettingsData();
//      String os = System.getProperty("os.name");
//      DefaultRuntimeFolder = os.toLowerCase().contains("win")?
//              "c:\\InvTrack\\bin" : "/jtrack/runtime/bin";
//      DefaultRuntimeFolder = Paths.BIN_PATH;
      readJsonObjectFromFile();
   }
//   public void SetLogger(OpMsgLogger s) {
//      st = s;
//   }
   public void readJsonObjectFromFile() {
      Gson gson = new GsonBuilder()
         .setPrettyPrinting()
         .create();
      settingsFileName = configPkg.AppPaths.CFG_PATH + "settings.json";
      initMsg = "Reading saved app settings from " + settingsFileName + "\n";
      try {
         Reader jr = Files.newBufferedReader(Paths.get(settingsFileName));
         try
         {
            SettingsData sDataNull = sData; 
            sData = gson.fromJson(jr, SettingsData.class);
            if (sData == null) {
               sData = sDataNull;
            } else {
               String pp = Utils.prettyPrintUsingGson(new Gson().toJson(sData));
               System.out.println(pp);
            }
            
            System.out.println(sData);
         }
         catch (JsonParseException je)
         {
            initMsg += Utils.ExceptionString(je) + 
                     "\nException parsing Settings file:" +
                     "\n" + settingsFileName + "\nUsing defaults\n";
         }
         jr.close();
      }
      catch (Exception je)
      {
         initMsg += Utils.ExceptionString(je) + 
                "\nException reading Settings file:" +
                "\n" + settingsFileName + "\nUsing defaults\n";
      }
      st.LogMsg(initMsg);
   }
   
   public static void WriteJsonObject() {
      Gson gson = new Gson();
      try
      {
         // overwrite file if exists already
         File f = new File(settingsFileName);
         if (f.exists() ) {
            f.delete();
         }
         f.createNewFile();
         Writer writer = Files.newBufferedWriter(Paths.get(settingsFileName));
         gson.toJson(sData, writer);
         writer.close();
      }
      catch (Exception je)
      {
         st.LogMsg(Utils.ExceptionString(je) + 
                  "\nException saving to settings file:" +
                  "\n" + settingsFileName + "\n", OpMsgLogger.LogLevel.WARN);
      }
   }
   
   
}
