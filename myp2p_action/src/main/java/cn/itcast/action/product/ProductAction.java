package cn.itcast.action.product;

import cn.itcast.action.common.BaseAction;
import cn.itcast.domain.product.Product;
import cn.itcast.domain.product.ProductEarningRate;
import cn.itcast.service.product.IProductService;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.JsonMapper;
import cn.itcast.utils.ProductStyle;
import cn.itcast.utils.Response;
import com.opensymphony.xwork2.ModelDriven;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 显示产品的Action
 * Created by yqlbd on 2016/4/12.
 */


@Controller
@Scope("prototype")
@Namespace("/product")
@ParentPackage("json-default")
public class ProductAction extends BaseAction implements ModelDriven<Product> {

    @Autowired
    private IProductService productService;

    //查找所有产品
    @Action(value = "findAllProduct")
    public void findAllProducts() {

        //处理乱码
        this.getResponse().setCharacterEncoding("UTF-8");

        //调用service完成操作
        List<Product> ps = productService.findAll();

        //转换成中文
        changeStatusToChinese(ps);

        // 响应数据到浏览器端
        try {

            // 返回的json数据格式{status:1,data:[{},{},{}]}
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(ps).toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //查询指定商品
    @Action(value = "findProductById")
    public void findProductById() {

        //处理乱码
        this.getResponse().setCharacterEncoding("UTF-8");

        String proId = this.getRequest().getParameter("proId");

        Product product = productService.findOne(Long.parseLong(proId));

        changeStatusToChinese(product);

        try {
            if (product == null) {
                this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_RESULT).toJSON());
            } else {
                this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(product).toJSON());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //查找利率
    @Action(value = "findRates")
    public void findRates() {

        //处理乱码
        this.getResponse().setCharacterEncoding("UTF-8");

        String proId = this.getParameter("proId");

        List<ProductEarningRate> pers = productService.getProductEarningRates(proId);

        try {

            // 返回的json数据格式{status:1,data:[{},{},{}]}
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(pers).toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //提交修改
    @Action(value = "modifyProduct")
    public void modifyProduct() {
        //接受参数
        String productEarningRates = this.getParameter("proEarningRates");
        //封装成list
        List<ProductEarningRate> pers = new ArrayList<>();
        //将Json数据转换成map
        Map map = new JsonMapper().fromJson(productEarningRates, Map.class);
        //封装
        for (Object key : map.keySet()) {
            ProductEarningRate per = new ProductEarningRate();
            per.setMonth(Integer.parseInt(key.toString()));
            per.setIncomeRate(Double.parseDouble(map.get(key).toString()));
            per.setProductId((int) product.getProId());
            pers.add(per);
        }
        product.setProEarningRate(pers);
        productService.update(product);

        try {
            this.getResponse().getWriter().write(Response.build().setStatus("1").toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //添加
    @Action(value = "addProduct")
    public void addProduct() {
        //接受参数
        String productEarningRates = this.getParameter("proEarningRates");
        //封装成list
        List<ProductEarningRate> pers = new ArrayList<>();
        //将Json数据转换成map
        Map map = new JsonMapper().fromJson(productEarningRates, Map.class);
        //封装
        for (Object key : map.keySet()) {
            ProductEarningRate per = new ProductEarningRate();
            per.setMonth(Integer.parseInt(key.toString()));
            per.setIncomeRate(Double.parseDouble(map.get(key).toString()));
            pers.add(per);
        }
        product.setProEarningRate(pers);
        productService.save(product);
        try {
            this.getResponse().getWriter().write(Response.build().setStatus("1").toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //删除
    @Action(value = "delProduct")
    public void delProduct() {
        productService.delete(product);
        try {
            this.getResponse().getWriter().write(Response.build().setStatus("1").toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //将数字状态转换成中文
    private void changeStatusToChinese(List<Product> ps) {
        if (ps == null) {
            return;
        }
        for (Product product : ps) {
            int way = product.getWayToReturnMoney();
            // 每月部分回款
            if (ProductStyle.REPAYMENT_WAY_MONTH_PART.equals(String.valueOf(way))) {
                product.setWayToReturnMoneyDesc("每月部分回款");
                // 到期一次性回款
            } else if (ProductStyle.REPAYMENT_WAY_ONECE_DUE_DATE.equals(String.valueOf(way))) {
                product.setWayToReturnMoneyDesc("到期一次性回款");
            }

            // 是否复投 isReaptInvest 136：是、137：否
            // 可以复投
            if (ProductStyle.CAN_REPEAR == product.getIsRepeatInvest()) {
                product.setIsRepeatInvestDesc("是");
                // 不可复投
            } else if (ProductStyle.CAN_NOT_REPEAR == product.getIsRepeatInvest()) {
                product.setIsRepeatInvestDesc("否");
            }
            // 年利率
            if (ProductStyle.ANNUAL_RATE == product.getEarningType()) {
                product.setEarningTypeDesc("年利率");
                // 月利率 135
            } else if (ProductStyle.MONTHLY_RATE == product.getEarningType()) {
                product.setEarningTypeDesc("月利率");
            }

            if (ProductStyle.NORMAL == product.getStatus()) {
                product.setStatusDesc("正常");
            } else if (ProductStyle.STOP_USE == product.getStatus()) {
                product.setStatusDesc("停用");
            }

            // 是否可转让
            if (ProductStyle.CAN_NOT_TRNASATION == product.getIsAllowTransfer()) {
                product.setIsAllowTransferDesc("否");
            } else if (ProductStyle.CAN_TRNASATION == product.getIsAllowTransfer()) {
                product.setIsAllowTransferDesc("是");
            }
        }
    }

    //重载方法，将单个Product转换成中文
    private void changeStatusToChinese(Product product) {
        ArrayList<Product> ps = new ArrayList<>();
        ps.add(product);
        changeStatusToChinese(ps);
    }

    Product product = new Product();

    @Override
    public Product getModel() {
        return product;
    }
}
