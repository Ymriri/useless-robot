package pers.wuyou.robot.web.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wuyou
 */
@Data
@NoArgsConstructor
public class User {
    private String code;
    private String name;
    private byte[] avatar;

    public User(String code) {
        this.code = code;
//        GlobalVariable.getSender().GETTER.get
    }
}
