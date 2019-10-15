package cn.com.easyerp.core.widget.grid.dt;

public class DataTablePagingOrderModel {
	 public static enum OrderDir {
	   desc, asc;
	 }
	 private int column;
	 private String name;
	 private OrderDir dir = OrderDir.asc;
   
	 public int getColumn() { return this.column; }
   
	 public void setColumn(int column) { this.column = column; }
   
	 public OrderDir getDir() { return this.dir; }
   
	 public String getName() { return this.name; }
   
 	public void setName(String name) { this.name = name; }
   
 	public void setDir(OrderDir dir) { this.dir = dir; }
}