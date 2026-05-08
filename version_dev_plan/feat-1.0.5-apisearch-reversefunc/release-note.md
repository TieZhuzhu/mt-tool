# 发布说明 / 开发日志

## 版本

- `1.0.5-SNAPSHOT`

## 本次改造点

### 1. API Search Cache 新增“方法反查接口地址”能力

- 支持通过 Java 方法全限定名反查 API path；
- 支持常见方法表达方式：`Class#method`、`Class.method`、带参数签名的方法表达式；
- 支持基于 IntelliJ PSI 沿调用链向上钻取，汇总所有可达的 API path；
- 支持配置上钻层级，默认值为 `5`，UI 可配置范围为 `0 ~ 8`；
- 上钻时会同时处理当前方法、父接口方法、实现类方法，提升真实项目链路命中率；
- 查询结果支持展示接口地址、入口方法、命中方式（直接命中 / 上钻命中）。

### 2. API 搜索输入体验优化

- `API Search Cache` 的 API 搜索支持自动识别前导 `/`；
- 当用户输入未带 `/` 的接口地址时，系统自动补齐为标准 path；
- 补齐后的 path 会回写到输入框中，便于确认实际查询内容。

### 3. 方法反查结果展示与交互优化

- 结果区域由简单列表升级为表格展示，避免窗口宽度不足时结果高度被严重压缩；
- “入口方法”列默认展示短类名 `类名#方法名`，悬停 tooltip 展示完整全限定名；
- “命中方式”列增加直观区分：直接命中与上钻命中分别突出展示；
- 结果表格支持按钮操作：复制接口地址、跳转入口方法；
- 结果表格支持双击行直接跳转入口方法；
- 结果表格支持右键菜单：复制接口地址、跳转入口方法；
- 右键时自动选中当前行，减少误操作；
- 接口地址列宽保持固定；
- 命中方式列收窄；
- 入口方法列按可见区域自动扩展剩余宽度。

### 4. 插件兼容性调整

- 插件兼容区间调整为 `since-build=231`，不再显式设置 `until-build`；
- 允许 `231` 及以上 IDE 版本继续安装插件，避免本地导入时被 `231.*` 上限拦截。

## 已同步文档

- `E:/workspace/project/mt-tool/version_dev_plan/feat-1.0.5-apisearch-reversefunc/api-search-cache-method-reverse-design.md`
- `E:/workspace/project/mt-tool/version_dev_plan/feat-1.0.5-apisearch-reversefunc/api-search-cache-method-reverse-plan.md`
- `E:/workspace/project/mt-tool/version_dev_plan/feat-1.0.5-apisearch-reversefunc/release-note.md`
- `E:/workspace/project/mt-tool/README.md`
- `E:/workspace/project/mt-tool/src/main/resources/META-INF/plugin.xml`

## 兼容性说明

- 保留原有按 API path 跳转 Java 方法的能力；
- 保留本地 API 缓存刷新与持久化能力；
- 方法反查与方法上钻能力均基于 IDE PSI 分析，不引入新的远程依赖；
- 本次结果展示优化属于 UI / 交互增强，不影响原有缓存数据结构。
