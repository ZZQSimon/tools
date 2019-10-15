 package cn.com.easyerp.core.module;
 
 import java.util.List;
 
 
 
 
 public class ModuleConfigureFactory
   implements ModuleConfigureAdapter
 {
   private List<String> js;
   private List<String> css;
   private ModuleService.LogLevel logLevel;
   
   public List<String> getJs() { return this.js; }
 
 
 
   
   public void setJs(List<String> js) { this.js = js; }
 
 
 
   
   public List<String> getCss() { return this.css; }
 
 
 
   
   public void setCss(List<String> css) { this.css = css; }
 
 
 
   
   public ModuleService.LogLevel getLogLevel() { return this.logLevel; }
 
 
 
   
   public void setLogLevel(ModuleService.LogLevel logLevel) { this.logLevel = logLevel; }
 
 
 
   
   public void configure(ModuleConfig config) {
     if (this.js != null)
       for (String s : this.js)
         config.js(s);  
     if (this.css != null)
       for (String s : this.css)
         config.css(s);  
     if (this.logLevel != null)
       config.log().level(this.logLevel); 
   }
 }


