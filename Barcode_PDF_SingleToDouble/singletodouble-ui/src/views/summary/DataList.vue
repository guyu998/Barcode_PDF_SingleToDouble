<template>
  <div class="app-container"> 
    <el-row :gutter="10" class="mb8">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" >      
        <el-form-item label="流水线：" prop="lineName">
          <el-input v-model="queryParams.lineName" style="width:100px" clearable @keyup.enter.native="handleQuery" disabled/>
        </el-form-item>
        <el-form-item label="背番：" prop="backNo">
          <el-input v-model="queryParams.backNo" style="width:100px" clearable @keyup.enter.native="handleQuery"/>
        </el-form-item>
        <el-form-item label="TRCW号：" prop="trcwNo">
          <el-input v-model="queryParams.trcwNo" style="width:200px" clearable @keyup.enter.native="handleQuery"/>
        </el-form-item>
        <el-form-item label="布标号：" prop="labelNo">
          <el-input v-model="queryParams.labelNo" style="width:200px" clearable @keyup.enter.native="handleQuery"/>
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
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>        
    </el-row>

    <el-table
      style="width:100%;"
      v-loading="loading"
      :data="dataList.slice((pageNum-1)*pageSize,pageNum*pageSize)"
      row-key="id"
    >    
      <el-table-column prop="backNo" label="背番" header-align="center" align="center"></el-table-column>
      <el-table-column prop="trcwNo" label="TRCW号" header-align="center" align="center"  width="300"></el-table-column>
      <el-table-column prop="labelNo" label="布标号" header-align="center" align="center" width="200"></el-table-column>
      <el-table-column prop="labelStartId" label="布标ID起始位" header-align="center" align="center" width="150"></el-table-column>
      <el-table-column prop="labelEndId" label="布标ID终止位" header-align="center" align="center" width="150"></el-table-column>
      <el-table-column prop="modelNo" label="M.NO" header-align="center" align="center" width="200"></el-table-column>
      <el-table-column label="条码状态" header-align="center" align="center" width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.barCodeEnable == '1' ? '有' : '无'}}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="80">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.parentId != 0"
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:dept:remove']"
          >删除</el-button>
        </template>
      </el-table-column>      
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="pageNum" :limit.sync="pageSize" />
  </div>
</template>

<script>
import { listData, delData } from "@/api/system/base";

export default {
  name: "DataList",
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
        trcwNo:'',
        labelNo:''
      },
      dialogVisible: false,        
      // 表格树数据
      dataList: [],
      // 重新渲染表格状态
      refreshTable: true
    };
  },
  created() {
    this.queryParams.lineName = this.$route.params.lineName;
    this.handleQuery();
  },
  methods: {
    /** 搜索按钮操作 */
    handleQuery() {
      this.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },      
    /** 查询列表 */
    getList() {
      this.loading = true;
      listData(this.queryParams).then(response => {
        this.dataList = response.rows;
        this.total = response.total;          
        this.loading = false;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {

    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$modal.confirm('是否确认删除背番为"' + row.backNo + '"的数据项？').then(function() {
        return delData(row.lineName, row.backNo);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleDialogClose() {

    },   
  }
};
</script>
  