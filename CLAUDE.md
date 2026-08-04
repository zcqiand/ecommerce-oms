# ecommerce-oms — 仓库工作约定（供 Claude Code）

本仓为《Codex 从入门到项目实践》第 28-35 章「电商订单系统」的可运行配套工程，是书稿代码块的 **source of truth**。

## 项目定位

中小企业电商订单与库存管理系统。员工创建订单、管理商品、监控库存、协同供应商。用于演示用 Claude Code 完成一个**生产级、Spring Boot 3.3 + React 18 全栈项目**的完整开发流程。

## 铁律

- **TDD**：每个模块先写失败测试 → 跑确认失败 → 实现 → 跑确认绿 → commit。
- **版本钉死**：依赖与 `version-lock.json` 的 `version_lock` 一致；不引入 lock 外的库。
- **tag 即放行**：全量回归绿后打 `v<MAJOR>.<MINOR>-<NNN>`（NNN=项目号）。
- **只增不改**：扩充时不动现有模块签名/行为；新模块独立测试，CI 双跑（旧测试 + 新测试都绿）。
- **mock-friendly**：`npm install && npm test` 必须在无 Key、无 Docker、无网下全绿。

## 技术栈与版本（钉死于 version-lock.json）

- Spring Boot 3.3.x
- Java 21 LTS
- PostgreSQL 16
- Flyway 10.x
- React 18.x
- Vite 5.x
- TypeScript 5.x
- Docker Compose v2
- springdoc-openapi 2.x

## 验收

```bash
docker compose up -d   # 拉起 postgres + backend + frontend
docker compose down    # 关停
```

## 目录结构

```
ecommerce-oms/
├── docker-compose.yml             ← 三服务编排（postgres:16-alpine + backend + frontend）
├── .env.example
├── CLAUDE.md
├── backend/
│   ├── pom.xml
│   ├── Dockerfile
│   ├── src/main/java/com/zcqiand/ecommerce/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── exception/
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   └── src/test/java/
├── frontend/
│   ├── package.json
│   ├── Dockerfile
│   ├── vite.config.ts
│   ├── tsconfig.json
│   └── src/
│       ├── App.tsx
│       ├── main.tsx
│       ├── api/
│       └── pages/
└── .github/workflows/
    └── ci.yml
```

## 编码约定

- 所有业务类型放在 `src/types/`（前端）
- 所有 HTTP 客户端封装在 `src/api/`（前端）
- 特性按 `src/features/` 组织（前端）
- API 必须有 OpenAPI 注解：所有 Controller 方法必须有 `@Operation` + `@ApiResponse` 注解
- 前后端接口契约同步：后端改 DTO，前端必须同步改对应 TypeScript 类型
- 环境变量配置：所有敏感信息走 `.env`，禁止硬编码进代码
- 使用 jakarta.*：Spring Boot 3.x 使用 jakarta.* 而非 javax.*
