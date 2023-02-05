import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Toast from "vue-toastification";

import "bootstrap/dist/css/bootstrap.min.css";
import 'vue3-easy-data-table/dist/style.css';
import "vue-toastification/dist/index.css";
// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

import { aliases, mdi } from 'vuetify/iconsets/mdi'



const myCustomLightTheme = {
    dark: false,
    colors: {
        background: '#FFFFFF',
        surface: '#FFFFFF',
        primary: '#3f51b5',
        secondary: '#607d8b',
        accent: '#4caf50',
        error: '#f44336',
        warning: '#ff9800',
        info: '#00bcd4',
        success: '#8bc34a'
    }
}

//for toaster
const options = {
    // You can set your default options here
    position: "top-center",
    timeout: 4660,
    closeOnClick: true,
    pauseOnFocusLoss: false,
    pauseOnHover: true,
    draggable: true,
    draggablePercent: 0.6,
    showCloseButtonOnHover: true,
    hideProgressBar: false,
    closeButton: "button",
    icon: true,
    rtl: false
};

const vuetify = createVuetify({
    icons: {
        defaultSet: 'mdi',
        aliases,
        sets: {
            mdi,
        }
    },
    components,
    directives,
    theme: {
        defaultTheme: 'myCustomLightTheme',
        themes: {
            myCustomLightTheme,
        }
    },
})

createApp(App).use(store).use(router).use(vuetify).use(Toast,options).mount('#app');

