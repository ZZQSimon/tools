 package cn.com.easyerp.core.widget.grid.dt;

public class DataTablePagingColumnModel
 {
   private String data;
   private String name;
   private boolean searchable;
   private boolean orderable;
   private DataTablePagingSearchModel search;
   
   public String getData() { return this.data; }
 
 
 
   
   public void setData(String data) { this.data = data; }
 
 
 
   
   public String getName() { return this.name; }
 
 
 
   
   public void setName(String name) { this.name = name; }
 
 
 
   
   public boolean isSearchable() { return this.searchable; }
 
 
 
   
   public void setSearchable(boolean searchable) { this.searchable = searchable; }
 
 
 
   
   public boolean isOrderable() { return this.orderable; }
 
 
 
   
   public void setOrderable(boolean orderable) { this.orderable = orderable; }
 
 
 
   
   public DataTablePagingSearchModel getSearch() { return this.search; }
 
 
 
   
   public void setSearch(DataTablePagingSearchModel search) { this.search = search; }
 }


