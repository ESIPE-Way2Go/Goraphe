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
                  v-model.trim="select"
                  :loading="loading"
                  :items="items.map(i => i.label).filter((val,i)=>i<4)"
                  v-model:search="search"
                  class="mx-4"
                  density="default"
                  label="Rechercher une destination"
                  clearable
                  variant="outlined"
                  @update:modelValue="selectedSearch"
              ></v-autocomplete>
            </v-row>

            <v-row align="start" class="mt-1">
              <v-col cols="10">
                <v-text-field variant="outlined" v-model.trim="name" :rules="nameRules" label="Nom de la simulation"></v-text-field>
              </v-col>
              <v-col cols="2" align-self="start">
                <v-btn prepend-icon="mdi-chevron-left" @click.stop="close= !close" flat size="large"></v-btn>
              </v-col>
            </v-row>
            <v-select
                variant="outlined"
                chips
                label="Type de routes"
                :items=roadTypes
                v-model.trim="selectedRoadTypes"
                multiple
                clearable
                closable-chips
            ></v-select>
            <v-btn variant="text" class="mb-3" :prepend-icon="(optionsOpen)? 'mdi-chevron-up':'mdi-chevron-down'"
                   @click.stop="optionsOpen= !optionsOpen">Options avancées
            </v-btn>
            <v-scroll-y-transition>
              <div class="d-flex flex-column justify-content-between" v-if="optionsOpen">
                <v-text-field variant="outlined" v-model.trim="start" label="Début" density="compact"
                              @update:modelValue="updateStart"></v-text-field>
                <v-btn density="compact" class="mb-3" variant="text" prepend-icon="mdi-swap-vertical"
                       @click="swapPoints"></v-btn>
                <v-text-field variant="outlined" v-model.trim="end" label="Fin" density="compact"
                              @update:modelValue="updateEnd"></v-text-field>
                <v-text-field variant="outlined" v-model.trim="center" label="Centre" density="compact"
                              @update:modelValue="circleUpdate"></v-text-field>
                <v-text-field variant="outlined" v-model.number.trim="randomPoints" label="Nombres de points Random"
                              type="number" density="compact"
                              :min="2"
                              max="100" step="1"></v-text-field>
                <v-text-field variant="outlined" v-model.trim="desc" label="Description" density="compact"></v-text-field>
                <div class="d-flex align-start">
                  <v-text-field class="w-75" variant="outlined" v-model.number.trim="dist" label="Distance (mètre)"
                                density="compact"
                                type="number" :min="minDist"
                                max="100000" step="10" @change="circleChange"></v-text-field>
                  <v-btn class="w-25 pt-1" prepend-icon="mdi-refresh" @click.stop="dist = minDist; circleChange()" flat
                         size="large"></v-btn>
                </div>
              </div>
            </v-scroll-y-transition>
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
        this.circle_center.remove();
        this.circle_center = L.circle(this.center, {radius: this.dist}).addTo(this.map);
      }
    },
    search(val) {
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
      center: [48.8405364, 2.5843466],
      loading: false,
      items: [],
      search: null,
      select: null,
      //end search bar
      optionsOpen: false,
      control: null,
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
      randomPoints: 2,
      allowedChars: /^[a-zA-Z0-9_-]+$/,
      nameRules: [
        value => {
          if (value === '')
            return 'Ce champ ne doit pas être vide'
          return this.allowedChars.test(value)?true:"Name cannot contain any special characters";
        },
      ],
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

    const control = L.Routing.control({
      show: false,
      showInstructions: false,
      routeWhileDragging: false,
      showAlternatives: false,
      lineOptions: {
        addWaypoints: false
      },
      createMarker: function (i, wp) {
        return L.marker(wp.latLng, {
          draggable: true,
          icon: L.icon({
            iconUrl: (i === 0) ? require('@/assets/pin.png') : require('@/assets/flag-2.png'),
            iconSize: [40, 40], // size of the icon
            iconAnchor: (i === 1) ? [6, 38] : [20, 40], // point of the icon which will correspond to marker's location
            shadowAnchor: [4, 62],  // the same for the shadow
          }),
        });
      },
      waypointMode: 'snap',
      waypoints: [
        L.latLng(48.8393560, 2.5859384),
        L.latLng(48.8400943, 2.5861805)
      ],
      router: L.Routing.mapbox(process.env.VUE_APP_MAPBOX_KEY)
    }).addTo(map);

    this.control = control;
    //need instance of circle with value
    this.circle_center = L.circle(this.center, {radius: 200}).addTo(map);

    this.control.on('routesfound', (e) => {
      this.start = [e.waypoints[0].latLng.lng, e.waypoints[0].latLng.lat];
      this.end = [e.waypoints[e.waypoints.length - 1].latLng.lng, e.waypoints[e.waypoints.length - 1].latLng.lat];
      this.length = e.routes[0] ? e.routes[0].summary.totalDistance : 0;
      this.center = this.getCenter(e.routes[0].coordinates.map(coord => [coord.lat, coord.lng]));

      //remove circle and update it
      this.circle_center.remove();
      this.circle_center = L.circle(this.center, {radius: this.dist}).addTo(map);
    });

    //create btn with DOM (please kill me)
    function createButton(label, container) {
      let btn = L.DomUtil.create('button', 'v-btn pa-2 ma-2 bg-accent', container);
      btn.setAttribute('type', 'button');
      btn.innerHTML = label;
      return btn;
    }

    //btn for change start or and of the Path with click interaction
    map.on('click', function (e) {
      let container = L.DomUtil.create('div', 'popup'),
          startBtn = createButton('Start ', container),
          destBtn = createButton('Fin', container);

      L.popup()
          .setContent(container)
          .setLatLng(e.latlng)
          .openOn(map);

      L.DomEvent.on(startBtn, 'click', function () {
        control.spliceWaypoints(0, 1, e.latlng);
        map.closePopup();
      });

      L.DomEvent.on(destBtn, 'click', function () {
        control.spliceWaypoints(control.getWaypoints().length - 1, 1, e.latlng);
        map.closePopup();
      });
    });


    this.map = map;
  },
  methods: {
    circleUpdate() {
      this.center = this.center.split(',').map(parseFloat);
      console.log(this.center);
      this.circle_center.remove();
      this.circle_center = L.circle(this.center, {radius: this.dist}).addTo(this.map);
    },

    circleChange() {
      this.circle_center.remove();
      this.circle_center = L.circle(this.center, {radius: this.dist}).addTo(this.map);
    },

    //search bar
    async querySelections(v) {
      const provider = new OpenCageProvider({
        params: {
          key: process.env.VUE_APP_SEARCH_KEY,
        },
      });
      this.loading = true
      this.items = await provider.search({query: v});
      this.loading = false
    },
    findOsmid(position){
      return fetch(`https://nominatim.openstreetmap.org/reverse?format=jsonv2&zoom=16&lat=${position[1]}&lon=${position[0]}`, {
        method: 'GET',
        headers: { 'Content-Type': `application/json`},
      }).then(response => {
        if (!response.ok) {
          throw new Error(`Request failed with status code: ${response.status}`);
        }
        return response.json();
      }).then(data => data.osm_id)
    },

    swapPoints() {
      //console.log(this.control._plan._waypoints)
      this.control.setWaypoints([
        L.latLng(this.control._plan._waypoints[1].latLng.lat, this.control._plan._waypoints[1].latLng.lng),
        L.latLng(this.control._plan._waypoints[0].latLng.lat, this.control._plan._waypoints[0].latLng.lng)
      ])
    },

    selectedSearch() {
      //console.log(this.select)
      if (this.select === null) return;
      let value = this.items.filter(i => i.label === this.select);
      if (value.length < 1) return;
      // console.log(this.value)
      this.map.setView([value[0].y, value[0].x], 15);
      this.control.setWaypoints([
        L.latLng(value[0].y, value[0].x),
        L.latLng(value[0].y, value[0].x)
      ])
      //console.log(this.control.waypoints)
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
      const lats = coordinates.map(coord => coord[0]);
      const lngs = coordinates.map(coord => coord[1]);

      const topLeft = [Math.max(...lats), Math.min(...lngs)];
      const bottomRight = [Math.min(...lats), Math.max(...lngs)];

      const lat1 = topLeft[0] * Math.PI / 180;
      const lng1 = topLeft[1] * Math.PI / 180;
      const lat2 = bottomRight[0] * Math.PI / 180;
      const lng2 = bottomRight[1] * Math.PI / 180;

      const Bx = Math.cos(lat2) * Math.cos(lng2 - lng1);
      const By = Math.cos(lat2) * Math.sin(lng2 - lng1);
      const lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
      const lng3 = lng1 + Math.atan2(By, Math.cos(lat1) + Bx);

      return [lat3 * 180 / Math.PI, lng3 * 180 / Math.PI];
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
        this.toast.error("Generation distance cannot be less than 100 or less that 60% of the route length");
        this.dist = this.minDist;
        return;
      }
      if (this.randomPoints < 2 || this.randomPoints > 100) {
        this.toast.error("Number of random points must be between 2 and 100");
        return;
      }
      //console.log("length = " + this.length * 0.6)
      //console.log("dist = " + this.dist)
      try {
        let randomPoints = this.$data.randomPoints;
        let center = this.center;
        let name = this.$data.name;
        let desc = this.$data.desc;
        let start = this.start;
        let end = this.end;
        let distance = this.dist;
        let roadTypes = this.$data.selectedRoadTypes;
        let script = this.$data.script;

        let start_id = await this.findOsmid(start);
        let end_id = await this.findOsmid(end);

        //console.log(start_id)
        //console.log(end_id)
        if(start_id===undefined){
          return;
        }
        let body = JSON.stringify({start, end, distance, name, desc, roadTypes, script, center,randomPoints,start_id,end_id});

        const response = await fetch('/api/simulation', {
          method: 'POST',
          headers: authHeader(),
          body: body
        });

        if (!response.ok) {
          throw new Error(`Request failed with status code: ${response.status}`);
        }

        const data = await response.json();
        //console.log(data['simulationId']);
        this.$router.push({name: 'simulation', params: {id: data['simulationId']}});

      } catch (error) {
        console.error(error);
      }
    }
  },
};

</script>

<style>

.popup {
  display: flex;
  flex-direction: column;
}

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
  width: 35% !important;
  z-index: 999;

}

.panel-map-lg {
  width: 25% !important;
  z-index: 999;
}


.panel-search-bar {
  width: 300px;
  height: 40px;
  left: 50%;
  margin-left: -250px; /* Negative half of width. */
}

.panel-burger {
  z-index: 999;
}


</style>
