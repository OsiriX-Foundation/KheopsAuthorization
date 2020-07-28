package online.kheops.auth_server;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import java.util.HashMap;
import java.util.Map;


@ApplicationPath("/*")
public class AuthApplication extends ResourceConfig {




    public AuthApplication() {
        //register(STOWResource.class);
        register(new MyApplicationBinder());
        property("jersey.config.server.provider.classnames", "org.glassfish.jersey.media.multipart.MultiPartFeature,online.kheops.auth_server.filter.CacheFilterFactory");
        /*Map<String, Object> props = new HashMap<>();
        props.put("jersey.config.server.provider.classnames",
                "org.glassfish.jersey.media.multipart.MultiPartFeature,online.kheops.auth_server.filter.CacheFilterFactory");
        addProperties(props);*/
        packages(true, "online.kheops.auth_server");

    }
}
