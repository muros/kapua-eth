package org.eclipse.kapua.locator;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.KapuaService;

public interface KapuaServiceLoader {

	/**
     * Returns an instance of a KapuaService implementing the provided KapuaService class.
     * 
     * @param serviceClass - class of the service whose instance is required.
     * @return
     */
    public <S extends KapuaService> S getService(Class<S> serviceClass);

    /**
     * Returns an instance of a KapuaEntityFactory implementing the provided KapuaFactory class.
     * 
     * @param factoryClass - class of the factory whose instance is required.
     * @return
     */
    public <F extends KapuaObjectFactory> F getFactory(Class<F> factoryClass);
    
}