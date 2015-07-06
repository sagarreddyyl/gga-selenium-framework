/****************************************************************************
 * Copyright (C) 2014 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 3 as published by the Free Software
 * Foundation.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 ***************************************************************************/
package com.ggasoftware.uitest.control.simple;

import com.ggasoftware.uitest.control.interfaces.ITextArea;
import org.openqa.selenium.By;

/**
 * Text Field control implementation
 *
 * @author Alexeenko Yan
 * @author Shubin Konstantin
 * @author Zharov Alexandr
 */
public class TextArea extends Input implements ITextArea {
    public TextArea() { }
    public TextArea(By byLocator) { super(byLocator); }
    public TextArea(String name, By byLocator) { super(name, byLocator); }

    @Override
    public String getValueAction() throws Exception {
        return getWebElement().getText();
    }

    public final String[] getLines() throws Exception {
        return getText().split("\\n");
    }
}
