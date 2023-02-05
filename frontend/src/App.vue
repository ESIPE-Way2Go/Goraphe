<template>
  <component :is="resolveLayout">
    <router-view></router-view>
  </component>
</template>

<script>
import {computed} from "vue";
import {useRouter} from '@/utils'
import LayoutBlank from '@/layouts/LayoutBlank'
import LayoutContent from '@/layouts/LayoutContent'
export default {
  name: 'App',
  components: {
    LayoutBlank,
    LayoutContent
  },
  setup() {
    const {route} = useRouter()
    const  resolveLayout = computed(()=>{
      if(route.value.name === null) return null
      if(route.value.meta.layout === 'blank') return 'layout-blank'
      return 'layout-content'
    })

    return {
      resolveLayout
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
</style>
