import { createWebHistory, createRouter } from 'vue-router';

import Main from '@/components/Main.vue';
import TitleView from '@/components/TitleView.vue';
import HomeView from '@/components/HomeView.vue';
import LobbyView from '@/components/LobbyView.vue';
import Username from '@/components/Username.vue';

const routes = [
  { path: '/', redirect: '/title' },
  { path: '/title', component: TitleView },
  { path: '/home', component: HomeView },
  { path: '/lobby/:code', component: LobbyView },
  { path: '/username', component: Username },
  { path: '/game/:code', component: Main },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
