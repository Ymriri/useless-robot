package pers.wuyou.robot.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * @author wuyou
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterClass {
    private Object instance;
    private Method method;

}
