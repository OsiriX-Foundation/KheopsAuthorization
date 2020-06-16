package online.kheops.auth_server.webhook;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheRuntimeConfiguration;
import org.ehcache.config.builders.*;
import org.ehcache.event.*;
import org.ehcache.impl.events.CacheEventAdapter;

import java.time.Duration;
import java.util.logging.Logger;

public class FooCache {


    private static final Logger LOG = Logger.getLogger(FooCache.class.getName());

    class ListenerObject implements CacheEventListener<Object, Object> {
        @Override
        public void onEvent(CacheEvent<?, ?> event) {
            LOG.info(event.getType().name() +" key:"+ (String)event.getKey() +" new:"+(String)event.getNewValue() +" old:" +(String)event.getOldValue());
        }
    }

    class ListenerObject2 extends CacheEventAdapter<Object, Object> {
        @Override
        protected void onExpiry(Object key, Object expiredValue) {

            LOG.info("key:"+(String)key + " expiredValue:" +(String)expiredValue);
        }
    }

    private static FooCache instance = null;
    private static final String CACHE_ALIAS = "foo";

    private Cache<String, String> userCache;

    final CacheEventListenerConfigurationBuilder cacheEventListenerConfiguration = CacheEventListenerConfigurationBuilder
            //.newEventListenerConfiguration(event -> LOG.info(event.getType().name()), EventType.CREATED, EventType.UPDATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED)
            .newEventListenerConfiguration(new ListenerObject(), EventType.CREATED, EventType.UPDATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED)
            //.newEventListenerConfiguration(new ListenerObject2(), EventType.CREATED, EventType.UPDATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED)
            .unordered().asynchronous();

    private FooCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(CACHE_ALIAS, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(30))
                        .withExpiry(ExpiryPolicyBuilder.expiry().access(Duration.ofSeconds(5)).create(Duration.ofSeconds(10)).update(Duration.ofSeconds(15)).build()/*timeToLiveExpiration(Duration.ofSeconds(5))*/).withService(cacheEventListenerConfiguration))
                .build();
        cacheManager.init();
        userCache = cacheManager.getCache(CACHE_ALIAS, String.class, String.class);
    }

    public static synchronized FooCache getInstance() {
        if(instance != null) {
            return instance;
        }
        return instance = new FooCache();
    }

    public void cacheValue(String a, String b) {
        userCache.put(a, b);
    }
    public void get(String a) { LOG.info("GET key:"+a+" value:"+userCache.get(a)); }
    public void p() {
        userCache.forEach(val -> LOG.info(val.getKey()+"-"+val.getValue()));
        CacheRuntimeConfiguration<String, String> toto =userCache.getRuntimeConfiguration();
        LOG.info(toto.toString());


    }
}
