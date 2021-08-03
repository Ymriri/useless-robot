//package pers.wuyou.robot.utils;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import pers.wuyou.robot.entity.BotDefaultTip;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class DefaultValueUtil {
//    private static BotDefaultTipService botDefaultTipService;
//
//    @Autowired
//    public DefaultValueUtil(BotDefaultTipService botDefaultTipService) {
//        DefaultValueUtil.botDefaultTipService = botDefaultTipService;
//    }
//
//    public static Map<String, Object> map(String... command) {
//        Map<String, Object> map = null;
//        QueryWrapper<BotDefaultTip> queryWrapper1 = new QueryWrapper<>();
//        queryWrapper1.select("name", "value").in("name", (Object[]) command);
//        List<BotDefaultTip> list = botDefaultTipService.list(queryWrapper1);
//        if (list.size() > 0) {
//            map = new HashMap<>();
//            for (BotDefaultTip tip : list) {
//                map.put(tip.getName(), tip.getValue());
//            }
//        }
//        return map;
//    }
//
//    public static Object get(String command) {
//        QueryWrapper<BotDefaultTip> queryWrapper1 = new QueryWrapper<>();
//        queryWrapper1.select("name", "value").eq("name", command);
//        return botDefaultTipService.getMap(queryWrapper1).get("value");
//    }
//}
