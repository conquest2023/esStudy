import { createRouter, createWebHistory } from 'vue-router'
import VoteForm from '@/pages/feed/VoteForm.vue'
const routes = [
    { path: '/',          component: () => import('@/pages/feed/FeedList.vue') },
    { path: '/search/view/feed/id/:id', name: 'feed-detail', component: () => import('@/pages/feed/FeedDetail.vue')},
    { path: '/login',     component: () => import('@/pages/auth/Login.vue')  ,  meta: { hideLayout: true } },
    { path: '/signup',    component: () => import('@/pages/auth/SignUp.vue') ,  meta: { hideLayout: true }  },
    { path: '/search/view/content', name: 'search-result', component: () => import('@/pages/SearchResult.vue')},
    { path: '/search/view/feed/Form', name: 'feed-form', component: () => import('@/pages/feed/FeedForm.vue'),meta: { hideLayout: true } },
    { path: '/search/view/feed/vote', name: 'vote-form', component: VoteForm}]

export default createRouter({
    history: createWebHistory(),
    routes
})
