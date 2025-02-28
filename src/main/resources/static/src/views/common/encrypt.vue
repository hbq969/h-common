<script lang="ts" setup>
import {
  Edit
} from '@element-plus/icons-vue'
import {ref, reactive, onMounted, computed, provide, inject} from 'vue'
import axios from '@/network'
import {msg} from '@/utils/Utils'
import type {FormInstance, FormRules} from 'element-plus'

const form = reactive({
  keySource: 'default',
  encodeData: '',
  encodeResult: '',
  customKey: ''
})
const formRef1 = ref<FormInstance>();
const rules1 = reactive<FormRules>({
  encodeData: [{required: true, message: '不能为空', trigger: 'blur'}],
  customKey: [{required: true, message: '不能为空', trigger: 'blur'}]
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
          let content = '调用 '+res.config.baseURL+res.config.url+': '+res.data.errorMessage;
          msg(content, "warning")
        }
      }).catch((err: Error) => {
        console.log('',err)
        msg('请求异常', 'error')
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
  decodeData: [{required: true, message: '不能为空', trigger: 'blur'}],
  customKey: [{required: true, message: '不能为空', trigger: 'blur'}]
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
          let content = '调用 '+res.config.baseURL+res.config.url+': '+res.data.errorMessage;
          msg(content, "warning")
        }
      }).catch((err: Error) => {
        console.log('',err)
        msg('请求异常', 'error')
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
      let content = '调用 '+res.config.baseURL+res.config.url+': '+res.data.errorMessage;
      msg(content, "warning")
    }
  }).catch((err: Error) => {
    console.log('',err)
    msg('请求异常', 'error')
  })
}

const resetAESKey = () => {
  aesRandomKey.value = ''
}


const form3 = reactive({
  encryptKey: '',
  encodeData: '',
  encodeResult: ''
})
const formRef3 = ref<FormInstance>();
const rules3 = reactive<FormRules>({
  encryptKey: [{required: true, message: '不能为空', trigger: 'blur'}],
  encodeData: [{required: true, message: '不能为空', trigger: 'blur'}]
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
          content: form3.encodeData
        }
      }).then((res: any) => {
        if (res.data.state == 'OK') {
          form3.encodeResult = res.data.body
        } else {
          let content = '调用 '+res.config.baseURL+res.config.url+': '+res.data.errorMessage;
          msg(content, "warning")
        }
      }).catch((err: Error) => {
        console.log('',err)
        msg('请求异常', 'error')
      })
    }
  })
}

const resetAESEncode = () => {
  form3.encryptKey = ''
  form3.encodeData = ''
  form3.encodeResult = ''
}

const form4 = reactive({
  decryptKey: '',
  decodeData: '',
  decodeResult: ''
})
const formRef4 = ref<FormInstance>();
const rules4 = reactive<FormRules>({
  decryptKey: [{required: true, message: '不能为空', trigger: 'blur'}],
  decodeData: [{required: true, message: '不能为空', trigger: 'blur'}]
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
          content: form4.decodeData
        }
      }).then((res: any) => {
        if (res.data.state == 'OK') {
          form4.decodeResult = res.data.body
        } else {
          let content = '调用 '+res.config.baseURL+res.config.url+': '+res.data.errorMessage;
          msg(content, "warning")
        }
      }).catch((err: Error) => {
        console.log('',err)
        msg('请求异常', 'error')
      })
    }
  })
}

const resetAESDecode = () => {
  form4.decryptKey = ''
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
      let content = '调用 '+res.config.baseURL+res.config.url+': '+res.data.errorMessage;
      msg(content, "warning")
    }
  }).catch((err: Error) => {
    console.log('',err)
    msg('请求异常', 'error')
  })
}

const resetRsaKeyPair = () => {
  form5.privateKey = ''
  form5.publicKey = ''
}

const form6 = reactive({
  publicKey: '',
  encodeData: '',
  encodeResult: ''
})
const formRef6 = ref<FormInstance>()
const rules6 = reactive<FormRules>({
  publicKey: [{required: true, message: '不能为空', trigger: 'blur'}],
  encodeData: [{required: true, message: '不能为空', trigger: 'blur'}]
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
          let content = '调用 '+res.config.baseURL+res.config.url+': '+res.data.errorMessage;
          msg(content, "warning")
        }
      }).catch((err: Error) => {
        console.log('',err)
        msg('请求异常', 'error')
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
  privateKey: [{required: true, message: '不能为空', trigger: 'blur'}],
  decodeData: [{required: true, message: '不能为空', trigger: 'blur'}]
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
          let content = '调用 '+res.config.baseURL+res.config.url+': '+res.data.errorMessage;
          msg(content, "warning")
        }
      }).catch((err: Error) => {
        console.log('',err)
        msg('请求异常', 'error')
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

</script>

<template>
  <div class="container">
    <el-space wrap>
      <el-card style="max-width: 400px">
        <template #header>
          <div class="card-header">
            <span>Spring配置加密</span>
          </div>
        </template>
        <el-form :model="form" size="small" label-position="right" inline-message inline label-width="100px"
                 ref="formRef1" :rules="rules1">
          <el-form-item label="秘钥来源" prop="keySource">
            <el-radio-group v-model="form.keySource" size="small">
              <el-radio-button label="default" value="缺省默认"/>
              <el-radio-button label="custom" value="自定义"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="自定义秘钥：" prop="customKey" v-if="form.keySource=='custom'">
            <el-input v-model="form.customKey" style="width: 220px"/>
          </el-form-item>
          <el-form-item label="加密内容：" prop="encodeData">
            <el-input v-model="form.encodeData" type="textarea" rows="5" style="width: 220px"/>
          </el-form-item>
          <el-form-item label="加密结果：" prop="encodeResult">
            <el-input v-model="form.encodeResult" type="textarea" rows="5" style="width: 220px" readonly/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="springConfigEncode(formRef1)">提交</el-button>
            <el-button type="info" size="small" @click="resetSpringConfigEncode">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
      <el-card style="max-width: 400px;">
        <template #header>
          <div class="card-header">
            <span>Spring配置解密</span>
          </div>
        </template>
        <el-form :model="form2" size="small" label-position="right" inline-message inline label-width="100px"
                 ref="formRef2" :rules="rules2">
          <el-form-item label="秘钥来源" prop="keySource">
            <el-radio-group v-model="form2.keySource" size="small">
              <el-radio-button label="default" value="缺省默认"/>
              <el-radio-button label="custom" value="自定义"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="自定义秘钥：" prop="customKey" v-if="form2.keySource=='custom'">
            <el-input v-model="form2.customKey" style="width: 220px"/>
          </el-form-item>
          <el-form-item label="解密内容：" prop="encodeData">
            <el-input v-model="form2.decodeData" type="textarea" rows="5" style="width: 220px"/>
          </el-form-item>
          <el-form-item label="解密结果：" prop="encodeResult">
            <el-input v-model="form2.decodeResult" type="textarea" rows="5" style="width: 220px" readonly/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="springConfigEncode2(formRef2)">提交</el-button>
            <el-button type="info" size="small" @click="resetSpringConfigEncode2">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card style="max-width: 400px;">
        <template #header>
          <div class="card-header">
            <span>AES秘钥随机生成</span>
          </div>
        </template>
        <el-form size="small" label-position="right" inline-message inline label-width="100px">
          <el-form-item label="自定义秘钥：" prop="aesRandomKey">
            <el-input v-model="aesRandomKey" type="textarea" rows="5" style="width: 220px"/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="aesRandomKeyCreate()">生成</el-button>
            <el-button type="info" size="small" @click="resetAESKey">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card style="max-width: 400px;">
        <template #header>
          <div class="card-header">
            <span>AES加密</span>
          </div>
        </template>
        <el-form :model="form3" size="small" label-position="right" inline-message inline label-width="100px"
                 ref="formRef3" :rules="rules3">
          <el-form-item label="秘钥：" prop="encryptKey">
            <el-input v-model="form3.encryptKey" type="text" style="width: 220px"/>
          </el-form-item>
          <el-form-item label="加密内容：" prop="encodeData">
            <el-input v-model="form3.encodeData" type="textarea" rows="5" style="width: 220px"/>
          </el-form-item>
          <el-form-item label="加密结果：" prop="encodeResult">
            <el-input v-model="form3.encodeResult" type="textarea" rows="5" style="width: 220px" readonly/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="aesEncode(formRef3)">提交</el-button>
            <el-button type="info" size="small" @click="resetAESEncode">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card style="max-width: 400px;">
        <template #header>
          <div class="card-header">
            <span>AES解密</span>
          </div>
        </template>
        <el-form :model="form4" size="small" label-position="right" inline-message inline label-width="100px"
                 ref="formRef4" :rules="rules4">
          <el-form-item label="秘钥：" prop="decryptKey">
            <el-input v-model="form4.decryptKey" type="text" style="width: 220px"/>
          </el-form-item>
          <el-form-item label="加密内容：" prop="encodeData">
            <el-input v-model="form4.decodeData" type="textarea" rows="5" style="width: 220px"/>
          </el-form-item>
          <el-form-item label="加密结果：" prop="encodeResult">
            <el-input v-model="form4.decodeResult" type="textarea" rows="5" style="width: 220px" readonly/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="aesDecode(formRef4)">提交</el-button>
            <el-button type="info" size="small" @click="resetAESDecode">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>


      <el-card style="max-width: 1000px;">
        <template #header>
          <div class="card-header">
            <span>RSA秘钥对生成</span>
          </div>
        </template>
        <el-form size="small" label-position="right" inline-message inline label-width="100px">
          <el-form-item label="私钥：" prop="privateKey">
            <el-input v-model="form5.privateKey" type="textarea" rows="9" style="width: 800px" readonly/>
          </el-form-item>
          <el-form-item label="公钥：" prop="publicKey">
            <el-input v-model="form5.publicKey" type="textarea" rows="5" style="width: 800px" readonly/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="rsaKeyPairCreate()">生成</el-button>
            <el-button type="info" size="small" @click="resetRsaKeyPair">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card style="max-width: 1000px;">
        <template #header>
          <div class="card-header">
            <span>RSA公钥加密</span>
          </div>
        </template>
        <el-form :model="form6" size="small" label-position="right" inline-message inline label-width="100px"
                 ref="formRef6" :rules="rules6">
          <el-form-item label="公钥：" prop="publicKey">
            <el-input v-model="form6.publicKey" type="textarea" rows="9" style="width: 800px"/>
          </el-form-item>
          <el-form-item label="加密内容：" prop="encodeData">
            <el-input v-model="form6.encodeData" type="textarea" rows="5" style="width: 800px"/>
          </el-form-item>
          <el-form-item label="加密结果：" prop="encodeResult">
            <el-input v-model="form6.encodeResult" type="textarea" rows="5" style="width: 800px" readonly/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="rsaEncode(formRef6)">提交</el-button>
            <el-button type="info" size="small" @click="resetRsaEncode">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card style="max-width: 1000px;">
        <template #header>
          <div class="card-header">
            <span>RSA私钥解密</span>
          </div>
        </template>
        <el-form :model="form7" size="small" label-position="right" inline-message inline label-width="100px"
                 ref="formRef7" :rules="rules7">
          <el-form-item label="私钥：" prop="privateKey">
            <el-input v-model="form7.privateKey" type="textarea" rows="9" style="width: 800px"/>
          </el-form-item>
          <el-form-item label="解密内容：" prop="decodeData">
            <el-input v-model="form7.decodeData" type="textarea" rows="5" style="width: 800px"/>
          </el-form-item>
          <el-form-item label="加密结果：" prop="decodeResult">
            <el-input v-model="form7.decodeResult" type="textarea" rows="5" style="width: 800px" readonly/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="rsaDecode(formRef7)">提交</el-button>
            <el-button type="info" size="small" @click="resetRsaDecode">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-space>
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