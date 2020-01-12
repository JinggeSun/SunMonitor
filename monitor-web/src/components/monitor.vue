<template>
<div>
<el-row>
        <el-col :span="24" style="align-items: center">
            局域网机器监控工具V12
        </el-col>
    </el-row>
    <el-row>
        <el-col :span="6">
            <el-card :body-style="{ padding: '10px' }">
                <div style="padding: 5px;">
                    <span>在线客户机数量</span>
                    <el-button style="float: right; padding: 3px 0" type="text">{{onlinecount}}</el-button>
                </div>
            </el-card>
        </el-col>
    </el-row>
    <el-row>
        <el-col :span="6" v-for="macinfo in macInfoList" :key="macinfo.ip">
        <el-card class="box-card">
            <div slot="header" class="clearfix">
                <span>IP地址: {{macinfo.ip}}</span>
                <el-button style="float: right; padding: 3px 0" type="text">查看详情</el-button>
            </div>
             <div slot="header" class="clearfix">
                <el-row>
                    <el-col :span="16">
                <span>内存: 8G</span>
                <span>已用: 6G</span>
                    </el-col>
                    <el-col :span="8">
                        <el-progress type="circle" :percentage="25" :width='60' :height='60'></el-progress>
                    </el-col>
                </el-row>
            
               
            </div>
            <div v-for="disk in macinfo.disk" :key="disk.diskName" class="text item">
                {{'盘符: ' + disk.diskName }} {{' 全部:'+disk.total}} {{' 剩余:'+disk.free}}
                <el-progress :text-inside="true" :stroke-width="26" :percentage='disk.rate'></el-progress>
            </div>
        </el-card>
        </el-col>
    </el-row>
</div>
</template>

<script>
export default {
  name: 'Monitor',
  data () {
    return {
      onlinecount: 10,
      macInfoList :[
        {ip:'192.168.10.19',disk:[{diskName:'A',total:100,free:10,rate:10}]},
        {ip:'192.168.10.19',disk:[{diskName:'A',total:100,free:10,rate:10}]}
      ]
    }
  }
}
</script>