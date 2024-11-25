import { createWebHistory, createRouter } from 'vue-router';

import HomeView from '@/components/HomeView.vue';
import TitleView from '@/components/TitleView.vue';
import LobbyView from '@/components/LobbyView.vue';
import Username from '@/components/Username.vue';
import Scene from '@/components/Scene.vue';

const routes = [
  { path: '/', component: TitleView },
  { path: '/home', component: HomeView },
  { path: '/lobby/:code', component: LobbyView },
  { path: '/username', component: Username },
  { path: '/game/:code', component: Scene },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
