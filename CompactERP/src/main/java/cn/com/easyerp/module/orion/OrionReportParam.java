 package cn.com.easyerp.module.orion;

public class OrionReportParam
 {
   private boolean labelOnly = false;
   private char leadingChar = ' ';
   private String delimiter = ";";
   private int labelSize = 64;
 
 
   
   public boolean isLabelOnly() { return this.labelOnly; }
 
 
 
   
   public void setLabelOnly(boolean labelOnly) { this.labelOnly = labelOnly; }
 
 
 
   
   public char getLeadingChar() { return this.leadingChar; }
 
 
 
   
   public void setLeadingChar(char leadingChar) { this.leadingChar = leadingChar; }
 
 
 
   
   public String getDelimiter() { return this.delimiter; }
 
 
 
   
   public void setDelimiter(String delimiter) { this.delimiter = delimiter; }
 
 
 
   
   public int getLabelSize() { return this.labelSize; }
 
 
 
   
   public void setLabelSize(int labelSize) { this.labelSize = labelSize; }
 }


