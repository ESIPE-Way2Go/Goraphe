<template>
  <div class="mx-auto w-75">
    <v-card class="mb-10">
      <div class="d-flex">
        <div class="flex-column">
          <h2 class="ma-2 pa-1">Status</h2>
          <v-sheet class="ma-2 pa-1 px-10 rounded-pill"
                   :style="{ 'backgroundColor': status === 'ERROR' ? '#F04438' : 'green' }">{{ status }}
          </v-sheet>
        </div>
        <div class="flex-column">
          <h2 class="ma-2 pa-1">Duration</h2>
          <v-sheet class="ma-2 pa-1 px-10 bg-light-blue rounded-pill">{{ duration }}</v-sheet>
        </div>
      </div>
      <div class="d-flex">
        <div class="flex-column">
          <h2 class="ma-2 pa-1">Module de résilience</h2>
          <v-sheet class="ma-2 pa-1 px-10 bg-light-blue rounded-pill">{{ computingScript }}</v-sheet>
        </div>
        <div class="flex-column">
          <h2 class="ma-2 pa-1">Distance de rendu</h2>
          <v-sheet class="ma-2 pa-1 px-10 bg-light-blue rounded-pill">{{ distance }}</v-sheet>
        </div>
      </div>
      <div>
        <h2>Types de routes</h2>
        <div class="d-flex">
          <v-sheet v-for="road in roads" :key=road>
            <v-sheet class="ma-2 pa-1 px-10 bg-light-blue rounded-pill">{{ road }}</v-sheet>
          </v-sheet>
        </div>
      </div>
    </v-card>

    <v-progress-linear :model-value=progession color="light-blue" height="20" :striped='progession != 100'
                       class="mb-10">
      <template v-slot:default="{ value }">
        <strong>{{ Math.ceil(value) }}%</strong>
      </template>
    </v-progress-linear>

    <v-card class="mx-auto">
      <template v-slot:title>
        Logs
      </template>
      <v-card-text>
        <div>
          <div class="text-center d-flex pb-4">
            <v-btn class="ma-2" @click="all">
              Afficher tous
            </v-btn>
            <v-btn class="ma-2" @click="none">
              Cacher tous
            </v-btn>
          </div>
          <v-expansion-panels v-model="panel" multiple>
            <v-expansion-panel v-for="log in logs" :key="log.script" :value=log.script>
              <template v-slot:title>
                <div class="mr-2">
                  <v-icon icon="mdi-check" class="bg-green" v-if="log.status === 'SUCCESS'"></v-icon>
                  <v-icon icon="mdi-close" class="bg-red" v-else-if="log.status === 'ERROR'"></v-icon>
                  <v-icon icon="mdi-check" class="bg-green" v-else></v-icon>
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

export default {
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
    }
  },

  methods: {
    all() {
      this.panel = this.allLog
    },
    none() {
      this.panel = []
    },
    getLogs() {
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
            this.getDuration(data)
            this.allLog = []
            this.logs = []
            data['logResponses'].forEach(elt => {
              this.logs.push({script: elt['scriptName'], status: elt['status'], content: elt['content']})
              this.allLog.push(elt['scriptName'])
              this.status = elt['status']
            });
            if (this.logs !== []) {
              this.progession = this.logs.length / 3 * 100
            }
          });
    },
    getDuration(data) {
      if (data['beginDate'] === null) {
        return;
      }
      var endDate = data['endDate']
      var duration = 0
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
      var parts = "";
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
    this.getLogs()
    this.intervalIds.push(setInterval(this.getLogs, 1000));
  },
  beforeUnmount() {
    // clear all intervals before unmounting the component
    this.intervalIds.forEach((id) => clearInterval(id));
  },
}
</script>
