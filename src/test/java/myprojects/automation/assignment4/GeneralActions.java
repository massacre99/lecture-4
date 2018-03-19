package myprojects.automation.assignment4;


import myprojects.automation.assignment4.model.ProductData;
import myprojects.automation.assignment4.utils.Properties;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains main script actions that may be used in scripts.
 */
public class GeneralActions {
    private WebDriver driver;
    private WebDriverWait wait;
    private String linkToCreatedProduct;
    private ArrayList<String> productsName = new ArrayList<>();
    private ProductData siteProduct;


    //login page
    private By inputLogin = By.id("email");
    private By inputPassword = By.id("passwd");
    private By submitLoginPassButton = By.name("submitLogin");    // By.xpath("//button[@tabindex=4]")

    //dashboard page
    private By spinnerLoader = By.id("ajax_running");
    private By menuCatalog = By.id("subtab-AdminCatalog");
    private By subMenuProducts = By.id("subtab-AdminProducts");

    //products page
    private By newProductButton = By.cssSelector("#page-header-desc-configuration-add >span");
    private By newProductName = By.id("form_step1_name_1");
    private By newProductQuantity = By.id("form_step1_qty_0_shortcut");
    private By newProductPrice = By.id("form_step1_price_ttc_shortcut");
    private By switcherOff = By.xpath("//*[@class = 'switch-input ']");
    private By switcherOn = By.xpath("//*[@class = 'switch-input  -checked']");
    private By popupClose = By.className("growl-close");
    private By saveNewProductButton = By.cssSelector("button.js-btn-save"); //    .btn-primary.js-btn-save
    private By redirectButton = By.cssSelector(".btn-submit.preview");

    /* Можно получить элемент обобщенным селектором вида [class*='switch-input'] и проверять его активность
    по наличию добавляемого класса element.getAttribute("class").contains("checked") */

    //frontend site
    private By allProductsLink = By.cssSelector(".all-product-link");
    private By searchField = By.className("ui-autocomplete-input");
    private By productLinks = By.cssSelector(".product-title > a");
    private By productTitle = By.cssSelector(".h1[itemprop='name']");
    private By productQuantity = By.cssSelector(".product-quantities > span");
    private By productPrice = By.cssSelector("span[content]");

    private By submitSearch = By.xpath("//button[@type='submit']");


    public GeneralActions(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 30);
    }

    /**
     * Logs in to Admin Panel.
     *
     * @param login
     * @param password
     */
    public void login(String login, String password) {
        driver.get(Properties.getBaseAdminUrl());
        driver.findElement(inputLogin).sendKeys(login);
        driver.findElement(inputPassword).sendKeys(password);
        driver.findElement(submitLoginPassButton).click();
        waitForContentLoadSpinner();
    }

    public void createProduct(ProductData newProduct) {
        openProductPage();
        driver.findElement(newProductButton).click();
        waitForContentLoad();
        driver.findElement(newProductName).sendKeys(newProduct.getName());
//        driver.findElement(newProductQuantity).sendKeys(Keys.chord(Keys.CONTROL + "A") + Keys.BACK_SPACE, newProduct.getQty().toString());
//        driver.findElement(newProductPrice).sendKeys(Keys.chord(Keys.CONTROL + "A") + Keys.BACK_SPACE, newProduct.getPrice());
        driver.findElement(newProductQuantity).sendKeys(newProduct.getQty().toString());
        driver.findElement(newProductPrice).sendKeys(newProduct.getPrice());
        driver.findElement(switcherOff).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(switcherOn));
        wait.until(ExpectedConditions.visibilityOfElementLocated(popupClose));
        driver.findElement(popupClose).click();
//        driver.findElement(saveNewProductButton).click();
        new Actions(driver).click(driver.findElement(saveNewProductButton)).perform(); // TODO Переписал как просили
        wait.until(ExpectedConditions.visibilityOfElementLocated(popupClose));
        driver.findElement(popupClose).click();

        linkToCreatedProduct = driver.findElement(redirectButton).getAttribute("data-redirect");
    }

    /**
     * Waits until page loader disappears from the page
     */
    public void waitForContentLoadSpinner() {
        waitForContentLoad();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(spinnerLoader));
    }

    public void waitForContentLoad() {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        Boolean isReady = false;
        while (!isReady) {
            isReady = executor.executeScript("return document.readyState").equals("complete");
        }
    }

    public void openProductPage() {
        new Actions(driver).moveToElement(driver.findElement(menuCatalog)).perform();
        wait.until(ExpectedConditions.elementToBeClickable(subMenuProducts));
        driver.findElement(subMenuProducts).click();
        waitForContentLoad();
    }

    public void verifyProductInSearch(ProductData product) throws InterruptedException {
        driver.get(Properties.getBaseUrl());
        waitForContentLoad();
        driver.findElement(allProductsLink).click();
        driver.findElement(searchField).sendKeys(product.getName());
        //TODO Вылет на FF 54, какая то задержка сохранения формы, оно пишет в productsName.add(productsElement.getText())
        //TODO элементы, которые остались на пред. странице и которых уже нет в DOM, почему?
//        driver.findElement(searchField).submit();
        driver.findElement(submitSearch).click();
        waitForContentLoad();

        List<WebElement> productsElements = driver.findElements(productLinks);
        for (WebElement productsElement : productsElements) {
            productsName.add(productsElement.getText());
        }
    }

    public void openAndCheckItem() {
        List<WebElement> productsElement = driver.findElements(productLinks);

        productsElement.get(0).click();

        String name = driver.findElement(productTitle).getText();
        int quantity = Integer.parseInt(
                driver.findElement(productQuantity)
                        .getText().replaceAll("[\\D]", "").trim());
        float price = Float.parseFloat(
                driver.findElement(productPrice)
                        .getAttribute("content"));
        ProductData temp = new ProductData(name, quantity, price);
        siteProduct = temp;

    }

    public String getLinkToCreatedProduct() {
        return linkToCreatedProduct;
    }

    public ArrayList<String> getProductsName() {
        return productsName;
    }

    public ProductData getSiteProduct() {
        return siteProduct;
    }

}

