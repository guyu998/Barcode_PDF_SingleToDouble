<template>
  <div class="app-container">
    <!-- 顶部欢迎信息 -->
    <el-row :gutter="10" class="welcome-section mb8">
      <el-col :span="24">
        <h1>欢迎使用理昌初终物检查品质确认系统</h1>
        <p>这里是流水线数据处理页面，您可以在这里进行数据查询、上传和配置。</p>
      </el-col>
    </el-row>

    <!-- 模式切换按钮 -->
    <el-row :gutter="10" class="mode-switch-section mb8">
      <el-col :span="24">
        <el-switch
          v-model="isSimpleMode"
          active-text="简单模式"
          inactive-text="列表模式"
          @change="handleModeSwitch"
        />
      </el-col>
    </el-row>

    <!-- 简单模式下的流水线选择和快速操作面板 -->
    <transition name="fade">
      <div v-if="isSimpleMode" class="simple-mode-section">
        <!-- 流水线选择下拉菜单 -->
        <el-row :gutter="10" class="pipeline-selection-section mb8">
          <el-col :span="24">
            <el-select v-model="selectedPipeline" placeholder="请选择流水线" style="width: 200px;">
              <el-option
                v-for="pipeline in pipelines"
                :key="pipeline.lineId"
                :label="pipeline.lineName"
                :value="pipeline.lineId"
              />
            </el-select>
          </el-col>
        </el-row>

        <!-- 快速操作面板 -->
        <el-row :gutter="10" class="quick-action-section mb8">
          <!-- <el-col :span="6">
            <el-card shadow="hover" @click.native="handleQuickAction('query')">
              <div class="card-content">
                <i class="el-icon-search card-icon"></i>
                <p>快速查询</p>
              </div>
            </el-card>
          </el-col> -->
          <el-col :span="6">
            <el-card shadow="hover" @click.native="handleQuickAction('viewData')">
              <div class="card-content">
                <i class="el-icon-data-analysis card-icon"></i>
                <p>查看数据</p>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" @click.native="handleQuickAction('config')">
              <div class="card-content">
                <i class="el-icon-setting card-icon"></i>
                <p>配置检查</p>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" @click.native="handleQuickAction('upload')">
              <div class="card-content">
                <i class="el-icon-upload2 card-icon"></i>
                <p>上传文件</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </transition>

    <!-- 列表模式下的内容 -->
    <transition name="fade">
      <div v-if="!isSimpleMode" class="list-mode-section">
        <!-- 搜索表单 -->
        <el-row :gutter="10" class="search-section mb8">
          <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
            <el-form-item label="检索内容：" prop="searchValue">
              <el-input v-model="queryParams.searchValue" placeholder="流水线名/Ip地址" clearable @keyup.enter.native="handleQuery"/>
            </el-form-item>
            <el-form-item>
              <el-col :span="1.5">
                <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
                <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
              </el-col>
            </el-form-item>
          </el-form>
        </el-row>

        <!-- 数据表格 -->
        <el-row :gutter="10" class="table-section mb8" style="width:100%;">
          <el-table
            style="width:100%;"
            v-loading="loading"
            :data="dataList.slice((pageNum-1)*pageSize,pageNum*pageSize)"
          >
            <el-table-column prop="lineName" label="流水线名" header-align="center" align="center" />
            <el-table-column prop="ipAddress" label="IP" header-align="center" align="center" width="130"/>
            <el-table-column prop="uploadCount" label="上传次数" header-align="center" align="center" width="80" />
            <el-table-column prop="createBy" label="管理人" header-align="center" align="center" width="100" />
            <el-table-column prop="createTime" label="最新上传时间" header-align="center" align="center" width="150" />
            <el-table-column label="操作" align="center" width="200">
              <template slot-scope="scope">
                <el-upload style="display: inline-block;width:50px"
                  ref="upload"
                  action="#"
                  :before-remove="beforeRemove"
                  :multiple="false"
                  accept=".xls,.xlsx"
                  :before-upload="beforeUpload.bind(null, scope.row)"
                  :show-file-list="false"
                  :on-exceed="handleExceed"
                  :http-request="handleFileUpload"
                >
                  <el-button type="text" plain icon="el-icon-upload" size="mini">上传</el-button>
                </el-upload>
                <el-button size="mini" type="text" icon="el-icon-setting" @click="handleConfig(scope.row)">配置</el-button>
                <el-button size="mini" type="text" icon="el-icon-s-data" @click="handleData(scope.row)">数据</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-row>

        <!-- 分页 -->
        <el-row :gutter="10" class="pagination-section mb8">
          <pagination v-show="total>0" :total="total" :page.sync="pageNum" :limit.sync="pageSize" />
        </el-row>
      </div>
    </transition>

    <!-- 配置对话框 -->
    <el-dialog title="编辑/新增检查配置" :visible.sync="dialogVisible" width="90%" @close="handleDialogClose">
      <el-form ref="form" :model="form" label-width="160px">
        <el-form-item label="流水线名" prop="backNo">
          <el-input v-model="form.lineName" placeholder="" disabled />
        </el-form-item>
        <el-form-item label="检查项目及选项" style="max-height: 500px; overflow-y: auto; overflow-x: hidden;">
          <el-checkbox-group v-model="selectedProps" @change="onSelectionChange">
            <el-row :gutter="1" v-for="(prop, index) in props" :key="index">
              <el-col :span="10">
                <el-checkbox :label="prop.id">{{ prop.typeName }}</el-checkbox>
              </el-col>
              <el-col :span="12" v-if="selectedProps.includes(prop.id)">
                <el-form-item :prop="'checkList.' + prop.id">
                  <el-select style="width:100%" v-model="selectedProps1.find(t => t.typeId == prop.id).selList" multiple placeholder="请选择属性值" @change="onCheckChange($event, prop.id)">
                    <el-option
                      v-for="option in prop.propList"
                      :key="option.propId"
                      :label="option.propName"
                      :value="option.propId"
                    />
                  </el-select>
                </el-form-item>
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
  </div>
</template>

<style scoped>
.app-container {
  padding: 20px;
}

.welcome-section {
  margin-bottom: 20px;
}

.welcome-section h1 {
  font-size: 24px;
  margin-bottom: 10px;
}

.welcome-section p {
  color: #666;
  font-size: 16px;
}

.mode-switch-section {
  margin-top: 20px;
}

.simple-mode-section, .list-mode-section {
  margin-top: 20px;
}

.quick-action-section .el-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.quick-action-section .el-card:hover {
  transform: translateY(-10px);
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.card-icon {
  font-size: 32px;
  color: #409eff;
  margin-bottom: 10px;
}

.pipeline-selection-section {
  margin-top: 20px;
}

.search-section {
  margin-top: 20px;
}

.table-section {
  margin-top: 20px;
}

.pagination-section {
  margin-top: 20px;
  text-align: right;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter, .fade-leave-to /* .fade-leave-active in <2.1.8 */ {
  opacity: 0;
}
</style>

<script>
import { listHistory, uploadBackData, laset, listAppearanceType, addlaset } from "@/api/system/base";
import Pagination from "@/components/Pagination"; // 假设你有一个分页组件

export default {
  name: "HistoryShow",
  components: {
    Pagination
  },
  data() {
    return {
      loading: true,
      showSearch: true,
      pageNum: 1,
      pageSize: 20,
      total: 0,
      queryParams: {
        searchValue: ''
      },
      lineName: '',
      dialogVisible: false,
      dataList: [],
      form: {
        lineId: 0,
        lineName: '',
        checkList: []
      },
      selectedProps: [],
      selectedProps2: [],
      selectedProps1: [],
      props: [],
      pipelines: [], // 所有流水线列表
      selectedPipeline: null, // 当前选中的流水线
      title: "",
      open: false,
      refreshTable: true,
      isSimpleMode: false // 控制模式切换
    };
  },
  created() {
    this.handleQuery();
    this.getPropList();
    this.getPipelines();
  },
  methods: {
    onCheckChange(value, typeId) {
      this.selectedProps1.find(t => t.typeId == typeId).selList = value;
    },
    handleConfig(row) {
      this.dialogVisible = true;
      this.form.lineId = row.lineId;
      this.form.lineName = row.lineName;
      laset(row.lineId).then(response => {
        this.selectedProps1 = response.data.map(item => ({
          typeId: item.typeId,
          selList: item.propList.map(prop => prop.propId)
        }));
        this.selectedProps = response.data.map(item => item.typeId);
      }).catch(() => {});
    },
    submitForm() {
      for (let item of this.selectedProps1) {
        if (item.selList.length === 0) {
          this.$message.error(`属性选项不能为空`);
          return;
        }
      }
      const loading = this.$loading({
        lock: true,
        text: '保存中',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      this.selectedProps1 = this.selectedProps1.map(item => ({
        lineId: this.form.lineId,
        typeId: item.typeId,
        propList: item.selList.map(propId => ({ propId }))
      }));

      addlaset(this.selectedProps1).then(response => {
        loading.close();
        this.$modal.msgSuccess("配置成功");
        this.dialogVisible = false;
        this.handleQuery();
      }).catch(() => {});
    },
    getPropList() {
      listAppearanceType({ useStatus: 1 }).then(response => {
        this.props = response.rows;
        this.props.forEach(prop => {
          this.$set(prop, 'selList', []);
        });
      });
    },
    handleDialogClose() {
      this.form.lineName = '';
      this.selectedProps = [];
      this.props.forEach(prop => {
        this.$set(prop, 'selList', []);
      });
    },
    onSelectionChange() {
      const array1Set = new Set(this.selectedProps);
      const filteredArray2 = this.selectedProps1.filter(item => array1Set.has(item.typeId));
      const additionalItems = this.selectedProps.filter(id => !this.selectedProps1.some(item => item.typeId === id))
        .map(id => ({ typeId: id, selList: [] }));
      this.selectedProps1 = [...filteredArray2, ...additionalItems];
    },
    handleQuery() {
      this.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    getList() {
      this.loading = true;
      listHistory(this.queryParams).then(response => {
        this.dataList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    handleData(row) {
      this.$router.push('/Data/' + row.lineName);
    },
    beforeRemove(file, fileList) {
      return this.$confirm(`确定移除 ${file.name}？`);
    },
    beforeUpload(row, file) {
      this.lineName = row.lineName;
      const allowedExtensions = ['xls', 'xlsx'];
      const extension = file.name.split('.').pop().toLowerCase();
      const isAllowed = allowedExtensions.includes(extension);
      if (!isAllowed) {
        this.$message.error('只能上传xls格式的文件!');
      }
      return isAllowed;
    },
    handleExceed(files, fileList) {
      this.$modal.msgWarning(
        `当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`
      );
    },
    handleFileUpload(file) {
      this.loading = true;
      const formData = new FormData();
      formData.append('file', file.file);
      formData.append('lineName', this.lineName);
      uploadBackData(formData).then((resp) => {
        this.getList();
        this.$modal.msgSuccess('上传成功');
      }).catch((e) => {
        this.getList();
        this.$message.error(e.message);
      });
    },
    getPipelines() {
      listHistory({}).then(response => {
        this.pipelines = response.rows;
      });
    },
    handleQuickAction(action) {
      if (!this.selectedPipeline) {
        this.$message.warning('请先选择一个流水线');
        return;
      }
      const pipeline = this.pipelines.find(pipeline => pipeline.lineId === this.selectedPipeline);
      switch (action) {
        case 'query':
          this.queryParams.searchValue = pipeline.lineName;
          this.handleQuery();
          break;
        case 'viewData':
          this.handleData(pipeline);
          break;
        case 'config':
          this.handleConfig(pipeline);
          break;
        case 'upload':
          this.beforeUpload(pipeline, null);
          break;
        default:
          break;
      }
    },
    handleModeSwitch() {
      if (this.isSimpleMode) {
        this.selectedPipeline = null; // 清空选中的流水线
      } else {
        this.handleQuery(); // 切换回列表模式时重新加载数据
      }
    }
  }
};
</script>