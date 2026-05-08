# API Search Cache 方法反查接口地址 Plan

- 分支：`feat-1.0.5-apisearch-reversefunc`
- 日期：`2026-05-08`
- 来源设计：`api-search-cache-method-reverse-design.md`
- 作者：August Lee

## 1. 实施目标

在现有 `API Search Cache` 选项卡中，保留“API path -> 方法跳转”能力，同时新增“方法全限定名 -> API path 列表”的反向查询能力，并支持点击查询结果复制 API path。

## 2. 实施原则

1. 业务代码手术式修改：业务代码只修改 `SearchCacheManager` 与 `ApiCacheSearchPanel`；发版配套允许修改版本号、README、插件描述等文档/元信息文件。
2. 不改缓存持久化结构：继续使用现有 `ApiPathState#apiPathJson` 保存 `List<ApiIndexDTO>`。
3. 不新增网关请求：仅基于已刷新或已恢复的缓存反查。
4. 第一版只做确定性匹配：`serviceName + methodName`，不做仅方法名模糊搜索。
5. 新增 Java 方法补充 JavaDoc，作者统一 `August Lee`。
6. 文件编码保持 UTF-8（非 BOM）。

## 3. 任务拆解

### Step 1：服务层增加反向索引

文件：`src/main/java/com/augustlee/mt/toolWindow/mws/service/SearchCacheManager.java`

- [ ] 新增静态索引字段：`METHOD_INDEX_MAP`。
- [ ] 新增私有方法：`buildMethodKey(String serviceName, String methodName)`。
- [ ] 修改 `refreshClassIndex(List<ApiIndexDTO> list)`：
  - [ ] 清空 `CLASS_INDEX_MAP`。
  - [ ] 清空 `METHOD_INDEX_MAP`。
  - [ ] 写入原有 path -> class/method 索引。
  - [ ] 写入新增 methodKey -> api list 反向索引。
- [ ] 新增公共查询方法：`getApiIndexListByMethod(String serviceName, String methodName)`。
- [ ] 无匹配时返回空 List，避免 UI 层处理 null。

### Step 2：面板层增加方法查询 UI

文件：`src/main/java/com/augustlee/mt/toolWindow/mws/panel/ApiCacheSearchPanel.java`

- [ ] 新增字段：
  - [ ] `METHOD_LABEL`
  - [ ] `METHOD_TEXT_FIELD`
  - [ ] `METHOD_SEARCH_BUTTON`
  - [ ] `METHOD_RESULT_HINT_LABEL`
  - [ ] `METHOD_RESULT_LIST_MODEL`
  - [ ] `METHOD_RESULT_LIST`
- [ ] 在 `initLayout()` 中新增 Method 输入行。
- [ ] 在 `initLayout()` 中新增查询按钮行。
- [ ] 在 `initLayout()` 中新增结果展示区域。
- [ ] 保持原有 API 输入框、Search 按钮和 Refresh 行为不变。

### Step 3：实现方法表达式解析

文件：`src/main/java/com/augustlee/mt/toolWindow/mws/panel/ApiCacheSearchPanel.java`

- [ ] 新增私有方法：`parseMethodExpression(String expression)`。
- [ ] 支持 `类全限定名#方法名`。
- [ ] 支持 `类全限定名.方法名`。
- [ ] 支持带参数签名，截断 `(` 后内容。
- [ ] 对空输入、无法分割、缺失类名、缺失方法名给出明确异常信息。
- [ ] 不支持仅方法名查询。

### Step 4：实现方法反查事件

文件：`src/main/java/com/augustlee/mt/toolWindow/mws/panel/ApiCacheSearchPanel.java`

- [ ] 在 `initComponent()` 中绑定 `METHOD_SEARCH_BUTTON`。
- [ ] 新增私有方法：`searchApiPathByMethod(ActionEvent actionEvent)`。
- [ ] 查询前校验缓存数量：`SEARCH_CACHE_MANAGER.getApiCount()`。
- [ ] 调用 `parseMethodExpression(...)` 得到 `serviceName + methodName`。
- [ ] 调用 `SEARCH_CACHE_MANAGER.getApiIndexListByMethod(...)`。
- [ ] 查询结果为空时：清空结果列表并更新提示。
- [ ] 查询结果非空时：展示去重后的 API path。

### Step 5：实现点击复制

文件：`src/main/java/com/augustlee/mt/toolWindow/mws/panel/ApiCacheSearchPanel.java`

- [ ] 在 `METHOD_RESULT_LIST` 上绑定鼠标点击事件。
- [ ] 用户点击结果行时读取选中的 API path。
- [ ] 使用系统剪贴板复制 path。
- [ ] 复制成功后更新 `METHOD_RESULT_HINT_LABEL`，例如：`已复制: /api/xxx/path`。
- [ ] 不使用频繁弹窗，避免打扰连续复制。

### Step 6：编译与基础验证

- [ ] 执行 Gradle 编译：`./gradlew compileJava` 或 Windows 下 `.\gradlew.bat compileJava`。
- [ ] 确认无 Java 编译错误。
- [ ] 如有 Checkstyle/IDE inspection 要求，再补充对应验证。

### Step 7：升级插件版本号

文件：`build.gradle.kts`

- [ ] 将项目版本从当前 `1.0.4-SNAPSHOT` 升级到本分支目标版本。
- [ ] 推荐版本：`1.0.5-SNAPSHOT`；如果本次作为正式包发布，则改为 `1.0.5`。
- [ ] 确认 `shadowJar` 与 `buildPlugin` 使用的是同一个 `project.version`，避免产物版本与插件版本不一致。
- [ ] 编译后检查产物文件名或插件包元信息中的版本号是否符合预期。

### Step 8：更新插件描述

文件：`src/main/resources/META-INF/plugin.xml`

- [ ] 在 `<description><![CDATA[ ... ]]></description>` 的 API 查找工具条目中增加本次能力说明。
- [ ] 建议新增描述点：`API Search Cache 支持通过 Java 方法全限定名反查 API path，并支持点击复制结果`。
- [ ] 不调整插件 `id`、`name`、`vendor`、`depends` 等与本需求无关的元信息。
- [ ] 保持现有 HTML 结构，不做大范围重写，避免引入描述格式问题。

### Step 9：更新项目文档

文件：`README.md`

- [ ] 在 API 查找工具说明中补充方法反查 API path 的使用说明。
- [ ] 给出至少一个示例输入：`com.xxx.Service#methodName`。
- [ ] 说明查询结果点击后会复制 API path。
- [ ] 如 README 中已有历史乱码或格式问题，本次只修改与功能说明相关的最小范围，不做全文重排。

### Step 10：补充版本开发记录

文件夹：`version_dev_plan/feat-1.0.5-apisearch-reversefunc/`

- [ ] 保留并更新 `api-search-cache-method-reverse-design.md` 与当前实现保持一致。
- [ ] 保留并更新本 plan 文档，完成项可在实现过程中逐步勾选。
- [ ] 如需要发版说明，新增 `release-note.md`，记录：
  - [ ] 新增能力：方法全限定名反查 API path。
  - [ ] 支持格式：`Class#method`、`Class.method`、带参数签名。
  - [ ] 用户交互：点击结果复制 API path。
  - [ ] 兼容性说明：不改变旧缓存结构。

### Step 11：最终发版前检查

- [ ] 确认业务代码编译通过。
- [ ] 确认版本号已升级。
- [ ] 确认 README 已更新。
- [ ] 确认 `plugin.xml` 描述已更新。
- [ ] 确认 version_dev_plan 下 design/plan/release note 与实际实现一致。
- [ ] 确认 Git diff 中没有误改历史乱码注释、无关格式或本地日志文件。
## 4. 验收用例

### Case 1：原功能不回归

1. 打开 `API Search Cache`。
2. 刷新缓存。
3. 输入已有 API path。
4. 点击 `Search`。
5. 应跳转到原 Java 方法。

### Case 2：标准 `#` 表达式反查

输入：

```text
com.sankuai.sgb2b.seashop.mall.api.thrift.service.promotion.ApiFlashSaleQueryThriftService#mallPageList
```

预期：展示对应 API path。

### Case 3：`.` 表达式反查

输入：

```text
com.sankuai.sgb2b.seashop.mall.api.thrift.service.promotion.ApiFlashSaleQueryThriftService.mallPageList
```

预期：展示对应 API path。

### Case 4：带参数签名反查

输入：

```text
com.sankuai.sgb2b.seashop.mall.api.thrift.service.promotion.ApiFlashSaleQueryThriftService#mallPageList(Request)
```

预期：忽略参数部分，展示对应 API path。

### Case 5：多个接口绑定同一方法

输入一个被多个 API path 绑定的方法。

预期：所有匹配 API path 均展示，且顺序稳定、重复 path 去重。

### Case 6：点击复制

1. 查询出结果。
2. 点击某条 API path。
3. 粘贴到任意文本框。

预期：剪贴板内容为被点击的 API path。

### Case 7：异常输入

| 输入 | 预期 |
| --- | --- |
| 空字符串 | 提示请输入方法全限定名 |
| `mallPageList` | 提示格式不支持 |
| `com.foo.Service#` | 提示缺少方法名 |
| `#mallPageList` | 提示缺少类全限定名 |

## 5. 推荐提交粒度

1. Commit 1：`SearchCacheManager` 增加 method 反向索引。
2. Commit 2：`ApiCacheSearchPanel` 增加方法查询 UI 与复制交互。
3. Commit 3：版本号、README、插件描述与 release note 等发版配套更新。
4. Commit 4：编译修正与验收补丁（如需要）。

## 6. 回滚方案

如果功能出现问题，回滚范围清晰：

- 移除 `SearchCacheManager` 中 `METHOD_INDEX_MAP` 及相关查询方法。
- 移除 `ApiCacheSearchPanel` 中新增 Method 查询 UI、解析方法、复制事件。
- 原有 API_TEXT_FIELD -> SEARCH_BUTTON -> getClassIndex(path) -> goToCode(...) 链路不应受到影响。
- 如版本号或文档已更新但功能回滚，需要同步回滚 README、plugin.xml 描述与版本开发记录，避免发布说明与实际能力不一致。

## 7. 注意事项

- 当前源码中存在部分历史乱码注释，本次不顺手修复，避免扩大 diff。
- `ApiIndexDTO` 无默认构造函数但当前缓存解析已在项目中使用，除非编译或运行暴露问题，否则不在本需求中改动。
- `METHOD_INDEX_MAP` 与 `CLASS_INDEX_MAP` 均为静态缓存，保持现有缓存生命周期语义一致。