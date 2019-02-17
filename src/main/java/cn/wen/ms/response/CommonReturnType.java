package cn.wen.ms.response;

import javax.swing.plaf.PanelUI;

public class CommonReturnType {
    //表明对请求的返回处理结果success或FAILED
    private String status;
    private Object data;

    //构造方法
    public  static  CommonReturnType create(Object result){
        return  CommonReturnType.create(result,"success");
    }
    public  static  CommonReturnType create(Object result,String status){
        CommonReturnType type = new CommonReturnType();
        type.setData(result);
        type.setStatus(status);
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


}
