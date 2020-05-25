package cn.travelround.core.service.user;

import cn.travelround.core.bean.order.Order;
import cn.travelround.core.bean.user.Buyer;

/**
 * Created by travelround on 2019/4/19.
 */
public interface BuyerService {
    Buyer selectBuyerByUsername(String username);

    void insertOrder(Order order, String username);
}
