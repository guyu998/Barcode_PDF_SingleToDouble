<template>
    <el-dialog title="添加/编辑工程" :visible.sync="dialogVisible" width="50%" @close="handleClose">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="工程编号" prop="projectCode">
          <el-input v-model="form.projectCode"  maxlength="6" style="width:100px;" :disabled="form.id != ''"></el-input>
        </el-form-item>
        <el-form-item label="工程名" prop="projectName">
          <el-input v-model="form.projectName"  maxlength="32"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">保 存</el-button>
      </span>
    </el-dialog>
  </template>
  
  <script>
  import { addProjectType ,updateProjectType} from "@/api/system/base";
  export default {
    data() {
      return {
        dialogVisible: false,
        form: {
          id:'',
          projectCode: '',
          projectName: '',
          useStatus:'1',
        },
        rules: {
          projectCode: [{ required: true, message: '请输入工程编号', trigger: 'blur' }],
          projectName: [{ required: true, message: '请输入工程名', trigger: 'blur' }],
        }
      };
    },
    methods: {
      show(obj) {
        this.dialogVisible = true;
        if (obj == null) {
          this.form = {
            id:'',
            projectCode: '',
            projectName: '',
            useStatus:'1',
          };
        } else {
          this.form = {
            id:obj.projectCode,
            ...obj
          };
          console.log(this.form)
        }
      },
      handleClose(done) {
        this.$emit('submit');
      },
      submitForm() {
        this.$refs['form'].validate(valid => {
          if (valid) {
            if (this.form.id == '') {
              addProjectType(this.form).then(response => {
                this.$modal.msgSuccess("新增成功");
                this.dialogVisible = false;
                this.$emit('submit');
              }).catch(() => {});
            } else {
              updateProjectType(this.form).then(response => {
                this.$modal.msgSuccess("更新成功");
                this.dialogVisible = false;
                this.$emit('submit');
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
  /* 可选：设置输入框宽度一致 */
  .el-input {
    width: 100%;
  }
  </style>