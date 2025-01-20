import axios from 'axios'
import store from '@/store'
import { ElLoading } from 'element-plus'
import router from "@/router"

console.log('环境信息: ', process.env.NODE_ENV);

// 缺省认证信息
const defaultAuthentication = "default-token"

export default (config: any) => {
  let instance = axios.create(
    "development" == process.env.NODE_ENV ?
      // dev环境
      {
        baseURL: process.env.VUE_APP_DEV_BASE_URL,
        headers: { Authentication: store.getters.getAuthentication || defaultAuthentication }
      } :
      // prod环境
      {
        baseURL: process.env.VUE_APP_PROD_BASE_URL,
        headers: { Authentication: store.getters.getAuthentication || defaultAuthentication }
      });

  let loadingInstance: any;

  // 添加请求拦截器
  instance.interceptors.request.use(function (config: any) {
    // 在发送请求之前做些什么
    // 增加过度效果
    loadingInstance = ElLoading.service({
      lock: true,
      text: '加载中...',
      fullscreen: true,
      background: 'rgba(0, 0, 0, 0.7)'
    });
    return config;
  }, function (error: any) {
    // 对请求错误做些什么
    return Promise.reject(error);
  });

  // 添加响应拦截器
  instance.interceptors.response.use(function (response: any) {
    // 对响应数据做点什么
    // 关闭过度效果
    loadingInstance.close()
    return response;
  }, function (error: any) {
    // 对响应错误做点什么
    // 关闭过度效果
    loadingInstance.close()
    return Promise.reject(error);
  });

  return instance(config);
}
