# AntiExplosion

这是一个Minecraft防爆插件, 允许保留爆炸伤害的同时阻止破坏方块

## 目录

- [AntiExplosion](#antiexplosion)
  - [目录](#目录)
  - [说明](#说明)
    - [项目背景](#项目背景)
  - [我们修改了什么?](#我们修改了什么)
  - [相关仓库与引用](#相关仓库与引用)
  - [贡献者](#贡献者)
    - [如何贡献](#如何贡献)
    - [特别鸣谢](#特别鸣谢)
  - [展望未来](#展望未来)

## 说明

### 项目背景

该项目fork自由 Sanityko 与 RaycusMX 开发的爆炸保护模组, 该项目由 MCZLF 团队在原项目的基础上进行了一些修改与拓展以适应本服的需求.

本项目的初始源代码是在由 Sanityko 授权后逆向而来, 可能初代构建物会与原项目发行的二进制文件hash有所不同, 但行为我们确保是一致的.

点击查看[原始插件发布页](https://www.mcbbs.net/forum.php?mod=viewthread&tid=501794 "点击查看")

## 我们修改了什么?

- 允许使用配置文件控制插件的生效范围与生效形式
  - 新增了配置文件, 允许自由的打开或关闭相关功能
  - 允许控制爆炸保护的范围, 允许设置某区域中本插件的行为

## 相关仓库与引用

- [bukkit](https://dev.bukkit.org) - 一个优秀的支持插件的Minecraft服务端
- [原始插件发布页](https://www.mcbbs.net/forum.php?mod=viewthread&tid=501794 "点击查看") - 本项目的原始发布地址

## 贡献者

[![Contributors](https://contrib.rocks/image?repo=MCZLF/plugin-AntiExplosion)](https://github.com/MCZLF/plugin-AntiExplosion/graphs/contributors)

### 如何贡献

非常欢迎你的加入！[提一个 Issue](https://github.com/MCZLF/plugin-AntiExplosion/issues/new) 或者提交一个 Pull Request。

标准 Readme 遵循 [Contributor Covenant](http://contributor-covenant.org/version/1/3/0/) 行为规范。

### 特别鸣谢

感谢[Sanityko](https://www.mcbbs.net/?577462) 与 [RaycusMX](https://www.mcbbs.net/?681175) 编写了本插件的第一个版本

## 展望未来

- [ ] 优化爆炸点搜索算法
