package org.selophane.elements.impl.internal;

import org.selophane.elements.Element;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.selophane.elements.impl.internal.ImplementedByProcessor.getWrapperClass;

/**
 * Replaces DefaultLocatingElementHandler. Simply opens it up to descendants of the WebElement interface, and other
 * mix-ins of WebElement and Locatable, etc. Saves the wrapping type for calling the constructor of the wrapped classes.
 */
public class ElementHandler implements InvocationHandler {
    private final ElementLocator locator;
    private final Class<?> wrappingType;

    /**
     * Generates a handler to retrieve the WebElement from a locator for a given WebElement interface descendant.
     *
     * @param interfaceType Interface wrapping this class. It contains a reference the the implementation.
     * @param locator       Element locator that finds the element on a page.
     * @param <T>           type of the interface
     */
    public <T> ElementHandler(Class<T> interfaceType, ElementLocator locator) {
        this.locator = locator;
        if (!Element.class.isAssignableFrom(interfaceType)) {
            throw new RuntimeException("interface not assignable to Element.");
        }

        this.wrappingType = getWrapperClass(interfaceType);
    }

    @Override
    public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
        WebElement element;
        Exception storedexception = null;
		try {
			element = locator.findElement();
		} catch (Exception e1) {
			//Don't throw an error yet as we want to know if we are calling isElementPresent and if so then return false.
			storedexception = e1;
			element = null;
		}
		
		if ("isElementPresent".equals(method.getName())) {
			return !(element == null);
        }
		
		//now that we aren't calling isElementPresent, the exception should be thrown
		if (element==null)
			throw new Exception(storedexception);

		if ("getWrappedElement".equals(method.getName())) {
			return element;
		}
		Constructor cons = wrappingType.getConstructor(WebElement.class);
		Object thing = cons.newInstance(element);
		try {
			return method.invoke(wrappingType.cast(thing), objects);
		} catch (InvocationTargetException e) {
			// Unwrap the underlying exception
			throw e.getCause();
		}

    }
}
