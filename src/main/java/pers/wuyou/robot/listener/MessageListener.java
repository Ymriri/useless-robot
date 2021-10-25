package pers.wuyou.robot.listener;

import pers.wuyou.robot.core.annotation.DefaultValue;
import pers.wuyou.robot.core.annotation.Injection;
import pers.wuyou.robot.service.MessageService;

/**
 * 自动回复监听器
 * @author wuyou
 */
@SuppressWarnings("unused")
public class MessageListener {
    @Injection
    MessageService service;
    public void addMessage(String group, String qq, @DefaultValue("${0}") String message, @DefaultValue("${1}") String answer){
        System.out.println(group);
        System.out.println(qq);
        System.out.println(message);
        System.out.println(answer);
        System.out.println(service);
//        QueryWrapper<Message> wrapper = new QueryWrapper<>();
//        wrapper.eq("message", );
//        service.getOne(wrapper);
    }
}
