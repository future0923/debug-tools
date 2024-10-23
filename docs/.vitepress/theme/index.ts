// https://vitepress.dev/guide/custom-theme
import { h } from 'vue'
import type { Theme } from 'vitepress'
import DefaultTheme from 'vitepress/theme'
import mediumZoom from "medium-zoom";
import './style.css'

export default {
  extends: DefaultTheme,
  Layout: () => {
    return h(DefaultTheme.Layout, null, {
      // https://vitepress.dev/guide/extending-default-theme#layout-slots
    })
  },
  enhanceApp({app, router, siteData}) {
    app.directive('zoom', {
      mounted(el) {
        mediumZoom(el, {
          margin: 24,
          background: 'rgba(0, 0, 0, 0.8)',
          scrollOffset: 0,
        })
      }
    })
  }
} satisfies Theme
