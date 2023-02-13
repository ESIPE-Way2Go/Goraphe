<template>
  <div id="map" class="map"></div>
  <button @click="makePostRequest()"> SEND</button>
</template>
<script>
import L from 'leaflet';
import 'leaflet-routing-machine';

export default {
  name: 'TestSearch',
  data() {
    return {
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
        console.log(coordinates)
        const distance = 100;
        const response = await fetch('http://localhost:8080/api/map/newSimulation', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({coordinates, distance})
        });

        if (!response.ok) {
          throw new Error(`Request failed with status code: ${response.status}`);
        }

        const data = await response.json();
        console.log(data);
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
