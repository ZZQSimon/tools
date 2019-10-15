 package cn.com.easyerp.core.map;
 
 import java.util.Date;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;
 
 
 
 
 
 @Widget("pc_map")
 public class MapModel
   extends FormModelBase
 {
   private String attendanceId;
   private String attendanceArea;
   private String attendanceName;
   private String attendanceLon;
   private String attendanceLat;
   private Date beginTime;
   private Date endTime;
   
   public MapModel(String parent) { super(ActionType.view, parent); }
 
 
 
   
   public String getTitle() { return "map"; }
 
 
   
   public String getAttendanceId() { return this.attendanceId; }
 
 
   
   public void setAttendanceId(String attendanceId) { this.attendanceId = attendanceId; }
 
 
   
   public String getAttendanceArea() { return this.attendanceArea; }
 
 
   
   public void setAttendanceArea(String attendanceArea) { this.attendanceArea = attendanceArea; }
 
 
   
   public String getAttendanceName() { return this.attendanceName; }
 
 
   
   public void setAttendanceName(String attendanceName) { this.attendanceName = attendanceName; }
 
 
   
   public String getAttendanceLon() { return this.attendanceLon; }
 
 
   
   public void setAttendanceLon(String attendanceLon) { this.attendanceLon = attendanceLon; }
 
 
   
   public String getAttendanceLat() { return this.attendanceLat; }
 
 
   
   public void setAttendanceLat(String attendanceLat) { this.attendanceLat = attendanceLat; }
 
 
   
   public Date getBeginTime() { return this.beginTime; }
 
 
   
   public void setBeginTime(Date beginTime) { this.beginTime = beginTime; }
 
 
   
   public Date getEndTime() { return this.endTime; }
 
 
   
   public void setEndTime(Date endTime) { this.endTime = endTime; }
 }


