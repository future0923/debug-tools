# Gitee Release 同步

使用 `scripts/sync-gitee-release.mjs` 将 GitHub Release 的发布说明和附件同步到
对应的 Gitee Release。

脚本默认是 dry-run 模式，只会读取 GitHub Release、下载附件到
`dist/gitee-release/<tag>/`，并打印将要上传的文件。只有加上 `--execute` 时，才会
真正写入 Gitee。

## 快速使用

```bash
cd /Users/weilai/Documents/debug-tools
node scripts/sync-gitee-release.mjs --tag v5.0.0 --download-timeout-ms 600000
node scripts/sync-gitee-release.mjs --tag v5.0.0 --download-timeout-ms 600000 --execute
```

第一条命令是预演，用来确认附件列表和下载校验都正常；第二条命令才会推送 tag、
创建或更新 Gitee Release，并上传附件。

## Token 配置

脚本会自动从 `.env.local` 读取 `GITEE_ACCESS_TOKEN`，不用每次手动 export。

```bash
cd /Users/weilai/Documents/debug-tools
test -f .env.local && echo "token 文件存在"
```

如果文件不存在，可以这样创建：

```bash
cat > .env.local
GITEE_ACCESS_TOKEN=your-token
```

输入 token 行后按 `Ctrl-D` 结束。`.env.local` 已被 git 忽略，不会提交到仓库。

## 分步执行

先预演：

```bash
cd /Users/weilai/Documents/debug-tools
node scripts/sync-gitee-release.mjs --tag v5.0.0 --download-timeout-ms 600000
```

确认无误后同步到 Gitee：

```bash
cd /Users/weilai/Documents/debug-tools
node scripts/sync-gitee-release.mjs --tag v5.0.0 --download-timeout-ms 600000 --execute
```

执行同步时，脚本会做这些事：

- 将指定 tag 推送到 `gitee` 远端
- 创建或更新对应的 Gitee Release
- 复用 GitHub Release 的发布说明
- 删除同名旧附件和旧的 `checksums.txt`
- 如果 GitHub API 提供了 `sha256:` digest，就校验下载文件
- 上传 Release 附件，并打印 Gitee 下载链接

## 切换版本

同步其他版本时，只需要替换 `--tag`：

```bash
cd /Users/weilai/Documents/debug-tools
node scripts/sync-gitee-release.mjs --tag v5.0.1 --download-timeout-ms 600000
node scripts/sync-gitee-release.mjs --tag v5.0.1 --download-timeout-ms 600000 --execute
```

`--tag` 是必填项，目的是避免脚本误用旧的默认版本。
