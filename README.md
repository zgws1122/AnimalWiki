# AnimalWiki - 动物科普App

面向大众的动物科普应用，帮助用户了解各类动物的基本信息、生活习性、保护现状等科学知识，提升公众的动物保护意识。

## 技术栈

- **语言**: Kotlin, Java 11 兼容
- **UI**: Jetpack Compose + Material Design 3（无 XML 布局）
- **架构**: MVVM（ViewModel + Repository）
- **本地存储**: Room (SQLite)
- **网络**: Retrofit + Gson + OkHttp
- **图片加载**: Coil Compose
- **异步**: Kotlin Coroutines + Flow
- **导航**: Navigation Compose
- **依赖注入**: 手动 DI（Application 类）
- **最低支持**: Android 7.0 (API 24)

## 功能列表

1. **用户登录/注册** — 密码经 Android Keystore + AES/GCM 加密存储，登录状态本地持久化
2. **首页轮播** — 顶部自动轮播 5 张动物卡片（2 秒切换，无限循环），下方为动物网格列表
3. **分类浏览** — 按类别（哺乳动物、鸟类、爬行动物、两栖动物、鱼类、昆虫、海洋动物）筛选浏览
4. **搜索功能** — 实时关键词搜索（中英文），搜索历史持久化，热门搜索标签
5. **动物详情** — 图片、名称、拉丁学名、保护等级、简介、栖息地、食性、体型、分布区域、分类学信息，支持双击图片放大查看，远程 API 补充物种描述
6. **收藏功能** — 一键收藏/取消收藏，查看收藏列表
7. **浏览历史** — 自动记录浏览记录，可查看历史列表
8. **个人中心** — 查看数据统计，修改昵称和头像（从相册选择）
9. **设置** — 深色模式切换、切换账号、退出登录
10. **深色模式** — 全局深色/浅色主题切换

## 数据实体

**AnimalEntity（动物表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 主键，动物 ID |
| name | String | 中文名（如"大熊猫"） |
| latinName | String | 拉丁学名（如"Ailuropoda melanoleuca"） |
| category | String | 分类（哺乳动物、鸟类、爬行动物、两栖动物、鱼类、昆虫、海洋动物） |
| description | String | 动物简介 |
| habitat | String | 栖息地（生态环境描述） |
| diet | String | 食性 |
| conservationStatus | String | 保护等级（如"濒危 (EN)"） |
| imageUrl | String? | 图片路径（本地 asset 或网络 URL） |
| taxonomy | String | 分类学信息（界→门→纲→目→科→属） |
| bodySize | String | 体型（体长、体重等） |
| distribution | String | 分布区域（地理范围） |

**UserEntity（用户表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 主键，自增 |
| username | String | 用户名（唯一索引） |
| encryptedPassword | String | 加密密码（Android Keystore + AES/GCM） |
| nickname | String | 昵称 |
| avatar | String? | 头像 URI（从相册选择） |
| createdAt | Long | 注册时间戳 |

**FavoriteEntity（收藏表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 主键，自增 |
| userId | Int | 用户 ID |
| animalId | Int | 动物 ID |

**HistoryEntity（浏览历史表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 主键，自增 |
| userId | Int | 用户 ID |
| animalId | Int | 动物 ID |
| timestamp | Long | 浏览时间戳 |

## 数据源

- **本地**: `assets/animals.json`（22 条动物数据，每启动同步到 Room）
- **远程**: 中国动物主题数据库 API（`http://zoology.especies.cn/`），详情页补充物种描述

## 项目结构

```
app/src/main/java/com/example/code/
├── App.kt                  # Application 类，手动 DI
├── MainActivity.kt         # 入口，NavHost + BottomNavigation
├── data/
│   ├── local/
│   │   ├── entity/         # Room 实体
│   │   ├── dao/            # Room DAO
│   │   ├── AppDatabase.kt
│   │   └── LocalDataSource.kt
│   ├── remote/
│   │   ├── ApiService.kt   # Retrofit 接口
│   │   └── dto/            # API 响应数据类
│   └── repository/         # AnimalRepository, UserRepository
├── ui/
│   ├── home/               # 首页（轮播 + 网格）
│   ├── category/           # 分类浏览
│   ├── search/             # 搜索页
│   ├── detail/             # 动物详情
│   ├── profile/            # 个人中心
│   ├── auth/               # 登录/注册
│   ├── favorite/           # 收藏列表
│   ├── history/            # 浏览历史
│   ├── settings/           # 设置
│   ├── common/             # 共享组件
│   └── theme/              # 主题
└── utils/                  # 工具类（密码加密等）
```

## 构建运行

```bash
cd code
./gradlew assembleDebug    # 构建 Debug APK
./gradlew installDebug     # 安装到设备/模拟器
./gradlew clean            # 清理构建产物
```

## 环境要求

- Android Studio Hedgehog 或更高版本
- JDK 11+
- Android SDK 36
- Gradle Kotlin DSL + Version Catalog
