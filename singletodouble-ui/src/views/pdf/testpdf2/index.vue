<template>
  <div>
    <div>
      <el-button size="small" type="primary" @click="changePdfPage(0)" class="turn"
        :class="{ grey: currentPage == 1 }">上一页</el-button>
      {{ currentPage }} / {{ pageCount }}
      <el-button size="small" type="primary" @click="changePdfPage(1)" class="turn"
        :class="{ grey: currentPage == pageCount }">下一页</el-button>
    </div>
    <div class="content" ref="printContent">
      <!--<pdf id="myIframe" ref="pdfDom" :src="pdfSrc" :page="currentPage" @num-pages="pageCount = $event"
        @page-loaded="currentPage = $event" @loaded="loadPdfHandler">
      </pdf>-->


      <pdf  v-for="i in numPages" :key="i"  :src="url" :page="i"></pdf>	
    </div>
  </div>
</template>
<script>
import pdf from 'vue-pdf'
import { getData } from "@/api/pdf/test1PDF";
// 然后在component中进行注册

export default {
  name: 'test',
  data() {
    return {
      pdfSrc: '', // pdf文件src
      pageCount: 0, // pdf文件总页数
      currentPage: 1, // pdf文件页码
      numPages:0,
      url:""
    }
  },
  components: { pdf },
  created() {
    this.getNumPages("http://storage.xuetangx.com/public_assets/xuetangx/PDF/PlayerAPI_v1.0.6.pdf") 
  },
  methods: {
    // 获取文件流和文件总页数
    async getFileInfo () {
      let formData = new FormData()
      formData.append('wjid', this.wjid)
      formData.append('yhid', this.userId)

      getPdf().then(response => {
        const blob = new Blob([response])
        this.pdfSrc = URL.createObjectURL(blob)
        }
      );


    },

    getNumPages(url) {
				var loadingTask = pdf.createLoadingTask(url)
				loadingTask.promise.then(pdf => {
					this.url = loadingTask
					this.numPages = pdf.numPages
				}).catch((err) => {
					console.error('pdf加载失败')
				})
			},

    // 改变PDF页码,val传过来区分上一页下一页的值,0上一页,1下一页
    changePdfPage(val) {
      if (val === 0 && this.currentPage > 1) {
        this.currentPage--
      }
      if (val === 1 && this.currentPage < this.pageCount) {
        this.currentPage++
      }
    },

    // pdf加载时
    loadPdfHandler(e) {
      this.currentPage = 1 // 加载的时候先加载第一页
    }

  }
}
</script>

<style></style>
