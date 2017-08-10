package fr.ebiz.computerdatabase.service.utils.utils;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

public class SpringUtils {
    public static Object unwrapProxy(Object bean) throws Exception {
    /*
     * If the given object is a proxy, set the return value as the object
     * being proxied, otherwise return the given object.
     */
        if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {
            Advised advised = (Advised) bean;
            bean = advised.getTargetSource().getTarget();
        }
        return bean;
    }
}
