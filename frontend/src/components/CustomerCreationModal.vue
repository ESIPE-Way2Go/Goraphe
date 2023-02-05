<template>
  <v-btn
      variant="flat"
      color="primary"
      rounded="lg"
      @click="dialogCreatUser=true"
  class="mr-2">
    Créer un client
  </v-btn>

  <v-dialog
      v-model="dialogCreatUser"
      fullscreen

  >
    <v-card >
      <v-toolbar
          dark
          color="primary"
      >
        <v-btn
            icon
            dark
            @click="dialogCreatUser = false"
        >
          <v-icon>mdi-close</v-icon>
        </v-btn>
        <v-toolbar-title></v-toolbar-title>
        <v-spacer></v-spacer>
      </v-toolbar>
      <CustomerCreation @close="dialogCreatUser=false"></CustomerCreation>
    </v-card>
  </v-dialog>
</template>

<script>
import CustomerCreation from "@/components/CustomerCreation";
export default {
  name: "CustomerCreationModal",
  components: {CustomerCreation},
  setup(){
    return{

    }
  },
  data() {
    return{
      dialogCreatUser: false,
    }
  },
}
</script>

<style scoped>

</style>