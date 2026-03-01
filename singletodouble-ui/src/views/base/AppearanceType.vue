<template>
    <div class="app-container">
      <el-row :gutter="10" class="mb8">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" >      
          <el-form-item label="检索内容：" prop="searchValue">
            <el-input
              v-model="queryParams.searchValue"
              placeholder="项目名"
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
        style="width: 100%;"
        v-loading="loading"
        :data="dataList.slice((pageNum-1)*pageSize,pageNum*pageSize)"
        row-key="deptId"
      > 
        <el-table-column prop="typeName" label="检查项目名" header-align="center" align="left" width="300"></el-table-column>
        <el-table-column prop="status" label="检查选项值" header-align="center" align="left">
          <template slot-scope="scope">
            <div :title=" getPropText(scope.row.propList)" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">{{ getPropText(scope.row.propList) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="类型" align="center" key="inputType" width="100" >
            <template slot-scope="scope">
              {{scope.row.inputType == '1'?'属性值':scope.row.inputType == '2'?'范围值':'文字'}}
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

      <add-property-modal ref="addPropertyModal" @submit="onSubmit"></add-property-modal>
    </div>
  </template>
  
  <script>
  import { listAppearanceType, updateAppearanceTypeStatus, delAppearanceType } from "@/api/system/base";
  import AddPropertyModal from '../../components/AddPropertyModal.vue';
  
  export default {
    name: "Dept",
    dicts: ['sys_normal_disable'],
    components: {
      AddPropertyModal
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
        // 数据
        dataList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 重新渲染表格状态
        refreshTable: true,
        // 表单参数
        form: {},
        // 表单校验
        rules: {
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
        listAppearanceType(this.queryParams).then(response => {
          this.dataList = response.rows;
          this.total = response.total;          
          this.loading = false;
        });
      },
      // 状态修改
      handleStatusChange(row) {
        let self = this;
        let text = row.useStatus === 2 ? "启用" : "停用";
        this.$modal.confirm('确认要"' + text + '"检查项目"' + row.typeName + '"吗？').then(function() {            
          var commitData = {
            id:row.id,
            useStatus: row.useStatus == 1 ? 2 : 1
          }
          return updateAppearanceTypeStatus(commitData);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess(text + "成功");
        }).catch(() => {});     
      },
      showAddModal() {
        this.$refs.addPropertyModal.show(null);
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.$refs.addPropertyModal.show(row);
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        this.$modal.confirm('是否确认删除名称为"' + row.typeName + '"的数据项？').then(function() {
          return delAppearanceType(row.id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
      },
      onSubmit() {
        this.getList()            
      },
      getPropText(arr){
          let ret = ''
          if (arr!=null){
              arr.forEach(function(item){
                ret += item.propName + '\n\r'
              })
          }
          return ret
      },
    }
  };
  </script>
  