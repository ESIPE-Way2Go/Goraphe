<template>
  <v-container class="bg-blue-grey-lighten-5 my-3" v-if="simulationsInLoad.length !== 0">
    <div class="text-capitalize text-uppercase font-weight-bold">Simulation en cours</div>
    <v-row no-gutters>
      <v-col v-for="simulation in simulationsInLoad" :key=simulation.id cols="12" sm="3" lg="2">
        <v-card class="ma-2">
          <v-img class="align-end text-white" height="200" :src=imageTest cover >
          </v-img>
          <v-card-title class="pt-4">
            {{ simulation.title }}
          </v-card-title>
          <v-card-subtitle> {{ simulation.date }}</v-card-subtitle>
          <v-card-actions class="justify-space-between">
            <v-btn color="orange" @click="goLogs(simulation.id)">
              Logs
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>

  <v-container class="" v-if="simulations.length !== 0">
    <div class="d-flex flex-column flex-md-row">
      <div class="pl-2 text-capitalize text-uppercase font-weight-bold">Simulations terminées ({{
          simulationsShow.length
        }})
      </div>
      <div :class="(mobile)? 'w-100 pl-2' : 'w-15 pl-2'">
        <v-text-field
            label="Recherche"
            variant="outlined"
            prepend-inner-icon="mdi-magnify"
            density="compact"
            @update:modelValue="querySelections"

        ></v-text-field>
      </div>
    </div>
    <v-divider></v-divider>
    <v-row>
      <v-col v-for="simulation in simulationsShow" :key=simulation.id cols="12" sm="6" md="4" lg="3" xl="2">
        <v-card class="ma-2">
          <v-img class="align-end text-white" height="200" :src=imageTest cover></v-img>
          <v-card-title>
            {{ simulation.title }}
          </v-card-title>
          <v-card-subtitle class="d-flex flex-column flex-md-row  align-md-start justify-content-between">
            {{ simulation.date }}
            <div class="h5 text-caption text-uppercase font-weight-bold">
              <v-badge
                  :color="(simulation.status === 'ERROR') ? 'error' : 'success'"
                  content=""
                  dot
                  inline
              ></v-badge>
              {{ (simulation.status === 'ERROR') ? 'error' : 'success' }}
            </div>
          </v-card-subtitle>
          <v-card-actions class="justify-space-between">
            <div>
              <v-btn color="secondary" @click="goSimulation(simulation.id)" v-if="simulation.status==='SUCCESS'">
                Résultat
              </v-btn>
              <v-btn color="secondary" @click="goLogs(simulation.id)">
                Logs
              </v-btn>
            </div>
            <v-btn icon="mdi-trash-can-outline" @click="deleteSimulationDialog(simulation)"></v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
  <v-container v-if="simulations.length === 0 && simulationsInLoad.length ===0">
    <v-card
        color="#385F73"
        rounded
        theme="dark"
        class="z-1">
      <v-card-title class="text-h5">
        Aucune simulation
      </v-card-title>

      <v-card-subtitle>Commencer une simulation maintenant !</v-card-subtitle>

      <v-card-actions>
        <v-btn variant="text" @click="this.$router.push({ name: 'map'})">
          Créer une simulation
        </v-btn>
      </v-card-actions>
    </v-card>

    <v-img

        :src="require('@/assets/location.png')"
        class="w-100 img-no-item"
        max-height="500"

    ></v-img>

  </v-container>


  <v-dialog v-model="deleteDialog" max-width="600">
    <v-card>
      <v-toolbar color="primary">
        <v-toolbar-title>Suppression de la simulation <b>{{ simulationTitle }}</b></v-toolbar-title>
      </v-toolbar>

      <v-card-text>
        Attention ! Vous êtes sur le point de supprimer définitivement la simulation <b>{{
          simulationTitle
        }}</b>.
        Cette action est irréversible et entraînera la suppression de toutes les données de
        cette simulation, y compris tous les résultats et les analyses associées.
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="grey" @click="deleteDialog = false">Annuler</v-btn>
        <v-btn color="danger" variant="text" @click="deleteSimulation(simulationId)">
          Supprimer
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import authHeader from "@/services/auth-header";
import {useToast} from "vue-toastification";
import {useDisplay} from 'vuetify'

export default {
  setup() {
    const toast = useToast();
    const {mobile} = useDisplay()

    return {toast,mobile}
  },
  data() {
    return {
      simulationsInLoad: [],
      simulationsShow: [],
      simulations: [],
      imageTest: require('@/assets/paris.png'),
      deleteDialog: false,
      simulationId: -1,
      simulationTitle: "",

    }
  },
  watch: {
    search(val) {
      this.querySelections(val)
    },
  },

  methods: {
    querySelections(v) {
      this.simulationsShow = this.simulations.filter(e => e.title.toLowerCase().includes(v.toLowerCase()))
      this.loading = false
    },

    getSimuations() {
      const simulationsInLoad = []
      const simulations = []
      fetch("/api/simulation/", {
        method: "GET",
        headers: authHeader(),
      })
          .then(response => response.json())
          .then(data => {
            data.forEach(element => {
              let elt = {
                id: element['id'],
                title: element['title'],
                date: this.getFormatDate(element['beginDate']),
                status: element['status']
              };
              if (element['endDate'] === null)
                simulationsInLoad.push(elt)
              else
                simulations.push(elt)
            });

            this.simulationsInLoad = simulationsInLoad
            this.simulations = simulations
            this.simulationsShow = simulations;
          });
    },
    goSimulation(id) {
      this.$router.push({name: 'simulationMap', params: {id: id}});
    },
    goLogs(id) {
      this.$router.push({name: 'simulation', params: {id: id}});
    },
    getFormatDate(date) {
      let d = new Date(date);
      const year = d.getFullYear();
      const month = d.getMonth() + 1;
      const day = d.getDate();
      return `${day.toString().padStart(2, '0')}/${month.toString().padStart(2, '0')}/${year}`;
    },
    deleteSimulationDialog(simulation) {
      this.simulationId = simulation.id
      this.simulationTitle = simulation.title
      this.deleteDialog = true
    },
    deleteSimulation(id) {
      this.deleteDialog = false
      fetch("/api/simulation/" + id, {
        method: "DELETE",
        headers: authHeader(),
      }).then(response => response.json())
          .then(data => {
            if (data === true) {
              this.toast.success(`Suppression de la simulation réussi`)
              this.simulations = []
              this.getSimuations()
            } else {
              this.toast.error(`Suppression de la simulation pas réussi`)
            }
          });
    }
  },
  mounted() {
    this.getSimuations()
  }
}
</script>

<style>
.img-no-item {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.w-15{
  width: 15% !important;
}
</style>