<template>
    <div class="app-container"> 
      <el-row :gutter="10" class="mb8">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" >      
          <el-form-item label="检索内容：" prop="searchValue">
            <el-input
              v-model="queryParams.searchValue"
              placeholder="工程名"
              clearable
              @keyup.enter.native="handleQuery"
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

      <el-row :gutter="10" class="mb8" style="width:100%;">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="showAddModal">新增</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>        
      </el-row>
  
      <el-table
        v-if="refreshTable"
        style="width:100%;"
        v-loading="loading"
        :data="dataList.slice((pageNum-1)*pageSize,pageNum*pageSize)"
        row-key="projectCode"
      >    
        <el-table-column prop="projectCode" label="工程编号" header-align="center" align="center" width="200"></el-table-column>
        <el-table-column prop="projectName" label="工程名" header-align="center" align="left"></el-table-column>
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
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
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
      <pagination v-show="total>0" :total="total" :page.sync="pageNum" :limit.sync="pageSize" style="width:100%;"/>
      
      <add-project-type-modal ref="addProjectTypeModal" @submit="onSubmit"></add-project-type-modal>
    </div>
  </template>
  
  <script>
  import { listProjectType, updateProjectTypeStatus, delProjectType } from "@/api/system/base";
  import AddProjectTypeModal from '../../components/AddProjectTypeModal.vue';
  
  export default {
    name: "ProjectType",
    dicts: ['sys_normal_disable'],
    components: {
      AddProjectTypeModal
    },
    data() {
      return {
        // 遮罩层
        loading: true,
        // 显示搜索条件
        showSearch: true,      
        pageNum: 1,
        pageSize: 10,        
        // 总条数
        total: 0,        
        queryParams: {
          searchValue:''
        },
        dialogVisible: false,        
        dataList: [],              
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 重新渲染表格状态
        refreshTable: true,
        // 表单参数
        form: {},
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
      /** 查询列表 */
      getList() {
        this.loading = true;
        listProjectType(this.queryParams).then(response => {
          this.dataList = response.rows;
          this.total = response.total;          
          this.loading = false;
        });
      },
      // 状态修改
      handleStatusChange(row) {
        let self = this;
        let text = row.useStatus === 2 ? "启用" : "停用";
        this.$modal.confirm('确认要"' + text + '"工程"' + row.projectName + '"吗？').then(function() {            
          var commitData = {
            projectCode:row.projectCode,
            useStatus: row.useStatus == 1 ? 2 : 1
          }
          return updateProjectTypeStatus(commitData);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess(text + "成功");
        }).catch(() => {});           
      },
      showAddModal() {
        this.$refs.addProjectTypeModal.show(null);
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.$refs.addProjectTypeModal.show(row);
      },
      onSubmit() {
        this.getList()            
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        this.$modal.confirm('是否确认删除名称为"' + row.projectName + '"的数据项？').then(function() {
          return delProjectType(row.projectCode);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
      }
    }
  };
  </script>
  