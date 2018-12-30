package cn.itcast.core.dao.item;

import cn.itcast.core.pojo.item.ItemCatSt;
import cn.itcast.core.pojo.item.ItemCatStQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemCatStDao {
    int countByExample(ItemCatStQuery example);

    int deleteByExample(ItemCatStQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(ItemCatSt record);

    int insertSelective(ItemCatSt record);

    List<ItemCatSt> selectByExample(ItemCatStQuery example);

    ItemCatSt selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ItemCatSt record, @Param("example") ItemCatStQuery example);

    int updateByExample(@Param("record") ItemCatSt record, @Param("example") ItemCatStQuery example);

    int updateByPrimaryKeySelective(ItemCatSt record);

    int updateByPrimaryKey(ItemCatSt record);
}