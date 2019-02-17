package cn.wen.ms.controller;

import cn.wen.ms.controller.viewobject.ItemVO;
import cn.wen.ms.error.BusinessException;
import cn.wen.ms.response.CommonReturnType;
import cn.wen.ms.service.ItemService;
import cn.wen.ms.service.model.ItemModel;
import javafx.scene.input.DataFormat;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@CrossOrigin
@RequestMapping("/item")
public class ItemController extends BaseController{

    @Autowired
    ItemService itemService;


    @GetMapping("/fetchItemList")
    public CommonReturnType fetchItemList(){
        List<ItemModel> itemList = itemService.fetchItemList();
        List<ItemVO> itemVOList = itemList.stream().map(this::convertItemVOFromModel).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }


    @GetMapping("/getItem")
    public CommonReturnType getItem(@RequestParam(name = "id")Integer id){
        ItemModel item = itemService.getItemById(id);
        ItemVO itemVO = convertItemVOFromModel(item);
        return CommonReturnType.create(itemVO);
    }

    @PostMapping("/createItem")
    public CommonReturnType createItem(@RequestParam(name="title") String title,
                                       @RequestParam(name="price")BigDecimal price,
                                       @RequestParam(name="description") String description,
                                       @RequestParam(name="stock")Integer stock,
                                       @RequestParam(name="imgUrl")String imgUrl) throws BusinessException {
        ItemModel itemModel = new ItemModel();
        itemModel.setPrice(price);
        itemModel.setTitle(title);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);
        itemModel.setDescription(description);
        ItemModel item = itemService.createItem(itemModel);
        ItemVO itemVO = convertItemVOFromModel(item);
        return CommonReturnType.create(itemVO);
    }

    private ItemVO convertItemVOFromModel(ItemModel itemModel){
        if(itemModel==null){
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);
        if(itemModel.getPromoModel()!=null){
            //有正在进行或即将进行的秒杀活动
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setPromoItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else {
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }
}
