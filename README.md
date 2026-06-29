# 电商订单与库存管理系统

> 《Codex 从入门到项目实践》第 28-35 章的可部署配套案例——电商订单与库存管理系统。

本仓库是书中讲解项目规划、数据库与 API、前端开发、库存与订单联动、审批流、CI/CD 等概念的**全功能实物载体**。

## 项目背景

为什么用「电商订单与库存管理系统」来配书？电商订单的复杂度刚好够用：

- **要素齐全**：库存状态机（下单锁/支付扣/取消释放）、多级审批（金额阈值触发不同审批流）、Flyway 数据库迁移、Spring Boot + React 全栈一应俱全；
- **循序渐进**：第 28 章建领域模型，第 29-30 章搭数据库与 API，第 31-32 章做前端 UI，第 33 章联动库存与订单，第 34 章补审批流，第 35 章接 CI/CD；
- **贴近实战**：中小企业电商订单管理是常见的业务场景，读者能快速理解领域逻辑，专注工程实现。

## 功能特性

### 业务功能

- 商品管理与库存查询（支持安全库存阈值预警）
- 订单创建与状态流转（DRAFT → SUBMITTED → APPROVED → PAID → SHIPPED → COMPLETED，含 REJECTED / CANCELLED 分支）
- 库存自动锁定/扣减/释放（下单锁库存，支付成功扣减，取消/拒绝/支付失败释放锁定）
- 多级审批流程（金额 < 1000 一级审批，≥ 1000 两级审批）
- 供应商管理与关联查询
- 数据初始化（2 个供应商 + 5 个商品 + 库存种子数据）

### 工程特性（教学要点）

- Spring Boot 3.3 + Java 21 LTS + Flyway 数据库迁移
- React 18 + Vite 5 + TypeScript 前端，REST API
- Docker Compose 一键拉起三服务（postgres:16-alpine + backend + frontend）
- 前后端双向 OpenAPI 注解（springdoc-openapi 2.x），接口契约同步
- CI/CD 双 job（backend `mvn verify` + frontend `npm run build`）

## 章节映射

### 《Codex 从入门到项目实践》（第 28-35 章）

| 章节 | 对应代码 |
| ---- | -------- |
| 第 28 章 项目规划与架构设计 | `CLAUDE.md` + `docker-compose.yml` |
| 第 29-30 章 数据库与 API 开发 | `backend/src/main/java/com/zcqiand/ecommerce/` + `db/migration/` |
| 第 31-32 章 前端与 UI 实现 | `frontend/src/pages/` |
| 第 33 章 库存与订单联动 | `backend/service/OrderService.java` + `InventoryService.java` |
| 第 34 章 审批流与多级审核 | `backend/entity/Order.java` + `OrderService` 审批逻辑 |
| 第 35 章 自动化测试与 CI/CD | `backend/src/test/` + `.github/workflows/ci.yml` |

## 快速开始

```bash
# 一键拉起三服务（postgres + backend + frontend）
docker compose up -d

# 访问
# - 后端 API 文档: http://localhost:8080/swagger-ui.html
# - 前端工作台:    http://localhost:5173
# - 数据库:         localhost:5432 (user: postgres / db: ecommerce)

# 关停
docker compose down
```

## 部署架构

```text
       浏览器
          │
          ▼
┌─────────────────┐
│  frontend:5173  │  React 18 + Vite
└────────┬────────┘
         │ REST
         ▼
┌─────────────────┐
│  backend:8080   │  Spring Boot 3.3 + Java 21
└────────┬────────┘
         │ JPA
         ▼
┌─────────────────┐
│ postgres:5432   │  PostgreSQL 16 + Flyway
└─────────────────┘
```

## 配套书籍

本仓库是以下书籍的可部署配套案例：

- **《Codex 从入门到项目实践》**（第 28-35 章）— 南荣相如

  - 电商订单与库存管理全栈项目（需求分析 / 数据库与 API / 前端 / 库存联动 / 审批流 / CI/CD）
  - 代码片段索引：[codex-book](https://github.com/zcqiand/codex-book)
  - 电子书籍网址：[亚马逊](https://www.amazon.com/dp/B0H3781RB9)

**Issues**：[https://github.com/zcqiand/ecommerce-oms/issues](https://github.com/zcqiand/ecommerce-oms/issues)
