import {defineConfig} from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "DebugTools",
  description: "快速调用任意Java方法(本地/远程)、打印SQL语句与耗时、执行Groovy脚本",
  themeConfig: {
    siteTitle: 'DebugTools',
    logo: '/pluginIcon.svg',
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Examples', link: '/markdown-examples' }
    ],
    outline: {
      level: 'deep',
      label: '页面导航'
    },
    editLink: {
      pattern: 'https://github.com/future0923/debug-tools/edit/main/debug-tools/docs/:path',
      text: '在 GitHub 上编辑此页面'
    },
    docFooter: {
      prev: '上一页',
      next: '下一页'
    },
    lastUpdated: {
      text: '最后更新于',
    },
    sidebar: [
      {
        text: '简介',
        items: [
          { text: '什么是 DebugTools？', link: '/guide/introduction' },
          { text: '快速开始', link: '/guide/quick-start' }
        ]
      },
      {
        text: '调用任意Java方法',
        items: [
          { text: '', link: ''},
          { text: '', link: ''}
        ]
      },
      {
        text: '打印SQL语句与耗时',
        items: [
          { text: '', link: ''},
          { text: '', link: ''}
        ]
      },
      {
        text: '执行Groovy脚本',
        items: [
          { text: '', link: ''},
          { text: '', link: ''}
        ]
      }
    ],
    socialLinks: [
      { icon: 'github', link: 'https://github.com/future0923/debug-tools' }
    ],
    footer: {
      message: '基于 Apache 许可发布',
      copyright: `版权所有 © 2024-${new Date().getFullYear()} Future0923`
    },
  }
})
