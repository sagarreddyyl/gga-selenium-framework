package com.ggasoftware.uitest.control.apiInteract;

import com.ggasoftware.uitest.utils.common.Timer;
import com.ggasoftware.uitest.utils.linqInterfaces.JFuncTT;
import com.ggasoftware.uitest.utils.map.KeyValue;
import com.ggasoftware.uitest.utils.map.MapArray;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.ggasoftware.uitest.control.apiInteract.ContextType.Locator;
import static com.ggasoftware.uitest.utils.TryCatchUtil.tryGetResult;
import static com.ggasoftware.uitest.utils.common.LinqUtils.where;
import static com.ggasoftware.uitest.utils.common.WebDriverByUtils.fillByTemplate;
import static com.ggasoftware.uitest.utils.common.WebDriverByUtils.getByFunc;
import static com.ggasoftware.uitest.utils.common.WebDriverByUtils.getByLocator;
import static com.ggasoftware.uitest.utils.settings.FrameworkSettings.*;
import static java.lang.String.format;

/**
 * Created by Roman_Iovlev on 7/3/2015.
 */
public class GetElementModule {
    private By byLocator;
    public By getLocator() { return byLocator; };
    public boolean haveLocator() { return byLocator != null; }
    private String driverName = "";

    private MapArray<ContextType, By> context = new MapArray<>();
    public void addToContext(By byLocator) { addToContext(Locator, byLocator); }
    public void addToContext(ContextType type, By byLocator) { context.add(type, byLocator); }
    public String printContext() { return context.toString(); }

    private JFuncTT<WebElement, Boolean> elementSearchCriteria = WebElement::isDisplayed;
    public void setElementSearchCriteria(JFuncTT<WebElement, Boolean> criteria) { elementSearchCriteria = criteria; }

    public GetElementModule() {
        driverName = seleniumFactory.currentDriverName;
    }
    public GetElementModule(By byLocator) {
        this();
        this.byLocator = byLocator;
    }

    public WebDriver getDriver() {
        return tryGetResult(() -> seleniumFactory.getDriver(driverName));
    }

    public WebElement getElement() {
        logger.info("Get Web element: " + this.toString());
        WebElement element = getElement(byLocator);
        logger.debug("One element found");
        return element;
    }

    public WebElement getElement(String value) throws Exception {
        logger.info("Get Web element: " + this.toString());
        By byLocator = fillByTemplate(this.byLocator, value);
        WebElement element = getElement(byLocator);
        logger.debug("One element found");
        return element;
    }

    public List<WebElement> getElements() {
        logger.info("Get Web elements: " + this.toString());
        List<WebElement> elements = getElements(byLocator);
        logger.debug(format("Found %s elements", elements.size()));
        return elements;
    }

    private List<WebElement> getElements(By byLocator) {
        List<WebElement> result = new Timer(timeouts.currentTimoutSec * 1000).getByCondition(
                () -> getContext().findElements(byLocator),
                els -> where(els, elementSearchCriteria::invoke).size() > 0);
        timeouts.dropTimeouts();
        return result;
    }
    private WebElement getElement(By byLocator) {
        int timeout = timeouts.currentTimoutSec;
        List<WebElement> result = getElements(byLocator);
        if (result == null) {
            asserter.exception(format(failedToFindElementMessage, this.toString(), timeout));
            return null;
        }
        if (result.size() > 1) {
            asserter.exception(format(findToMuchElementsMessage, result.size(), this.toString(), timeout));
            return null;
        }
        return result.get(0);
    }

    private static final String failedToFindElementMessage = "Can't find element '%s' during %s seconds";
    private static final String findToMuchElementsMessage = "Find %s elements instead of one for element '%s' during %s seconds";
    private SearchContext getContext() {
        if (context == null || context.size() == 0)
            return getDriver();
        SearchContext searchContext = getDriver().switchTo().defaultContent();
        if (context.size() > 0) {
            correctFirstXPathLocator(context);
            for (KeyValue<ContextType, By> locator : context) {
                WebElement element = searchContext.findElement(locator.value);
                if (locator.key == Locator)
                    searchContext = element;
                else {
                    getDriver().switchTo().frame(element);
                    searchContext = getDriver();
                }
            }
        }
        return searchContext;
    }

    private void correctFirstXPathLocator(MapArray<ContextType, By> context) {
        By byValue = context.get(0).value;
        if (byValue.toString().contains("By.xpath: //"))
            context.get(0).value = tryGetResult(() -> getByFunc(byValue).invoke(getByLocator(byValue)
                    .replaceFirst("/", "./")));
    }

    public void clearCookies() throws Exception {
        getDriver().manage().deleteAllCookies();
    }
    public By fillLocatorTemplate(String... args) throws Exception {
        return fillByTemplate(byLocator, args);
    }

    @Override
    public String toString() {
        return format("Locator: '%s'", byLocator) +
                ((context.size() > 0)
                        ? format(", Context: '%s'", context)
                        : "");
    }
}
