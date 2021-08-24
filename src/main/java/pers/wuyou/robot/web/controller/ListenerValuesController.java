package pers.wuyou.robot.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.wuyou.robot.entity.ListenerValues;
import pers.wuyou.robot.service.ListenerValuesService;
import pers.wuyou.robot.web.common.RestResponse;
import pers.wuyou.robot.web.vo.ListenerValuesVO;

/**
 * @author wuyou
 */
@RestController
@RequestMapping("uselessRobot/listenerValues")
public class ListenerValuesController {
    private final ListenerValuesService listenerValuesService;

    @Autowired
    public ListenerValuesController(final ListenerValuesService listenerValuesService) {
        this.listenerValuesService = listenerValuesService;
    }


    @PostMapping("")
    public RestResponse<ListenerValues> add(@RequestBody ListenerValuesVO listenerValues) {
        if (listenerValues.getId() != null) {
            listenerValuesService.updateById(listenerValues);
            return RestResponse.success();
        }
        listenerValuesService.saveByName(listenerValues);
        return RestResponse.success();
    }

    @PutMapping("")
    public RestResponse<ListenerValues> edit(@RequestBody ListenerValues listenerValues) {
        listenerValuesService.updateById(listenerValues);
        return RestResponse.success();
    }

    @DeleteMapping("")
    public RestResponse<ListenerValues> delete(Integer id) {
        ListenerValues listenerValues = listenerValuesService.getById(id);
        if (listenerValues == null) {
            return RestResponse.success();
        }
        listenerValuesService.removeById(id);
        return RestResponse.success();
    }
}
