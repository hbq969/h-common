export default {
  setAuthentication(state: any, payload: any) {
    state.authentication = payload.authentication;
    console.log('设置认证信息: {}', state.authentication)
  }
}
