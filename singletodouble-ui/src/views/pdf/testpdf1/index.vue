<template>
  <div>
      <div style="text-align: left;">
      <H2>行业资料</H2>
      <el-divider>
      </el-divider>
      <el-container>
          <el-aside width="300px" style="border: 1px solid #eee;height: 1000px; background-color: #D3DCE6;">
              <div class="myTree">
              <el-tree default-expand-all :props="defaultProps" :data="tableData"  @node-click="handleNodeClick"></el-tree>
              </div>
          </el-aside>
          <el-container  style="border: 1px solid #eee;margin-left: 10px;">
              <div class="pdf" v-show="fileType === 'pdf'">
                <pdf
        :page="pageNum"
        :src="src"
        @progress="loadedRatio = $event"
        @num-pages="pageTotalNum = $event"
      />
      <!--  -->
      <div class="pdf-btn" v-if="pageTotalNum > 1">
        <button round type="info" size="small" @click="prePage"
          >上一页</button
        >
        <span class="pdf-span">{{ pageNum }}</span>
        <span>/</span>
        <span> {{ pageTotalNum }}</span>
        <button round type="info" size="small" @click="nextPage"
          >下一页</button
        >
      </div>

              </div>
          </el-container>
          <el-button @click="test">测试</el-button>
      </el-container>
      </div>
  </div>
</template>

<script>
import pdf from 'vue-pdf'
import { getData,getVDrawingAnnex } from "@/api/pdf/test1PDF";

export default {
  name: "IndustryInformation",
  components: {pdf},
  data(){
    return {
      currentPage: 0, // pdf文件页码
      pageCount: 0, // pdf文件总页数
      fileType: 'pdf', // 文件类型
      pdfSrc: '',  // pdf文件地址
      src: "",//文件地址
      pageNum: 1, //当前页
      pageTotalNum: 1, //默认总页数
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      tableData: [{
        id: 1,
        name: '道路工程资料',
        children:[
          {
            id: 2,
            name: '公路工程资料编制概述',
            children:[
              {
                id: 21,
                name: '路面工程部分分项划分表',
                src: '/test_20230523.pdf',
              },
              {
                id: 13,
                name: '一般建设项目单位工程划分表',
                src: '/test_20200707.pdf',
              },
              {
                id: 14,
                name: '路基工程部分分项划分表',
                src: '/3.pdf',
              },
              {
                id: 33,
                name: '桥梁工程部分分项划分表',
                src: '/4.pdf',
              },
              {
                id: 34,
                name: '隧道工程部分分项划分表',
                src: '/5.pdf',
              }
            ]
          },
          {
            id: 3,
            name: '公路工程竣工资料',
            children:[
              {
                id: 7,
                name: '公路工程竣工文件编排层次',
                src: '/5.pdf',
              },
              {
                id: 8,
                name: '工程洽商记录表',
                src: '/4.pdf',
              },
              {
                id: 9,
                name: '工程设计表更、洽商一览表',
                src: '/3.pdf',
              }
            ]
          }
        ]}]
    }
  },
  methods:{
    // 改变PDF页码,val传过来区分上一页下一页的值,0上一页,1下一页
    changePdfPage(val) {
      // console.log(val)
      if (val === 0 && this.currentPage > 1) {
        this.currentPage--
        // console.log(this.currentPage)
      }
      if (val === 1 && this.currentPage < this.pageCount) {
        this.currentPage++
        // console.log(this.currentPage)
      }
    },

    test(){
      var param = {id:'1',partNumber:'',params:{beginPutDate:'2024-01-03',endPutDate:'2024-01-05'}};
        var that = this;
      getVDrawingAnnex("6").then((res) => {
         that.src =window.URL.createObjectURL(new Blob([res]));
        that.$modal.msgSuccess("成功");
      });
    },
    // pdf加载时
    loadPdfHandler(e) {
      this.currentPage = 1 // 加载的时候先加载第一页
    },

    handleNodeClick(data) {
      this.pdfSrc = data.src;
    },
    //上一页
    prePage() {
      let page = this.pageNum;
      page = page > 1 ? page - 1 : this.pageTotalNum;
      this.pageNum = page;
    },
    //下一页
    nextPage() {
      let page = this.pageNum;
      page = page < this.pageTotalNum ? page + 1 : 1;
      this.pageNum = page;
    },

  }
}
</script>

<style scoped>
  .myTree /deep/ .el-tree {
      position: relative;
      cursor: default;
      color: #606266;
      background-color: #D3DCE6;
  }
</style>