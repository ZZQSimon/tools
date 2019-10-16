package cn.com.easyerp.email;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
    public HashMap<String, String> map = new HashMap<>();

    public void save(UserModel user) {
        System.out.println("cicicici");
        this.map.put("id", String.valueOf(user.getId()));
        this.map.put("email", user.getEmail());
        this.map.put("validateCode", user.getValidateCode());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        String time = sdf.format(user.getRegisterTime());
        this.map.put("registerTime", time);
        int status = user.getStatus();
        this.map.put("status", String.valueOf(status));
        this.map.put("name", user.getName());
        String t2 = sdf.format(user.getLastActivateTime());
        this.map.put("activeLastTime", String.valueOf(t2));
        System.out.println("=======s=========" + status);
    }

    public void update(UserModel user) {
        this.map.put("email", user.getEmail());
        this.map.put("validateCode", user.getValidateCode());
        Date time = user.getRegisterTime();
        this.map.put("registerTime", String.valueOf(time));
        int status = user.getStatus();
        this.map.put("status", String.valueOf(status));
        System.out.println("=======st=========" + status);
    }

    public UserModel find(String email) throws ParseException {
        UserModel user = new UserModel();
        user.setEmail((String) this.map.get("email"));
        user.setName((String) this.map.get("name"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        Date day = sdf.parse((String) this.map.get("registerTime"));
        user.setRegisterTime(day);
        user.setStatus(Integer.valueOf((String) this.map.get("status")).intValue());
        user.setValidateCode((String) this.map.get("validateCode"));

        return user;
    }
}
