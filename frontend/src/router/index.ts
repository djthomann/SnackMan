import { createWebHistory, createRouter } from 'vue-router';

import MainView from '@/components/MainView.vue';
import TitleView from '@/components/TitleView.vue';
import HomeView from '@/components/HomeView.vue';
import LobbyView from '@/components/LobbyView.vue';

const routes = [
  { path: '/', redirect: '/title' },
  { path: '/title', component: TitleView },
  { path: '/home', component: HomeView },
  { path: '/lobby/:code', component: LobbyView },
  { path: '/game/:code', component: MainView },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
