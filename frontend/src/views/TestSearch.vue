<template>
  <div id="map" class="map"></div>
   <div>
         <v-text-field v-model="name" label="Name"></v-text-field>
        <button @click="makePostRequest()"> SEND</button>
   </div>
</template>
<script>
import L from 'leaflet';
import 'leaflet-routing-machine';
import authHeader from "@/services/auth-header";

export default {
  name: 'TestSearch',
  data() {
    return {
      name: "",
      map: null,
      waypoints: []
    };
  },
  mounted() {
    let map = L.map('map');
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);
    let control = L.Routing.control({
      show: false,
      showInstructions: false,
      routeWhileDragging: true,
      showAlternatives: true,
      waypoints: [
        L.latLng(57.74, 11.94),
        L.latLng(57.6792, 11.949)
      ],
    }).addTo(map);
    control.on('routesfound', (e) => {
      this.waypoints = e.waypoints.map(w => [w.latLng.lat, w.latLng.lng]);
    });
  },
  methods: {
    async makePostRequest() {
      try {
        let coordinates = this.waypoints;
        const name = this.$data.name;
        console.log(coordinates)
        //const distance = 100;
        const response = await fetch('/api/simulation/', {
          method: 'POST',
          headers: authHeader(),
          body: JSON.stringify({ name })
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
.map {
  width: 100%;
  height: 95%;
}
</style>
