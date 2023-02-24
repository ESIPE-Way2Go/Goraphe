<template>
    <v-card>
        <div class="d-flex justify-space-between">
            <v-tabs v-model="tab">
                <v-tab value="users">Utilisateurs</v-tab>
                <v-tab value="invitation">Invitation</v-tab>
            </v-tabs>
            <v-btn color="blue" class="ma-2" positon="right" prepend-icon="mdi-plus" @click="inviteDialog = true">Inviter un
                utilisateur</v-btn>
        </div>
        <v-card-text>
            <v-window v-model="tab">
                <v-window-item value="users" transition="true">
                    <v-table>
                        <thead>
                            <tr>
                                <th scope="col" class="text-left">
                                    Mail
                                </th>
                                <th scope="col" class="text-left">
                                    Username
                                </th>
                                <th scope="col" class="text-left">
                                    Role
                                </th>
                                <th scope="col" class="text-center">
                                    Supprimer
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="user in users" :key="user.id">
                                <td>{{ user.mail }}</td>
                                <td>{{ user.name }}</td>
                                <td>{{ user.role }}</td>
                                <td class="text-center pa-1"> <v-btn v-if="user.role !== 'ROLE_ADMIN'" color="red"
                                        icon="mdi-trash-can-outline" @click="deleteSelectUser(user)"></v-btn></td>
                            </tr>
                        </tbody>
                    </v-table>
                </v-window-item>
                <v-window-item value="invitation" transition="true">
                    <v-table>
                        <thead>
                            <tr>
                                <th scope="col" class="text-left">
                                    Mail
                                </th>
                                <th scope="col" class="text-left">
                                    Date
                                </th>
                                <th scope="col" class="text-center">
                                    Options
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="invitation in invitations" :key="invitation.id">
                                <td>{{ invitation.email }}</td>
                                <td>{{ format(new Date(invitation.sendDate)) }}</td>
                                <td class="text-center d-flex  justify-center pa-1">
                                    <v-btn color="grey" prepend-icon="mdi-redo" class="mr-6"
                                        @click="reSendInvitation(invitation)">Relancer une invitation</v-btn>
                                    <v-btn color="grey" prepend-icon="mdi-trash-can-outline"
                                        @click="deleteSelectInvitation(invitation)">Supprimer</v-btn>
                                </td>
                            </tr>
                        </tbody>
                    </v-table>
                </v-window-item>
            </v-window>
        </v-card-text>
    </v-card>


    <v-dialog v-model="deleteDialog" max-width="600">
        <v-card>
            <v-toolbar color="primary">
                <v-toolbar-title>Suppression de l'utilisateur <b>{{ user.name }}</b> </v-toolbar-title>
            </v-toolbar>

            <v-card-text>
                Attention ! Vous êtes sur le point de supprimer définitivement un utilisateur de notre application. Cette
                action est irréversible et entraînera la suppression de toutes les données de cet utilisateur.
            </v-card-text>

            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="grey" @click="deleteDialog = false">Annuler</v-btn>
                <v-btn color="danger" variant="text" @click="deleteUser(user.id)">
                    Supprimer
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>

    <v-dialog v-model="inviteDialog" max-width="600">
        <v-card>
            <v-toolbar color="primary">
                <v-toolbar-title>Inviter un utilisateur</v-toolbar-title>
            </v-toolbar>
            <v-form fast-fail @submit.prevent="sendInvitation" v-model="isFormValid">

                <v-card-text>
                    <v-text-field v-model="form.email" :rules="emailRules" label="Email" required></v-text-field>
                </v-card-text>

                <v-card-actions >
                    <v-spacer></v-spacer>
                    <v-btn color="grey" @click="inviteDialog = false">Annuler</v-btn>
                    <v-btn type="submit" color="green" :disabled="!isFormValid">Inviter</v-btn>
                </v-card-actions>
            </v-form>
        </v-card>
    </v-dialog>

    <v-dialog v-model="deleteInviteDialog" max-width="600">
        <v-card>
            <v-toolbar color="primary">
                <v-toolbar-title>Suppression l'invitation pour <b>{{ invitation.email }}</b> </v-toolbar-title>
            </v-toolbar>

            <v-card-text>
                Attention ! Vous êtes sur le point de supprimer définitivement un utilisateur de notre application. Cette
                action est irréversible et entraînera la suppression de toutes les données de cet utilisateur.
            </v-card-text>

            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="grey" @click="deleteInviteDialog = false">Annuler</v-btn>
                <v-btn color="danger" variant="text" @click="deleteInvitation(invitation.id)">
                    Supprimer
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>



<script>
import { can } from '@/utils'
import authHeader from "@/services/auth-header";
import { useToast } from "vue-toastification";

export default {
    setup() {
        const toast = useToast();
        return { toast }
    },
    data: () => ({
        tab: [],
        users: [],
        user: '',
        invitations: [],
        invitation: '',
        deleteDialog: false,
        inviteDialog: false,
        deleteInviteDialog: false,
        emailRules: [
            value => {
                if (value) return true
                return 'mail est requis'
            },
            value => {
                if (/^\w+([\\.-]?\w+)*@\w+([\\.-]?\w+)*(\.\w{2,})+$/.test(value)) return true
                return 'Mail doit être valide'
            },
        ],
        form: { email: '' },
        isFormValid: false,
    }),
    computed: {
        formIsValid() {
            return this.$refs.form.validate()
        },
    },
    methods: {
        getUsers() {
            fetch("/api/administration/users/", {
                method: "GET",
                headers: authHeader(),
            })
                .then(response => response.json()).then(data => {
                    this.users = data
                })
        },
        deleteSelectUser(user) {
            this.deleteDialog = true
            this.user = user
        },
        deleteUser(id) {
            fetch("/api/administration/user/" + id, {
                method: "DELETE",
                headers: authHeader(),
            })
                .then(response => {
                    if (response.ok) {
                        this.deleteDialog = false
                        this.getUsers()
                        this.toast.success("Utilisateur supprimé")
                    }
                    response.json().then(data => {
                        this.toast.error(data['message'])
                    })
                })
        },
        sendInvitation() {
            fetch("/api/administration/invitation", {
                method: "POST",
                headers: authHeader(),
                body: JSON.stringify(this.form)
            })
                .then(response => {
                    if (response.ok) {
                        this.toast.success("Invitation envoyé")
                        this.inviteDialog = false
                    }
                    response.json().then(data => {
                        this.toast.error(data['message'])
                    })
                })
        },
        getInvitations() {
            fetch("/api/administration/invitations/", {
                method: "GET",
                headers: authHeader(),
            })
                .then(response => response.json()).then(data => {
                    this.invitations = data
                })
        },
        deleteSelectInvitation(invitation) {
            this.deleteInviteDialog = true
            this.invitation = invitation
        },
        deleteInvitation(id) {
            fetch("/api/administration/invitation/" + id, {
                method: "DELETE",
                headers: authHeader(),
            })
                .then(response => {
                    if (response.ok) {
                        this.deleteInviteDialog = false
                        this.getInvitations()
                        this.toast.success("Invitaion supprimée")
                    }
                    response.json().then(data => {
                        this.toast.error(data['message'])
                    })
                })
        },
        reSendInvitation(invitation) {
            fetch("/api/administration/invitation/" + invitation.id, {
                method: "PUT",
                headers: authHeader(),
            })
                .then(response => {
                    if (response.ok) {
                        this.toast.success("Invitation renvoyé")
                        this.getInvitations()
                    }
                    response.json().then(data => {
                        this.toast.error(data['message'])
                    })
                })
        },
        checkAdmin() {
            let checkAdmin = can("ROLE_ADMIN")
            if (checkAdmin === null || !checkAdmin)
                this.$router.push({ name: 'home' });
        },
        format(inputDate) {
            let date, month, year;
            date = inputDate.getDate();
            month = inputDate.getMonth() + 1;
            year = inputDate.getFullYear();

            date = date
                .toString()
                .padStart(2, '0');

            month = month
                .toString()
                .padStart(2, '0');

            return `${date}/${month}/${year}`;
        }
    },
    mounted() {
        this.checkAdmin()
        this.getUsers()
        this.getInvitations()
    }
}
</script>