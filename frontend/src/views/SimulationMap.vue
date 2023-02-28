<template>
  <v-container fluid style="padding: 10px">
    <v-row class="d-flex">
      <div class="vh-100 w-100">
        <l-map ref="map" v-model:zoom="zoom" :center="center" :bounds="bounds"
               :max-bounds="maxBounds" :options="{ zoomControl: false}"
        >
          <l-tile-layer
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
              layer-type="base"
              name="OpenStreetMap"
             attribution="OpenStreetMap contributors"
          ></l-tile-layer>
          <l-control-zoom position="bottomright" zoom-in-text="+" zoom-out-text="-" />
            <l-geo-json  :geojson="simulation.path"  :options-style="geoStyler" :visible=listLayout[0].isShow :options="optionsTest"   ></l-geo-json>
            <l-geo-json :geojson="simulation.randomPoints" :options-style="styleFunction" :visible=listLayout[1].isShow :options="optionsTest"  ></l-geo-json>
        </l-map>
      </div>

      <v-btn icon="mdi-chevron-right" class="position-fixed mt-15 panel-burger ma-5" @click.stop="closeleft= !closeleft"
             v-if="closeleft"></v-btn>
      <v-slide-y-transition>
        <v-card class="position-fixed pa-5 mt-15 panel-map ma-5 " :class="{'panel-map-lg' : lgAndUp,'panel-map-md': md}"
                v-if="!closeleft">
          <v-row align="start">
            <v-col cols="10">
              <v-list lines="two">
                <v-list-item
                    title="Titre"
                    variant="text"
                    rounded
                    :subtitle="simulation.name"
                >
                </v-list-item>
              </v-list>
            </v-col>
            <v-col cols="2">
              <v-btn prepend-icon="mdi-chevron-left" @click.stop="closeleft= !closeleft" flat size="x-large"></v-btn>
            </v-col>
          </v-row>

          <v-list lines="two">
            <v-list-item
                v-if="simulation.description.length>0"
                title="Description"
                variant="outlined"
                rounded
                :subtitle="simulation.description"
                class="ma-1"
            >
            </v-list-item>

            <v-list-item
                title="Module"
                variant="outlined"
                rounded
                :subtitle="simulation.computingScript"
                class="ma-1 border-primary"
            >
            </v-list-item>

            <v-item-group multiple selected-class="bg-purple">
              <div class="text-caption mb-2">Tags</div>
              <v-item
                  v-for="road in simulation.roads"
                  :key="road" >
                <v-chip density="comfortable" class="ma-1">
                  {{ road }}
                </v-chip>
              </v-item>
            </v-item-group>
          </v-list>
        </v-card>
      </v-slide-y-transition>


      <v-btn icon="mdi-chevron-right" class="position-fixed mt-15 panel-burger ma-5 panel-map-right" @click.stop="closeright= !closeright"
             v-if="closeright"></v-btn>
      <v-slide-y-transition>
        <v-card class="position-fixed pa-5 mt-15 panel-map ma-5 panel-map-right" :class="{'panel-map-lg' : lgAndUp,'panel-map-md': md}"
                v-if="!closeright">
          <v-row align="start">
            <v-col cols="10">

            </v-col>
            <v-col cols="2">
              <v-btn prepend-icon="mdi-chevron-left" @click.stop="closeright= !closeright" flat size="x-large"></v-btn>
            </v-col>
          </v-row>

          <v-list lines="two">

            <v-list-item
                v-for="layout in listLayout" :key="layout"
                :title="layout.name"
                variant="outlined"
                rounded
                class="ma-1"
            >
                <v-list-item-action>
                  <v-switch inset v-model="layout.isShow"></v-switch>
                </v-list-item-action>
            </v-list-item>
          </v-list>

          <v-list border variant="flat" rounded>
            <v-list-item-title class="ma-2 text-capitalize text-h6 text-center">
              Légende
            </v-list-item-title>
            <v-list-item
                v-for="legend in listLegends" :key="legend"
                variant=""
                rounded
                class="ma-1"
            >
              <v-list-item-action class="d-flex justify-space-around">
                <v-badge
                    :color="legend.color"
                    inline
                    bordered
                ></v-badge>
                <div class="text-uppercase font-weight-bold ">
                  {{legend.start}} > LoS > {{legend.end}}
                </div>
              </v-list-item-action>
            </v-list-item>
          </v-list>
        </v-card>
      </v-slide-y-transition>

    </v-row>
  </v-container>

</template>
<script>

import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { LMap, LTileLayer,LGeoJson, LControlZoom} from "@vue-leaflet/vue-leaflet";
import authHeader from "@/services/auth-header";
import {useDisplay, useTheme} from "vuetify";
import {useToast} from "vue-toastification";
import {latLngBounds} from "leaflet/src/geo";

export default {
  setup() {
    const theme = useTheme();
    const toast = useToast();
    const {sm, md, lgAndUp} = useDisplay();


    return {
      listLegends: [
        {color:"#FFFFB2",start:0,end:100,titre:"LOS"},
        {color:"#FED976",start:100,end:200,titre:"LOS"},
        {color:"#FEB24C",start:200,end:300,titre:"LOS"},
        {color:"#F03B20",start:300,end:400,titre:"LOS"},
        {color:"#BD0026",start:400,end:500,titre:"LOS"},
        {color:"#BE1D9B",start:500,end:600,titre:"LOS"}],

      theme,
      toggleTheme: () => theme.global.name.value = theme.global.current.value.dark ? 'myCustomLightTheme' : 'dark',
      toast,
      sm, md, lgAndUp,

      styleFunction() {
        return () => {
          return {
            weight: 10,
            color: "#bebebe",
            opacity: 1,
            fillOpacity: 1
          };
        };
      }
    }

  },
  computed: {
    optionsTest() {
      return {
        pointToLayer: (feature, latlng) => {
          return L.circleMarker(latlng, {
            fillColor: '#000000',
            color: '#31a2f3',
            weight: 10,
            radius: 5
          })
        },
        onEachFeature: (feature, layer) => {
          layer.bindTooltip(
              "<div>LoS:" +
              feature.properties.evi_local +
              "</div><div>nom: " +
              feature.properties.name +
              "</div>",
              {permanent: false, sticky: true}
          );
        }
      }
    },
    optionsTest2() {
      return {
        onEachFeature: (feature, layer) => {
          layer.bindTooltip(
              "<div>route:" +
              feature.properties.highway +
              "</div><div>nom: " +
              feature.properties.name +
              "</div>",
              {permanent: false, sticky: true}
          );
        }
      }
    },
  },


  components: {
    LMap,
    LTileLayer,
    LGeoJson,
    LControlZoom,
  },
  name: 'TestSearch',

  data() {
    return {
      center: [48.8405364, 2.5843466],
      map: null,
      zoom: 14,
      waypoints: [],
      closeleft: false,
      closeright: false,
      simulation: {name: "", description: "", computingScript: "", roads: [], dist: 0, path: [], randomPoints: []},
      id: this.$route.params.id,
      geoStyler: (feature)=>({
        weight: 5,
        color: this.listLegends.map(c => {return {color: c.color,end: c.end}}).sort((a,b) =>{return (a.end - b.end)}).find(c => c.end > feature.properties.evi_local).color ,
        opacity: 0.8,
      }),

      bounds: latLngBounds([
        [45.8405364, 2.5843466],
        [48.8405364, 2.5843466]
      ]),
      maxBounds: latLngBounds([
        [48.8405364, 3.5843466],
        [48.8405364, 4.5843466]
      ]),
      listLayout: [{json: this.geojson, name: "graphe", isShow: true, style: this.styleFunction}, {
        json: this.geojson,
        name: "chemin",
        isShow: true,
        style: this.geoStyler
      }],
    }
  },
  mounted() {
    this.getSimulation()
  },
  methods: {
    uncheckRoadTypes() {
      this.selectedRoadTypes = [];
    },

    async getSimulation() {
      await fetch(`/api/simulation/${this.id}/map`, {
        method: "GET",
        headers: authHeader(),
      })
          .then(response => response.json())
          .then(data => {
            this.simulation.name = data['title']
            this.simulation.description = data['description']
            this.simulation.computingScript = data['script']
            this.simulation.roads = data['roads']
            this.simulation.path = JSON.parse(data['path'])
            this.simulation.randomPoints = JSON.parse(data["randomPoints"])
            console.log(this.simulation.path)
            console.log(this.geojson4)
          });
    },
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
.panel-map-right{
  right: 0;
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
