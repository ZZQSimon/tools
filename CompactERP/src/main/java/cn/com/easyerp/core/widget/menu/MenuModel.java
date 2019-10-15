 package cn.com.easyerp.core.widget.menu;
 
 import java.util.List;

import cn.com.easyerp.core.widget.WidgetModelBase;
 
 
 
 
 
 
 
 public class MenuModel
   extends WidgetModelBase
 {
   private String id;
   private String international_id;
   private int type;
   private String icon;
   private String icon_out;
   private String page_id;
   private String parent_id;
   private int is_mobile_menu;
   private int seq;
   private List<MenuModel> sub;
   private String module;
   private String[] listModule;
   private int is_system;
   
   public int getIs_system() { return this.is_system; }
 
 
   
   public void setIs_system(int is_system) { this.is_system = is_system; }
 
 
   
   public int getIs_mobile_menu() { return this.is_mobile_menu; }
 
 
   
   public void setIs_mobile_menu(int is_mobile_menu) { this.is_mobile_menu = is_mobile_menu; }
 
 
   
   public String getId() { return this.id; }
 
 
   
   public void setId(String id) { this.id = id; }
 
 
   
   public String getInternational_id() { return this.international_id; }
 
 
   
   public void setInternational_id(String international_id) { this.international_id = international_id; }
 
 
   
   public String getIcon_out() { return this.icon_out; }
 
 
   
   public void setIcon_out(String icon_out) { this.icon_out = icon_out; }
 
 
   
   public String[] getListModule() { return this.listModule; }
 
 
   
   public String getModule() { return this.module; }
 
   
   public void setModule(String module) {
     this.module = module;
     if (module != null) {
       this.listModule = module.split(",");
     }
   }
 
 
   
   public MenuModel() { super(""); }
 
 
 
 
   
   public String idPrefix() { return "m"; }
 
 
 
   
   public int getType() { return this.type; }
 
 
 
   
   public void setType(int type) { this.type = type; }
 
 
 
   
   public String getIcon() { return this.icon; }
 
 
 
   
   public void setIcon(String icon) { this.icon = icon; }
 
 
 
   
   public String getParent_id() { return this.parent_id; }
 
 
 
   
   public void setParent_id(String parent_id) { this.parent_id = parent_id; }
 
 
 
   
   public int getSeq() { return this.seq; }
 
 
 
   
   public void setSeq(int seq) { this.seq = seq; }
 
 
 
   
   public List<MenuModel> getSub() { return this.sub; }
 
 
   
   public void setSub(List<MenuModel> sub) {
     this.sub = sub;
     for (MenuModel menu : sub) {
       menu.setParent(getId());
     }
   }
 
   
   public String getPage_id() { return this.page_id; }
 
 
 
   
   public void setPage_id(String page_id) { this.page_id = page_id; }
 }


