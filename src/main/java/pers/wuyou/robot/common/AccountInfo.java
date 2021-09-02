package pers.wuyou.robot.common;

import lombok.Getter;
import lombok.Setter;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import org.apache.http.cookie.Cookie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wuyou
 */
@Getter
public class AccountInfo {
    private String code;
    private String nickname;
    private String avatarUrl;
    private List<String> groupList;
    @Setter
    private List<Cookie> cookies;

    public AccountInfo(String code, String group) {
        if (code == null || group == null) {
            return;
        }
        this.code = code;
        GroupMemberInfo memberInfo = GlobalVariable.getter().getMemberInfo(group, code);
        this.nickname = memberInfo.getAccountNickname();
        this.avatarUrl = memberInfo.getAccountAvatar();
        this.groupList = new ArrayList<>(Collections.singleton(group));
    }

    public void addGroup(String group) {
        this.groupList.add(group);
    }
}
