# ecommerce-oms — Claude Code 项目级上下文

> 本项目对应《xr-knowledge-suite》第 28-35 章「电商订单系统」实战案例。

## 项目定位

中小企业电商订单与库存管理系统。员工创建订单、管理商品、监控库存、协同供应商。用于演示用 Claude Code 完成一个**生产级、Spring Boot 3.3 + React 18 全栈项目**的完整开发流程。

## 技术栈与版本（严格按以下版本）

| 类别 | 选型 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.3.x |
| 后端语言 | Java | 21 LTS |
| 数据库迁移 | Flyway | 10.x |
| 数据库 | PostgreSQL | 16 |
| ORM | Spring Data JPA + Hibernate | 6.x |
| 前端框架 | React | 18.x |
| 构建工具 | Vite | 5.x |
| 前端语言 | TypeScript | 5.x |
| 容器编排 | Docker Compose | v2 |
| API 文档 | springdoc-openapi | 2.x |

## 目录结构

```
ecommerce-oms/
├── docker-compose.yml             ← 三服务编排（postgres:16-alpine + backend + frontend）
├── .env.example
├── CLAUDE.md                     ← 本文件
├── backend/
│   ├── pom.xml                   ← Maven 配置，Spring Boot 3.3.0, Java 21
│   ├── Dockerfile
│   ├── src/main/java/com/zcqiand/ecommerce/
│   │   ├── EcommerceApplication.java
│   │   ├── controller/
│   │   │   ├── ProductController.java
│   │   │   ├── OrderController.java
│   │   │   ├── InventoryController.java
│   │   │   ├── SupplierController.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── service/
│   │   │   ├── ProductService.java
│   │   │   ├── OrderService.java
│   │   │   └── InventoryService.java
│   │   ├── repository/
│   │   │   ├── ProductRepository.java
│   │   │   ├── OrderRepository.java
│   │   │   └── InventoryRepository.java
│   │   ├── entity/
│   │   │   ├── Product.java
│   │   │   ├── Inventory.java
│   │   │   ├── Order.java
│   │   │   ├── OrderItem.java
│   │   │   ├── Supplier.java
│   │   │   ├── User.java
│   │   │   └── OrderStatus.java（enum）
│   │   ├── dto/
│   │   │   ├── CreateOrderRequest.java
│   │   │   └── ApiResponse.java
│   │   └── exception/
│   │       ├── BusinessException.java
│   │       └── ResourceNotFoundException.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   │       ├── V1__init_schema.sql
│   │       └── V2__seed_data.sql
│   └── src/test/java/com/zcqiand/ecommerce/
│       └── controller/
│           └── ProductControllerTest.java
├── frontend/
│   ├── package.json
│   ├── Dockerfile
│   ├── vite.config.ts
│   ├── tsconfig.json
│   ├── src/
│   │   ├── App.tsx
│   │   ├── main.tsx
│   │   ├── api/
│   │   │   └── client.ts
│   │   └── pages/
│   │       ├── ProductList.tsx
│   │       ├── OrderList.tsx
│   │       └── OrderCreate.tsx
│   └── index.html
└── .github/workflows/
    └── ci.yml                    ← CI/CD 双 job（backend mvn verify + frontend npm run build）
```

## 编码约定

- **零伪代码**：禁止 `// TODO`、`throw new UnsupportedOperationException()`、`return null;` 占位。
- **数据库迁移走 Flyway**：禁止用 `ddl-auto: update`，所有 schema 变更必须以新的 Flyway 迁移文件登记。
- **API 必须有 OpenAPI 注解**：所有 Controller 方法必须有 `@Operation` + `@ApiResponse` 注解。
- **前后端接口契约同步**：后端改 DTO，前端必须同步改对应 TypeScript 类型。
- **环境变量配置**：所有敏感信息走 `.env`，禁止硬编码进代码。
- **使用 jakarta.***：Spring Boot 3.x 使用 jakarta.* 而非 javax.*

## 危险操作

以下操作需要用户二次确认：

- 删除 Flyway 已应用的迁移文件
- 修改 `application.yml` 中的数据库连接配置
- `docker compose down -v`（会删除 postgres volume）

## 业务规则

- 订单状态流转：`DRAFT → SUBMITTED → PAID → SHIPPED → COMPLETED / CANCELLED`
- 金额 < 1000 元：直属经理审批（一级）
- 金额 ≥ 1000 元：部门经理 + 财务总监两级审批
- 下单时库存自动锁定，支付成功扣减，支付失败/取消释放
- 拒绝必须填写理由

## 与 xr-know-003 的关系

| 章节 | 本仓库对应 |
|------|----------|
| 第 28 章 项目规划 | 本文件 + `docker-compose.yml` |
| 第 29-30 章 数据库与 API | `backend/src/main/java/...` + `db/migration/` |
| 第 31-32 章 前端与 UI | `frontend/src/pages/` |
| 第 33 章 库存与订单联动 | `backend/service/OrderService.java` + `InventoryService.java` |
| 第 34 章 审批流 | `backend/entity/Order.java` 状态机 + `OrderService` 审批逻辑 |
| 第 35 章 CI/CD | `.github/workflows/ci.yml`（backend + frontend 双 job） |
