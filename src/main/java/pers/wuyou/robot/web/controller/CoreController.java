package pers.wuyou.robot.web.controller;

import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.results.GroupFullInfo;
import love.forte.simbot.api.message.results.GroupList;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.api.sender.Getter;
import love.forte.simbot.bot.BotManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.wuyou.robot.common.AccountInfo;
import pers.wuyou.robot.common.GlobalVariable;
import pers.wuyou.robot.entity.RequestEntity;
import pers.wuyou.robot.service.BootStateInfoService;
import pers.wuyou.robot.utils.HttpImageUtil;
import pers.wuyou.robot.web.annotation.CurrentUser;
import pers.wuyou.robot.web.annotation.LoginRequired;
import pers.wuyou.robot.web.common.RestCode;
import pers.wuyou.robot.web.common.RestResponse;
import pers.wuyou.robot.web.entity.GroupEntity;
import pers.wuyou.robot.web.utils.JwtUtil;
import pers.wuyou.robot.web.utils.QQLogin;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * @author wuyou
 */
@RestController
@RequestMapping("uselessRobot/robot")
public class CoreController {
    private final BootStateInfoService bootStateInfoService;
    private final Getter GETTER;

    @Autowired
    public CoreController(BootStateInfoService bootStateInfoService, BotManager manager) {
        this.bootStateInfoService = bootStateInfoService;
        GETTER = manager.getDefaultBot().getSender().GETTER;
    }

    @GetMapping("getLoginQrCode")
    public RestResponse<byte[]> getLoginQrCode(String key) {
        return RestResponse.success(QQLogin.getLoginQrCode(key));
    }

    @GetMapping("getAvatar")
    public RestResponse<String> getAvatar(String url) {
        return RestResponse.success("data:image/png;base64," + Base64.getEncoder().encodeToString((byte[]) HttpImageUtil.get(url).getOtherEntity()));
    }

    @GetMapping("getLoginState")
    public RestResponse<RequestEntity> getLoginState(String key) {
        RequestEntity loginState = QQLogin.getLoginState(key);
        if (loginState == null) {
            return RestResponse.error(RestCode.KEY_ILLEGAL);
        }
        String success = "登录成功";
        if (loginState.getResponse().contains(success)) {
            String uin = "";
            for (Cookie cookie : loginState.getCookies()) {
                if ("uin".equals(cookie.getName())) {
                    uin = cookie.getValue().substring(1);
                    if (GlobalVariable.getAccountFromMemberIndex(uin) == null) {
                        return RestResponse.error(RestCode.USER_VERIFY_FAILED);
                    }
                }
            }
            if (uin.isEmpty()) {
                return RestResponse.error(RestCode.USER_VERIFY_FAILED);
            }
            String token = JwtUtil.sign(uin);
            GlobalVariable.getAccountFromMemberIndex(uin).setCookies(loginState.getCookies());
            loginState.getCookies().add(new BasicClientCookie("token", token));
        }
        return RestResponse.success(loginState);

    }

    @PostMapping("powerSwitch")
    public RestResponse<GroupEntity> powerSwitch(@RequestBody Map<String, Object> map) {
        String groupCode = (String) map.get("groupCode");
        Boolean power = (Boolean) map.get("power");
        bootStateInfoService.bootAndShutDown(groupCode, power);
        GlobalVariable.getBOOT_MAP().put(groupCode, power);
        return RestResponse.success();
    }

    @GetMapping("getGroupList")
    @LoginRequired
    public RestResponse<List<GroupEntity>> getGroupList(@CurrentUser AccountInfo user, String name) {
        GroupList list = GETTER.getGroupList();
        List<GroupEntity> groupInfoList = new ArrayList<>();
        list.forEach(group -> {
            if (GlobalVariable.isAdministrator(user.getCode()) || user.getGroupList().contains(group.getGroupCode())) {
                if (name == null || name.isEmpty()) {
                    groupInfoList.add(buildGroupEntity(group));
                } else {
                    if (group.getGroupName() != null && group.getGroupName().contains(name)) {
                        groupInfoList.add(buildGroupEntity(group));
                    }
                    if (group.getGroupCode().contains(name)) {
                        groupInfoList.add(buildGroupEntity(group));
                    }
                }
            }
        });
        groupInfoList.sort((a, b) -> b.getPermission().getLevel() - a.getPermission().getLevel());
        return RestResponse.success(groupInfoList);
    }

    @GetMapping("getGroupInfo")
    public RestResponse<GroupEntity> getGroupInfo(String groupCode) {
        GroupFullInfo info = GETTER.getGroupInfo(groupCode);
        return RestResponse.success(buildGroupEntity(info));
    }

    @GetMapping("getMemberList")
    public RestResponse<List<GroupMemberInfo>> getMemberList(String groupCode, String search) {
        List<GroupMemberInfo> list = GETTER.getGroupMemberList(groupCode).getResults();
        list.add(GETTER.getMemberInfo(groupCode, GlobalVariable.getDefaultBotCode()));
        list.removeIf(memberInfo -> {
            if (search == null || search.isEmpty()) {
                return false;
            } else {
                if (memberInfo.getAccountRemarkOrNickname() != null && memberInfo.getAccountRemarkOrNickname().contains(search)) {
                    return false;
                }
                return !memberInfo.getAccountCode().contains(search);
            }
        });

        list.sort((a, b) -> b.getPermission().getLevel() - a.getPermission().getLevel());
        return RestResponse.success(list);
    }

    @GetMapping("getMemberInfo")
    public RestResponse<GroupMemberInfo> getMemberInfo(String groupCode, String qqCode) {
        if (groupCode == null || groupCode.isEmpty()) {
            return RestResponse.error(RestCode.GROUP_CODE_IS_EMPTY);
        }
        return RestResponse.success(GETTER.getMemberInfo(groupCode, qqCode));
    }

    private GroupEntity buildGroupEntity(GroupInfo info) {
        String groupCode = info.getGroupCode();
        return GroupEntity.builder()
                .groupAvatar(info.getGroupAvatar())
                .groupName(info.getGroupName())
                .groupCode(groupCode)
                .groupCount(GETTER.getGroupMemberList(groupCode).size() + 1)
                .permission(GETTER.getMemberInfo(groupCode, GlobalVariable.getDefaultBotCode()).getPermission())
                .powerSwitch(GlobalVariable.getBOOT_MAP().getOrDefault(groupCode, false))
                .build();
    }
}
