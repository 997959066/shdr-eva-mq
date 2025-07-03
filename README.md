




Fanout exchange（扇型交换机）
特点：一对多广播，所有绑定的队列都会收到消息。

exchange + queue


Topic exchange（主题交换机）
多级匹配，支持通配符 *（匹配1个） 和 #（0或多个）。

exchange +

Headers exchange（头交换机）
基于 headers 键值匹配，routingKey 不参与路由。

exchange + 


default exchange （默认交换机）
不需要手动声明交换机，routingKey = 队列名。


| 模式类型             | 类型常量                          | 是否需要 `routingKey` | 消息路由策略说明                      | 典型场景      |
| ---------------- | ----------------------------- | ----------------- | ----------------------------- | --------- |
| Direct Exchange  | `BuiltinExchangeType.DIRECT`  | ✅ 是               | routingKey **全匹配**            | 精准投递、点对点  |
| Fanout Exchange  | `BuiltinExchangeType.FANOUT`  | ❌ 否（被忽略）          | **广播**到所有绑定的队列                | 广播、群发通知   |
| Topic Exchange   | `BuiltinExchangeType.TOPIC`   | ✅ 是（支持通配符）        | routingKey **通配符匹配**（`*`、`#`） | 日志系统、分级通知 |
| Headers Exchange | `BuiltinExchangeType.HEADERS` | ❌ 否（使用 headers）   | 根据 headers 中的键值对进行**匹配**      | 多标签条件路由   |
| Default Exchange | `""`（空字符串）                    | ✅ 是（必须等于队列名）      | routingKey 是队列名，系统级默认交换机      | 简易直发      |
| 延迟 Exchange      | `"x-delayed-message"`（插件）     | ✅ 是               | 消息包含延迟时间，**插件级支持**延迟投递        | 延迟任务、重试机制 |
