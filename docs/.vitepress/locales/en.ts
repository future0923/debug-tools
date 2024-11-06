import { createRequire } from 'module'
import { DefaultTheme, defineConfig } from 'vitepress'

const require = createRequire(import.meta.url)
const pkg = require('../../package.json')

export const en = defineConfig({
  lang: 'en-US',
  title: 'DebugTools',
  titleTemplate: 'Java Debugging Tools',
  description: "Quickly call any Java method (local/remote), print SQL statements and time consumption, and execute Groovy scripts",
  themeConfig: {
    siteTitle: 'DebugTools',
    logo: '/pluginIcon.svg',
    // https://vitepress.dev/reference/default-theme-config
    nav: nav(),
    outline: {
      level: 'deep',
    },
    editLink: {
      pattern: 'https://github.com/future0923/debug-tools/edit/main/docs/:path',
      text: 'Edit this page on GitHub'
    },
    sidebar: {
      '/guide': {
        base: '/guide/',
        items: sidebarGuide()
      },
    },
    socialLinks: [
      {icon: 'github', link: 'https://github.com/future0923/debug-tools'}
    ],
    footer: {
      message: `Released under the Apache License | Copyright © 2024-${new Date().getFullYear()} <a href="https://github.com/future0923/" target="_blank">Future0923</a>`,
      copyright: '<a href="https://beian.miit.gov.cn/" target="_blank">Ji ICP No.2024021764-1</a> | <img src="/icon/beian.png" alt="" style="display: inline-block; width: 18px; height: 18px; vertical-align: middle;" /> <a href="https://beian.mps.gov.cn/#/query/webSearch?code=22010302000528" rel="noreferrer" target="_blank">Ji internet security 22010302000528</a>'
    },
  }
})

function nav(): DefaultTheme.NavItem[] {
  return [
    {
      text: 'Document',
      link: '/guide/introduction',
      activeMatch: '/guide/'
    },
    {
      text: pkg.version,
      items: [
        {
          text: 'Changelog',
          link: 'https://github.com/future0923/debug-tools/blob/main/CHANGELOG.md'
        },
        {
          text: 'Contributing',
          link: 'https://github.com/future0923/debug-tools/blob/main/.github/contributing.md'
        }
      ]
    }
  ]
}

function sidebarGuide(): DefaultTheme.SidebarItem[] {
  return [
    {
      text: 'Introduction',
      collapsed: false,
      items: [
        {text: 'What is DebugTools？', link: 'introduction'},
        {text: 'Quick start', link: 'quick-start'}
      ]
    },
    {
      text: 'Quickly call java method',
      collapsed: false,
      items: [
        {text: 'Attach local', link: 'attach-local'},
        {text: 'Attach remote', link: 'attach-remote'},
        {text: 'Debug panel', link: 'quick-debug'},
        {text: 'Run result', link: 'run-result'},
        {text: 'ClassLoader', link: 'classloader'},
        {text: 'Header parameter', link: 'header'},
        {text: 'Xxl-job parameter', link: 'xxl-job'},
        {text: 'Execute last', link: 'execute-last'},
      ]
    },
    {
      text: 'Printing SQL statements and execution time',
      collapsed: false,
      items: [
        {text: 'SQL', link: 'sql'},
      ]
    },
    {
      text: 'Execute groovy script',
      collapsed: false,
      items: [
        {text: 'Execute groovy script', link: 'groovy-execute'},
        {text: 'Build-in function', link: 'groovy-function'},
      ]
    },
    {
      text: 'Idea plugin extension',
      collapsed: false,
      items: [
        {text: 'Tools window', link: 'idea-tools-window'},
        {text: 'Right-click menu', link: 'idea-menu'},
        {text: 'Config', link: 'idea-config'},
        {text: 'Debug panel', link: 'idea-panel'},
        {text: 'Run result', link: 'idea-result'},
      ]
    },
  ]
}