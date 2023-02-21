<template>
  <v-container fluid style="padding: 10px">
    <v-row class="d-flex">
      <div id="map" class="map"></div>

      <v-btn icon="mdi-chevron-right" class="position-fixed mt-15 panel-burger ma-5" @click.stop="close= !close"
             v-if="close"></v-btn>
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
            <v-text-field variant="outlined" v-model.number="dist" label="Distance (mètre)" type="number" :min="minDist"
                          max="100000" step="10"></v-text-field>
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
            <v-text-field variant="outlined" v-model="script" label="Computing Script" disabled></v-text-field>
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
  computed: {
    minDist() {
      let min=Math.max(this.length*0.6, 100);
      return min;
    },
  },
  watch: {
    minDist(newVal) {
      if (this.dist < newVal) {
        this.dist = newVal;
      }
    },
  },
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
      length: 0,
      center: [],
      start: [],
      end: [],
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
      this.start = [e.waypoints[0].latLng.lng, e.waypoints[0].latLng.lat];
      this.end = [e.waypoints[e.waypoints.length - 1].latLng.lng, e.waypoints[e.waypoints.length - 1].latLng.lat];
      this.length = e.routes[0] ? e.routes[0].summary.totalDistance : 0;
      this.center=this.getCenter(e.routes[0].coordinates.map(coord => [coord.lat, coord.lng]));
      console.log(this.center)
    });
  },
  methods: {
    getCenter(coordinates) {
      let x = 0, y = 0, z = 0;

      for (let i = 0; i < coordinates.length; i++) {
        let lat = coordinates[i][0] * Math.PI / 180;
        let lng = coordinates[i][1] * Math.PI / 180;

        x += Math.cos(lat) * Math.cos(lng);
        y += Math.cos(lat) * Math.sin(lng);
        z += Math.sin(lat);
      }

      const total = coordinates.length;

      x = x / total;
      y = y / total;
      z = z / total;

      const centralLongitude = Math.atan2(y, x);
      const centralSquareRoot = Math.sqrt(x * x + y * y);
      const centralLatitude = Math.atan2(z, centralSquareRoot);

      return [centralLatitude * 180 / Math.PI, centralLongitude * 180 / Math.PI];
    },
    uncheckRoadTypes() {
      this.selectedRoadTypes = [];
    },
    async makePostRequest() {
      if (!this.name) {
        this.toast.error("Simulation name cannot be empty");
        return;
      }
      if (this.dist < this.length * 0.6 || this.dist < 100) {
        this.toast.error("Generation distance cannot be less than 100 or less that 60% of the route length")
      }

      try {
        let center=this.center;
        let name = this.$data.name;
        let desc = this.$data.desc;
        let start = this.start;
        let end = this.end;
        let distance = 100;
        let roadTypes = this.$data.selectedRoadTypes;
        let script = this.$data.script;
        let body = JSON.stringify({start, end, distance, name, desc, roadTypes, script,center});
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
  },
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

.panel-burger {
  z-index: 999;
}


</style>
