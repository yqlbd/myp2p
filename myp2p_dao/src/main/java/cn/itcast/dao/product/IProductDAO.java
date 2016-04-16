package cn.itcast.dao.product;

import cn.itcast.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by yqlbd on 2016/4/12.
 */
public interface IProductDAO extends JpaRepository<Product, Long> {

}
