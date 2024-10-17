import {defineConfig} from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
    base: '/debug-tools/',
    head: [
        ['meta', {name: 'theme-color', content: '#389BFF'}],
        ['link', {rel: 'icon', type: 'image/svg+xml', href: '/debug-tools/pluginIcon.svg'}]
    ],
    title: "DebugTools",
    description: "快速调用任意Java方法(本地/远程)、打印SQL语句与耗时、执行Groovy脚本",
    themeConfig: {
        siteTitle: 'DebugTools',
        logo: '/pluginIcon.svg',
        // https://vitepress.dev/reference/default-theme-config
        nav: [
            {text: 'Home', link: '/'},
            {text: 'Examples', link: '/markdown-examples'}
        ],
        outline: {
            level: 'deep',
            label: '页面导航'
        },
        editLink: {
            pattern: 'https://github.com/future0923/debug-tools/edit/main/docs/:path',
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
                    {text: '什么是 DebugTools？', link: '/guide/introduction'},
                    {text: '快速开始', link: '/guide/quick-start'}
                ]
            },
            {
                text: '调用任意Java方法',
                items: [
                    {text: '', link: ''},
                    {text: '', link: ''}
                ]
            },
            {
                text: '打印SQL语句与耗时',
                items: [
                    {text: '', link: ''},
                    {text: '', link: ''}
                ]
            },
            {
                text: '执行Groovy脚本',
                items: [
                    {text: '', link: ''},
                    {text: '', link: ''}
                ]
            }
        ],
        socialLinks: [
            {icon: 'github', link: 'https://github.com/future0923/debug-tools'}
        ],
        footer: {
            message: '基于 Apache 许可发布',
            copyright: `版权所有 © 2024-${new Date().getFullYear()} Future0923`
        },
        search: {
            provider: 'algolia',
            options: {
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
    }
})
