 package cn.com.easyerp.module.tree2;
 
 import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.module.Module;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.view.TableFormRequestModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.module.dao.Tree2Dao;
 
 
 
 @Controller
 @Module("tree2")
 @RequestMapping({"/tree2"})
 public class Tree2Controller
   extends FormViewControllerBase
 {
   List<Map<String, String>> TreeNode;
   List<Map<String, Object>> Tree123 = new ArrayList<>();
   Map<String, String> mapTest = new HashMap<>();
   @Autowired
   private DataService dataService;
   @Autowired
   private ViewService viewService;
   @Autowired
   private Tree2Dao tree2Dao;
   private float totalProD;
   private int stepAll = 0;
   
   @RequestMapping({"/tree.view"})
   public ModelAndView view(@RequestBody TableFormRequestModel data) {
     String tableName = "v_lot_bom_search";
     
     List<FieldModelBase> fields = this.dataService.buildEmptyDataRecord(tableName, null);
     List<FieldModelBase> fieldsNew = new ArrayList<FieldModelBase>();
     List<FieldModelBase> fieldsCondition = new ArrayList<FieldModelBase>();
     fieldsNew.add(fields.get(1));
     fieldsNew.add(fields.get(2));
     fieldsNew.add(fields.get(3));
     Tree2FormModel form = new Tree2FormModel(data.getParent(), tableName, fieldsNew, fieldsCondition);
     return buildModelAndView(form);
   }
 
 
   
   @Transactional
   @ResponseBody
   @RequestMapping({"/searchUpTreeNode.do"})
   public ActionResult searchUpTreeData(@RequestBody Tree2RequestModel params) {
     String item_id = params.getItem_no();
     String process_no = params.getProcess_no();
     String lot_no = params.getLot_no();
     this.TreeNode = new ArrayList<>();
     Map<String, Object> returnData = new HashMap<String, Object>();
     String item_nm = this.tree2Dao.itemNameSelect(item_id);
     this.stepAll = 0;
     getTreeDataUp(item_id, item_nm, process_no, lot_no, "", "", "", "", "Up", null);
     returnData.put("TreeNode", this.TreeNode);
     String successMsg = this.dataService.getMessageText("DCS-001", new Object[0]);
     returnData.put("msg", successMsg);
     return new ActionResult(true, returnData);
   }
 
   
   @Transactional
   @ResponseBody
   @RequestMapping({"/searchDownTreeNode.do"})
   public ActionResult searchDownTreeData(@RequestBody Tree2RequestModel params) {
     String item_id = params.getItem_no();
     String process_no = params.getProcess_no();
     String lot_no = params.getLot_no();
     this.TreeNode = new ArrayList<>();
     Map<String, Object> returnData = new HashMap<String, Object>();
     String item_nm = this.tree2Dao.itemNameSelect(item_id);
     this.stepAll = 0;
     getTreeDataUp(item_id, item_nm, process_no, lot_no, "", "", "", "", "Down", null);
     returnData.put("TreeNode", this.TreeNode);
     String successMsg = this.dataService.getMessageText("DCS-001", new Object[0]);
     returnData.put("msg", successMsg);
     return new ActionResult(true, returnData);
   }
 
 
 
 
   
   public void getTreeDataUp(String item_id, String item_nm, String process_no, String lot_no, String p_item_id, String p_process_no, String p_lot_no, String parentId, String flag, String product_Detail_Id) {
     String processName = this.dataService.getColumnLabel("v_lot_bom_search", "process_no");
     String lotName = this.dataService.getColumnLabel("v_lot_bom_search", "lot_no");
     String okQuantity = this.dataService.getColumnLabel("t_check_result", "ok_quantity");
     String ngQuantity = this.dataService.getColumnLabel("t_check_result", "ng_quantity");
     String keepQuantity = this.dataService.getColumnLabel("t_check_result", "keep_quantity");
     String adjustQuantity = this.dataService.getColumnLabel("t_stock_adjust", "adjust_quantity");
     String exportQuantity = this.dataService.getColumnLabel("t_transfer_account", "export_quantity");
     String importQuantityAfter = this.dataService.getColumnLabel("t_transfer_account", "import_quantity_after");
     Map<String, String> map = new HashMap<String, String>();
     
     if (parentId.equals("") || parentId == null) {
       map.put("id", item_id + process_no + lot_no + "All");
       map.put("parent", "#");
       map.put("text", item_nm + "(" + item_id + ") , " + processName + "(" + process_no + ")");
       this.TreeNode.add(map);
     } 
 
     
     String lotId = item_id + process_no + lot_no + this.stepAll;
     map = new HashMap<String, String>();
     map.put("id", lotId);
     if (parentId.equals("") || parentId == null) {
       map.put("parent", item_id + process_no + lot_no + "All");
     } else {
       map.put("parent", parentId);
     } 
     
     if (parentId.equals("") || parentId == null) {
       map.put("text", lotName + "(" + lot_no + ")");
     }
     else if (product_Detail_Id == null) {
       map.put("text", lotName + "(" + lot_no + ") 使用数(" + this.tree2Dao.userQuantityLotUpSelect(item_id, process_no, lot_no) + ")");
     } else {
       map.put("text", lotName + "(" + lot_no + ") 使用数(" + this.tree2Dao.userQuantityLotDownSelect(item_id, process_no, lot_no, product_Detail_Id) + ")");
     } 
     
     this.TreeNode.add(map);
 
 
 
     
     List<Map<String, Object>> rList = this.tree2Dao.checkResultSelect(item_id, process_no, lot_no);
     if (rList.size() > 0) {
       float sumOk = 0.0F;
       float sumNg = 0.0F;
       float sumKeep = 0.0F;
       for (Map<String, Object> rMap : rList) {
 
         
         String titleChNode = rMap.get("check_date").toString() + " " + okQuantity + "(" + rMap.get("ok_quantity").toString() + ") 銆�" + ngQuantity + "(" + rMap.get("ng_quantity").toString() + ") 銆�" + keepQuantity + "(" + rMap.get("keep_quantity").toString() + ")";
         sumOk += Float.parseFloat(rMap.get("ok_quantity").toString());
         sumNg += Float.parseFloat(rMap.get("ng_quantity").toString());
         sumKeep += Float.parseFloat(rMap.get("keep_quantity").toString());
         map = new HashMap<String, String>();
         map.put("id", rMap.get("id").toString() + rMap.get("check_date").toString() + this.stepAll);
         map.put("parent", "0" + this.stepAll);
         map.put("text", titleChNode);
         this.TreeNode.add(map);
       } 
       map = new HashMap<String, String>();
       String textNode = this.dataService.getMessageText("检验", new Object[0]) + " " + okQuantity + "(" + sumOk + ") 銆�" + ngQuantity + "(" + sumNg + ") 銆�" + keepQuantity + "(" + sumKeep + ")";
       
       map.put("id", "0" + this.stepAll);
       map.put("parent", lotId);
       map.put("text", textNode);
       this.TreeNode.add(map);
     } 
 
 
 
     
     List<Map<String, Object>> rAndPList = this.tree2Dao.receiveAndProSelect(item_id, process_no, lot_no);
     if (rAndPList.size() > 0) {
       float sumOk = 0.0F;
       float sumNg = 0.0F;
       float sumKeep = 0.0F;
       for (Map<String, Object> rPMap : rAndPList) {
 
         
         String titleChNode = rPMap.get("transReceive_date").toString() + " " + okQuantity + "(" + rPMap.get("ok_quantity").toString() + ") 銆�" + ngQuantity + "(" + rPMap.get("ng_quantity").toString() + ") 銆�" + keepQuantity + "(" + rPMap.get("keep_quantity").toString() + ")";
         sumOk += Float.parseFloat(rPMap.get("ok_quantity").toString());
         sumNg += Float.parseFloat(rPMap.get("ng_quantity").toString());
         sumKeep += Float.parseFloat(rPMap.get("keep_quantity").toString());
         map = new HashMap<String, String>();
         map.put("id", rPMap.get("id").toString() + rPMap.get("transReceive_date").toString() + this.stepAll);
         map.put("parent", "1" + this.stepAll);
         map.put("text", titleChNode);
         this.TreeNode.add(map);
       } 
       map = new HashMap<String, String>();
       String textNode = this.dataService.getMessageText("接受/生产实绩", new Object[0]) + " " + okQuantity + "(" + sumOk + ") 銆�" + ngQuantity + "(" + sumNg + ") 銆�" + keepQuantity + "(" + sumKeep + ")";
       
       map.put("id", "1" + this.stepAll);
       map.put("parent", lotId);
       map.put("text", textNode);
       this.TreeNode.add(map);
     } 
 
 
 
     
     List<Map<String, Object>> pDList = new ArrayList<Map<String, Object>>();
     List<Map<String, Object>> pDListLotNo = new ArrayList<Map<String, Object>>();
     if (flag.equals("Down")) {
       pDListLotNo = this.tree2Dao.productionDetailLotNoDownSelect(item_id, process_no, lot_no);
       pDList = this.tree2Dao.productionDetailDownSelect(item_id, process_no, lot_no);
     } else {
       pDListLotNo = this.tree2Dao.productionDetailLotNoUpSelect(item_id, process_no, lot_no);
       pDList = this.tree2Dao.productionDetailUpSelect(item_id, process_no, lot_no);
     } 
     if (pDList.size() > 0) {
       map = new HashMap<String, String>();
       map.put("id", "2" + this.stepAll);
       map.put("parent", lotId);
       String titleNode = "";
       titleNode = this.dataService.getMessageText("消费", new Object[0]);
       map.put("text", titleNode);
       this.TreeNode.add(map);
       String allLotNo = "";
       for (Map<String, Object> pDMap : pDList) {
         for (int i = 0; i < pDListLotNo.size(); i++) {
           if (pDMap.get("item_id").toString().equals(((Map)pDListLotNo.get(i)).get("item_id").toString())) {
             allLotNo = allLotNo + ((Map)pDListLotNo.get(i)).get("lot_no").toString();
           }
         } 
         String tcn = countProductionDetail(pDMap.get("item_id").toString(), pDMap.get("process_no").toString(), item_id, process_no, lot_no, flag);
         
         String titleChNode = pDMap.get("item_nm").toString() + "(" + pDMap.get("item_id").toString() + ") , " + processName + "(" + pDMap.get("process_no").toString() + ")";
         map = new HashMap<String, String>();
         map.put("id", pDMap.get("item_id").toString() + pDMap.get("process_no").toString() + allLotNo);
         map.put("parent", "2" + this.stepAll);
         map.put("text", titleChNode + " " + this.dataService.getMessageText("使用数合计", new Object[0]) + " (" + tcn + ")");
         this.TreeNode.add(map);
         allLotNo = "";
       } 
     } 
 
 
 
 
 
     
     List<Map<String, Object>> fkList = this.tree2Dao.fromKeepSelect(item_id, process_no, lot_no);
     List<Map<String, Object>> tkList = this.tree2Dao.toKeepSelect(item_id, process_no, lot_no);
     if (fkList.size() > 0 || tkList.size() > 0) {
       float sumOk = 0.0F;
       float sumNg = 0.0F;
       float sumKeep = 0.0F;
       for (Map<String, Object> fkMap : fkList) {
 
         
         String titleChNode = fkMap.get("judge_date").toString() + " " + okQuantity + "(-" + fkMap.get("ok_quantity").toString() + ") 銆�" + ngQuantity + "(" + fkMap.get("ng_quantity").toString() + ") 銆�" + keepQuantity + "(" + fkMap.get("keep_quantity").toString() + ")";
         sumOk += Float.parseFloat(fkMap.get("ok_quantity").toString());
         sumNg += Float.parseFloat(fkMap.get("ng_quantity").toString());
         sumKeep += Float.parseFloat(fkMap.get("keep_quantity").toString());
         map = new HashMap<String, String>();
         map.put("id", fkMap.get("id").toString() + "t_judge_to_keep" + fkMap.get("judge_date").toString() + this.stepAll);
         map.put("parent", "3" + this.stepAll);
         map.put("text", titleChNode);
         this.TreeNode.add(map);
       } 
       for (Map<String, Object> tkMap : tkList) {
 
         
         String titleChNode = tkMap.get("judge_date").toString() + " " + okQuantity + "(+" + tkMap.get("ok_quantity").toString() + ") 銆�" + ngQuantity + "(" + tkMap.get("ng_quantity").toString() + ") 銆�" + keepQuantity + "(" + tkMap.get("keep_quantity").toString() + ")";
         map = new HashMap<String, String>();
         sumOk += Float.parseFloat(tkMap.get("ok_quantity").toString());
         sumNg += Float.parseFloat(tkMap.get("ng_quantity").toString());
         sumKeep += Float.parseFloat(tkMap.get("keep_quantity").toString());
         map.put("id", tkMap.get("id").toString() + "t_judge_from_keep" + tkMap.get("judge_date").toString() + this.stepAll);
         map.put("parent", "3" + this.stepAll);
         map.put("text", titleChNode);
         this.TreeNode.add(map);
       } 
       String textNode = this.dataService.getMessageText("保留判定", new Object[0]) + " " + okQuantity + "(" + sumOk + ") 銆�" + ngQuantity + "(" + sumNg + ") 銆�" + keepQuantity + "(" + sumKeep + ")";
       
       map = new HashMap<String, String>();
       map.put("id", "3" + this.stepAll);
       map.put("parent", lotId);
       map.put("text", textNode);
       this.TreeNode.add(map);
     } 
 
 
 
     
     List<Map<String, Object>> sList = this.tree2Dao.stockAdjustSelect(item_id, process_no, lot_no);
     if (sList.size() > 0) {
       float sumStock = 0.0F;
       for (Map<String, Object> sMap : sList) {
         String titleChNode = "";
         if (sMap.get("adjust_type").toString().equals("1")) {
           titleChNode = sMap.get("adjust_date").toString() + " " + adjustQuantity + "(+" + sMap.get("adjust_quantity").toString() + ")";
         } else {
           titleChNode = sMap.get("adjust_date").toString() + " " + adjustQuantity + "(-" + sMap.get("adjust_quantity").toString() + ")";
         } 
         sumStock += Float.parseFloat(sMap.get("adjust_quantity").toString());
         map = new HashMap<String, String>();
         map.put("id", sMap.get("id").toString() + "t_stock_adjust");
         map.put("parent", "4" + this.stepAll);
         map.put("text", titleChNode);
         this.TreeNode.add(map);
       } 
       String textNode = this.dataService.getMessageText("库存调整", new Object[0]) + " " + adjustQuantity + "(" + sumStock + ") ";
       map = new HashMap<String, String>();
       map.put("id", "4" + this.stepAll);
       map.put("parent", lotId);
       map.put("text", textNode);
       this.TreeNode.add(map);
     } 
 
 
 
 
 
     
     List<Map<String, Object>> taList = this.tree2Dao.transferAccountSelect(item_id, process_no, lot_no);
     List<Map<String, Object>> taAfterList = this.tree2Dao.transferAccountAfterSelect(item_id, process_no, lot_no);
     if (taList.size() > 0 || taAfterList.size() > 0) {
       float sumEx = 0.0F;
       float sumIm = 0.0F;
       for (Map<String, Object> taMap : taList) {
         String titleChNode = taMap.get("trans_date").toString() + " " + exportQuantity + "(-" + taMap.get("export_quantity").toString() + ")";
         sumEx += Float.parseFloat(taMap.get("export_quantity").toString());
         map = new HashMap<String, String>();
         map.put("id", taMap.get("id").toString());
         map.put("parent", "5" + this.stepAll);
         map.put("text", titleChNode);
         this.TreeNode.add(map);
       } 
       for (Map<String, Object> taAfterMap : taAfterList) {
         
         String titleChNode = taAfterMap.get("trans_date").toString() + " " + importQuantityAfter + "(+" + taAfterMap.get("import_quantity_after").toString() + ")";
         sumIm += Float.parseFloat(taAfterMap.get("import_quantity_after").toString());
         map = new HashMap<String, String>();
         map.put("id", taAfterMap.get("id").toString());
         map.put("parent", "5" + this.stepAll);
         map.put("text", titleChNode);
         this.TreeNode.add(map);
       } 
       String textNode = this.dataService.getMessageText("转账", new Object[0]) + " " + exportQuantity + "(" + sumEx + ") " + " " + importQuantityAfter + "(" + sumIm + ") " + " ";
       
       map = new HashMap<String, String>();
       map.put("id", "5" + this.stepAll);
       map.put("parent", lotId);
       map.put("text", textNode);
       this.TreeNode.add(map);
     } 
 
 
 
     
     String allLotNo2 = "";
     for (Map<String, Object> pDMap : pDListLotNo) {
       this.stepAll++;
       for (int i = 0; i < pDListLotNo.size(); i++) {
         if (pDMap.get("item_id").toString().equals(((Map)pDListLotNo.get(i)).get("item_id").toString())) {
           allLotNo2 = allLotNo2 + ((Map)pDListLotNo.get(i)).get("lot_no").toString();
         }
       } 
       
       parentId = pDMap.get("item_id").toString() + pDMap.get("process_no").toString() + allLotNo2;
       allLotNo2 = "";
       if (flag.equals("Down")) {
         getTreeDataUp(pDMap.get("item_id").toString(), pDMap.get("item_nm").toString(), pDMap.get("process_no").toString(), pDMap
             .get("lot_no").toString(), item_id, process_no, lot_no, parentId, "Down", pDMap.get("product_Detail_Id").toString());
         continue;
       } 
       getTreeDataUp(pDMap.get("item_id").toString(), pDMap.get("item_nm").toString(), pDMap.get("process_no").toString(), pDMap
           .get("lot_no").toString(), item_id, process_no, lot_no, parentId, "Up", null);
     } 
   }
 
 
 
 
 
 
 
 
   
   public String countProductionDetail(String item_id, String process_no, String p_item_id, String p_process_no, String p_lot_no, String flag) {
     this.totalProD = 0.0F;
     float sum = 0.0F;
     if (flag.equals("Down")) {
       if (this.tree2Dao.userQuantityDownSelect(item_id, process_no, p_item_id, p_process_no, p_lot_no) != null) {
         sum = Float.parseFloat(this.tree2Dao.userQuantityDownSelect(item_id, process_no, p_item_id, p_process_no, p_lot_no));
       }
     } else {
       List<Map<String, Object>> lotNoList = new ArrayList<Map<String, Object>>();
       lotNoList = this.tree2Dao.lotNoUpSelect(item_id, process_no, p_item_id, p_process_no, p_lot_no);
       String lotNoString = "";
       for (Map<String, Object> lotNoDMap : lotNoList) {
         lotNoString = lotNoString + lotNoDMap.get("lot_no").toString() + ",";
       }
       if (lotNoString.length() <= 0) {
         sum = 0.0F;
       } else {
         lotNoString = lotNoString.substring(0, lotNoString.length() - 1);
         sum = Float.parseFloat(this.tree2Dao.userQuantityUpSelect(item_id, process_no, lotNoString));
       } 
     } 
     return sum + "";
   }
 }


