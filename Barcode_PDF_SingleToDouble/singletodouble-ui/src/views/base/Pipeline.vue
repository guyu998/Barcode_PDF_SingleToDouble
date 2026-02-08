<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" >
        <el-form-item label="检索内容：" prop="searchValue">
          <el-input
            v-model="queryParams.searchValue"
            placeholder="流水线名/Ip地址"
            clearable
            @keyup.enter.native="getList"
          />
        </el-form-item>
        <el-form-item>
          <el-col :span="1.5">
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-col>
        </el-form-item>
      </el-form>            
    </el-row>

    <el-row :gutter="10" class="mb8" style="width: 100%;">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="showAddModal">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>    
    </el-row>

    <el-table  
      style="width: 100%;"
      v-loading="loading"
      :data="dataList.slice((pageNum-1)*pageSize,pageNum*pageSize)"
      row-key="backNo"
    >          
      <el-table-column prop="lineName" label="流水线名" header-align="center" align="left"></el-table-column>
      <el-table-column prop="ipAddress" label="IP地址" align="center" width="300"></el-table-column>
      <el-table-column prop="supportLabelCount" label="是否检查数量" align="center" width="100">
        <template #default="{ row }">
          <el-tag :type="row.supportLabelCount == 1 ? 'success' : 'danger'">
            {{ row.supportLabelCount == 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="useStatus" label="状态" align="center" width="100">
        <template #default="{ row }">
          <el-tag :type="row.useStatus == 1 ? 'success' : 'danger'">
            {{ row.useStatus == 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="200">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>        
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
            <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['system:dept:edit']"
                >修改</el-button>
                <el-button v-if="scope.row.useStatus == 1"
                size="mini"
                type="text"
                icon="el-icon-lock"
                @click="handleStatusChange(scope.row)"
                v-hasPermi="['system:dept:add']"
                >停用</el-button>
                <el-button v-if="scope.row.useStatus == 2"
                size="mini"
                type="text"
                icon="el-icon-unlock"
                @click="handleStatusChange(scope.row)"
                v-hasPermi="['system:dept:add']"
                >启用</el-button>
                <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['system:dept:remove']"
                >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="pageNum" :limit.sync="pageSize" style="width:100%;"/>

    <!-- 编辑/新增对话框 -->
    <el-dialog title="编辑/新增流水线" :visible.sync="dialogVisible" width="50%" @close="handleDialogClose">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="流水线名" prop="lineName">
          <el-input v-model="form.lineName" placeholder="请输入流水线名" :disabled="form.id != 0"/>
        </el-form-item>
        <el-form-item label="IP地址" prop="ipAddress">
          <ip-input v-model="form.ipAddress" placeholder="请输入IP地址" ></ip-input>
        </el-form-item>
        <el-form-item label="是否检查数量" prop="supportLabelCount">
          <el-radio-group v-model="form.supportLabelCount">
            <el-radio :label="1">是</el-radio>
            <el-radio :label="2">否</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">保 存</el-button>
      </span>
    </el-dialog>
    </div>
  </template>
  
  <script>
    import IpInput from '../../components/IpInput';
    import { listPipeline, addPipeline, updatePipeline, delPipeline,updatePipelineStatus } from '@/api/system/base';
  
  export default {
    components: {
      IpInput,
    },
    data() {
      return {
        loading: true,
        // 显示搜索条件
        showSearch: true,      
        pageNum: 1,
        pageSize: 10,        
        // 总条数
        total: 0,               
        queryParams: {
          searchValue: ''
        },        
        dataList: [],
        dialogVisible: false,
        form: {
          id: 0,
          lineName: '',
          ipAddress: '',
          useStatus: 1,
          supportLabelCount: 1,
        },
        rules: {
          lineName: [{ required: true, message: '请输入流水线名', trigger: 'blur' }],
          ipAddress: [{ required: true, message: '请输入正确的IP地址', trigger: 'blur', validator: this.validateIPAddress }]
        }
      };
    },
    created() {
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
      getList() {
        this.loading = true;
        listPipeline(this.queryParams).then(response => {
          this.dataList = response.rows;
          this.total = response.total;          
          this.loading = false;
        });      
      },
      // 状态修改
      handleStatusChange(row) {
        let self = this;
        let text = row.useStatus === 2 ? "启用" : "停用";
        this.$modal.confirm('确认要"' + text + '"流水线"' + row.lineName + '"吗？').then(function() {
            var commitData = {
              id:row.id,
              useStatus: row.useStatus == 1 ? 2 : 1
            }
            return updatePipelineStatus(commitData);
          }).then(() => {
            this.getList();
            this.$modal.msgSuccess(text + "成功");
          }).catch(() => {});               
      },
      showAddModal() {
        this.form = {
          id: 0,
          lineName: '',
          ipAddress: '',
          useStatus: 1
        };
        this.dialogVisible = true;
      },
      handleUpdate(row) {
        this.form = Object.assign({}, row);
        this.dialogVisible = true;
      },
      handleDelete(row) {
        this.$modal.confirm('是否确认删除名称为"' + row.lineName + '"的数据项？').then(function() {
          return delPipeline(row.id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});        
      },
      handleDialogClose() {
        this.form = {
          id: 0,
          lineName: '',
          ipAddress: '',
          useStatus: 1
        };
      },
      validateIPAddress(rule, value, callback) {
        // Regular expression to validate IPv4 address format
        const ipPattern = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
        if (!value) {
          return callback(new Error('请输入IP地址'));
        }
        if (!ipPattern.test(value)) {
          return callback(new Error('请输入正确的IP地址'));
        }
        callback();
      },
      submitForm() {
        this.$refs['form'].validate(valid => {
          if (valid) {
            if (this.form.id == 0) {
              addPipeline(this.form).then(() => {
                this.$modal.msgSuccess("新增成功");    
                this.getList();
                this.dialogVisible = false;
              }).catch(() => {});    
            } else {
              updatePipeline(this.form).then(() => {
                this.$modal.msgSuccess("更新成功");
                this.getList();
                this.dialogVisible = false;
              }).catch(() => {});              
            }
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      }
    }
  };
  </script>
  
  <style scoped>
  .el-card {
    margin-top: 20px;
  }
  </style>
  