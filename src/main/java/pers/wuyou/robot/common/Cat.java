package pers.wuyou.robot.common;

import catcode.CatCodeUtil;
import catcode.CodeTemplate;
import catcode.MutableNeko;
import catcode.Neko;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.sender.Getter;
import love.forte.simbot.bot.BotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.utils.HttpUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wuyou<br>
 * 2020年5月3日
 */
@Component
@SuppressWarnings("unused")
public class Cat {
    public static final CatCodeUtil UTILS = CatCodeUtil.getInstance();
    private static final CodeTemplate<Neko> NEKO_TEMPLATE = UTILS.getNekoTemplate();
    public static Getter GETTER;
    private static String projectPath;

    @Autowired
    public Cat(BotManager manager) {
        Cat.GETTER = manager.getDefaultBot().getSender().GETTER;
    }

    /**
     * 格式化QQ号
     *
     * @param fromGroup 群号
     * @param qq        QQ号
     * @return 格式化之后的文本
     */
    public static String formatQq(String fromGroup, String qq) {
        if (qq.isEmpty()) {
            return qq;
        }
        try {
            String name = GETTER.getMemberInfo(fromGroup, qq).getAccountRemarkOrNickname();
            return "[" + qq + "](" + name + ")";
        } catch (NoSuchElementException e) {
            return "[" + qq + "]";
        }
    }

    public static String at(String qq, String name) {
        final MutableNeko at = NEKO_TEMPLATE.at(qq).mutable();
        at.put(StringVariable.NAME, name);
        return at + " ";
    }

    /**
     * 艾特某人
     *
     * @param qq 要艾特的QQ号
     * @return 猫猫码字符串
     */
    public static String at(String qq) {
        return NEKO_TEMPLATE.at(qq) + " ";
    }

    /**
     * 是否艾特了bot
     *
     * @param msgget MsgGet
     */
    public static boolean atBot(MsgGet msgget) {
        if (msgget instanceof GroupMsg) {
            return getAts((GroupMsg) msgget).contains(GlobalVariable.getBotManager().getDefaultBot().getBotInfo().getAccountCode());
        } else {
            return false;
        }
    }

    /**
     * 获取艾特bot的猫猫码
     */
    public static String atBot() {
        return Cat.at(GlobalVariable.getDefaultBotCode());
    }

    /**
     * 艾特全体
     */
    public static String atAll() {
        return NEKO_TEMPLATE.atAll() + "";
    }

    /**
     * 获取第一个艾特的QQ号
     *
     * @param msg 消息内容
     * @return 获取到的QQ号
     */
    public static String startsWithAt(String msg) {
        List<String> list = UTILS.split(msg);
        Neko code = UTILS.getNeko(list.get(0), StringVariable.AT);
        if (code != null) {
            return code.get("code");
        }
        return "";
    }

    /**
     * 获取所有艾特的QQ号
     */
    public static Set<String> getAts(MessageGet msg) {
        Set<String> set = new HashSet<>();
        for (Neko neko : msg.getMsgContent().getCats(StringVariable.AT)) {
            if (neko.get("code") != null) {
                set.add(neko.get("code"));
            }
        }
        return set;
    }

    /**
     * 获取所有艾特的QQ号的KQ码
     */
    public static Set<Neko> getAtKqs(String msg) {
        final List<Neko> list = UTILS.getNekoList(msg, StringVariable.AT);
        return new HashSet<>(list);
    }

    /**
     * 获取第一个艾特的QQ号
     */
    public static String getAt(String msg) {
        Neko neko = UTILS.getNeko(msg);
        if (neko == null) {
            return null;
        }
        return neko.get("code");
    }

    /**
     * 将消息里的at的Neko转为文本格式的@xxx
     *
     * @param fromGroup 消息的群号,用于获取对应名片
     * @param str       被处理的文本内容
     * @return 处理后的内容
     */
    public static String nekoToAtText(String fromGroup, String str) {
        return nekoToAtText(fromGroup, str, false);
    }

    /**
     * 将消息里的at的Neko转为文本格式的@xxx,并添加透明特殊符号
     *
     * @param fromGroup 消息的群号,用于获取对应名片
     * @param str       被处理的文本内容
     * @param withCode  是否在文本后面添加QQ号
     * @return 处理后的内容
     */
    public static String nekoToAtText(String fromGroup, String str, boolean withCode) {
        Set<Neko> stringSet = Cat.getAtKqs(str);
        for (Neko neko : stringSet) {
            try {
                if ("true".equals(neko.get("all"))) {
                    str = str.replace(neko, "@全体成员");
                } else {
                    str = str.replace(neko, "@" + GETTER.getMemberInfo(fromGroup, Objects.requireNonNull(neko.get("code"))).getAccountRemarkOrNickname() + "\u202D" + (withCode ? neko.get("code") + "\u202C" : ""));
                }
            } catch (NoSuchElementException e) {
                str = str.replace(neko, "@" + GETTER.getFriendInfo(Objects.requireNonNull(neko.get("code"))).getAccountRemarkOrNickname() + "\u202D" + (withCode ? neko.get("code") + "\u202C" : ""));
            }
        }
        return str;
    }

    public static List<Neko> getKq(String msg, String type) {
        return UTILS.getNekoList(msg, type);
    }

    public static List<Neko> getKq(MessageGet msg, String type) {
        return msg.getMsgContent().getCats(type);
    }

    public static Neko getFace(String id) {
        return NEKO_TEMPLATE.face(id);
    }

    /**
     * 获取语音的猫猫码
     *
     * @param path 语音的文件路径
     * @return Neko
     */
    public static Neko getRecord(String path) {
        MutableNeko neko = NEKO_TEMPLATE.record(path).mutable();
        neko.setType("voice");
        return neko.immutable();
    }

    public static Neko getImage(String path) {
        return NEKO_TEMPLATE.image(path);
    }

    /**
     * 获取音乐
     *
     * @param music 歌曲名
     * @return 获取到的猫猫码, 优先QQ音乐
     */
    public static Neko getMusic(String music) {
        try {
            return getQqMusic(music);
        } catch (Exception e) {
            return get163Music(music);
        }
    }

    /**
     * 网易云音乐搜索歌曲
     *
     * @param music 歌曲名
     * @return 获取到的猫猫码
     */
    public static Neko get163Music(String music) {
        String resp = HttpUtil.get("https://music.163.com/api/search/get/web?type=1&s=" + music).getResponse();
        JSONObject json = JSON.parseObject(resp);
        JSONArray jsonArray = json.getJSONObject("result").getJSONArray("songs");
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String id = jsonObject.getString("id");
        JSONArray artistsJson = jsonObject.getJSONArray("artists");
        List<String> artistList = artistsJson.stream().map(item -> {
            JSONObject object = (JSONObject) item;
            return object.getString(StringVariable.NAME);
        }).collect(Collectors.toList());
        String artists = String.join("&", artistList);
        String song = "https://music.163.com/song/media/outer/url?id=" + id + ".mp3";
        String detail = "https://api.imjad.cn/cloudmusic/?type=detail&id=" + id;
        String preview = JSON.parseObject(HttpUtil.get(detail).getResponse()).getJSONArray("songs").getJSONObject(0).getJSONObject("al").getString("picUrl");
        String jumpUrl = "https://music.163.com/#/song?id=" + id;
        String title = jsonObject.getString(StringVariable.NAME);
        return getMusicNeko(title, preview, artists, song, jumpUrl, "163");
    }

    /**
     * QQ音乐搜索歌曲
     *
     * @param music 歌曲名
     * @return 获取到的猫猫码
     */
    public static Neko getQqMusic(String music) {
        String resp = HttpUtil.get("https://c.y.qq.com/soso/fcgi-bin/client_search_cp?new_json=1&remoteplace=txt.yqq.song&t=0&aggr=1&cr=1&w=" + music + "&format=json&platform=yqq.json").getResponse();
        JSONObject json = JSON.parseObject(resp);
        JSONArray jsonArray = json.getJSONObject("data").getJSONObject("song").getJSONArray("list");
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String mid = jsonObject.getString("mid");
        JSONArray artistsJson = jsonObject.getJSONArray("singer");
        List<String> artistList = artistsJson.stream().map(item -> {
            JSONObject object = (JSONObject) item;
            return object.getString(StringVariable.NAME);
        }).collect(Collectors.toList());
        String artists = String.join("&", artistList);
        String song = null;
        try {
            song = "https://u.y.qq.com/cgi-bin/musicu.fcg?data=" + URLEncoder.encode("{\"req_0\":{\"module\":\"vkey.GetVkeyServer\",\"method\":\"CgiGetVkey\",\"param\":{\"guid\":\"4095854469\",\"songmid\":[\"" + mid + "\"],\"songtype\":[0],\"uin\":\"0\",\"loginflag\":1,\"platform\":\"20\"}},\"comm\":{\"uin\":0,\"format\":\"json\",\"ct\":24,\"cv\":0}}", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "https://dl.stream.qqmusic.qq.com/" + JSON.parseObject(HttpUtil.get(song).getResponse()).getJSONObject("req_0").getJSONObject("data").getJSONArray("midurlinfo").getJSONObject(0).getString("purl");
        String jumpUrl = "https://y.qq.com/n/ryqq/songDetail/" + mid;
        String preview = "https:" + HttpUtil.getJson(HttpUtil.get(jumpUrl).getResponse(), "__INITIAL_DATA__").getJSONObject("detail").getString("picurl");
        String title = jsonObject.getString(StringVariable.NAME);
        return getMusicNeko(title, preview, artists, url, jumpUrl, "qq");
    }

    private static Neko getMusicNeko(String title, String preview, String artists, String musicUrl, String jumpUrl, String type) {
        Map<String, String> map = new HashMap<>(8);
        map.put("content", artists);
        map.put("type", type);
        map.put("musicUrl", musicUrl);
        map.put("title", title);
        map.put("pictureUrl", preview);
        map.put("jumpUrl", jumpUrl);
        map.put("brief", "[分享]" + title);
        return UTILS.toNeko("music", map);
    }

    public static String getProjectPath() {
        return projectPath;
    }

    public static void setProjectPath(String path) {
        projectPath = path;
    }

    @Value("${project.path}")
    public void setPath(String path) {
        setProjectPath(path);
    }


}
