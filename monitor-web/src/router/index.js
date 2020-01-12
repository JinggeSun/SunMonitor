import Vue from 'vue'
import Router from 'vue-router'
import Monitor from '@/components/monitor'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'monitor',
      component: Monitor
    }
  ]
})
