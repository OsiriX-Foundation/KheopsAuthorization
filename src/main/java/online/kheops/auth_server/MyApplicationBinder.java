package online.kheops.auth_server;

import online.kheops.auth_server.stow.FooHashMap;
import online.kheops.auth_server.stow.FooHashMapImpl;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class MyApplicationBinder extends AbstractBinder {


    @Override
    protected void configure() {
        bind(FooHashMapImpl.class).to(FooHashMap.class).in(Singleton.class);
    }
}
