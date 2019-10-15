 package cn.com.easyerp.core.view.form.index;
 
 import java.util.HashMap;
import java.util.Map;
 
 
 
 
 
 
 
 public class DesktopItem
 {
   public static final int xBase = 245;
   public static final int yBase = 145;
   private String type;
   public static final Map<String, String> typeMap = new HashMap<>(); static  {
     typeMap.put("70", "list");
     typeMap.put("60", "matter");
     typeMap.put("50", "shct");
     typeMap.put("40", "chart");
     typeMap.put("30", "relist");
     typeMap.put("20", "work");
     typeMap.put("10", "bull");
   }
   private int x;
   private int y;
   private int w;
   private int h;
   private String bg;
   
   public void extend(DesktopItemModel d) {
     this.x = d.getStart_col() * 255 + 10;
     this.y = d.getStart_row() * 155;
     
     this.h = (d.getEnd_row() - d.getStart_row() + 1) * 145 + delta(d
         .getEnd_row(), d.getStart_row());
     this.w = (d.getEnd_col() - d.getStart_col() + 1) * 245 + delta(d
         .getEnd_col(), d.getStart_col());
     this.bg = d.getBackground_color();
     this.type = (String)typeMap.get(d.getItem_type());
   }
 
   
   private int delta(int end, int start) {
     if (end - start > 0) {
       return (end - start) * 10;
     }
     return 0;
   }
 
 
   
   public String getType() { return this.type; }
 
 
 
   
   public int getX() { return this.x; }
 
 
 
   
   public void setX(int x) { this.x = x; }
 
 
 
   
   public int getY() { return this.y; }
 
 
 
   
   public void setY(int y) { this.y = y; }
 
 
 
   
   public int getW() { return this.w; }
 
 
 
   
   public void setW(int w) { this.w = w; }
 
 
 
   
   public int getH() { return this.h; }
 
 
 
   
   public void setH(int h) { this.h = h; }
 
 
 
   
   public String getBg() { return this.bg; }
 
 
 
   
   public void setBg(String bg) { this.bg = bg; }
 }


