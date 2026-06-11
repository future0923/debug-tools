import assert from 'node:assert/strict'
import { test } from 'node:test'
import { join } from 'node:path'
import {
  assetNamesFromGithubRelease,
  buildGitPushTagCommand,
  buildGithubReleaseApiUrl,
  buildGiteeApiUrl,
  buildGiteeReleasePayload,
  githubReleaseBody,
  parseArgs,
  parseEnvFile,
  verifyFileDigest,
} from './sync-gitee-release.mjs'

test('parseArgs requires an explicit tag for release syncs', () => {
  assert.throws(() => parseArgs([]), /--tag is required/)
})

test('parseArgs reads common overrides', () => {
  assert.equal(parseArgs(['--tag', 'v5.0.0', '--execute']).execute, true)
  assert.equal(parseArgs(['--tag', 'v5.0.0']).tag, 'v5.0.0')
  assert.equal(parseArgs(['--tag', 'v5.0.0', '--target-commitish', '385a256']).targetCommitish, '385a256')
  assert.equal(parseArgs(['--tag', 'v5.0.0', '--download-timeout-ms', '5000']).downloadTimeoutMs, 5000)
  assert.equal(parseArgs(['--tag', 'v5.0.0', '--gitee-remote', 'mirror']).giteeRemote, 'mirror')
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
        {
          name: 'debug-tools-agent.jar',
          browser_download_url: 'https://example.com/agent.jar',
          digest: 'sha256:abc123',
        },
        { name: 'debug-tools-boot.jar', browser_download_url: 'https://example.com/boot.jar' },
        { name: 'checksums.txt', browser_download_url: 'https://example.com/checksums.txt' },
        { name: '', browser_download_url: 'https://example.com/blank' },
      ],
    }),
    [
      { name: 'debug-tools-agent.jar', url: 'https://example.com/agent.jar', digest: 'sha256:abc123' },
      { name: 'debug-tools-boot.jar', url: 'https://example.com/boot.jar', digest: '' },
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

test('githubReleaseBody reuses GitHub release notes for Gitee', () => {
  assert.equal(githubReleaseBody({ body: '- real release notes' }), '- real release notes')
  assert.equal(githubReleaseBody({ body: null }), '')
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

test('buildGitPushTagCommand pushes only the requested tag to Gitee', () => {
  assert.deepEqual(buildGitPushTagCommand('gitee', 'v5.0.0'), {
    command: 'git',
    args: ['push', 'gitee', 'refs/tags/v5.0.0'],
  })
})

test('verifyFileDigest accepts GitHub sha256 digests and rejects mismatches', async () => {
  const filePath = join(process.cwd(), 'scripts', 'sync-gitee-release.test.mjs')
  await assert.doesNotReject(verifyFileDigest(filePath, ''))
  await assert.rejects(
    verifyFileDigest(filePath, 'sha256:0000000000000000000000000000000000000000000000000000000000000000'),
    /Digest mismatch/,
  )
})
