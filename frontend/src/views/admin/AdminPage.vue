<template>
    <v-container></v-container>
    <v-container class="bg-surface-variant">
        <div v-for="user in users" :key="user.id">
            <div class="d-flex  justify-space-between ">
            <h2>{{  user.mail }}</h2>
            <h2>{{  user.name }}</h2>
            <h2>{{  user.role }}</h2>
            <v-btn color="red" prepend-icon="mdi-trash-can-outline">Supprimer</v-btn>
            </div>
        </div>
  </v-container>
</template>

<script>
import { useToast } from "vue-toastification";

export default {
    setup() {
        const toast = useToast();
        return { toast }
    },
    data: () => ({
        users: [],
    }),
    computed: {

    },
    methods: {
        getUsers() {
            fetch("/api/administration/users/", {
                method: "GET",
                headers: { "Content-Type": "application/json" },
            })
                .then(response => response.json()).then(data => {
                    this.users = data
                })
        },
    },
    mounted() {
        this.getUsers()
    }
}
</script>