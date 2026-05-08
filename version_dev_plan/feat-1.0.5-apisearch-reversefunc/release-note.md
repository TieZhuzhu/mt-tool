# 发布说明

## 版本

- `1.0.5-SNAPSHOT`

## 新增能力

- `API Search Cache` 支持通过 Java 方法全限定名反查 API path
- 支持输入 `Class#method`、`Class.method` 以及带参数签名的方法表达式
- 查询结果支持点击直接复制 API path

## 兼容性说明

- 保留原有按 API path 跳转 Java 方法的能力
- 不修改本地缓存持久化结构，继续使用现有 `ApiPathState#apiPathJson`
