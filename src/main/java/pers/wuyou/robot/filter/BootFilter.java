//package pers.wuyou.robot.filter;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import pers.wuyou.robot.core.FilterData;
//import pers.wuyou.robot.core.ListenerFilter;
//import pers.wuyou.robot.core.MessageUtil;
//import pers.wuyou.robot.core.annotation.Injection;
//import pers.wuyou.robot.entity.BootStateInfo;
//import pers.wuyou.robot.service.BootStateInfoService;
//import pers.wuyou.robot.utils.SenderUtil;
//
//import java.util.Map;
//
//
///**
// * @author wuyou
// */
//public class BootFilter implements ListenerFilter {
//    @Injection
//    BootStateInfoService bootStateInfoService;
//
//    @Override
//    public boolean test(FilterData filterData) {
//        if (filterData.isGroup()) {
//            String[] command = new String[]{"boot_command", "shut_down_command", "boot_tip", "shut_down_tip"};
//            String group = filterData.getGroupMsg().getGroupInfo().getGroupCode();
//            QueryWrapper<BootStateInfo> queryWrapper = new QueryWrapper<>();
//            queryWrapper.select(command).eq("groupCode", group);
//            Map<String, Object> map = bootStateInfoService.getMap(queryWrapper);
//            map = MessageUtil.getDefaultValue(map, command);
//            Map<String, Object> finalMap = map;
//            map.forEach((k, v)->{
//                if(MessageUtil.verifyMessage(v.toString(),filterData.getGroupMsg().getMsg())){
//                    if(k.equals(command[0])){
//                        //执行开机命令
//                        SenderUtil.sendGroupMsg(group, finalMap.get(command[2]).toString());
//                    }
//                    if(k.equals(command[1])){
//                        //执行关机命令
//                        SenderUtil.sendGroupMsg(group, finalMap.get(command[3]).toString());
//                    }
//                }
//            });
//            System.out.println(map);
//            return false;
//        }
//        return false;
//    }
//
//}
