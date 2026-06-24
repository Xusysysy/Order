# Order

酒吧/餐厅点单管理 App — 精简单机版。

## 功能

- **菜单浏览**：调酒、饮料、小食分类筛选，含配方步骤/原料查看
- **点单下单**：选桌 → 点菜品 → 调整数量 → 结账
- **员工编辑**：点击"编辑"按钮切换编辑模式，增删改菜品/桌位/配方
- **订单管理**：当前订单 + 历史订单明细，支持结账
- **自适应布局**：平板单页双栏（菜单+账单并排），手机底部导航双页
- **桌号记忆**：记住上次选择的桌位，启动自动恢复

## 与原项目

本项目从 [Orderpp](https://github.com/Xusysysy/Orderpp.git) 精简而来，移除了：
- 局域网同步（Ktor + NSD）
- 客人/员工双角色系统（合并为编辑模式切换）
- 设置页、主题切换
- 多 ViewModel 拆分（合并为单一 MainViewModel）

## 技术栈

- Kotlin + Jetpack Compose + Material3
- SQLite（SQLiteOpenHelper，非 Room）
- MVVM 架构

## 构建

### 环境要求

- Windows 系统
- Android Studio
- JDK 21（路径：`D:\software\AndroidStudio\jbr`）

### 编译

```powershell
$env:JAVA_HOME = "D:\software\AndroidStudio\jbr"
.\gradlew.bat assembleDebug
```

输出 APK: `app\build\outputs\apk\debug\app-debug.apk`

## 项目结构

详见 [STRUCTURE.md](STRUCTURE.md)

## License

MIT
