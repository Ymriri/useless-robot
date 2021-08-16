package pers.wuyou.robot.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.wuyou.robot.entity.DefaultValue;
import pers.wuyou.robot.service.DefaultValueService;
import pers.wuyou.robot.web.common.RestResponse;
import pers.wuyou.robot.web.vo.DefaultValueVO;

/**
 * @author wuyou
 */
@RestController
@RequestMapping("uselessRobot/defaultValue")
public class DefaultValueController {
    private final DefaultValueService defaultValueService;

    @Autowired
    public DefaultValueController(DefaultValueService defaultValueService) {
        this.defaultValueService = defaultValueService;
    }

    @GetMapping("search")
    public RestResponse<Page<DefaultValueVO>> search(Integer page, Integer size, String name) {
        long st = System.currentTimeMillis();
        Page<DefaultValueVO> pageParam = new Page<>(page, size);
        pageParam.setRecords(defaultValueService.selectPage(pageParam, name));
        System.out.println(System.currentTimeMillis() - st);
        return RestResponse.success(pageParam);
    }


    @PostMapping("add")
    public RestResponse<Page<DefaultValue>> add(@RequestBody DefaultValue defaultValue) {
        defaultValueService.save(defaultValue);
        return RestResponse.success();
    }

    @PutMapping("edit")
    public RestResponse<Page<DefaultValue>> edit(@RequestBody DefaultValue defaultValue) {
        defaultValueService.updateById(defaultValue);
        return RestResponse.success();
    }
}
