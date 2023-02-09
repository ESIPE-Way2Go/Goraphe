<template>
  <div id="map" class="map"></div>
</template>

<script>
import L from 'leaflet';
import 'leaflet-routing-machine';

export default {
  name: 'TestSearch',
  data() {
    return {
      map: null
    };
  },
  mounted() {
    let map = L.map('map');
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);
    L.Routing.control({
      show: false,
      showInstructions: false,
      waypoints: [
        L.latLng(57.74, 11.94),
        L.latLng(57.6792, 11.949)
      ],
      routeWhileDragging: true,
    }).addTo(map);
  }
};
</script>

<style>
.map {
  position: absolute;
  width: 100%;
  height: 100%;
}
</style>
