<template>
    <el-dialog title="添加/编辑检查项目" :visible.sync="dialogVisible" width="50%" @close="handleClose">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="检查项目名" prop="typeName">
          <el-input v-model="form.typeName" maxlength="32"></el-input>
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="form.inputType" :disabled="form.id != ''">
            <el-radio :label="1">选项</el-radio>
            <el-radio :label="2">范围</el-radio>
            <el-radio :label="3">文字</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.inputType === 1" label="检查选项" style="max-height: 400px;overflow-y: auto;">
          <el-row v-for="(optionItem, optionIndex) in form.propList" :key="optionIndex" type="flex" align="middle" style="margin-bottom:3px;">
            <el-col :span="10">
              <el-input v-model="optionItem.propName" placeholder="选项名" maxlength="32"/>
            </el-col>
            <el-col :span="2">
              <el-button type="danger" style="margin-left:4px;" icon="el-icon-minus" size="mini" @click="removeOption(optionIndex)"></el-button>
            </el-col>
          </el-row>
          <el-button type="primary" icon="el-icon-plus" size="mini" @click="addOption"></el-button>
        </el-form-item>
        <el-form-item v-if="form.inputType === 2" label="范围">
          <el-row v-for="(rangeItem, rangeIndex) in form.propList" :key="rangeIndex" type="flex" align="middle" style="margin-bottom:3px;">
            <el-col :span="4">
                <el-input-number controls-position="right" size='small' v-model="rangeItem.min"  maxlength="5" placeholder="最小值" />
            </el-col>
            <el-col :span="2" class="text-center">~</el-col>
            <el-col :span="4">
              <el-input-number controls-position="right" size='small' v-model="rangeItem.max"  maxlength="5" placeholder="最大值" />
            </el-col>
            <el-col :span="2">
              <el-button type="danger" style="margin-left:4px;" icon="el-icon-minus" size="mini" @click="removeRange(rangeIndex)"></el-button>
            </el-col>
          </el-row>
          <el-button type="primary" icon="el-icon-plus" size="mini" @click="addRange"></el-button>
        </el-form-item>
        <el-form-item v-if="form.inputType === 3" label="检查文字" style="max-height: 400px;overflow-y: auto;">
          <el-row v-for="(optionItem, optionIndex) in form.propList" :key="optionIndex" type="flex" align="middle" style="margin-bottom:3px;">
            <el-col :span="10">
              <el-input v-model="optionItem.propName" placeholder="" maxlength="32"/>
            </el-col>
            <el-col :span="2">
              <el-button type="danger" style="margin-left:4px;" icon="el-icon-minus" size="mini" @click="removeOption(optionIndex)"></el-button>
            </el-col>
          </el-row>
          <el-button type="primary" icon="el-icon-plus" size="mini" @click="addOption"></el-button>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">保 存</el-button>
      </span>
    </el-dialog>
  </template>
  
  <script>
  import { addAppearanceType ,updateAppearanceType} from "@/api/system/base";
  export default {
    data() {
      return {
        dialogVisible: false,
        form: {
          id: '',
          typeName: '',
          inputType: 1,
          useStatus:'1',
          propList: [{ propName: '', propId: '' }],
        },
        rules: {
          typeName: [{ required: true, message: '请输入检查项目名称', trigger: 'blur' }],
        }
      };
    },
    methods: {
      show(obj) {
        this.dialogVisible = true;
        if (obj == null) {
          this.form = {
            id: '',
            typeName: '',
            inputType: 1,
            useStatus:'1',
            propList: [{ propName: '', propId: '' }],
          };
        } else {
          this.form = {
            ...obj,
            propList: obj.propList.map(item => {
              const newItem = { 
                propId: item.propId || '', // 保留propId
                ...(obj.inputType === 2 && typeof item.propName === 'string' ? {
                  min: parseFloat(item.propName.split('~')[0].trim()),
                  max: parseFloat(item.propName.split('~')[1].trim())
                } : { propName: item.propName })
              };
              return newItem;
            })
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
            // 处理范围的 propList 格式
            if (this.form.inputType === 2) {
                this.form.propList.forEach(item => {
                    item.propName = `${item.min}~${item.max}`;
                });
            }
            if (this.form.id == ''){
                addAppearanceType(this.form).then(response => {
                  this.$modal.msgSuccess("新增成功");
                  this.dialogVisible = false;
                  this.$emit('submit');
                }).catch(() => {});
            } else {
                updateAppearanceType(this.form).then(response => {
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
      },
      addOption() {
        this.form.propList.push({ propName: '', propId: '' });
      },
      removeOption(index) {
        this.form.propList.splice(index, 1);
      },
      addRange() {
        this.form.propList.push({ min: '', max: '' });
      },
      removeRange(index) {
        this.form.propList.splice(index, 1);
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