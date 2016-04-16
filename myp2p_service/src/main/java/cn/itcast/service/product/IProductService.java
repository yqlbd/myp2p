package cn.itcast.service.product;

import cn.itcast.domain.product.Product;
import cn.itcast.domain.product.ProductEarningRate;

import java.util.List;

public interface IProductService {

    List<Product> findAll();

    Product findOne(long l);

    List<ProductEarningRate> getProductEarningRates(String proId);

    void update(Product product);

    void save(Product product);

    void delete(Product product);
}
