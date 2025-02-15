![Crafting Dead Logo](./image/crafting-dead-logo.png)  

[现在是简体中文] [Change to English](./README_en.md)  

# Preface

> 这是行尸走肉模组的一个fork，旨在给中国玩家带来本地化体验


Crafting Dead起源于Minecraft 1.5的枪械模组, Crafting Dead（以下称为行尸走肉）已经存在多年了。它是以F3RULLO的枪械Mod为基础创建的, 作为Minecraft中的第一批枪械Mod之一，它一直在发展, 不断地增加了内容和功能。

行尸走肉模组增加了大量的生物和枪支, 以及装饰品和医疗用品. 枪械自定义性强, 您可以使用自定义配件和喷漆来增强枪械的美观和实用性；具有精确的命中检测，延迟补偿和射击反馈。包括急救箱, 肾上腺素和绷带等医疗用品，得以从战斗中快速逃离，攻击和恢复。

# Discord
![Discord Banner 2](https://discordapp.com/api/guilds/473735245636698153/widget.png?style=banner2)

# Contributing
我们欢迎提交Pull Requests, 但请遵守以下准则：
* 使用 [Google style guide](https://github.com/google/styleguide)
* 在适当的地方添加`@Override` 以及注解,这样可以使代码更加整洁，方便维护与多人协作
* 将半模糊的变量(例如p_77624_1_)重命名为有意义的名称
* 打开编译器警告并解决所有这些问题(原始类型(raw types)，资源泄漏(resource leaks)，未使用的导入(unused imports)，未使用的变量等(unused variables))。
* 使用关键字尽可能使代码清晰/易读，并避免含糊不清的的命名冲突(可能不被注意到)
* 在if语句周围使用花括号，使它们更清晰，更容易展开，例如`{}`。
```java
if(foo) {
  bar();
}
```
而不是
```java
if(foo)
  bar();
```

* Names of fields being used as constants should be all upper-case, with underscores separating words. The following are considered to be constants:
1. All `static final` primitive types (Remember that all interface fields are inherently static final).
2. All `static final` object reference types that are never followed by "`.`" (dot).
3. All `static final` arrays that are never followed by "`[`" (opening square bracket).

Examples of constants:
`MIN_VALUE, MAX_BUFFER_SIZE, OPTIONS_FILE_NAME`

Examples of non-constants:
`logger, clientConfig`

# License
Crafting Dead is licensed under LICENSE.txt for more information. You may use Crafting Dead in modpacks, reviews or any other medium as long as you obide by the terms of the license. Commercial use of the mod must be authorised by NEXUSNODE Directors (Brad Hunter).

Commercial use is any reproduction or purpose that is marketed, promoted, or sold and incorporates a financial transaction. You can use Crafting Dead for personal use, To host a server for friends for example, But not to use the mod or mod pack in any way that gives you a financial advantage, neither can you use or modify the code and re-sell it which will allow others to gain a financial advantage. Any use of the Crafting Dead mod other than within the official Crafting Dead Mod pack is for personal use, none of our work or assets can be used in any way that will allow the person commercial advantage or monetary compensation.

Any Youtubers or Twitch streamers are welcome to create videos using our mods and monetize them, However you will need to ensure that you link the official pack/mod in the video description. You are welcome to create your own pack using this mod without authrorisation as long as it is soley used as a private server and your finantial advantage comes soley from video monetization. 

Please contact brad@nexusnode.com or Brad#8888 on Discord if you have any questions or concerns. Commercial use through the official modpack is granted (Such as adding servers through the official modpack) anything that falls outside of the official modpack requires authrorisation. 

# Credit
- NEXUSNODE
- CakeBrains
- Sm0keySa1m0n
- Arzio
- F3RULLO14

# YourKit
YourKit supports open source projects with innovative and intelligent tools
for monitoring and profiling Java and .NET applications.
YourKit is the creator of [YourKit Java Profiler](https://www.yourkit.com/java/profiler/), [YourKit .NET Profiler](https://www.yourkit.com/.net/profiler/), and [YourKit YouMonitor](https://www.yourkit.com/youmonitor/).

![YourKit Logo](https://www.yourkit.com/images/yklogo.png)
