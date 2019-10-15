 package cn.com.easyerp.core.widget.grid;
 
 import java.util.Map;
 
 
 
 
 
 public class GridInMemoryWithAutoKeyModel
   extends GridModel
 {
   private int origIndex;
   private int keyIndex;
   
   public GridInMemoryWithAutoKeyModel(String tableName, Map<String, Object> parentKey, int keyIndex) {
     super(tableName, parentKey, true);
     this.origIndex = this.keyIndex = keyIndex;
   }
 
 
   
   public int getKeyIndex() { return this.keyIndex; }
 
 
 
   
   public void setKeyIndex(int keyIndex) { this.keyIndex = keyIndex; }
 
 
   
   public void reset() {
     super.reset();
     this.keyIndex = this.origIndex;
   }
 }


