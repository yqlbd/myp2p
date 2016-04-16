package cn.itcast.service.product.impl;

import cn.itcast.dao.product.IProductDAO;
import cn.itcast.dao.product.IProductEarningRateDao;
import cn.itcast.domain.product.Product;
import cn.itcast.domain.product.ProductEarningRate;
import cn.itcast.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * IProductService的实现类
 * Created by yqlbd on 2016/4/12.
 */

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private IProductDAO productDao;

    @Autowired
    private IProductEarningRateDao productEarningRateDao;


    @Override
    public List<Product> findAll() {
        return productDao.findAll();
    }

    @Override
    public Product findOne(long l) {
        return productDao.findOne(l);
    }

    @Override
    public List<ProductEarningRate> getProductEarningRates(String proId) {
        return productEarningRateDao.findByProductId(Integer.parseInt(proId));
    }

    @Override
    @Transactional//修改开启事务
    public void update(Product product) {
        //根据product的ID删除
        productEarningRateDao.delByProductId((int) product.getProId());
        List<ProductEarningRate> pers = product.getProEarningRate();
        productEarningRateDao.save(pers);
        productDao.save(product);
    }

    @Override
    @Transactional//新增开启事务
    public void save(Product product) {
        productDao.save(product);
        long proId = product.getProId();
        List<ProductEarningRate> pers = product.getProEarningRate();
        for (ProductEarningRate per : pers) {
            per.setProductId((int) proId);
            productEarningRateDao.save(per);
        }
    }

    @Override
    @Transactional//删除开启事务
    public void delete(Product product) {
        long proId = product.getProId();
        productEarningRateDao.delByProductId((int) proId);
        productDao.delete(proId);
    }
}
