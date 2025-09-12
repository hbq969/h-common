<script lang="ts" setup>
import {
  Edit
} from '@element-plus/icons-vue'
import {ref, reactive, onMounted, computed, provide, inject} from 'vue'
import axios from '@/network'
import {msg, notify} from '@/utils/Utils'
import type {FormInstance, FormRules} from 'element-plus'
import {locale} from "@/i18n/locale";

const language = sessionStorage.getItem('h-sm-lang') || 'zh-CN'
const langData = locale[language]
const publicKey = ref('')

onMounted(() => {
  axios({
    url: '/encrypt/restful/rsa/publicKey',
    method: 'get',
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      publicKey.value = res.data.body
    } else {
      notify(langData.notifyTitle, res.data.errorMessage, 'warning')
    }
  }).catch((err: Error) => {
    console.log('', err)
    notify(langData.notifyTitle, langData.axiosRequestErr, 'error')
  })
})

const form = reactive({
  keySource: 'default',
  encodeData: '',
  encodeResult: '',
  customKey: ''
})
const formRef1 = ref<FormInstance>();
const rules1 = reactive<FormRules>({
  encodeData: [{required: true, message: langData.notNull, trigger: 'blur'}],
  customKey: [{required: true, message: langData.notNull, trigger: 'blur'}]
})

const springConfigEncode = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      axios({
        url: '/encrypt/config/encrypt',
        method: 'post',
        data: {
          key: form.keySource == 'default' ? null : form.customKey,
          data: form.encodeData
        }
      }).then((res: any) => {
        if (res.data.state == 'OK') {
          form.encodeResult = res.data.body
        } else {
          let content = res.config.baseURL + res.config.url + ': ' + res.data.errorMessage;
          msg(content, "warning")
        }
      }).catch((err: Error) => {
        console.log('', err)
        msg(langData.requestErr, 'error')
      })
    }
  })
}

const resetSpringConfigEncode = () => {
  form.keySource = 'default'
  form.encodeData = ''
  form.encodeResult = ''
  form.customKey = ''
  form.decodeData = ''
  form.decodeResult = ''
}

const form2 = reactive({
  keySource: 'default',
  customKey: '',
  decodeData: '',
  decodeResult: ''
})
const formRef2 = ref<FormInstance>();
const rules2 = reactive<FormRules>({
  decodeData: [{required: true, message: langData.notNull, trigger: 'blur'}],
  customKey: [{required: true, message: langData.notNull, trigger: 'blur'}]
})

const springConfigEncode2 = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      axios({
        url: '/encrypt/config/decrypt',
        method: 'post',
        data: {
          key: form2.keySource == 'default' ? null : form2.customKey,
          data: form2.decodeData
        }
      }).then((res: any) => {
        if (res.data.state == 'OK') {
          form2.decodeResult = res.data.body
        } else {
          let content = res.config.baseURL + res.config.url + ': ' + res.data.errorMessage;
          msg(content, "warning")
        }
      }).catch((err: Error) => {
        console.log('', err)
        msg(langData.requestErr, 'error')
      })
    }
  })
}

const resetSpringConfigEncode2 = () => {
  form2.keySource = 'default'
  form2.customKey = ''
  form2.decodeData = ''
  form2.decodeResult = ''
}

const aesRandomKey = ref('')
const aesRandomKeyCreate = () => {
  axios({
    url: '/encrypt/restful/aes/getRandomKey',
    method: 'get'
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      aesRandomKey.value = res.data.body
    } else {
      let content = res.config.baseURL + res.config.url + ': ' + res.data.errorMessage;
      msg(content, "warning")
    }
  }).catch((err: Error) => {
    console.log('', err)
    msg(langData.requestErr, 'error')
  })
}

const aesRandomKey1 = async () => {
  try {
    let res = await axios({
      url: '/encrypt/restful/aes/getRandomKey',
      method: 'get'
    })
    if (res.data.state == 'OK') {
      form3.encryptKey = res.data.body
    } else {
      notify(langData.notifyTitle, res.data.errorMessage, 'warning')
    }
  } catch (e) {
    console.log(e)
    notify(langData.notifyTitle, langData.axiosRequestErr, 'error')
  }
}

const aesRandomKey2 = async () => {
  try {
    let res = await axios({
      url: '/encrypt/restful/aes/getRandomKey',
      method: 'get'
    })
    if (res.data.state == 'OK') {
      form3.iv = res.data.body
    } else {
      notify(langData.notifyTitle, res.data.errorMessage, 'warning')
    }
  } catch (e) {
    console.log(e)
    notify(langData.notifyTitle, langData.axiosRequestErr, 'error')
  }
}

const resetAESKey = () => {
  aesRandomKey.value = ''
}


const form3 = reactive({
  encryptKey: '',
  iv: '',
  encodeData: '',
  encodeResult: ''
})
const formRef3 = ref<FormInstance>();
const rules3 = reactive<FormRules>({
  encryptKey: [{required: true, message: langData.notNull, trigger: 'blur'}],
  iv: [{required: true, message: langData.notNull, trigger: 'blur'}],
  encodeData: [{required: true, message: langData.notNull, trigger: 'blur'}]
})

const aesEncode = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      axios({
        url: '/encrypt/restful/aes/encrypt',
        method: 'post',
        data: {
          key: form3.encryptKey,
          iv: form3.iv,
          content: form3.encodeData
        }
      }).then((res: any) => {
        if (res.data.state == 'OK') {
          form3.encodeResult = res.data.body
        } else {
          let content = res.config.baseURL + res.config.url + ': ' + res.data.errorMessage;
          msg(content, "warning")
        }
      }).catch((err: Error) => {
        console.log('', err)
        msg(langData.requestErr, 'error')
      })
    }
  })
}

const resetAESEncode = () => {
  form3.encryptKey = ''
  form3.iv = ''
  form3.encodeData = ''
  form3.encodeResult = ''
}

const form4 = reactive({
  decryptKey: '',
  iv: '',
  decodeData: '',
  decodeResult: ''
})
const formRef4 = ref<FormInstance>();
const rules4 = reactive<FormRules>({
  decryptKey: [{required: true, message: langData.notNull, trigger: 'blur'}],
  iv: [{required: true, message: langData.notNull, trigger: 'blur'}],
  decodeData: [{required: true, message: langData.notNull, trigger: 'blur'}]
})

const aesDecode = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      axios({
        url: '/encrypt/restful/aes/decrypt',
        method: 'post',
        data: {
          key: form4.decryptKey,
          iv: form4.iv,
          content: form4.decodeData
        }
      }).then((res: any) => {
        if (res.data.state == 'OK') {
          form4.decodeResult = res.data.body
        } else {
          let content = res.config.baseURL + res.config.url + ': ' + res.data.errorMessage;
          msg(content, "warning")
        }
      }).catch((err: Error) => {
        console.log('', err)
        msg(langData.requestErr, 'error')
      })
    }
  })
}

const resetAESDecode = () => {
  form4.decryptKey = ''
  form4.iv = ''
  form4.decodeData = ''
  form4.decodeResult = ''
}

const form5 = reactive({
  privateKey: '',
  publicKey: ''
})

const rsaKeyPairCreate = () => {
  axios({
    url: '/encrypt/restful/rsa/genKeyPair',
    method: 'get'
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      form5.privateKey = res.data.body.RSAPrivateKey
      form5.publicKey = res.data.body.RSAPublicKey
    } else {
      let content = res.config.baseURL + res.config.url + ': ' + res.data.errorMessage;
      msg(content, "warning")
    }
  }).catch((err: Error) => {
    console.log('', err)
    msg(langData.requestErr, 'error')
  })
}

const resetRsaKeyPair = () => {
  form5.privateKey = ''
  form5.publicKey = ''
}

const form6 = reactive({
  publicKey: publicKey,
  encodeData: '',
  encodeResult: ''
})
const formRef6 = ref<FormInstance>()
const rules6 = reactive<FormRules>({
  publicKey: [{required: true, message: langData.notNull, trigger: 'blur'}],
  encodeData: [{required: true, message: langData.notNull, trigger: 'blur'}]
})

const rsaEncode = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      axios({
        url: '/encrypt/restful/rsa/encryptWithPublicKey',
        method: 'post',
        data: {
          publicKey: form6.publicKey,
          obj: form6.encodeData
        },
        params: {}
      }).then((res: any) => {
        if (res.data.state == 'OK') {
          form6.encodeResult = res.data.body
        } else {
          let content = res.config.baseURL + res.config.url + ': ' + res.data.errorMessage;
          msg(content, "warning")
        }
      }).catch((err: Error) => {
        console.log('', err)
        msg(langData.requestErr, 'error')
      })
    }
  })
}

const resetRsaEncode = () => {
  form6.publicKey = ''
  form6.encodeData = ''
  form6.encodeResult = ''
}


const form7 = reactive({
  privateKey: '',
  decodeData: '',
  decodeResult: ''
})
const formRef7 = ref<FormInstance>()
const rules7 = reactive<FormRules>({
  privateKey: [{required: true, message: langData.notNull, trigger: 'blur'}],
  decodeData: [{required: true, message: langData.notNull, trigger: 'blur'}]
})

const rsaDecode = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      axios({
        url: '/encrypt/restful/rsa/decryptWithPrivateKey',
        method: 'post',
        data: {
          privateKey: form7.privateKey,
          encode: form7.decodeData
        },
        params: {}
      }).then((res: any) => {
        if (res.data.state == 'OK') {
          form7.decodeResult = res.data.body
        } else {
          let content = res.config.baseURL + res.config.url + ': ' + res.data.errorMessage;
          msg(content, "warning")
        }
      }).catch((err: Error) => {
        console.log('', err)
        msg(langData.requestErr, 'error')
      })
    }
  })
}

const resetRsaDecode = () => {
  form7.privateKey = ''
  form7.decodeData = ''
  form7.decodeResult = ''
}

const debounce = (callback: (...args: any[]) => void, delay: number) => {
  let tid: any;
  return function (...args: any[]) {
    const ctx = self;
    tid && clearTimeout(tid);
    tid = setTimeout(() => {
      callback.apply(ctx, args);
    }, delay);
  };
};

const _ = (window as any).ResizeObserver;
(window as any).ResizeObserver = class ResizeObserver extends _ {
  constructor(callback: (...args: any[]) => void) {
    callback = debounce(callback, 20);
    super(callback);
  }
};

const formRef8 = ref<FormInstance>()
const rules8 = reactive<FormRules>({
  publicKey: [{required: true, message: langData.notNull, trigger: 'blur'}],
})
const form8 = reactive({
  publicKey: publicKey,
  key: '',
  iv: '',
  keyRSA: '',
  ivRSA: '',
  body: '',
  bodyAES: '',
  result: '',
  base64Result: ''
})

const form8Encrypt = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  if (form8.key == '' || form8.iv == '' || form8.keyRSA == '' || form8.ivRSA == '' || form8.body == '') {
    notify(langData.notifyTitle, 'key、iv、keyRSA、ivRSA、body不能为空', 'warning')
    return
  }
  await formEl.validate((valid, fields) => {
    if (valid) {
      axios({
        url: '/encrypt/restful/aes/encrypt',
        method: 'post',
        data: {
          key: form8.key,
          iv: form8.iv,
          content: form8.body
        }
      }).then((res: any) => {
        if (res.data.state == 'OK') {
          let map = {} as any
          map.key = form8.keyRSA
          map.iv = form8.ivRSA
          map.body = res.data.body
          form8.result = JSON.stringify(map)
          form8.base64Result = btoa(form8.result)
        } else {
          notify(langData.notifyTitle, res.data.errorMessage, 'warning')
        }
      }).catch((err: Error) => {
        console.log('', err)
        notify(langData.notifyTitle, langData.axiosRequestErr, 'error')
      })
    }
  })
}

const form8Dencrypt = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  if (form8.result == '' && form8.base64Result == '') {
    notify(langData.notifyTitle, '请求体和base64编码不能同时为空', 'warning')
    return
  }
  await formEl.validate((valid, fields) => {
    if (valid) {
      if (form8.base64Result != '')
        form8.result = atob(form8.base64Result)
      let map = JSON.parse(form8.result)
      form8.keyRSA = map.key
      form8.ivRSA = map.iv
      axios({
        url: '/encrypt/restful/aes/ras/decrypt',
        method: 'post',
        data: {
          keyRSA: form8.keyRSA,
          ivRSA: form8.ivRSA,
          bodyEncode: map.body
        },
      }).then((res: any) => {
        if (res.data.state == 'OK') {
          form8.key = res.data.body.key
          form8.iv = res.data.body.iv
          form8.body = res.data.body.body
        } else {
          notify(langData.notifyTitle, res.data.errorMessage, 'warning')
        }
      }).catch((err: Error) => {
        console.log('', err)
        notify(langData.notifyTitle, langData.axiosRequestErr, 'error')
      })
    }
  })
}

const form8Reset = () => {
  form8.key = ''
  form8.iv = ''
  form8.keyRSA = ''
  form8.ivRSA = ''
  form8.body = ''
  form8.bodyAES = ''
  form8.result = ''
  form8.base64Result = ''
}

const aesRandomKey3 = async () => {
  try {
    let res = await axios({
      url: '/encrypt/restful/aes/getRandomKey',
      method: 'get'
    })
    if (res.data.state == 'OK') {
      form8.key = res.data.body
    } else {
      notify(langData.notifyTitle, res.data.errorMessage, 'warning')
    }
    if (res.data.state == 'OK') {
      form8.key = res.data.body
    } else {
      notify(langData.notifyTitle, res.data.errorMessage, 'warning')
    }
    let res2 = await axios({
      url: '/encrypt/restful/rsa/encryptWithPublicKey',
      method: 'post',
      data: {
        publicKey: form8.publicKey,
        obj: form8.key
      }
    })
    if (res2.data.state == 'OK') {
      form8.keyRSA = res2.data.body
    } else {
      notify(langData.notifyTitle, res2.data.errorMessage, 'warning')
    }
  } catch (e) {
    console.log(e)
    notify(langData.notifyTitle, langData.axiosRequestErr, 'error')
  }
}

const aesRandomKey4 = async () => {
  try {
    let res = await axios({
      url: '/encrypt/restful/aes/getRandomKey',
      method: 'get'
    })
    if (res.data.state == 'OK') {
      form8.iv = res.data.body
    } else {
      notify(langData.notifyTitle, res.data.errorMessage, 'warning')
    }
    let res2 = await axios({
      url: '/encrypt/restful/rsa/encryptWithPublicKey',
      method: 'post',
      data: {
        publicKey: form8.publicKey,
        obj: form8.iv
      }
    })
    if (res2.data.state == 'OK') {
      form8.ivRSA = res2.data.body
    } else {
      notify(langData.notifyTitle, res2.data.errorMessage, 'warning')
    }
  } catch (e) {
    console.log(e)
    notify(langData.notifyTitle, langData.axiosRequestErr, 'error')
  }
}

</script>

<template>
  <div class="container">
    <el-row>
      <el-col :span="12">
        <el-card style="width: 90%">
          <template #header>
            <div class="card-header">
              <span>{{ langData.dialog1Title }}</span>
            </div>
          </template>
          <el-form :model="form" size="small" label-position="right" inline-message :inline="false" label-width="100px"
                   ref="formRef1" :rules="rules1">
            <el-form-item :label="langData.dialog1SecretSource" prop="keySource">
              <el-radio-group v-model="form.keySource" size="small">
                <el-radio-button label="default" value="缺省默认"/>
                <el-radio-button label="custom" value="自定义"/>
              </el-radio-group>
            </el-form-item>
            <el-form-item :label="langData.dialog3CustomSecret" prop="customKey" v-if="form.keySource=='custom'">
              <el-input v-model="form.customKey" style="width: 100%"/>
            </el-form-item>
            <el-form-item :label="langData.dialog1EncryptContent" prop="encodeData">
              <el-input v-model="form.encodeData" type="textarea" rows="5" style="width: 100%"/>
            </el-form-item>
            <el-form-item :label="langData.dialog1EncryptResult" prop="encodeResult">
              <el-input v-model="form.encodeResult" type="textarea" rows="5" style="width: 100%" readonly/>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="small" @click="springConfigEncode(formRef1)">
                {{ langData.dialog1BtnSubmit }}
              </el-button>
              <el-button type="info" size="small" @click="resetSpringConfigEncode">{{ langData.dialog1BtnReset }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card style="width: 90%">
          <template #header>
            <div class="card-header">
              <span>{{ langData.dialog2Title }}</span>
            </div>
          </template>
          <el-form :model="form2" size="small" label-position="right" inline-message :inline="false" label-width="100px"
                   ref="formRef2" :rules="rules2">
            <el-form-item :label="langData.dialog2SecretSource" prop="keySource">
              <el-radio-group v-model="form2.keySource" size="small">
                <el-radio-button label="default" value="缺省默认"/>
                <el-radio-button label="custom" value="自定义"/>
              </el-radio-group>
            </el-form-item>
            <el-form-item :label="langData.dialog3CustomSecret" prop="customKey" v-if="form2.keySource=='custom'">
              <el-input v-model="form2.customKey" style="width: 100%"/>
            </el-form-item>
            <el-form-item :label="langData.dialog2EncryptContent" prop="encodeData">
              <el-input v-model="form2.decodeData" type="textarea" rows="5" style="width: 100%"/>
            </el-form-item>
            <el-form-item :label="langData.dialog2EncryptResult" prop="encodeResult">
              <el-input v-model="form2.decodeResult" type="textarea" rows="5" style="width: 100%" readonly/>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="small" @click="springConfigEncode2(formRef2)">
                {{ langData.dialog2BtnSubmit }}
              </el-button>
              <el-button type="info" size="small" @click="resetSpringConfigEncode2">{{ langData.dialog2BtnReset }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="12" style="margin-top: 5px">
        <el-card style="width: 90%;">
          <template #header>
            <div class="card-header">
              <span>{{ langData.dialog4Title }}</span>
            </div>
          </template>
          <el-form :model="form3" size="small" label-position="right" inline-message :inline="false" label-width="100px"
                   ref="formRef3" :rules="rules3">
            <el-form-item :label="langData.dialog4Secret" prop="encryptKey">
              <el-input v-model="form3.encryptKey" type="text" style="width: 100%">
                <template #suffix>
                  <svg style="cursor: pointer;" :title="langData.randomPassword" @click="aesRandomKey1"
                       t="1757470138917"
                       class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6653"
                       width="14" height="14">
                    <path
                        d="M421.376 481.28s117.248 24.576 175.104-8.704c0 0-89.6 70.144-89.6 166.4 0.512-0.512-8.192-121.344-85.504-157.696zM438.784 969.216s68.608 6.656 68.608-80.896c0 0 3.072 88.576 65.024 78.336 0 0.512-50.688 22.016-133.632 2.56zM161.28 238.08s-30.208 65.536 11.264 91.648c0 0-67.072-17.408-81.408 37.376 0 0 8.704-82.944 70.144-129.024zM857.6 227.328s49.152 50.176 1.024 81.408c0 0 58.88-18.432 66.56 36.352 0 0 5.12-69.632-67.584-117.76z"
                        fill="#454859" p-id="6654"></path>
                    <path
                        d="M443.392 970.752c-5.632 0-10.752-1.024-15.36-3.072l-270.848-157.184-1.536-1.024s-1.024-1.024-4.608-2.56c-51.2-29.184-62.976-94.208-65.536-120.832V386.56c0-3.072 0.512-7.168 1.024-11.264l0.512-3.584 1.024-2.56c19.456-50.688 76.8-51.2 103.936-44.032l-1.536 5.632 4.096-6.144L476.16 486.4l18.944 37.888c20.992 36.864 29.184 77.824 32.768 99.84v258.048c-4.608 56.32-36.864 76.288-55.808 82.944-1.024 0.512-15.36 5.632-28.672 5.632z m-262.144-196.096l263.168 152.576c12.288-0.512 36.864-6.656 40.448-48.128v-250.368c-4.608-31.744-20.992-103.936-72.192-128L322.56 445.44l1.536 3.072-142.336-82.432c-2.048-0.512-40.448-9.216-52.736 15.872-0.512 2.56-0.512 4.608-0.512 6.144v294.4c1.536 16.896 9.728 67.072 43.52 86.528 3.584 2.048 6.656 4.096 9.216 5.632z"
                        fill="#454859" p-id="6655"></path>
                    <path
                        d="M837.632 212.992c6.656 4.096 12.8 7.168 18.432 10.752l1.536 1.024 1.536 1.536c5.12 4.096 10.752 9.216 16.384 15.36 6.144 11.776 5.632 33.28 4.608 49.152-1.024 12.288-6.656 30.208-26.624 44.544l-1.024 0.512-247.808 156.672c-26.624 14.336-62.976 18.432-96.256 18.432-40.96 0-77.824-6.656-89.088-8.704l-3.072-0.512-245.248-142.336c-39.424-29.696-28.16-85.504-15.36-113.664l2.56-6.144 263.68-166.912c29.184-14.336 104.448-43.008 173.056-1.024 3.584 2.56 58.368 34.304 119.296 69.632M431.616 460.8c40.448 7.168 114.176 13.824 152.576-6.144l244.736-155.136c7.168-5.632 8.192-10.24 8.704-12.8 1.024-11.264-9.728-26.624-15.36-32.768-55.808-32.256-243.712-141.312-250.368-145.408-49.664-30.72-107.008-9.216-130.048 2.56L192.512 268.8c-4.096 12.288-12.288 42.496 3.584 55.808l235.52 136.192z"
                        fill="#454859" p-id="6656"></path>
                    <path
                        d="M831.488 299.008c4.096-1.024 38.4-11.264 66.048 6.144 7.168 4.608 17.92 11.776 24.064 24.576 1.024 5.632 4.096 10.752 4.608 16.896v2.048l-1.024 323.072c-5.12 35.328-22.528 91.648-77.312 125.44l-5.12 3.584h-1.024l-262.144 165.888-4.608 0.512c-4.096 0.512-8.704 1.024-12.8 1.024-15.872 0-30.208-5.12-41.984-14.848-24.576-20.48-32.768-55.808-35.328-73.728l-1.024-252.928h1.536c6.144-96.768 88.576-164.864 96.768-171.008l-0.512-0.512L829.44 299.52m-301.056 567.808c0.512 10.24 5.12 41.472 19.968 53.76 3.072 2.56 7.68 5.632 16.384 5.12l264.704-167.936c56.32-38.4 53.76-115.712 53.76-116.224l-0.512-32.256 1.024-250.368h-0.512c-1.536-12.8-7.168-16.384-8.704-17.408-8.704-5.632-23.552-3.072-28.672-2.048l-235.52 148.992c-1.024 0.512-80.896 65.024-80.896 149.504h-1.536l0.512 228.864zM435.2 264.192c0 27.648 31.744 50.176 71.168 50.176s71.168-22.528 71.168-50.176-31.744-50.176-71.168-50.176S435.2 236.544 435.2 264.192z"
                        fill="#454859" p-id="6657"></path>
                    <path
                        d="M663.552 782.848c0 30.72-22.528 67.072-49.664 80.384-27.648 13.824-50.176-0.512-50.176-31.232s22.528-67.072 50.176-80.384c27.136-13.824 49.664 0 49.664 31.232zM760.32 602.624c0 30.72-22.528 67.072-49.664 80.384-27.648 13.824-49.664-0.512-49.664-31.232s22.528-67.072 49.664-80.384c27.136-13.824 49.664 0.512 49.664 31.232zM867.84 428.032c0 30.72-22.528 67.072-49.664 80.384-27.648 13.824-50.176-0.512-50.176-31.232s22.528-67.072 50.176-80.384c27.136-13.824 49.664 0 49.664 31.232zM270.848 538.112c0 30.72-22.016 41.984-48.64 24.576-27.136-16.896-48.64-55.808-48.64-86.528 0-30.72 22.016-41.984 48.64-24.576 26.624 16.896 48.64 55.808 48.64 86.528zM432.128 823.296c0 30.72-22.016 41.984-48.64 24.576-26.624-17.408-48.64-55.808-48.64-86.528 0-30.72 22.016-41.984 48.64-24.576 26.624 16.896 48.64 55.808 48.64 86.528z"
                        fill="#454859" p-id="6658"></path>
                  </svg>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item :label="langData.dialog4Iv" prop="iv">
              <el-input v-model="form3.iv" type="text" style="width: 100%">
                <template #suffix>
                  <svg style="cursor: pointer;" :title="langData.randomPassword" @click="aesRandomKey2"
                       t="1757470138917"
                       class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6653"
                       width="14" height="14">
                    <path
                        d="M421.376 481.28s117.248 24.576 175.104-8.704c0 0-89.6 70.144-89.6 166.4 0.512-0.512-8.192-121.344-85.504-157.696zM438.784 969.216s68.608 6.656 68.608-80.896c0 0 3.072 88.576 65.024 78.336 0 0.512-50.688 22.016-133.632 2.56zM161.28 238.08s-30.208 65.536 11.264 91.648c0 0-67.072-17.408-81.408 37.376 0 0 8.704-82.944 70.144-129.024zM857.6 227.328s49.152 50.176 1.024 81.408c0 0 58.88-18.432 66.56 36.352 0 0 5.12-69.632-67.584-117.76z"
                        fill="#454859" p-id="6654"></path>
                    <path
                        d="M443.392 970.752c-5.632 0-10.752-1.024-15.36-3.072l-270.848-157.184-1.536-1.024s-1.024-1.024-4.608-2.56c-51.2-29.184-62.976-94.208-65.536-120.832V386.56c0-3.072 0.512-7.168 1.024-11.264l0.512-3.584 1.024-2.56c19.456-50.688 76.8-51.2 103.936-44.032l-1.536 5.632 4.096-6.144L476.16 486.4l18.944 37.888c20.992 36.864 29.184 77.824 32.768 99.84v258.048c-4.608 56.32-36.864 76.288-55.808 82.944-1.024 0.512-15.36 5.632-28.672 5.632z m-262.144-196.096l263.168 152.576c12.288-0.512 36.864-6.656 40.448-48.128v-250.368c-4.608-31.744-20.992-103.936-72.192-128L322.56 445.44l1.536 3.072-142.336-82.432c-2.048-0.512-40.448-9.216-52.736 15.872-0.512 2.56-0.512 4.608-0.512 6.144v294.4c1.536 16.896 9.728 67.072 43.52 86.528 3.584 2.048 6.656 4.096 9.216 5.632z"
                        fill="#454859" p-id="6655"></path>
                    <path
                        d="M837.632 212.992c6.656 4.096 12.8 7.168 18.432 10.752l1.536 1.024 1.536 1.536c5.12 4.096 10.752 9.216 16.384 15.36 6.144 11.776 5.632 33.28 4.608 49.152-1.024 12.288-6.656 30.208-26.624 44.544l-1.024 0.512-247.808 156.672c-26.624 14.336-62.976 18.432-96.256 18.432-40.96 0-77.824-6.656-89.088-8.704l-3.072-0.512-245.248-142.336c-39.424-29.696-28.16-85.504-15.36-113.664l2.56-6.144 263.68-166.912c29.184-14.336 104.448-43.008 173.056-1.024 3.584 2.56 58.368 34.304 119.296 69.632M431.616 460.8c40.448 7.168 114.176 13.824 152.576-6.144l244.736-155.136c7.168-5.632 8.192-10.24 8.704-12.8 1.024-11.264-9.728-26.624-15.36-32.768-55.808-32.256-243.712-141.312-250.368-145.408-49.664-30.72-107.008-9.216-130.048 2.56L192.512 268.8c-4.096 12.288-12.288 42.496 3.584 55.808l235.52 136.192z"
                        fill="#454859" p-id="6656"></path>
                    <path
                        d="M831.488 299.008c4.096-1.024 38.4-11.264 66.048 6.144 7.168 4.608 17.92 11.776 24.064 24.576 1.024 5.632 4.096 10.752 4.608 16.896v2.048l-1.024 323.072c-5.12 35.328-22.528 91.648-77.312 125.44l-5.12 3.584h-1.024l-262.144 165.888-4.608 0.512c-4.096 0.512-8.704 1.024-12.8 1.024-15.872 0-30.208-5.12-41.984-14.848-24.576-20.48-32.768-55.808-35.328-73.728l-1.024-252.928h1.536c6.144-96.768 88.576-164.864 96.768-171.008l-0.512-0.512L829.44 299.52m-301.056 567.808c0.512 10.24 5.12 41.472 19.968 53.76 3.072 2.56 7.68 5.632 16.384 5.12l264.704-167.936c56.32-38.4 53.76-115.712 53.76-116.224l-0.512-32.256 1.024-250.368h-0.512c-1.536-12.8-7.168-16.384-8.704-17.408-8.704-5.632-23.552-3.072-28.672-2.048l-235.52 148.992c-1.024 0.512-80.896 65.024-80.896 149.504h-1.536l0.512 228.864zM435.2 264.192c0 27.648 31.744 50.176 71.168 50.176s71.168-22.528 71.168-50.176-31.744-50.176-71.168-50.176S435.2 236.544 435.2 264.192z"
                        fill="#454859" p-id="6657"></path>
                    <path
                        d="M663.552 782.848c0 30.72-22.528 67.072-49.664 80.384-27.648 13.824-50.176-0.512-50.176-31.232s22.528-67.072 50.176-80.384c27.136-13.824 49.664 0 49.664 31.232zM760.32 602.624c0 30.72-22.528 67.072-49.664 80.384-27.648 13.824-49.664-0.512-49.664-31.232s22.528-67.072 49.664-80.384c27.136-13.824 49.664 0.512 49.664 31.232zM867.84 428.032c0 30.72-22.528 67.072-49.664 80.384-27.648 13.824-50.176-0.512-50.176-31.232s22.528-67.072 50.176-80.384c27.136-13.824 49.664 0 49.664 31.232zM270.848 538.112c0 30.72-22.016 41.984-48.64 24.576-27.136-16.896-48.64-55.808-48.64-86.528 0-30.72 22.016-41.984 48.64-24.576 26.624 16.896 48.64 55.808 48.64 86.528zM432.128 823.296c0 30.72-22.016 41.984-48.64 24.576-26.624-17.408-48.64-55.808-48.64-86.528 0-30.72 22.016-41.984 48.64-24.576 26.624 16.896 48.64 55.808 48.64 86.528z"
                        fill="#454859" p-id="6658"></path>
                  </svg>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item :label="langData.dialog4EncryptContent" prop="encodeData">
              <el-input v-model="form3.encodeData" type="textarea" rows="5" style="width: 100%"/>
            </el-form-item>
            <el-form-item :label="langData.dialog4EncryptResult" prop="encodeResult">
              <el-input v-model="form3.encodeResult" type="textarea" rows="5" style="width: 100%" readonly/>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="small" @click="aesEncode(formRef3)">{{ langData.dialog4BtnSubmit }}
              </el-button>
              <el-button type="info" size="small" @click="resetAESEncode">{{ langData.dialog4BtnReset }}</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12" style="margin-top: 5px">
        <el-card style="width: 90%;">
          <template #header>
            <div class="card-header">
              <span>{{ langData.dialog5Title }}</span>
            </div>
          </template>
          <el-form :model="form4" size="small" label-position="right" inline-message :inline="false" label-width="100px"
                   ref="formRef4" :rules="rules4">
            <el-form-item :label="langData.dialog5Secret" prop="decryptKey">
              <el-input v-model="form4.decryptKey" type="text" style="width: 100%">
                <template #suffix>
                  <svg @click="form4.decryptKey=form3.encryptKey" style="cursor: pointer" t="1757584984156" class="icon"
                       viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="22578" width="14"
                       height="14">
                    <path
                        d="M260.7 883.3L126 703.7h770v64.2H254.3l57.8 77zM896 318.7H126v-64.2h641.7l-57.8-77 51.4-38.5z"
                        fill="#1296db" p-id="22579"></path>
                  </svg>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item :label="langData.dialog4Iv" prop="iv">
              <el-input v-model="form4.iv" type="text" style="width: 100%">
                <template #suffix>
                  <svg @click="form4.iv=form3.iv" style="cursor: pointer" t="1757584984156" class="icon"
                       viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="22578" width="14"
                       height="14">
                    <path
                        d="M260.7 883.3L126 703.7h770v64.2H254.3l57.8 77zM896 318.7H126v-64.2h641.7l-57.8-77 51.4-38.5z"
                        fill="#1296db" p-id="22579"></path>
                  </svg>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item :label="langData.dialog5EncryptContent" prop="decodeData">
              <el-input v-model="form4.decodeData" type="textarea" rows="5" style="width: 100%"/>
            </el-form-item>
            <el-form-item :label="langData.dialog5EncryptResult" prop="encodeResult">
              <el-input v-model="form4.decodeResult" type="textarea" rows="5" style="width: 100%" readonly/>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="small" @click="aesDecode(formRef4)">{{ langData.dialog5BtnSubmit }}
              </el-button>
              <el-button type="info" size="small" @click="resetAESDecode">{{ langData.dialog5BtnReset }}</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24" style="margin-top: 5px">
        <el-card style="max-width: 95%;">
          <template #header>
            <div class="card-header">
              <span>{{ langData.dialog3Title }}</span>
            </div>
          </template>
          <el-form size="small" label-position="right" inline-message :inline="false" label-width="100px">
            <el-form-item :label="langData.dialog3CustomSecret" prop="aesRandomKey">
              <el-input v-model="aesRandomKey" type="text" style="width: 100%"/>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="small" @click="aesRandomKeyCreate()">{{ langData.dialog3BtnSubmit }}
              </el-button>
              <el-button type="info" size="small" @click="resetAESKey">{{ langData.dialog3BtnReset }}</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24" style="margin-top: 5px">
        <el-card style="width: 95%;">
          <template #header>
            <div class="card-header">
              <span>{{ langData.dialog6Title }}</span>
            </div>
          </template>
          <el-form size="small" label-position="right" inline-message :inline="false" label-width="100px">
            <el-form-item :label="langData.dialog6PrivateKey" prop="privateKey">
              <el-input v-model="form5.privateKey" type="textarea" rows="5" style="width: 100%" readonly/>
            </el-form-item>
            <el-form-item :label="langData.dialog6PublicKey" prop="publicKey">
              <el-input v-model="form5.publicKey" type="textarea" rows="3" style="width: 100%" readonly/>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="small" @click="rsaKeyPairCreate()">{{ langData.dialog6BtnSubmit }}
              </el-button>
              <el-button type="info" size="small" @click="resetRsaKeyPair">{{ langData.dialog6BtnReset }}</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
    <el-row>
    </el-row>
    <el-row>
      <el-col :span="12" style="margin-top: 5px">
        <el-card style="width: 90%;">
          <template #header>
            <div class="card-header">
              <span>{{ langData.dialog7Title }}</span>
            </div>
          </template>
          <el-form :model="form6" size="small" label-position="right" inline-message :inline="false" label-width="100px"
                   ref="formRef6" :rules="rules6">
            <el-form-item :label="langData.dialog7PublicKey" prop="publicKey">
              <el-input v-model="form6.publicKey" type="textarea" rows="9" style="width: 800px"/>
            </el-form-item>
            <el-form-item :label="langData.dialog7EncryptContent" prop="encodeData">
              <el-input v-model="form6.encodeData" type="textarea" rows="5" style="width: 800px"/>
            </el-form-item>
            <el-form-item :label="langData.dialog7EncryptResult" prop="encodeResult">
              <el-input v-model="form6.encodeResult" type="textarea" rows="5" style="width: 800px" readonly/>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="small" @click="rsaEncode(formRef6)">{{
                  langData.dialog7BtnSubmit
                }}
              </el-button>
              <el-button type="info" size="small" @click="resetRsaEncode">{{ langData.dialog7BtnReset }}</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12" style="margin-top: 5px">
        <el-card style="width: 90%;">
          <template #header>
            <div class="card-header">
              <span>{{ langData.dialog8Title }}</span>
            </div>
          </template>
          <el-form :model="form7" size="small" label-position="right" inline-message :inline="false" label-width="100px"
                   ref="formRef7" :rules="rules7">
            <el-form-item :label="langData.dialog8PrivateKey" prop="privateKey">
              <el-input v-model="form7.privateKey" type="textarea" rows="9" style="width: 100%"/>
            </el-form-item>
            <el-form-item :label="langData.dialog8EncryptContent" prop="decodeData">
              <el-input v-model="form7.decodeData" type="textarea" rows="5" style="width: 100%"/>
            </el-form-item>
            <el-form-item :label="langData.dialog8EncryptResult" prop="decodeResult">
              <el-input v-model="form7.decodeResult" type="textarea" rows="5" style="width: 100%" readonly/>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="small" @click="rsaDecode(formRef7)">{{ langData.dialog8BtnSubmit }}
              </el-button>
              <el-button type="info" size="small" @click="resetRsaDecode">{{ langData.dialog8BtnReset }}</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24" style="margin-top: 5px">
        <el-card style="width: 95%;">
          <template #header>
            <div class="card-header">
              <span>{{ langData.dialog9Title }}</span>
            </div>
          </template>
          <el-form :model="form8" size="small" label-position="right" inline-message :inline="false" label-width="100px"
                   ref="formRef8" :rules="rules8">
            <el-form-item :label="langData.dialog7PublicKey" prop="publicKey">
              <el-input v-model="form8.publicKey" type="textarea" :rows="3" style="width: 100%"/>
            </el-form-item>
            <el-form-item :label="langData.dialog4Secret" prop="key">
              <el-input v-model="form8.key" type="text">
                <template #suffix>
                  <svg style="cursor: pointer;" @click="aesRandomKey3" t="1757470138917"
                       class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6653"
                       width="14" height="14">
                    <path
                        d="M421.376 481.28s117.248 24.576 175.104-8.704c0 0-89.6 70.144-89.6 166.4 0.512-0.512-8.192-121.344-85.504-157.696zM438.784 969.216s68.608 6.656 68.608-80.896c0 0 3.072 88.576 65.024 78.336 0 0.512-50.688 22.016-133.632 2.56zM161.28 238.08s-30.208 65.536 11.264 91.648c0 0-67.072-17.408-81.408 37.376 0 0 8.704-82.944 70.144-129.024zM857.6 227.328s49.152 50.176 1.024 81.408c0 0 58.88-18.432 66.56 36.352 0 0 5.12-69.632-67.584-117.76z"
                        fill="#454859" p-id="6654"></path>
                    <path
                        d="M443.392 970.752c-5.632 0-10.752-1.024-15.36-3.072l-270.848-157.184-1.536-1.024s-1.024-1.024-4.608-2.56c-51.2-29.184-62.976-94.208-65.536-120.832V386.56c0-3.072 0.512-7.168 1.024-11.264l0.512-3.584 1.024-2.56c19.456-50.688 76.8-51.2 103.936-44.032l-1.536 5.632 4.096-6.144L476.16 486.4l18.944 37.888c20.992 36.864 29.184 77.824 32.768 99.84v258.048c-4.608 56.32-36.864 76.288-55.808 82.944-1.024 0.512-15.36 5.632-28.672 5.632z m-262.144-196.096l263.168 152.576c12.288-0.512 36.864-6.656 40.448-48.128v-250.368c-4.608-31.744-20.992-103.936-72.192-128L322.56 445.44l1.536 3.072-142.336-82.432c-2.048-0.512-40.448-9.216-52.736 15.872-0.512 2.56-0.512 4.608-0.512 6.144v294.4c1.536 16.896 9.728 67.072 43.52 86.528 3.584 2.048 6.656 4.096 9.216 5.632z"
                        fill="#454859" p-id="6655"></path>
                    <path
                        d="M837.632 212.992c6.656 4.096 12.8 7.168 18.432 10.752l1.536 1.024 1.536 1.536c5.12 4.096 10.752 9.216 16.384 15.36 6.144 11.776 5.632 33.28 4.608 49.152-1.024 12.288-6.656 30.208-26.624 44.544l-1.024 0.512-247.808 156.672c-26.624 14.336-62.976 18.432-96.256 18.432-40.96 0-77.824-6.656-89.088-8.704l-3.072-0.512-245.248-142.336c-39.424-29.696-28.16-85.504-15.36-113.664l2.56-6.144 263.68-166.912c29.184-14.336 104.448-43.008 173.056-1.024 3.584 2.56 58.368 34.304 119.296 69.632M431.616 460.8c40.448 7.168 114.176 13.824 152.576-6.144l244.736-155.136c7.168-5.632 8.192-10.24 8.704-12.8 1.024-11.264-9.728-26.624-15.36-32.768-55.808-32.256-243.712-141.312-250.368-145.408-49.664-30.72-107.008-9.216-130.048 2.56L192.512 268.8c-4.096 12.288-12.288 42.496 3.584 55.808l235.52 136.192z"
                        fill="#454859" p-id="6656"></path>
                    <path
                        d="M831.488 299.008c4.096-1.024 38.4-11.264 66.048 6.144 7.168 4.608 17.92 11.776 24.064 24.576 1.024 5.632 4.096 10.752 4.608 16.896v2.048l-1.024 323.072c-5.12 35.328-22.528 91.648-77.312 125.44l-5.12 3.584h-1.024l-262.144 165.888-4.608 0.512c-4.096 0.512-8.704 1.024-12.8 1.024-15.872 0-30.208-5.12-41.984-14.848-24.576-20.48-32.768-55.808-35.328-73.728l-1.024-252.928h1.536c6.144-96.768 88.576-164.864 96.768-171.008l-0.512-0.512L829.44 299.52m-301.056 567.808c0.512 10.24 5.12 41.472 19.968 53.76 3.072 2.56 7.68 5.632 16.384 5.12l264.704-167.936c56.32-38.4 53.76-115.712 53.76-116.224l-0.512-32.256 1.024-250.368h-0.512c-1.536-12.8-7.168-16.384-8.704-17.408-8.704-5.632-23.552-3.072-28.672-2.048l-235.52 148.992c-1.024 0.512-80.896 65.024-80.896 149.504h-1.536l0.512 228.864zM435.2 264.192c0 27.648 31.744 50.176 71.168 50.176s71.168-22.528 71.168-50.176-31.744-50.176-71.168-50.176S435.2 236.544 435.2 264.192z"
                        fill="#454859" p-id="6657"></path>
                    <path
                        d="M663.552 782.848c0 30.72-22.528 67.072-49.664 80.384-27.648 13.824-50.176-0.512-50.176-31.232s22.528-67.072 50.176-80.384c27.136-13.824 49.664 0 49.664 31.232zM760.32 602.624c0 30.72-22.528 67.072-49.664 80.384-27.648 13.824-49.664-0.512-49.664-31.232s22.528-67.072 49.664-80.384c27.136-13.824 49.664 0.512 49.664 31.232zM867.84 428.032c0 30.72-22.528 67.072-49.664 80.384-27.648 13.824-50.176-0.512-50.176-31.232s22.528-67.072 50.176-80.384c27.136-13.824 49.664 0 49.664 31.232zM270.848 538.112c0 30.72-22.016 41.984-48.64 24.576-27.136-16.896-48.64-55.808-48.64-86.528 0-30.72 22.016-41.984 48.64-24.576 26.624 16.896 48.64 55.808 48.64 86.528zM432.128 823.296c0 30.72-22.016 41.984-48.64 24.576-26.624-17.408-48.64-55.808-48.64-86.528 0-30.72 22.016-41.984 48.64-24.576 26.624 16.896 48.64 55.808 48.64 86.528z"
                        fill="#454859" p-id="6658"></path>
                  </svg>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item :label="langData.rsaEncryptKey" prop="keyRSA">
              <el-input v-model="form8.keyRSA" type="textarea" :rows="3" clearable/>
            </el-form-item>
            <el-form-item :label="langData.dialog4Iv" prop="iv">
              <el-input v-model="form8.iv" type="text">
                <template #suffix>
                  <svg style="cursor: pointer;" @click="aesRandomKey4" t="1757470138917"
                       class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6653"
                       width="14" height="14">
                    <path
                        d="M421.376 481.28s117.248 24.576 175.104-8.704c0 0-89.6 70.144-89.6 166.4 0.512-0.512-8.192-121.344-85.504-157.696zM438.784 969.216s68.608 6.656 68.608-80.896c0 0 3.072 88.576 65.024 78.336 0 0.512-50.688 22.016-133.632 2.56zM161.28 238.08s-30.208 65.536 11.264 91.648c0 0-67.072-17.408-81.408 37.376 0 0 8.704-82.944 70.144-129.024zM857.6 227.328s49.152 50.176 1.024 81.408c0 0 58.88-18.432 66.56 36.352 0 0 5.12-69.632-67.584-117.76z"
                        fill="#454859" p-id="6654"></path>
                    <path
                        d="M443.392 970.752c-5.632 0-10.752-1.024-15.36-3.072l-270.848-157.184-1.536-1.024s-1.024-1.024-4.608-2.56c-51.2-29.184-62.976-94.208-65.536-120.832V386.56c0-3.072 0.512-7.168 1.024-11.264l0.512-3.584 1.024-2.56c19.456-50.688 76.8-51.2 103.936-44.032l-1.536 5.632 4.096-6.144L476.16 486.4l18.944 37.888c20.992 36.864 29.184 77.824 32.768 99.84v258.048c-4.608 56.32-36.864 76.288-55.808 82.944-1.024 0.512-15.36 5.632-28.672 5.632z m-262.144-196.096l263.168 152.576c12.288-0.512 36.864-6.656 40.448-48.128v-250.368c-4.608-31.744-20.992-103.936-72.192-128L322.56 445.44l1.536 3.072-142.336-82.432c-2.048-0.512-40.448-9.216-52.736 15.872-0.512 2.56-0.512 4.608-0.512 6.144v294.4c1.536 16.896 9.728 67.072 43.52 86.528 3.584 2.048 6.656 4.096 9.216 5.632z"
                        fill="#454859" p-id="6655"></path>
                    <path
                        d="M837.632 212.992c6.656 4.096 12.8 7.168 18.432 10.752l1.536 1.024 1.536 1.536c5.12 4.096 10.752 9.216 16.384 15.36 6.144 11.776 5.632 33.28 4.608 49.152-1.024 12.288-6.656 30.208-26.624 44.544l-1.024 0.512-247.808 156.672c-26.624 14.336-62.976 18.432-96.256 18.432-40.96 0-77.824-6.656-89.088-8.704l-3.072-0.512-245.248-142.336c-39.424-29.696-28.16-85.504-15.36-113.664l2.56-6.144 263.68-166.912c29.184-14.336 104.448-43.008 173.056-1.024 3.584 2.56 58.368 34.304 119.296 69.632M431.616 460.8c40.448 7.168 114.176 13.824 152.576-6.144l244.736-155.136c7.168-5.632 8.192-10.24 8.704-12.8 1.024-11.264-9.728-26.624-15.36-32.768-55.808-32.256-243.712-141.312-250.368-145.408-49.664-30.72-107.008-9.216-130.048 2.56L192.512 268.8c-4.096 12.288-12.288 42.496 3.584 55.808l235.52 136.192z"
                        fill="#454859" p-id="6656"></path>
                    <path
                        d="M831.488 299.008c4.096-1.024 38.4-11.264 66.048 6.144 7.168 4.608 17.92 11.776 24.064 24.576 1.024 5.632 4.096 10.752 4.608 16.896v2.048l-1.024 323.072c-5.12 35.328-22.528 91.648-77.312 125.44l-5.12 3.584h-1.024l-262.144 165.888-4.608 0.512c-4.096 0.512-8.704 1.024-12.8 1.024-15.872 0-30.208-5.12-41.984-14.848-24.576-20.48-32.768-55.808-35.328-73.728l-1.024-252.928h1.536c6.144-96.768 88.576-164.864 96.768-171.008l-0.512-0.512L829.44 299.52m-301.056 567.808c0.512 10.24 5.12 41.472 19.968 53.76 3.072 2.56 7.68 5.632 16.384 5.12l264.704-167.936c56.32-38.4 53.76-115.712 53.76-116.224l-0.512-32.256 1.024-250.368h-0.512c-1.536-12.8-7.168-16.384-8.704-17.408-8.704-5.632-23.552-3.072-28.672-2.048l-235.52 148.992c-1.024 0.512-80.896 65.024-80.896 149.504h-1.536l0.512 228.864zM435.2 264.192c0 27.648 31.744 50.176 71.168 50.176s71.168-22.528 71.168-50.176-31.744-50.176-71.168-50.176S435.2 236.544 435.2 264.192z"
                        fill="#454859" p-id="6657"></path>
                    <path
                        d="M663.552 782.848c0 30.72-22.528 67.072-49.664 80.384-27.648 13.824-50.176-0.512-50.176-31.232s22.528-67.072 50.176-80.384c27.136-13.824 49.664 0 49.664 31.232zM760.32 602.624c0 30.72-22.528 67.072-49.664 80.384-27.648 13.824-49.664-0.512-49.664-31.232s22.528-67.072 49.664-80.384c27.136-13.824 49.664 0.512 49.664 31.232zM867.84 428.032c0 30.72-22.528 67.072-49.664 80.384-27.648 13.824-50.176-0.512-50.176-31.232s22.528-67.072 50.176-80.384c27.136-13.824 49.664 0 49.664 31.232zM270.848 538.112c0 30.72-22.016 41.984-48.64 24.576-27.136-16.896-48.64-55.808-48.64-86.528 0-30.72 22.016-41.984 48.64-24.576 26.624 16.896 48.64 55.808 48.64 86.528zM432.128 823.296c0 30.72-22.016 41.984-48.64 24.576-26.624-17.408-48.64-55.808-48.64-86.528 0-30.72 22.016-41.984 48.64-24.576 26.624 16.896 48.64 55.808 48.64 86.528z"
                        fill="#454859" p-id="6658"></path>
                  </svg>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item :label="langData.rsaEncryptIv" prop="ivRSA">
              <el-input v-model="form8.ivRSA" type="textarea" :rows="3" clearable/>
            </el-form-item>
            <el-form-item :label="langData.body" prop="body">
              <el-input v-model="form8.body" type="textarea" :rows="3"
                        style="width: 100%"/>
            </el-form-item>
            <el-form-item :label="langData.bodyExample" prop="result">
              <el-input v-model="form8.result" type="textarea" :rows="5"
                        style="width: 49%"/>
              <el-input v-model="form8.base64Result" type="textarea" :rows="5"
                        style="width: 49%;margin-left: 2%"/>
            </el-form-item>
            <el-form-item label-width="0%">
              <el-button type="primary" size="small" @click="form8Encrypt(formRef8)">{{ langData.aesEncrypt }}
              </el-button>
              <el-button type="success" size="small" @click="form8Dencrypt(formRef8)">{{ langData.aesDecrypt }}
              </el-button>
              <el-button type="info" size="small" @click="form8Reset">{{ langData.dialog1BtnReset }}</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.container {
  flex-grow: 1;
  padding: 20px 2%;
  overflow: auto;
  width: 96%;
}
</style>