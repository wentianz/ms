package cn.wen.ms.dao;

import cn.wen.ms.dataobject.ItemDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemDO record);

    List<ItemDO> fetchItemList();

    int insertSelective(ItemDO record);

    ItemDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ItemDO record);

    int updateByPrimaryKey(ItemDO record);

    void increaseSales(@Param("itemId") Integer itemId,@Param("amount") Integer amount);
}