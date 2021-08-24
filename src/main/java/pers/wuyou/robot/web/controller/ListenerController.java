package pers.wuyou.robot.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.wuyou.robot.core.Listener;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.web.common.RestResponse;
import pers.wuyou.robot.web.vo.ListenerVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuyou
 */
@RestController
@RequestMapping("uselessRobot/listener")
public class ListenerController {

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

}
