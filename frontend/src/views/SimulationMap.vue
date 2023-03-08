<template>
  <v-container fluid style="padding: 10px">

    <div class="fixed-loader" v-if="!legendsReady">
      <v-progress-circular
          :size="50"
          color="primary"
          indeterminate
      ></v-progress-circular>
    </div>

    <v-row class="d-flex">
      <div class="vh-100 w-100" >
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
          <l-geo-json   :geojson="simulation.graph" :options-style="styleGraph" ></l-geo-json>
            <l-geo-json ref="graph" :geojson="simulation.path"  :options-style="geoStyler" :visible=listLayout[0].isShow :options="optionsPath"  v-if="legendsReady"></l-geo-json>
            <l-geo-json :geojson="simulation.randomPoints" :options-style="styleMarker" :visible=listLayout[1].isShow :options="optionsMarker"  ></l-geo-json>
        </l-map>
      </div>

      <v-btn icon="mdi-chevron-right" class="position-fixed mt-15 panel-burger ma-5" @click.stop="closeleft= !closeleft"
             v-if="closeleft"></v-btn>
      <v-slide-y-transition v-if="legendsReady">
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
                variant="text"
                rounded
                :subtitle="simulation.description"
                class="ma-1"
            >
            </v-list-item>

            <v-list-item
                title="Module"
                variant="text"
                rounded
                :subtitle="simulation.computingScript"
                class="ma-1"
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
            <v-list-item >
            <v-btn color="primary" prepend-icon="mdi-download" class="ma-1 align-self-center" @click="downloadFile" variant="outlined">
              Excel
            </v-btn>

            <v-btn color="primary" class="ma-1 align-self-center" @click="gotoSimulation" variant="outlined">
              Logs
            </v-btn>
              </v-list-item>
          </v-list>
        </v-card>
      </v-slide-y-transition>


      <v-btn icon="mdi-chevron-right" class="position-fixed mt-15 panel-burger ma-5 panel-map-right" @click.stop="closeright= !closeright"
             v-if="closeright"></v-btn>
      <v-slide-y-transition v-if="legendsReady">
        <v-card class="position-fixed pa-5 mt-15 panel-map ma-5 panel-map-right" :class="{'panel-map-lg' : lgAndUp,'panel-map-md': md}"
                v-if="!closeright">
          <v-row align="start">
            <v-col cols="10">

            </v-col>
            <v-col cols="2">
              <v-btn prepend-icon="mdi-chevron-left" @click.stop="closeright= !closeright" flat size="x-large"></v-btn>
            </v-col>
          </v-row>

          <v-select
              v-model="selectedSimulation"
              label="Simulation"
              :items="listsOfJson.map(json => json.type + json.number).filter((element, index,self) => {
              return self.indexOf(element) === index;})"
              variant="underlined"
              :update:modelValue="changeSimulationJson(selectedSimulation)"
          ></v-select>

          <v-list lines="two">
            <v-list-item
                v-for="layout in listLayout" :key="layout"
                :title="layout.name"

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


import { latLngBounds } from "leaflet/dist/leaflet-src.esm";

export default {
  setup() {

    const theme = useTheme();
    const {sm, md, lgAndUp} = useDisplay();

    return {
      step:100,
      listLegends: [
        {color:"#FFFFB2",start:0,end:100,titre:"LOS"},
        {color:"#FED976",start:100,end:100*2,titre:"LOS"},
        {color:"#FEB24C",start:100*2,end:100*3,titre:"LOS"},
        {color:"#F03B20",start:100*3,end:100*4,titre:"LOS"},
        {color:"#BD0026",start:100*4,end:100*5,titre:"LOS"},
        {color:"#BE1D9B",start:100*5,end:100*6,titre:"LOS"}],

      theme,
      toggleTheme: () => theme.global.name.value = theme.global.current.value.dark ? 'myCustomLightTheme' : 'dark',
      sm, md, lgAndUp,

      styleMarker() {
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
    optionsMarker() {
      return {
        pointToLayer: (feature, latlng) => {
          return L.circleMarker(latlng, {
            fillColor: '#000000',
            color: '#31a2f3',
            weight: 10,
            radius: 5
          })
        },
      }
    },
    optionsPath() {
      return {
        onEachFeature: (feature, layer) => {
          layer.bindTooltip(
              "<div>LoS:" +
              feature.properties.evi_average_nip +
              "<div>NIP:" +
              feature.properties.impacted_paths +
              "</div><div>NBP: " +
              feature.properties.broken_paths +
              "<div>TTR:" +
              feature.properties.timetravel_ratio +
              "<div>Beta:" +
              feature.properties.beta_traveltimes +
              "</div><div>Nom: " +
              feature.properties.name +
              "</div><div>Osmid:" +
          feature.properties.osmid +

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
      //center of the maps at the start
      center: [48.205364, 2.5843466],
      zoom: 14,
      closeleft: false,
      closeright: false,
      //simulation selected that is show on the map
      simulation: {name: "", description: "", computingScript: "", roads: [], dist: 0, path: [], randomPoints: [],graph: []},
      //id of the simulation
      id: this.$route.params.id,

      //style for the Path
      geoStyler: (feature)=>({
        weight: 5,
        color: this.colorFeat(feature),
        opacity: 0.8,
      }),
      //style for the Graph
      styleGraph: ()=>({
        weight: 8,
        color: '#000000',
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
      listLayout: [
          { name: "Chemin", isShow: true, },
        {name: "Points aléatoires", isShow: true}],
      listsOfJson: [],
      selectedSimulation : "FINAL",
      legendsReady: false,
    }
  },
  mounted() {
    this.getSimulation()
  },
  methods: {
    gotoSimulation(){
      this.$router.push({name: 'simulation', params: {id: this.id}});
    },

    colorFeat(feature){
      const val = this.listLegends.map(c => {return {color: c.color,end: c.end}}).sort((a,b) =>{return (a.end - b.end)}).find(c => c.end > feature.properties.evi_average_nip);
      const max = this.listLegends.reduce((prev, current) => (prev.end > current.end) ? prev : current)

      if(val===undefined){
        if(feature.properties.evi_average_nip>max.end){
          return max.color;
        }
        return this.listLegends[0].color;
      }
      return val.color;
    },

    uncheckRoadTypes() {
      this.selectedRoadTypes = [];
    },

    multipleFloat(value,multiple){
      const result = value*multiple;
      return Math.round(result* 100) /100
    },

    changeSimulationJson(v){
      //console.log(v)
      let tempo = this.listsOfJson.filter(json => json.find===v);
      tempo.forEach(json => {
        if(json.isPath) {
          this.simulation.path = json.json
        }else{
          this.simulation.randomPoints = json.json
        }
      })
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
            //get all the geojson data on a list
            let map = Object.entries(data['results']).sort( (r1,r2) => r1.key <r2.key);

            let results = [];

            map.forEach( entry => {
              const [key, val] = entry;
              let tempo = key.split('_');
              //if geojson of the graph
              if(tempo.length<2 && tempo[0]==='GRAPHE'){
                this.simulation.graph = JSON.parse(val);
              }
              //if geojson is an iteration
              if(tempo[0].includes("ITERATION")){
                let number = tempo[0].substring("ITERATION".length)
                results.push({type:'ITERATION',number:number,isPath:tempo[1].includes('road'),json:JSON.parse(val),find:tempo[0]})
              }
              //if geojson is the final result
              if(tempo[0].includes("FINAL")){
                results.push({type:tempo[0],number:'',isPath:tempo[1].includes('road'),json:JSON.parse(val),find:tempo[0]})
                if(tempo[1].includes('road')){
                  this.simulation.path= JSON.parse(val);
                  //this.simulation.path.map(data)
                  console.log(Math.max(...JSON.parse(val).features.map(f => f.properties.evi_average_nip)))
                  let max = Math.max(...JSON.parse(val).features.map(f => f.properties.evi_average_nip));
                  //max = Math.round(max)
                  this.step = max/5;
                  this.step = Math.round(this.step * 100) / 100
                  console.log(this.step)
                  this.listLegends=[
                    {color:"#FFFFB2",start:0,end:this.step,titre:"LOS"},
                    {color:"#FED976",start:this.step,end: this.multipleFloat(this.step,2),titre:"LOS"},
                    {color:"#FEB24C",start: this.multipleFloat(this.step,2),end:this.multipleFloat(this.step,3),titre:"LOS"},
                    {color:"#F03B20",start:this.multipleFloat(this.step,3),end:this.multipleFloat(this.step,4),titre:"LOS"},
                    {color:"#BD0026",start:this.multipleFloat(this.step,4),end:this.multipleFloat(this.step,5),titre:"LOS"},
                    {color:"#BE1D9B",start:this.multipleFloat(this.step,5),end:this.multipleFloat(this.step,6)  ,titre:"LOS"}]
                  console.log(this.listLegends)
                  this.legendsReady = true;
                }else{
                  this.simulation.randomPoints= JSON.parse(val);
                }
              }
            })
            let geojsongroup = L.geoJSON(this.simulation.graph);
            this.listsOfJson = results;
            this.$refs.map.leafletObject.fitBounds(geojsongroup.getBounds());
            this.$refs.map.leafletObject.setMaxBounds(geojsongroup.getBounds());
            this.$refs.map.leafletObject.setMinZoom(6);
          });
    },

    downloadFile() {
      const url = '/api/simulation/' + this.id + '/download';
      fetch(url, {method: 'GET', headers: authHeader()})
          .then(response => {
            // Check that the response status is in the 200-299 range,
            // which indicates a successful response.
            if (response.ok) {
              // Get the filename from the Content-Disposition header
              const contentDisposition = response.headers.get('Content-Disposition');
              const filenameMatch = contentDisposition.match(/filename="(.+)"/);
              const filename = filenameMatch ? filenameMatch[1] : 'unknown';
              // Create a new Blob object from the response body
              return response.blob().then(blob => ({blob, filename}));
            } else {
              throw new Error(`Request failed with status ${response.status}`);
            }
          })
          .then(({blob, filename}) => {
            // Create a new URL object for the blob
            const url = URL.createObjectURL(blob);
            // Create a new anchor element to trigger the download
            const link = document.createElement('a');
            link.href = url;
            link.download = filename;
            // Trigger the download by clicking the anchor element
            link.click();
            // Clean up the URL object and anchor element
            URL.revokeObjectURL(url);
            link.remove();
          })
          .catch(error => {
            console.error(error);
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

.fixed-loader {
  position: fixed;
  width: 50px;
  height: 50px;
  padding: 10px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1000;
  margin-top: -25px;/* Negative half of height. */
  margin-left: -25px;/* Negative half of width. */
}
</style>
