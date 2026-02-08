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
        <el-form-item label="工程：" prop="projectCode">
          <el-select v-model="queryParams.projectCode"">
            <el-option v-for="item in projectTypeList" :key="item.projectCode" :label="item.projectName" :value="item.projectCode"/>            
          </el-select>
        </el-form-item>
        <el-form-item label="返修日期">
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
      <el-table-column prop="lineName" label="流水线" header-align="center" align="center" width="160"></el-table-column>
      <el-table-column prop="backNo" label="背番" header-align="center" align="center" width="80"></el-table-column>
      <el-table-column prop="labelNo" label="布标号" header-align="center" align="center" width="140"></el-table-column>
      <el-table-column prop="inputId" label="布标ID/刻印号" header-align="center" align="center" width="140"></el-table-column>
      <el-table-column prop="newInputId" label="更换布标ID/刻印号" header-align="center" align="center" width="140"></el-table-column>
      <el-table-column label="状态" header-align="center" align="center" width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.repairStatus == '1' ? '更换' : '不更换'}}</span>
        </template>
      </el-table-column>
      <el-table-column label="工程" header-align="center" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.projectCode + "-" + scope.row.projectName}}</span>
        </template>
      </el-table-column>
      <el-table-column label="细节" header-align="center" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.reasonCode <= 0 ? '' : scope.row.reasonCode + "-" + scope.row.reasonName}}</span>
        </template>
      </el-table-column>
      <el-table-column prop="abnormalUserName" label="异常处置人员" header-align="center" align="center" width="120"></el-table-column>
      <el-table-column label="返修时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="pageNum" :limit.sync="pageSize" />
  </div>
</template>

<script>
import { listRepair } from "@/api/summary/index";
import { listPipeline, listProjectType, listRepairReason } from "@/api/system/base";

export default {
  name: "RepairList",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,      
      pageNum: 1,
      pageSize: 20,        
      // 总条数
      total: 0,        
      queryParams: {
        lineName:'',
        backNo:'',
        labelNo:'',
        projectCode:'',
        reasonCode:''
      },
      dateRange:[],
      dialogVisible: false,        
      // 表格树数据
      dataList: [],
      // 重新渲染表格状态
      refreshTable: true,
      lineList:[],
      projectTypeList:[],
      repairReasonList:[]
    };
  },
  created() {
    let endDate = this.getToday();
    this.dateRange.push(endDate, endDate);
    this.getLineList();
    this.getProjectTypeList();
    this.getRepairReasonList();

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
    getProjectTypeList() {
      listProjectType({}).then(response => {
        this.projectTypeList.push({
          projectCode:"",
          projectName:"所有"
        });
        response.rows.forEach((item, index) => {
          this.projectTypeList.push({
            projectCode:item.projectCode,
            projectName:item.projectCode + "." + item.projectName
          });
        })
      });
    },
    getRepairReasonList() {
      listRepairReason({}).then(response => {
        this.repairReasonList.push({
          reasonCode:"",
          reasonName:"所有"
        });
        response.rows.forEach((item, index) => {
          this.repairReasonList.push({
            reasonCode:item.reasonCode,
            reasonName:item.reasonCode + "-" + item.reasonName
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
      listRepair(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.dataList = response.rows;
        this.total = response.total;          
        this.loading = false;
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('/summary/repair/export', {
        ...this.queryParams
      }, `返修历史(${this.dateRange[0]}~${this.dateRange[1]}).xlsx`)
    }    
  }
};
</script>
  