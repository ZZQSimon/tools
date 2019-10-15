 package cn.com.easyerp.core.mail.adidas;
 
 import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.easyerp.core.dao.AdidasDao;
import cn.com.easyerp.core.dao.ApproveDao;
import cn.com.easyerp.core.mail.SystemDefaultApiService;
import cn.com.easyerp.core.master.DxRoutingDataSource;
 
 
 
 
 
 @Service
 public class OuterAdidasServiceImpl
   implements OuterAdidasService
 {
   @Autowired
   private DxRoutingDataSource dxRoutingDataSource;
   @Autowired
   private SystemDefaultApiService systemDefaultApiService;
   @Autowired
   private AdidasDao adidasDao;
   @Autowired
   private ApproveDao approveDao;
   
   @Transactional(propagation = Propagation.REQUIRES_NEW)
   public List<EntryManage> updateAdidasData(List<String> uuid, List<EntryManage> list, String outerDataBase, String localDataBase) {
     this.adidasDao.deleteDayEmpManage();
     List<EntryManage> message = new ArrayList<EntryManage>();
     List<String> aList = new ArrayList<String>();
     if (list.size() > 0) {
       this.adidasDao.updateEntryManageById(list);
       for (EntryManage manage : list) {
         if (manage.getFlag() == 1) {
           EntryManage m = this.adidasDao.findEntryManageByEntryId(manage.getId());
           if (m != null) {
             aList.add(manage.getId());
             message.add(manage);
           } 
         } 
       } 
       List<String> type = new ArrayList<String>();
       type.add("agree");
       List<String> reason = new ArrayList<String>();
       reason.add("");
       List<String> user = new ArrayList<String>();
       user.add("Newcomer");
       
       List<String> domain = new ArrayList<String>();
       List<String> dataId = new ArrayList<String>();
       List<String> tableId = new ArrayList<String>();
       domain.add(localDataBase);
       tableId.add("t_entry_manage");
       for (String id : aList) {
         
         dataId.add(id);
         System.out.println("diaofangfa==========================");
         this.systemDefaultApiService.automatic_approve(uuid, tableId, dataId, user, reason, domain, type);
       } 
     } 
     return message;
   }
 
 
   
   @Transactional(propagation = Propagation.REQUIRES_NEW)
   public List<EntryManage> updateAdidasDataByZhongZhi(List<String> uuid, List<EntryManage> manageList, String outerDataBase, String localDataBase) {
     List<EntryManage> idList = new ArrayList<EntryManage>();
     try {
       this.adidasDao.updateEntryManageById(manageList);
       List<Object> ids = new ArrayList<Object>();
       for (EntryManage entryManage : manageList) {
         ids.add(entryManage.getId());
         EntryManage m = this.adidasDao.findEntryManageByEntryId(entryManage.getId());
         if (m != null) {
           idList.add(entryManage);
         }
       } 
 
       
       for (EntryManage entryManage : manageList) {
         
         List<String> reason = new ArrayList<String>();
         reason.add("");
         List<String> user = new ArrayList<String>();
         user.add("CIICLOCAL");
         List<String> domain = new ArrayList<String>();
         domain.add(localDataBase);
         List<String> dataId = new ArrayList<String>();
         dataId.add(entryManage.getId());
         List<String> tableId = new ArrayList<String>();
         tableId.add("t_entry_manage");
         List<String> type = new ArrayList<String>();
         if ("1".equals(entryManage.getZhongzhi_flag())) {
           type.add("agree");
           
           this.systemDefaultApiService.automatic_approve(uuid, tableId, dataId, null, reason, domain, type);
           
           List<Map<String, Object>> approveData = this.approveDao.getApproveDataByTableData((String)tableId.get(0), dataId.get(0));
           for (Map<String, Object> waitdata : approveData) {
             Object nodeUser = waitdata.get("user");
             System.out.println("appuser:" + nodeUser);
             if (nodeUser != null && nodeUser.toString().equals("CIICLOCAL")) {
               System.out.println("appuser_CIICLOCAL: " + nodeUser);
               this.systemDefaultApiService.automatic_approve(uuid, tableId, dataId, user, reason, domain, type); break;
             } 
           } 
           continue;
         } 
         if ("2".equals(entryManage.getZhongzhi_flag())) {
           type.add("termination");
           
           this.systemDefaultApiService.automatic_approve(uuid, tableId, dataId, null, reason, domain, type);
         } 
       } 
     } catch (Exception e) {
       return idList;
     } 
     return idList;
   }
 }


