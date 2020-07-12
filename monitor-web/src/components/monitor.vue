<template>
<div>
<el-row>
        <el-col :span="24" style="align-items: center">
            局域网机器监控工具V1-20200717
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
    <el-row STYLE="margin-top:20px">
        <el-col :span="6" v-for="macinfo in macInfoList" :key="macinfo.ip">
        <el-card class="box-card" style="margin: 10px">
            <div slot="header" class="clearfix">
                <span style="font-weight: bold">{{macinfo.ip}}</span>
<!--                <el-button style="float: right; padding: 3px 0" type="text">查看详情</el-button>-->
            </div>
             <div slot="header" class="clearfix">
                <el-row>
                    <el-col :span="12">
                      <span>内存: {{macinfo.memory.total}}</span><br>
                      <span>可用: {{macinfo.memory.available}}</span>
                    </el-col>
                    <el-col :span="12">
                      <span>CPU: {{macinfo.cpu.total}}</span><br>
                      <span>可用: {{macinfo.cpu.available}}</span>
                    </el-col>
                </el-row>
               <el-row>
                 <el-col :span="12">
                   <el-progress type="dashboard" :percentage="macinfo.memory.used" :color="macinfo.memory.used >= 80 ? red : blue" :width='60' :height='60'></el-progress>
                 </el-col>
                 <el-col :span="12">
                   <el-progress type="dashboard" :percentage="macinfo.cpu.used" :color="macinfo.cpu.used >= 80 ? red : blue" :width='60' :height='60'></el-progress>
                 </el-col>
               </el-row>
            </div>
            <div v-for="(disk,index) in macinfo.disk" :key="disk.diskName" class="text item">
                {{'盘符: ' + disk.diskName }} {{' 全部:'+disk.total}} {{' 剩余:'+disk.free}}
                <el-progress :text-inside="true" :color="disk.rate > 90 ? red : blue" :stroke-width="26" :percentage='(disk.rate)'></el-progress>
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
      red:'red',
      blue:'blue',
      onlinecount: 1,
      macInfoList :[
        {ip:'192.168.10.19','cpu':{'total':0,'available':'22','used':'11'},memory:{'total':'8 GiB','available':'1.5 GiB','used':80.89},disk:[{diskName:'A',total:100,free:10,rate:10},{diskName:'A',total:100,free:10,rate:10},{diskName:'A',total:100,free:10,rate:10}]},
      ]
    }
  },
  methods : {
    connection() {
      var that = this;
      console.log("您的浏览器支持WebSocket");
      //实现化WebSocket对象，指定要连接的服务器地址与端口  建立连接
      //等同于
      const index = new WebSocket("ws://localhost:8080/websocket/mac");
      //socket = new WebSocket("${basePath}websocket/${cid}".replace("http","ws"));
      //打开事件
      index.onopen = function() {
        console.log("Socket 已打开");
        //JSON序列化
        var msg = {param:'all'};
        index.send(JSON.stringify(msg));
      };
      //获得消息事件
      index.onmessage = function(msg) {
        console.log(msg.data);
        if (msg.data.length > 10){
          var mac = JSON.parse(msg.data);
          that.onlinecount = mac.onlinecount;
          that.macInfoList = mac.macInfoList;
        }
        //发现消息进入    开始处理前端触发逻辑
      };
      //关闭事件
      index.onclose = function() {
        console.log("Socket已关闭");
      };
      //发生了错误事件
      index.onerror = function() {
        alert("Socket发生了错误");
        //此时可以尝试刷新页面
      }
    },
  },
  mounted(){
    this.connection();
  },
}
</script>
