 package cn.com.easyerp.DeployTool.service;
 import java.util.List;
import java.util.Map;
 
 public class DateDelayMap {
   private Map<String, Passage> passageMap;
   
   public Map<String, Passage> getPassageMap() { return this.passageMap; }
   private Map<String, List<PassageRow>> passageRowMap; private Map<String, List<PassageRowFormula>> passageRowFormulaMap;
   
   public void setPassageMap(Map<String, Passage> passageMap) { this.passageMap = passageMap; }
 
   
   public Map<String, List<PassageRow>> getPassageRowMap() { return this.passageRowMap; }
 
   
   public void setPassageRowMap(Map<String, List<PassageRow>> passageRowMap) { this.passageRowMap = passageRowMap; }
 
   
   public Map<String, List<PassageRowFormula>> getPassageRowFormulaMap() { return this.passageRowFormulaMap; }
 
   
   public void setPassageRowFormulaMap(Map<String, List<PassageRowFormula>> passageRowFormulaMap) { this.passageRowFormulaMap = passageRowFormulaMap; }
 }


