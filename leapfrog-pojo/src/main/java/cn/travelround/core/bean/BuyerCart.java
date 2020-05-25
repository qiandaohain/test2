package cn.travelround.core.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.PipedReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by travelround on 2019/4/20.
 */
public class BuyerCart implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<BuyerItem> items = new ArrayList<>();

    public void addItem(BuyerItem item) {
        // 判断是否同款
        if (items.contains(item)) {
            // 有同款
            for (BuyerItem it : items) {
                if (it.equals(item)) {
                    it.setAmount(item.getAmount()+it.getAmount());
                }
            }
        } else {
            items.add(item);
        }
    }

    public List<BuyerItem> getItems() {
        return items;
    }

    public void setItems(List<BuyerItem> items) {
        this.items = items;
    }

    // 小计 (商品数据 商品金额 运费 总计)
    @JsonIgnore
    public Integer getProductAmount() {
        Integer result = 0;
        for (BuyerItem buyerItem : items) {
            result += buyerItem.getAmount();
        }
        return result;
    }
    // 商品金额
    @JsonIgnore
    public Float getProductPrice() {
        Float result = 0f;
        for (BuyerItem buyerItem : items) {
            result += buyerItem.getAmount()*buyerItem.getSku().getPrice();
        }
        return result;
    }
    // 运费
    @JsonIgnore
    public Float getFee() {
        Float result = 0f;
        if (getProductPrice() < 79) {
            result = 5f;
        }
        return result;
    }
    // 总金额
    @JsonIgnore
    public Float getTotalPrice() {
        return getProductPrice() + getFee();
    }
}
