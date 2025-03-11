import { createRouter, createWebHistory } from 'vue-router'
import SeatsView from '../views/SeatsView.vue';
import SearchView from "../views/SearchView.vue";
import CheckoutView from "../views/CheckoutView.vue";

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
            name: 'seat',
            component: SearchView,
        },
        {
            path: '/checkout',
            name: 'Checkout',
            component: CheckoutView
        }
    ],
})

export default router
