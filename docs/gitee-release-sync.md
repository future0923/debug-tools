# Gitee Release Sync

Use `scripts/sync-gitee-release.mjs` to publish GitHub release assets to the
matching Gitee release.

The script is dry-run by default. It reads the GitHub release asset list,
downloads the assets to `dist/gitee-release/<tag>/`, and prints what it would
upload. It only writes to Gitee when `--execute` is present.

## Quick Run

```bash
cd /Users/weilai/Documents/debug-tools
node scripts/sync-gitee-release.mjs --tag v4.6.1 --download-timeout-ms 600000
node scripts/sync-gitee-release.mjs --tag v4.6.1 --download-timeout-ms 600000 --execute
```

The first command is a dry run. Check that the asset list looks right, then run
the second command to update Gitee.

## Token

The script reads `GITEE_ACCESS_TOKEN` from `.env.local`, so you do not need to
export it every time.

```bash
cd /Users/weilai/Documents/debug-tools
test -f .env.local && echo "token file exists"
```

If the file is missing, create it like this:

```bash
cat > .env.local
GITEE_ACCESS_TOKEN=your-token
```

Press `Ctrl-D` after the token line. `.env.local` is ignored by git.

## Step By Step

Dry run:

```bash
cd /Users/weilai/Documents/debug-tools
node scripts/sync-gitee-release.mjs --tag v4.6.1 --download-timeout-ms 600000
```

Sync to Gitee:

```bash
cd /Users/weilai/Documents/debug-tools
node scripts/sync-gitee-release.mjs --tag v4.6.1 --download-timeout-ms 600000 --execute
```

The script creates or updates the Gitee release, writes a clean Chinese release
description, removes same-name old assets and any old `checksums.txt` asset, and
uploads the release packages.

## Change Version

For another release, replace the tag:

```bash
cd /Users/weilai/Documents/debug-tools
node scripts/sync-gitee-release.mjs --tag v4.6.2 --download-timeout-ms 600000
node scripts/sync-gitee-release.mjs --tag v4.6.2 --download-timeout-ms 600000 --execute
```
