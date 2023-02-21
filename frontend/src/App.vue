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
import LayoutMap from '@/layouts/LayoutMap'
export default {
  name: 'App',
  components: {
    LayoutBlank,
    LayoutContent,
    LayoutMap

  },
  setup() {
    const {route} = useRouter()
    const  resolveLayout = computed(()=>{
      if(route.value.name === null) return null
      switch (route.value.meta.layout) {
        case 'blank': return 'layout-blank';
        case 'map' : return 'layout-map';
        default : return 'layout-content';
      }
    })

    return {
      resolveLayout
    }
  }
}
</script>

<style>
#app {
  font-family: Inter, Helvetica, Arial, sans-serif !important;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
</style>
