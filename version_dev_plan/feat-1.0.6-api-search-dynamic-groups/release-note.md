# 1.0.6-SNAPSHOT 发布说明

## 版本

- `1.0.6-SNAPSHOT`
- 上一版本：`1.0.5-SNAPSHOT`
- 版本类型：PATCH

## 本次改动

### API Search 动态分组

- API Search 分组来源由静态 `GroupEnum` 调整为 Shepherd `/spapi/v1/groups/list` 接口优先。
- 即时搜索根据在线分组的 `commonPrefix` 匹配候选分组。
- API Search Cache 刷新使用在线接口返回的全部有效分组。
- 在线分组继续沿用 `GroupManager` 的 1 小时缓存，避免频繁请求 Shepherd。

### 降级兼容

- 在线请求异常、响应 `code` 非 0、响应无法解析或无有效分组时，自动回退 `GroupEnum`。
- `GroupEnum` 原有内容保持不变，继续承担本地降级数据源职责。
- 单个分组的 API 列表获取失败时，只跳过该分组，不中断整体缓存刷新。

## 安全与兼容性

- 请求复用插件中用户配置的 Shepherd Cookie，不保存固定 Cookie 或 SSO 会话信息。
- 不涉及数据库写入、事务、RPC 接口或插件登录策略变更。
- 不改变现有 API path 与 Java 方法索引的数据结构。
- 本次未执行编译或测试，仅完成代码、版本号及增量文档修改。

## 同步文件

- `build.gradle.kts`
- `README.md`
- `src/main/resources/META-INF/plugin.xml`
- `docs/mws-api-search.md`
- `version_dev_plan/feat-1.0.6-api-search-dynamic-groups/release-note.md`
