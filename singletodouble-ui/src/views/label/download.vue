<template>
  <div class="app-container">
    <!-- 模式切换 -->
    <el-radio-group v-model="activeMode" size="small" class="mb8" @change="handleModeChange">
      <el-radio-button label="batch">批次下载</el-radio-button>
      <el-radio-button label="label">标签下载</el-radio-button>
    </el-radio-group>

    <!-- 批次下载模式 -->
    <div v-if="activeMode === 'batch'" key="batch-mode">
      <!-- 搜索表单 -->
      <el-form :model="batchQueryParams" ref="batchQueryForm" size="small" :inline="true" v-show="showSearch">
        <el-form-item label="批次ID" prop="id">
          <el-input v-model="batchQueryParams.id" placeholder="请输入批次ID" clearable @keyup.enter.native="handleBatchQuery" />
        </el-form-item>
        <el-form-item label="客户编码" prop="custBm">
          <el-select v-model="batchQueryParams.custBm" placeholder="请选择客户" clearable>
            <el-option
              v-for="customer in customers"
              :key="customer.customerCode"
              :label="customer.customerName"
              :value="customer.customerCode"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态" prop="handleStatus">
          <el-select v-model="batchQueryParams.handleStatus" placeholder="请选择处理状态" clearable>
            <el-option label="处理完成" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleBatchQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetBatchQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 批次列表 -->
      <el-table v-if="activeMode === 'batch'" v-loading="batchLoading" :data="batchList" style="width: 100%" id="batchTable">
        <el-table-column label="批次ID" prop="id" width="120" align="center" />
        <el-table-column label="客户名" width="120" align="center">
          <template slot-scope="scope">
            {{ getCustomerName(scope.row.custBm) }}
          </template>
        </el-table-column>
        <el-table-column label="上传时间" prop="uploadTime" width="180" align="center" />
        <el-table-column label="上传文件数量" prop="fileCount" width="120" align="center" />
        <el-table-column label="页数总量" prop="pageCount" width="100" align="center" />
        <el-table-column label="标签数量" prop="dataCount" width="120" align="center" />
        <!-- <el-table-column label="处理状态" prop="handleStatus" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.handleStatus)">
              {{ getStatusText(scope.row.handleStatus) }}
            </el-tag>
          </template>
        </el-table-column> -->
        <el-table-column label="输出文件" prop="outputFile" width="400" align="center" show-overflow-tooltip />
        <el-table-column label="操作" align="center" width="120">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-download"
              :disabled="!scope.row.outputFile || scope.row.handleStatus !== 2"
              @click="handleBatchDownload(scope.row)"
            >
              下载
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 批次分页 -->
      <pagination v-show="batchTotal > 0" :total="batchTotal" :page.sync="batchQueryParams.pageNum" :limit.sync="batchQueryParams.pageSize" @pagination="getBatchList" />
    </div>

    <!-- 标签下载模式 -->
    <div v-if="activeMode === 'label'" key="label-mode">
      <!-- 搜索表单 -->
      <el-form :model="labelQueryParams" ref="labelQueryForm" size="small" :inline="true" v-show="showSearch">
        <el-form-item label="批次号" prop="taskId">
          <el-input v-model="labelQueryParams.taskId" placeholder="请输入批次号" clearable @keyup.enter.native="handleLabelQuery" />
        </el-form-item>
        <el-form-item label="数据番号" prop="dataNo">
          <el-input v-model="labelQueryParams.dataNo" placeholder="请输入数据番号" clearable @keyup.enter.native="handleLabelQuery" />
        </el-form-item>
        <el-form-item label="序列号" prop="serialNumber">
          <el-input v-model="labelQueryParams.serialNumber" placeholder="请输入序列号" clearable @keyup.enter.native="handleLabelQuery" />
        </el-form-item>
        <el-form-item label="SEQ" prop="seqNo">
          <el-input v-model="labelQueryParams.seqNo" placeholder="请输入SEQ" clearable @keyup.enter.native="handleLabelQuery" />
        </el-form-item>
        <el-form-item label="物料编码（顾客）" prop="custMatNo">
          <el-input v-model="labelQueryParams.custMatNo" placeholder="请输入物料编码（顾客）" clearable @keyup.enter.native="handleLabelQuery" />
        </el-form-item>
        <el-form-item label="物料编码（生产商）" prop="makerMatNo">
          <el-input v-model="labelQueryParams.makerMatNo" placeholder="请输入物料编码（生产商）" clearable @keyup.enter.native="handleLabelQuery" />
        </el-form-item>
        <el-form-item label="颜色代码" prop="custColorCode">
          <el-input v-model="labelQueryParams.custColorCode" placeholder="请输入颜色代码" clearable @keyup.enter.native="handleLabelQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleLabelQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetLabelQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 待打印列表按钮 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-badge :value="cartCount" :hidden="cartCount === 0" class="item">
            <el-button type="primary" icon="el-icon-document" size="mini" @click="showCartDialog = true">
              待打印列表
            </el-button>
          </el-badge>
        </el-col>
      </el-row>

      <!-- 标签列表 -->
      <el-table v-if="activeMode === 'label'" v-loading="labelLoading" :data="labelList" style="width: 100%" id="labelTable" @selection-change="handleLabelSelectionChange">
        <el-table-column label="批次ID" prop="taskId" width="150" align="center" fixed="left">
          <template slot-scope="scope">
            {{ scope.row.taskId || scope.row.id || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="数据番号" width="100" align="center">
          <template slot-scope="scope">
            {{ formatSerialNumber(scope.row.dataNo) }}
          </template>
        </el-table-column>
        <el-table-column label="SEQ" prop="seqNo" width="180" align="center" />
        <el-table-column label="序列号" width="100" align="center">
          <template slot-scope="scope">
            {{ formatSerialNumber(scope.row.serialNumber) }}
          </template>
        </el-table-column>
        <el-table-column label="背番" prop="backNo" width="100" align="center" show-overflow-tooltip />
        <el-table-column label="物料简码" prop="custBackNo" width="120" align="center" show-overflow-tooltip />
        <el-table-column label="品名" prop="custMatName" width="150" align="center" show-overflow-tooltip />
        <el-table-column label="物料编码(顾客)" prop="custMatNo" width="150" align="center" show-overflow-tooltip />
        <el-table-column label="收容数" prop="custSnp" width="100" align="center" />
        <el-table-column label="物料编码(生产商)" prop="makerMatNo" width="150" align="center" show-overflow-tooltip />
        <el-table-column label="颜色代码" prop="custColorCode" width="100" align="center" />
        <el-table-column label="操作" align="center" width="100" >
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-plus"
              :disabled="isInCart(scope.row)"
              @click="addToCart(scope.row)"
            >
              {{ isInCart(scope.row) ? '已添加' : '添加' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 标签分页 -->
      <pagination v-show="labelTotal > 0" :total="labelTotal" :page.sync="labelQueryParams.pageNum" :limit.sync="labelQueryParams.pageSize" @pagination="getLabelList" />
    </div>

    <!-- 待打印列表对话框 -->
    <el-dialog title="待打印列表" :visible.sync="showCartDialog" width="80%" append-to-body>
      <div class="cart-header">
        <span>已选择 {{ cartCount }} 个标签</span>
        <el-button type="danger" size="mini" icon="el-icon-delete" @click="clearCart">清空列表</el-button>
      </div>

      <el-table :data="cartList" style="width: 100%" max-height="500">
        <el-table-column label="批次ID" prop="taskId" width="120" align="center" />
        <el-table-column label="数据标号" prop="dataNo" width="100" align="center" />
        <el-table-column label="序列号" width="100" align="center">
          <template slot-scope="scope">
            {{ formatSerialNumber(scope.row.serialNumber) }}
          </template>
        </el-table-column>
        <el-table-column label="背番" prop="backNo" width="100" align="center" show-overflow-tooltip />
        <el-table-column label="物料简码" prop="custBackNo" width="120" align="center" show-overflow-tooltip />
        <el-table-column label="品名" prop="custMatName" width="150" align="center" show-overflow-tooltip />
        <el-table-column label="操作" align="center" width="100">
          <template slot-scope="scope">
            <el-button size="mini" type="danger" icon="el-icon-delete" @click="removeFromCart(scope.$index)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button @click="showCartDialog = false">关闭</el-button>
        <el-button type="primary" icon="el-icon-printer" @click="handlePrint" :disabled="cartCount === 0">打印</el-button>
      </div>
    </el-dialog>

    <!-- 批量添加到待打印列表对话框 -->
    <el-dialog title="批量添加到待打印列表" :visible.sync="showBatchAddDialog" width="400px" append-to-body>
      <el-alert
        :title="'确认将选中的 ' + selectedLabels.length + ' 个标签添加到待打印列表吗？'"
        type="info"
        :closable="false"
        show-icon
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="showBatchAddDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmBatchAdd">确认添加</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCustomer, listTask, listLabelDetail, printLabels } from '@/api/label'
import Pagination from '@/components/Pagination'

export default {
  name: 'LabelDownload',
  components: {
    Pagination
  },
  data() {
    return {
      // 当前模式：batch-批次下载，label-标签下载
      activeMode: 'batch',
      // 显示搜索条件
      showSearch: true,
      // 客户列表
      customers: [],

      // 批次下载相关数据
      batchLoading: true,
      batchList: [],
      batchTotal: 0,
      batchQueryParams: {
        pageNum: 1,
        pageSize: 10,
        id: '',
        custBm: '',
        handleStatus: '2' // 默认只显示处理完成的批次
      },

      // 标签下载相关数据
      labelLoading: false,
      labelList: [],
      labelTotal: 0,
      labelQueryParams: {
        pageNum: 1,
        pageSize: 10,
        taskId: '',
        dataNo: '',
        serialNumber: '',
        seqNo: '',
        custMatNo: '',
        makerMatNo: '',
        custColorCode: ''
      },
      selectedLabels: [],

      // 待打印列表相关数据
      cartList: [],
      showCartDialog: false,
      showBatchAddDialog: false,

      // 批量添加待确认的标签
      pendingAddLabels: []
    }
  },
  computed: {
    // 待打印列表数量
    cartCount() {
      return this.cartList.length
    }
  },
  created() {
    this.getCustomers()
    this.getBatchList()
  },
  methods: {
    /** 获取客户列表 */
    getCustomers() {
      listCustomer().then(response => {
        this.customers = response.rows
      })
    },

    /** 模式切换 */
    handleModeChange(mode) {
      if (mode === 'batch') {
        this.getBatchList()
      } else {
        // 标签下载模式不自动查询，需要用户输入检索条件
        this.labelList = []
        this.labelTotal = 0
      }
    },

    // ========== 批次下载相关方法 ==========

    /** 查询批次列表 */
    getBatchList() {
      this.batchLoading = true
      listTask(this.batchQueryParams).then(response => {
        this.batchList = response.rows
        this.batchTotal = response.total
        this.batchLoading = false
      }).catch(() => {
        this.batchLoading = false
      })
    },

    /** 批次搜索 */
    handleBatchQuery() {
      this.batchQueryParams.pageNum = 1
      this.getBatchList()
    },

    /** 重置批次搜索 */
    resetBatchQuery() {
      this.$refs.batchQueryForm.resetFields()
      this.batchQueryParams.handleStatus = '2'
      this.handleBatchQuery()
    },

    /** 批次下载 */
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

    // ========== 标签下载相关方法 ==========

    /** 检查是否有检索条件 */
    hasSearchConditions() {
      const { taskId, dataNo, serialNumber, seqNo, custMatNo, makerMatNo, custColorCode } = this.labelQueryParams
      return !!(taskId || dataNo || serialNumber || seqNo || custMatNo || makerMatNo || custColorCode)
    },

    /** 查询标签列表 */
    getLabelList() {
      // 检查是否有检索条件，如果没有则不调用接口
      if (!this.hasSearchConditions()) {
        this.labelList = []
        this.labelTotal = 0
        this.$message.warning('请至少输入一个检索条件')
        return
      }

      this.labelLoading = true
      listLabelDetail(this.labelQueryParams).then(response => {
        this.labelList = response.rows || []
        this.labelTotal = response.total || 0
        this.labelLoading = false
      }).catch(() => {
        this.labelList = []
        this.labelTotal = 0
        this.labelLoading = false
      })
    },

    /** 标签搜索 */
    handleLabelQuery() {
      this.labelQueryParams.pageNum = 1
      this.getLabelList()
    },

    /** 重置标签搜索 */
    resetLabelQuery() {
      this.$refs.labelQueryForm.resetFields()
      this.labelList = []
      this.labelTotal = 0
    },

    /** 标签选择变化 */
    handleLabelSelectionChange(selection) {
      this.selectedLabels = selection
    },

    // ========== 购物车相关方法 ==========

    /** 添加到待打印列表 */
    addToCart(label) {
      if (this.isInCart(label)) {
        this.$message.warning('该标签已在待打印列表中')
        return
      }
      this.cartList.push({ ...label })
      this.$message.success('已添加到待打印列表')
    },

    /** 批量添加到待打印列表 */
    batchAddToCart() {
      if (this.selectedLabels.length === 0) {
        this.$message.warning('请先选择要添加的标签')
        return
      }

      // 过滤掉已经在待打印列表中的标签
      const newLabels = this.selectedLabels.filter(label => !this.isInCart(label))
      if (newLabels.length === 0) {
        this.$message.warning('所选标签已在待打印列表中')
        return
      }

      this.pendingAddLabels = newLabels
      this.showBatchAddDialog = true
    },

    /** 确认批量添加 */
    confirmBatchAdd() {
      this.cartList.push(...this.pendingAddLabels)
      this.showBatchAddDialog = false
      this.pendingAddLabels = []
      this.$message.success(`已添加 ${this.pendingAddLabels.length} 个标签到待打印列表`)
    },

    /** 从待打印列表移除 */
    removeFromCart(index) {
      this.cartList.splice(index, 1)
    },

    /** 清空待打印列表 */
    clearCart() {
      this.$confirm('确定要清空待打印列表吗？', '清空确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.cartList = []
        this.$message.success('待打印列表已清空')
      }).catch(() => {
        // 取消清空
      })
    },

    /** 判断标签是否在待打印列表中 */
    isInCart(label) {
      const taskId = label.taskId || label.id
      return this.cartList.some(item => {
        const itemTaskId = item.taskId || item.id
        return itemTaskId === taskId &&
               item.dataNo === label.dataNo &&
               item.serialNumber === label.serialNumber
      })
    },

    /** 打印 */
    handlePrint() {
      if (this.cartCount === 0) {
        this.$message.warning('待打印列表为空，请先添加标签')
        return
      }

      // 显示加载中效果
      const loading = this.$loading({
        lock: true,
        text: '打印任务提交中，请稍候...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })

      // 提取打印数据：每个元素包括taskId和dataNo属性
      const printData = this.cartList.map(item => ({
        taskId: item.taskId || item.id,
        dataNo: item.dataNo
      }))

      // 调用打印接口
      printLabels(printData).then(response => {
        // 处理文件流下载
        const blob = new Blob([response])
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        // 生成文件名：包含当前日期和时间
        const now = new Date()
        const timestamp = now.toISOString().replace(/[-:.]/g, '').substring(0, 14)
        a.download = `labels_${timestamp}.pdf`
        a.style.display = 'none'
        document.body.appendChild(a)
        a.click()
        // 清理
        window.URL.revokeObjectURL(url)
        document.body.removeChild(a)
        
        this.$message.success('标签文件下载成功')
        this.showCartDialog = false
        // 清空待打印列表
        this.cartList = []
      }).catch(error => {
        this.$message.error('打印失败：' + (error.message || '未知错误'))
      }).finally(() => {
        // 关闭加载中效果
        loading.close()
      })
    },

    // ========== 工具方法 ==========

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
    }
  }
}
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.item {
  margin-top: 8px;
}
</style>
