package pers.wuyou.robot.listener;

import pers.wuyou.robot.common.Cat;
import pers.wuyou.robot.core.annotation.DefaultValue;
import pers.wuyou.robot.utils.SenderUtil;
import pers.wuyou.robot.utils.Text2Speech;

/**
 * @author wuyou
 */
@SuppressWarnings("unused")
public class VoiceListener {
    public void sayText(String group, @DefaultValue("${0}") String text) {
        String path = Text2Speech.textToSpeech(text);
        System.out.println(path);
        SenderUtil.sendGroupMsg(group, Cat.getRecord(path));
    }
}
