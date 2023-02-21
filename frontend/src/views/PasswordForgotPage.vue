<template>
    <v-form fast-fail @submit.prevent="sendForgotPassword" v-model="isFormValid">
        <v-text-field v-model="email" :rules="emailRules" label="Email" required></v-text-field>
        <v-btn type="submit" block class="mt-2" :disabled="!isFormValid">Récupérer le mot de passe</v-btn>
    </v-form>
</template>

<script>
import { useToast } from "vue-toastification";

export default {
    setup() {
        const toast = useToast();
        return { toast }
    },
    data: () => ({
        valid: false,
        email: '',
        emailRules: [
            value => {
                if (value) return true
                return 'mail est requis'
            },
            value => {
                if (/.+@.+\..+/.test(value)) return true
                return 'Mail doit être valide'
            },
        ],
        isFormValid: false
    }),
    computed: {
        formIsValid() {
            return this.$refs.form.validate()
        },
    },
    methods: {
        sendForgotPassword() {
            fetch("/api/administration/forgetPassword/" + this.email, {
                method: "GET",
                headers: { "Content-Type": "application/json" },
            })
                .then(response => {
                    if (response.ok) {
                        this.toast.success("Mail envoyé")
                        this.$router.push({ name: 'login' })
                    }
                    response.json().then(data => {
                        this.toast.error(data['message'])
                    })
                })
        },
    }
}
</script>