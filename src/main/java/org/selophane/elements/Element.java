package org.selophane.elements;

import org.selophane.elements.impl.ElementImpl;
import org.selophane.elements.impl.internal.ImplementedBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;

/**
 * wraps a web element interface with extra functionality. Anything added here will be added to all descendants.
 */
@ImplementedBy(ElementImpl.class)
public interface Element extends WebElement, WrapsElement, Locatable {
    /**
     * Returns true when the inner element is ready to be used.
     *
     * @return boolean true for an initialized WebElement, or false if we were somehow passed a null WebElement.
     */
    boolean elementWired();
    
    /**
     * Performs a click and then an explicit wait
     * @param time
     */
    public void click(int time);
    
    /**
     * Returns true when the element exists. When using the factory look in {@link org.selophane.elements.impl.internal.ElementHandler#invoke()}
     *
     * @return boolean true for exists, or false if NoSuchElementException is thrown.
     */
    public boolean isElementPresent();
}
