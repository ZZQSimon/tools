 package cn.com.easyerp.core.mail.adidas;
 
 import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.easyerp.core.cache.CityDescribe;
import cn.com.easyerp.core.dao.AdidasDao;
import cn.com.easyerp.core.mail.SystemDefaultApiService;
import cn.com.easyerp.core.master.DxRoutingDataSource;
 
 
 @Service
 public class AdidasServiceImpl
   implements AdidasService
 {
   @Autowired
   private AdidasDao adidasDao;
   @Autowired
   private DxRoutingDataSource dxRoutingDataSource;
   
   @Transactional(propagation = Propagation.REQUIRES_NEW)
   public List<String> addGcPreOnboarding(EntryManage entryManage, CodeStr codeStr, Object entry_id) {
     this.adidasDao.deleteEntryManageByEntryId(entry_id);
     List<String> messages = new ArrayList<String>();
     if (entryManage == null) {
       messages.add("查询出原库数据为空");
       return messages;
     } 
     
     EntryManage manage = new EntryManage();
     manage.setAccount_category(entryManage.getAccount_category());
     manage.setAge(entryManage.getAge());
     manage.setApprove_status(entryManage.getApprove_status());
     manage.setBank_name(entryManage.getBank_name());
     manage.setBasic_wage(entryManage.getBasic_wage());
     manage.setBirth_date(entryManage.getBirth_date());
     manage.setCard_no(entryManage.getCard_no());
     manage.setChildren_id_number(entryManage.getChildren_id_number());
     manage.setChildren_name(entryManage.getChildren_name());
     manage.setCity(entryManage.getCity());
     manage.setCity_address(entryManage.getCity_address());
     manage.setCity_contacts(entryManage.getCity_contacts());
     manage.setCity_email(entryManage.getCity_email());
     manage.setCity_entrustment(entryManage.getCity_entrustment());
     manage.setCity_phone(entryManage.getCity_phone());
     manage.setContract_begin_day(entryManage.getContract_begin_day());
     manage.setContract_end_day(entryManage.getContract_end_day());
     manage.setContract_term(entryManage.getContract_term());
     manage.setCre_date(entryManage.getCre_date());
     manage.setCre_user(entryManage.getCre_user());
     manage.setDirect_supervisor(entryManage.getDirect_supervisor());
     manage.setDocument_num(entryManage.getDocument_num());
     manage.setDocument_type(entryManage.getDocument_type());
     manage.setEmail(entryManage.getEmail());
     manage.setEmergency_contact(entryManage.getEmergency_contact());
     manage.setEmergency_contact_phone(entryManage.getEmergency_contact_phone());
     manage.setEmployee_company(entryManage.getEmployee_company());
     manage.setEmployee_id(entryManage.getEmployee_id());
     manage.setEmployee_type(entryManage.getEmployee_type());
     manage.setEntry_type(entryManage.getEntry_type());
     manage.setGraduation_time(entryManage.getGraduation_time());
     manage.setHighest_education(entryManage.getHighest_education());
     manage.setHistorical_service_brand(entryManage.getHistorical_service_brand());
     manage.setId(entryManage.getId());
     manage.setLast_employer(entryManage.getLast_employer());
     manage.setMarital_status(entryManage.getMarital_status());
     manage.setMobile(entryManage.getMobile());
     manage.setName(entryManage.getName());
     manage.setNationality(entryManage.getNationality());
     manage.setOwner(entryManage.getOwner());
     manage.setPermanent_residence(entryManage.getPermanent_residence());
     manage.setPosition(entryManage.getPosition());
     manage.setProbation(entryManage.getProbation());
     manage.setRank(entryManage.getRank());
     manage.setResidential_address(entryManage.getResidential_address());
     manage.setSex(entryManage.getSex());
     manage.setShop_id(entryManage.getShop_id());
     manage.setShop_name(entryManage.getShop_name());
     manage.setTakejob_time(entryManage.getTakejob_time());
     manage.setUpd_date(new Date());
     manage.setUpd_user(entryManage.getUpd_user());
     manage.setWork_hour_type(entryManage.getWork_hour_type());
     manage.setBankName(entryManage.getBankName());
     manage.setGraduation_school(entryManage.getGraduation_school());
     manage.setChildren_id_number2(entryManage.getChildren_id_number2());
     manage.setChildren_name2(entryManage.getChildren_name2());
     manage.setRemark(entryManage.getRemark());
     
     this.adidasDao.insertEntryManage(manage);
     System.out.println("添加业务表数据！");
     CodeStr cs = new CodeStr();
     cs.setCre_date(codeStr.getCre_date());
     cs.setStr(codeStr.getStr());
     cs.setType(codeStr.getType());
     cs.setUUID(codeStr.getUUID());
     
     messages.add("添加外库数据成功！");
     return messages;
   }
   @Autowired
   private SystemDefaultApiService systemDefaultApiService; @Autowired
   private OuterAdidasService outerAdidasService;
   
   @Transactional(propagation = Propagation.REQUIRES_NEW)
   public List<String> updateDataByEntryManage(List<String> uuid, List<ManageVO> idList, String outerDataBase, String localDataBase) {
     System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
     List<String> message = new ArrayList<String>();
     
     this.adidasDao.deleteDayEmpManage();
 
 
     
     List<EntryManage> list = this.adidasDao.getDataById();
     this.dxRoutingDataSource.setActiveDomainKey(localDataBase);
     List<EntryManage> result = this.outerAdidasService.updateAdidasData(uuid, list, outerDataBase, localDataBase);
     if (result.size() <= 0) {
       System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
       message.add("更新数据失败！");
       return message;
     } 
     
     this.dxRoutingDataSource.setActiveDomainKey(outerDataBase);
     this.adidasDao.updateManageFlag(result);
     message.add("成功!");
     return message;
   }
 
   
   @Transactional(propagation = Propagation.REQUIRES_NEW)
   public List<String> adidasZhongZhiConfirm(List<String> uuid, List<ManageVO> voList, String outerDataBase, String localDataBase) {
     List<String> messages = new ArrayList<String>();
     
     List<EntryManage> manageList = this.adidasDao.findEntryManageByFlagAndZhongZhiFlag();
     if (manageList == null || manageList.size() == 0) {
       messages.add("外库没有查询出符合的数据。");
       return messages;
     } 
 
 
 
 
 
 
 
 
 
 
 
     
     this.dxRoutingDataSource.setActiveDomainKey(localDataBase);
     List<EntryManage> result = this.outerAdidasService.updateAdidasDataByZhongZhi(uuid, manageList, outerDataBase, localDataBase);
     if (result.size() <= 0) {
       messages.add("更新回adidas库失败！");
       return messages;
     } 
     this.dxRoutingDataSource.setActiveDomainKey(outerDataBase);
     for (EntryManage vo : result) {
       if ("1".equals(vo.getZhongzhi_flag()) || "2".equals(vo.getZhongzhi_flag())) {
         this.adidasDao.delEntryManageByIdAndFlag(vo.getId(), vo.getShop_id());
       }
     } 
 
     
     "true".equals(result);
     
     messages.add("成功");
     return messages;
   }
 
   
   @Transactional(propagation = Propagation.REQUIRES_NEW)
   public List<String> DeleteOuterDataByIdAndName(List<String> ids, List<String> shop_ids) {
     List<String> message = new ArrayList<String>();
     ManageVO vo = new ManageVO();
     vo.setId((String)ids.get(0));
     vo.setShop_id((String)shop_ids.get(0));
     
     try {
       this.adidasDao.delEntryManageById(vo.getId(), vo.getShop_id());
     } catch (Exception e) {
       e.printStackTrace();
       message.add("删除失败");
     } 
     return message;
   }
   
   @Transactional(propagation = Propagation.REQUIRES_NEW)
   public List<String> deleteInsertCity(List<CityDescribe> cityDescribe, String outerDataBase, String localDataBase) {
     List<String> message = new ArrayList<String>();
     
     this.dxRoutingDataSource.setActiveDomainKey(outerDataBase);
     
     this.adidasDao.deleteCity();
     
     this.adidasDao.insertCity(cityDescribe);
     
     this.dxRoutingDataSource.setActiveDomainKey(localDataBase);
     return null;
   }
 }


