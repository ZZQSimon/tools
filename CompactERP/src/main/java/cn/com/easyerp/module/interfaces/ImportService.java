package cn.com.easyerp.module.interfaces;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SqlRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.easyerp.module.interfaces.xmlAnalysis.XMLAnalysis;

@Service
public class ImportService {
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    public List<Map<String, Object>> getImportTypeList() {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());
        String dynamicSql = "select UTypeCode as code,UTypeName as name from U_UFIDA_MASTER_TYPE";
        try {
            return runner.selectAll(dynamicSql, new Object[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional
    public String importXML(String typeCode, InputStream input) throws Exception {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());
        String dynamicSql = "select * from U_UFIDA_MASTER_TYPE where UTypeCode = ?";
        List<Map<String, Object>> result = null;
        List<Object> params = new ArrayList<Object>();
        params.add(typeCode);
        try {
            result = runner.selectAll(dynamicSql, params.toArray());
            if (result.size() == 0) {
                throw new Exception("数据库未维护该类型。");
            }
            Map<String, Object> map = (Map) result.get(0);
            String codeItem = (String) map.get("UCODEITEM");
            String nameItem = (String) map.get("UNAMEITEM");
            XMLAnalysis xa = new XMLAnalysis();
            List<String> fields = new ArrayList<String>();
            fields.add(codeItem);
            fields.add(nameItem);
            List<Map<String, String>> XMLResult = xa.getContent(input, fields);
            dynamicSql = "delete from U_MASTER where UTypeCode = ?";
            runner.delete(dynamicSql, params.toArray());
            dynamicSql = "insert into U_MASTER(UTypeCode,UCode,UName) values(?,?,?)";
            for (int i = 0; i < XMLResult.size(); i++) {
                params.clear();
                params.add(typeCode);
                params.add(((Map) XMLResult.get(i)).get(codeItem));
                params.add(((Map) XMLResult.get(i)).get(nameItem));
                runner.insert(dynamicSql, params.toArray());
            }
            return "更新成功！";
        } catch (SQLException e) {
            e.printStackTrace();
            session.rollback();
            return "数据库发生SQL错误！更新失败！";
        } finally {
            session.close();
        }
    }

    @Transactional
    public String importXML(String typeCode, String data) {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());
        try {
            List<Object> params = new ArrayList<Object>();
            params.add(typeCode);
            String dynamicSql = "delete from U_MASTER where UTypeCode = ?";
            runner.delete(dynamicSql, params.toArray());
            dynamicSql = "insert into U_MASTER(UTypeCode,UCode,UName) values(?,?,?)";
            String[] dataSet = data.split("&&&");
            for (int i = 0; i < dataSet.length; i++) {
                String[] records = dataSet[i].split("@@@");
                params.clear();
                params.add(typeCode);
                params.add(records[0]);
                params.add(records[1]);
                runner.insert(dynamicSql, params.toArray());
            }
            return "更新成功！";
        } catch (SQLException e) {
            e.printStackTrace();
            session.rollback();
            return "数据库发生SQL错误！更新失败！";
        } finally {
            session.close();
        }
    }

    public String displayData(String type) {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());
        String html = "";
        String dynamicSql = "select UName, UCode from U_MASTER  where UTypeCode = ?";
        List<Map<String, Object>> result = null;
        List<Object> params = new ArrayList<Object>();
        params.add(type);
        try {
            result = runner.selectAll(dynamicSql, params.toArray());
            if (result.size() != 0) {
                for (Map<String, Object> tmp : result) {
                    html = html + "<tr><td>" + tmp.get("UCODE") + "</td><td>" + tmp.get("UNAME") + "</td></tr>";
                }
            } else {
                html = "<tr><td colspan='2' style='text-align: center'>没有找到您需要的数据</td></tr>";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.close();
            return null;
        }
        session.close();
        return html;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, Object> readXML(String type, InputStream input) {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());
        String dynamicSql = "select * from U_UFIDA_MASTER_TYPE where UTypeCode = ?";
        List<Map<String, Object>> result = null;
        List<Object> params = new ArrayList<Object>();
        params.add(type);
        try {
            result = runner.selectAll(dynamicSql, params.toArray());
            if (result.size() == 0) {
                return null;
            }
            Map<String, Object> map = (Map) result.get(0);
            String codeItem = (String) map.get("UCODEITEM");
            String nameItem = (String) map.get("UNAMEITEM");
            XMLAnalysis xa = new XMLAnalysis();
            List<String> fields = new ArrayList<String>();
            fields.add(codeItem);
            fields.add(nameItem);
            List<Map<String, String>> XMLResult = xa.getContent(input, fields);
            if (XMLResult == null) {
                return null;
            }
            Map<String, Object> resultMap = new HashMap<String, Object>();
            List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
            for (int i = 0; i < XMLResult.size(); i++) {
                String code = (String) ((Map) XMLResult.get(i)).get(codeItem);
                String name = (String) ((Map) XMLResult.get(i)).get(nameItem);
                if (code == null || name == null) {
                    resultMap.put("status", "invalid");
                    return resultMap;
                }
                Map<String, String> tmp = new HashMap<String, String>();
                tmp.put("code", code);
                tmp.put("name", name);
                resultList.add(tmp);
            }
            resultMap.put("status", "success");
            resultMap.put("data", resultList.toArray());
            return resultMap;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }
}