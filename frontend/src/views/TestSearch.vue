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
          <v-row align="start" class="mt-1">
            <v-autocomplete
                v-model="select"
                :loading="loading"
                :items="items.map(i => i.label).filter((val,i)=>i<4)"
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
              <v-text-field variant="outlined" v-model="start" label="Start" @update:modelValue="updateStart"></v-text-field>
              <v-text-field variant="outlined" v-model="end" label="End" @update:modelValue="updateEnd"></v-text-field>
            <v-text-field variant="outlined" v-model="name" label="Name"></v-text-field>
              <div @click="swapPoints">SWAP</div>
            </v-col>
            <v-col cols="2" align-self="start">
              <v-btn prepend-icon="mdi-chevron-left" @click.stop="close= !close" flat size="large"></v-btn>
            </v-col>
          </v-row>
          <v-text-field variant="outlined" v-model="desc" label="Description"></v-text-field>
          <v-text-field variant="outlined" v-model.number="dist" label="Distance (mètre)" type="number" :min="minDist"
                        max="100000" step="10" @change="circleChange"></v-text-field>
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
  computed: {
    minDist() {
      return Math.max(this.length * 0.6, 100);
    },
  },
  watch: {
    minDist(newVal) {
      if (this.dist < newVal) {
        this.dist = newVal;
      }
    },
    search (val) {
      val && val !== this.select && this.querySelections(val)
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
      length: 0,
      start: [],
      end: [],
      //test
      circle_center: [],

    };
  },
  mounted() {
    let map = L.map('map', {
      maxBounds: [[-90, -180], [90, 180]],
      maxZoom: 18,
      minZoom: 3,
      zoomControl: false
    }).setView([48.8393560, 2.5859384], 16);
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
      createMarker: function(i, wp) {
          return L.marker(wp.latLng, {
            draggable: true,
            icon:L.icon({
              iconUrl: (i===0) ? require('@/assets/pin.png') :require('@/assets/flag.png') ,
              iconSize:     [40, 40], // size of the icon
              iconAnchor:  (i===1) ? [6, 38] : [20, 40], // point of the icon which will correspond to marker's location
              shadowAnchor: [4, 62],  // the same for the shadow
            }),
          });
        },
      waypointMode:'snap',
      waypoints: [
        L.latLng(48.83935609413248, 2.585938493701621),
        L.latLng(48.84009439586693, 2.586180556928124)
      ],
      router: L.Routing.mapbox('pk.eyJ1IjoibWV4aW1hIiwiYSI6ImNsZWd2djNkdDBwc3gzcXR0ZXB3Nmt6dDQifQ.GeKKqQmsdu8WhrePgFj2ww')
    }).addTo(map);

    this.circle_center =  L.circle(this.center, {radius: 200}).addTo(map);

    this.control.on('routesfound', (e) => {
      this.start = [e.waypoints[0].latLng.lng, e.waypoints[0].latLng.lat];
      this.end = [e.waypoints[e.waypoints.length - 1].latLng.lng, e.waypoints[e.waypoints.length - 1].latLng.lat];
      this.length = e.routes[0] ? e.routes[0].summary.totalDistance : 0;
      this.center = this.getCenter(e.routes[0].coordinates.map(coord => [coord.lat, coord.lng]));
      console.log(this.center);
      this.circle_center.remove();
      this.circle_center =  L.circle(this.center, {radius: this.minDist}).addTo(map);
    });
  this.map = map;
  },
  methods: {

    circleChange(){
      this.circle_center.remove();
      this.circle_center =  L.circle(this.center, {radius: this.dist}).addTo(this.map);
    },

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

    swapPoints(){
      console.log(this.control._plan._waypoints)
      this.control.setWaypoints([
        L.latLng(this.control._plan._waypoints[1].latLng.lat,this.control._plan._waypoints[1].latLng.lng),
        L.latLng(this.control._plan._waypoints[0].latLng.lat,this.control._plan._waypoints[0].latLng.lng)
      ])
    },

    selectedSearch(){
      //console.log(this.select)
      if(this.select===null) return;
      let value = this.items.filter(i => i.label === this.select);
      if(value.length<1) return;
     // console.log(this.value)
      this.map.setView([value[0].y,value[0].x], 15);
      this.control.setWaypoints([
        L.latLng(value[0].y, value[0].x),
        L.latLng(value[0].y, value[0].x)
      ])
      console.log(this.control.waypoints)
    },
    updateStart() {
      const startLatLng = this.start.split(',').reverse().map(parseFloat);
      const endLatLng = this.end.toString().split(',').reverse().map(parseFloat);
      this.control.setWaypoints([
        L.latLng(startLatLng[0], startLatLng[1]),
        L.latLng(endLatLng[0], endLatLng[1])
      ]);
    },
    updateEnd() {
      const startLatLng = this.start.toString().split(',').reverse().map(parseFloat);
      const endLatLng = this.end.split(',').reverse().map(parseFloat);
      this.control.setWaypoints([
        L.latLng(startLatLng[0], startLatLng[1]),
        L.latLng(endLatLng[0], endLatLng[1])
      ]);
    },

    //end search bar
    getCenter(coordinates) {
      let x = 0, y = 0, z = 0;

      for (const coordinate of coordinates) {
        const lat = coordinate[0] * Math.PI / 180;
        const lng = coordinate[1] * Math.PI / 180;

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
        this.dist = this.minDist
        return;
      }
      console.log("length = " + this.length * 0.6)
      console.log("dist = " + this.dist)
      try {
        let center = this.center;
        let name = this.$data.name;
        let desc = this.$data.desc;
        let start = this.start;
        let end = this.end;
        let distance = this.dist;
        let roadTypes = this.$data.selectedRoadTypes;
        let script = this.$data.script;
        let body = JSON.stringify({start, end, distance, name, desc, roadTypes, script, center});
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
