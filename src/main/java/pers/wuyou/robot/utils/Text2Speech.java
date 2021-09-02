package pers.wuyou.robot.utils;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
import com.ibm.watson.text_to_speech.v1.util.WaveUtils;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.common.GlobalVariable;
import pers.wuyou.robot.constants.Constant;

import java.io.*;

/**
 * @author wuyou
 */
@Component
@Accessors(chain = true)
@ConfigurationProperties(prefix = Constant.TEXT_TO_SPEECH)
public class Text2Speech {
    private Text2Speech() {
    }

    private static String url;
    private static String apikey;
    private static final String PATH = GlobalVariable.PROJECT_PATH + "\\voice\\";


    public synchronized void setUrl(String url) {
        Text2Speech.url = url;
    }

    public synchronized void setApiKey(String apikey) {
        Text2Speech.apikey = apikey;
    }

    public static String textToSpeech(String text) {
        return textToSpeech(text, Voice.WangWeiVoice);
    }

    public static String textToSpeech(String text, Voice voice) {
        IamAuthenticator authenticator = new IamAuthenticator(apikey);
        TextToSpeech textToSpeech = new TextToSpeech(authenticator);
        textToSpeech.setServiceUrl(url);
        String voicePath = PATH + text + ".wav";
        File file = new File(PATH);
        if (!file.exists() && !file.mkdirs()) {
            return null;
        }
        try {
            SynthesizeOptions synthesizeOptions =
                    new SynthesizeOptions.Builder()
                            .text(text)
                            .accept("audio/wav")
                            .voice(voice.type)
                            .build();

            InputStream inputStream = textToSpeech.synthesize(synthesizeOptions).execute().getResult();
            InputStream in = WaveUtils.reWriteWaveHeader(inputStream);

            OutputStream out = new FileOutputStream(voicePath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.close();
            in.close();
            inputStream.close();
            return voicePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    enum Voice {
        /**
         * LiNa
         */
        LiNaVoice("zh-CN_LiNaVoice"),
        /**
         * WangWei
         */
        WangWeiVoice("zh-CN_WangWeiVoice"),
        /**
         * ZhangJing
         */
        ZhangJingVoice("zh-CN_ZhangJingVoice");

        Voice(String type) {
            this.type = type;
        }

        private final String type;
    }
}
