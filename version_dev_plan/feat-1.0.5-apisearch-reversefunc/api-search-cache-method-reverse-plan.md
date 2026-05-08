# API Search Cache 方法反查接口地址 Plan

- 分支：`feat-1.0.5-apisearch-reversefunc`
- 日期：`2026-05-08`
- 来源设计：`api-search-cache-method-reverse-design.md`
- 作者：August Lee

## 1. 实施目标

在现有 `API Search Cache` 选项卡中，分两阶段完成能力建设：

1. 已完成阶段：
   - 保留“API path -> 方法跳转”能力；
   - 新增“方法全限定名 -> API path 列表”的反向查询能力；
   - 支持点击查询结果复制 API path；
   - 支持 API 搜索输入自动补齐前导 `/`。
2. 已完成增强阶段：
   - 在现有方法反查能力中加入“方法上钻调用链 -> 接口地址汇总”的增强查询能力；
   - 上钻层级支持可配置，默认值为 `5`。

## 2. 实施原则

1. 业务代码手术式修改：业务代码只修改 `SearchCacheManager` 与 `ApiCacheSearchPanel`；发版配套允许修改版本号、README、插件描述等文档/元信息文件。
2. 不改缓存持久化结构：继续使用现有 `ApiPathState#apiPathJson` 保存 `List<ApiIndexDTO>`。
3. 不新增网关请求：仅基于已刷新或已恢复的缓存反查。
4. 第一阶段只做确定性匹配：`serviceName + methodName`，不做仅方法名模糊搜索。
5. 第二阶段在确定性匹配基础上，增加受层级控制的上层调用链搜索。
6. 新增 Java 方法补充 JavaDoc，作者统一 `August Lee`。
7. 文件编码保持 UTF-8（非 BOM）。

## 3. 任务拆解

### Step 1：服务层增加反向索引（已完成）

文件：`src/main/java/com/augustlee/mt/toolWindow/mws/service/SearchCacheManager.java`

- [x] 新增静态索引字段：`METHOD_INDEX_MAP`。
- [x] 新增私有方法：`buildMethodKey(String serviceName, String methodName)`。
- [x] 修改 `refreshClassIndex(List<ApiIndexDTO> list)`：
  - [x] 清空 `CLASS_INDEX_MAP`。
  - [x] 清空 `METHOD_INDEX_MAP`。
  - [x] 写入原有 path -> class/method 索引。
  - [x] 写入新增 methodKey -> api list 反向索引。
- [x] 新增公共查询方法：`getApiIndexListByMethod(String serviceName, String methodName)`。
- [x] 无匹配时返回空 List，避免 UI 层处理 null。

### Step 2：面板层增加方法查询 UI（已完成）

文件：`src/main/java/com/augustlee/mt/toolWindow/mws/panel/ApiCacheSearchPanel.java`

- [x] 新增字段：
  - [x] `METHOD_LABEL`
  - [x] `METHOD_TEXT_FIELD`
  - [x] `METHOD_SEARCH_BUTTON`
  - [x] `METHOD_RESULT_HINT_LABEL`
  - [x] `METHOD_RESULT_LIST_MODEL`
  - [x] `METHOD_RESULT_LIST`
- [x] 在 `initLayout()` 中新增 Method 输入行。
- [x] 在 `initLayout()` 中新增查询按钮行。
- [x] 在 `initLayout()` 中新增结果展示区域。
- [x] 保持原有 API 输入框、Search 按钮和 Refresh 行为不变。

### Step 3：实现方法表达式解析（已完成）

文件：`src/main/java/com/augustlee/mt/toolWindow/mws/panel/ApiCacheSearchPanel.java`

- [x] 新增私有方法：`parseMethodExpression(String expression)`。
- [x] 支持 `类全限定名#方法名`。
- [x] 支持 `类全限定名.方法名`。
- [x] 支持带参数签名，截断 `(` 后内容。
- [x] 对空输入、无法分割、缺失类名、缺失方法名给出明确异常信息。
- [x] 不支持仅方法名查询。

### Step 4：实现方法反查事件（已完成）

文件：`src/main/java/com/augustlee/mt/toolWindow/mws/panel/ApiCacheSearchPanel.java`

- [x] 在 `initComponent()` 中绑定 `METHOD_SEARCH_BUTTON`。
- [x] 新增私有方法：`searchApiPathByMethod(ActionEvent actionEvent)`。
- [x] 查询前校验缓存数量：`SEARCH_CACHE_MANAGER.getApiCount()`。
- [x] 调用 `parseMethodExpression(...)` 得到 `serviceName + methodName`。
- [x] 调用 `SEARCH_CACHE_MANAGER.getApiIndexListByMethod(...)`。
- [x] 查询结果为空时：清空结果列表并更新提示。
- [x] 查询结果非空时：展示去重后的 API path。

### Step 5：实现点击复制（已完成）

文件：`src/main/java/com/augustlee/mt/toolWindow/mws/panel/ApiCacheSearchPanel.java`

- [x] 在 `METHOD_RESULT_LIST` 上绑定鼠标点击事件。
- [x] 用户点击结果行时读取选中的 API path。
- [x] 使用系统剪贴板复制 path。
- [x] 复制成功后更新 `METHOD_RESULT_HINT_LABEL`，例如：`已复制: /api/xxx/path`。
- [x] 不使用频繁弹窗，避免打扰连续复制。

### Step 6：编译与基础验证（已完成）

- [x] 执行 Gradle 编译：`./gradlew compileJava` 或 Windows 下 `.\gradlew.bat compileJava`。
- [x] 确认无 Java 编译错误。
- [ ] 如有 Checkstyle/IDE inspection 要求，再补充对应验证。

### Step 7：升级插件版本号（已完成）

文件：`build.gradle.kts`

- [x] 将项目版本从当前 `1.0.4-SNAPSHOT` 升级到本分支目标版本。
- [x] 推荐版本：`1.0.5-SNAPSHOT`；如果本次作为正式包发布，则改为 `1.0.5`。
- [x] 确认 `shadowJar` 与 `buildPlugin` 使用的是同一个 `project.version`，避免产物版本与插件版本不一致。
- [ ] 编译后检查产物文件名或插件包元信息中的版本号是否符合预期。

### Step 8：更新插件描述（已完成）

文件：`src/main/resources/META-INF/plugin.xml`

- [x] 在 `<description><![CDATA[ ... ]]></description>` 的 API 查找工具条目中增加本次能力说明。
- [x] 建议新增描述点：`API Search Cache 支持通过 Java 方法全限定名反查 API path，并支持点击复制结果`。
- [x] 不调整插件 `id`、`name`、`vendor`、`depends` 等与本需求无关的元信息。
- [x] 保持现有 HTML 结构，不做大范围重写，避免引入描述格式问题。

### Step 9：更新项目文档（已完成）

文件：`README.md`

- [x] 在 API 查找工具说明中补充方法反查 API path 的使用说明。
- [x] 给出至少一个示例输入：`com.xxx.Service#methodName`。
- [x] 说明查询结果点击后会复制 API path。
- [x] 如 README 中已有历史乱码或格式问题，本次只修改与功能说明相关的最小范围，不做全文重排。

### Step 10：补充版本开发记录（持续维护）

文件夹：`version_dev_plan/feat-1.0.5-apisearch-reversefunc/`

- [x] 保留并更新 `api-search-cache-method-reverse-design.md` 与当前实现保持一致。
- [x] 保留并更新本 plan 文档，完成项可在实现过程中逐步勾选。
- [x] 已补充 `release-note.md`，记录：
  - [x] 新增能力：方法全限定名反查 API path。
  - [x] 支持格式：`Class#method`、`Class.method`、带参数签名。
  - [x] 用户交互：点击结果复制 API path。
  - [x] 兼容性说明：不改变旧缓存结构。
  - [x] API 搜索支持自动补齐前导 `/`。
  - [x] 已同步下一阶段“方法上钻反查接口地址”的设计与计划。

### Step 11：API 搜索输入归一化（已完成）

文件：`src/main/java/com/augustlee/mt/toolWindow/mws/panel/ApiCacheSearchPanel.java`

- [x] 在 API 搜索逻辑中增加路径规范化。
- [x] 当用户输入不以 `/` 开头时，自动补齐前导 `/`。
- [x] 将补齐后的值回写输入框，保持用户可见。
- [x] 编译验证通过。

### Step 12：方法上钻反查接口地址（已完成）

#### 12.1 服务与结构设计

- [x] 新增调用链分析服务类：`ApiMethodCallTraceService`。
- [x] 设计结果 DTO：`ApiMethodSearchResultDTO`。
- [x] 保持 `SearchCacheManager` 仅负责缓存与索引，不混入复杂 PSI 搜索职责。

#### 12.2 方法定位与重载处理

- [x] 将方法表达式解析结果升级为可支持参数签名的结构。
- [x] 当输入带参数时，优先精确定位重载方法。
- [x] 当输入不带参数且存在同名重载时，合并所有同名方法查询。
- [x] 对“未找到源码方法”的场景给出明确提示。

#### 12.3 调用链上钻搜索

- [x] 基于 `PsiMethod` 向上搜索调用方方法。
- [x] 使用 BFS，便于记录层级与控制上限。
- [x] 搜索过程中对方法节点去重，避免递归或环路死循环。
- [x] 每一层都尝试与缓存入口方法做匹配。
- [x] 收集直接命中与上钻命中的 API path。

#### 12.4 上钻层级配置

- [x] 在面板增加“上钻层级”输入控件。
- [x] 默认值设为 `5`。
- [x] UI 可配置范围控制为 `0 ~ 8`。
- [x] 代码内部保留硬上限保护，限制为 `10`。
- [x] 当用户输入非法层级时给出明确提示。

#### 12.5 结果展示升级

- [x] 结果展示从单纯 `String path` 升级为结果对象视图。
- [x] 至少展示：`path`、`入口方法`、`命中层级`、`命中方式`。
- [x] 继续支持点击复制，仅复制接口地址本身。
- [x] 结果提示文案能区分“直接命中”和“上钻命中”数量。

#### 12.6 验证与文档同步

- [x] 增加底层方法 -> 上层 API 入口地址的验收用例。
- [x] 编译验证通过。
- [x] README、plugin.xml、release-note 与 design/plan 同步更新。

### Step 13：最终发版前检查

- [x] 确认业务代码编译通过。
- [x] 确认版本号已升级。
- [x] 确认 README 已更新。
- [x] 确认 `plugin.xml` 描述已更新。
- [x] 确认 version_dev_plan 下 design/plan/release note 与实际实现一致。
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

### Case 8：API 搜索自动补齐 `/`

输入：

```text
api/order/create
```

预期：

1. 系统自动补齐为 `/api/order/create`；
2. 输入框回写补齐后的路径；
3. 搜索结果与直接输入 `/api/order/create` 一致。

### Case 9：方法上钻命中接口

前提链路：

```text
ApiEntryMethod -> ServiceMethod -> DomainMethod -> TargetMethod
```

输入：

```text
com.foo.TargetService#targetMethod
```

上钻层级：

```text
5
```

预期：

1. 即使 `targetMethod` 本身未直接绑定 API；
2. 只要存在上层 API 入口方法可达；
3. 也能展示对应 API path；
4. 结果中标识命中层级与入口方法。

## 5. 推荐提交粒度

1. Commit 1：`SearchCacheManager` 增加 method 反向索引。
2. Commit 2：`ApiCacheSearchPanel` 增加方法查询 UI 与复制交互。
3. Commit 3：API 搜索自动补齐前导 `/`。
4. Commit 4：版本号、README、插件描述与 release note 等发版配套更新。
5. Commit 5：方法上钻调用链分析服务与 UI 增强。
6. Commit 6：编译修正与验收补丁（如需要）。

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
- “方法上钻反查”依赖 IDE PSI 调用关系分析，性能成本高于纯缓存查询，必须严格控制默认层级和最大层级。
