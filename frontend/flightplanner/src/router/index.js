import { createRouter, createWebHistory } from 'vue-router'
import SeatsView from '../views/SeatsView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
      {
      path: '/seat',
      name: 'seats',
      component: SeatsView,
    },
  ],
})

export default router
