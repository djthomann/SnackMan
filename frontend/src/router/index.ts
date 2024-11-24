import { createWebHistory, createRouter } from 'vue-router';

import HomeView from '@/components/HomeView.vue';
import TitleView from '@/components/TitleView.vue';

const routes = [
  { path: '/', component: TitleView },
  { path: '/home', component: HomeView },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
