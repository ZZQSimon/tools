package cn.com.easyerp.module.interfaces;

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

@Service
public class SettingService {
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    public void initModel(SettingModel model) {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());
        model.setTypeList(getType(runner));
        session.close();
    }

    public List<Map<String, Object>> getType(SqlRunner runner) {
        List<Map<String, Object>> result = null;
        try {
            result = runner.selectAll(
                    "select DXTypeCode, DXTypeName from U_DX_MASTER_TYPE where DXTypeName is not null and DXTypeName !=''",
                    new Object[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    @SuppressWarnings({ "rawtypes" })
    public List<Map<String, Object>> queryList(String type) {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());
        List<Map<String, Object>> typeResult = null;
        String dynamicSql = "select * from U_DX_MASTER_TYPE where DXTypeCode= ?";
        List<Object> params = new ArrayList<Object>();
        params.add(type);
        try {
            typeResult = runner.selectAll(dynamicSql, params.toArray());
        } catch (SQLException e) {
            e.printStackTrace();
            typeResult = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("发生错误", e.getMessage());
            typeResult.add(tmp);
            session.close();
            return typeResult;
        }
        if (typeResult.size() == 0) {
            typeResult = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("发生错误", "数据库未找到该类型！");
            typeResult.add(tmp);
            session.close();
            return typeResult;
        }
        List<Map<String, Object>> dxResult = new ArrayList<Map<String, Object>>();
        dynamicSql = "WITH T1 AS(SELECT * FROM " + (String) ((Map) typeResult.get(0)).get("DXTABLE") + " WHERE 1=1 ";
        if (null == ((Map) typeResult.get(0)).get("DXWHERE") || "".equals(((Map) typeResult.get(0)).get("DXWHERE"))) {
            dynamicSql = dynamicSql + " ";
        } else {
            dynamicSql = dynamicSql + (String) ((Map) typeResult.get(0)).get("DXWHERE");
        }
        dynamicSql = dynamicSql + ")SELECT  T1." + (String) ((Map) typeResult.get(0)).get("DXCODEITEM") + " AS DXCODE, "
                + " REPLACE(T1." + (String) ((Map) typeResult.get(0)).get("DXNAMEITEM") + ",' ','_') AS DXNAME, "
                + " T2.UTypeCode AS UTYPECODE," + " T3.UCode  AS UCODE,REPLACE(T3.UName,' ','_') AS UNAME, "
                + " T2.UACCCODE AS UACCCODE, " + "T4.UNAME AS UACCNAME" + " FROM " + " T1 "
                + " LEFT JOIN U_MASTER_MAPPING T2 ON T1." + (String) ((Map) typeResult.get(0)).get("DXCODEITEM")
                + "=T2.DXCode " + " AND T2.DXTypeCode = '" + (String) ((Map) typeResult.get(0)).get("DXTYPECODE") + "' "
                + " LEFT JOIN U_MASTER T3 ON T2.UCode=T3.UCode AND T3.UTypeCode='"
                + (String) ((Map) typeResult.get(0)).get("UTYPECODE") + "' "
                + "LEFT JOIN U_MASTER T4 ON T4.UCode = T2.UAccCode AND T4.UTypeCode = 'U001' " + " ORDER BY T1."
                + (String) ((Map) typeResult.get(0)).get("DXCODEITEM");
        System.out.println("+++++++" + dynamicSql);
        try {
            dxResult = runner.selectAll(dynamicSql, new Object[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            dxResult = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("发生错误", e.getMessage());
            dxResult.add(tmp);
            session.close();
            return dxResult;
        }
        if (dxResult.size() == 0) {
            dxResult = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("未找到您所需的信息", "未找到任何符合条件的表头信息！");
            dxResult.add(tmp);
            session.close();
            return dxResult;
        }
        session.close();
        return dxResult;
    }

    @SuppressWarnings({ "rawtypes" })
    public List<Map<String, Object>> queryUcode(String type) {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());
        List<Map<String, Object>> typeResult = null;
        String dynamicSql = "select * from U_DX_MASTER_TYPE where DXTypeCode= ?";
        List<Object> params = new ArrayList<Object>();
        params.add(type);
        try {
            typeResult = runner.selectAll(dynamicSql, params.toArray());
        } catch (SQLException e) {
            e.printStackTrace();
            typeResult = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", e.getMessage());
            typeResult.add(tmp);
            session.close();
            return typeResult;
        }
        if (typeResult.size() == 0) {
            typeResult = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", "鏁版嵁搴撴湭鎵惧埌璇ョ被鍨嬶紒");
            typeResult.add(tmp);
            session.close();
            return typeResult;
        }
        List<Map<String, Object>> UResult = new ArrayList<Map<String, Object>>();
        dynamicSql = "SELECT * FROM U_MASTER WHERE UTypeCode='" + (String) ((Map) typeResult.get(0)).get("UTYPECODE")
                + "' ORDER BY UCODE";

        System.out.println("+++++++" + dynamicSql);
        try {
            UResult = runner.selectAll(dynamicSql, new Object[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            UResult = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", e.getMessage());
            UResult.add(tmp);
            session.close();
            return UResult;
        }
        if (UResult.size() == 0) {
            UResult = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鏈壘鍒版偍鎵�闇�鐨勪俊鎭�", "鏈壘鍒颁换浣曠鍚堟潯浠剁殑琛ㄥご淇℃伅锛�");
            UResult.add(tmp);
            session.close();
            return UResult;
        }
        session.close();
        return UResult;
    }

    public List<Map<String, Object>> querySubjectlv1(String searchid, String type) {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());

        List<Map<String, Object>> subjectResult = null;
        StringBuffer dynamicSqlbuf = new StringBuffer();

        dynamicSqlbuf.append("SELECT * FROM U_MASTER WHERE UTypeCode ='U001' AND LEN(UCODE) = 4 ORDER BY UCODE ");

        String dynamicSql = dynamicSqlbuf.toString();
        try {
            System.out.println("+++++++" + dynamicSql);
            subjectResult = runner.selectAll(dynamicSql, new Object[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            subjectResult = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", e.getMessage());
            subjectResult.add(tmp);
            session.close();
            return subjectResult;
        }
        if (subjectResult.size() == 0) {
            subjectResult = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", "鏁版嵁搴撴湭鎵惧埌璇ョ被鍨嬶紒");
            subjectResult.add(tmp);
            session.close();
            return subjectResult;
        }
        session.close();
        return subjectResult;
    }

    public List<Map<String, Object>> querySubject(String searchid, String type) {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());

        List<Map<String, Object>> subjectResult = null;
        StringBuffer dynamicSqlbuf = new StringBuffer();

        dynamicSqlbuf.append("WITH T");
        dynamicSqlbuf.append(" AS (SELECT *, CAST (UCode AS VARBINARY (MAX)) AS px");
        dynamicSqlbuf.append(" FROM U_MASTER a");
        dynamicSqlbuf.append(" WHERE     a.UTypeCode = 'U001'");
        dynamicSqlbuf.append(" AND NOT EXISTS");
        dynamicSqlbuf.append(
                " (SELECT * FROM U_MASTER b  WHERE     b.UTypeCode = 'U001'              AND charindex (b.Ucode, a.UCode) = 1  AND LEN (b.ucode) = LEN (a.Ucode) - 2)");
        dynamicSqlbuf.append(" UNION ALL");
        dynamicSqlbuf.append(" SELECT c.*,");
        dynamicSqlbuf.append(" CAST (d.px + CAST (C.UCode AS VARBINARY) AS VARBINARY (MAX))");
        dynamicSqlbuf.append(" FROM U_MASTER c, T d");
        dynamicSqlbuf.append(" WHERE     c.UTypeCode = 'U001'");
        dynamicSqlbuf.append(" AND charindex (d.Ucode, c.UCode) = 1");
        dynamicSqlbuf.append(" AND LEN (d.ucode) = LEN (c.Ucode) - 2)");
        dynamicSqlbuf.append(" SELECT T.Ucode,t.Uname ");
        dynamicSqlbuf.append("  FROM T");

        dynamicSqlbuf.append(" WHERE T.UCODE LIKE '" + searchid + "%'");

        dynamicSqlbuf.append(" ORDER BY px;");

        String dynamicSql = dynamicSqlbuf.toString();
        try {
            System.out.println("+++++++" + dynamicSql);
            subjectResult = runner.selectAll(dynamicSql, new Object[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            subjectResult = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("鍙戠敓閿欒", e.getMessage());
            subjectResult.add(tmp);
            session.close();
            return subjectResult;
        }

        session.close();
        return subjectResult;
    }

    @SuppressWarnings({ "rawtypes" })
    public Map<String, List<Map<String, Object>>> getTypeContent(String type) throws Exception {
        Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();

        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());

        String sql = "select * from U_DX_MASTER_TYPE where DXTypeCode= ?";
        List<String> params = new ArrayList<String>();
        params.add(type);
        List<Map<String, Object>> typeResult = new ArrayList<Map<String, Object>>();

        try {
            typeResult = runner.selectAll(sql, params.toArray());
            if (typeResult.size() == 0) {
                throw new Exception("鏁版嵁搴撴湭缁存姢姝ょ被鍨�");
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }

        List<Map<String, Object>> dxResult = new ArrayList<Map<String, Object>>();
        sql = "select " + (String) ((Map) typeResult.get(0)).get("DXCODEITEM") + " as code, "
                + (String) ((Map) typeResult.get(0)).get("DXNAMEITEM") + " as name from "
                + (String) ((Map) typeResult.get(0)).get("DXTABLE");
        try {
            dxResult = runner.selectAll(sql, new Object[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        List<Map<String, Object>> uResult = new ArrayList<Map<String, Object>>();
        sql = "select Ucode as code, Uname as name from U_MASTER where UTypeCode=? ";
        params.clear();
        params.add((String) ((Map) typeResult.get(0)).get("UTYPECODE"));
        try {
            uResult = runner.selectAll(sql, params.toArray());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        result.put("dx", dxResult);
        result.put("u", uResult);
        return result;
    }

    public List<Map<String, Object>> getuTypeCode(String type) throws Exception {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());

        String sql = "select * from U_DX_MASTER_TYPE where DXTypeCode= ?";
        List<String> params = new ArrayList<String>();
        params.add(type);
        List<Map<String, Object>> typeResult = new ArrayList<Map<String, Object>>();
        try {
            typeResult = runner.selectAll(sql, params.toArray());
            if (typeResult.size() == 0) {
                throw new Exception("数据库未维护此类型");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return typeResult;
    }

    @Transactional
    public String updateSetting(String typeCode, String utypeCode, String data) {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());
        try {
            List<Object> params = new ArrayList<Object>();
            params.add(typeCode);
            String dynamicSql = "delete from U_MASTER_MAPPING where DxTypeCode = ?";
            System.out.println("++++++++++" + dynamicSql);
            try {
                runner.delete(dynamicSql, params.toArray());
            } catch (SQLException e) {

                e.printStackTrace();
                session.rollback();
                return "鏁版嵁搴撳彂鐢烻QL閿欒锛佹洿鏂板け璐�";
            }
            String[] dataSet = data.split("&&&");
            StringBuffer valuestr = new StringBuffer();
            for (int i = 0; i < dataSet.length; i++) {
                String[] records = dataSet[i].split("@@@");
                valuestr.append("(NEWID(),");
                valuestr.append("'" + typeCode + "',");
                valuestr.append("'" + records[0] + "',");
                valuestr.append("'" + utypeCode + "',");
                valuestr.append("'" + records[1] + "',");
                valuestr.append("'" + records[2] + "'),");
            }
            String valuestr2 = valuestr.toString();
            valuestr2 = valuestr2.substring(0, valuestr2.length() - 1);

            dynamicSql = "insert into U_MASTER_MAPPING(ID , DxTypeCode , DXCode , UTypeCode , UCode , UAccCode) values"
                    + valuestr2;
            System.out.println("++++++++++" + dynamicSql);
            runner.insert(dynamicSql, new Object[0]);

            return "鏇存柊鎴愬姛锛�";
        } catch (SQLException e) {

            e.printStackTrace();
            session.rollback();
            return "鏁版嵁搴撳彂鐢烻QL閿欒锛佹洿鏂板け璐�";
        } finally {
            session.close();
        }
    }

    public List<Map<String, Object>> getsearchCode(String searchid, String typeset) {
        SqlSession session = this.sqlSessionFactory.openSession();
        SqlRunner runner = new SqlRunner(session.getConnection());
        String sql = "select * from U_DX_MASTER_TYPE where DXTypeCode= ?";
        List<String> params = new ArrayList<String>();
        params.add(typeset);
        List<Map<String, Object>> typeResult1 = new ArrayList<Map<String, Object>>();
        try {
            typeResult1 = runner.selectAll(sql, params.toArray());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("++++++++++" + (typeResult1.get(0)).get("UTYPECODE").toString());
        sql = "select * from U_MASTER where UTypeCode= ? and Ucode like '%" + searchid + "%'";
        params.clear();
        params.add((typeResult1.get(0)).get("UTYPECODE").toString());
        List<Map<String, Object>> typeResult = new ArrayList<Map<String, Object>>();

        try {
            typeResult = runner.selectAll(sql, params.toArray());
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return typeResult;
    }
}
