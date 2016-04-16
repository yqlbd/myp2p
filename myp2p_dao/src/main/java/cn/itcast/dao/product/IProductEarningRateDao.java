package cn.itcast.dao.product;

import cn.itcast.domain.product.ProductEarningRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 利率Dao
 * Created by yqlbd on 2016/4/12.
 */
public interface IProductEarningRateDao extends JpaRepository<ProductEarningRate, Integer> {

    List<ProductEarningRate> findByProductId(int parseInt);

    @Modifying
    @Query("delete from ProductEarningRate per where per.productId=?1")
    void delByProductId(int proId);
}
