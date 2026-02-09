import { createRouter, createWebHistory } from 'vue-router'

const DefaultLayout = () => import('@/layouts/DefaultLayout.vue')

const FeedList = () => import('@/pages/feed/FeedList.vue')
const FeedDetail = () => import('@/pages/feed/FeedDetail.vue')
const PostFeed = () => import('@/pages/feed/PostFeed.vue')
const NoticeDetail = () => import('@/pages/feed/NoticeDetail.vue')
const FeedUpdate = () => import('@/pages/feed/FeedUpdate.vue')
const FeedForm = () => import('@/pages/feed/FeedForm.vue')

// vote / poll
const VoteForm = () => import('@/pages/feed/VoteForm.vue')
const PollDetail = () => import('@/pages/feed/PollDetail.vue')

// site / jobs
const JobView = () => import('@/pages/site/JobView.vue')
const JobSiteList = () => import('@/pages/site/JobSiteList.vue')

const Todo = () => import('@/pages/todo/Todo.vue')
const NewTodo = () => import('@/pages/todo/TodoAdd.vue')

// certificate
const CertificateData = () => import('@/pages/certificate/CertificateData.vue')
const CertificateDetail = () => import('@/pages/certificate/CertificateDetail.vue')
const CertificateCalendar = () => import('@/pages/certificate/CertificateCalendar.vue')
const CertificateSearch = () => import('@/pages/certificate/CertificateSearch.vue')
const QuestionBank = () => import('@/pages/certificate/QuestionBank.vue')
const PracticeQuestion = () => import('@/pages/certificate/PracticeQuestion.vue')

const GovInterviewPanel = () => import('@/pages/interview/GovInterviewPanel.vue')
const PriInterviewPanel = () => import('@/pages/interview/PriInterviewPanel.vue')

const CalendarPage = () => import('@/pages/calendar/CalendarPage.vue')
const Login = () => import('@/pages/auth/Login.vue')
const SignUp = () => import('@/pages/auth/SignUp.vue')
const SomeoneProfile = () => import('@/pages/feed/user/SomeoneProfile.vue')
const SearchResult = () => import('@/pages/SearchResult.vue')
const MyPage = () => import('@/pages/feed/MyPage.vue')
const NotificationsPage = () => import('@/components/NotificationsPage.vue')

const OAuthGoogleLogin = () => import('@/pages/auth/Google.vue')
const OAuthKakaoLogin  = () => import('@/pages/auth/Kakao.vue')
const OAuthNaverLogin  = () => import('@/pages/auth/Naver.vue')

const routes = [
    {
        path: '/',
        component: DefaultLayout,
        children: [
            { path: '', component: FeedList },
            {
                path: '/practice',
                component: () => import('@/pages/english/PracticeHome.vue')
            },
            {
                path: '/practice/rc',
                component: () => import('@/pages/english/PracticeSession.vue')
            },
            {
                path: '/wrong-notes',
                component: () => import('@/pages/english/WrongNoteDashBoard.vue')
            },
            {
                path: "/practice/vocab",
                component: () => import("@/pages/english/PracticeVocabSession.vue"),
            },
            // {
            //     path: '/wrong-notes',
            //     component: () => import('@/pages/WrongNotes.vue')
            // },

            { path: 'search/view/feed/list/job', name: 'job-view', component: JobView },
            { path: 'site', name: 'job-sites', component: JobSiteList },


            { path: 'todo', name: 'todo', component: Todo },
            { path: 'todo/new', name: 'NewTodo', component: NewTodo },

            { path: 'calendar', name: 'calendar', component: CalendarPage },

            // feed
            { path: 'notice', component: PostFeed },
            { path: 'mypage', component: MyPage },
            { path: 'notice/detail/:id', name: 'NoticeDetail', component: NoticeDetail },
            { path: 'post/update', name: 'PostUpdate', component: FeedUpdate },

            // certificate
            { path: 'certificate/data', component: CertificateData },
            { path: 'certificate/detail', component: CertificateDetail },
            { path: 'certificate/calendar', component: CertificateCalendar },
            { path: 'certificate/list', component: CertificateSearch },

            // interview
            { path: 'interview/govinterview', component: GovInterviewPanel },
            { path: 'interview/priinterview', component: PriInterviewPanel },

            // question bank
            { path: 'search/view/question', name: 'QuestionBank', component: QuestionBank },
            { path: 'search/view/practical/question', name: 'PracticeQuestion', component: PracticeQuestion },


            // { path: 'search/view/og/feed/id', meta: { ssrOnly: true } },
        ],
    },

    // auth
    { path: '/login', name: 'Login', component: Login, meta: { hideLayout: true } },
    { path: '/signup', component: SignUp, meta: { hideLayout: true } },

    // detail pages outside layout
    { path: '/post/:id', name: 'PostDetail', component: FeedDetail, meta: { hideLayout: true } },

    // others
    { path: '/search/view/feed/update', name: 'feed-update', component: FeedUpdate },
    { path: '/search/view/feed/Form', name: 'feed-form', component: FeedForm, meta: { hideLayout: true } },
    { path: '/user/profile/:username', name: 'user-profile', component: SomeoneProfile },
    { path: '/search/view/content', name: 'search-result', component: SearchResult },
    { path: '/search/view/feed/vote', name: 'vote-form', component: VoteForm },
    { path: '/poll/:id', name: 'PollDetail', component: PollDetail },
    { path: '/search/view/feed/list/page', component: MyPage },
    { path: '/notifications', name: 'Notifications', component: NotificationsPage },

    // oauth
    { path: '/google/callback', name: 'GoogleOAuth', component: OAuthGoogleLogin },
    { path: '/kakao/callback',  name: 'KakaoOAuth',  component: OAuthKakaoLogin },
    { path: '/naver/callback',  name: 'NaverOAuth',  component: OAuthNaverLogin },
]

export default createRouter({
    history: createWebHistory(),
    routes,
})
