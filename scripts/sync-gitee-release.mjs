import { createWriteStream } from 'node:fs'
import { mkdir, readFile, rename, rm, stat } from 'node:fs/promises'
import { basename, dirname, join, resolve } from 'node:path'
import { pipeline } from 'node:stream/promises'
import { fileURLToPath, pathToFileURL } from 'node:url'

const rootDir = resolve(dirname(fileURLToPath(import.meta.url)), '..')
const githubApiBaseUrl = 'https://api.github.com'
const giteeBaseUrl = 'https://gitee.com/api/v5'

export function parseArgs(argv) {
  const options = {
    execute: false,
    githubOwner: 'future0923',
    githubRepo: 'debug-tools',
    giteeOwner: 'future94',
    giteeRepo: 'debug-tools',
    tag: 'v4.6.1',
    targetCommitish: 'main',
    outDir: '',
    downloadTimeoutMs: 120000,
  }

  for (let index = 0; index < argv.length; index += 1) {
    const arg = argv[index]
    if (arg === '--execute') {
      options.execute = true
    } else if (arg === '--tag') {
      options.tag = requiredValue(argv, ++index, arg)
    } else if (arg === '--target-commitish') {
      options.targetCommitish = requiredValue(argv, ++index, arg)
    } else if (arg === '--github-owner') {
      options.githubOwner = requiredValue(argv, ++index, arg)
    } else if (arg === '--github-repo') {
      options.githubRepo = requiredValue(argv, ++index, arg)
    } else if (arg === '--gitee-owner') {
      options.giteeOwner = requiredValue(argv, ++index, arg)
    } else if (arg === '--gitee-repo') {
      options.giteeRepo = requiredValue(argv, ++index, arg)
    } else if (arg === '--out-dir') {
      options.outDir = resolve(requiredValue(argv, ++index, arg))
    } else if (arg === '--download-timeout-ms') {
      options.downloadTimeoutMs = Number(requiredValue(argv, ++index, arg))
    } else if (arg === '--help' || arg === '-h') {
      options.help = true
    } else {
      throw new Error(`Unknown argument: ${arg}`)
    }
  }

  if (!options.outDir) {
    options.outDir = join(process.cwd(), 'dist', 'gitee-release', options.tag)
  }

  return options
}

function requiredValue(argv, index, name) {
  const value = argv[index]
  if (!value || value.startsWith('--')) {
    throw new Error(`${name} requires a value`)
  }
  return value
}

export function buildGithubReleaseApiUrl({ owner, repo, tag }) {
  return `${githubApiBaseUrl}/repos/${encodeURIComponent(owner)}/${encodeURIComponent(repo)}/releases/tags/${encodeURIComponent(tag)}`
}

export function assetNamesFromGithubRelease(release) {
  return (release.assets || [])
    .filter((asset) => asset.name && asset.name !== 'checksums.txt' && asset.browser_download_url)
    .map((asset) => ({ name: asset.name, url: asset.browser_download_url }))
}

export function buildGiteeApiUrl(path, params = {}) {
  let resolvedPath = path
  for (const [key, value] of Object.entries(params)) {
    resolvedPath = resolvedPath.replace(`:${key}`, encodeURIComponent(value))
  }
  return `${giteeBaseUrl}${resolvedPath}`
}

export function releaseBody() {
  return [
    '- 秒级热部署，修改代码后无需完整重启应用即可快速生效。',
    '- 秒级热重载，支持类、代理类、Spring、Solon、MyBatis 等常见场景。',
    '- 调用任意 Java 方法，无需额外编写测试入口即可验证业务逻辑。',
    '- 支持本地附加和远程附加，配合远程调试缩短排查链路。',
    '- 自动打印 SQL 语句与耗时，方便定位慢查询和参数问题。',
    '- 支持搜索 HttpUrl，快速跳转到对应接口方法。',
    '- 支持 xxl-job、Groovy 脚本执行和多种框架扩展能力。',
  ].join('\n')
}

export function buildGiteeReleasePayload({ tag, targetCommitish, body }) {
  return {
    tag_name: tag,
    target_commitish: targetCommitish,
    name: tag,
    body,
    prerelease: false,
  }
}

async function main() {
  const options = parseArgs(process.argv.slice(2))
  if (options.help) {
    console.log(usage())
    return
  }

  const env = await loadLocalEnv()
  const token = process.env.GITEE_ACCESS_TOKEN || env.GITEE_ACCESS_TOKEN || ''
  if (options.execute && !token) {
    throw new Error('GITEE_ACCESS_TOKEN is required when --execute is used. Put it in .env.local or export it.')
  }

  await syncGiteeRelease(options, token)
}

async function loadLocalEnv() {
  try {
    const content = await readFile(join(rootDir, '.env.local'), 'utf8')
    return parseEnvFile(content)
  } catch (error) {
    if (error.code === 'ENOENT') {
      return {}
    }
    throw error
  }
}

export function parseEnvFile(content) {
  const values = {}
  for (const rawLine of content.split(/\r?\n/)) {
    const line = rawLine.trim()
    if (!line || line.startsWith('#')) continue
    const separator = line.indexOf('=')
    if (separator === -1) continue
    const key = line.slice(0, separator).trim()
    const rawValue = line.slice(separator + 1).trim()
    if (!key) continue
    values[key] = unquoteEnvValue(rawValue)
  }
  return values
}

function unquoteEnvValue(value) {
  if (
    (value.startsWith('"') && value.endsWith('"')) ||
    (value.startsWith("'") && value.endsWith("'"))
  ) {
    return value.slice(1, -1)
  }
  return value
}

async function syncGiteeRelease(options, token) {
  const githubRelease = await fetchGithubRelease(options)
  const releaseAssets = assetNamesFromGithubRelease(githubRelease)
  if (releaseAssets.length === 0) {
    throw new Error(`No downloadable assets found on GitHub release ${options.githubOwner}/${options.githubRepo}@${options.tag}`)
  }

  await mkdir(options.outDir, { recursive: true })

  console.log(`Mode: ${options.execute ? 'execute' : 'dry-run'}`)
  console.log(`GitHub: ${options.githubOwner}/${options.githubRepo} ${options.tag}`)
  console.log(`Gitee:  ${options.giteeOwner}/${options.giteeRepo} ${options.tag}`)
  console.log(`Output: ${options.outDir}`)
  console.log()

  for (const asset of releaseAssets) {
    await downloadFile(asset.url, join(options.outDir, asset.name), options.downloadTimeoutMs)
  }

  if (!options.execute) {
    console.log()
    console.log('Dry run complete. Would sync these assets to Gitee:')
    for (const asset of releaseAssets) {
      console.log(`  - ${asset.name}`)
    }
    console.log()
    console.log('Run again with --execute and GITEE_ACCESS_TOKEN to update Gitee.')
    return
  }

  const release = await ensureGiteeRelease(options, token)
  await replaceGiteeAssets(options, token, release, releaseAssets.map((asset) => asset.name))
  console.log()
  console.log(`Gitee release synced: ${options.tag}`)
}

async function fetchGithubRelease(options) {
  const url = buildGithubReleaseApiUrl({
    owner: options.githubOwner,
    repo: options.githubRepo,
    tag: options.tag,
  })
  const response = await fetch(url, {
    headers: {
      Accept: 'application/vnd.github+json',
      'User-Agent': 'debug-tools-gitee-release-sync',
    },
  })
  if (!response.ok) {
    throw new Error(await responseError(response, 'GitHub release request failed'))
  }
  return response.json()
}

async function downloadFile(url, targetPath, timeoutMs) {
  try {
    const current = await stat(targetPath)
    if (current.size > 0) {
      console.log(`Using existing ${basename(targetPath)}`)
      return
    }
  } catch (error) {
    if (error.code !== 'ENOENT') throw error
  }

  console.log(`Downloading ${basename(targetPath)}`)
  const controller = new AbortController()
  const timeout = setTimeout(() => controller.abort(), timeoutMs)
  const partialPath = `${targetPath}.part`
  try {
    const response = await fetch(url, {
      redirect: 'follow',
      signal: controller.signal,
      headers: { 'User-Agent': 'debug-tools-gitee-release-sync' },
    })
    if (!response.ok) {
      throw new Error(`Download failed ${response.status} ${response.statusText}: ${url}`)
    }
    await pipeline(response.body, createWriteStream(partialPath))
    await rm(targetPath, { force: true })
    await rename(partialPath, targetPath)
  } catch (error) {
    await rm(partialPath, { force: true })
    if (error.name === 'AbortError') {
      throw new Error(`Download timed out after ${timeoutMs}ms: ${url}`)
    }
    throw error
  } finally {
    clearTimeout(timeout)
  }
}

async function ensureGiteeRelease(options, token) {
  const existing = await findGiteeRelease(options, token)
  const releasePayload = buildGiteeReleasePayload({
    tag: options.tag,
    targetCommitish: options.targetCommitish,
    body: releaseBody(),
  })

  if (!existing) {
    console.log(`Creating Gitee release ${options.tag}`)
    return giteeRequest('/repos/:owner/:repo/releases', options, token, {
      method: 'POST',
      body: releasePayload,
    })
  }

  console.log(`Updating Gitee release ${options.tag}`)
  return giteeRequest('/repos/:owner/:repo/releases/:id', { ...options, id: existing.id }, token, {
    method: 'PATCH',
    body: releasePayload,
  })
}

async function findGiteeRelease(options, token) {
  const releases = await giteeRequest('/repos/:owner/:repo/releases', options, token)
  return releases.find((release) => release.tag_name === options.tag) || null
}

async function replaceGiteeAssets(options, token, release, assetNames) {
  const assets = await giteeRequest('/repos/:owner/:repo/releases/:id/attach_files', {
    ...options,
    id: release.id,
  }, token)
  const namesToDelete = new Set([...assetNames, 'checksums.txt'])

  for (const asset of assets) {
    if (namesToDelete.has(asset.name)) {
      console.log(`Deleting existing Gitee asset ${asset.name}`)
      await giteeRequest('/repos/:owner/:repo/releases/:id/attach_files/:attachId', {
        ...options,
        id: release.id,
        attachId: asset.id,
      }, token, { method: 'DELETE' })
    }
  }

  for (const assetName of assetNames) {
    console.log(`Uploading Gitee asset ${assetName}`)
    await uploadGiteeAsset(options, token, release.id, join(options.outDir, assetName))
  }
}

async function uploadGiteeAsset(options, token, releaseId, filePath) {
  const form = new FormData()
  const bytes = await readFile(filePath)
  form.set('access_token', token)
  form.set('file', new Blob([bytes]), basename(filePath))

  const url = buildGiteeApiUrl('/repos/:owner/:repo/releases/:id/attach_files', {
    owner: options.giteeOwner,
    repo: options.giteeRepo,
    id: String(releaseId),
  })
  const response = await fetch(url, { method: 'POST', body: form })
  if (!response.ok) {
    throw new Error(await responseError(response, 'Upload failed'))
  }
  return response.json()
}

async function giteeRequest(path, options, token, request = {}) {
  const url = new URL(buildGiteeApiUrl(path, {
    owner: options.giteeOwner,
    repo: options.giteeRepo,
    id: String(options.id || ''),
    attachId: String(options.attachId || ''),
  }))
  url.searchParams.set('access_token', token)

  const init = { method: request.method || 'GET' }
  if (request.body) {
    init.headers = { 'Content-Type': 'application/json' }
    init.body = JSON.stringify({ access_token: token, ...request.body })
  }

  const response = await fetch(url, init)
  if (!response.ok) {
    throw new Error(await responseError(response, 'Gitee API request failed'))
  }
  if (response.status === 204) {
    return null
  }
  return response.json()
}

async function responseError(response, prefix) {
  const text = await response.text()
  return `${prefix}: ${response.status} ${response.statusText}${text ? `\n${text}` : ''}`
}

function usage() {
  return `Usage:
  node scripts/sync-gitee-release.mjs [options]

Token:
  Put GITEE_ACCESS_TOKEN in .env.local, or export it in the shell.

Options:
  --execute                 Write changes to Gitee. Without this, only dry-runs.
  --tag <tag>               Release tag to sync. Default: v4.6.1
  --target-commitish <ref>  Gitee release target ref. Default: main
  --github-owner <owner>    GitHub owner. Default: future0923
  --github-repo <repo>      GitHub repo. Default: debug-tools
  --gitee-owner <owner>     Gitee owner. Default: future94
  --gitee-repo <repo>       Gitee repo. Default: debug-tools
  --out-dir <path>          Download directory. Default: dist/gitee-release/<tag>
  --download-timeout-ms <n> Download timeout per asset. Default: 120000
`
}

if (process.argv[1] && import.meta.url === pathToFileURL(resolve(process.argv[1])).href) {
  main().catch((error) => {
    console.error(error.message || error)
    process.exit(1)
  })
}
