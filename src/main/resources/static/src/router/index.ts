import {createRouter, createWebHashHistory} from 'vue-router'

const routes = [
    {
        path: '/encrypt',
        component: () => import((`@/views/common/encrypt.vue`))
    }
]

const router = createRouter({
    // history: createWebHistory(process.env.BASE_URL),
    history: createWebHashHistory(process.env.BASE_URL),
    routes
})

export default router