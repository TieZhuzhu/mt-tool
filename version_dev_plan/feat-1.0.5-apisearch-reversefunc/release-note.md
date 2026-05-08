# 发布说明 / 开发日志

## 版本

- `1.0.5-SNAPSHOT`

## 已完成能力

### 1. 方法反查接口地址

- `API Search Cache` 支持通过 Java 方法全限定名反查 API path；
- 支持输入 `Class#method`、`Class.method` 以及带参数签名的方法表达式；
- 支持通过 IntelliJ PSI 沿方法调用链向上钻取，汇总所有可达的 API path；
- 支持配置上钻层级，默认值为 `5`，UI 可配置范围为 `0 ~ 8`，代码内部保留硬上限保护；
- 查询结果展示接口地址、入口方法、命中方式（直接命中 / 上钻命中）；
- 查询结果支持点击直接复制 API path。

### 2. API 搜索输入归一化

- `API Search Cache` 的 API 搜索支持自动识别前导 `/`；
- 当用户输入未带 `/` 的接口地址时，系统自动补齐为标准 path；
- 补齐后的 path 会回写到输入框中，便于确认实际查询内容。

## 已同步文档

- 已同步更新：
  - `api-search-cache-method-reverse-design.md`
  - `api-search-cache-method-reverse-plan.md`
  - `release-note.md`

## 本次实现补充

- 新增 `ApiMethodCallTraceService`，将调用链上钻逻辑从缓存服务中拆分出来；
- 新增 `ApiMethodQueryDTO` 与 `ApiMethodSearchResultDTO`，分别承载查询条件与结果展示信息；
- 方法输入带参数签名时，优先按参数匹配重载方法；
- 方法输入未带参数签名时，合并同名重载方法的查询结果。

## 兼容性说明

- 保留原有按 API path 跳转 Java 方法的能力；
- 保留当前方法全限定名直接反查 API path 的能力；
- 不修改本地缓存持久化结构，继续使用现有 `ApiPathState#apiPathJson`；
- 方法上钻能力基于 IDE PSI 调用关系分析实现，不引入新的远程接口依赖。
