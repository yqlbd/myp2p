package cn.itcast.domain.product;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.*;

/**
 * 产品利率表
 */
@Entity
@Table(name = "T_ProductEarningRate")
public class ProductEarningRate {
    @Id
    @GeneratedValue()
    @Column(name = "T_ID", nullable = false)
    private Integer id;// 主键id

    @Column(name = "T_PID")
    private Integer productId;// 产品id

    @Column(name = "T_MONTH")
    private Integer month;// 月份

    @Column(name = "T_INCOMERATE")
    private double incomeRate; // 收益率

    public ProductEarningRate() {
        super();
    }

    public ProductEarningRate(Integer month, double incomeRate) {
        super();
        this.month = month;
        this.incomeRate = incomeRate;
    }

    /**
     * @return id
     */

    public Integer getId() {
        return id;
    }

    /**
     * @param id 要设置的 id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return productId
     */

    public Integer getProductId() {
        return productId;
    }

    /**
     * @param productId 要设置的 productId
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * @return month
     */

    public Integer getMonth() {
        return month;
    }

    /**
     * @param month 要设置的 month
     */
    public void setMonth(Integer month) {
        this.month = month;
    }

    /**
     * @return incomeRate
     */

    public double getIncomeRate() {
        return incomeRate;
    }

    /**
     * @param incomeRate 要设置的 incomeRate
     */
    public void setIncomeRate(double incomeRate) {
        this.incomeRate = incomeRate;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

}
