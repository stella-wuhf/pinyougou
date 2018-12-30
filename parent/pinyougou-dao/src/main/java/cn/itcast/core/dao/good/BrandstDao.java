package cn.itcast.core.dao.good;

import cn.itcast.core.pojo.good.Brandst;
import cn.itcast.core.pojo.good.BrandstQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BrandstDao {
    int countByExample(BrandstQuery example);

    int deleteByExample(BrandstQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(Brandst record);

    int insertSelective(Brandst record);

    List<Brandst> selectByExample(BrandstQuery example);

    Brandst selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Brandst record, @Param("example") BrandstQuery example);

    int updateByExample(@Param("record") Brandst record, @Param("example") BrandstQuery example);

    int updateByPrimaryKeySelective(Brandst record);

    int updateByPrimaryKey(Brandst record);

    List<Map> selectOptionListSt(String name);
}