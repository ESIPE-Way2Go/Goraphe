<template>
  <v-container fluid style="padding: 10px">
    <v-row class="d-flex">
      <div id="map" class="map"></div>

      <v-btn icon="mdi-chevron-right" class="position-fixed mt-15 panel-burger ma-5" @click.stop="close= !close" v-if="close"></v-btn>
      <v-slide-y-transition>
      <v-card class="position-fixed pa-5 mt-15 panel-map ma-5 " :class="{'panel-map-lg' : lgAndUp,'panel-map-md': md}"
               v-if="!close">
        <v-form @submit.prevent="makePostRequest" v-if="!close">
          <v-row align="start" class="mt-1">
            <v-autocomplete
                v-model="select"
                :loading="loading"
                :items="items.map(items => items.label).filter((val,i)=>i<4)"
                v-model:search="search"
                class="mx-4"
                density="default"
                label="Rechercher une déstination"
                clearable
                variant="outlined"
                @update:modelValue="selectedSearch"
            ></v-autocomplete>
          </v-row>

          <v-row align="start" class="mt-1">
            <v-col cols="10">
            <v-text-field variant="outlined" v-model="name" label="Name"></v-text-field>
            </v-col>
            <v-col cols="2" align-self="start">
              <v-btn prepend-icon="mdi-chevron-left" @click.stop="close= !close" flat size="large"></v-btn>
            </v-col>
          </v-row>
          <v-text-field variant="outlined" v-model="desc" label="Description"></v-text-field>
          <v-text-field variant="outlined" v-model.number="dist" label="Distance (mètre)" type="number" min="100"
                        max="10000" step="10"></v-text-field>
          <v-select
              variant="outlined"
              chips
              label="Type de routes"
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
import {OpenCageProvider} from "leaflet-geosearch";


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



  watch: {
    search (val) {
      val && val !== this.select && this.querySelections(val)
    },
  },


  data() {
    return {
      //search bar
      center : [48.8405364, 2.5843466],
      loading: false,
      items: [],
      search: null,
      select: null,
      //end

      control:null,

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
    this.control = L.Routing.control({
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
      router: L.Routing.mapbox('pk.eyJ1IjoibWV4aW1hIiwiYSI6ImNsZWd2djNkdDBwc3gzcXR0ZXB3Nmt6dDQifQ.GeKKqQmsdu8WhrePgFj2ww')
    }).addTo(map);
    this.control.on('routesfound', (e) => {
      this.waypoints = e.waypoints.map(w => [w.latLng.lat, w.latLng.lng]);
    });
  this.map = map;
  },
  methods: {
    //search bar
    async querySelections (v) {
      const provider = new OpenCageProvider({
        params: {
          key: 'ab736f9a32a2477aaf2036de1dc4340d',
        },
      });
      this.loading = true
      this.items = await provider.search({ query: v });
      this.loading = false
    },
    selectedSearch(){
      //console.log(this.select)
      if(this.select===null) return;
      let value = this.items.filter(i => i.label === this.select);
      if(value.length<1) return;
     // console.log(this.value)
      this.map.setView([value[0].y,value[0].x], 15);
      console.log(value)
      this.control.setWaypoints([
        L.latLng(value[0].y, value[0].x),
        L.latLng(value[0].y, value[0].x)
      ])
      console.log(this.control.waypoints)
      //this.center = [value[0].y,value[0].x]
    },

    //end search bar

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

.panel-search-bar{
  width: 300px;
  height: 40px;
  left: 50%;
  margin-left: -250px; /* Negative half of width. */
}
.panel-burger{
  z-index: 999;
}


</style>
