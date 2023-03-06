<template>
  <div class="mx-auto w-75 mt-5">
    <v-card class="mb-10">
      <v-card-title style="font-size: 30px">
        Simulation : {{ name }}
      </v-card-title>
      <v-card-subtitle v-if="description !== null">
        {{ description }}
      </v-card-subtitle>
      <div class="d-flex">
        <v-img class="ma-2 rounded-1" max-height="300" max-width="35%" :src=imageTest cover></v-img>

        <div class="d-flex flex-column flex-grow-1 flex-shrink-0">
          <div class="d-flex justify-content-between">
            <div class="flex-column">
              <div class="ma-2 pa-1 h5 text-accent text-overline font-weight-bold">
                Status
              </div>
              <div class="ma-2 pa-1 h5  text-caption text-uppercase font-weight-bold">
                <v-badge :color="(status === 'ERROR') ? 'error' : 'success'" content="" dot inline></v-badge>
                {{ status }}
              </div>
            </div>
            <div class="flex-column">
              <div class="ma-2 pa-1 h5 text-accent text-overline font-weight-bold">Module</div>
              <v-sheet class="ma-2 pa-1 h5  text-caption font-weight-bold text-uppercase">{{
                  computingScript
                }}
              </v-sheet>
            </div>
            <div class="flex-column">
              <div class="ma-2 pa-1 h5 text-accent text-overline font-weight-bold">Duration</div>
              <v-sheet class="ma-2 pa-1 h5  text-caption font-weight-bold text-uppercase">{{ duration }}</v-sheet>
            </div>

          </div>
          <v-divider inset></v-divider>
          <div class="d-flex justify-space-between">
            <div class="flex-column">
              <div class="ma-2 pa-1 h5 text-accent text-uppercase text-overline font-weight-bold ">Routes choisies</div>
              <div>
                <div class="d-flex">
                  <v-sheet v-for="road in roads" :key=road>
                    <v-chip variant="elevated" class="ma-2 text-caption font-weight-bold text-uppercase">
                      {{ road }}
                    </v-chip>
                  </v-sheet>
                </div>
              </div>
            </div>
            <div class="d-flex flex-column">
              <v-btn color="danger" class="ma-2 align-self-center" @click="cancelSimulation" variant="outlined"
                     v-if="(status === 'LOAD')">
                Annuler la simulation
              </v-btn>
              <v-btn color="primary" class="ma-2 align-self-center" @click="gotoSimulationMap" variant="outlined"
                     v-if="(status === 'SUCCESS')">
                Résultat
              </v-btn>
            </div>
          </div>
        </div>
      </div>

    </v-card>


    <v-card class="mx-auto">
      <template v-slot:title>
        <div class="d-flex align-items-center">
          <div class="ma-2 pa-1 h5  text-overline font-weight-bold">Logs</div>
          <div class="text-center d-flex">
            <v-btn class="ma-2 font-weight-bold" @click="all" size="small" variant="tonal" v-if="panel.length === 0">
              Afficher tous
            </v-btn>
            <v-btn class="ma-2 font-weight-bold" @click="none" size="small" variant="tonal" v-if="panel.length > 0">
              Cacher tous
            </v-btn>
          </div>
        </div>
      </template>
      <v-card-text>
        <div>

          <v-expansion-panels v-model="panel" multiple>
            <v-expansion-panel v-for="log in logs" :key="log.script" :value=log.script>
              <template v-slot:title>
                <div class="mr-2">
                  <v-icon icon="mdi-check-circle" color="success" v-if="log.status === 'SUCCESS'"></v-icon>
                  <v-icon icon="mdi-close" color="error" v-else-if="log.status === 'ERROR'"></v-icon>
                  <v-progress-circular color="dark-blue" style="height: 18px" indeterminate width="3"
                                       v-else-if="log.status === 'LOAD'"></v-progress-circular>
                  <v-icon icon="mdi-launch" color="grey" v-else></v-icon>
                </div>
                <div>{{ log.script }}</div>
              </template>
              <template v-slot:text>
                <h5>{{ log.status }}</h5>
                <div v-for="content in log.content" :key=content>
                  {{ content }}
                </div>
              </template>
            </v-expansion-panel>
          </v-expansion-panels>
        </div>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
import authHeader from "@/services/auth-header";
import {useToast} from "vue-toastification";

export default {
  setup() {
    const toast = useToast();
    return {toast}
  },
  data() {
    return {
      panel: [],
      allLog: [],
      test: 0,
      status: "NOT LAUNCH",
      duration: "0",
      computingScript: "",
      distance: "",
      roads: [],
      logs: [],
      progession: 0,
      id: this.$route.params.id,
      intervalIds: [], // array to hold interval IDs
      imageTest: require('@/assets/paris.png'),
    }
  },

  methods: {

    gotoSimulationMap(){
      this.$router.push({name: 'simulationMap', params: {id: this.id}});
    },

    all() {
      this.panel = this.allLog
    },
    none() {
      this.panel = []
    },
    getSimulation() {
      fetch("/api/simulation/" + this.id, {
        method: "GET",
        headers: authHeader(),
      })
          .then(response => response.json())
          .then(data => {
            this.name = data['name']
            this.description = data['description']
            this.computingScript = data['computingScript']
            this.roads = data['roads']
            this.distance = data['distance']
            this.status = data['status']
            this.getDuration(data)
            this.allLog = []
            this.logs = []
            data['logResponses'].forEach(elt => {
              this.logs.push({script: elt['scriptName'], status: elt['status'], content: elt['content']})
              this.allLog.push(elt['scriptName'])
            });
            if (this.logs !== []) {
              this.progession = this.logs.length / 3 * 100
            }
          });
    },
    cancelSimulation() {
      fetch(`/api/simulation/${this.id}/cancel`, {
        method: "DELETE",
        headers: authHeader(),
      }).then(response => {
        if (response.ok) {
          this.toast.success(`Annulation de la simulation réussi`)
        } else {
          this.toast.error(`Erreur dans l'annulation de la simulation`)
        }
      })
    },
    getDuration(data) {
      if (data['beginDate'] === null) {
        return;
      }
      let endDate = data['endDate']
      let duration = 0
      if (endDate === null) {
        duration = Date.now() - new Date(data['beginDate'])
      } else {
        duration = new Date(data['endDate']) - new Date(data['beginDate'])
        this.intervalIds.forEach((id) => clearInterval(id));
      }
      // Result in minutes
      this.duration = this.secondsToTimeString(duration)
    },
    secondsToTimeString(seconds) {
      seconds /= 1000;
      const hours = Math.floor(seconds / 3600);
      const minutes = Math.floor((seconds - hours * 3600) / 60);
      const remainingSeconds = Math.round(seconds - hours * 3600 - minutes * 60);
      let parts = "";
      if (hours > 0) {
        parts += hours.toString().padStart(2, '0') + " h ";
      }
      if (minutes > 0 || hours > 0) {
        parts += minutes.toString().padStart(2, '0') + " m ";
      }
      parts += remainingSeconds.toString().padStart(2, '0') + " s ";
      return parts;
    }
  },
  mounted() {
    this.intervalIds.push(setInterval(this.getSimulation, 1000));
  },
  beforeUnmount() {
    // clear all intervals before unmounting the component
    this.intervalIds.forEach((id) => clearInterval(id));
  },
}
</script>
