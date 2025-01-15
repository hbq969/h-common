# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
