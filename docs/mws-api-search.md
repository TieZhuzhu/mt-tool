# MWS API Search 分组来源

## 背景

API Search 原先只通过 `GroupEnum` 获取 Shepherd API 分组。新增分组后需要同步修改插件枚举，存在数据滞后问题。

## 当前策略

- 在线优先：通过 `https://shepherd.mws-test.sankuai.com/spapi/v1/groups/list` 获取当前登录用户可访问的分组。
- 本地兜底：在线请求异常、响应 `code` 非 0、响应无法解析或没有有效分组时，回退 `GroupEnum`。
- 有效分组必须包含非空 `id` 和 `commonPrefix`。
- 在线分组由 `GroupManager` 缓存 1 小时，即时搜索和缓存刷新均通过该管理器获取分组。
- `GroupEnum` 继续保留，只作为在线接口不可用时的降级数据源。

## 搜索行为

即时搜索根据输入路径与分组的 `commonPrefix` 做前缀匹配，再查询匹配分组下的 API。缓存刷新会遍历当前可用的全部分组；单个分组 API 获取失败时只跳过该分组，不中断整体刷新。

## 安全约束

请求复用插件中用户输入的 Shepherd Cookie，不在代码或文档中保存固定 Cookie、SSO 凭证或浏览器会话信息。本改动不涉及数据库读写。

## 版本增量

### 1.0.6-SNAPSHOT

- 分组来源由仅使用 `GroupEnum` 调整为 Shepherd 在线接口优先。
- 即时 API Search 根据在线分组的 `commonPrefix` 匹配候选分组。
- API Search Cache 刷新遍历在线接口返回的有效分组。
- 在线请求失败、响应异常或无有效分组时，统一回退 `GroupEnum`。
- 保留原枚举内容及原有 API 查询流程，不改变 API path 与 Java 方法的匹配规则。
