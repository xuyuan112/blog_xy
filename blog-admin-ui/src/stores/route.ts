import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface TagItem {
  path: string
  title: string
  name?: string
}

/**
 * 标签栏 store
 *
 * <p>记录已访问的路由, 供 TagsView 组件渲染可关闭标签。
 * 持久化到 sessionStorage 以避免刷新丢失。</p>
 */
export const useRouteStore = defineStore(
  'route',
  () => {
    const tags = ref<TagItem[]>([])

    const addTag = (tag: TagItem) => {
      if (tags.value.some((t) => t.path === tag.path)) return
      tags.value.push(tag)
    }

    const removeTag = (path: string) => {
      tags.value = tags.value.filter((t) => t.path !== path)
    }

    const closeOthers = (path: string) => {
      tags.value = tags.value.filter((t) => t.path === path)
    }

    const closeAll = () => {
      tags.value = []
    }

    return { tags, addTag, removeTag, closeOthers, closeAll }
  },
  {
    persist: {
      key: 'blog-admin-tags',
      storage: sessionStorage
    }
  }
)
