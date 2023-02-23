<template>
    <div class="d-flex align-center fill-height">
        <v-card width="30%" class="mx-auto pa-6 bg-white" style="border-color: blue" variant="outlined" rounded-lg>
            <v-img class="align-end text-white" :src=imageGoraphe cover></v-img>
            <v-card-title class="text-center" style="color: blue">
                Changement de mot de passe
            </v-card-title>
            <v-card-text>
                <v-form fast-fail @submit.prevent="updateAccount" v-model="isFormValid">
                    <v-text-field variant="outlined" class="mb-2" v-model="form.password" :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
                        :rules="passwordRules" :type="show1 ? 'text' : 'password'" label="Mot de passe "
                        hint="Au moins 8 caractères avec au moins une majuscule, une minuscule et un chiffre"
                        @click:append="show1 = !show1"></v-text-field>

                    <v-text-field variant="outlined" v-model="confirmPassword" :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
                        :rules="confirmPasswordRules" :type="show2 ? 'text' : 'password'"
                        label="Confirmation de mot de passe" @click:append="show2 = !show2"></v-text-field>

                    <v-btn type="submit" color=primary block class="mt-2" :disabled="!isFormValid" rounded>Valider</v-btn>
                </v-form>
            </v-card-text>
        </v-card>       
    </div>
</template>

<script>
import { useToast } from "vue-toastification";

export default {
    setup() {
        const toast = useToast();
        return { toast }
    },
    data() {
        return {
            show1: false,
            show2: false,
            passwordRules: [
                value => {
                    if ((/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d).{8,}$/).test(value)) return true
                    return 'Le mot de passe doit avoir au moins huits caractères, une majuscule, une minuscule et un chiffre'
                },
            ],
            confirmPassword: '',
            confirmPasswordRules: [
                value => {
                    if (value === this.form.password) return true
                    return "La confirmation du mot de passe n'est pas identique au mot de passe"
                },
            ],

            form: { token: this.$route.params.token, password: "" },
            isFormValid: false,
            imageGoraphe: require('@/assets/Goraphe_small.png'),

        }
    },
    computed: {
        formIsValid() {
            return this.$refs.form.validate()
        },
    },
    methods: {
        checkAutorization() {
            var token = this.$route.params.token
            fetch("/api/administration/checkModifyPassword/" + token, {
                method: "GET",
            }).then(response => {
                if (!response.ok)
                    //  this.$router.push({ name: 'login' })
                    console.log("hello")
            });
        },
        updateAccount() {
            fetch("/api/administration/updatePassword/", {
                method: 'PUT',
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(this.form)
            })
                .then(response => {
                    if (response.ok) {
                        this.toast.success("Mot de passe a été modifié")
                        this.$router.push({ name: 'login' })
                    }
                    response.json().then(data => {
                        this.toast.error(data['message'])
                    })
                })
        },
    },
    mounted() {
        this.checkAutorization()
    }
}
</script>
