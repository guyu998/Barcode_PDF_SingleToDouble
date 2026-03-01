<template>
  <div class="app-container">
    <!-- 上传按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-upload" size="mini" @click="handleUpload">上传物料文件</el-button>
      </el-col>
    </el-row>

    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="制造商物料编码" prop="makerMatNo">
        <el-input v-model="queryParams.makerMatNo" placeholder="请输入制造商物料编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="客户物料编码" prop="custMatNo">
        <el-input v-model="queryParams.custMatNo" placeholder="请输入客户物料编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="物料名称" prop="makerMatName">
        <el-input v-model="queryParams.makerMatName" placeholder="请输入物料名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="客户" prop="custBm">
        <el-select v-model="queryParams.custBm" placeholder="请选择客户" clearable>
          <el-option
            v-for="customer in customers"
            :key="customer.customerCode"
            :label="customer.customerName"
            :value="customer.customerCode"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="materialList"
      style="width: 100%"
    >
      <el-table-column label="制造商物料编码" prop="makerMatNo" align="center" width="120"/>
      <el-table-column label="客户物料编码" prop="custMatNo" align="center" />
      <el-table-column label="物料名称" prop="makerMatName" align="center" />
      <el-table-column label="物料简码" prop="makerBackNo" align="center" />
      <el-table-column label="客户" prop="custShortName" align="center" />
      <el-table-column label="颜色" prop="makerColorCode" align="center" />
      <el-table-column label="收容数" prop="makerSnp" align="center" />
      <el-table-column label="计量单位" prop="makerCalUnit" align="center" />
      <el-table-column label="使用标识" prop="makerUseFlag" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.makerUseFlag === 1 ? 'success' : 'danger'">
            {{ scope.row.makerUseFlag === 1 ? '使用中' : '未使用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">详细</el-button>
          <el-button size="mini" type="text" icon="el-icon-s-grid" @click="handleBOM(scope.row)">BOM</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 上传对话框 -->
    <el-dialog title="上传物料文件" :visible.sync="uploadDialogVisible" width="500px" append-to-body>
      <el-form ref="uploadForm" :model="uploadForm" label-width="100px">
        <el-form-item label="选择文件" prop="file">
          <el-upload
            class="upload-demo"
            ref="fileUpload"
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-exceed="handleExceed"
            :limit="1"
            :file-list="fileList"
            accept=".xlsx,.xls"
            :http-request="handleHttpRequest"
          >
            <el-button slot="trigger" type="primary">选取Excel文件</el-button>
            <div slot="tip" class="el-upload__tip">
              只能上传Excel文件，且不超过5MB
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpload">确定上传</el-button>
      </div>
    </el-dialog>

    <!-- 详细信息对话框 -->
    <el-dialog title="物料详细信息" :visible.sync="detailDialogVisible" width="80%" append-to-body>
      <div class="dialog-content-scroll">
        <el-form ref="detailForm" :model="detailForm" label-width="150px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="制造商编码">
                <el-input v-model="detailForm.makerBm" disabled />
              </el-form-item>
              <el-form-item label="制造商简称">
                <el-input v-model="detailForm.makerShortName" disabled />
              </el-form-item>
              <el-form-item label="制造商物料编码">
                <el-input v-model="detailForm.makerMatNo" disabled />
              </el-form-item>
              <el-form-item label="制造商物料简码">
                <el-input v-model="detailForm.makerBackNo" disabled />
              </el-form-item>
              <el-form-item label="制造商物料名称">
                <el-input v-model="detailForm.makerMatName" disabled />
              </el-form-item>
              <el-form-item label="制造商物料名称(英)">
                <el-input v-model="detailForm.makerMatNameEn" disabled />
              </el-form-item>
              <el-form-item label="制造商颜色代码">
                <el-input v-model="detailForm.makerColorCode" disabled />
              </el-form-item>
              <el-form-item label="制造商颜色">
                <el-input v-model="detailForm.makerColor" disabled />
              </el-form-item>
              <el-form-item label="制造商收容数">
                <el-input v-model="detailForm.makerSnp" disabled />
              </el-form-item>
              <el-form-item label="制造商计量单位">
                <el-input v-model="detailForm.makerCalUnit" disabled />
              </el-form-item>
              <el-form-item label="制造商采购单位">
                <el-input v-model="detailForm.makerBuyUnit" disabled />
              </el-form-item>
              <el-form-item label="制造商规格型号">
                <el-input v-model="detailForm.makerSpecModel" disabled />
              </el-form-item>
              <el-form-item label="制造商物料分类">
                <el-input v-model="detailForm.makerMatCatg" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="客户编码">
                <el-input v-model="detailForm.custBm" disabled />
              </el-form-item>
              <el-form-item label="客户简称">
                <el-input v-model="detailForm.custShortName" disabled />
              </el-form-item>
              <el-form-item label="客户物料编码">
                <el-input v-model="detailForm.custMatNo" disabled />
              </el-form-item>
              <el-form-item label="客户物料简码">
                <el-input v-model="detailForm.custBackNo" disabled />
              </el-form-item>
              <el-form-item label="客户物料名称">
                <el-input v-model="detailForm.custMatName" disabled />
              </el-form-item>
              <el-form-item label="客户物料名称(英)">
                <el-input v-model="detailForm.custMatNameEn" disabled />
              </el-form-item>
              <el-form-item label="客户颜色代码">
                <el-input v-model="detailForm.custColorCode" disabled />
              </el-form-item>
              <el-form-item label="客户颜色">
                <el-input v-model="detailForm.custColor" disabled />
              </el-form-item>
              <el-form-item label="客户收容数">
                <el-input v-model="detailForm.custSnp" disabled />
              </el-form-item>
              <el-form-item label="客户计量单位">
                <el-input v-model="detailForm.custCalUnit" disabled />
              </el-form-item>
              <el-form-item label="客户采购单位">
                <el-input v-model="detailForm.custBuyUnit" disabled />
              </el-form-item>
              <el-form-item label="客户规格型号">
                <el-input v-model="detailForm.custSpecModel" disabled />
              </el-form-item>
              <el-form-item label="客户物料分类">
                <el-input v-model="detailForm.custMatCatg" disabled />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="制造商左右件">
                <el-input v-model="detailForm.makerLeftRightFlag" disabled />
              </el-form-item>
              <el-form-item label="制造商重保件">
                <el-input v-model="detailForm.makerImpProtectFlag" disabled />
              </el-form-item>
              <el-form-item label="制造商车型">
                <el-input v-model="detailForm.makerCarModel" disabled />
              </el-form-item>
              <el-form-item label="制造商车型用量">
                <el-input v-model="detailForm.makerCarPerQty" disabled />
              </el-form-item>
              <el-form-item label="制造商库位">
                <el-input v-model="detailForm.makerPlacePos" disabled />
              </el-form-item>
              <el-form-item label="制造商后工程">
                <el-input v-model="detailForm.makerPostProc" disabled />
              </el-form-item>
              <el-form-item label="制造商包装编码">
                <el-input v-model="detailForm.makerPackBm" disabled />
              </el-form-item>
              <el-form-item label="制造商托占比">
                <el-input v-model="detailForm.makerPackRate" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="客户左右件">
                <el-input v-model="detailForm.custLeftRightFlag" disabled />
              </el-form-item>
              <el-form-item label="客户重保件">
                <el-input v-model="detailForm.custImpProtectFlag" disabled />
              </el-form-item>
              <el-form-item label="客户车型">
                <el-input v-model="detailForm.custCarModel" disabled />
              </el-form-item>
              <el-form-item label="客户车型用量">
                <el-input v-model="detailForm.custCarPerQty" disabled />
              </el-form-item>
              <el-form-item label="客户库位">
                <el-input v-model="detailForm.custPlacePos" disabled />
              </el-form-item>
              <el-form-item label="客户后工程">
                <el-input v-model="detailForm.custPostProc" disabled />
              </el-form-item>
              <el-form-item label="客户包装编码">
                <el-input v-model="detailForm.custPackBm" disabled />
              </el-form-item>
              <el-form-item label="客户托占比">
                <el-input v-model="detailForm.custPackRate" disabled />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="制造商组合打托">
                <el-input v-model="detailForm.makerPackGroup" disabled />
              </el-form-item>
              <el-form-item label="制造商托标准箱数">
                <el-input v-model="detailForm.makerTrayStdBoxQty" disabled />
              </el-form-item>
              <el-form-item label="制造商初始库存类型">
                <el-input v-model="detailForm.makerInitStockType" disabled />
              </el-form-item>
              <el-form-item label="制造商标签颜色">
                <el-input v-model="detailForm.makerLabelColor" disabled />
              </el-form-item>
              <el-form-item label="制造商线别">
                <el-input v-model="detailForm.makerLine" disabled />
              </el-form-item>
              <el-form-item label="库存系数下限">
                <el-input v-model="detailForm.stockDown" disabled />
              </el-form-item>
              <el-form-item label="库存系数上限">
                <el-input v-model="detailForm.stockUp" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="客户组合打托">
                <el-input v-model="detailForm.custPackGroup" disabled />
              </el-form-item>
              <el-form-item label="客户标签颜色">
                <el-input v-model="detailForm.custLabelColor" disabled />
              </el-form-item>
              <el-form-item label="备货L/T">
                <el-input v-model="detailForm.prepareLeadTime" disabled />
              </el-form-item>
              <el-form-item label="出货L/T">
                <el-input v-model="detailForm.sendLeadTime" disabled />
              </el-form-item>
              <el-form-item label="制造商日历">
                <el-input v-model="detailForm.makerCal" disabled />
              </el-form-item>
              <el-form-item label="制造商线别编码">
                <el-input v-model="detailForm.makerLineBm" disabled />
              </el-form-item>
              <el-form-item label="物料排序">
                <el-input v-model="detailForm.matOrder" disabled />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- BOM对话框 -->
    <el-dialog title="BOM信息" :visible.sync="bomDialogVisible" width="600px" append-to-body>
      <el-form ref="bomForm" :model="bomForm" label-width="120px">
        <el-form-item label="制造商物料编码">
          <el-input v-model="bomForm.makerMatNo" disabled />
        </el-form-item>
        <el-form-item label="客户物料编码">
          <el-input v-model="bomForm.custMatNo" disabled />
        </el-form-item>
        <el-form-item label="颜色编码">
          <el-input v-model="bomForm.custColorCode" disabled />
        </el-form-item>
      </el-form>
      <el-table v-if="bomList.length > 0" :data="bomList" style="width: 100%" class="mt10">
        <el-table-column label="顺番号" prop="orderNo" align="center" />
        <el-table-column label="部品名" prop="partName" align="center" />
        <el-table-column label="背番号" prop="backNo" align="center" />
      </el-table>
      <div v-else class="mt10 text-center text-gray">
        暂无BOM信息
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="bomDialogVisible = false">关闭</el-button>
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
import { listCustomer, uploadMaterialFile, listMaterial, getMaterialDetail, listBOM } from '@/api/material'
import Pagination from '@/components/Pagination'

export default {
  name: 'MaterialManagement',
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
      // 物料表格数据
      materialList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        makerMatNo: '',
        custMatNo: '',
        makerMatName: '',
        custBm: ''
      },
      // 客户列表
      customers: [],
      // 上传对话框
      uploadDialogVisible: false,
      uploadForm: {},
      // 文件上传
      fileList: [],
      currentFile: null,
      // 上传进度
      showProgress: false,
      uploadProgress: 0,
      uploadStatus: '',
      // 上传结果
      showResult: false,
      resultMessage: '',
      resultType: '',
      // 详细信息对话框
      detailDialogVisible: false,
      detailForm: {},
      // BOM对话框
      bomDialogVisible: false,
      bomForm: {},
      bomList: []
    }
  },
  created() {
    this.getCustomers()
    this.getList()
  },
  methods: {
    /** 获取客户列表 */
    getCustomers() {
      listCustomer().then(response => {
        this.customers = response.rows
      })
    },
    /** 查询物料列表 */
    getList() {
      this.loading = true
      listMaterial(this.queryParams).then(response => {
        this.materialList = response.rows
        this.total = response.total
        this.loading = false
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
      this.fileList = []
      this.currentFile = null
    },
    /** 处理文件选择变化 */
    handleFileChange(file, fileList) {
      this.fileList = fileList
      this.currentFile = file
    },
    /** 处理文件超出限制 */
    handleExceed(files, fileList) {
      this.$message.warning(`当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`)
    },
    /** 提交上传 */
    submitUpload() {
      if (!this.currentFile) {
        this.$message.warning('请先选择要上传的Excel文件')
        return
      }

      this.showProgress = true
      this.uploadProgress = 0
      this.uploadStatus = ''
      this.showResult = false

      // 显示全局loading
      const loading = this.$loading({
        lock: true,
        text: '上传中，请稍候...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })

      // 模拟上传进度
      const progressInterval = setInterval(() => {
        if (this.uploadProgress < 90) {
          this.uploadProgress += 10
        }
      }, 500)

      // 准备上传数据
      const formData = new FormData()
      formData.append('file', this.currentFile.raw)

      // 调用上传API
      uploadMaterialFile(formData).then(response => {
        clearInterval(progressInterval)
        this.uploadProgress = 100
        this.uploadStatus = 'success'
        this.showResult = true
        this.resultMessage = '物料文件上传成功！'
        this.resultType = 'success'
        this.uploadDialogVisible = false
        
        // 刷新物料列表
        this.getList()
        
        // 关闭loading
        loading.close()
        
        // 3秒后隐藏结果提示
        setTimeout(() => {
          this.showResult = false
          this.showProgress = false
        }, 3000)
      }).catch(error => {
        clearInterval(progressInterval)
        this.uploadStatus = 'exception'
        this.showResult = true
        this.resultMessage = '物料文件上传失败：' + (error.message || '未知错误')
        this.resultType = 'error'
        
        // 关闭loading
        loading.close()
        
        // 3秒后隐藏结果提示
        setTimeout(() => {
          this.showResult = false
          this.showProgress = false
        }, 3000)
      })
    },
    /** 自定义上传处理 */
    handleHttpRequest() {
      // 自定义上传逻辑，由submitUpload方法处理
    },
    /** 处理详细按钮点击 */
    handleDetail(row) {
      this.detailDialogVisible = true
      // 使用新接口获取详细信息
      getMaterialDetail(row.makerMatNo, row.custMatNo,row.custColorCode).then(response => {
        this.detailForm = response.data
      }).catch(() => {
        // 如果接口调用失败，使用表格中的数据
        this.detailForm = { ...row }
      })
    },
    /** 处理BOM按钮点击 */
    handleBOM(row) {
      this.bomDialogVisible = true
      this.bomForm = {
        makerMatNo: row.makerMatNo,
        custMatNo: row.custMatNo,
        custColorCode:row.custColorCode
      }
      // 调用BOM接口获取数据
      this.getBOM(row.makerMatNo, row.custMatNo,row.custColorCode)
    },
    /** 获取BOM信息 */
    getBOM(makerMatNo, custMatNo,custColorCode) {
      listBOM({ makerMatNo, custMatNo,custColorCode }).then(response => {
        this.bomList = response.rows || []
      }).catch(() => {
        this.bomList = []
      })
    }
  }
}
</script>

<style scoped>
.fixed-width {
  width: 150px;
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

.dialog-content-scroll {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 10px;
}

.dialog-content-scroll::-webkit-scrollbar {
  width: 8px;
}

.dialog-content-scroll::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.dialog-content-scroll::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.dialog-content-scroll::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>