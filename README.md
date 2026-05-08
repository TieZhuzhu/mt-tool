# 美团工具集

美团开发者工具集（HiShop 专用）。

## 功能概览

- API 查询工具
  - 支持通过 API path 跳转到对应 Java 方法
  - 支持刷新 API Search Cache 本地缓存
  - 支持通过 Java 方法全限定名反查 API path
  - 支持沿方法调用链向上钻取并反查可达的 API path
  - 支持点击反查结果直接复制 API path
  - 支持 API 搜索自动补齐前导 `/`
  - 支持自动获取 Cookie
- Maven 查询工具
  - 支持快捷跳转 ART 组件仓库
  - 支持自动获取 Cookie
- Other Dev Tools
  - 支持 ST 菜单转换工具
  - 支持结果键名点击复制
  - 支持快捷跳转 Lion 配置中心

## API Search Cache 新能力

在 `API Search Cache` 选项卡中，刷新缓存后除了可以按 API path 跳转代码，现在还可以按方法反查接口地址，并支持沿调用链向上钻取，找出所有可达的 API 入口地址。

支持的输入格式：

- `com.foo.Service#methodName`
- `com.foo.Service.methodName`
- `com.foo.Service#methodName(Request)`

使用方式：

1. 点击 `刷新缓存` 拉取最新 API Search Cache。
2. 在 `方法` 输入框中输入方法表达式。
3. 按需设置 `上钻层级`，默认值为 `5`。
4. 点击 `查询接口地址` 查询。
5. 在结果列表中查看：
   - 接口地址
   - 入口方法
   - 命中方式（直接命中 / 上钻第 N 层命中）
6. 点击任意一条结果可直接复制 API path。

补充说明：

- 当 API 搜索输入未带前导 `/` 时，系统会自动补齐后再执行搜索；
- 当方法存在多重调用链时，会汇总展示所有命中的 API path；
- 当输入未显式指定参数签名时，会合并同名重载方法的查询结果。
