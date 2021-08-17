# useless-robot

*基于[simpler-robot](https://github.com/ForteScarlet/simpler-robot)实现*

**一个机器人项目, 仅供学习使用**

以前的版本(通过`json`文件实现配置监听器和过滤器)请参考[useless-robot](https://github.com/wuyou-123/simbot-robot-old/) (已停止维护)

以前的以前的版本(通过注解实现listener和filter)请参考[simbot-robot](https://github.com/wuyou-123/simbot-robot/) (已停止维护)

**[后台管理项目(vue)](https://github.com/wuyou-123/useless-robot-admin)**

[QQ交流群](https://jq.qq.com/?_wv=1027&k=1Lopqryf)

## 文档

*官方文档地址: [simpler-robot文档](https://www.yuque.com/simpler-robot/simpler-robot-doc)*

### 下面是针对此项目的一个简单的文档

**监听器配置**

###### 此项目在`simpler-robot`的基础上,添加了通过数据库配置过滤器来配置`listener`, 当然你也可以继续选择使用`simpler-robot`注解开发

数据库主要包含两部分: 监听器 (`listener`) 和 字段值 (`default_value`和`listener_values`)

简单说一下字段值的作用:

字段值是一个`name`和`value`的键值对,可以通过`name`获取到对应的`value`,可能是一个值也可能是多个值,值中可以包含需要替换的对象,[下表](#replace)中会详细列出可以替换的内容

监听器表主要结构:

| 字段名 | 类型 | 注释 | 
| ---- | ---- | ---- |
| name | String | 监听器名称 |
| introduction | String | 监听器简介,用于在web中展示 |
| type | String | 监听类型,多个用","隔开 |
| class_name | String | 监听器类名 |
| method_name | String | 监听器方法名 |
| filter_name | String | 过滤字段名,值为`default_value`的`name` |
| is_boot | Boolean | 是否需要开机 |
| at_any | Boolean | 是否艾特任何人 |
| at_bot | Boolean | 是否艾特bot |
| trim | Boolean | 匹配前是否去除前后空格 |
| at | String | 当参数中的人被at了才会触发,多个用","隔开 |
| codes | String | 匹配这段消息的账号列表,多个用","隔开 |
| groups | String | 匹配当前消息的群列表,多个用","隔开 |
| priority | Integer | 优先级, 1-10, 默认为0 |
| is_spare | Boolean | 是否是备用监听 |
| break_listeners | String | 被阻断的监听器数组,多个用","隔开 |
| update_time | Date | 更新时间,主要用于检测监听器是否需要重新实例化 |

字段值表主要结构:

`default_value`

| 字段名 | 类型 | 注释 |
| ---- | ---- | ---- |
| name | String | 字段名 |
| value | String | 字段值 |
| description | String | 字段描述 |

`listener_values`

| 字段名 | 类型 | 注释 |
| ---- | ---- | ---- |
| value_id | Integer | 字段id,关联`default_value`的`id`字段 |
| value | String | 字段值 |
| group_code | String | 群号 |

**注:**

1. `default_value`表为全局默认表,保证每个监听器中需要的字段的name都可以在表中找到值,当需要存在多个值或者需要根据群区分开时,就可以存在`listener_values`中。默认返回`listener_values`中的值,如果`listener_values`中没有对应的值,则返回`default_value`中的值

2. 项目没有以往的监听器`filter`,而是采用了监听器表`listener`里的`filter_name`字段进行判断,[举例](#example)

**注入**

通过配置文件加载的类不需要添加注解,如需动态注入`bean`请在属性上方添加`@Injection`注解,被注入的对象需要由`springboot`管理

动态注入配置文件的值请在属性上方添加`@InjectionValue`注解

<span id="replace">
</span>

**字段值中的可替换内容表**

| 字段值内容 | 被替换的内容 | 备注 |
| ---- | ---- | ---- |
| ${accountCode} | 发消息的人的QQ号 | - |
| ${accountName} | 发消息的人的昵称 | - |
| ${groupCode} | 消息来源的群号 | 私聊消息会被替换为空 |
| ${groupName} | 消息来源的群名称 | 私聊消息会被替换为空 |
| ${@bot} | 艾特`bot`的猫猫码 | - |
| ${@sender} | 艾特发送者的猫猫码 | - |

<span id="example">
</span>

**示例:针对每个群实现不同的开机命令以及发送内容**

数据库部分:

`default_value`

| id | name | value | description |
| ---- | ---- | ---- | ---- | 
| 1 | boot_command | 开机 | 开机命令 |
| 2 | boot_message | 已开机 | 开机后发送的内容 |

`listener_values`

| id | value_id | value | group_code |
| ---- | ---- | ---- | ---- |
| 1 | 1 | 开启机器人 | 111111111 |
| 3 | 1 | 机器人别睡了 | 222222222 |
| 4 | 2 | 我醒了 | 222222222 |
| 5 | 1 | ${@bot}开机 | 333333333 |
| 6 | 1 | 开机${@bot} | 333333333 |
| 7 | 1 | 开机 | 333333333 |
| 8 | 2 | @{sender}已开机 | 333333333 |
| 9 | 2 | @{sender}开机了 | 333333333 |

`listener`

| id | name | introduction | type | class_name | method_name | filter_name | .... |
| ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- |
| 1 | boot | 开机 | GroupMsg | BootListener | boot | boot_command | ... |

代码部分:
```java
public class BootListener {

    /**
     * 开机
     * 当群`111111111`发送:"开启机器人"时执行此方法
     * 当群`222222222`发送:"开机"时执行此方法
     * 当群`333333333`发送:"开机", "@bot开机"或"开机@bot"时执行此方法
     * @param group 群号
     * @param bootMessage 数据库中配置的内容,
     *                    如果是群`111111111`,则内容为 ["已开机"]
     *                    如果是群`222222222`,则内容为 ["我醒了"]
     *                    如果是群`333333333`,则内容为 ["@xxx 已开机", "@xxx 开机了"]
     *                    注: @bot表示艾特机器人, @xxx表示艾特发送消息的人
     */
    public void boot(String group, @DefaultValue("boot_message") String[] bootMessage) {
        // TODO: 执行开机代码
        // 随机发送数据库中对应的消息
        SenderUtil.sendGroupMsg(group, RandomUtil.getRandomString(bootTip));
    }
}
```

**动态参数**

| 参数类型 | 描述 |  
|  ----  | ----  |
|  ? extends MsgGet  |    本次监听到的事件所对应的消息类型。也是你当前监听的消息类型中的某一种。  |
| MsgSender    |    本次监听所对应的送信器  |
| MessageContent| 消息的MessageContent  |
| Sender         |  对应MsgSender中的SENDER  |
| Setter       |    对应MsgSender中的SETTER  |
| Getter       |    对应MsgSender中的SETTER  |
| AtDetection  |    at检测器，可用于检测当前bot是否被at  |
| Bot          |    当前监听到消息的bot  |
| List         |  消息中的艾特列表     |
| Set          |  消息中的艾特列表     |

*下面是根据变量名注入的参数(多个用`/`隔开)*

|  参数变量名   |   描述  |  
|     ----     |  ----  |
| message/msg  |    消息内容,等同于`msg.getMsg()`   |
| group/fromGroup/groupCode  |    当前消息的群号  |
| qq/fromQQ/qqCode           |  当前消息的发信人QQ  |
| botCode/bot/thisCode         |  当前bot的QQ号  |
| result                     |    阻断通过时监听的返回值,只能用Object类型  |

注:
1. 需要在打包时添加`-parameters`参数或在`pom.xml`中配置`-parameters`,否则打包后变量名会变成`arg0,arg1`导致注入失败
2. 如果某些参数注入失败,比如`List`或者`Set`以及所有[根据变量名注入的参数](),请检查是不是使用了`simbot`的`@Listener`注解,如果使用了此注解调用监听器,请参阅[simpler-robot文档](https://www.yuque.com/simpler-robot/simpler-robot-doc)

