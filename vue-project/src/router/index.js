import { createRouter, createWebHistory } from 'vue-router'
import VoteForm from '@/pages/feed/VoteForm.vue'
import JobView from '@/pages/site/JobView.vue'
import JobSiteList from '@/pages/site/JobSiteList.vue'
import Todo from '@/pages/todo/Todo.vue'
import NewTodo from '@/pages/todo/TodoAdd.vue'
import FeedUpdate from '@/pages/feed/FeedUpdate.vue'
import QuestionBank from '@/pages/certificate/QuestionBank.vue'
import PracticeQuestion from '@/pages/certificate/PracticeQuestion.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import calendar from '@/pages/calendar/CalendarPage.vue'
import voteDetail  from "@/pages/feed/VoteDetail.vue";
import OAuthGoogleLogin from '@/pages/auth/Google.vue'
import OAuthKakaoLogin from '@/pages/auth/Kakao.vue'
import OAuthNaverLogin from '@/pages/auth/Naver.vue'

const routes = [
    {
        path: '/',
        component: DefaultLayout,
        children: [
            { path: '', component: () => import('@/pages/feed/FeedList.vue') },
            { path: 'search/view/feed/list/job', name: 'job-view', component: JobView },
            { path: 'site', name: 'job-sites', component: JobSiteList },
            { path: 'todo', name: 'todo', component: Todo },
            { path: 'todo/new', name: 'NewTodo', component: NewTodo },
            { path: 'calendar', name: 'calendar', component: calendar },
            {
                path: '/notice/detail/:id',
                name: 'NoticeDetail',
                component: () => import('@/pages/feed/NoticeDetail.vue')
            },
            {
                path: '/post/update',
                name: 'PostUpdate',
                component: FeedUpdate,
            },
            { path: '/notice', component: () => import('@/pages/feed/PostFeed.vue') },
            { path: '/mypage', component: () => import('@/pages/feed/MyPage.vue') },
            { path: 'certificate/data', component: () => import('@/pages/certificate/CertificateData.vue') },
            { path: 'certificate/detail', component: () => import('@/pages/certificate/CertificateDetail.vue') },
            { path: 'certificate/calendar', component: () => import('@/pages/certificate/CertificateCalendar.vue') },
            { path: 'certificate/list', component: () => import('@/pages/certificate/CertificateSearch.vue') },
            { path: 'interview/govinterview', component: () => import('@/pages/interview/GovInterviewPanel.vue') },
            { path: '/interview/priinterview', component: () => import('@/pages/interview/PriInterviewPanel.vue') },
            { path: 'search/view/question', name: 'QuestionBank', component: QuestionBank },
            { path: 'search/view/practical/question', name: 'PracticeQuestion', component: PracticeQuestion },
            { path: '/search/view/og/feed/id', meta: { ssrOnly: true },
    }
    ]
    },


    { path: '/login', name:`Login`, component: () => import('@/pages/auth/Login.vue'), meta: { hideLayout: true } },
    { path: '/signup', component: () => import('@/pages/auth/SignUp.vue'), meta: { hideLayout: true } },
    { path: '/user/profile/:username', name: 'user-profile', component: () => import('@/pages/feed/user/SomeoneProfile.vue')},
    { path: '/search/view/feed/update', name: 'feed-update', component: FeedUpdate },
    { path: '/search/view/vote/detail', name: 'vote-detail', component: voteDetail, meta: { hideLayout: true } },
    { path: '/search/view/feed/Form', name: 'feed-form', component: () => import('@/pages/feed/FeedForm.vue'), meta: { hideLayout: true } },
    { path: '/post/:id', name: 'PostDetail', component: () => import('@/pages/feed/FeedDetail.vue'), meta: { hideLayout: true } },
    { path: '/search/view/feed/vote', name: 'vote-form', component: VoteForm },
    { path: '/search/view/content', name: 'search-result', component: () => import('@/pages/SearchResult.vue') },
    { path: '/search/view/feed/list/page', component: () => import('@/pages/feed/MyPage.vue') },
    { path:'/notifications', name:'Notifications', component:() => import('@/components/NotificationsPage.vue') },
    {path: '/google/callback', name: 'GoogleOAuth', component: OAuthGoogleLogin},
    {path: '/kakao/callback', name: 'KakaoOAuth', component: OAuthKakaoLogin},
    {path: '/naver/callback', name: 'NaverOAuth', component: OAuthNaverLogin}
]
export default createRouter({
    components: {
        DefaultLayout
    },
    history: createWebHistory(),
    routes
})
