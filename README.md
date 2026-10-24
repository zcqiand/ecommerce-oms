# 电商订单与库存管理系统

中小企业电商订单与库存管理系统。员工创建订单、管理商品、监控库存、协同供应商。

## 快速开始

```bash
docker compose up -d

# 访问
# - 后端 API 文档: http://localhost:8080/swagger-ui.html
# - 前端工作台:    http://localhost:5173
# - 数据库:         localhost:5432 (user: postgres / db: ecommerce)

docker compose down
```

## 功能特性

- 商品管理与库存查询（支持安全库存阈值预警）
- 订单创建与状态流转（DRAFT → SUBMITTED → APPROVED → PAID → SHIPPED → COMPLETED，含 REJECTED / CANCELLED 分支）
- 库存自动锁定/扣减/释放（下单锁库存，支付成功扣减，取消/拒绝/支付失败释放锁定）
- 多级审批流程（金额 < 1000 一级审批，≥ 1000 两级审批）
- 供应商管理与关联查询
- 数据初始化（2 个供应商 + 5 个商品 + 库存种子数据）

## 技术栈

| 技术 | 版本 |
| :--- | :--- |
| Spring Boot | 3.3.x |
| Java | 21 LTS |
| React | 18.x |
| Vite | 5.x |
| TypeScript | 5.x |
| PostgreSQL | 16 |
| Flyway | 10.x |
| Docker Compose | v2 |

## 快速链接

- [功能规格文档.md](功能规格文档.md) — 功能名称、描述与验收标准
- [CLAUDE.md](CLAUDE.md) — 开发约定与编码规范
