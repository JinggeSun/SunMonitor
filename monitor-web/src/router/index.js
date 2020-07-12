import Vue from 'vue'
import Router from 'vue-router'
import Monitor from '@/components/monitor'
import Login from '@/components/login'
import Index from '@/components/index'
Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'monitor',
      component: Monitor
    },
    {
      path:'/login',
      name: 'Login',
      component: Login
    },
    {
      path:'/index',
      name: 'Index',
      component: Index
    }
  ]
})
