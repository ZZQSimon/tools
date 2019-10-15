 package cn.com.easyerp.core.widget.grid.dt;
 
 import java.util.List;
 
 
 
 
 
 public class DataTablePagingParameterModel
 {
   private int draw;
   private int start;
   private int length;
   private DataTablePagingSearchModel search;
   private List<DataTablePagingOrderModel> order;
   private List<DataTablePagingColumnModel> columns;
   
   public int getDraw() { return this.draw; }
 
 
 
   
   public void setDraw(int draw) { this.draw = draw; }
 
 
 
   
   public int getStart() { return this.start; }
 
 
 
   
   public void setStart(int start) { this.start = start; }
 
 
 
   
   public int getLength() { return this.length; }
 
 
 
   
   public void setLength(int length) { this.length = length; }
 
 
 
   
   public DataTablePagingSearchModel getSearch() { return this.search; }
 
 
 
   
   public void setSearch(DataTablePagingSearchModel search) { this.search = search; }
 
 
 
   
   public List<DataTablePagingOrderModel> getOrder() { return this.order; }
 
 
 
   
   public void setOrder(List<DataTablePagingOrderModel> order) { this.order = order; }
 
 
 
   
   public List<DataTablePagingColumnModel> getColumns() { return this.columns; }
 
 
 
   
   public void setColumns(List<DataTablePagingColumnModel> columns) { this.columns = columns; }
 }


