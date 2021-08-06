package pers.wuyou.robot.core.annotation;

import love.forte.simbot.spring.autoconfigure.SimbotAppInfoConfiguration;
import org.springframework.context.annotation.Import;
import pers.wuyou.robot.core.ScanUtil;

import java.lang.annotation.*;

/**
 * @author wuyou
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ScanUtil.class, SimbotAppInfoConfiguration.class})
public @interface UselessScan {
    @SuppressWarnings("unused")
    String[] listenerPackages() default {};
}