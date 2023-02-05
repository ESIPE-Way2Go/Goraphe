<template>
  <v-app :theme="myCustomLightTheme">
    <v-main>
      <slot></slot>
    </v-main>
  </v-app>
</template>

<script>

export default {
  setup() {
  },
}
</script>

<style scoped>

</style>