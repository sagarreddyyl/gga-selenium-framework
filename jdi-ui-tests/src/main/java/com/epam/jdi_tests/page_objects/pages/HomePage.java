package com.epam.jdi_tests.page_objects.pages;


import com.ggasoftware.jdi_ui_tests.implementation.selenium.elements.composite.Page;
import com.ggasoftware.jdi_ui_tests.implementation.selenium.elements.interfaces.common.IText;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Maksim_Palchevskii on 8/17/2015.
 */

public class HomePage extends Page {
        @FindBy(css = ".main-txt")
        public IText text;

}