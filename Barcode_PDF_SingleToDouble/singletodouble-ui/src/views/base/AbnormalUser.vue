<template>
  <div class="app-container"> 
    <el-row :gutter="10" class="mb8">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" >      
        <el-form-item label="检索内容：" prop="searchValue">
          <el-input
            v-model="queryParams.searchValue"
            placeholder="二维码/姓名"
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
      <el-col :span="1.5">
          <el-upload
            ref="upload"
            class="upload-demo"
            action="#"
            :multiple='false'
            accept=".xls,.xlsx"
            :before-upload="beforeUpload"
            :show-file-list='false'
            :http-request="handleFileUpload"
          >
            <el-button type="primary" plain icon="el-icon-upload" size="mini" >批量上传</el-button>
          </el-upload>          
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>        
    </el-row>

    <el-table
      style="width:100%;"
      v-loading="loading"
      :data="dataList.slice((pageNum-1)*pageSize,pageNum*pageSize)"
      row-key="projectCode"
    >    
      <el-table-column prop="userQr" label="二维码" header-align="center" align="center" width="140"></el-table-column>
      <el-table-column prop="userName" label="姓名" header-align="center" align="center"></el-table-column>
      <el-table-column prop="useStatus" label="用户类型" align="center" width="250">
        <template #default="{ row }">
          {{ row.userType.split(',').length == 2 ? '异常处置员，检查确认员' : row.userType == 1?'异常处置员':'检查确认员' }}
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

    <el-dialog title="添加/编辑线管员" :visible.sync="dialogVisible" width="50%" @close="handleDialogClose">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="二维码" prop="userQr">
          <el-input v-model="form.userQr"  maxlength="10" style="width:100px;" :disabled="form.id != ''"></el-input>
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-checkbox-group v-model="typeList">
            <el-checkbox label="1">异常处置员</el-checkbox>
            <el-checkbox label="2">检查确认员</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="姓名" prop="userName">
          <el-input v-model="form.userName"  maxlength="32"></el-input>
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
import {uploadAbnormaluser, listAbnormalUser, addAbnormalUser, updateAbnormalUser, updateAbnormalUserStatus, delAbnormalUser } from "@/api/system/base";

export default {
  name: "AbnormalUser",
  dicts: ['sys_normal_disable'],
  data() {
    let self = this;
    const checkuserType = (rule, value, callback) => {
      if (self.typeList.length > 0) {
          callback();
        }else {
          callback(new Error("请选择服务人员类型"));
        }
    }
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
      // 表格树数据
      dataList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 重新渲染表格状态
      refreshTable: true,
      // 表单参数
      form: {
        id:'',
        userQr:'',
        userName:'',
        useStatus:1
      },
      rules: {
        userQr: [{ required: true, message: '请输入二维码', trigger: 'blur' }],
        userName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        userType: [{ required: true,  validator:checkuserType, trigger: "blur" }]
      },
      typeList:[],
    };
  },
  created() {
    this.handleQuery();
  },
  methods: {
    beforeUpload(file) {
        const allowedExtensions = ['xls', 'xlsx'];
        const extension = file.name.split('.').pop().toLowerCase();
        const isAllowed = allowedExtensions.includes(extension);  
        if (!isAllowed) {
          this.$message.error('xls/xlsx格式的文件!');
        }
  
        return isAllowed;
      },
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
      listAbnormalUser(this.queryParams).then(response => {
        this.dataList = response.rows;
        this.total = response.total;          
        this.loading = false;
      });
    },
    // 状态修改
    handleStatusChange(row) {
      let self = this;
      let text = row.useStatus === 2 ? "启用" : "停用";
      this.$modal.confirm('确认要"' + text + '"处置人员"' + row.userName + '"吗？').then(function() {            
        var commitData = {
          userQr:row.userQr,
          useStatus: row.useStatus == 1 ? 2 : 1
        }
        return updateAbnormalUserStatus(commitData);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(text + "成功");
      }).catch(() => {});           
    },
    showAddModal() {
      this.form = {
        id:'',
        userQr:'',
        userName:'',
        useStatus:1
      };
      this.dialogVisible = true;
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.form = Object.assign({}, row);
      this.typeList = this.form.userType.split(',')
      this.form.id = this.form.userQr;
      this.dialogVisible = true;
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$modal.confirm('是否确认删除名称为"' + row.userName + '"的数据项？').then(function() {
        return delAbnormalUser(row.userQr);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleDialogClose() {
      this.form = {
        id:'',
        userQr:'',
        userName:'',
        useStatus:1
      };
    },          
    // 处理文件上传操作
    handleFileUpload(file) {
      this.loading = true;
      const formData = new FormData();
      formData.append('file', file.file);
      // 调用后端服务器的接口
      uploadAbnormaluser(formData).then((resp) => {
        this.getList();
        this.$modal.msgSuccess('上传成功');
        this.loading = false;
      }).catch((e) => {
        // this.$message.error(e.message);
            this.getList();
            this.loading = false;
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          this.form.userType = this.typeList.join(',')
          if (this.form.id == '') {
            addAbnormalUser(this.form).then(() => {
              this.$modal.msgSuccess("新增成功");    
              this.getList();
              this.dialogVisible = false;
            }).catch(() => {});    
          } else {
            updateAbnormalUser(this.form).then(() => {
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
  