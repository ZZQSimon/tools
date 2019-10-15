 package cn.com.easyerp.core.module;
 
 import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import cn.com.easyerp.framework.common.Common;
 
 
 
 
 
 
 
 public class ModuleDescribe
 {
   private String name;
   private Set<String> js;
   private Set<String> css;
   
   public ModuleDescribe(Module module) {
     this.name = module.value();
     String[] array = Common.split(module.js(), "[, ]");
     if (array.length == 0)
       array = new String[] { "/js/dx-" + this.name + ".js" }; 
     this.js = new LinkedHashSet();
     Collections.addAll(this.js, array);
     
     array = Common.split(module.css(), "[, ]");
     this.css = new LinkedHashSet();
     Collections.addAll(this.css, array);
   }
 
 
   
   public String getName() { return this.name; }
 
 
 
   
   public Set<String> getJs() { return this.js; }
 
 
 
   
   public Set<String> getCss() { return this.css; }
 }


