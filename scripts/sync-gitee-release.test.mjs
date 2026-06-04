import assert from 'node:assert/strict'
import { test } from 'node:test'
import { join } from 'node:path'
import {
  assetNamesFromGithubRelease,
  buildGithubReleaseApiUrl,
  buildGiteeApiUrl,
  buildGiteeReleasePayload,
  parseArgs,
  parseEnvFile,
  releaseBody,
} from './sync-gitee-release.mjs'

test('parseArgs defaults to dry run for debug-tools v4.6.1', () => {
  assert.deepEqual(parseArgs([]), {
    execute: false,
    githubOwner: 'future0923',
    githubRepo: 'debug-tools',
    giteeOwner: 'future94',
    giteeRepo: 'debug-tools',
    tag: 'v4.6.1',
    targetCommitish: 'main',
    outDir: join(process.cwd(), 'dist', 'gitee-release', 'v4.6.1'),
    downloadTimeoutMs: 120000,
  })
})

test('parseArgs reads common overrides', () => {
  assert.equal(parseArgs(['--execute']).execute, true)
  assert.equal(parseArgs(['--tag', 'v5.0.0']).tag, 'v5.0.0')
  assert.equal(parseArgs(['--target-commitish', '385a256']).targetCommitish, '385a256')
  assert.equal(parseArgs(['--download-timeout-ms', '5000']).downloadTimeoutMs, 5000)
})

test('buildGithubReleaseApiUrl points at the release metadata endpoint', () => {
  assert.equal(
    buildGithubReleaseApiUrl({
      owner: 'future0923',
      repo: 'debug-tools',
      tag: 'v4.6.1',
    }),
    'https://api.github.com/repos/future0923/debug-tools/releases/tags/v4.6.1',
  )
})

test('assetNamesFromGithubRelease keeps downloadable release assets only', () => {
  assert.deepEqual(
    assetNamesFromGithubRelease({
      assets: [
        { name: 'debug-tools-agent.jar', browser_download_url: 'https://example.com/agent.jar' },
        { name: 'debug-tools-boot.jar', browser_download_url: 'https://example.com/boot.jar' },
        { name: 'checksums.txt', browser_download_url: 'https://example.com/checksums.txt' },
        { name: '', browser_download_url: 'https://example.com/blank' },
      ],
    }),
    [
      { name: 'debug-tools-agent.jar', url: 'https://example.com/agent.jar' },
      { name: 'debug-tools-boot.jar', url: 'https://example.com/boot.jar' },
    ],
  )
})

test('buildGiteeApiUrl encodes repository paths', () => {
  assert.equal(
    buildGiteeApiUrl('/repos/:owner/:repo/releases', {
      owner: 'future94',
      repo: 'debug-tools',
    }),
    'https://gitee.com/api/v5/repos/future94/debug-tools/releases',
  )
})

test('releaseBody describes the product without public sync metadata', () => {
  const body = releaseBody()
  assert.match(body, /秒级热部署/)
  assert.match(body, /调用任意 Java 方法/)
  assert.match(body, /SQL 语句/)
  assert.doesNotMatch(body, /GitHub/i)
  assert.doesNotMatch(body, /同步/)
  assert.doesNotMatch(body, /checksums\.txt/)
})

test('buildGiteeReleasePayload includes fields required by create and update APIs', () => {
  assert.deepEqual(
    buildGiteeReleasePayload({
      tag: 'v4.6.1',
      targetCommitish: 'main',
      body: 'release notes',
    }),
    {
      tag_name: 'v4.6.1',
      target_commitish: 'main',
      name: 'v4.6.1',
      body: 'release notes',
      prerelease: false,
    },
  )
})

test('parseEnvFile reads shell-style token assignments', () => {
  assert.deepEqual(parseEnvFile('GITEE_ACCESS_TOKEN=abc123\nOTHER="hello world"\n# ignored\n'), {
    GITEE_ACCESS_TOKEN: 'abc123',
    OTHER: 'hello world',
  })
})
