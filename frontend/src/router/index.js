import { createRouter, createWebHistory } from 'vue-router'
import SeatsView from '../views/SeatsView.vue';
import SearchView from "../views/SearchView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/seat',
            name: 'seats',
            component: SeatsView,
        },
        {
            path: '/',
            name: 'search',
            component: SearchView,
        },
    ],
})

export default router
