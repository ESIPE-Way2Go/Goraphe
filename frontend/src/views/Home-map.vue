<template>
  <div class="flex-box">
    <div style="height:900px; width:1200px">
      <l-map ref="map" @click="selectMarker" v-model:zoom="zoom" :center="[47.41322, -1.219482]" :minZoom="3"
             :maxZoom="18" :max-bounds="[[-90,-180],   [90,180]]">
        <l-tile-layer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            layer-type="base"
            name="OpenStreetMap"
            no-wrap="true"
        ></l-tile-layer>
        <l-marker v-for="(marker,index) in markers" :key="index"
                  :lat-lng="marker"
                  @click="removeMarker(index)">
        </l-marker>
        <l-marker class="selected" v-if="selectedMarker!=null" :lat-lng="selectedMarker" >

          <LIcon :icon-url=icon.url_icon
                 :shadow-url=icon.shadow_url
                 :icon-size=icon.icon_size
                 :icon-anchor=icon.icon_anchor
                 :popup-anchor=icon.popup_anchor
                 :shadow-size=icon.shadow_size></LIcon>

        </l-marker>
      </l-map>
    </div>


    <div v-if="selectedMarker!=null">
      <button @click="saveMarker()"> SAVE</button>
    </div>
  </div>
</template>

<script>
import "leaflet/dist/leaflet.css";
import {LIcon, LMap, LMarker, LTileLayer} from "@vue-leaflet/vue-leaflet";

export default {
  components: {
    LMap,
    LTileLayer,
    LMarker,
    LIcon,
  },
  data() {
    return {
      selectedMarker: null,
      zoom: 2,
      markers: [],
      icon: {
        color: "green",
        url_icon: `https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png`,
        shadow_url: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png",
        icon_size: "[25, 41]",
        icon_anchor: "[12, 41]",
        popup_anchor: "[1, -34]",
        shadow_size: "[41, 41]",
      }
    };
  },
  methods: {
    removeMarker(index) {
      this.markers.splice(index, 1);
    },
    selectMarker(event) {
      if (event.latlng != null) {
        this.selectedMarker = (event.latlng);
      }
      console.log(event)
    },
    async getMarkers() {

      const response = await fetch("http://localhost/api/point/test");
      const data = await response.json();
      this.markers = []
      data.forEach(m => this.markers.push([m.latitude, m.longitude]));
      this.totalVuePackages = data.total;
    },
    async saveMarker() {
      const requestOptions = {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
          latitude: this.selectedMarker.lat.toString(),
          longitude: this.selectedMarker.lng.toString()
        })
      };
      await fetch(`http://localhost/api/point/save`, requestOptions);
      //await fetch(`http://localhost/api/point/save?latitude=${this.selectedMarker.lat}&longitude=${this.selectedMarker.lng}`,requestOptions);

      await this.getMarkers();
    },
  },


  created() {
    this.getMarkers();
  }


};
</script>

<style>
.selected {
  background-color: fuchsia;
}
.flex-box{
  display: flex;

}
</style>