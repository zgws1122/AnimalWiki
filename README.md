# AnimalWiki - 动物科普App

面向大众的动物科普应用，帮助用户了解各类动物的基本信息、生活习性、保护现状等科学知识，提升公众的动物保护意识。

## 技术栈

- **语言**: Kotlin
- **UI**: Jetpack Compose + Material Design 3
- **架构**: MVVM
- **本地存储**: Room (SQLite)
- **异步**: Kotlin Coroutines + Flow
- **导航**: Navigation Component
- **依赖注入**: Hilt

## MVP 功能

1. **用户登录/注册** - 用户名+密码注册登录，登录状态本地持久化
2. **动物分类浏览** - 按分类（哺乳动物、鸟类、爬行类等）浏览动物列表
3. **动物详情查看** - 展示动物的完整信息（名称、分类、习性、保护等级等）
4. **搜索功能** - 关键词搜索动物，搜索历史记录
5. **收藏功能** - 收藏/取消收藏动物，查看收藏列表

## 数据实体

| 实体 | 说明 |
|------|------|
| User | 用户信息（用户名、密码、昵称、头像） |
| Animal | 动物百科数据（名称、分类、习性、保护等级等） |
| Favorite | 用户收藏记录（用户ID + 动物ID） |
| SearchHistory | 搜索历史记录 |

## 项目结构

```
app/
├── data/              # 数据层
│   ├── local/         # Room 数据库、SharedPreferences
│   └── repository/    # 数据仓库
├── ui/                # 界面层
│   ├── home/          # 首页
│   ├── category/      # 分类
│   ├── search/        # 搜索
│   ├── detail/        # 详情
│   ├── profile/       # 我的
│   └── common/        # 公共组件
└── utils/             # 工具类
```

## 环境要求

- Android Studio Hedgehog 或更高版本
- JDK 11+
- Android SDK 36
- 最低支持 Android 8.0 (API 26)
