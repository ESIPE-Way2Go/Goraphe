<template>
  <v-card
      class="mx-auto"
      color="grey-lighten"
      max-width="400"
  >
    <v-card-text>
      <v-text-field
          :loading="loading"
          density="compact"
          variant="solo"
          label="Entrez numéro de compte"
          append-inner-icon="mdi-magnify"
          single-line
          hide-details
          @keyup.enter="onClick"
          @click:append-inner="onClick"
      ></v-text-field>
    </v-card-text>
  </v-card>
</template>

<script>
export default {
  name: "SearchBar.vue",
  data:() => ({
    loaded: false,
    loading:false
  }),
  methods: {
    //TODO requete pour trouver l'utilsateur VOIR QUOI FAIRE APRES
    onClick () {
      this.loading = true

      setTimeout(() => {
        this.loading = false
        this.loaded = true
      }, 2000)
    },
  },
}

</script>

<style scoped>

</style>