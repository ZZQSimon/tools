 package cn.com.easyerp.core.view.form.index;

public class DesktopListModel
   extends DesktopItem
 {
   private String id;
   private String view_id;
   private String sql;
   private String url;
   private String name;
   
   public String getId() { return this.id; }
 
 
 
   
   public void setId(String id) { this.id = id; }
 
 
 
   
   public String getView_id() { return this.view_id; }
 
 
 
   
   public void setView_id(String view_id) { this.view_id = view_id; }
 
 
 
   
   public String getSql() { return this.sql; }
 
 
 
   
   public void setSql(String sql) { this.sql = sql; }
 
 
 
   
   public String getUrl() { return this.url; }
 
 
 
   
   public void setUrl(String url) { this.url = url; }
 
 
 
   
   public void extend(DesktopItemModel d) {
     super.extend(d);
     this.name = d.getName();
   }
 
 
   
   public String getName() { return this.name; }
 
 
 
   
   public void setName(String name) { this.name = name; }
 }


