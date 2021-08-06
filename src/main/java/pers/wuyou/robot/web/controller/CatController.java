package pers.wuyou.robot.web.controller;

import pers.wuyou.robot.common.Cat;
import pers.wuyou.robot.web.common.RestResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuyou
 */
@RestController
@RequestMapping("robot/cat")
public class CatController {

    @GetMapping("getAts")
    public RestResponse<Map<String, String>> getAts(@RequestParam("qqList") List<String> qqList) {
        Map<String, String> map = new HashMap<>(4);
        for (String qq : qqList) {
            map.put(qq, Cat.at(qq));
        }
        return RestResponse.success(map);
    }

    @GetMapping("getAt")
    public RestResponse<String> getAt(String qq) {
        return RestResponse.success(Cat.at(qq));
    }

}
