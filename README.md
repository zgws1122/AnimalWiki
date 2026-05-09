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
| Animal | 动物百科数据（中文名、英文名、分类、体型、分布区域、栖息地、食性、保护等级、图片） |
| Favorite | 用户收藏记录（用户ID + 动物ID） |
| SearchHistory | 搜索历史记录 |

**User 表结构：**

| 类型 | 字段 | 约束 | 说明 |
|------|------|------|------|
| Long | id | PK, AUTOINCREMENT | 主键 - 用户 ID |
| String | username | NOT NULL | 用户名 |
| String | password | NOT NULL | 密码 |
| String | nickname |  | 昵称 |
| String | avatar |  | 头像 URL |
| Long | createTime | NOT NULL | 创建时间 |

唯一索引：id

**Animal 表结构：**

| 类型 | 字段 | 约束 | 说明 |
|------|------|------|------|
| Long | id | PK, AUTOINCREMENT | 主键 - 动物 ID |
| String | name | NOT NULL | 动物中文名 |
| String | englishName |  | 动物英文名 |
| String | category |  | 动物分类 |
| String | body |  | 动物体型 |
| String | distribution |  | 动物分布区域 |
| String | habitat |  | 动物栖息地 |
| String | diet |  | 动物食性 |
| String | protectionLevel |  | 动物保护等级 |
| String | image |  | 动物图片 URL |

**Favorite 表结构：**

| 类型 | 字段 | 约束 | 说明 |
|------|------|------|------|
| Long | userId | PK, FK | 外键 - 用户 ID |
| Long | animalId | PK, FK | 外键 - 动物 ID |
| Long | createTime | NOT NULL | 收藏时间 |

主键：(userId, animalId)
外键：
userId → User(id)
animalId → Animal(id)

**SearchHistory 表结构：**
| 类型 | 字段 | 约束 | 说明 |
|------|------|------|------|
| Long | id | PK | 主键 - 搜索记录 ID |
| Long | userId | FK | 外键 - 用户 ID |
| String | keyword | NOT NULL | 搜索关键词 |
| Long | createTime | NOT NULL | 搜索时间 |

唯一索引：(userId, keyword)
外键：
userId → User(id)

### ER图
<img width="1088" height="649" alt="屏幕截图 2026-05-09 220157" src="https://github.com/user-attachments/assets/8bbaa4e8-2bb4-4793-b083-906972b9d583" />


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
- 最低支持 Android 7.0 (API 24)
