import {createApp} from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import store from './store'
import './assets/css/base.css'
import DashboardIcon from "@/components/icon/DashboardIcon.vue";
import FolderIcon from "@/components/icon/FolderIcon.vue";
import MenuIcon from "@/components/icon/MenuIcon.vue";
import LogIcon from "@/components/icon/LogIcon.vue";
import PermissionIcon from "@/components/icon/PermissionIcon.vue";
import RoleIcon from "@/components/icon/RoleIcon.vue";
import SettingsIcon from "@/components/icon/SettingsIcon.vue";
import UserIcon from "@/components/icon/UserIcon.vue";
import BaiduIcon from "@/components/icon/BaiduIcon.vue";
import GoogleIcon from "@/components/icon/GoogleIcon.vue";
import BingIcon from "@/components/icon/BingIcon.vue";
import TenantIcon from "@/components/icon/TenantIcon.vue";
import ClusterIcon from "@/components/icon/ClusterIcon.vue";
import APIIcon from "@/components/icon/APIIcon.vue";
import MiddlewareIcon from "@/components/icon/MiddlewareIcon.vue";
import K8sIcon from "@/components/icon/K8sIcon.vue";
import CloudIcon from "@/components/icon/CloudIcon.vue";
import ContainerIcon from "@/components/icon/ContainerIcon.vue";
import GatewayIcon from "@/components/icon/GatewayIcon.vue";
import ReportIcon from "@/components/icon/ReportIcon.vue";
import ViewIcon from "@/components/icon/ViewIcon.vue";
import DictionaryIcon from "@/components/icon/DictionaryIcon.vue";
import TableManagementIcon from "@/components/icon/TableManagementIcon.vue";
import MySQLIcon from "@/components/icon/MySQLIcon.vue";
import OracleIcon from "@/components/icon/OracleIcon.vue";
import MongoDBIcon from "@/components/icon/MongoDBIcon.vue";
import RedisIcon from "@/components/icon/RedisIcon.vue";
import KafkaIcon from "@/components/icon/KafkaIcon.vue";
import NginxIcon from "@/components/icon/NginxIcon.vue";
import HAProxyIcon from "@/components/icon/HAProxyIcon.vue";
import ElasticsearchIcon from "@/components/icon/ElasticsearchIcon.vue";
import HomeIcon from "@/components/icon/HomeIcon.vue";

const zhCn = require('element-plus/dist/locale/zh-cn.min.js')

const app = createApp(App)
app.component('DashboardIcon', DashboardIcon);
app.component('FolderIcon', FolderIcon);
app.component('MenuIcon', MenuIcon);
app.component('LogIcon', LogIcon);
app.component('PermissionIcon', PermissionIcon);
app.component('RoleIcon', RoleIcon);
app.component('SettingsIcon', SettingsIcon);
app.component('UserIcon', UserIcon);
app.component('BaiduIcon', BaiduIcon);
app.component('GoogleIcon', GoogleIcon);
app.component('BingIcon', BingIcon);
app.component('TenantIcon', TenantIcon);
app.component('ClusterIcon', ClusterIcon);
app.component('APIIcon', APIIcon);
app.component('MiddlewareIcon', MiddlewareIcon);
app.component('K8sIcon', K8sIcon);
app.component('CloudIcon', CloudIcon);
app.component('ContainerIcon', ContainerIcon);
app.component('GatewayIcon', GatewayIcon);
app.component('ReportIcon', ReportIcon);
app.component('ViewIcon', ViewIcon);
app.component('DictionaryIcon', DictionaryIcon);
app.component('TableManagementIcon', TableManagementIcon);
app.component('MySQLIcon', MySQLIcon);
app.component('OracleIcon', OracleIcon);
app.component('MongoDBIcon', MongoDBIcon);
app.component('RedisIcon', RedisIcon);
app.component('KafkaIcon', KafkaIcon);
app.component('NginxIcon', NginxIcon);
app.component('HAProxyIcon', HAProxyIcon);
app.component('ElasticsearchIcon', ElasticsearchIcon);
app.component('HomeIcon', HomeIcon)

app.use(ElementPlus, {locale: zhCn})
app.use(store)
app.use(router)
app.mount('#app')
