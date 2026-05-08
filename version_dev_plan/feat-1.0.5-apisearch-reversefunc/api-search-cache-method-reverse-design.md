# API Search Cache 方法反查接口地址 Design

- 分支：`feat-1.0.5-apisearch-reversefunc`
- 日期：`2026-05-08`
- 目标面板：`API Search Cache`
- 设计作者：August Lee

## 1. 背景与目标

当前 `API Search Cache` 选项卡已经具备以下能力：

1. 支持刷新 API 缓存；
2. 支持根据 API path 跳转到对应 Java 方法；
3. 支持根据方法全限定名反向查询 API path；
4. 支持点击结果复制 API path；
5. 支持 API 搜索输入自动补齐前导 `/`。

在此基础上，新增第二阶段需求：

> 当用户输入某个“底层方法”时，不仅要查询该方法本身直接绑定的 API path，还要继续向上查找它的调用方方法；如果上层调用链最终能回到已缓存的 API 入口方法，则这些 API path 也需要一起展示出来。

等价理解：

- 现有能力：`方法 -> 直接绑定的接口地址`
- 新增能力：`方法 -> 上层调用链 -> 所有可到达的接口入口地址`

例如：

```text
API入口方法A -> Service方法B -> Domain方法C -> Utility方法D
```

如果用户查询的是 `Utility方法D`，系统应能继续向上钻取调用链，最终将 `API入口方法A` 绑定的接口地址展示出来。

### 1.1 当前实现落地说明

当前实现已经按本设计落地，核心结构如下：

1. `SearchCacheManager`
   - 继续负责缓存读取、path 索引、method 索引；
2. `ApiMethodCallTraceService`
   - 负责基于 IntelliJ PSI 做方法定位、调用方搜索、上钻遍历与结果汇总；
3. `ApiCacheSearchPanel`
   - 新增 `上钻层级` 控件；
   - 查询结果升级为对象展示，展示接口地址、入口方法、命中方式；
4. `ApiMethodQueryDTO` / `ApiMethodSearchResultDTO`
   - 分别承载查询参数与结果展示信息。

## 2. 本次设计范围

### 2.1 已完成能力同步

本设计文档需同步记录当前已落地能力：

1. 方法表达式反查 API path；
2. 查询结果点击复制；
3. API 搜索输入未带 `/` 时自动补齐。

### 2.2 新增设计范围

本次新增设计仅覆盖以下内容：

1. 方法反查时支持“上钻调用链”；
2. 上钻层级可配置；
3. 查询结果需要能区分：
   - 直接命中的接口；
   - 通过上层调用链追溯得到的接口；
4. 继续保持当前同面板查询，不新增新的 ToolWindow。

### 2.3 明确不做

1. 不做全局全文检索；
2. 不做跨语言调用链分析；
3. 不修改网关缓存持久化结构；
4. 不做复杂图谱可视化，第一版仍以列表展示为主；
5. 不引入数据库或远程服务依赖，继续基于本地缓存 + IDE PSI 分析。

## 3. 现状梳理

### 3.1 当前缓存能力

当前 `SearchCacheManager` 已具备：

- `path -> ClassIndexDTO(serviceName, methodName)` 正向索引；
- `serviceName#methodName -> List<ApiIndexDTO>` 反向索引。

这使得“直接绑定方法”的接口反查已经可以完成。

### 3.2 新需求为什么不能只靠缓存完成

对于“方法上钻反查”，仅靠缓存中的 `serviceName + methodName + path` 不够。

原因是缓存只知道：

- 哪个接口地址绑定了哪个入口方法；

但不知道：

- 某个普通方法被哪些上层方法调用；
- 调用链是否最终连接到某个 API 入口方法。

因此第二阶段需要引入 **IDE 项目代码调用关系分析**，而不是单纯查缓存。

## 4. 核心设计思路

## 4.1 总体方案

查询流程分为两段：

### 阶段一：定位目标方法

用户输入方法表达式，例如：

```text
com.foo.OrderService#createOrder
com.foo.OrderService.createOrder
com.foo.OrderService#createOrder(Request)
```

系统先解析表达式，并定位到一个或多个 `PsiMethod`。

### 阶段二：向上钻取调用链并回查接口

从目标 `PsiMethod` 开始，递归或广度遍历其调用方方法：

1. 当前方法先尝试直接匹配缓存中的 API 入口方法；
2. 再搜索“谁调用了当前方法”；
3. 将调用方方法继续作为下一层节点向上搜索；
4. 搜索过程中，如果某个调用方方法匹配到了缓存中的入口方法，则收集对应 API path；
5. 达到最大层级后停止；
6. 将所有命中的 API path 去重后展示。

## 4.2 数据流示意

```text
用户输入方法表达式
    ↓
解析为 serviceName + methodName (+ 可选参数信息)
    ↓
定位 PsiMethod
    ↓
先查直接绑定 API path
    ↓
按调用关系向上遍历 caller method
    ↓
遍历过程中用 caller 的 serviceName#methodName 匹配缓存入口方法
    ↓
汇总所有 API path
    ↓
结果展示 + 点击复制
```

## 5. 上钻层级设计

### 5.1 为什么需要层级上限

调用链向上搜索如果没有边界，会有以下问题：

1. 性能不可控：全项目 PSI 引用搜索成本较高；
2. 结果噪音大：层级过深后容易引入工具类、通用封装、测试代码；
3. 可能遇到环路或递归调用。

因此必须增加上钻层级上限。

### 5.2 层级建议值评估

结合常见服务调用层次：

```text
API入口 -> Facade/ThriftService -> ApplicationService -> DomainService -> Manager -> Helper
```

典型项目里，从底层业务方法回溯到 API 入口，常见深度通常在 **2 ~ 5 层**。

结论：

- **默认值建议：5**
- **配置范围建议：0 ~ 8**
- **实现硬上限建议：10**

说明：

- `0`：只查当前方法直接绑定的接口，不做上钻；
- `1 ~ 5`：满足大多数业务代码；
- `6 ~ 8`：作为复杂链路兜底；
- `> 8`：性能收益很小，噪音和耗时风险明显上升；
- 硬上限 `10`：防止误输入导致极端深搜。

因此，用户提出的“暂定 5”是合理的，建议作为默认值保留。

## 6. 交互设计

### 6.1 面板改造建议

在现有“方法反查接口地址”区域补充两个控件：

```text
方法：          [com.xxx.Service#doSomething]
上钻层级：      [5]
[查询接口地址]
结果：
- /api/order/create
  入口方法：com.foo.ApiOrderService#create
  命中方式：上钻第 3 层
```

更具体建议：

1. 保留当前 `METHOD_TEXT_FIELD`；
2. 新增 `JSpinner` 或轻量输入框：
   - 标签：`上钻层级：`
   - 默认值：`5`
   - 最小值：`0`
   - 最大值：`8`
3. 查询按钮保持一个即可，不额外拆两个按钮；
4. 查询结果建议从纯 `String path` 升级为结果对象展示，至少包含：
   - `path`
   - `entryMethod`
   - `depth`
   - `matchType`（直接命中 / 上钻命中）

### 6.2 结果展示建议

第一版仍建议使用 `JList`，但展示模型从 `String` 升级为结果 DTO，例如：

```java
ApiMethodSearchResultDTO
```

建议字段：

- `String path`
- `String entryServiceName`
- `String entryMethodName`
- `int depth`
- `boolean directMatch`

展示文案示例：

```text
/api/order/create
入口方法：com.foo.ApiOrderService#createOrder
命中方式：上钻第 2 层
```

点击任一结果：

- 复制 `path` 到剪贴板；
- 状态栏/提示文案更新为：`已复制接口地址：/api/order/create`

## 7. 方法定位设计

### 7.1 输入表达式兼容

继续保留当前兼容格式：

| 输入形式 | 示例 |
| --- | --- |
| `类全限定名#方法名` | `com.foo.UserService#getUser` |
| `类全限定名.方法名` | `com.foo.UserService.getUser` |
| 带括号签名 | `com.foo.UserService#getUser(Long)` |
| 前后空格/换行 | `  com.foo.UserService#getUser  ` |

### 7.2 重载方法处理

由于“上钻”必须依赖真实 `PsiMethod`，重载问题需要比第一版更谨慎。

建议策略：

1. **如果输入带参数签名**：
   - 尝试按方法名 + 参数个数 / 参数类型做精确匹配；
2. **如果输入不带参数签名，且存在多个同名重载**：
   - 合并查询所有同名 `PsiMethod`；
   - 在结果提示中说明：`检测到方法重载，已合并查询结果`。

这样既不阻断使用，也避免误只选中其中一个重载。

## 8. 调用链分析设计

### 8.1 搜索范围

建议范围：

- 仅限当前 `project`；
- 仅限 Java 方法；
- 跳过 library class、反编译依赖、测试代码（如项目现状允许再决定是否排除测试目录）。

### 8.2 遍历方式

建议采用 **BFS（广度优先）**：

优点：

1. 更容易记录“第几层命中”；
2. 更容易控制最大层级；
3. 当多个 API 入口都能到达目标方法时，先找到更近的入口。

伪流程：

```text
queue <- 目标方法集合(depth=0)
visited <- 空

while queue 非空:
    取出当前方法
    如果已访问则跳过
    标记已访问

    用当前方法匹配缓存入口方法
    若命中则收集 path

    如果当前 depth >= maxDepth:
        continue

    查找当前方法的所有调用方方法
    将调用方以 depth + 1 入队
```

### 8.3 去重与防环

必须同时做两层去重：

1. **方法节点去重**：避免环路、递归、重复遍历；
2. **结果 path 去重**：避免同一个接口通过不同调用链重复命中。

建议：

- 方法去重 key：`qualifiedClassName#methodName(parameterCount)`；
- 结果去重 key：`path`。

## 9. 服务层设计调整

### 9.1 SearchCacheManager 保持职责边界

`SearchCacheManager` 继续负责：

- 缓存读取；
- path 索引；
- method 索引；
- 直接绑定 API 查询。

不建议把 PSI 调用链搜索强塞进 `SearchCacheManager`，否则职责会混杂。

### 9.2 新增调用链查询服务

建议新增独立服务类，例如：

```text
src/main/java/com/augustlee/mt/toolWindow/mws/service/ApiMethodCallTraceService.java
```

职责：

1. 将方法表达式解析结果转换为 `PsiMethod`；
2. 向上搜索调用链；
3. 每一层结合 `SearchCacheManager` 做入口方法匹配；
4. 输出结果 DTO 列表供面板层展示。

这样可以保持：

- `SearchCacheManager`：缓存索引；
- `ApiMethodCallTraceService`：调用链分析；
- `ApiCacheSearchPanel`：UI 与交互。

## 10. 面板层设计调整

`ApiCacheSearchPanel` 需要补充：

1. `上钻层级` 输入控件；
2. 查询结果模型升级；
3. 新的查询提示文案，例如：
   - `共找到 4 个接口地址，其中 1 个直接命中，3 个来自上层调用链`
4. 当输入层级非法时提示：
   - `上钻层级必须在 0 ~ 8 之间`

## 11. 异常与边界场景

| 场景 | 处理建议 |
| --- | --- |
| 缓存为空 | 提示先刷新缓存 |
| 方法表达式非法 | 提示支持格式 |
| 方法无法定位到源码 | 提示未找到对应 Java 方法 |
| 存在同名重载 | 合并查询并提示 |
| 上钻层级为 0 | 仅查询直接绑定接口 |
| 上钻无命中 | 结果区清空并提示未找到可达接口 |
| 调用链过大 | 达到层级上限即停止 |
| 出现递归/环路 | 通过 visited 去重避免死循环 |

## 12. 文件修改建议

预计下一阶段需要修改：

1. `src/main/java/com/augustlee/mt/toolWindow/mws/panel/ApiCacheSearchPanel.java`
   - 增加上钻层级控件；
   - 升级结果展示结构；
   - 接入调用链查询服务。

2. `src/main/java/com/augustlee/mt/toolWindow/mws/service/SearchCacheManager.java`
   - 保持现有 method 反查索引；
   - 如有必要补充便捷查询方法，但不承担 PSI 搜索职责。

3. `src/main/java/com/augustlee/mt/toolWindow/mws/service/ApiMethodCallTraceService.java`
   - 新增，负责方法上钻分析。

4. 如结果模型需要单独承载展示信息：
   - `src/main/java/com/augustlee/mt/toolWindow/mws/dto/ApiMethodSearchResultDTO.java`

## 13. 验收标准补充

除当前已完成能力外，新增验收项：

1. 查询一个底层方法时，若它被某 API 入口方法间接调用，则能展示该 API path；
2. 多条上层调用链最终指向多个 API 入口时，能全部展示；
3. 层级为 `0` 时，行为退化为当前“直接方法反查”；
4. 层级为 `5` 时，常见业务链路可正常命中；
5. 查询结果能区分直接命中与上钻命中；
6. 点击结果仍然可复制 API path；
7. Gradle 编译通过。

## 14. 风险与取舍

### 14.1 性能风险

PSI 调用链搜索明显重于单纯查缓存，因此必须限制：

1. 搜索范围；
2. 上钻层级；
3. 节点去重。

### 14.2 准确性风险

以下情况可能导致结果偏宽：

1. 方法重载；
2. 多实现类；
3. 通用工具类被大量调用；
4. 同名方法但业务语义不同。

因此第一版应优先保证“可用 + 可控”，不追求复杂静态分析完美精确。

### 14.3 实现复杂度取舍

不建议第一版就做：

1. 完整调用链路径可视化；
2. 调用图树形展开；
3. 跨模块跨语言调用；
4. 自定义复杂过滤器。

先把“给定底层方法，找出所有可能归属的 API 入口地址”做实用化即可。
