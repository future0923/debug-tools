import { createRequire } from 'module'
import { DefaultTheme, defineConfig } from 'vitepress'

const require = createRequire(import.meta.url)
const pkg = require('../../package.json')

export const zh = defineConfig({
  lang: 'zh-Hans',
  title: 'DebugTools',
  titleTemplate: '集成于 IntelliJ IDEA 的 Java 开发调试插件',
  description: "专注于提升开发效率与缩短调试周期",
  themeConfig: {
    siteTitle: 'DebugTools',
    logo: '/pluginIcon.svg',
    // https://vitepress.dev/reference/default-theme-config
    nav: nav(),
    outline: {
      level: 'deep',
      label: '页面导航'
    },
    editLink: {
      pattern: 'https://github.com/java-hot-deploy/debug-tools/edit/main/docs/:path',
      text: '在 GitHub 上编辑此页面'
    },
    docFooter: {
      prev: '上一页',
      next: '下一页'
    },
    returnToTopLabel: '回到顶部',
    darkModeSwitchLabel: '主题',
    lightModeSwitchTitle: '切换到浅色模式',
    darkModeSwitchTitle: '切换到深色模式',
    lastUpdated: {
      text: '最后更新于',
    },
    sidebar: {
      '/guide': {
        base: '/guide/',
        items: sidebarGuide()
      },
      '/blog': {
        base: '/blog/',
        items: sidebarBlog()
      },
      'other': {
        base: '/other/',
        items: sidebarOther()
      }
    },
    socialLinks: [
      {icon: 'github', link: 'https://github.com/java-hot-deploy/debug-tools'},
      {icon: 'gitee', link: 'https://gitee.com/future94/debug-tools'},
    ],
    footer: {
      message: `基于 Apache 许可发布 | 版权所有 © 2024-${new Date().getFullYear()} <a href="https://github.com/java-hot-deploy/" target="_blank">Future0923</a>`,
      copyright: '<a href="https://beian.miit.gov.cn/" target="_blank">吉ICP备2024021764号-1</a> | <img src="/icon/beian.png" alt="" style="display: inline-block; width: 18px; height: 18px; vertical-align: middle;" /> <a href="https://beian.mps.gov.cn/#/query/webSearch?code=22010302000528" rel="noreferrer" target="_blank">吉公网安备22010302000528</a>'
    },
  }
})

function nav(): DefaultTheme.NavItem[] {
  return [
    {
      text: '文档',
      link: '/guide/introduction',
    },
    {
      text: '博客',
      link: '/blog/java-env',
    },
    {
      text: '联系我',
      link: '/contact-me',
    },
    {
      text: pkg.version,
      items: [
        {
          text: '更新日志',
          link: '/other/changelog'
        },
        {
          text: '参与贡献',
          link: '/other/contributing.md'
        },
        {
          text: 'DeepWiki',
          link: 'https://deepwiki.com/java-hot-deploy/debug-tools'
        }
      ]
    }
  ]
}

function sidebarGuide(): DefaultTheme.SidebarItem[] {
  return [
    {
      text: '简介',
      collapsed: false,
      items: [
        {text: '什么是 DebugTools？', link: 'introduction'},
        {text: '安装说明', link: 'install'},
        {text: '快速开始', link: 'quick-start'}
      ]
    },
    {
      text: '热重载',
      collapsed: false,
      items: [
        {text: '使用热重载', link: 'hot-reload'},
        {text: 'Class', link: 'hot-reload-class'},
        {text: 'Proxy', link: 'hot-reload-proxy'},
        {text: 'SpringBoot', link: 'hot-reload-springboot'},
        {text: 'Mybatis', link: 'hot-reload-mybatis'},
        {text: 'MybatisPlus', link: 'hot-reload-mybatis-plus'},
        {text: 'Solon', link: 'hot-reload-solon'},
      ]
    },
    {
      text: '热部署',
      collapsed: false,
      items: [
        {text: '使用热部署', link: 'hot-deploy'},
      ]
    },
    {
      text: '快速调用Java方法',
      collapsed: false,
      items: [
        {text: '附着本地', link: 'attach-local'},
        {text: '附着远程', link: 'attach-remote'},
        {text: '调试面板', link: 'quick-debug'},
        {text: '运行结果', link: 'run-result'},
        {text: '类加载器', link: 'classloader'},
        {text: 'header参数', link: 'header'},
        {text: 'xxl-job参数', link: 'xxl-job'},
        {text: '快捷调用上一次', link: 'execute-last'},
      ]
    },
    {
      text: '搜索Http地址',
      collapsed: false,
      items: [
        {text: '搜索Http地址', link: 'search-http'},
      ]
    },
    {
      text: '打印SQL语句与执行耗时',
      collapsed: false,
      items: [
        {text: 'SQL', link: 'sql'},
      ]
    },
    {
      text: '执行Groovy脚本',
      collapsed: false,
      items: [
        {text: '执行Groovy脚本', link: 'groovy-execute'},
        {text: '内置函数', link: 'groovy-function'},
      ]
    },
    {
      text: 'Idea插件说明',
      collapsed: false,
      items: [
        {text: 'ToolsWindow', link: 'idea-tools-window'},
        {text: '右键菜单', link: 'idea-menu'},
        {text: '配置项', link: 'idea-config'},
        {text: '调试面板', link: 'idea-panel'},
        {text: '展示结果', link: 'idea-result'},
      ]
    },
  ]
}

function sidebarBlog(): DefaultTheme.SidebarItem[] {
  return [
    {text: 'Java多版本管理', link: 'java-env'},
    {text: '远程调试', link: 'remote-debug'},
    {text: '@Autowired与@Value源码解析', link: 'autowired-value'},
  ]
}

function sidebarOther(): DefaultTheme.SidebarItem[] {
  return [
    {text: '版本迭代记录', link: 'changelog'},
    {text: '参与贡献', link: 'contributing'},
  ]
}

export const search: DefaultTheme.AlgoliaSearchOptions['locales'] = {
  zh: {
    placeholder: '搜索文档',
    translations: {
      button: {
        buttonText: '搜索文档',
        buttonAriaLabel: '搜索文档'
      },
      modal: {
        searchBox: {
          resetButtonTitle: '清除查询条件',
          resetButtonAriaLabel: '清除查询条件',
          cancelButtonText: '取消',
          cancelButtonAriaLabel: '取消'
        },
        startScreen: {
          recentSearchesTitle: '搜索历史',
          noRecentSearchesText: '没有搜索历史',
          saveRecentSearchButtonTitle: '保存至搜索历史',
          removeRecentSearchButtonTitle: '从搜索历史中移除',
          favoriteSearchesTitle: '收藏',
          removeFavoriteSearchButtonTitle: '从收藏中移除'
        },
        errorScreen: {
          titleText: '无法获取结果',
          helpText: '你可能需要检查你的网络连接'
        },
        footer: {
          selectText: '选择',
          navigateText: '切换',
          closeText: '关闭',
          searchByText: '搜索提供者'
        },
        noResultsScreen: {
          noResultsText: '无法找到相关结果',
          suggestedQueryText: '你可以尝试查询',
          reportMissingResultsText: '你认为该查询应该有结果？',
          reportMissingResultsLinkText: '点击反馈'
        }
      }
    }
  }
}