package cn.com.easyerp.core.master;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterDao {
  List<DataSourceDescModel> selectDataSourceDesc(@Param("active_flag") int paramInt);
}


