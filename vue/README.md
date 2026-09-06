# 自习室座位预约系统（Seat Reservation System）

基于 **Spring Boot + Vue2** 的前后端分离自习室座位预约系统，支持 **学生 / 教师 / 管理员** 三种角色。系统提供区域选择、座位预约、扫码签到、暂离管理、信用分奖惩、违规记录、公告与论坛等完整功能，并通过定时任务自动处理超时未签到、暂离超时等异常场景。

## 项目预览

| 模块 | 说明 |
| --- | --- |
| 学生端 | 区域选择 → 座位预约 → 签到 → 使用中（支持暂离）→ 结束释放座位 |
| 教师端 | 查看预约记录、管理座位信息、对学生进行信用分加/扣分 |
| 管理端 | 公告、论坛、座位、学生、教师、违规、签到记录、信用日志、数据统计 |

## 技术栈

### 前端（vue 目录）
- Vue 2.6 + Vue Router 3（history 模式）
- Element UI 2.15（PC 端管理界面）
- Vant 2.12（移动端 H5 界面）
- Axios 0.27（统一封装，BaseURL `/api`，60s 超时）
- 开发端口：`3000`，通过 devServer 代理 `/api` → `http://localhost:9003`

### 后端（springboot 目录）
- Spring Boot 2.7.0 / Java 17
- MyBatis 2.2.2 + MySQL 8（`mysql-connector-java`）
- Redis 6379（辅助缓存）
- 服务端口：`9003`
- `TimerTask` 定时/延迟任务：预约到期自动恢复座位、超时未签到自动判违规、暂离超时处理

### 数据库
- 数据库名：`seatreservation`（utf8mb4），初始化脚本见根目录 [seat.sql](../seat.sql)

| 表名 | 说明 |
| --- | --- |
| `user` | 用户（学生/教师/管理员）信息、信用分 |
| `area` | 自习区域（区域名、缩写） |
| `seat` | 座位（状态：空闲/占用等） |
| `reservation` | 预约记录（0 待签到 / 1 使用中 / 2 未及时签到 / 3 暂离 / 4 暂离超时 / -1 完成） |
| `sign_record` | 签到记录 |
| `violation` | 违规记录 |
| `credit_log` | 信用分变动日志 |
| `announce` | 公告 |
| `article` | 论坛帖子 |
| `comments` | 帖子评论 |

## 功能模块

### 学生端
- 登录 / 注册
- 区域选择、查看区域座位图
- 座位预约、取消预约
- 到场签到（超时未签到自动判违规并扣信用分）
- 使用中状态管理：暂离、暂离超时处理、结束使用自动释放座位
- 信用分查询
- 论坛：发帖、评论、查看公告

### 教师端
- 区域选择与预约记录查看
- 座位信息管理
- 预约管理：查看需评分预约、删除违规预约
- 信用分管理：扣分 / 加分

### 管理员端
- 公告管理（发布 / 删除）
- 论坛管理（帖子删除）
- 座位管理：座位号查看、座位增删改、运动区域座位配置
- 学生管理、教师管理、密码重置
- 数据统计
- 违规记录、签到记录、信用日志查询

## 快速开始

### 环境要求
- JDK 17+
- Node.js（建议 14~16，配合 Vue CLI 4）
- MySQL 8.x
- Redis（默认 `localhost:6379`）

### 1. 初始化数据库
使用 MySQL 执行根目录下的初始化脚本：

```sql
source seat.sql;
```

脚本会自动创建 `seatreservation` 数据库及全部表结构和测试数据。

### 2. 启动后端
修改 `springboot/src/main/resources/application.yaml` 中的 MySQL / Redis 密码后，运行：

```
SpringSeatReservationApplication  # Spring Boot 启动类
```

后端默认监听 `http://localhost:9003`。

### 3. 启动前端
```bash
cd vue
npm install
npm run serve
```

前端默认运行在 `http://localhost:3000`，`/api` 请求自动代理至后端 `9003` 端口。

### Compiles and minifies for production
```
npm run build
```

## 测试账号

| 角色 | 账号 | 密码 |
| --- | --- | --- |
| 管理员 | 666 | 123456789 |
| 教师 | 999 | 123456789 |
| 学生 | 123 / 124 / 126 / 130 / 131 / 135 | 123456789 |

## API 概览

| 模块 | 前缀 | 主要接口 |
| --- | --- | --- |
| 公共 | `/public` | 登录、注册、修改密码、获取区域、获取区域座位、已签到人数 |
| 学生 | `/user` | 添加/取消预约、签到、暂离、查询预约与信用分 |
| 教师 | `/teacher` | 查询预约、待评分预约、加分/扣分、删除预约 |
| 管理员 | `/admin` | 公告、座位、用户管理、统计、违规/签到/信用日志 |
| 论坛 | `/forum` | 帖子与评论的发布、查询，公告查询 |

## 项目结构

```
SpringSeatReservationApplication
├── springboot                  # 后端 Spring Boot 工程
│   └── src/main/java/com/example/spring_seatreservation
│       ├── Bean                # 实体类
│       ├── config              # 配置类
│       ├── controller          # 控制器（user/teacher/admin/public/forum）
│       ├── mapper              # MyBatis 数据访问层
│       └── Other               # 工具/其他
├── vue                         # 前端 Vue 工程
│   └── src
│       ├── Guide               # 登录 / 注册
│       ├── user                # 学生端页面
│       ├── teacher             # 教师端页面
│       ├── admin               # 管理端页面
│       ├── router              # 路由
│       └── req.js              # axios 封装
├── seat.sql                    # 数据库初始化脚本
└── database_erd.drawio         # 数据库 ER 图
```
