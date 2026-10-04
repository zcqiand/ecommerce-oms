# Tag 规约

> 2026-10-04 对齐：原铁律的 `v<MAJOR>.<MINOR>-<NNN>` 是三个兄弟书稿仓已废弃的
> 历史遗留格式，本仓同步改为 Release 格式（用户裁定）。

| 字段 | 值 |
|---|---|
| 格式 | `v<MAJOR>.<MINOR>.<PATCH>-<YYYYMMDD>` |
| 用途 | 公开里程碑（sprint 收尾 / 部署上线 / 版本基线） |
| 能否删 / 覆盖 | **禁止** |

样例：

- `v0.7.0-20260821` —— saas-nextjs backend 塌缩后的 0.7.0 release
- `v0.1.2-20260821` —— suite 根仓的 release（这次 form A → B + tag / submodule 规约更新）

> `<YYYYMMDD>` 是 tag 创建日（commit author date 也可，但要同一仓一致）。
> 不放 commit 数 —— `git describe` 会自动加 `-<N>-g<sha>` 后缀。
>
> **历史遗留**：本仓存有一个旧 iteration tag `v1.0-003`（早于本规约）。按兄弟仓
> 先例应重命名为 Release 格式后显式推送；重命名涉及删 tag + force push，属外发
> 动作，留待人批执行。**新 tag 一律用 Release 格式。**

## 推送

```bash
# 正确
git push origin v0.7.0-20260821

# 错误：可能误推未准备好的 tag
git push --tags
```

`--tags` 把本地**全部** tag 推上去。Release tag 应该显式 `push origin <tag>`，让
reviewer 在推送前显式选择。
