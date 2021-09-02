package pers.wuyou.robot.web.common;


import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.reflect.FieldUtils;
import pers.wuyou.robot.exception.DataAlreadyExistException;
import pers.wuyou.robot.exception.DataNotFoundException;
import pers.wuyou.robot.exception.WithTypeException;

/**
 * @author wuyou
 */
public class Exception2CodeRepo {

    private static final ImmutableMap<Object, RestCode> MAP = ImmutableMap.<Object, RestCode>builder()
            .put(IllegalStateException.class, RestCode.UNKNOWN_ERROR)
            .put(DataNotFoundException.class, RestCode.DATA_NOT_FOUND)
            .put(DataAlreadyExistException.class, RestCode.DATA_ALREADY_EXIST)
            .put(UserException.Type.USER_NOT_LOGIN, RestCode.USER_NOT_LOGIN)
            .put(UserException.Type.USER_NOT_FOUND, RestCode.USER_NOT_LOGIN)
            .build();

    private static Object getType(Throwable throwable) {
        try {
            return FieldUtils.readDeclaredField(throwable, "type", true);
        } catch (Exception e) {
            return null;
        }
    }

    public static RestCode getCode(Throwable throwable) {
        if (throwable == null) {
            return RestCode.UNKNOWN_ERROR;
        }
        Object target = throwable;
        if (throwable instanceof WithTypeException) {
            Object type = getType(throwable);
            if (type != null) {
                target = type;
            }
        }
        RestCode restCode = MAP.get(target);
        if (restCode != null) {
            return restCode;
        }
        restCode = MAP.get(target.getClass());
        if (restCode != null) {
            return restCode;
        }
        return RestCode.UNKNOWN_ERROR;
    }

}
