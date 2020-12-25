package com.rj.core.huawei.pay;

/**
 * author: jansir
 * e-mail: 369394014@qq.com
 * date: 2020/6/11.
 */
public class GoodBean {
    private String productId;
    private long productPrice;
    private int productSubscriptionStatus;
    private String currency;

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductPrice(long productPrice) {
        this.productPrice = productPrice;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setSubscriptionStatus(int productSubscriptionStatus) {
        this.productSubscriptionStatus = productSubscriptionStatus;
    }
}
