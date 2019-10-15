 package cn.com.easyerp.core.evaluate;
 
 import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.evaluate.DataModel.ChildModel;
import cn.com.easyerp.core.evaluate.DataModel.DataModel;
import cn.com.easyerp.core.evaluate.DataModel.Model;
import cn.com.easyerp.core.view.form.detail.DetailFormModel;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.core.widget.grid.RecordModel;
 
 
 
 @Service
 public class ModelService
 {
   @Autowired
   private CacheModelService cacheModelService;
   
   public void buildModel(DetailFormModel form) {
     Model model = new Model(form.getId(), form.getParent(), form.getAction().name(), form.getTableName());
     model.setFieldMap(form.getFieldMap());
     List<GridModel> children = form.getChildren();
     model.setChildren(buildChildrenModel(children));
     this.cacheModelService.cacheModel(model.getId(), model);
   }
   
   public void buildModel(GridModel grid, List<RecordModel> ret) {
     if (ret != null && ret.size() != 0) {
       for (int i = 0; i < ret.size(); i++) {
         Model model = new Model(grid.getId(), grid.getParent(), "list", grid.getTable());
         model.setFieldMap(((RecordModel)ret.get(i)).getFieldMap());
         this.cacheModelService.cacheModel(grid.getId(), ((RecordModel)ret.get(i)).getId(), model);
       } 
     }
   }
   
   public void clearListModel(String id) { this.cacheModelService.clearListModel(id); }
   
   private Map<String, ChildModel> buildChildrenModel(List<GridModel> children) {
     if (children == null || children.size() == 0)
       return null; 
     Map<String, ChildModel> childrenModels = new HashMap<String, ChildModel>();
     for (int i = 0; i < children.size(); i++) {
       
       ChildModel child = new ChildModel(((GridModel)children.get(i)).getId(), ((GridModel)children.get(i)).getParent(), ((GridModel)children.get(i)).getTable());
       child.setChildrenDatas(buildChildDatas(((GridModel)children.get(i)).getRecords()));
       childrenModels.put(child.getId(), child);
     } 
     return childrenModels;
   }
   private List<DataModel> buildChildDatas(List<RecordModel> datas) {
     if (datas == null || datas.size() == 0)
       return null; 
     List<DataModel> dataList = new ArrayList<DataModel>();
     for (int i = 0; i < datas.size(); i++) {
       DataModel dataModel = new DataModel(((RecordModel)datas.get(i)).getId(), ((RecordModel)datas.get(i)).getParent());
       dataModel.setData(((RecordModel)datas.get(i)).getFieldMap());
       dataModel.setStatus(((RecordModel)datas.get(i)).getStatus().name());
       dataList.add(dataModel);
     } 
     return dataList;
   }
 }


