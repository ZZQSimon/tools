package cn.com.easyerp.module.interfaces;
 
 import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.framework.enums.ActionType;
 
@Widget("generate")
public class GenerateModel extends FormModelBase {
	private GridModel grid;
	private List<String> typeName;
	private Map<String, List<Map<String, Object>>> typeList;
   
	public GenerateModel(ActionType action, String parent) {
		super(action, parent);
		this.grid = this.grid;
	}
	
	public GridModel getGrid() { return this.grid; }
   
	public Map<String, List<Map<String, Object>>> getTypeList() { return this.typeList; }
   
	public void setTypeList(Map<String, List<Map<String, Object>>> typeList) {
		this.typeList = typeList;
		this.typeName = new ArrayList<>();
		Set<Map.Entry<String, List<Map<String, Object>>>> entries = typeList.entrySet();
		Iterator<Map.Entry<String, List<Map<String, Object>>>> it = entries.iterator();
		while (it.hasNext()) {
			this.typeName.add((it.next()).getKey());
		}
	}
   
	public List<String> getTypeName() {
		return this.typeName;
	}
   
	public String getTitle() { return "生成页面"; }
}