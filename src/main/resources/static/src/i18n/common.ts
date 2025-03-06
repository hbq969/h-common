import axios from '@/network/index'
import {msg} from '@/utils/Utils'

const langs1 = new Map();
langs1.set('zh-CN', '中文')
langs1.set('en-US', 'English')
langs1.set('ja-JP', '日本語')
const langs2 = new Map();
langs2.set('中文', 'zh-CN')
langs2.set('English', 'en-US')
langs2.set('日本語', 'ja-JP')

export function langKey2Desc(key: string) {
    return langs1.get(key)
}


export function langDesc2Key(desc: string) {
    return langs2.get(desc)
}

export function switchLang(event: any) {
    let langKey = langDesc2Key(event)
    axios({
        url: '/i18n/lang',
        method: 'put',
        data: {lang: langKey}
    }).then((res: any) => {
        if (res.data.state == 'OK') {
            console.log('切换语言: ', langKey)
            window.sessionStorage.setItem('h-sm-lang', langKey)
            window.location.reload()
        } else {
            let content = res.config.baseURL + res.config.url + ': ' + res.data.errorMessage;
            msg(content, "warning")
        }
    }).catch((err: Error) => {
        console.log('', err)
    })
}

export function queryLangData(map: any, keys: []) {
    axios({
        url: '/i18n/lang/data',
        method: 'get',

    }).then((res: any) => {
        if (res.data.state == 'OK') {
            let pairs = res.data.body
            keys.forEach(key => {
                map[key] = pairs[key]
            })
        } else {
            let content = res.config.baseURL + res.config.url + ': ' + res.data.errorMessage;
            msg(content, "warning")
        }
    }).catch((err: Error) => {
        console.log('', err)
        msg('请求异常', 'error')
    })
}

export function queryLang(currentPageLanguage: string, lang: any) {
    axios({
        url: '/i18n/lang',
        method: 'get'
    }).then((res: any) => {
        if (res.data.state == 'OK') {
            let langKey = res.data.body
            console.log('从后端查询到语言: ', langKey)
            lang.value = langKey2Desc(langKey)
            sessionStorage.setItem('h-sm-lang', langKey)
        } else {
            let content = res.config.baseURL + res.config.url + ': ' + res.data.errorMessage;
            msg(content, "warning")
        }
    }).catch((err: Error) => {
        console.log('', err)
    })
}