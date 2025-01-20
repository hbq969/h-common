import {createStore} from 'vuex'
import getters from './getters'
import mutations from './mutations'
import actions from './actions'

const state = {
  msg: 'hello vuex',
  authentication: '',
}

export default createStore({
  state,
  getters: getters,
  mutations: mutations,
  actions: actions,
  modules: {}
})
