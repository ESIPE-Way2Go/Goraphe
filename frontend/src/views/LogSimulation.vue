<template>
  <div class="mx-auto" width="80%">
    <v-card class="mb-10">
      <div class="d-flex">
        <div class="flex-column">
          <h2 class="ma-2 pa-1">Status</h2>
          <v-sheet class="ma-2 pa-1 px-10 bg-light-blue rounded-pill">{{ status }}</v-sheet>
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
    <v-card class="mx-auto" width="80%">
      <template v-slot:title>
        Logs
      </template>
      <v-card-text>
        <div>
          <div class="text-center d-flex pb-4">
            <v-btn class="ma-2" @click="all">
              All
            </v-btn>
            <v-btn class="ma-2" @click="none">
              None
            </v-btn>
          </div>
          <v-expansion-panels v-model="panel" multiple>
            <v-expansion-panel v-for="log in logs" :key="log.script" :title=log.script :value=log.script>
              <template v-slot:text>
                <h5>{{  log.status }}</h5>
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
      status: "",
      duration: "",
      computingScript: "",
      distance: "",
      roads: [],
      logs: [],
      id: this.$route.params.id
    }
  },

  methods: {
    all() {
      console.log("test ", this.allLog)
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

          var endDate = data['endDate']
          if (endDate === null) {
            this.duration = new Date(data['beginDate']) - Date.now()
          }
          else {
            this.duration = new Date(data['endDate']) - new Date(data['beginDate'])
          }
          // Result in minutes
          this.duration = this.duration / 1000 / 60
          this.allLog = [];
          this.logs = [];
          data['logResponses'].forEach(elt => {
            this.logs.push({ script: elt['scriptName'], status: elt['status'], content: elt['content'] })
            this.allLog.push(elt['scriptName'])
            this.status = elt['status']
          });       
        })
    },
  },
  mounted() {
    setInterval( this.getLogs(), 10000)   
  }
}
</script>
