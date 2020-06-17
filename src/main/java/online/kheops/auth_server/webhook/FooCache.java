package online.kheops.auth_server.webhook;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.EvictionAdvisor;
import org.ehcache.config.builders.*;
import org.ehcache.event.*;
import org.ehcache.impl.events.CacheEventAdapter;

import java.time.Duration;
import java.util.EnumSet;
import java.util.logging.Logger;

import static org.ehcache.event.EventType.*;

public class FooCache {


    private static final Logger LOG = Logger.getLogger(FooCache.class.getName());

    class ListenerObject implements CacheEventListener<Object, Object> {
        @Override
        public void onEvent(CacheEvent<?, ?> event) {
            LOG.info(event.getType().name() +" key:"+ (String)event.getKey() +" new:"+(String)event.getNewValue() +" old:" +(String)event.getOldValue());
        }
    }

    class ListenerObject2 extends CacheEventAdapter<String, String> {
        @Override
        protected void onExpiry(String key, String expiredValue) {

            LOG.info("key:"+key + " expiredValue:" +expiredValue);
        }
    }

    class testEvictionAdvisor implements EvictionAdvisor<String, String> {
        @Override
        public boolean adviseAgainstEviction(String key, String value) {
            return key.compareTo("a") != 0;
        }
    }

    private static FooCache instance = null;
    private static final String CACHE_ALIAS = "foo";
    private static final String CACHE_ALIAS_2 = "foo2";

    private Cache<String, String> userCache;
    private Cache<String, String> userCache2;

    final CacheEventListenerConfigurationBuilder cacheEventListenerConfiguration = CacheEventListenerConfigurationBuilder
            //.newEventListenerConfiguration(event -> LOG.info(event.getType().name()), CREATED, UPDATED, EVICTED, EXPIRED, REMOVED)
            .newEventListenerConfiguration(new ListenerObject(), CREATED, UPDATED, EVICTED, EXPIRED, REMOVED)
            //.newEventListenerConfiguration(new ListenerObject2(), CREATED, UPDATED, EVICTED, EXPIRED, REMOVED)
            .unordered().asynchronous();

    private FooCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .using(PooledExecutionServiceConfigurationBuilder.newPooledExecutionServiceConfigurationBuilder().pool("bliblablo",2,3).pool("bliblablo2",1,5).build())
                .withDefaultEventListenersThreadPool("bliblablo")
                .withCache(CACHE_ALIAS,
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(30))
                        //.withService(cacheEventListenerConfiguration)
                        .withExpiry(ExpiryPolicyBuilder.expiry().access(Duration.ofSeconds(5)).create(Duration.ofSeconds(10)).update(Duration.ofSeconds(15)).build()/*timeToLiveExpiration(Duration.ofSeconds(5))*/)
                        //.withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(5)))
                        //.withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ZERO))
                        .withEvictionAdvisor(new testEvictionAdvisor())
                        )
                .withCache(CACHE_ALIAS_2,
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(3))
                        .withService(cacheEventListenerConfiguration)
                        .withExpiry(ExpiryPolicyBuilder.expiry().access(Duration.ofSeconds(5)).create(Duration.ofSeconds(10)).update(Duration.ofSeconds(15)).build()/*timeToLiveExpiration(Duration.ofSeconds(5))*/)
                        //.withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(5)))
                        //.withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ZERO))
                        .withEvictionAdvisor(new testEvictionAdvisor())
                        .withEventListenersThreadPool("bliblablo2")
                        )
                .build(true);

        userCache = cacheManager.getCache(CACHE_ALIAS, String.class, String.class);
        userCache2 = cacheManager.getCache(CACHE_ALIAS_2, String.class, String.class);
        userCache.getRuntimeConfiguration().registerCacheEventListener(new ListenerObject(), EventOrdering.UNORDERED, EventFiring.SYNCHRONOUS, EnumSet.of(CREATED, REMOVED, EXPIRED, UPDATED, EVICTED));
    }

    public static synchronized FooCache getInstance() {
        if(instance != null) {
            return instance;
        }
        return instance = new FooCache();
    }

    public void cacheValue(String a, String b) {
        userCache.put(a, b);
        userCache2.put(a, b);
    }
    public void get(String a) {
        LOG.info("GET key:"+a+" value:"+userCache.get(a));
        LOG.info("GET key:"+a+" value:"+userCache2.get(a)); }
    public void p() {
        userCache.forEach(val -> LOG.info(val.getKey()+"-"+val.getValue()));
        userCache2.forEach(val -> LOG.info(val.getKey()+"-"+val.getValue()));
    }
}
