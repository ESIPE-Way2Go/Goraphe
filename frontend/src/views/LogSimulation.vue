<template>
  <h1>Logs simulation {{  id }}</h1>
  <div v-html="log"></div>
</template>


<script>


export default {
  name: 'logsSimulation',
  
  
  data() {
      return {
        log: "",
        id: this.$route.params.id
      }
  },

  mounted() { 
    setInterval(this.getLogs(), 1000)
       
    
  },
  methods: {
    getLogs() {
      fetch("/api/simulation/" + this.id)
      .then(response => response.json())
      .then(data => {
          var tab = data['content'];
          this.log ="";
          tab.forEach(element => {
            this.log += "<span>" + element + "</span> <br />"
          });
          
          
      })      
    }
  }
}
</script>
