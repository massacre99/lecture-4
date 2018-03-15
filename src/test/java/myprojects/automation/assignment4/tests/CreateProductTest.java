package myprojects.automation.assignment4.tests;

import myprojects.automation.assignment4.BaseTest;
import myprojects.automation.assignment4.model.ProductData;
import myprojects.automation.assignment4.utils.logging.CustomReporter;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreateProductTest extends BaseTest {

    private ProductData newTestProduct = ProductData.generate();

    @DataProvider
    public Object[][] getProduct() {
        Object[][] product = new Object[][]{{newTestProduct}};
        return product;
    }

    @DataProvider
    public Object[][] getAccount() {
        Object[][] account = new Object[1][2];
        account[0][0] = "webinar.test@gmail.com";
        account[0][1] = "Xcg7299bnSmMuRLp9ITw";
        return account;
    }
    @Test(dataProvider = "getAccount")
    public void createNewProduct(String login, String password) throws InterruptedException {
        actions.login(login,password);
        CustomReporter.logAction("Successful login");

        actions.createProduct(newTestProduct);
        CustomReporter.logAction(String.format("Product %s created", newTestProduct));
        CustomReporter.logAction(String.format("Link to created product - %s", actions.getLinkToCreatedProduct()));
    }

    @Test(dependsOnMethods = "createNewProduct")
    public void checkProduct() throws InterruptedException {
        actions.verifyProductInSearch(newTestProduct);

        Assert.assertTrue(actions.getProductsName().contains(newTestProduct.getName()), "Product not exist at the shop");
        CustomReporter.logAction(String.format("Product name in the menu - %s",actions.getProductsName().get(0)));

        actions.openAndCheckItem();
        ProductData expectedProduct = actions.getSiteProduct();

        Assert.assertEquals(newTestProduct.getName().toUpperCase(), expectedProduct.getName(), "Name not equals");
        CustomReporter.logAction(String.format("Name equals, name - %s",expectedProduct.getName()));
        Assert.assertEquals(newTestProduct.getPrice(),expectedProduct.getPrice(), "Price not equals");
        CustomReporter.logAction(String.format("Price equals, price - %s",expectedProduct.getPrice()));
        Assert.assertEquals(newTestProduct.getQty(),expectedProduct.getQty(),"Quantity not equals");
        CustomReporter.logAction(String.format("Quantity equals, qty - %s",expectedProduct.getQty()));
    }

}
