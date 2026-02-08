<template>
  <el-form ref="form" :model="user" :rules="rules" label-width="80px">
    <el-form-item label="旧密码" prop="oldPassword">
      <el-input v-model="user.oldPassword" placeholder="请输入旧密码" type="password" show-password/>
    </el-form-item>
    <el-form-item label="新密码" prop="newPassword">
      <el-input v-model="user.newPassword" placeholder="请输入新密码" type="password" show-password/>
    </el-form-item>
    <el-form-item label="确认密码" prop="confirmPassword">
      <el-input v-model="user.confirmPassword" placeholder="请确认新密码" type="password" show-password/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="mini" @click="submit">保存</el-button>
      <el-button type="danger" size="mini" @click="close">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { updateUserPwd } from "@/api/system/user";

export default {
  data() {
    const checkPassword = (rule, value, callback) => {
      /*if(!/[A-Z]+/.test(value) || !/[a-z]+/.test(value) || !/\d+/.test(value)){
        //|| !/[`~!@#$%^&*()\-=_+,.?<>/;':"\[\]{}|\\]+/.test(value)) {
        callback(new Error("密码必须长度必须大于8位，且包含大小写字母和数字"));
      }else{
        callback();
      }*/
      if(!/(^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,}$)|(^(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$)|(^(?=.*[a-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$)|(^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$)/.test(value)){
        //|| !/[`~!@#$%^&*()\-=_+,.?<>/;':"\[\]{}|\\]+/.test(value)) {
        callback(new Error("密码必须长度必须大于7位，且包含大小写字母、数字和特殊字符"));
      }else{
        callback();
      }
    };
    const equalToPassword = (rule, value, callback) => {
      if (this.user.newPassword !== value) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };
    return {
      user: {
        oldPassword: undefined,
        newPassword: undefined,
        confirmPassword: undefined
      },
      // 表单校验
      rules: {
        oldPassword: [
          { required: true, message: "旧密码不能为空", trigger: "blur" }
        ],
        // newPassword: [
        //   { required: true, message: "新密码不能为空", trigger: "blur" },
        //   { min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" }
        // ],
        newPassword: [
          { required: true, message: "新密码不能为空", trigger: "blur" },
          { min: 8, max: 20, message: "长度在 8 到 20 个字符", trigger: "blur" },
          { required: true, validator: checkPassword, trigger: "blur" }
        ],
        confirmPassword: [
          { required: true, message: "确认密码不能为空", trigger: "blur" },
          { required: true, validator: equalToPassword, trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    submit() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          updateUserPwd(this.user.oldPassword, this.user.newPassword).then(response => {
            this.$modal.msgSuccess("修改成功");
          });
        }
      });
    },
    close() {
      this.$tab.closePage();
    }
  }
};
</script>
