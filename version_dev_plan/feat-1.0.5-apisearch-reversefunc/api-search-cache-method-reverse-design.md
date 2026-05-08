# API Search Cache 方法反查接口地址 Design

- 分支：`feat-1.0.5-apisearch-reversefunc`
- 日期：`2026-05-08`
- 目标面板：`API Search Cache`
- 设计作者：August Lee

## 1. 背景与目标

当前 `API Search Cache` 选项卡在刷新缓存后，只支持输入接口地址 `path`，再从缓存中找到 `serviceName + methodName` 并跳转到具体 Java 方法。

本次需求是在同一个 `API Search Cache` 选项卡中增加“方法反查接口地址”的能力：用户输入常见 Java 方法表达式，例如：

```text
com.sankuai.sgb2b.seashop.mall.api.thrift.service.promotion.ApiFlashSaleQueryThriftService#mallPageList
```

系统应能识别出目标方法，从已缓存的 API 数据中反向查询接口地址，并在面板中展示结果；用户点击结果时可以复制接口地址。

## 2. 现状梳理

### 2.1 当前关键类

- `src/main/java/com/augustlee/mt/toolWindow/mws/panel/ApiCacheSearchPanel.java`
  - 面板 UI。
  - 当前输入框 `API_TEXT_FIELD` 仅按 API path 搜索。
  - 点击 `SEARCH_BUTTON` 后调用 `SearchCacheManager#getClassIndex(path)`。
  - 找到后调用 `goToCode(serviceName, methodName, project)` 跳转方法。

- `src/main/java/com/augustlee/mt/toolWindow/mws/service/SearchCacheManager.java`
  - 负责刷新、初始化、读取 API 缓存。
  - 当前核心索引为：

    ```java
    private final static Map<String, ClassIndexDTO> CLASS_INDEX_MAP = new ConcurrentHashMap<>();
    ```

  - key 是接口地址 `path`，value 是 `ClassIndexDTO(serviceName, methodName)`。
  - 缓存持久化内容为 `List<ApiIndexDTO>` 的 JSON，`ApiIndexDTO` 目前包含：`path`、`serviceName`、`methodName`。

### 2.2 当前数据已经足够支持反查

`ApiIndexDTO` 同时拥有 `path`、`serviceName`、`methodName`，所以不需要额外请求网关接口，也不需要修改持久化结构。

推荐新增一个内存反向索引：

```java
Map<String, List<ApiIndexDTO>> METHOD_INDEX_MAP
```

key 使用标准化后的方法签名，value 是匹配到的接口列表。value 使用 `List` 是因为同一个 Java 方法理论上可能被多个 API path 绑定。

## 3. 需求范围

### 3.1 做什么

1. 在 `API Search Cache` 同页面中增加“按方法查询接口地址”的入口。
2. 支持从用户输入中解析 `serviceName` 与 `methodName`。
3. 基于缓存反向查询一个或多个 API path。
4. 在同面板中展示结果。
5. 点击结果中的 API path 复制到系统剪贴板。
6. 缓存为空、输入非法、无匹配时给出明确提示。

### 3.2 不做什么

1. 不改网关 API 请求逻辑。
2. 不增加新的持久化字段，避免破坏旧缓存兼容性。
3. 不做模糊搜索或全文搜索，第一版只做确定性反查。
4. 不改变原有“按接口地址跳转方法”的功能。

## 4. 交互设计

### 4.1 推荐 UI：在同一面板增加查询模式区域

在现有 `API Search Cache` 面板下方增加一个轻量区域，不新开顶层 ToolWindow：

```text
[API Search Cache]

Cache size: 123          [自动获取Cookie]
                         [Refresh]
Cookie:                  <cookie text area>

API:                     [/api/path]
                         [Search]

Method:                  [com.xxx.Service#method]
                         [Query API Path]

Result:
- /api/xxx/path          [点击复制]
- /api/yyy/path          [点击复制]
```

说明：

- 保留原 `API:` 输入框和 `Search` 按钮，行为不变。
- 新增 `Method:` 输入框和 `Query API Path` 按钮。
- 新增结果区 `Result`，用 `JList` 或垂直 `JPanel` 展示 API path。
- 点击单条 API path 复制该 path 到系统剪贴板，并弹出轻提示或状态文字。

### 4.2 结果展示控件选择

推荐使用 `JList<ApiIndexDTO>` 或 `JList<String>`：

- 简单、代码少。
- 自带选择模型。
- 双击或单击均容易处理。
- 适合多结果。

第一版交互建议：

- 单击结果行：复制 path。
- 复制成功后更新面板内状态文字，例如：`已复制: /api/xxx/path`。

这样满足“点击允许复制”，同时避免每次复制都弹窗打断操作。

## 5. 方法表达式识别设计

### 5.1 标准缓存 key

新增标准方法 key：

```text
<serviceName>#<methodName>
```

例如：

```text
com.sankuai.sgb2b.seashop.mall.api.thrift.service.promotion.ApiFlashSaleQueryThriftService#mallPageList
```

构建方式：

```java
serviceName.trim() + "#" + methodName.trim()
```

### 5.2 支持的输入表达式

第一版建议支持以下常见表达方式：

| 输入形式 | 示例 | 解析结果 |
| --- | --- | --- |
| `类全限定名#方法名` | `com.foo.UserService#getUser` | service=`com.foo.UserService`, method=`getUser` |
| `类全限定名.方法名` | `com.foo.UserService.getUser` | service=`com.foo.UserService`, method=`getUser` |
| 带括号签名 | `com.foo.UserService#getUser(Long)` | 忽略参数，method=`getUser` |
| IDE 复制引用风格 | `com.foo.UserService.getUser(Long)` | 忽略参数，method=`getUser` |
| 前后空格/换行 | `  com.foo.UserService#getUser  ` | trim 后解析 |

不建议第一版支持仅方法名 `getUser`，因为冲突概率高；如果后续需要，可以做“方法名宽搜”，并明确展示多个 service/path。

### 5.3 解析规则

新增一个小的解析方法即可，不需要过度抽象：

1. `trim` 输入。
2. 如果包含 `(`，截断到 `(` 之前。
3. 优先按 `#` 分割。
4. 否则按最后一个 `.` 分割。
5. 校验：
   - `serviceName` 非空。
   - `methodName` 非空。
   - `serviceName` 至少包含一个 `.`。
6. 返回 `ClassIndexDTO(serviceName, methodName)` 或新增轻量 DTO。

## 6. 服务层设计

### 6.1 新增反向索引

在 `SearchCacheManager` 中新增：

```java
private final static Map<String, List<ApiIndexDTO>> METHOD_INDEX_MAP = new ConcurrentHashMap<>();
```

刷新缓存时同时重建两个索引：

```java
private void refreshClassIndex(List<ApiIndexDTO> list) {
    CLASS_INDEX_MAP.clear();
    METHOD_INDEX_MAP.clear();

    if (list == null || list.isEmpty()) {
        return;
    }

    list.forEach(apiIndexDTO -> {
        CLASS_INDEX_MAP.put(apiIndexDTO.getPath(), apiIndexDTO);
        String methodKey = buildMethodKey(apiIndexDTO.getServiceName(), apiIndexDTO.getMethodName());
        METHOD_INDEX_MAP.computeIfAbsent(methodKey, key -> new ArrayList<>()).add(apiIndexDTO);
    });
}
```

注意：如果继续使用 `ConcurrentHashMap`，`computeIfAbsent(...).add(...)` 在并发写入下不是完全安全。但 `refreshClassIndex` 当前是单线程重建索引，可以接受。若未来并发更新索引，再改成线程安全 List。

### 6.2 新增查询方法

推荐新增：

```java
public List<ApiIndexDTO> getApiIndexListByMethod(String serviceName, String methodName)
```

职责：

- 参数判空。
- 构建标准 key。
- 返回匹配列表。
- 无结果返回空 List，而不是 null，降低 UI 判断复杂度。

## 7. 面板层设计

### 7.1 新增字段

在 `ApiCacheSearchPanel` 中新增：

```java
private final JLabel METHOD_LABEL = new JLabel("Method:");
private final JTextField METHOD_TEXT_FIELD = new JTextField(30);
private final JButton METHOD_SEARCH_BUTTON = new JButton("Query API Path");
private final JLabel METHOD_RESULT_HINT_LABEL = new JLabel("Result:");
private final DefaultListModel<String> METHOD_RESULT_LIST_MODEL = new DefaultListModel<>();
private final JList<String> METHOD_RESULT_LIST = new JList<>(METHOD_RESULT_LIST_MODEL);
```

如果希望后续展示 service/method/path，可将 ListModel 改为 `ApiIndexDTO`，用 renderer 控制展示；第一版用 `String path` 最简洁。

### 7.2 新增事件

```java
this.METHOD_SEARCH_BUTTON.addActionListener(this::searchApiPathByMethod);
this.METHOD_RESULT_LIST.addMouseListener(... copy selected path ...);
```

`searchApiPathByMethod` 流程：

1. 读取 `METHOD_TEXT_FIELD`。
2. 校验缓存是否为空。
3. 解析表达式为 `serviceName + methodName`。
4. 调用 `SEARCH_CACHE_MANAGER.getApiIndexListByMethod(serviceName, methodName)`。
5. 无结果：清空结果区并提示。
6. 有结果：将每个 `ApiIndexDTO#getPath()` 放入结果 List。

### 7.3 复制逻辑

使用 AWT 剪贴板即可：

```java
Toolkit.getDefaultToolkit()
        .getSystemClipboard()
        .setContents(new StringSelection(path), null);
```

复制成功提示推荐更新 `METHOD_RESULT_HINT_LABEL`，例如：

```text
已复制: /api/xxx/path
```

## 8. 异常与边界场景

| 场景 | 行为 |
| --- | --- |
| 缓存为空 | 提示先点击 `Refresh` |
| 输入为空 | 提示请输入方法全限定名 |
| 表达式无法解析 | 提示支持格式：`类全限定名#方法名` 或 `类全限定名.方法名` |
| 无匹配结果 | 结果区清空，提示未找到绑定的 API path |
| 多个 API path | 全部展示，点击任一条复制 |
| 同一 path 重复 | 展示前用 `LinkedHashSet` 去重并保持顺序 |

## 9. 文件修改计划

预计只需要修改以下文件：

1. `src/main/java/com/augustlee/mt/toolWindow/mws/service/SearchCacheManager.java`
   - 增加 `METHOD_INDEX_MAP`。
   - 增加方法 key 构建逻辑。
   - `refreshClassIndex` 同时刷新 path 索引和 method 索引。
   - 增加 `getApiIndexListByMethod(...)`。

2. `src/main/java/com/augustlee/mt/toolWindow/mws/panel/ApiCacheSearchPanel.java`
   - 增加 Method 输入区、查询按钮、结果展示区。
   - 增加表达式解析私有方法。
   - 增加查询和复制事件。

不建议修改：

- `ApiIndexDTO`：现有字段已满足需求。
- `ApiPathState`：持久化结构无需变更。
- 网关 manager：无需新增接口请求。

## 10. JavaDoc 与编码要求

根据项目要求：

- 新增 Java 方法需要补充 JavaDoc。
- 如新增类或需要署名处，`@author` 使用 `August Lee`。
- 文件编码使用 UTF-8（非 BOM）。
- 仅做手术式修改，不顺手重构乱码注释或相邻代码。

## 11. 验收标准

1. 刷新缓存后，原有按 API path 跳转方法功能仍可用。
2. 输入：

   ```text
   com.sankuai.sgb2b.seashop.mall.api.thrift.service.promotion.ApiFlashSaleQueryThriftService#mallPageList
   ```

   能查询到对应 API path 并展示。

3. 输入带参数签名也可识别：

   ```text
   com.sankuai.sgb2b.seashop.mall.api.thrift.service.promotion.ApiFlashSaleQueryThriftService#mallPageList(Request)
   ```

4. 输入 `类全限定名.方法名` 也可识别。
5. 查询结果存在多条时全部展示。
6. 点击结果可以复制 API path。
7. 缓存为空、输入非法、无匹配时有明确提示。
8. 插件可以通过 Gradle 编译。

## 12. 简要实现顺序

1. 在 `SearchCacheManager` 建立 method -> api list 反向索引。
2. 在 `ApiCacheSearchPanel` 增加 UI 组件。
3. 实现方法表达式解析。
4. 实现查询结果展示。
5. 实现点击复制。
6. 编译验证。

## 13. 风险与取舍

- **方法重载风险**：缓存数据目前只有 `methodName`，没有参数签名；因此带参数输入仅用于剥离方法名，不能区分重载。当前网关绑定模型也是 `serviceName + methodName`，第一版保持一致。
- **多 API 绑定同一方法**：用 `List<ApiIndexDTO>` 展示全部结果，不擅自选择其中一个。
- **仅方法名搜索**：暂不支持，避免误匹配；如后续需要，可加一个“宽松搜索”入口。
- **弹窗过多**：复制成功推荐用面板内状态 Label，不推荐每次复制都弹窗。