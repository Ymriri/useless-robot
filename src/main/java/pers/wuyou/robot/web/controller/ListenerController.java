package pers.wuyou.robot.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import love.forte.simbot.api.message.events.GroupMsg;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.wuyou.robot.core.Listener;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.entity.GroupListener;
import pers.wuyou.robot.service.GroupListenerService;
import pers.wuyou.robot.web.common.RestResponse;
import pers.wuyou.robot.web.vo.ListenerVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuyou
 */
@RestController
@RequestMapping("uselessRobot/listener")
public class ListenerController {
    private final GroupListenerService groupListenerService;

    public ListenerController(GroupListenerService groupListenerService) {
        this.groupListenerService = groupListenerService;
    }

    @GetMapping("")
    public RestResponse<List<ListenerVO>> getListeners() {
        List<ListenerVO> listenerMap = new ArrayList<>();
        RobotCore.LISTENER_MAP.values().forEach(v -> {
            for (Listener listener : v) {
                listenerMap.add(ListenerVO.build(listener));
            }
        });
        return RestResponse.success(listenerMap);
    }

    @GetMapping("{group}")
    public RestResponse<List<ListenerVO>> getListenersByGroup(@PathVariable String group) {
        Map<Integer, ListenerVO> listenerMap = new HashMap<>(16);
        RobotCore.LISTENER_MAP.get(GroupMsg.class).forEach(listener -> listenerMap.put(listener.getId(), ListenerVO.build(listener)));
        QueryWrapper<GroupListener> wrapper = new QueryWrapper<>();
        wrapper.eq("group_code", group);
        wrapper.in("listener_id", listenerMap.keySet());
        List<GroupListener> groupListener = groupListenerService.list(wrapper);
        for (GroupListener listener : groupListener) {
            listenerMap.get(listener.getListenerId()).setGroupBoot(listener.isOpen());
        }
        List<ListenerVO> result = new ArrayList<>(listenerMap.values());
        result.sort((a, b) -> b.getId() - a.getId());
        return RestResponse.success(result);
    }

}
