# 版本记录

该项目的所有显著变更都将记录在此文件中。
格式基于“保留变更日志”，并且该项目遵循语义版本控制。

## 2022-12-10
### Features
- 扩展Spring Cache支持失效和自定义KEY
- 多数据库源动态切换，策略可动态指定
- 通用门面模式扩展代码
- restful接口加解密辅助代码，支持aes、rsa
- 基于redis的分布式锁辅助代码
- 优化扩展mybatis Mapper接口扫描器,支持只扫@Mapper接口
- 规则匹配引擎代码
- AOP扩展，日志、异常、接口限流
- 通用Feign代理模版类
- 基于配置的mvc拦截器和handler

## 2025-01-14
### Features
- 增加Swagger2Controller的安全拦截器

## 2025-01-17
### Features
- 接口操作日志记录，提供缺省的采集策略实现DefaultLogCollect, DefaultLogModelDefProvider


## 2025-01-20
### Features
- 增加common包部分实用接口的页面功能，增加使用体验