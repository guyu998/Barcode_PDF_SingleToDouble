
<template>
    <div class="register">
      <el-form ref="resetForm" :model="resetForm" :rules="resetRules" class="register-form">
        <h3 class="title">修改密码</h3>
   
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="resetForm.oldPassword" placeholder="请输入旧密码" type="password" show-password/>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="resetForm.newPassword" placeholder="8位由数字、大小写字母、符号组成" type="password" show-password/>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="resetForm.confirmPassword" placeholder="请确认密码" type="password" show-password/>
        </el-form-item>
   
        <!--<el-form-item prop="code" v-if="captchaOnOff">
          <el-input
            v-model="resetForm.code"
            auto-complete="off"
            placeholder="验证码"
            style="width: 63%"
          >
            <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
          </el-input>
          <div class="register-code">
            <img :src="codeUrl" @click="getCode" class="register-code-img"/>
          </div>
        </el-form-item>-->
        <el-form-item style="width:100%;">
          <el-button
            :loading="loading"
            size="medium"
            type="primary"
            style="width:100%;"
            @click.native.prevent="handleReset"
          >
            <span v-if="!loading">确认修改</span>
          </el-button>
        </el-form-item>
      </el-form>
      <!--  底部  -->
      <div class="el-register-footer">
        <span>Copyright © 2018-2021 ruoyi.vip All Rights Reserved.</span>
      </div>
    </div>
  </template>
   
  <script>
  import { getCodeImg } from "@/api/login";
  import { resetUserProfilePwd } from "@/api/system/user";
  import { setToken } from '@/utils/auth'
   
  export default {
    name: "Reset",
    data() {

    const checkPassword = (rule, value, callback) => {
      if(!/(^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,}$)|(^(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$)|(^(?=.*[a-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$)|(^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$)/.test(value)){
        //|| !/[`~!@#$%^&*()\-=_+,.?<>/;':"\[\]{}|\\]+/.test(value)) {
        callback(new Error("密码必须长度必须大于7位，且包含大小写字母、数字和特殊字符"));
      }else{
        callback();
      }
    };
    const equalToPassword = (rule, value, callback) => {
      if (this.resetForm.newPassword !== value) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };

      return {
        codeUrl: "",
        resetForm: {
          username: "",
          oldPassword: "",
          newPassword: "",
          confirmPassword: "",
          code: "",
          sign: "",
          uuid: ""
        },
        resetRules: {
          oldPassword: [
            { required: true, trigger: "blur", message: "旧密码不能为空" },
          ],
          newPassword: [
            { required: true, message: "新密码不能为空", trigger: "blur" },
            { min: 8, max: 20, message: "长度在 8 到 20 个字符", trigger: "blur" },
            { required: true, validator: checkPassword, trigger: "blur" }
          ],
          confirmPassword: [
            { required: true, message: "确认密码不能为空", trigger: "blur" },
            { required: true, validator: equalToPassword, trigger: "blur" }
          ],
          //code: [{ required: true, trigger: "change", message: "请输入验证码" }]
        },
        loading: false,
        captchaOnOff: true
      };
    },
    created() {
      //this.getCode();
    },
    mounted() {
      // 先清除token，防止回退后能直接登录，从而绕过强制重置密码的逻辑
      setToken('');
      // 获取当前链接的参数
      const params = this.$route.query;
      this.resetForm.sign = params.sign;
      this.resetForm.username = params.username;
      this.token = params.token;
    },
    methods: {
      getCode() {
        getCodeImg().then(res => {
          this.captchaOnOff = res.captchaOnOff === undefined ? true : res.captchaOnOff;
          if (this.captchaOnOff) {
            this.codeUrl = "data:image/gif;base64," + res.img;
            this.resetForm.uuid = res.uuid;
          }
        });
      },
      handleReset() {
        this.$refs["resetForm"].validate(valid => {
          if (valid) {
            this.loading = true;
            // 获取并设置从登录页拿到的token，调用接口需要登录成功的token，否则会提示过期
            setToken(localStorage.getItem("reset_token"));
            resetUserProfilePwd(this.resetForm).then(res => {
              this.$alert("<font color='green'>修改成功，请重新登录</font>", '系统提示', {
                dangerouslyUseHTMLString: true,
                type: 'success'
              }).then(() => {
                //删除登录成功设置的token
                localStorage.removeItem("reset_token");
                //清除token，强制登录
                setToken("")
                // 跳转到登录页
                this.$router.push("/login");
              }).catch(() => {});
            }).catch(() => {
              this.loading = false;
              if (this.captchaOnOff) {
                //this.getCode();
              }
            })
          }
        })
      }
    }
  };
  </script>
   
  <style rel="stylesheet/scss" lang="scss">
  .register {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    background-image: url("../assets/images/login-background.jpg");
    background-size: cover;
  }
  .title {
    margin: 0px auto 30px auto;
    text-align: center;
    color: #707070;
  }
   
  .register-form {
    border-radius: 6px;
    background: #ffffff;
    width: 400px;
    padding: 25px 25px 5px 25px;
    .el-input {
      height: 38px;
      input {
        height: 38px;
      }
    }
    .input-icon {
      height: 39px;
      width: 14px;
      margin-left: 2px;
    }
  }
  .register-tip {
    font-size: 13px;
    text-align: center;
    color: #bfbfbf;
  }
  .register-code {
    width: 33%;
    height: 38px;
    float: right;
    img {
      cursor: pointer;
      vertical-align: middle;
    }
  }
  .el-register-footer {
    height: 40px;
    line-height: 40px;
    position: fixed;
    bottom: 0;
    width: 100%;
    text-align: center;
    color: #fff;
    font-family: Arial;
    font-size: 12px;
    letter-spacing: 1px;
  }
  .register-code-img {
    height: 38px;
  }
  </style>