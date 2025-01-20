const {defineConfig} = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  outputDir: 'hbq969-common',
  publicPath: './',
  devServer: {
    proxy: {
      '/dev': {
        target: `http://localhost:30100`,
        changeOrigin: true,
        secure: false,
        pathRewrite: { '^/dev': '' },
      },
    },
  }
})
