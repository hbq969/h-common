import request from '@/network'
import * as echarts from 'echarts'
import {msg} from "@/utils/Utils";

export class Instance {
  appName: string;
  dataCenter: string;
  ip: string;
  port: number;
  myChart: any = null;

  constructor(appName: string, dataCenter: string, ip: string, port: number) {
    this.appName = appName;
    this.dataCenter = dataCenter;
    this.ip = ip;
    this.port = port;
  }

  getKey = (): string => {
    return this.ip + ':' + this.port;
  }

  queryData = (trendForm: any) => {
    let data = {
      appName: trendForm.appName,
      quotaName: trendForm.quotaName,
      dataCenter: this.dataCenter,
      ip: this.ip,
      port: this.port,
      timeType: trendForm.timeType,
      time: trendForm.time
    }
    request({
      url: '/hmis/monitor/qd/queryQuotaDatas/v1.0',
      method: 'post',
      data: data
    }).then((res: any) => {
      if (res.data.code == 1) {
        const qdl = res.data.body;
        let xAxisData: any[] = []
        let seriesData: any[] = []
        qdl.forEach((item: any) => {
          xAxisData.push(item.fmtCollectTime)
          seriesData.push(item.dataValue)
        })
        if (this.myChart == null) {
          this.myChart = echarts.init(document.getElementById(this.getKey()) as HTMLElement)
        }
        this.myChart.setOption({
          legend: {
            type: 'plain',
          },
          tooltip: {
            trigger: 'axis',
            formatter: '时间: {b}<br/>值: {c}'
          },
          xAxis: {
            data: xAxisData
          },
          yAxis: {},
          series: [
            {
              name: this.ip + ':' + this.port,
              data: seriesData,
              type: 'line',
              symbolSize: 0,
              label: {
                show: false
              },
              areaStyle: {
                color: '#B1FF54A8',
                opacity: 0.3
              },
              lineStyle: {
                color: '#B1FF54A8',
                width: 1
              }
            }
          ]
        })
      }
    }).catch((error: any) => {
      msg('请求异常', 'error')
    })
  }

  resize = () => {
    this.myChart.resize()
  }
}
