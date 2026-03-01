<template>
  <div class="app-container">
    <!-- 上传按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-upload" size="mini" @click="handleUpload">上传标签文件</el-button>
      </el-col>
    </el-row>

    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="批次ID" prop="id">
        <el-input v-model="queryParams.id" placeholder="请输入批次ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="客户编码" prop="custBm">
        <el-select v-model="queryParams.custBm" placeholder="请选择客户" clearable>
          <el-option
            v-for="customer in customers"
            :key="customer.customerCode"
            :label="customer.customerName"
            :value="customer.customerCode"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="处理状态" prop="handleStatus">
        <el-select v-model="queryParams.handleStatus" placeholder="请选择处理状态" clearable>
          <el-option label="待处理" value="0" />
          <el-option label="处理中" value="1" />
          <el-option label="处理完成" value="2" />
          <el-option label="部分失败" value="3" />
          <el-option label="全部失败" value="4" />
          <el-option label="异常结束" value="9" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 自动刷新设置 -->
    <el-row :gutter="10" class="mb8" v-show="false">
      <el-col :span="4">
        <el-form :model="autoRefreshForm" size="small" :inline="true">
          <el-form-item label="自动刷新">
            <el-switch v-model="autoRefreshForm.enabled" @change="handleAutoRefreshChange" />
          </el-form-item>
          <el-form-item label="间隔(秒)">
            <el-input-number v-model="autoRefreshForm.interval" :min="5" :max="60" :step="5" size="small" />
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="taskList"
      style="width: 100%"
    >
      <!-- <el-table-column type="index" width="50" align="center" /> -->
      <el-table-column label="批次ID" prop="id" width="120" align="center" />
      <el-table-column label="客户名" width="120" align="center">
        <template slot-scope="scope">
          {{ getCustomerName(scope.row.custBm) }}
        </template>
      </el-table-column>
      <el-table-column label="上传时间" prop="uploadTime" width="160" align="center" />
      <el-table-column label="上传文件数量" prop="fileCount" width="100" align="center" />
      <el-table-column label="页数总量" prop="pageCount" width="100" align="center" />
      <el-table-column label="待处理数据量" prop="dataCount" width="120" align="center" />
      <el-table-column label="处理状态" prop="handleStatus" align="center">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.handleStatus)">
            {{ getStatusText(scope.row.handleStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="开始处理时间" prop="startHandleTime" width="160" align="center" />
      <el-table-column label="结束处理时间" prop="endHandleTime" width="160" align="center" />
      <el-table-column label="消耗时间(秒)" prop="costSec" width="120" align="center" />
      <el-table-column label="成功处理数据量" prop="successCount" width="140" align="center" />
      <!-- <el-table-column label="打印类别" prop="printType" width="100" align="center">
        <template slot-scope="scope">
          {{ scope.row.printType === 1 ? '普通' : '穿透' }}
        </template>
      </el-table-column> -->
      <el-table-column label="操作" align="center" width="260" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleLabelDetail(scope.row)">标签详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-download" @click="handleBatchDownload(scope.row)" :disabled="!scope.row.outputFile || scope.row.handleStatus !== 2">下载</el-button>
          <el-button size="mini" type="text" icon="el-icon-warning-outline" @click="handleViewLog(scope.row)" :disabled="scope.row.handleStatus === 0 || scope.row.handleStatus === 1 || scope.row.handleStatus === 2" style="color: #E6A23C" >异常日志</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" style="color: #F56C6C">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 上传对话框 -->
    <el-dialog title="上传标签文件" :visible.sync="uploadDialogVisible" width="500px" append-to-body>
      <el-form ref="uploadForm" :model="uploadForm" label-width="100px">
        <el-form-item label="选择客户" prop="customerCode">
          <el-select v-model="uploadForm.customerCode" placeholder="请选择客户" style="width: 300px;">
            <el-option
              v-for="customer in customers"
              :key="customer.customerCode"
              :label="customer.customerName"
              :value="customer.customerCode"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择文件" prop="file">
          <div class="upload-container">
            <el-upload
              class="upload-demo"
              ref="fileUpload"
              action="#"
              :auto-upload="false"
              :on-change="handleFileChange"
              :on-exceed="handleExceed"
              :limit="100"
              :file-list="fileList"
              accept=".pdf"
              :multiple="true"
              :http-request="handleHttpRequest"
            >
              <el-button slot="trigger" type="primary">选取PDF文件</el-button>
              <div slot="tip" class="el-upload__tip">
                支持上传多个PDF文件，每次最多100个
              </div>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpload">确定上传</el-button>
      </div>
    </el-dialog>

    <!-- 标签详情对话框 -->
    <el-dialog title="标签详情" :visible.sync="detailDialogVisible" width="80%" append-to-body>
      <el-form ref="detailForm" :model="detailForm" label-width="150px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="批次ID">
              <el-input v-model="detailForm.id" disabled />
            </el-form-item>
            <el-form-item label="客户名">
              <el-input v-model="detailForm.customerName" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <!-- 标签列表 -->
      <el-card shadow="hover" class="mt10">
        <template slot="header">
          <div class="card-header">
            <span>标签列表</span>
            <el-tag type="info">{{ totalLabels }} 个标签</el-tag>
          </div>
        </template>
        <div class="card-body" style="overflow: auto; max-height: 450px;">
          <el-table v-if="pagedLabelList.length > 0" :data="pagedLabelList" style="width: 2000px;">
            <!-- 固定列：数据标号和序列号 -->
            <el-table-column label="数据标号" prop="dataNo" width="100" fixed="left" align="center" />
            <el-table-column label="序列号" width="100" fixed="left" align="center">
              <template slot-scope="scope">
                {{ formatSerialNumber(scope.row.serialNumber) }}
              </template>
            </el-table-column>
            
            <!-- 中间可滚动列：所有其他字段 -->
            <el-table-column label="背番" prop="backNo" width="100" align="center" />
            <el-table-column label="TR纳入日" prop="trEntryDate" width="120" align="center" />
            <el-table-column label="SEQ" prop="seqNo" width="100" align="center" />
            <el-table-column label="订货号" prop="orderCode" width="120" align="center" />
            <el-table-column label="订单NO." prop="orderNo" width="120" align="center" />
            <el-table-column label="总箱数" prop="boxCount" width="100" align="center" />
            <el-table-column label="纳入指示数量" prop="entryTargetBoxCount" width="120" align="center" />
            <el-table-column label="发行日" prop="publishDate" width="120" align="center" />
            <el-table-column label="物料编码(顾客)" prop="custMatNo" width="150" align="center" />
            <el-table-column label="物料编码(制造商)" prop="makerMatNo" width="150" align="center" />
            <el-table-column label="品名" prop="custMatName" width="150" align="center" />
            <el-table-column label="物料简码" prop="custBackNo" width="120" align="center" />
            <el-table-column label="收容数" prop="custSnp" width="100" align="center" />
            <el-table-column label="W/C" prop="custWc" width="100" align="center" />
            <el-table-column label="库位" prop="custPlacePos" width="120" align="center" />
            <el-table-column label="箱种" prop="custBoxType" width="100" align="center" />
            <el-table-column label="线别" prop="lineName" width="100" align="center" />
            <el-table-column label="批次号" prop="batchNo" width="120" align="center" />
            <el-table-column label="顾客名" prop="custName" width="120" align="center" />
            <el-table-column label="颜色编码" prop="custColorCode" width="120" align="center" />
            <el-table-column label="KD NO." prop="kdNo" width="100" align="center" />
            <el-table-column label="受入场所编号" prop="entryPlaceNo" width="120" align="center" />
            <el-table-column label="纳入指示日期" prop="entryDate" width="120" align="center" />
            <el-table-column label="便次" prop="tipNo" width="100" align="center" />
            <el-table-column label="时间" prop="entryTime" width="100" align="center" />
            <el-table-column label="出货日" prop="outDate" width="120" align="center" />
            <el-table-column label="托盘No" prop="trayNo" width="120" align="center" />
            <el-table-column label="客先使用栏" prop="memo" width="150" align="center" />
            
            <!-- 固定列：操作栏 -->
            <el-table-column label="操作" width="100" fixed="right" align="center">
              <template slot-scope="scope">
                <el-button size="mini" type="primary" icon="el-icon-printer" @click="handlePrint(scope.row)">打印</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-else class="mt10 text-center text-gray">
            暂无标签信息
          </div>
        </div>
        <!-- 分页 -->
        <div class="mt10" v-if="totalLabels > 0">
          <el-pagination
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-size="5"
            layout="total, prev, pager, next, jumper"
            :total="totalLabels"
          />
        </div>
      </el-card>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 异常日志对话框 -->
    <el-dialog title="异常日志" :visible.sync="logDialogVisible" width="80%" append-to-body>
      <el-form ref="logForm" :model="logForm" label-width="150px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="批次ID">
              <el-input v-model="logForm.id" disabled />
            </el-form-item>
            <el-form-item label="客户名">
              <el-input v-model="logForm.customerName" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <!-- 异常日志列表 -->
      <el-card shadow="hover" class="mt10" v-loading="logLoading">
        <template slot="header">
          <div class="card-header">
            <span>异常日志列表</span>
            <el-tag type="danger">{{ logList.length }} 条异常</el-tag>
          </div>
        </template>
        <div class="card-body" style="overflow: auto; max-height: 450px;">
          <el-table v-if="logList.length > 0" :data="logList" style="width: 100%">
            <el-table-column label="任务ID" prop="taskId" width="200" align="center" show-overflow-tooltip />
            <el-table-column label="数据标号" prop="dataNo" width="120" align="center" />
            <el-table-column label="处理失败原因" prop="log" align="left">
              <template slot-scope="scope">
                <el-popover
                  placement="top"
                  width="400"
                  trigger="hover"
                  :content="scope.row.log"
                >
                  <template slot="reference">
                    <span class="log-content">{{ scope.row.log }}</span>
                  </template>
                </el-popover>
              </template>
            </el-table-column>
          </el-table>
          <div v-else class="mt10 text-center text-gray">
            暂无异常日志
          </div>
        </div>
      </el-card>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="logDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 上传进度条 -->
    <el-progress v-if="showProgress" :percentage="uploadProgress" :stroke-width="20" :status="uploadStatus || undefined" />

    <!-- 上传结果提示 -->
    <el-alert
      v-if="showResult"
      :title="resultMessage"
      :type="resultType"
      show-icon
      class="mt10"
    />
  </div>
</template>

<script>
import { listCustomer, uploadLabelFile, listTask, listLabelDetail, deleteTask, printLabels, getTaskLog } from '@/api/label'
import Pagination from '@/components/Pagination'

export default {
  name: 'LabelManagement',
  components: {
    Pagination
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 批次表格数据
      taskList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        id: '',
        custBm: '',
        handleStatus: ''
      },
      // 客户列表
      customers: [],
      // 上传对话框
      uploadDialogVisible: false,
      uploadForm: {
        customerCode: ''
      },
      // 文件上传
      fileList: [],
      currentFiles: [],
      // 上传进度
      showProgress: false,
      uploadProgress: 0,
      uploadStatus: '',
      // 上传结果
      showResult: false,
      resultMessage: '',
      resultType: '',
      // 自动刷新
      autoRefreshForm: {
        enabled: true,
        interval: 30
      },
      autoRefreshTimer: null,
      // 标签详情对话框
      detailDialogVisible: false,
      detailForm: {},
      labelList: [],
      pagedLabelList: [],
      totalLabels: 0,
      currentPage: 1,
      pageSize: 5,
      // 异常日志对话框
      logDialogVisible: false,
      logForm: {},
      logList: [],
      logLoading: false
    }
  },
  created() {
    this.getCustomers()
    this.getList()
    this.startAutoRefresh()
  },
  beforeDestroy() {
    this.stopAutoRefresh()
  },
  methods: {
    /** 获取客户列表 */
    getCustomers() {
      listCustomer().then(response => {
        this.customers = response.rows
      })
    },
    /** 查询批次列表 */
    getList(isRefresh = false) {
      if (!isRefresh) {
        this.loading = true
      }
      listTask(this.queryParams).then(response => {
        this.taskList = response.rows
        this.total = response.total
        if (!isRefresh) {
          this.loading = false
        }
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    /** 重置表单 */
    resetForm(formName) {
      if (this.$refs[formName]) {
        this.$refs[formName].resetFields()
      }
    },
    /** 处理上传按钮点击 */
    handleUpload() {
      this.uploadDialogVisible = true
      this.uploadForm.customerCode = ''
      this.fileList = []
      this.currentFiles = []
    },
    /** 处理文件选择变化 */
    handleFileChange(file, fileList) {
      this.fileList = fileList
      this.currentFiles = fileList.map(item => item.raw)
    },
    /** 处理文件超出限制 */
    handleExceed(files, fileList) {
      this.$message.warning(`当前限制选择 10 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`)
    },
    /** 提交上传 */
    async submitUpload() {
      if (!this.uploadForm.customerCode) {
        this.$message.warning('请先选择客户')
        return
      }

      if (this.currentFiles.length === 0) {
        this.$message.warning('请先选择要上传的PDF文件')
        return
      }

      this.showProgress = true
      this.uploadProgress = 0
      this.uploadStatus = ''
      this.showResult = false

      // 准备上传数据
      const formData = new FormData()
      formData.append('customerCode', this.uploadForm.customerCode)
      this.currentFiles.forEach(file => {
        formData.append('file', file)
      })

      // 异步调用上传API，不等待返回结果
      uploadLabelFile(formData).then(response => {
        // 上传成功后的处理（可选）
        console.log('上传成功:', response)
      }).catch(error => {
        // 上传失败后的处理（可选）
        console.error('上传失败:', error)
      })

      // 直接继续往下走，不等待上传结果
      this.uploadProgress = 100
      this.uploadStatus = 'success'
      this.showResult = true
      this.resultMessage = '标签文件已提交，系统正在后台处理...'
      this.resultType = 'success'
      this.uploadDialogVisible = false

      // 刷新批次列表
      this.getList()

      // 3秒后隐藏结果提示
      setTimeout(() => {
        this.showResult = false
        this.showProgress = false
      }, 3000)
    },
    /** 自定义上传处理 */
    handleHttpRequest() {
      // 自定义上传逻辑，由submitUpload方法处理
    },
    /** 获取状态类型 */
    getStatusType(status) {
      switch (status) {
        case 2:
          return 'success'
        case 3:
        case 4:
        case 9:
          return 'danger'
        case 1:
          return 'warning'
        default:
          return 'info'
      }
    },
    /** 获取状态文本 */
    getStatusText(status) {
      switch (status) {
        case 0:
          return '待处理'
        case 1:
          return '处理中'
        case 2:
          return '处理完成'
        case 3:
          return '部分失败'
        case 4:
          return '全部失败'
        case 9:
          return '异常结束'
        default:
          return status
      }
    },
    /** 开始自动刷新 */
    startAutoRefresh() {
      if (this.autoRefreshForm.enabled) {
        this.stopAutoRefresh()
        this.autoRefreshTimer = setInterval(() => {
          this.getList(true)
        }, this.autoRefreshForm.interval * 1000)
      }
    },
    /** 停止自动刷新 */
    stopAutoRefresh() {
      if (this.autoRefreshTimer) {
        clearInterval(this.autoRefreshTimer)
        this.autoRefreshTimer = null
      }
    },
    /** 处理自动刷新开关变化 */
    handleAutoRefreshChange() {
      if (this.autoRefreshForm.enabled) {
        this.startAutoRefresh()
      } else {
        this.stopAutoRefresh()
      }
    },
    /** 处理标签详情按钮点击 */
    handleLabelDetail(row) {
      this.detailDialogVisible = true
      // 重置分页
      this.currentPage = 1
      this.pageSize = 5
      // 填充基本信息
      const customer = this.customers.find(c => c.customerCode === row.custBm)
      this.detailForm = {
        id: row.id,
        customerName: customer ? customer.customerName : row.custBm
      }
      // 获取标签详情
      this.getLabelDetail(row.id)
    },
    /** 获取标签详情 */
    getLabelDetail(taskId) {
      listLabelDetail({taskId: taskId,pageSize:10000}).then(response => {
        this.labelList = response.rows || []
        this.totalLabels = response.total || 0
        this.updatePagedLabels()
      }).catch(() => {
        this.labelList = []
        this.pagedLabelList = []
        this.totalLabels = 0
      })
    },
    /** 更新分页后的标签列表 */
    updatePagedLabels() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      this.pagedLabelList = this.labelList.slice(start, end)
    },
    /** 处理当前页码变化 */
    handleCurrentChange(current) {
      this.currentPage = current
      this.updatePagedLabels()
    },
    /** 处理打印按钮点击 */
    handlePrint(row) {
      // 显示加载中效果
      const loading = this.$loading({
        lock: true,
        text: '打印任务提交中，请稍候...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })

      // 准备打印数据：单个标签的数据
      const printData = [{
        taskId: row.taskId || row.id,
        dataNo: row.dataNo
      }]

      // 调用打印接口
      printLabels(printData).then(response => {
        // 处理文件流下载
        const blob = new Blob([response])
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        // 生成文件名：包含当前日期、时间和标签信息
        const now = new Date()
        const timestamp = now.toISOString().replace(/[-:.]/g, '').substring(0, 14)
        a.download = `label_${row.dataNo}_${this.formatSerialNumber(row.serialNumber)}_${timestamp}.pdf`
        a.style.display = 'none'
        document.body.appendChild(a)
        a.click()
        // 清理
        window.URL.revokeObjectURL(url)
        document.body.removeChild(a)
        
        this.$message.success('标签文件下载成功')
      }).catch(error => {
        this.$message.error('打印失败：' + (error.message || '未知错误'))
      }).finally(() => {
        // 关闭加载中效果
        loading.close()
      })
    },
    /** 格式化序列号，保证4位，前面补零 */
    formatSerialNumber(serialNumber) {
      if (!serialNumber && serialNumber !== 0) {
        return '0000'
      }
      const num = parseInt(serialNumber, 10)
      return num.toString().padStart(4, '0')
    },
    /** 根据客户编码获取客户名称 */
    getCustomerName(customerCode) {
      if (!customerCode) {
        return ''
      }
      const customer = this.customers.find(c => c.customerCode === customerCode)
      return customer ? customer.customerName : customerCode
    },
    /** 处理删除按钮点击 */
    handleDelete(row) {
      this.$confirm(`确定要删除批次 ${row.id} 吗？`, '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteTask(row.id).then(response => {
          this.$message.success('删除成功')
          this.getList()
        }).catch(error => {
          this.$message.error('删除失败：' + (error.message || '未知错误'))
        })
      }).catch(() => {
        // 取消删除
      })
    },
    /** 处理批次下载 */
    handleBatchDownload(row) {
      if (!row.outputFile) {
        this.$message.warning('该批次暂无输出文件')
        return
      }

      this.$confirm(`确定要下载批次 ${row.id} 的文件吗？`, '下载确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 直接使用outputFile字段，拼上前缀路径
        const baseUrl = process.env.VUE_APP_BASE_API
        const fileUrl = baseUrl + row.outputFile

        // 创建隐藏的a标签进行下载
        const link = document.createElement('a')
        link.href = fileUrl
        link.download = `batch_${row.id}.pdf`
        link.style.display = 'none'
        document.body.appendChild(link)
        link.click()

        // 清理
        document.body.removeChild(link)
        this.$message.success('下载成功')
      }).catch(() => {
        // 取消下载
      })
    },
    /** 查看异常日志 */
    handleViewLog(row) {
      this.logDialogVisible = true
      // 填充基本信息
      const customer = this.customers.find(c => c.customerCode === row.custBm)
      this.logForm = {
        id: row.id,
        customerName: customer ? customer.customerName : row.custBm
      }
      // 获取异常日志
      this.getTaskLog(row.id)
    },
    /** 获取任务异常日志 */
    getTaskLog(taskId) {
      this.logLoading = true
      getTaskLog(taskId).then(response => {
        this.logList = response || []
      }).catch(() => {
        this.logList = []
      }).finally(() => {
        this.logLoading = false
      })
    }
  }
}
</script>

<style scoped>
.fixed-width {
  width: 120px;
}

.small-padding {
  padding-right: 10px;
}

.mt10 {
  margin-top: 10px;
}

.text-center {
  text-align: center;
}

.text-gray {
  color: #999;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-body {
  line-height: 24px;
  color: #666;
}

/* 上传容器滚动条样式 */
.upload-container {
  max-height: 300px;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 10px;
}

/* 自定义滚动条样式 */
.upload-container::-webkit-scrollbar {
  width: 8px;
}

.upload-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.upload-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.upload-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>