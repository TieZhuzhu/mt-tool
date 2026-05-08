# 美团工具集

美团开发者工具集（HiShop 专用）。

## 功能概览

- API 查询工具
  - 支持通过 API path 跳转到对应 Java 方法
  - 支持刷新 API Search Cache 本地缓存
  - 支持通过 Java 方法全限定名反查 API path
  - 支持点击反查结果直接复制 API path
  - 支持自动获取 Cookie
- Maven 查询工具
  - 支持快捷跳转 ART 组件仓库
  - 支持自动获取 Cookie
- Other Dev Tools
  - 支持 ST 菜单转换工具
  - 支持结果键名点击复制
  - 支持快捷跳转 Lion 配置中心

## API Search Cache 新能力

在 `API Search Cache` 选项卡中，刷新缓存后除了可以按 API path 跳转代码，现在还可以按方法反查接口地址。

支持的输入格式：

- `com.foo.Service#methodName`
- `com.foo.Service.methodName`
- `com.foo.Service#methodName(Request)`

使用方式：

1. 点击 `Refresh` 刷新缓存。
2. 在 `Method` 输入框中输入方法表达式。
3. 点击 `Query API Path` 查询。
4. 在结果列表中点击任意一条 API path 直接复制。
