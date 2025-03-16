import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import VueAxios from 'vue-axios'
import axios from "axios"

// ✅ Impordi Vuetify ja vajalikud komponendid
import {createVuetify} from 'vuetify'
import 'vuetify/styles'
import * as components from 'vuetify/components' // 🔹 Lisa kõik komponendid
import * as directives from 'vuetify/directives' // 🔹 Lisa direktiivid
import {aliases, mdi} from 'vuetify/iconsets/mdi' // 🔹 Lisa ikoonid

const vuetify = createVuetify({
    components,
    directives,
    icons: {
        defaultSet: 'mdi',
        aliases,
        sets: {mdi},
    },
})

const app = createApp(App)

app.provide('axios', axios)
app.use(vuetify)
app.use(router)
app.use(VueAxios, axios)

app.mount('#app')
