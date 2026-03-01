<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" >      
        <el-form-item label="检索内容：" prop="searchValue">
          <el-input
            v-model="queryParams.searchValue"
            placeholder="背番号"
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
      row-key="backNo"
    >       
      <el-table-column prop="backNo" header-align="center" align="left" label="背番号" width="180">
      </el-table-column>
      <el-table-column prop="checkList" header-align="center" align="left" label="检查项目及比对值">
        <template slot-scope="scope">
          <ul>
            <li v-for="(checkItem, index) in scope.row.checkList" :key="index">
              {{ props.find(t=>t.id == checkItem.typeId) ==null?'': props.find(t=>t.id == checkItem.typeId).typeName}} (比对值: {{ getPropNames(checkItem.typeId, checkItem.propId) }})
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
    <el-dialog title="编辑/新增检查配置" :visible.sync="dialogVisible" width="80%" @close="handleDialogClose">
      <el-form ref="form" :model="form" :rules="rules" label-width="160px">
        <el-form-item label="背番号" prop="backNo">
          <el-input v-model="form.backNo" placeholder="请输入背番号" :disabled="action == 'edit'"/>
        </el-form-item>
        <el-form-item label="检查项目及比对值" style="max-height: 500px; overflow-y: auto; overflow-x: hidden;">
          <el-checkbox-group v-model="selectedProps" @change="onSelectionChange">
            <el-row :gutter="1" v-for="(prop, index) in props" :key="index">
              <el-col :span="2">
                <el-input :class="{ hidden: !selectedProps.includes(prop.id) }" v-model="prop.sortNo" maxlength="3" placeholder="序号" oninput="value=value.replace(/[^0-9]/g,'')" style="width: 60px" />
              </el-col>
              <el-col :span="8">
                <el-checkbox :label="prop.id">{{ prop.typeName }}</el-checkbox>
              </el-col>
              <el-col :span="8" v-if="selectedProps.includes(prop.id)">
                <el-form-item :prop="'checkList.' + prop.id">
                  <el-select v-model="prop.defaultValues" multiple placeholder="请选择比对值" @change="updateDefaultValues(prop)">
                    <el-option
                      v-for="option in prop.propList"
                      :key="option.propId"
                      :label="option.propName"
                      :value="option.propId"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="5" v-if="selectedProps.includes(prop.id)">
                <div v-for="item in prop.defaultValues" :key="item" style="display: flex;">
                  <el-upload
                    ref="upload"
                    class="upload-demo"
                    :on-preview="handlePreview"
                    action="#"
                    :on-remove="handleRemove"
                    :before-remove="beforeRemove"
                    :multiple='false'
                    accept=".bmp,.gif,.jpg,.jpeg,.png"
                    :before-upload="beforeUpload"
                    :show-file-list='false'
                    :on-exceed="handleExceed"
                    :http-request="handleFileUpload(item,prop)"
                  >
                    <el-button type="primary" plain icon="el-icon-upload" size="mini" >上传选项[{{getPropName(prop.id,item)}}]的图片</el-button>
                  </el-upload>      
                  <div v-show="prop.defaultImages.find(t=>t.propId == item).imageUrl" >
                    <img :src="`${baseUrl + prop.defaultImages.find(t=>t.propId == item).imageUrl}?t=${new Date().getTime()}`" @click="viewImage(prop.defaultImages.find(t=>t.propId == item).imageUrl)" alt="Image" style="object-fit:fill;width: 30px;height: 30px;">
                  </div>
                </div>
              </el-col>
            </el-row>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">保 存</el-button>
      </span>
    </el-dialog>
    <el-dialog :visible.sync="dialogVisible1" width="80%">
      <div class="image-container">
        <img :src="`${baseUrl + currentImageUrl}?t=${new Date().getTime()}`" alt="Current Image" class="responsive-image">
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAppearanceCheck, listAppearanceType,addAppearanceCheck ,updateAppearanceCheck,delAppearanceCheck,uploadTmp} from '@/api/system/base';

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
      dialogVisible1: false,
      dataList: [],      
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 重新渲染表格状态
      refreshTable: true,
      form: {
        backNo: '',
        checkList: []
      },
      selectedProps: [],
      props: [],
      rules: {
        backNo: [{ required: true, message: '请输入背番号', trigger: 'blur' }]
      },
      tempRules: {},
      action:'add',
      currentImageUrl:'',
      baseUrl:'',//process.env.VUE_APP_BASE_API,
    };
  },
  created() {
    this.getPropList();
    this.handleQuery();
  },
  methods: {
    handleExceed(files, fileList) {
        this.$modal.msgWarning(
          `当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`
        );
      },
    viewImage(url) {
        this.currentImageUrl = url;
        this.dialogVisible1 = true;
      },
    handlePreview(file) {
      console.log(file);
    },
    handleRemove(file, fileList) {
      console.log(file, fileList);
    },
    beforeRemove(file, fileList) {
      return this.$confirm(`确定移除 ${file.name}？`);
    },
    beforeUpload(file) {
      const allowedExtensions = ['bmp', 'gif', 'jpg', 'jpeg', 'png'];
      const extension = file.name.split('.').pop().toLowerCase();
      const isAllowed = allowedExtensions.includes(extension);  
      if (!isAllowed) {
        this.$message.error('只能上传bmp/gif/jpg/jpeg/png格式的图片!');
      }

      return isAllowed;
    },
    // 处理文件上传操作
    handleFileUpload(item,prop) {
      let self = this
      return (options) => {
        const formData = new FormData();
        formData.append('file', options.file);
    // handleFileUpload(file) {
        self.loading = true;
      // const formData = new FormData();
      // formData.append('file', file.file); 
      // 调用后端服务器的接口
        uploadTmp(formData).then((resp) => {
          prop.defaultImages.find(t=>t.propId == item).imageUrl = resp.url;
          
          self.$modal.msgSuccess('上传成功');
          self.$nextTick(()=>{
            self.$forceUpdate()
          })
          
        }).catch((e) => {
          self.$message.error(e.message);
        })
      }
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
    getList() {
      this.loading = true;      
      listAppearanceCheck(this.queryParams).then(response => {
        this.dataList = response.rows;
        this.total = response.total;          
        this.loading = false;
      });
    },
    getPropList() {
      listAppearanceType({useStatus:1}).then(response => {
        this.props = response.rows;
        // 确保每个属性都有一个响应式的 defaultValue 字段
        this.props.forEach(prop => {
          this.$set(prop, 'defaultValues', []);
        });
      });
    },
    showAddModal() {
      this.handleUpdate(null);
    },
    getPropName(typeId, propId) {
      const prop = this.props.find(p => p.id === typeId);
      return prop ? prop.propList.find(opt => opt.propId === propId)?.propName : '';
    },
    onSelectionChange() {
      this.selectedProps.forEach(propId => {
        const prop = this.props.find(p => p.id === propId);
        if (!prop.defaultValue) {
          this.$set(prop, 'defaultValue', ''); // 确保 defaultValue 始终存在
        }
      });
    },
    getPropNames(typeId, propId) {
      const prop = this.props.find(p => p.id === typeId);
      return prop ? prop.propList
                      .filter(option => propId.split(',').map(id => id.trim()).includes(String(option.propId)))
                      .map(option => option.propName)
                      .join(', ') : '';
    },
    handleUpdate(row) {
      if (row == null)
        this.action = 'add';
      else
        this.action = 'edit';
      this.dialogVisible = true;
      this.form.backNo = row?.backNo || '';
      this.selectedProps = (row?.checkList || []).map(checkItem => checkItem.typeId);
      this.props.forEach(prop => {
        const matchingCheckItem = (row?.checkList || []).find(checkItem => checkItem.typeId === prop.id);
        if (matchingCheckItem) {
          const propImgs = matchingCheckItem.imageList ? matchingCheckItem.imageList : [];
          const propIds = matchingCheckItem.imageList ? matchingCheckItem.imageList.map(dv => Number(dv.propId)):[];
          this.$set(prop, 'defaultValues', propIds); // 确保 propIds 是数字
          this.$set(prop, 'defaultImages', propImgs);  
          this.$set(prop, 'sortNo', matchingCheckItem.sortNo);  
        } else {
          this.$set(prop, 'defaultValues', []);
          this.$set(prop, 'defaultImages', []);
          this.$set(prop, 'sortNo', '');  
        }
      });
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除背番号为"' + row.backNo + '"的数据项？').then(function() {
        return delAppearanceCheck(row.backNo);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleDialogClose() {
      this.form.backNo = '';
      this.selectedProps = [];
      this.props.forEach(prop => {
        this.$set(prop, 'defaultValues', []);
      });
    },
    updateDefaultValue(prop) {
      this.$set(prop, 'defaultValue', prop.defaultValue);
    },
    updateDefaultValues(prop) {
      this.$set(prop, 'defaultValues', prop.defaultValues);
      // 创建一个新的 defaultValues 数组，以保持原有的 imageUrl
      const newDefaultValues = prop.defaultValues.map(selectedId => {
        // 尝试找到已有的 defaultValues 中对应的项
        const existingValue = prop.defaultImages.find(dv => dv.propId === String(selectedId));
        if (existingValue) {
          // 如果找到了，则保留其 url
          return { ...existingValue };
        } else {
          // 如果没有找到，则创建一个新的对象，imageUrl 设置为 ''
          return { propId: selectedId, imageUrl: '' };
        }
      });
      this.$set(prop, 'defaultImages', newDefaultValues);
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          const missingDefaultValues = this.selectedProps
            .filter(propId => !this.props.find(p => p.id === propId).defaultValues.length)
            .map(propId => this.props.find(p => p.id === propId).typeName);

          if (missingDefaultValues.length > 0) {
            this.$message.error(`请为以下检查项目选择比对值: ${missingDefaultValues.join(', ')}`);
            return false;
          }
          const formData = {
            backNo: this.form.backNo,
            checkList: this.selectedProps.map(propId => {
              const prop = this.props.find(p => p.id === propId);
              return {
                typeId: prop.id,
                imageList: prop.defaultImages,
                sortNo:prop.sortNo
              };
            })
          };
          
          console.log(formData)
          if (formData.checkList.some(item => item.sortNo == null) || formData.checkList.some(item => item.sortNo == '')){
              this.$message.error(`序号为必填项。`);
              return false;
          }
          if (this.action == 'add') {
            addAppearanceCheck(formData).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.dialogVisible = false;
              this.getList();
            }).catch(() => {});
          } else {
            updateAppearanceCheck(formData).then(response => {
              this.$modal.msgSuccess("更新成功");
              this.dialogVisible = false;
              this.getList();
            }).catch(() => {});
          }
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
  }
};
</script>

<style scoped>
.el-card {
  margin-top: 20px;
}
.hidden {
  visibility: hidden;
}
</style>
