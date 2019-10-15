 package cn.com.easyerp.module.importer;
 
 import java.util.HashSet;
import java.util.Set;
 
 
 
 
 
 
 
 
 
 public class CustomImportData
 {
   private Set<Integer> errors;
   
   public Set<Integer> getErrors() { return this.errors; }
 
 
   
   public CustomImportData addError(int index) {
     if (this.errors == null)
       this.errors = new HashSet(); 
     this.errors.add(Integer.valueOf(index));
     return this;
   }
 }


