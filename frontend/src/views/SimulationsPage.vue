<template>
    <v-container class="bg-blue-grey-lighten-5" >
        <h2 align-center>En cours</h2>
        <div v-if="simulationsInLoad.length == 0">
            <v-alert type="info" title="Info"
                text="Pas de simulation en cours"></v-alert>
        </div>
        <v-row no-gutters>
            <v-col v-for="simulation in simulationsInLoad" :key=simulation.id cols="12" sm="3" lg="2">
                <v-card class="ma-2">
                    <v-img class="align-end text-white" height="200" :src=imageTest cover>
                    </v-img>
                    <v-card-title class="pt-4">
                        {{ simulation.title }}
                    </v-card-title>
                    <v-card-subtitle> {{ simulation.date }}</v-card-subtitle>
                    <v-card-actions class="justify-space-between">
                        <v-btn color="orange" @click="goSimulation(simulation.id)">
                            Détails
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
    <v-container class="bg-light-green-accent-1">
        <h2>Terminé</h2>
        <div v-if="simulationsInLoad.length == 0">
            <v-alert type="info" title="Info"
                text="Aucune simulation"></v-alert>
        </div>
        <v-row no-gutters>
            <v-col v-for="simulation in simulations" :key=simulation.id cols="12" sm="3" lg="2">
                <v-card class="ma-2">
                    <v-img class="align-end text-white" height="200" :src=imageTest cover></v-img>
                    <v-card-title class="pt-4">
                        {{ simulation.title }}
                    </v-card-title>
                    <v-card-subtitle> {{ simulation.date }}</v-card-subtitle>
                    <v-card-actions class="justify-space-between">
                        <v-btn color="orange" @click="goSimulation(simulation.id)">
                            Détails
                        </v-btn>
                        <v-btn icon="mdi-trash-can-outline" @click="deleteSimulationDialog(simulation)"></v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>
    </v-container>

    <v-dialog v-model="deleteDialog" max-width="600">
        <v-card>
            <v-toolbar color="primary">
                <v-toolbar-title>Suppression de la simulation <b>{{ simulationTitle }}</b> </v-toolbar-title>
            </v-toolbar>

            <v-card-text>
                Attention ! Vous êtes sur le point de supprimer définitivement la simulation <b>{{
                    simulationTitle
                }}</b>.
                Cette action est irréversible et entraînera la suppression de toutes les données de
                cette simulation, y compris tous les résultats et les analyses associées.
            </v-card-text>

            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="grey" @click="deleteDialog = false">Annuler</v-btn>
                <v-btn color="danger" variant="text" @click="deleteSimulation(simulationId)">
                    Supprimer
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import authHeader from "@/services/auth-header";
import { useToast } from "vue-toastification";

export default {
    setup() {
        const toast = useToast();

        return { toast }
    },
    data() {
        return {
            simulationsInLoad: [],
            simulations: [],
            imageTest: require('@/assets/test.png'),
            deleteDialog: false,
            simulationId: -1,
            simulationTitle: "",

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
                        var elt = { id: element['id'], title: element['title'], date: this.getFormatDate(element['beginDate']) };
                        if (element['endDate'] === null)
                            this.simulationsInLoad.push(elt)
                        else
                            this.simulations.push(elt)
                    });
                });

        },
        goSimulation(id) {
            this.$router.push({ name: 'simulation', params: { id: id } });
        },
        getFormatDate(date) {
            var d = new Date(date);
            const year = d.getFullYear();
            const month = d.getMonth() + 1;
            const day = d.getDate();
            const formattedDate = `${day.toString().padStart(2, '0')}/${month.toString().padStart(2, '0')}/${year}`;
            return formattedDate
        },
        deleteSimulationDialog(simulation) {
            this.simulationId = simulation.id
            this.simulationTitle = simulation.title
            this.deleteDialog = true
        },
        deleteSimulation(id) {
            this.deleteDialog = false
            fetch("/api/simulation/" + id, {
                method: "DELETE",
                headers: authHeader(),
            }).then(response => response.json())
                .then(data => {
                    if (data === true) {
                        this.toast.success(`Suppression de la simulation réussi`)
                        this.getSimuations()
                    } else {
                        this.toast.error(`Suppression de la simulation pas réussi`)
                    }
                });
        }
    },
    mounted() {
        this.getSimuations()
    }
}
</script>
