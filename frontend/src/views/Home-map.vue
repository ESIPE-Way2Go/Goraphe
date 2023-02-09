<template>
  <div class="flex-box">
    <div style="height:900px; width:1200px">
      <l-map ref="map" @click="addMarker" v-model:zoom="zoom" :center="[47.41322, -1.219482]" :minZoom="3"
             :maxZoom="18" :max-bounds="[[-90,-180],   [90,180]]">
        <l-tile-layer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            layer-type="base"
            name="OpenStreetMap"
            no-wrap="true"
        ></l-tile-layer>
        <l-marker v-for="(marker,index) in markers" :key="index"
                  :lat-lng="marker"
                  :draggable="true"
                  @dragend="updateMarker(index, marker)"
        ></l-marker>
      </l-map>
    </div>

    <div v-if="markers.length === 2">
      <button @click="saveMarkers()"> SAVE</button>
    </div>
  </div>
</template>

<script>
import "leaflet/dist/leaflet.css";
import {LMap, LMarker, LTileLayer} from "@vue-leaflet/vue-leaflet";

export default {
  components: {
    LMap,
    LTileLayer,
    LMarker,
  },
  data() {
    return {
      zoom: 2,
      markers: [],
    };
  },
  methods: {
    addMarker(event) {
      if (event.latlng != null && this.markers.length < 2) {
        this.markers.push(event.latlng);
      }
    },
    updateMarker(index, marker) {
      this.markers[index] = marker;

    },
    async getMarkers() {
      const response = await fetch("http://localhost/api/point/test");
      const data = await response.json();
      this.markers = []
      data.forEach(m => this.markers.push([m.latitude, m.longitude]));
      this.totalVuePackages = data.total;
    },
    async saveMarkers() {
      this.markers.forEach(marker => console.log(marker.lng +" " +marker.lat))
    },
  },
  created() {
    this.getMarkers();
  }
};

</script>

<style>
.flex-box{
  display: flex;

}
</style>