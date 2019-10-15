 package cn.com.easyerp.core.view.form.index;
 
 import java.util.List;

import cn.com.easyerp.core.widget.WidgetModelBase;
 
 
 
 
 
 
 
 public class MatterDeskTopModel
   extends WidgetModelBase
 {
   private List<OpRecordModel> matterDesk;
   private int Start_col;
   private int start_row;
   private int height;
   private int width;
   private String background_color;
   
   public List<OpRecordModel> getMatterDesk() { return this.matterDesk; }
 
 
   
   public void setMatterDesk(List<OpRecordModel> matterDesk) { this.matterDesk = matterDesk; }
 
 
   
   public int getStart_col() { return this.Start_col; }
 
 
   
   public void setStart_col(int start_col) { this.Start_col = start_col; }
 
 
   
   public int getStart_row() { return this.start_row; }
 
 
   
   public void setStart_row(int start_row) { this.start_row = start_row; }
 
 
   
   public int getHeight() { return this.height; }
 
 
   
   public void setHeight(int height) { this.height = height; }
 
 
   
   public int getWidth() { return this.width; }
 
 
   
   public void setWidth(int width) { this.width = width; }
 
 
   
   public String getBackground_color() { return this.background_color; }
 
 
   
   public void setBackground_color(String background_color) { this.background_color = background_color; }
 }


