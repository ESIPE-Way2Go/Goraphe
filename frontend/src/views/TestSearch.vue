<template>
  <v-container fluid style="padding: 10px">
    <v-row class="d-flex">
      <div id="map" class="map"></div>

      <v-btn icon="mdi-chevron-right" class="position-fixed mt-15 panel-burger ma-5" @click.stop="close= !close" v-if="close"></v-btn>
      <v-slide-y-transition>
      <v-card class="position-fixed pa-5 mt-15 panel-map ma-5 " :class="{'panel-map-lg' : lgAndUp,'panel-map-md': md}"
               v-if="!close">
        <v-form @submit.prevent="makePostRequest" v-if="!close">
          <v-row align="start">
            <v-col cols="10">
            <v-text-field variant="outlined" v-model="name" label="Name"></v-text-field>
            </v-col>
            <v-col cols="2">
              <v-btn prepend-icon="mdi-chevron-left" @click.stop="close= !close" flat size="x-large"></v-btn>
            </v-col>
          </v-row>
          <v-text-field variant="outlined" v-model="desc" label="Description"></v-text-field>
          <v-text-field variant="outlined" v-model.number="dist" label="Distance (mètre)" type="number" min="100"
                        max="10000" step="10"></v-text-field>
          <v-select
              variant="outlined"
              chips
              label="Select road types"
              :items=roadTypes
              v-model="selectedRoadTypes"
              multiple
              clearable
              closable-chips
          ></v-select>
          <v-text-field variant="outlined" v-model="script" label="Computing Script" disabled ></v-text-field>
          <v-btn type="submit" color="primary" v-if="selectedRoadTypes.length>0">Lancer la simulation</v-btn>
        </v-form>
      </v-card>
      </v-slide-y-transition>
    </v-row>
  </v-container>

</template>
<script>
import L from 'leaflet';
import 'leaflet-routing-machine';
import authHeader from "@/services/auth-header";
import {useDisplay, useTheme} from "vuetify";
import {useToast} from "vue-toastification";


export default {
  setup() {
    const theme = useTheme();
    const toast = useToast();
    const {sm, md, lgAndUp} = useDisplay()
    return {
      theme,
      toggleTheme: () => theme.global.name.value = theme.global.current.value.dark ? 'myCustomLightTheme' : 'dark',
      toast,
      sm, md, lgAndUp
    }
  },

  name: 'TestSearch',
  data() {
    return {
      name: "",
      desc: "",
      map: null,
      waypoints: [],
      roadTypes: ['motorway', 'trunk', 'primary', 'secondary', 'tertiary', 'residential', 'service'],
      selectedRoadTypes: ['motorway', 'trunk', 'primary', 'secondary', 'tertiary', 'residential', 'service'],
      dist: 100,
      script: "default",
      close: false,
    };
  },
  mounted() {
    let map = L.map('map', {
      maxBounds: [[-90, -180], [90, 180]],
      maxZoom: 18,
      minZoom: 3,
      zoomControl: false
    }).setView([48.83935609413248, 2.585938493701621], 16);
    L.control.zoom({
      position: 'bottomright'
    }).addTo(map);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);
    let control = L.Routing.control({
      show: false,
      showInstructions: false,
      routeWhileDragging: false,
      showAlternatives: false,
      lineOptions: {
        addWaypoints: false
      },
      waypoints: [
        L.latLng(48.83935609413248, 2.585938493701621),
        L.latLng(48.84009439586693, 2.586180556928124)
      ],
    }).addTo(map);
    control.on('routesfound', (e) => {
      this.waypoints = e.waypoints.map(w => [w.latLng.lat, w.latLng.lng]);
    });
  },
  methods: {
    uncheckRoadTypes() {
      this.selectedRoadTypes = [];
    },
    async makePostRequest() {
      if (!this.name) {
        this.toast.error("Simulation name cannot be empty");
        return;
      }

      class Point {
        constructor(x, y) {
          this.x = x;
          this.y = y;
        }
      }

      try {
        let coordinates = this.waypoints.map(coord => new Point(coord[0], coord[1]));
        let name = this.$data.name;
        let desc = this.$data.desc;
        let start = coordinates.pop();
        let startX = start.x;
        let startY = start.y;

        let end = coordinates.pop();
        let endX = end.x;
        let endY = end.y;
        let distance = 100;
        let roadTypes = this.$data.selectedRoadTypes;
        let script = this.$data.script;
        let body = JSON.stringify({startX, startY, endX, endY, distance, name, desc, roadTypes, script});
        const response = await fetch('/api/simulation', {
          method: 'POST',
          headers: authHeader(),
          body: body
        });

        if (!response.ok) {
          throw new Error(`Request failed with status code: ${response.status}`);
        }

        const data = await response.json();
        console.log(data['simulationId']);
        this.$router.push({name: 'simulation', params: {id: data['simulationId']}});
      } catch (error) {
        console.error(error);
      }
    }
  }
};

</script>

<style>
#map {
  position: relative;
  height: 100vh;
  width: 100%;
}

.leaflet-routing-container {
  display: none !important;
}


.panel-map {
  width: 90%;
  z-index: 999;

}

.panel-map-md {
  width: 30% !important;
  z-index: 999;

}

.panel-map-lg {
  width: 20% !important;
  z-index: 999;
}

.panel-burger{
  z-index: 999;
}


</style>
