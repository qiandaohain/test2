package cn.travelround.core.bean;

import cn.travelround.core.bean.product.Sku;

import java.io.Serializable;

/**
 * Created by travelround on 2019/4/20.
 */
public class BuyerItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Sku sku;
    private Boolean isHave = true;// 是否有货
    private Integer amount = 1;// 购买数量

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    public Boolean getIsHave() {
        return isHave;
    }

    public void setIsHave(Boolean have) {
        isHave = have;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BuyerItem)) return false;

        BuyerItem buyerItem = (BuyerItem) o;

        return sku != null ? sku.getId().equals(buyerItem.sku.getId()) : buyerItem.sku == null;

    }

    @Override
    public int hashCode() {
        return sku != null ? sku.hashCode() : 0;
    }
}
