package entity;

import java.math.BigDecimal;


public class OrderVo {
    private BigDecimal countprice;
    /**
     * 订单创建时间
     */
    private String createday;

    public OrderVo() {
    }

    public OrderVo(BigDecimal countprice, String createday) {
        this.countprice = countprice;
        this.createday = createday;
    }

    public BigDecimal getCountprice() {
        return countprice;
    }

    public void setCountprice(BigDecimal countprice) {
        this.countprice = countprice;
    }

    public String getCreateday() {
        return createday;
    }

    public void setCreateday(String createday) {
        this.createday = createday;
    }

    @Override
    public String toString() {
        return "OrderVo{" +
                "countprice=" + countprice +
                ", createday=" + createday +
                '}';
    }
}
