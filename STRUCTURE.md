# Order 项目架构

## 项目目标

酒吧/餐厅点单管理精简版 App。客人点单 + 员工编辑合并在同一界面，通过"编辑/保存"按钮切换模式。无网络功能，纯本地单机运行。支持平板（单页双栏）和手机（双页导航）自适应布局。

## 与原项目 Orderpp 的差异

| 特性 | Orderpp | Order |
|------|---------|-------|
| 局域网同步 | 支持 (Ktor + NSD) | **移除** |
| 角色系统 | 客人/员工分离 + PIN | **合并**，编辑按钮切换 |
| ViewModel | 5 个独立 ViewModel | **1 个** MainViewModel |
| 导航 | 3 Tab (菜单/账单/我的) | 平板单页 / 手机 2 页 |
| 设置页 | 独立 SettingsSheet | **移除** |
| 配方弹窗 | ModalBottomSheet | 保留，编辑模式下可编辑 |
| 桌位选择 | 抽屉 | 平板下拉菜单 / 手机侧滑抽屉 |

## 技术栈

- **UI**: Jetpack Compose + Material3
- **数据库**: 原生 SQLiteOpenHelper（非 Room），版本 2
- **架构**: MVVM (ViewModel → Repository → DAO)
- **最低 SDK**: 24，目标 SDK: 36
- **Java**: JDK 21 (`D:\software\AndroidStudio\jbr`)
- **依赖**: 无 Ktor，无网络库

## 项目结构

```
app/src/main/java/com/order/app/
├── OrderApp.kt                     # Application，初始化 SQLite + 插入预置数据
├── MainActivity.kt                 # 入口 Activity，Loading → Error → MainScreen
│
├── data/
│   ├── db/
│   │   ├── DatabaseHelper.kt       # SQLiteOpenHelper v2（6 张表 + 迁移）
│   │   ├── entity/
│   │   │   └── Entities.kt         # 数据实体：Table / MenuItem / Order / OrderItem / RecipeStep / RecipeIngredient
│   │   └── dao/
│   │       ├── TableDao.kt         # 桌位 CRUD + 拖拽排序
│   │       ├── MenuItemDao.kt      # 菜单 CRUD + 分类查询 + 拖拽排序
│   │       ├── RecipeDao.kt        # 配方步骤/原料增删查
│   │       └── OrderDao.kt         # 订单 CRUD + OrderWithItems / OrderBill 关联查询
│   ├── preset/
│   │   └── PresetRecipes.kt       # 15 款调酒 + 5 饮料 + 4 小食预置数据
│   └── repository/
│       ├── TableRepository.kt     # 桌位业务逻辑（含自动创建默认 10 桌）
│       ├── MenuRepository.kt      # 菜单+配方业务逻辑
│       └── OrderRepository.kt     # 订单业务逻辑（含同菜品叠加数量）
│
├── viewmodel/
│   ├── MainViewModel.kt           # 核心 ViewModel：编辑模式 + 桌位 + 菜单 + 订单 + 配方 + 自动刷新
│   └── MainViewModelFactory.kt    # ViewModel 工厂（注入 DatabaseHelper + SharedPreferences）
│
├── ui/
│   ├── theme/
│   │   ├── Color.kt               # 暗色主题色板（琥珀主色/绿辅色/红错误色）
│   │   ├── Type.kt                # 字体排版
│   │   ├── Theme.kt               # Material3 暗/亮双主题
│   │   └── PreviewUtils.kt        # Compose 预览工具包装器
│   ├── screen/
│   │   ├── MainScreen.kt          # 主界面：平板 TabletLayout + 手机 Scaffold + Pager
│   │   │                           #   内含：TabletMenuPanel / TabletBillPanel
│   │   │                           #         PhoneMenuPanel / PhoneBillPanel / PhoneTableDrawer
│   │   │                           #         MenuItemEditDialog（增删改菜品弹窗）
│   │   │                           #         添加桌位弹窗 / 桌位选择 DrowpdownMenu
│   │   └── RecipeSheet.kt         # ModalBottomSheet：查看/编辑配方（原料+步骤）
│   └── component/
│       ├── MenuCard.kt            # 菜单项卡片（名称/价格/"+"/配方链接）
│       ├── TableChip.kt           # 桌位标签（名称 + 状态圆点 + 选中高亮）
│       └── QuantityStepper.kt     # 数量加减步进器（− 数量 +）
│
res/values/
├── strings.xml                    # app_name = "Order"
└── themes.xml                     # 原生主题（statusBar/navBar 黑色）
```

## 核心数据流

```
用户操作 → Composable UI
  → collectAsStateWithLifecycle() ← MainViewModel (StateFlow)
    → Repository (suspend)
      → DAO (suspend / 手动 Flow)
        → SQLiteOpenHelper → SQLite 数据库
```

无网络同步，全部操作直接读写本地 SQLite。

## 导航流程

```
App 启动 → Loading → MainScreen

=== 平板 (≥600dp) ===
  单页双栏布局（无 BottomBar）
  ┌──────────────────────┬──────────────┐
  │    左侧 Menu (2/3)    │  右侧 Bill   │
  │  [桌位选择 ▼]         │  (1/3)       │
  │  [分类 FilterChip]    │  当前订单     │
  │  [菜单网格]           │  所有订单列表 │
  └──────────────────────┴──────────────┘

=== 手机 (<600dp) ===
  TopAppBar: "点餐"/"账单" + [编辑/保存]
  ┌────────────────┐
  │   Pager 页面    │
  │  0: 菜单页      │
  │  [桌位 ▸]       │
  │  [订单摘要条]   │
  │  [分类/菜单网格] │
  │  1: 账单页      │
  │  当前订单+所有订单 │
  ├────────────────┤
  │ NavigationBar   │
  │ [🍽 菜单] [📋 账单] │
  └────────────────┘
  桌位抽屉：左侧滑出（手机端侧滑覆盖层）

全局：
  配方弹窗（ModalBottomSheet，点击含配方菜单项触发）
  菜品编辑弹窗（AlertDialog，编辑模式下点击 ✏ 按钮触发）
  添加桌位弹窗（AlertDialog）
```

## 编辑模式行为

- **客人模式（默认）**：点击菜单项直接加入订单（无配方项弹出配方页）；订单可修改数量/结账；桌位可查看不可增删。
- **编辑模式（isEditing=true）**：顶部按钮变为"保存"；菜单项显示 ✏ 编辑按钮；出现"+ 添加菜品"按钮；桌位抽屉显示"+ 添加桌位"和"删除"按钮；配方页可编辑原料和步骤；账单页可结账任意订单。
- **切换回客人模式（点击"保存"）**：重新从数据库加载菜单、桌位和订单数据。

## 数据库

| 表名 | 用途 | 关键列 |
|------|------|--------|
| `tables` | 桌位 | id, name, zone, status, sort_order |
| `menu_items` | 菜单项 | id, name, price, category, hasRecipe, sort_order |
| `recipe_steps` | 配方步骤 | id, menuItemId, stepNumber, description |
| `recipe_ingredients` | 配方原料 | id, menuItemId, name, amount, unit |
| `orders` | 订单 | id, tableId, status, createdAt |
| `order_items` | 订单项 | id, orderId, menuItemId, name, quantity, price |

数据库从 v1 迁移到 v2 时添加 `sort_order` 列（ALTER TABLE）。

## 数据持久化

- **桌位记住**: 选桌后保存到 SharedPreferences (`order_prefs` → `selected_table_id`)，下次启动自动恢复
- **预置数据一次性插入**: OrderApp 启动时检查 `menuDao.getById(1L)`，若为空则插入 15 款调酒 + 饮料 + 小食
- **拖拽排序**: 通过 `sort_order` 字段持久化

## 关键配置

| 文件 | 关键项 |
|------|--------|
| `gradle.properties` | `org.gradle.java.home=D\:/software/AndroidStudio/jbr` |
| `app/build.gradle.kts` | namespace=`com.order.app`, versionCode=1, minSdk=24, targetSdk=36 |
| `gradle/libs.versions.toml` | Kotlin 2.2.10, AGP 9.2.1, Compose BOM 2025.12.00 |
| `local.properties` | `sdk.dir=C\:\\Users\\20119\\AppData\\Local\\Android\\Sdk` |
| `order.keystore` | 签名密钥 (alias=Order, password=oder123) |

## 构建命令

```bash
$env:JAVA_HOME = "D:\software\AndroidStudio\jbr"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
./gradlew assembleDebug
```

## 与原项目的文件对照（便于迁移理解）

| Orderpp 源文件 | Order 对应 |
|---------------|-----------|
| `RoleViewModel.kt` + `PinHelper.kt` | **移除**（无角色系统） |
| `HostViewModel.kt` + `HostServer.kt` + `SyncClient.kt` + `DiscoveryService.kt` | **移除**（无网络） |
| `TableViewModel.kt` | 合并至 `MainViewModel.kt` |
| `MenuViewModel.kt` | 合并至 `MainViewModel.kt` |
| `OrderViewModel.kt` | 合并至 `MainViewModel.kt` |
| `RoleSelectScreen.kt` + `HostSetupScreen.kt` | **移除** |
| `SettingsSheet.kt` | **移除**（无设置页） |
| `OrderAppContent.kt` | 合并至 `MainActivity.kt` |
| `MainScreen.kt` (3 Tab) | 重写为平板双栏 + 手机 Pager |
| `TableListPane.kt` | 内联至 MainScreen（TabletMenuPanel / PhoneTableDrawer） |
| `MenuPane.kt` | 内联至 MainScreen（TabletMenuPanel / PhoneMenuPanel） |
| `OrderPane.kt` | 内联至 MainScreen（TabletBillPanel / PhoneBillPanel） |
| `TableDao` / `MenuItemDao` / `OrderDao` / `RecipeDao` | **不变**（数据层保持） |
| `TableRepository` / `MenuRepository` / `OrderRepository` | **不变**（逻辑层保持） |
| `MenuCard.kt` / `TableChip.kt` / `QuantityStepper.kt` | **不变**（组件保持） |
| `RecipeSheet.kt` | 增加编辑模式下的配方编辑功能 |
| `Entities.kt` / `DatabaseHelper.kt` / `PresetRecipes.kt` | **不变** |
