import {createRouter, createWebHistory} from 'vue-router'
import SeatsView from '../views/SeatsView.vue';
import StartView from "../views/StartView.vue";
import FlightsView from "../views/FlightsView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/seats',
            name: 'seats',
            component: SeatsView,
        },
        {
            path: '/flights',
            name: 'flights',
            component: FlightsView,
        },
        {
            path: '/',
            name: 'start',
            component: StartView,
        },
    ],
})

export default router
