import { defineConfig } from 'vitepress'
import { en } from "./locales/en"
import { search as zhSearch, zh } from "./locales/zh"


const pkg = require('../package.json')

// https://vitepress.dev/reference/site-config
export default defineConfig({
    head: [
        ['meta', {name: 'theme-color', content: '#389BFF'}],
        ['link', {rel: 'icon', type: 'image/svg+xml', href: '/pluginIcon.svg'}],
        [
            'script',
            {},
            `var _hmt = _hmt || [];
            (function() {
              var hm = document.createElement("script");
              hm.src = "https://hm.baidu.com/hm.js?40a7c38d178d1f141a76eaae0c813277";
              var s = document.getElementsByTagName("script")[0]; 
              s.parentNode.insertBefore(hm, s);
            })();`
        ],
        [
            'script',
            { async: true, src: 'https://www.googletagmanager.com/gtag/js?id=G-9EPFQSXQ95' }
        ],
        [
            'script',
            {},
            `window.dataLayer = window.dataLayer || [];
            function gtag(){dataLayer.push(arguments);}
            gtag('js', new Date());
            gtag('config', 'G-9EPFQSXQ95');`
        ],
    ],
    sitemap: {
        hostname: 'https://debugtools.cc',
    },
    rewrites: {
        'zh/:rest*': ':rest*'
    },
    lastUpdated: true,
    locales: {
        root: {
            label: '简体中文',
            ...zh
        },
        en: {
            label: 'English',
            ...en
        },
    },
    themeConfig: {
        search: {
            provider: 'algolia',
            options: {
                appId: 'Q3PQ9B9Z5C',
                apiKey: 'c1efd92fbc0ff7c6829d99c17a7f9926',
                indexName: 'debug-tools',
                locales: {
                    ...zhSearch,
                }
            }
        },
    },
    markdown: {
        image: {
            lazyLoading: true
        }
    },
})