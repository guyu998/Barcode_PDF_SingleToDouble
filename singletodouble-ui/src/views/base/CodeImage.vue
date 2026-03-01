
<template>
    <div class="app-container">
      <el-row :gutter="10" class="mb8" style="width:100%;">
        <el-col :span="1.5">
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
            :http-request="handleFileUpload"
          >
            <el-button type="primary" plain icon="el-icon-upload" size="mini" >点击上传</el-button>
          </el-upload>          
        </el-col>
        <right-toolbar :search="false" @queryTable="getList"></right-toolbar>        
      </el-row>

      <el-row :gutter="10" style="margin-top:5px;">
        <div style="height:800px;overflow-y: auto;">
        <el-col :span="4" v-for="(item, index) in images" :key="index">
          <el-card shadow="hover" style="margin-bottom: 10px;width: 150px;height: 170px;">
            <img :src="`${item.url}?t=${new Date().getTime()}`"   @click="viewImage(item.url)" alt="Image" style="object-fit:fill;width: 110px;height: 100px;">
            <div style="padding: 4px;">
              <span style="font-size:12px">{{ item.name }}</span>
              <div class="bottom clearfix" style="margin-top:0px">
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-delete"
                  @click.stop="removeImage(item.name)"
                >删除</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
    </div>
      </el-row>
  
       <el-dialog :visible.sync="dialogVisible" width="80%">
      <div class="image-container">
        <img :src="`${currentImageUrl}?t=${new Date().getTime()}`" alt="Current Image" class="responsive-image">
      </div>
    </el-dialog>
    </div>
  </template>
  
  <script>
  import { listCodeimage, uploadCodeimage,delCodeimage } from '@/api/system/base';
  
  export default {
    data() {
      return {
        // 遮罩层
        loading: true,
        pageNum: 1,
        pageSize: 20,        
        // 总条数
        total: 0,             
        // 显示搜索条件
        showSearch: false,        
        images: [
        ],
        fileList: [],
        dialogVisible: false,
        currentImageUrl: ''
      };
    },
    created() {
      this.getList();
    },
    methods: {
      getList() {
        listCodeimage().then(response => {
          this.images = response.data;
        });
      },
      viewImage(url) {
        this.currentImageUrl = url;
        this.dialogVisible = true;
      },
      removeImage(index) {
        this.$modal.confirm('是否确认删除此背番图片？').then(function() {
          return delCodeimage(index);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});          
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
      handleExceed(files, fileList) {
        this.$modal.msgWarning(
          `当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`
        );
      },
      handleChange1(){},
      handleChange(file) {
        // 返回 false 可以阻止默认的上传行为
        console.log(file)
        this.uploadFile(file).then(() => {
          this.getList(); // 上传后重新获取图片列表
          return false;
        }).catch(() => {
          return false;
        });
      },
      async uploadFile(file) {
        try {
          // 创建 FormData 对象
          const formData = new FormData();
          formData.append('file', file); // 假设后端接收的参数名为 'file'
  
          // 发送上传请求
          await uploadCodeimage(formData);
  
          this.$modal.msgSuccess('上传成功');
        } catch (error) {
        //   console.error(error);
          this.$modal.msgError('上传失败');
        }
      },
      // 处理文件上传操作
      handleFileUpload(file) {
      		this.loading = true;
            const formData = new FormData();
            formData.append('file', file.file); 
          // 调用后端服务器的接口
          uploadCodeimage(formData).then((resp) => {
            this.getList();
            this.$modal.msgSuccess('上传成功');
      		}).catch((e) => {
      			// this.$message.error(e.message);
                this.getList();
      		})
      }
    }
  };
  </script>
  
  <style scoped>
  .el-card__body {
    padding: 5px 10px 10px 10px;
}
  .bottom {
    margin-top: 13px;
    line-height: 12px;
  }
  .image-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh; /* 使容器高度占屏幕的90% */
}

.responsive-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}
  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }
  .clearfix:after {
    clear: both
  }
  </style>