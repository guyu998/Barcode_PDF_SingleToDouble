<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" >      
        <el-form-item label="检索内容：" prop="searchValue">
          <el-input
            v-model="queryParams.searchValue"
            placeholder="流水线名/IP地址"
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
      row-key="lineName"
    >       
      <el-table-column prop="lineName" header-align="center" align="left" label="流水线名" width="200"/>
      <el-table-column prop="ipAddress" header-align="center" align="center" label="流水线IP" width="200"/>
      <el-table-column prop="ptList" header-align="center" align="left" label="工程">
        <template slot-scope="scope">
          <ul>
            <li v-for="(relation, index) in scope.row.ptList" :key="index">
              {{ relation.projectName}}
            </li>
          </ul>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:dept:edit']"
          >修改</el-button>
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
    <el-dialog title="编辑/新增工程配置" :visible.sync="dialogVisible" width="50%" @close="handleDialogClose">
      <el-form ref="form" :model="form" :rules="rules" label-width="160px">
        <el-form-item label="流水线" prop="">
          <el-select v-model="form.lineId" placeholder="请选择流水线" :disabled="form.lineId != ''">
            <el-option v-for="item in lineList" :key="item.id" :label="item.lineName" :value="item.id"/>            
          </el-select>
        </el-form-item>
        <el-form-item label="工程" style="max-height: 500px; overflow-y: auto; overflow-x: hidden;">
          <el-checkbox-group v-model="form.ptList">
            <el-row :gutter="1" v-for="(prop, index) in projectTypeList" :key="index">
              <el-checkbox :label="prop.projectCode">{{ prop.projectName }}</el-checkbox>
            </el-row>
          </el-checkbox-group>
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
import { listPtRelation, listProjectType, addPtRelation ,updatePtRelation, delPtRelation, listPipeline} from '@/api/system/base';

export default {
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
      dataList:[],
      form: {
        lineId:'',
        ptList:[]
      },
      lineList:[],
      projectTypeList: [],
      rules: {
        lineId: [{ required: true, message: '请选择流水线', trigger: 'blur' }],        
      },
      tempRules: {},
      action:'add',
    };
  },
  created() {
    this.getProjectList();
    this.getLineList();
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
    getLineList() {
      listPipeline({useStatus:1}).then(response => {
        this.lineList = response.rows;
      });      
    },    
    getProjectList() {
      listProjectType({useStatus:1}).then(response => {
        this.projectTypeList = response.rows;        
      });
    },
    getList() {
      this.loading = true;      
      listPtRelation(this.queryParams).then(response => {
        this.dataList = response.rows;
        this.total = response.total;          
        this.loading = false;
      });
    },
    showAddModal() {
      this.handleUpdate(null);
    },
    handleUpdate(row) {
      if (row == null)
        this.action = 'add'
      else
        this.action = 'edit'
      this.dialogVisible = true;
      this.form.lineId = row?.lineId || '';
      this.form.ptList = (row?.ptList || []).map(checkItem => checkItem.projectCode);
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除流水线为"' + row.lineName + '"的数据项？').then(function() {
        return delPtRelation(row.lineId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleDialogClose() {
      this.form.lineId = '';
      this.form.ptList = [];
    },  
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          const formData = {
            lineId:this.form.lineId,
            ptList: this.form.ptList.map(propId => {
              return {
                projectCode: propId,
              };
            })
          }
          if (this.action == 'add'){
            addPtRelation(formData).then(response => {
              this.$modal.msgSuccess("新增成功");                  
              this.dialogVisible = false;
              this.getList()
            }).catch(() => {});
          } else {
            updatePtRelation(formData).then(response => {
              this.$modal.msgSuccess("更新成功");
              this.dialogVisible = false;
              this.getList()
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
