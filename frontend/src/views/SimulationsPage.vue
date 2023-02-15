<template>
    <v-container class="bg-surface-variant">
        <v-row no-gutters>
            <v-col v-for="simulation in simulations" :key=simulation.id cols="12" sm="4">
                <v-card class="mr-4 mb-4">
                    <v-img class="align-end text-white" height="200" :src=imageTest cover></v-img>
                    <v-card-title class="pt-4">
                        {{ simulation.title }}
                    </v-card-title>
                    <v-card-actions class="justify-space-between">
                        <v-btn color="orange" @click="goSimulation(simulation.id)">
                            Détails
                        </v-btn>
                        <v-btn icon="mdi-trash-can-outline"></v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>       
    </v-container>


    <div class="mx-auto w-75">
        <!-- <v-sheet :class="model" class="mx-auto mt-8" elevation="12" height="256" width="100%">
            <div class="d-flex">
                <v-chip class="ma-2 align-center" color="green" text-color="white">
                    En cours
                </v-chip>
                <div v-for="simulation in simulationsInLoad" :key="simulation.title">
                    <v-card class="mx-auto" max-width="400">
                        <v-img class="align-end text-white" height="200" src="../assets/test.png" cover>
                            <v-card-title>{{ simulation.title }}</v-card-title>
                        </v-img>

                        <v-card-subtitle class="pt-4">
                            Number 10
                        </v-card-subtitle>

                        <v-card-text>
                            <div>Whitehaven Beach</div>

                            <div>Whitsunday Island, Whitsunday Islands</div>
                        </v-card-text>

                        <v-card-actions>
                            <v-btn color="orange">
                                Share
                            </v-btn>

                            <v-btn color="orange">
                                Explore
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </div>
            </div>
        </v-sheet>-->
        <!--<v-sheet :class="model" class="mx-auto mt-8" elevation="12" height="256" width="100%">   </v-sheet> 
         <v-chip class="ma-2 align-center" color="green" text-color="white">
                Terminé
            </v-chip>
        
        

        <v-sheet class="d-flex justify-center flex-wrap bg-surface-variant" width="100%">
            <div v-for="simulation in simulations" :key="simulation.title" width="100%">
                <v-card class="mr-4 mb-4" width="20%">
                    <v-img class="align-end text-white" height="200" :src=imageTest cover></v-img>
                    <v-card-title class="pt-4">
                        {{ simulation.title }}
                    </v-card-title>
                    <v-card-actions class="justify-space-between">
                        <v-btn color="orange" @click="goSimulation(simulation.id)">
                            Détails
                        </v-btn>
                        <v-btn icon="mdi-trash-can-outline"></v-btn>
                    </v-card-actions>
                </v-card>
            </div>
        </v-sheet>-->

    </div>
</template>

<script>
import authHeader from "@/services/auth-header";

export default {
    data() {
        return {
            simulationsInLoad: [],
            simulations: [],
            imageTest: require('@/assets/test.png')
        }
    },

    methods: {
        getSimuations() {
            fetch("/api/simulation/", {
                method: "GET",
                headers: authHeader(),
            })
                .then(response => response.json())
                .then(data => {
                    data.forEach(element => {
                        var elt = { id: element['id'], title: element['title'], date: element['beginDate'] };
                        if (element['endDate'] === null)
                            this.simulationsInLoad.push(elt)
                        else
                            this.simulations.push(elt)
                    });
                });
        },
        goSimulation(id) {
            this.$router.push({ name: 'simulation', params: { id: id } });
        }
    },
    mounted() {
        this.getSimuations()
    }
}
</script>
