<template>
  <div id="map" class="map"></div>
  <div>
    <v-text-field v-model="name" label="Name"></v-text-field>
    <v-text-field v-model="desc" label="Description"></v-text-field>
    <button @click="makePostRequest()"> SEND</button>
  </div>
</template>
<script>
import L from 'leaflet';
import 'leaflet-routing-machine';
import authHeader from "@/services/auth-header";
import {useTheme} from "vuetify";


export default {
  setup() {
    const theme = useTheme();
    return {
      theme,
      toggleTheme: () => theme.global.name.value = theme.global.current.value.dark ? 'myCustomLightTheme' : 'dark',
    }
  },

  name: 'TestSearch',
  data() {
    return {
      name: "",
      desc: "",
      map: null,
      waypoints: []
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
    async makePostRequest() {
      class Point {
        constructor(x, y) {
          this.x = x;
          this.y = y;
        }
      }

      try {
        let coordinates = this.waypoints.map(coord => new Point(coord[0], coord[1]));
        const name = this.$data.name;
        const desc = this.$data.desc;
        let start = coordinates.pop();
        let startX = start.x;
        let startY = start.y;

        let end = coordinates.pop();
        let endX = end.x;
        let endY = end.y;
        const distance = 100;
        let body = JSON.stringify({startX, startY, endX, endY, distance, name,desc});
        const response = await fetch('/api/simulation', {
          method: 'POST',
          headers: authHeader(),
          body: body
        });

        if (!response.ok) {
          throw new Error(`Request failed with status code: ${response.status}`);
        }

        const data = await response.json();
        console.log(data);
        // this.$router.push({name: 'logsSimulation', params: {id: data}});
      } catch (error) {
        console.error(error);
      }
    }
  },


};

</script>

<style>
.map {
  width: 100%;
  height: 70%;
}

.leaflet-routing-container {
  display: none !important;
}

</style>
