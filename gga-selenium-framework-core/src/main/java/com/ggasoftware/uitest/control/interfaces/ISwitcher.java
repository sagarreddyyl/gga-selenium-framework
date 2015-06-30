package com.ggasoftware.uitest.control.interfaces;

/**
 * Created by Roman_Iovlev on 6/10/2015.
 */
public interface ISwitcher extends IClickable, IText, IHaveValue {
    void switchOn();
    void switchOff();
}