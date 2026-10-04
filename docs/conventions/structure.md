# 目录结构与模块职责

> 从 CLAUDE.md 移出（L0 60 行门）。树是文件系统的复写，以仓库实际为准；
> 模块职责注解在此维护。

```text
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
