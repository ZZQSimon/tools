 package cn.com.easyerp.core.module;

public class LogConfig
 {
   private ModuleConfig module;
   ModuleService.LogLevel logLevel;
   
   LogConfig(ModuleConfig module) { this.module = module; }
 
 
   
   public ModuleConfig level(ModuleService.LogLevel l) {
     this.logLevel = l;
     return this.module;
   }
 }


