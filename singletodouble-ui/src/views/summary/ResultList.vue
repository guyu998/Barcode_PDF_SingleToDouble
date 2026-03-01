<template>
  <div class="app-container"> 
    <el-row :gutter="10" class="mb8">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" >      
        <el-form-item label="流水线：" prop="lineName">
          <el-select v-model="queryParams.lineName" style="width:200px">
            <el-option v-for="item in lineList" :key="item.lineName" :label="item.showName" :value="item.lineName"/>            
          </el-select>
        </el-form-item>
        <el-form-item label="背番：" prop="backNo">
          <el-input v-model="queryParams.backNo" style="width:100px" clearable @keyup.enter.native="handleQuery"/>
        </el-form-item>
        <el-form-item label="布标号：" prop="labelNo">
          <el-input v-model="queryParams.labelNo" style="width:150px" clearable @keyup.enter.native="handleQuery"/>
        </el-form-item>
        <el-form-item label="检查日期" prop="dateRange">
          <el-date-picker v-model="dateRange" :clearable="false" style="width: 240px" value-format="yyyy-MM-dd"
            type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-col :span="1.5">
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-col>
        </el-form-item>
      </el-form>            
    </el-row>

    <el-row :gutter="10" class="mb8" style="width:100%;">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>        
    </el-row>

    <el-table
      style="width:100%;"
      v-loading="loading"
      :data="dataList.slice((pageNum-1)*pageSize,pageNum*pageSize)"
      row-key="id"
    >    
      <el-table-column prop="lineName" label="流水线" header-align="center" align="center"></el-table-column>
      <el-table-column prop="backNo" label="背番" header-align="center" align="center" width="80"></el-table-column>
      <el-table-column prop="checkNo" label="检查序号" header-align="center" align="center" width="100"></el-table-column>
      <el-table-column prop="trcwNo" label="TRCW号" header-align="center" align="center" width="150"></el-table-column>
      <el-table-column prop="labelNo" label="布标号" header-align="center" align="center" width="150"></el-table-column>
      <el-table-column prop="labelCount" label="数量" header-align="center" align="center" width="100"></el-table-column>
      <el-table-column prop="checkDate" label="检查日" header-align="center" align="center" width="120"></el-table-column>      
      <el-table-column prop="labelStartTime" label="初检时间" header-align="center" align="center" width="100"></el-table-column>
      <el-table-column prop="labelStartId" label="初物ID/刻印号" header-align="center" align="center" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.labelStartId == '-1' ? '' : scope.row.labelStartId }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="labelEndTime" label="终检时间" header-align="center" align="center" width="100"></el-table-column>
      <el-table-column prop="labelEndId" label="终物ID/刻印号" header-align="center" align="center" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.labelEndId == '-1' ? '' : scope.row.labelEndId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="检查结果" header-align="center" align="center" width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.checkStatus == '1' ? 'OK' : 'NG' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="checkUserQr" label="确认者" header-align="center" align="center"></el-table-column>
      <el-table-column label="操作" align="center" width="120">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-document" @click="handleData(scope.row)">结果</el-button>        
        </template>        
      </el-table-column>      
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="pageNum" :limit.sync="pageSize" />

    <el-dialog :title="title" :visible.sync="dialogVisible" width="50%" @close="handleDialogClose">
      <el-table
        height="400"
        style="width:100%;"
        v-loading="loading2"
        :data="detailList"
        row-key="id">    
        <el-table-column prop="typeName" label="检查项目" header-align="center" align="left"></el-table-column>
        <el-table-column prop="startCheckStatus" label="初检结果" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.startCheckStatus == '1' ? 'OK' : scope.row.startCheckStatus == '2' ? 'NG' : '' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="endCheckStatus" label="终检结果" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.endCheckStatus == '1' ? 'OK' : scope.row.endCheckStatus == '2' ? 'NG' : '' }}</span>
          </template>
        </el-table-column>
      </el-table>

    </el-dialog>    
  </div>
</template>

<script>
import { listResult, listResultDetail } from "@/api/summary/index";
import { listPipeline } from "@/api/system/base";

export default {
  name: "ResultList",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      // 遮罩层
      loading: true,
      loading2: true,
      // 显示搜索条件
      showSearch: true,      
      pageNum: 1,
      pageSize: 20,        
      // 总条数
      total: 0,        
      queryParams: {
        lineName:'',
        backNo:'',
        trcwNo:'',
        labelNo:''
      },
      dateRange:[],
      dialogVisible: false,        
      // 表格树数据
      dataList: [],
      // 重新渲染表格状态
      refreshTable: true,
      lineList:[],
      title:'',
      queryDetailParams:{
        lineName:'',
	      backNo:'',
	      checkNo:''
      },
      detailList:[],
    };
  },
  created() {
    let endDate = this.getToday();
    this.dateRange.push(endDate, endDate);
    this.getLineList();

    this.handleQuery();
  },
  methods: {
    getToday() {
      //获取当前日期
      let myDate = new Date();
      let nowY = myDate.getFullYear();
      let nowM = myDate.getMonth() + 1;
      let nowD = myDate.getDate( );
      let endDate = nowY + '-' + (nowM < 10?"0"+ nowM : nowM) + '-' + (nowD< 10?"0"+ nowD : nowD); //当前日期
      return endDate;
    },
    getLineList() {
      listPipeline({}).then(response => {
        this.lineList.push({
          lineName:"",
          showName:"所有"
        });
        response.rows.forEach((item, index) => {
          this.lineList.push({
            lineName:item.lineName,
            showName:item.lineName
          });
        })
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      let endDate = this.getToday();
      this.dateRange.push(endDate, endDate);
      this.handleQuery();
    },      
    /** 查询列表 */
    getList() {
      this.loading = true;
      listResult(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.dataList = response.rows;
        this.total = response.total;          
        this.loading = false;
      });
    },
    handleData(row) {
      // 赋值
      this.queryDetailParams.lineName = row.lineName;
      this.queryDetailParams.backNo = row.backNo;
      this.queryDetailParams.checkNo = row.checkNo;
      this.dialogVisible = true;
      // 加载数据
      this.getDetailList();
    }, 
    getDetailList() {
      this.loading2 = true;
      listResultDetail(this.queryDetailParams).then(response => {
        this.detailList = response.rows;
        this.loading2 = false;
      });
    },
    handleDialogClose() {
      this.detailList = [];
      this.queryDetailParams = {
        lineName:'',
	      backNo:'',
	      checkNo:''
      };
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('/summary/result/export', {
        ...this.queryParams
      }, `检查历史(${this.dateRange[0]}~${this.dateRange[1]}).xlsx`)
    }
  }
};
</script>
  