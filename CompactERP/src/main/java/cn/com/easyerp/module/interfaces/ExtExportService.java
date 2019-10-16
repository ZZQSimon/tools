package cn.com.easyerp.module.interfaces;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.jdbc.SqlRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.module.interfaces.export.Data;
import cn.com.easyerp.module.interfaces.export.DataSource;
import cn.com.easyerp.module.interfaces.export.Entry;
import cn.com.easyerp.module.interfaces.export.ExportUtility;
import cn.com.easyerp.module.interfaces.export.Head;
import cn.com.easyerp.storage.StorageService;

@Service
public class ExtExportService {
    public void initModel(ExportsModel model) {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());
        Map<String, List<Map<String, Object>>> exportType = getExportTypeList(runner);
        if (exportType == null) {
            exportType = new HashMap<String, List<Map<String, Object>>>();
        }
        model.setTypeList(exportType);
        session.close();
    }

    @Autowired
    SqlSessionFactory sqlSessionFactory;
    @Autowired
    StorageService storageService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map<String, List<Map<String, Object>>> getExportTypeList(SqlRunner runner) {
        Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
        try {
            String dynamicSql = "select key2, cn from sys_international where type=3 and key1='U_DataType'";
            List<Map<String, Object>> type = runner.selectAll(dynamicSql, new Object[0]);
            dynamicSql = "select ID as code,DocName as name from U_DATA_MAPPING_H where DataTypeCode=?";
            List<String> params = new ArrayList<String>();
            for (int i = 0; i < type.size(); i++) {
                Map<String, Object> typeRecord = (Map) type.get(i);
                params.clear();
                params.add((String) typeRecord.get("KEY2"));
                result.put((String) typeRecord.get("CN"), runner.selectAll(dynamicSql, params.toArray()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Map<String, Object>> queryHead(String type, String start, String end, String status) {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());
        String dynamicSql = "select * from U_DATA_MAPPING_H where ID=?";
        List<Object> params = new ArrayList<Object>();
        params.add(type);
        List<Map<String, Object>> result = null;
        System.out.println("+++++" + dynamicSql);
        try {
            result = runner.selectAll(dynamicSql, params.toArray());
        } catch (SQLException e) {
            e.printStackTrace();
            result = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", e.getMessage());
            result.add(tmp);
            session.close();
            return result;
        }
        if (result.size() == 0) {
            result = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", "鏁版嵁搴撴湭鎵惧埌璇ョ被鍨嬶紒");
            result.add(tmp);
            session.close();
            return result;
        }
        String headTable = (String) ((Map) result.get(0)).get("IFHEADTABLE");
        dynamicSql = "select name  from dbo.syscolumns where id=OBJECT_ID('" + headTable + "')order by colorder";

        List<Map<String, Object>> result2 = null;
        System.out.println("+++++" + dynamicSql);
        try {
            result2 = runner.selectAll(dynamicSql, new Object[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            result2 = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", e.getMessage());
            result2.add(tmp);
            session.close();
            return result2;
        }
        if (result2.size() == 0) {
            result2 = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", "鏁版嵁搴撴湭鎵惧埌璇ョ被鍨嬶紒");
            result2.add(tmp);
            session.close();
            return result2;
        }
        if ("-1".equals(status)) {
            dynamicSql = "select * from " + headTable + " where date>=? and date<=?";
            params.clear();
            params.add(start);
            params.add(end);
        } else {
            dynamicSql = "select * from " + headTable + " where date>=? and date<=? and status =?";
            params.clear();
            params.add(start);
            params.add(end);
            params.add(status);
        }

        result = null;
        try {
            System.out.println("+++++" + dynamicSql);
            result = runner.selectAll(dynamicSql, params.toArray());
        } catch (SQLException e) {
            e.printStackTrace();
            result = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", e.getMessage());
            result.add(tmp);
            session.close();
            return result;
        }
        if (result.size() == 0) {
            result = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鏈壘鍒版偍鎵�闇�鐨勪俊鎭�", "鏈壘鍒颁换浣曠鍚堟潯浠剁殑琛ㄥご淇℃伅锛�");
            result.add(tmp);
            session.close();
            return result;
        }
        List<LinkedHashMap<String, Object>> ttt = new ArrayList<LinkedHashMap<String, Object>>(result.size());
        for (int j = 0; j < result.size(); j++) {
            LinkedHashMap<String, Object> tmp = new LinkedHashMap<String, Object>();
            for (int i = 0; i < result2.size(); i++) {
                tmp.put(((Map) result2.get(i)).get("NAME").toString(),
                        ((Map) result.get(j)).get(((Map) result2.get(i)).get("NAME").toString().toUpperCase()));
            }
            ttt.add(tmp);
        }

        List<Map<String, Object>> returnt = new ArrayList<Map<String, Object>>(result.size());
        for (Map map : ttt) {
            returnt.add(new LinkedHashMap(map));
        }
        session.close();
        return returnt;
    }

    public List<Map<String, Object>> queryTitleName() {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());
        String dynamicSql = "select * from sys_international where type = '2' and key1  = 'U_Title_Name' ";
        List<Map<String, Object>> result = null;
        System.out.println("+++++" + dynamicSql);
        try {
            result = runner.selectAll(dynamicSql, new Object[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            result = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", e.getMessage());
            result.add(tmp);
            session.close();
            return result;
        }
        if (result.size() == 0) {
            result = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", "鏁版嵁搴撴湭鎵惧埌璇ョ被鍨嬶紒");
            result.add(tmp);
            session.close();
            return result;
        }
        session.close();
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Map<String, Object>> queryBody(String type, String keyId) {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());
        String dynamicSql = "select * from U_DATA_MAPPING_H where ID=?";
        List<Object> params = new ArrayList<Object>();
        params.add(type);
        List<Map<String, Object>> result = null;
        try {
            result = runner.selectAll(dynamicSql, params.toArray());
        } catch (SQLException e) {
            e.printStackTrace();
            result = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", e.getMessage());
            result.add(tmp);
            session.close();
            return result;
        }
        if (result.size() == 0) {
            result = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", "鏁版嵁搴撴湭鎵惧埌璇ョ被鍨嬶紒");
            result.add(tmp);
            session.close();
            return result;
        }
        String bodyTable = (String) ((Map) result.get(0)).get("IFBODYTABLE");
        dynamicSql = "select name  from dbo.syscolumns where id=OBJECT_ID('" + bodyTable + "')order by colorder";
        List<Map<String, Object>> result2 = null;
        System.out.println("+++++" + dynamicSql);
        try {
            result2 = runner.selectAll(dynamicSql, new Object[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            result2 = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", e.getMessage());
            result2.add(tmp);
            session.close();
            return result2;
        }
        if (result2.size() == 0) {
            result2 = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", "鏁版嵁搴撴湭鎵惧埌璇ョ被鍨嬶紒");
            result2.add(tmp);
            session.close();
            return result2;
        }
        dynamicSql = "select * from " + bodyTable + " where KeyID= ? order by keyid";
        params.clear();
        params.add(keyId);
        result = null;
        try {
            result = runner.selectAll(dynamicSql, params.toArray());
        } catch (SQLException e) {
            e.printStackTrace();
            result = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", e.getMessage());
            result.add(tmp);
            session.close();
            return result;
        }
        if (result.size() == 0) {
            result = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鏈壘鍒版偍鎵�闇�鐨勪俊鎭�", "鏈壘鍒颁换浣曠鍚堟潯浠剁殑琛ㄤ綋淇℃伅锛�");
            result.add(tmp);
            session.close();
            return result;
        }
        List<LinkedHashMap<String, Object>> ttt = new ArrayList<LinkedHashMap<String, Object>>(result.size());
        for (int j = 0; j < result.size(); j++) {
            LinkedHashMap<String, Object> tmp = new LinkedHashMap<String, Object>();
            for (int i = 0; i < result2.size(); i++) {
                tmp.put(((Map) result2.get(i)).get("NAME").toString(),
                        ((Map) result.get(j)).get(((Map) result2.get(i)).get("NAME").toString().toUpperCase()));
            }
            ttt.add(tmp);
        }
        List<Map<String, Object>> returnt = new ArrayList<Map<String, Object>>(result.size());
        for (Map map : ttt) {
            returnt.add(new LinkedHashMap(map));
        }
        session.close();
        return returnt;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ActionResult exportData(String typeCode, String start, String end, List<String> keyid) {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());
        String dynamicSql = "select * from U_DATA_MAPPING_H where ID=?";
        List<Map<String, Object>> result = null;
        List<Object> params = new ArrayList<Object>();
        params.add(typeCode);
        String xmlTemplate = null;
        String headTable = null;
        String bodyTable = null;
        try {
            result = runner.selectAll(dynamicSql, params.toArray());
            if (result.size() == 0) {
                return new ActionResult(false, "鏁版嵁搴撲腑鏈壘鍒拌绫诲埆锛�");
            }
            Map<String, Object> map = (Map) result.get(0);
            xmlTemplate = (String) map.get("XMLFILENAME");
            headTable = (String) map.get("IFHEADTABLE");
            bodyTable = (String) map.get("IFBODYTABLE");
        } catch (SQLException e) {
            e.printStackTrace();
            session.close();
            return new ActionResult(false, e.getMessage());
        }
        result = null;
        params.clear();
        params.add(Integer.valueOf(3));
        params.add("U_XmlPath");
        try {
            result = runner.selectAll("select * from sys_international where type=? and key1=?", params.toArray());
        } catch (SQLException e) {
            e.printStackTrace();
            session.close();
            return new ActionResult(false, e.getMessage());
        }
        if (result.size() == 0) {
            session.close();
            return new ActionResult(false, "鏁版嵁搴撲腑鏈壘鍒版ā鏉胯矾寰勶紒");
        }
        String templatePath = (String) ((Map) result.get(0)).get("CN");
        File template = new File(templatePath + xmlTemplate);
        if (!template.exists()) {
            return new ActionResult(false, "妯℃澘鏂囦欢鏈壘鍒帮紒");
        }
        List<Map<String, Object>> headList = new ArrayList<Map<String, Object>>();
        params.clear();
        params.add(start);
        params.add(end);
        String param = "";
        for (int i = 0; i < keyid.size(); i++) {
            param = param + "'" + (String) keyid.get(i) + "',";
        }
        param = param.substring(0, param.length() - 1);
        System.out.println("++++++" + param);
        try {
            headList = runner.selectAll("select * from " + headTable
                    + " where date>=? and date<=? and status = '0' and keyid in ( " + param + ")  order by keyid",
                    params.toArray());
            if (result.size() == 0) {
                session.close();
                return new ActionResult(false, "鏁版嵁搴撲腑鏈壘浠讳綍绗﹀悎鏉′欢鐨勮〃澶达紒");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.close();
            return new ActionResult(false, e.getMessage());
        }

        List<Map<String, Object>> bodyList = new ArrayList<Map<String, Object>>();
        try {
            bodyList = runner.selectAll("select distinct * from " + bodyTable + ", (select keyid, date from "
                    + headTable + " where status ='0' ) as head where head.keyid= " + bodyTable
                    + ".KeyID and head.date>=? and head.date<=?  and head.keyid in ( " + param
                    + ")  order by head.keyid", params.toArray());
            if (result.size() == 0) {
                session.close();
                return new ActionResult(false, "数据库中未找任何符合条件的表体！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.close();
            return new ActionResult(false, "数据库发生SQL错误！");
        }
        DataSource ds = new DataSource();
        int progress = 0;
        for (int i = 0; i < headList.size(); i++) {
            Data data = new Data();

            Head head = new Head();
            Map<String, Object> headTemp = (Map) headList.get(i);
            Set<Map.Entry<String, Object>> headEntry = headTemp.entrySet();
            Iterator<Map.Entry<String, Object>> headIt = headEntry.iterator();
            while (headIt.hasNext()) {
                Map.Entry<String, Object> temp = (Map.Entry) headIt.next();
                head.addElement(((String) temp.getKey()).toLowerCase(),
                        (temp.getValue() == null) ? "" : temp.getValue().toString());
            }
            data.setHead(head);

            String hk = headTemp.get("KEYID").toString();
            for (int j = progress; j < bodyList.size(); j++) {
                Map<String, Object> bodyTemp = (Map) bodyList.get(j);
                String bk = bodyTemp.get("KEYID").toString();

                if (hk.equals(bk)) {
                    Entry entry = new Entry();
                    Set<Map.Entry<String, Object>> bodyEntry = bodyTemp.entrySet();
                    Iterator<Map.Entry<String, Object>> bodyIt = bodyEntry.iterator();
                    while (bodyIt.hasNext()) {
                        Map.Entry<String, Object> temp = (Map.Entry) bodyIt.next();
                        entry.addElement(((String) temp.getKey()).toLowerCase(),
                                (temp.getValue() == null) ? "" : temp.getValue().toString());
                    }
                    data.addEntry(entry);
                } else {
                    progress = j;
                    break;
                }
            }
            Date curdate = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String curTime = formatter.format(curdate);
            data.addAttr("id", "HAHA");
            ds.addAttr("sender", "DX");
            ds.addAttr("receiver", "u8");
            ds.addAttr("roottag", "oughtpay");
            ds.addAttr("docid", "296165108");
            ds.addAttr("proc", "query");
            ds.addAttr("codeexchanged", "N");
            ds.addAttr("exportneedexch", "N");
            ds.addAttr("paginate", "0");
            ds.addAttr("display", "ds1");
            ds.addAttr("family", "fa1");
            ds.addAttr("dynamicdate", curTime);
            ds.addAttr("timestamp", curTime);
            ds.addAttr("lastquerydate", "fa1");
            ds.addData(data);
        }

        ExportUtility eu = new ExportUtility();
        String xmlContent = eu.runTemplate(ds, templatePath, xmlTemplate);
        if (xmlContent.equals("FileNotFound")) {
            return new ActionResult(false, "没有找到模板！");
        }

        InputStream is = new ByteArrayInputStream(xmlContent.getBytes());

        ActionResult actionResult = this.storageService.createDownload(
                xmlTemplate.substring(0, xmlTemplate.lastIndexOf(".vm")) + ".xml", "", is,
                xmlContent.getBytes().length);
        params.clear();
        params.add(start);
        params.add(end);
        try {
            dynamicSql = "update " + headTable
                    + " set status = '1' where date>=? and date<=? and status = '0' and keyid in ( " + param + ")  ";
            System.out.println("+++++" + dynamicSql);
            runner.update(dynamicSql, params.toArray());
        } catch (SQLException e) {
            e.printStackTrace();
            session.close();
            return new ActionResult(false, e.getMessage());
        }
        session.close();
        return actionResult;
    }

    @SuppressWarnings({  "rawtypes" })
    public ActionResult cancelStatus(String type, List<String> cancelList) {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());
        try {
            String dynamicSql = "select * from U_DATA_MAPPING_H where ID=?";
            List<Object> params = new ArrayList<Object>();
            params.add(type);
            List<Map<String, Object>> result = null;
            try {
                result = runner.selectAll(dynamicSql, params.toArray());
            } catch (SQLException e) {
                e.printStackTrace();
                session.close();
                return new ActionResult(false, "数据类型未找到！");
            }
            if (result.size() == 0) {
                session.close();
                return new ActionResult(false, "数据类型未找到！");
            }
            String headTable = (String) ((Map) result.get(0)).get("IFHEADTABLE");
            String param = "";
            for (int i = 0; i < cancelList.size(); i++) {
                param = param + "'" + (String) cancelList.get(i) + "',";
            }
            param = param.substring(0, param.length() - 1);
            dynamicSql = "UPDATE " + headTable + " SET STATUS ='0' WHERE STATUS = '1' AND KEYID IN (" + param + ") ";
            System.out.println("++++++" + dynamicSql);
            runner.update(dynamicSql, new Object[0]);
            return new ActionResult(true, "导出状态撤销成功！");
        } catch (SQLException e) {
            e.printStackTrace();
            session.rollback();
            return new ActionResult(false, "数据库发生SQL错误！更新失败");
        } finally {
            session.close();
        }
    }
}