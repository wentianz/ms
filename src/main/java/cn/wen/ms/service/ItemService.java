package cn.wen.ms.service;

import cn.wen.ms.error.BusinessException;
import cn.wen.ms.service.model.ItemModel;

import java.util.List;

public interface ItemService {


    //创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    //商品列表预览
    List<ItemModel> fetchItemList();
    //商品详情
    ItemModel getItemById(Integer id);

    //库存扣减
    boolean decreaseStock(Integer itemId,Integer amount);

    //商品销量增加
    void increaseSales(Integer itemId,Integer amount);

}
