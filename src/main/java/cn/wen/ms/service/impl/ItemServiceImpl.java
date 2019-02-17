package cn.wen.ms.service.impl;

import cn.wen.ms.dao.ItemDOMapper;
import cn.wen.ms.dao.ItemStockDOMapper;
import cn.wen.ms.dataobject.ItemDO;
import cn.wen.ms.dataobject.ItemStockDO;
import cn.wen.ms.error.BusinessException;
import cn.wen.ms.error.EmBusinessError;
import cn.wen.ms.service.ItemService;
import cn.wen.ms.service.PromoService;
import cn.wen.ms.service.model.ItemModel;
import cn.wen.ms.service.model.PromoModel;
import cn.wen.ms.validator.ValidationResult;
import cn.wen.ms.validator.ValidatorImpl;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validation;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ItemDOMapper itemDOMapper;
    @Autowired
    ItemStockDOMapper itemStockDOMapper;
    @Autowired
    ValidatorImpl validator;
    @Autowired
    PromoService promoService;
    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        //校验入参

        ValidationResult result = validator.validate(itemModel);
        if(result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }
        //itemModel->dataObject
        ItemDO itemDO = convertItemDataObject(itemModel);
        //写入数据库
        itemDOMapper.insert(itemDO);
        itemModel.setId(itemDO.getId());
        ItemStockDO itemStockDO = convertItemStockDataObject(itemModel);
        itemStockDOMapper.insert(itemStockDO);
        //返回创建完成的对象
        return this.getItemById(itemDO.getId());
    }

    @Override
    public List<ItemModel> fetchItemList() {
        List<ItemDO> itemDOList = itemDOMapper.fetchItemList();

        return itemDOList.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            return this.convertItemModel(itemDO, itemStockDO);
        }).collect(Collectors.toList());
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if(itemDO==null){
            return null;
        }
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
        //将dataobject转化为model
        ItemModel itemModel = convertItemModel(itemDO, itemStockDO);

        //获取活动商品信息
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
        if(promoModel!=null && promoModel.getStatus() !=3){
            itemModel.setPromoModel(promoModel);
        }

        return itemModel;
    }
    @Transactional
    @Override
    public boolean decreaseStock(Integer itemId, Integer amount) {
        int affectedRow = itemStockDOMapper.decreaseStock(itemId, amount);
        //更新库存成功
        return affectedRow < 0;
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) {
        itemDOMapper.increaseSales(itemId,amount);
    }

    private ItemModel convertItemModel(ItemDO itemDO,ItemStockDO itemStockDO){
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return  itemModel;
    }
    private ItemDO convertItemDataObject(ItemModel itemModel){
        if(itemModel==null){
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel,itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }
    private ItemStockDO convertItemStockDataObject(ItemModel itemModel){
        if(itemModel==null){
            return null;
        }
        ItemStockDO itemStockDO=new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());
        return itemStockDO;
    }
}
