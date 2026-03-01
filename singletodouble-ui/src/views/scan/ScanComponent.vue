<template>
  <div id="app" style="background-color: darkgrey; width: 100%; height: 100vh; overflow-y: hidden; overflow-x: hidden;">
    <div class="container">
      <div class="db-status" >
        <span  :class="{ 'connected': isConnected, 'disconnected': !isConnected }">{{ isConnected ? '流水线：' + lineName : '通讯状态：未连接' }}</span>
        <el-button id="fullButton" style="padding: 10px 10px;margin-left:15px" @click="handleExitAction" :icon="isFullscreen?'el-icon-circle-close':'el-icon-full-screen'" round></el-button>
      </div>
      <h1 ref="title" style="font-size:50px;color:red; border-bottom: 3px solid;padding-bottom: 1px;">组立生产部初/终物品质检查系统</h1>
      <div class="board-container">
        <el-row style="width: 100%;display: flex;">
        <!-- 看板图片，固定左半边宽度 -->
        <div class="board-image">
          <div style="height:600px">
           <img :src="`${boardInfo.image || logo}?t=${new Date().getTime()}`" alt="看板图片" @click="toggleFullscreenImage">
          </div>
           <!-- 添加全屏图片弹出框 -->
          <transition name="fade">
            <div v-if="showFullscreenImage" class="fullscreen-image-overlay">
              <img :src="`${boardInfo.image || logo}?t=${new Date().getTime()}`" alt="全屏看板图片" @click="toggleFullscreenImage">
            </div>
          </transition>
        </div>
        <!-- 右侧内容 -->
        <div class="board-details" v-if="true">
          <!-- 看板信息 -->
          <div class="board-info" style="line-height: 25px;">
            <div class="info-item" v-for="(value, label) in boardInfoItems" :key="label">
              <label style="white-space: pre-wrap;display: inline-block;width:110px">{{ label }}</label><label style="font-weight: bold;display: inline-block;">：</label>
              
              <el-button style="font-size: 25px;margin-left:10px" id="countButton" class="square-button" :disabled="!result" size="small" v-if="label === '生产数'" @click="openInputDialog">{{ value || '点击输入' }}</el-button>
              <el-button style="font-size: 25px;margin-left:10px" :disabled="!result || currentStep <= 2" id="countButton_correctionCount" class="square-button"  size="small" v-else-if="label === '更正数'" @click="openInputDialog_correctionCount">{{ value || '点击输入' }}</el-button>
              <span style="margin-left:10px" v-else>{{ value || '未扫描' }}</span>
              
            </div>
          </div>
          <!-- 检查项目，三列布局 -->
          <div class="inspection-list">
            <div class="inspection-header" style="display: inline-block;margin-left:145px">
              <div class="inspection-column2"><el-button id="initButton" @click.native="InitPage(true)" :disabled="currentStep == 100" size="large" class="square-button">初始化</el-button></div>
              <div class="inspection-column2"><el-button id="repairButton" @click="repairProcess" :disabled=" result == null" size="large" class="square-button">返修</el-button></div>
              <div class="inspection-column2">
                <el-button v-if="barCodeEnable == 1" id="firstButton" @click="showFirstCode" :disabled="!result || currentStep <= 1 || currentStep == 100" size="large" class="square-button">检查初物</el-button>
                <el-button v-if="result && barCodeEnable == 2" id="firstButton" @click="showFirstCode" :disabled="currentStep <= 1 || currentStep == 100" size="large" class="square-button">检查初物</el-button>
              </div>
              <div class="inspection-column2">
                <el-button v-if="barCodeEnable == 1" id="endButton" @click="showEndCode" :disabled="!result || currentStep != 4 || currentStep == 100" size="large" class="square-button">检查终物</el-button>
                <el-button v-if="result && barCodeEnable == 2" id="endButton" @click="showEndCode" :disabled="(currentStep != 3 && currentStep != 4) || currentStep == 100" size="large" class="square-button">检查终物</el-button>
              </div>
            </div>
            <div class="inspection-header" v-if="currentStep != 100">
              <!-- <div class="inspection-column1">检查项目</div> -->
              <div class="inspection-column1"  v-if="result && (!(barCodeEnable == 2 && result.labelStartId == -1))"><label style="display: inline-block;width:110px">初物</label>：<span style="margin-left:10px">{{!result?'':barCodeEnable == 1?String(result.labelStartId):result.labelStartId }}</span></div>
              <div class="inspection-column2"><label style="display: inline-block;width:110px">初物结果</label>：<span style="margin-left:10px">{{(result == null || result.checkNo == 0)?'':'OK'}}</span></div>
              <div class="inspection-column1" v-if="result && (!(barCodeEnable == 2 && result.labelStartId == -1))"><label style="display: inline-block;width:110px">终物</label>：<span style="margin-left:10px">{{!result?'':barCodeEnable == 1?String(result.labelEndId):result.labelEndId }}</span></div>
              <div class="inspection-column2"><label style="display: inline-block;width:110px">终物结果</label>：<span style="margin-left:10px">{{currentStep == 4?'OK':''}}</span></div>
            </div>            
          </div>
        </div>
        </el-row>
        <el-row style="width: 100%;display: flex;">
          <!-- 按钮区域，与图片对齐 -->
          <div class="status-bar" v-if="currentStep != 100" :style="{'color':currentStep == 4?'red':'firebrick'}">
          {{ statusMessage }}
          </div>
          <div class="status-bar" v-if="currentStep == 100" :style="{'color':currentStep == 4?'red':'firebrick'}">
          {{ statusMessage1 }}
          </div>
        </el-row>
      </div>

      <!-- 弹出输入生产数的对话框 -->
      <el-dialog title="请输入生产数" :visible.sync="isDialogVisible" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"  width="20%">
        <el-input v-model="inputLabelCount" placeholder="输入生产数" maxlength="4" ref="inputLabelCountref" @focus="showKeyboard"
      readonly></el-input>
        <!-- 虚拟数字键盘 -->
        <virtual-numeric-keyboard v-model="inputLabelCount" />
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirmLabelCount">确定</el-button>
        </div>
      </el-dialog>
      <el-dialog title="请输入更正数" :visible.sync="isDialogVisible_correctionCount" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"  width="20%">
        <el-input v-model="inputLabelCount_correctionCount" placeholder="输入更正数" maxlength="4" ref="inputLabelCountref_correctionCount" @focus="showKeyboard"
      readonly></el-input>
        <!-- 虚拟数字键盘 -->
        <virtual-numeric-keyboard v-model="inputLabelCount_correctionCount" />
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirmLabelCount_correctionCount">确定</el-button>
        </div>
      </el-dialog>
      <el-dialog title="请选择检查结果" v-if="isDialogVisible1" :visible.sync="isDialogVisible1" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false" width="90%" class="custom-dialog">
        <div class="dialog-content"  v-if="currentStep == 2">
          <el-row style="width: 100%;display: flex;">
            <el-col :span="18" style="display: flex; flex-direction: column;" >
              <img style="object-fit: contain;margin-top:10px;max-width: 800px !important;max-height: 400px !important" 
              :src="`${result.startCheckList[currentItemIndex].img || ''}?t=${new Date().getTime()}`"  @click="toggleImage">
            </el-col>
            <!-- 添加全屏图片弹出框 -->
            <transition name="fade">
              <div v-if="showImage" class="fullscreen-image-overlay">
                <img  :src="`${result.startCheckList[currentItemIndex].img || ''}?t=${new Date().getTime()}`" @click="toggleImage">
              </div>
            </transition>
           <el-col :span="6" style="display: flex; flex-direction: column; justify-content: space-between;">
              
            <el-row style="width: 100%;">
                <h1>{{ currentInspectionItem.typeName == null? '': currentInspectionItem.typeName}}</h1>
            </el-row>
            <div style="flex-grow: 1;">
              </div>
              <div style="margin-top: auto" v-if="currentInspectionItem.inputType == 1">
                <el-button v-for="option in inspectionOptions" :disabled="buttonLoading" :id="'prop_' + option.propId"
                :style="{'background':result.startCheckList[currentItemIndex].propId != option.propId?'#1890ff':result.startCheckList[currentItemIndex].checkStatus == 1?'green':'red'}"
                :key="option.propId" type="primary" block style="margin-bottom: 30px" @click="selectOption(option)" class="optionButton"> 
                  {{ option.propName }}
                </el-button>
                <el-button   :disabled="buttonLoading" :id="'prop_' + ngOption.propId"
                :style="{'background':result.startCheckList[currentItemIndex].propId != ngOption.propId?'#1890ff':result.startCheckList[currentItemIndex].checkStatus == 1?'green':'red'}"
                 type="primary" block style="margin-bottom: 30px" @click="selectOption(ngOption)" class="optionButton"> 
                  {{ ngOption.propName }}
                </el-button>
              </div>
              <div style="margin-top: auto" v-if="currentInspectionItem.inputType == 2">
                <el-input style="width:130px;font-size: 20px;" v-model="inputResult" placeholder="输入" ref="inputResultCount" @focus="showKeyboard"
                readonly :disabled="buttonLoading"></el-input>
                <virtual-numeric-keyboard v-model="inputResult" />
                <el-button  type="primary"  block style="margin-top:5px;margin-left: 5px" @click="selectOption(null)" :disabled="buttonLoading">
                  确认
                </el-button>
              </div>
              <div style="margin-top: auto" v-if="currentInspectionItem.inputType == 3">
                <el-input style="width:130px;font-size: 20px;" v-model="inputResult" placeholder="输入" ref="inputResultText" @focus="showKeyboard"
                readonly :disabled="buttonLoading"></el-input>
                <virtual-numeric-keyboard v-model="inputResult" />
                <el-button  type="primary"  block style="margin-top:5px;margin-left: 5px" @click="selectOption(null)" :disabled="buttonLoading">
                  确认
                </el-button>
              </div>
              <h2 v-if="result != null && result.startCheckList != null && result.startCheckList.length > 0 && result.startCheckList[currentItemIndex] != null" 
              :style="{'color':result.startCheckList[currentItemIndex].checkStatus == 1?'green':'red'}">
              {{ result.startCheckList[currentItemIndex].checkStatus == 0?'请选择检查结果':result.startCheckList[currentItemIndex].checkStatus == 1?'检查通过，即将进行下一项':result.startCheckList[currentItemIndex].checkStatus == 3?'无需检查，即将进行下一项':result.startCheckList[currentItemIndex].confirm_user_qr == ''?'检查不通过，待异常人员扫码确认':'异常人员（' + result.startCheckList[currentItemIndex].confirm_user_qr + '）已确认，请继续检查。'}}</h2>
           </el-col>
          </el-row>
          
        </div>
        <div class="dialog-content"  v-if="currentStep == 3">
          
          <el-row style="width: 100%;display: flex;">
            <el-col :span="18" style="display: flex; flex-direction: column;" >
              <img style="object-fit: contain;margin-top:10px;max-width: 800px !important;max-height: 400px !important" :src="`${result.endCheckList[currentItemIndex].img || ''}?t=${new Date().getTime()}`"  @click="toggleImage">
            </el-col>
            <!-- 添加全屏图片弹出框 -->
            <transition name="fade">
              <div v-if="showImage" class="fullscreen-image-overlay">
                <img  :src="`${result.endCheckList[currentItemIndex].img || ''}?t=${new Date().getTime()}`" @click="toggleImage">
              </div>
            </transition>
            <el-col :span="6" style="display: flex; flex-direction: column; justify-content: space-between;">
              
              <el-row style="width: 100%;">
                  <h1>{{ currentInspectionItem.typeName == null? '': currentInspectionItem.typeName}}</h1>
              </el-row>
              <div style="flex-grow: 1;">
              </div>
              <!-- <h2>{{ currentInspectionItem.typeName == null? '': currentInspectionItem.typeName}}</h2> -->
              <div style="margin-top: auto" v-if="currentInspectionItem.inputType == 1">
                <el-button v-for="option in inspectionOptions" :disabled="buttonLoading" :id="'prop_' + option.propId"
                :style="{'background':result.endCheckList[currentItemIndex].propId != option.propId?'#1890ff':result.endCheckList[currentItemIndex].checkStatus == 1?'green':'red'}"
                :key="option.propId" type="primary" block style="margin-bottom: 30px" @click="selectOption(option)" class="optionButton"> 
                  {{ option.propName }}
                </el-button>
                <el-button  :disabled="buttonLoading" :id="'prop_' + ngOption.propId"
                :style="{'background':result.endCheckList[currentItemIndex].propId != ngOption.propId?'#1890ff':result.endCheckList[currentItemIndex].checkStatus == 1?'green':'red'}"
                 type="primary" block style="margin-bottom: 30px" @click="selectOption(ngOption)" class="optionButton"> 
                  {{ ngOption.propName }}
                </el-button>
              </div>
              <div style="margin-top: auto" v-if="currentInspectionItem.inputType == 2">
                <el-input style="width:130px;font-size: 20px;" v-model="inputResult" placeholder="输入" ref="inputResultCount" @focus="showKeyboard"
                readonly :disabled="buttonLoading"></el-input>
                <virtual-numeric-keyboard v-model="inputResult" />
                <el-button  type="primary"  block style="margin-top:5px;margin-left: 5px" @click="selectOption(null)" :disabled="buttonLoading">
                  确认
                </el-button>
              </div>
              <div style="margin-top: auto" v-if="currentInspectionItem.inputType == 3">
                <el-input style="width:130px;font-size: 20px;" v-model="inputResult" placeholder="输入" ref="inputResultText" @focus="showKeyboard"
                readonly :disabled="buttonLoading"></el-input>
                <virtual-numeric-keyboard v-model="inputResult" />
                <el-button  type="primary"  block style="margin-top:5px;margin-left: 5px" @click="selectOption(null)" :disabled="buttonLoading">
                  确认
                </el-button>
              </div>
               <h2 v-if="result != null && result.endCheckList != null && result.endCheckList.length > 0 && result.endCheckList[currentItemIndex] != null" 
            :style="{'color':result.endCheckList[currentItemIndex].checkStatus == 1?'green':'red'}">
            {{result.endCheckList[currentItemIndex].checkStatus == 0?'请选择检查结果':result.endCheckList[currentItemIndex].checkStatus == 1?'检查通过，即将进行下一项':result.endCheckList[currentItemIndex].checkStatus == 3?'无需检查，即将进行下一项':result.endCheckList[currentItemIndex].confirm_user_qr == ''?'检查不通过，待异常人员扫码确认':'异常人员（' + result.endCheckList[currentItemIndex].confirm_user_qr + '）已确认，请点击下一步继续检查。'}}</h2>
        
            </el-col>
          </el-row>
         </div>
        <div slot="footer" class="dialog-footer" v-if="currentStep == 22 && result.startCheckList[currentItemIndex].checkStatus == 2 && result.startCheckList[currentItemIndex].confirm_user_qr != ''">
          <el-button type="primary" @click="finishError">下一步</el-button>
        </div>
        <div slot="footer" class="dialog-footer" v-if="currentStep == 23 && result.endCheckList[currentItemIndex].checkStatus == 2 && result.endCheckList[currentItemIndex].confirm_user_qr != ''">
          <el-button type="primary" @click="finishError">下一步</el-button>
        </div>
      </el-dialog>
      <el-dialog title="检查结果确认"  :visible.sync="isDialogVisibleResualt" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false" width="70%" class="custom-dialog">
        <div class="dialog-content">
          <div class="inspection-items" v-if="currentStep != 100" style="font-size: 21px;">
            <div  class="inspection-item">
              <div class="inspection-column1" style="font-size: 20px;font-weight: bold;"></div>
              <div class="inspection-column2" style="font-size: 20px;font-weight: bold;">初物</div>
              <div class="inspection-column2" style="font-size: 20px;font-weight: bold;">终物</div>
            </div>
            <!-- <div  class="inspection-item">
              <div class="inspection-column1" style="font-size: 20px;font-weight: bold;">布标/刻印号</div>
              <div class="inspection-column2" style="font-size: 20px;font-weight: bold;">{{ !result?'':barCodeEnable == 1?String(result.labelStartId).padStart(7, '0'):result.labelStartId  }}</div>
              <div class="inspection-column2" style="font-size: 20px;font-weight: bold;">{{ (!result || !result.labelEndId)?'':barCodeEnable == 1?String(result.labelEndId).padStart(7, '0'):result.labelEndId}}</div>
            </div> -->
            <div v-for="(item, index) in  inspectionItems" :key="index" class="inspection-item" v-show="result.startCheckList[index].checkStatus == 1">
              <div v-if="result.startCheckList[index].checkStatus == 1" class="inspection-column1" :style="{'font-size': '20px','font-weight': 'bold','color':result.startCheckList.length == 0?'#606266': result.startCheckList[index].checkStatus == 0?'#606266':''}">{{ item.typeName }}</div>
              <div v-if="result.startCheckList[index].checkStatus == 1" class="inspection-column2" :style="{'color':result.startCheckList.length == 0 ? '' :result.startCheckList[index].checkStatus == 0?'#606266':result.startCheckList[index].checkStatus == 1?'green':'red '}">
                {{ result == null?'未检测':result.startCheckList.length == 0 ? '未检测' :result.startCheckList[index].checkStatus == 0?'OK':result.startCheckList[index].checkStatus == 1?'OK':result.startCheckList[index].checkStatus == 2?'未通过':'忽略'  }}
              </div>
              <div v-if="result.startCheckList[index].checkStatus == 1" class="inspection-column2" :style="{'color':result.endCheckList.length == 0 ? '' :result.endCheckList[index].checkStatus == 0?'#606266':result.endCheckList[index].checkStatus == 1?'green':'red '}">
                {{ result == null?'未检测':result.endCheckList.length == 0 ? '未检测' :result.endCheckList[index].checkStatus == 0?'OK':result.endCheckList[index].checkStatus == 1?'OK':result.endCheckList[index].checkStatus == 2?'未通过':'忽略'  }}
              </div>
            </div>
          </div>
          <div slot="footer" class="dialog-footer" >
            
          <h1 style="color:red">检查人员扫码确认</h1><el-button style="float:right;margin-top: -50px;" type="primary" @click="isDialogVisibleResualt = !isDialogVisibleResualt">重新检查</el-button>
          </div>
        </div>
      </el-dialog>
      <el-dialog title="扫码确认"  :visible.sync="isDialogVisible2" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false" width="70%" class="custom-dialog">
        <div class="dialog-content">
          <h1>{{ statusMessage1 }}</h1>
        </div>
      </el-dialog>
      <el-dialog title="待返修刻印号" v-if="isDialogVisible3" :visible.sync="isDialogVisible3" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"  width="50%">
        <el-input v-model="repairLabel" placeholder="输入刻印号" maxlength="16" ref="repairLabelref" readonly></el-input>
        <virtual-numeric-keyboard v-model="repairLabel" />
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirmRepairLabel">确定</el-button>
        </div>
      </el-dialog>
      <el-dialog title="待替换刻印号" :visible.sync="isDialogVisible4" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"  width="50%">
        <el-input v-model="replaceLabelText" placeholder="输入刻印号" maxlength="16" ref="replaceLabelref" readonly></el-input>
        <virtual-numeric-keyboard v-model="replaceLabelText" />
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirmReplaceLabelText">确定</el-button>
        </div>
      </el-dialog>
      <el-dialog title="初物刻印号" v-if="result" :visible.sync="isDialogVisible5" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"  width="40%">
        <el-input v-model="result.labelStartId" placeholder="输入初物刻印号" maxlength="16" ref="labelStartIdref" readonly></el-input>
        <virtual-numeric-keyboard v-model="result.labelStartId" />
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirmLabelId(1)">确定</el-button>
        </div>
      </el-dialog>
      <el-dialog title="终物刻印号" v-if="result" :visible.sync="isDialogVisible6" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"  width="40%">
        <el-input v-model="result.labelEndId" placeholder="输入终物刻印号" maxlength="16" ref="labelEndIdref" readonly></el-input>
        <virtual-numeric-keyboard v-model="result.labelEndId" />
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirmLabelId(2)">确定</el-button>
        </div>
      </el-dialog>
      <el-dialog title="返修" v-if="result" :visible.sync="isDialogVisible7" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"  width="70%">
        <div class="repair-div" v-if="currentStep == 100">
          <!-- 上半部分：扫码待返修布标和替换布标 -->
          <div class="scan-section">
            <el-row style="width: 100%;">
              <!-- 待返修布标 -->
              <el-col :span="12" class="left-col">
                <p v-if="barCodeEnable == 1">返修布标: {{ repairLabel }}</p>
                <p style="display: inline-block;"  v-if="barCodeEnable == 2">返修刻印号:</p> <el-button :disabled="!result" size="small" v-if="barCodeEnable == 2" @click="openRepairDialog">{{ repairLabel || '点击输入' }}</el-button>
              </el-col>
              <el-col :span="4" class="left-col" style="margin-top: 15px;">
                <el-button :disabled="repairLabel == ''" @click="replaceLabel(false)" :class="{ 'is-selected': replaceLabelFlag==1?true:false }">不替换布标</el-button>
              </el-col>
              <el-col :span="4" class="left-col" style="margin-top: 15px;">
                <el-button :disabled="repairLabel == ''" @click="replaceLabel(true)" :class="{ 'is-selected': replaceLabelFlag==2?true:false }">替换布标</el-button>
              </el-col>
              <!-- 待替换布标 -->
              <el-col :span="24" class="right-col">
                <p v-if="barCodeEnable == 1 && replaceLabelFlag==2">替换布标: {{ replaceLabelText }}</p>
                <p  style="display: inline-block;" v-if="barCodeEnable == 2 && replaceLabelFlag==2">替换刻印号:</p> <el-button :disabled="!result" size="small" v-if="barCodeEnable == 2 && replaceLabelFlag==2" @click="openReplaceDialog">{{ replaceLabelText || '点击输入' }}</el-button>
              </el-col>
            </el-row>
          </div>

          <!-- 下半部分：选择返修工程和细节 -->
          <div class="repair-reason-section">
            <div class="repair-reason-list">
              <h2 style="margin-top: 15px;margin-bottom: 5px;">返修工程</h2>
              <div class="repair-reason-list-div">
                <el-button   style="margin-left:0px;width:550px;margin-bottom: 3px;font-size:20px"
                  v-for="(reason, index) in ptlist"
                  :key="index"
                  :class="{ 'is-selected': selectedReason === reason.projectCode?true:false }"
                  @click="selectReason(reason)"
                >
                  {{ reason.projectName }}
                </el-button>
              </div>
            </div>
            <div class="repair-reason-list">
              <h2 style="margin-top: 15px;margin-bottom: 5px;">返修细节</h2>
              <div class="repair-reason-list-div">
                <el-button  style="margin-left:0px;width:550px;margin-bottom: 3px;font-size:20px"
                  v-for="(reason, index) in rrlist"
                  :key="index"
                  :class="{ 'is-selected': selectedDetail === reason.reasonCode?true:false }"
                  @click="selectDetail(reason)"
                >
                  {{ reason.reasonName }}
                </el-button>
              </div>
              <!-- <el-input v-if="selectedDetail == -1" v-model="repairMemo" placeholder="输入描述" ref="repairMemoref"></el-input> -->
            </div>
          </div>

          <!-- 确认按钮 -->
          <div class="confirm-section">
            <el-button type="primary" style="font-size: 1.5em;" @click="saveRepair">确 认</el-button>
            <el-button type="primary" style="font-size: 1.5em;" @click="closeRepair">返 回</el-button>
          </div>
        </div>
      </el-dialog>
      <!-- 隐藏的输入框，用于接收焦点 -->
      <input type="text" ref="hiddenInput" style="position: absolute; left: -9999px;" readonly tabindex="-1"/>
    </div>
  </div>
</template>

<script>
import { scanback,ptlistOnRequest,rrlistOnRequest,saveRepair ,saveCheck,addAbnormal,checkUser,checkLine,checkUser_confirm} from "@/api/system/base";
import '@/assets/styles/scan.scss';
import logoImg from '@/assets/logo/logo2.png'
import VirtualNumericKeyboard from "@/components/CustomNumberKeyboard.vue";
export default {
  components: {
    VirtualNumericKeyboard,
  },
  data() {
    return {
      isConnected: false, // 初始状态为未连接
      intervalId: null, // 用于存储 setInterval 的 ID


      logo: logoImg,
      scannedData: null,
      listening: false,
      boardInfo: {
        image: '', // 默认为空
        backNumber: '',
        labelSerial: '',
        mNo: '',
        labelCount: '' // 通过手动输入
      },
      result:null,
      inspectionItems: [], // 初始化为空，初物布标扫描后填充
      statusMessage: '等待扫描看板码',
      statusMessage1: '等待扫描待返修布标',
      currentStep: 1,
      currentInput: '',
      inputHandler: null,
      shiftPressed: false,
      specialChars: {
        ';': ':',
        '1': '!',
        '2': '@',
        '3': '#',
        '4': '$',
        '5': '%',
        '6': '^',
        '7': '&',
        '8': '*',
        '9': '(',
        '0': ')',
        '-': '_',
        '=': '+',
        '[': '{',
        ']': '}',
        '\\': '|',
        ';': ':',
        '\'': '"',
        ',': '<',
        '.': '>',
        '/': '?'
      },
      isDialogVisible: false,  // 控制对话框显示
      isDialogVisible_correctionCount:false,
      isDialogVisible1: false,  // 控制对话框显示
      isDialogVisible2:false,
      isDialogVisible3:false,
      isDialogVisible4:false,
      isDialogVisible5:false,
      isDialogVisible6:false,
      // isDialogVisible7:false,
      isDialogVisibleResualt:false,
      inputLabelCount: '',      // 对话框中的输入值
      inputLabelCount_correctionCount:'',
      currentItemIndex: -1, // 当前检查项目的索引
      currentItemName:'',  //检查项目的名称
      inspectionOptions: [], // 检查项目的选项
      scannedInitialLabel: '', // 初物布标扫描结果
      mismatchDetected: false, // 标记是否检测到不一致
      errStep:false,
      errUser:false,
      barCodeEnable:1,//是否需要扫布标  1需要  2不需要

      inputResult:'',
      showFullscreenImage: false,
      showImage:false,
      buttonLoading:false,


      canScan:false,//当前状态是否允许扫描


      repairLabel: '',
      replaceLabelText: '',
      replaceLabelFlag: 1,
      repairMemo:'',
      abnormalUserText:'',
      selectedReason: null,
      selectedDetail: null,
      ptlist:[],
      rrlist:[],
      lastStep:0,

      inputValue: "", // 绑定的输入框值
      keyboardVisible: false, // 控制虚拟键盘的显示
      ngOption:{propId:-1,propName:'NG'},
      isFullscreen:false,
      lineName:'',
      supportLabelCount:1,
    };
  },
  computed: {
    isDialogVisible7() {
      return this.currentStep === 100 && !this.isDialogVisible2;
    },
    boardInfoItems() {
      if (this.supportLabelCount == 1)
      return {
        '背　　番': this.boardInfo.backNumber,
        '布标件号': this.boardInfo.labelSerial,
        'M.NO': this.boardInfo.mNo,
        '生产数': this.boardInfo.labelCount,
        '更正数': this.boardInfo.labelCount_correctionCount
      };
      else {
        return {
          '背　　番': this.boardInfo.backNumber,
          '布标件号': this.boardInfo.labelSerial,
          'M.NO': this.boardInfo.mNo,
          // '生产数': this.boardInfo.labelCount,
          // '更正数': this.boardInfo.labelCount_correctionCount
        };
      }
    },
    currentInspectionItem() {
      // 返回当前正在检查的项目
      return this.inspectionItems[this.currentItemIndex] || {};
    },
    isMismatch() {
      return this.scannedInitialLabel !== this.boardInfo.labelSerial;
    },
  },
  created() {
    // this.getList();
    this.canScan = true;
    this.startListening();
  },
  methods: {
    handleClose(){
      this.$store.dispatch('tagsView/delView', this.$route)
    },
    handleExitAction() {
      if (!document.fullscreenElement && !document.mozFullScreenElement && !document.webkitFullscreenElement && !document.msFullscreenElement) {
        if (document.documentElement.requestFullscreen) {
            document.documentElement.requestFullscreen();
        } else if (document.documentElement.msRequestFullscreen) {
            document.documentElement.msRequestFullscreen();
        } else if (document.documentElement.mozRequestFullScreen) {
            document.documentElement.mozRequestFullScreen();
        } else if (document.documentElement.webkitRequestFullscreen) {
            document.documentElement.webkitRequestFullscreen(Element.ALLOW_KEYBOARD_INPUT);
        }
        this.isFullscreen = true
      } else {
          if (document.exitFullscreen) {
              document.exitFullscreen();
          } else if (document.webkitCancelFullscreen) {
              document.webkitCancelFullscreen();
          } else if (document.msExitFullscreen) {
              document.msExitFullscreen();
          } else if (document.mozCancelFullScreen) {
              document.mozCancelFullScreen();
          } else if (document.webkitExitFullscreen) {
              document.webkitExitFullscreen();
          }
          this.isFullscreen = false
      }
      document.querySelector('#fullButton').blur();
    },
    exitFullscreen() {
      if (document.exitFullscreen) {
        document.exitFullscreen().catch(err => console.error('退出全屏失败:', err));
      } else if (document.mozCancelFullScreen) { // Firefox
        document.mozCancelFullScreen().catch(err => console.error('退出全屏失败:', err));
      } else if (document.webkitExitFullscreen) { // Chrome, Safari and Opera
        document.webkitExitFullscreen().catch(err => console.error('退出全屏失败:', err));
      } else if (document.msExitFullscreen) { // IE/Edge
        document.msExitFullscreen().catch(err => console.error('退出全屏失败:', err));
      }
      console.log('成功退出全屏');
    },
    promptToClosePage() {
      this.$confirm('您想关闭当前页面吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 提示用户如何关闭页面
        this.$message({
          type: 'info',
          message: '请使用浏览器的关闭按钮或快捷键（如 Ctrl+W 或 Cmd+W）来关闭页面。'
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        });
      });
    },
    showKeyboard() {
      this.keyboardVisible = true;
    },
    hideKeyboard() {
      this.keyboardVisible = false;
    },
    reloadPage(){
      window.location.reload();
    },
    checkDatabaseConnection() {
      checkLine().then(response => {
        if (response.code == 200){
          this.lineName = response.data.lineName;
          this.supportLabelCount = response.data.supportLabelCount;
          this.isConnected = true
        }else {
          this.lineName = response.msg;
          this.isConnected = true
        }
      }).catch(() => {
        this.isConnected = false;
      });
    },
    getList(){
      ptlistOnRequest({useStatus:1}).then(response => {
        this.ptlist = response.data[0].ptList;
      });
    },
    replaceLabel(flag) {
      this.replaceLabelText = ''
      if (flag == true) {
        this.replaceLabelFlag = 2;
        if (this.barCodeEnable == 1)
          this.isDialogVisible2 = true;
        if (this.barCodeEnable == 2)
          this.isDialogVisible4 = true;
      } else
      this.replaceLabelFlag = 1;
      if (this.replaceLabelFlag  == 2 && !this.replaceLabelText) {
        this.statusMessage1 = "请扫描待替换布标。"
      }
    },
    scanReplaceLabel() {
      // 模拟扫码逻辑
      this.replaceLabelText = '替换布标456';
    },
    selectReason(reason) {
      this.selectedReason = reason.projectCode;
    },
    selectDetail(detail) {
      this.selectedDetail = detail.reasonCode;
      if (this.selectDetail == -1) {
        this.repairMemo = ''
        this.$refs.repairMemoref.focus();
      }
    },
    saveRepair() {
      if (this.selectedReason && this.selectedDetail && this.repairLabel) {
        this.$confirm('是否确认此次返修记录？', '确认', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          closeOnClickModal:false,
          showClose:false
        }).then(() => {
          const formData = {
            backNo:this.boardInfo.backNumber,
            labelNo:this.boardInfo.labelSerial,
            inputId:this.repairLabel,
            newInputId:this.replaceLabelText,
            repairStatus:this.replaceLabelFlag,
            projectCode:this.selectedReason,
            reasonCode:this.selectedDetail,
            abnormalUser:this.abnormalUserText,
            memo:this.repairMemo,
          }
          saveRepair(formData).then(response => {
                this.$modal.msgSuccess("保存成功");  
                this.closeRepair()
              }).catch(() => {});
        })
      } else if (!this.repairLabel){
        this.$modal.msgWarning("请输入返修刻印号");  
        this.statusMessage1 = "请输入返修刻印号"
      } else {
        this.$modal.msgWarning("请选择返修工程和细节");  
        this.statusMessage1 = "请选择返修工程和细节"
      }
    },

    toggleFullscreenImage() {
      if (this.boardInfo.backNumber !='')
        this.showFullscreenImage = !this.showFullscreenImage;
    },

    toggleImage() {
        this.showImage = !this.showImage;
    },
    InitPage(bol){
      document.querySelector('#initButton').blur();
      this.isDialogVisibleResualt = false;
      if (bol){
        this.$confirm('初始化将清空页面数据，是否确认？', '确认', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          closeOnClickModal:false,
          showClose:false
        }).then(() => {
          this.scannedData = null;
          this.boardInfo= {
            image: '', // 默认为空
            backNumber: '',
            labelSerial: '',
            mNo: '',
            labelStartId:'',
            labelCount: '' // 通过手动输入
          };
          this.result = null;
          this.inspectionItems = []; // 初始化为空，初物布标扫描后填充
          this.statusMessage =  '等待扫描看板码';
          this.currentStep = 1;
          this.canScan = true;
          this.isDialogVisibleResualt = false;
          this.shiftPressed = false;
        }).catch(() => {
          // 用户选择继续
        });
      } else {
        this.scannedData = null;
        this.boardInfo= {
          image: '', // 默认为空
          backNumber: '',
          labelSerial: '',
          mNo: '',
          labelCount: '' // 通过手动输入
        };
        this.result = null;

        this.inspectionItems = []; // 初始化为空，初物布标扫描后填充
        this.statusMessage =  '等待扫描看板码';
        this.currentStep = 1;
        this.canScan = true;
        this.shiftPressed = false;
      }
    },
    InitFirst(){

    },
    async showScanConfirmation(step, originId, newId) {
      // step 9 点击数量  3 初物 6 终物  4改初物   7改终物
      this.isDialogVisible2 = true; // 打开扫码确认对话框
      this.errUser = true;
      this.canScan = true;
      this.statusMessage1 = "请异常处置人员扫码";

      return new Promise(async (resolve) => {
        const checkUser = async () => {
          return new Promise((innerResolve) => {
            // 移除之前的事件监听器
            this.$off('scanConfirmed');

            // 添加新的事件监听器
            this.$on('scanConfirmed', async (userId) => {
              console.log('扫码确认事件触发，用户ID:', userId);
              this.isDialogVisible2 = false; // 关闭扫码确认对话框
              this.errUser = false;

              try {
                const isValidUser = await this.checkUserPass(userId);
                if (isValidUser) {
                  let ret = await this.saveAbnormal(null, step, originId, newId, userId);
                  console.log('step:' + step + ';return:' + isValidUser + ';Save Result:' + ret);
                  innerResolve(ret);
                } else {
                  this.isDialogVisible2 = true; // 重新打开扫码确认对话框
                  this.statusMessage1 = "异常处置人员不正确，请重新扫描";
                  this.errUser = true;
                  this.canScan = true;
                  innerResolve(false);
                }
              } catch (error) {
                console.error('扫码验证过程中发生错误:', error);
                this.isDialogVisible2 = true; // 重新打开扫码确认对话框
                this.statusMessage1 = "扫码验证失败，请重新扫描";
                this.errUser = true;
                this.canScan = true;
                innerResolve(false);
              }
            });
          });
        };

        while (true) {
          const isValidUser = await checkUser();
          if (isValidUser) {
            console.log('用户验证通过，关闭对话框');
            resolve(isValidUser);
            break;
          }
        }
      });
    },
    async checkUserPass(code) {
      try {
        const response = await checkUser({ userQr: code }); // 假设 checkUser 是您要调用的异步API
        if (response.code === 200) {
          return response.data === 1; // 直接返回布尔值
        } else {
          return false;
        }
      } catch (error) {
        console.error('Error checking user:', error);
        return false; // 或者抛出异常，取决于您的错误处理策略
      }
    },
    async checkUserConfirm(code) {
      try {
        const response = await checkUser_confirm({ userQr: code }); // 假设 checkUser 是您要调用的异步API
        if (response.code === 200) {
          return response.data === 1; // 直接返回布尔值
        } else {
          return false;
        }
      } catch (error) {
        console.error('Error checking user:', error);
        return false; // 或者抛出异常，取决于您的错误处理策略
      }
    },
    async openInputDialog() {
      // 调用扫码确认函数
      const labelCount = parseInt(this.result.labelCount, 10);
      this.canScan = true;
      this.currentItemIndex = -1
      if (this.result.startCheckList.length > 0 && this.result.startCheckList[0].checkStatus > 0)
        this.currentStep = 3;
      this.result.endCheckList.forEach(function(item){
        item.propId = 0;
        item.checkStatus = 0;
        item.confirm_user_qr = ''
      })
      document.querySelector('#countButton').blur();
      if (!isNaN(labelCount) && labelCount > 0) {
        const isValidUser = await this.showScanConfirmation(9,this.result.labelStartId,this.result.labelStartId);
        this.inputLabelCount = String(labelCount); // 将输入的生产数赋值
        if (isValidUser) {
          this.canScan = false;
          this.isDialogVisible = true; // 打开对话框
          setTimeout(() => {
            this.$nextTick(() => {
              this.$refs.inputLabelCountref.focus();
            });
          }, 800);
        }
      } else {
        this.canScan = false;
          this.isDialogVisible = true; // 打开对话框
          setTimeout(() => {
            this.$nextTick(() => {
              this.$refs.inputLabelCountref.focus();
            });
          }, 800);
      }
    },
    async openInputDialog_correctionCount() {
      // 调用扫码确认函数
      this.inputLabelCount_correctionCount = ''
      document.querySelector('#countButton_correctionCount').blur();

      this.canScan = false;
        this.isDialogVisible_correctionCount = true; // 打开对话框
        setTimeout(() => {
          this.$nextTick(() => {
            this.$refs.inputLabelCountref_correctionCount.focus();
          });
        }, 800);
      
    },
    openLabelInputDialog(param) {
      this.canScan = false;
      if (this.result.labelStartId == '-1')
          this.confirmLabelId(param)
      else {
        if (param == 1){
          
          this.isDialogVisible5 = true; // 打开对话框
          this.result.labelStartId = ''
          setTimeout(() => {
            this.$nextTick(() => {
              this.$refs.labelStartIdref.focus();
            });
          }, 800);
        } else {
          this.isDialogVisible6 = true; // 打开对话框
          this.result.labelEndId = ''
          setTimeout(() => {
            this.$nextTick(() => {
              this.$refs.labelEndIdref.focus();
            });
          }, 800);
        }
      }
    },
    openRepairDialog() {
      this.canScan = false;
      this.repairLabel = ''
      this.isDialogVisible3 = true; // 打开对话框
      setTimeout(() => {
        this.$nextTick(() => {
          this.$refs.repairLabelref.focus();
        });
      }, 800);
    },
    openReplaceDialog() {
      this.canScan = false;
      this.replaceLabelText = ""
      this.isDialogVisible4 = true; // 打开对话框
      setTimeout(() => {
        this.$nextTick(() => {
          this.$refs.replaceLabelref.focus();
        });
      }, 800);
    },
    closeDialog() {
      this.canScan = true;
      this.isDialogVisible = false; // 关闭对话框
      this.inputLabelCount = '';    // 清空输入
      
      if (this.barCodeEnable == 1)
        this.statusMessage = this.currentStep == 3?'请扫终物布标':'请扫初物布标';
      else {
          //无布标看板
          this.statusMessage = this.currentStep == 3?'请开始终物检查':'请开始初物检查';
          // this.openLabelInputDialog(1)
      }
    },
    confirmLabelId(param){
      if (param == 1) {
        if (this.result.labelCount)
          this.result.labelEndId = Number(this.result.labelStartId) + Number(this.result.labelCount) - 1
        if (this.result.labelStartId == '-1')
          this.result.labelEndId = '-1'
        this.isDialogVisible5 = false;
        this.fetchFirstCodeInfo();
      }
      if (param == 2) {
        this.isDialogVisible6 = false;
        if (this.result.labelStartId == '-1')
          this.fetchFirstCodeInfo();
        else if (this.result.labelEndId != Number(this.result.labelStartId) + Number(this.result.labelCount) - 1){
          this.mismatchDetected = true;
          this.showMismatchDialogEnd1(this.result.labelEndId);
        }
        else 
          this.fetchFirstCodeInfo();
      }
    },
    confirmLabelCount() {
      // 验证输入是否为正整数
      const labelCount = parseInt(this.inputLabelCount, 10);
      if (!isNaN(labelCount) && labelCount > 0) {
        this.boardInfo.labelCount = labelCount; // 将输入的生产数赋值
        this.result.labelCount = labelCount
        if (this.result.labelStartId)
          this.result.labelEndId = Number(this.result.labelStartId) + labelCount - 1
        this.currentItemIndex = -1
        this.currentInput = ''
        this.closeDialog(); // 关闭对话框
      } else {
        this.$message.error('请输入一个正整数的生产数');
      }
    },
    confirmLabelCount_correctionCount() {
      // 验证输入是否为正整数
      const labelCount = parseInt(this.inputLabelCount_correctionCount, 10);
      if (!isNaN(labelCount) && labelCount >= 0) {
        this.inputLabelCount_correctionCount = String(labelCount)
        this.boardInfo.labelCount_correctionCount = String(labelCount)
        this.result.correctionCount = String(labelCount)
        this.isDialogVisible_correctionCount = false

        this.canScan = true;
      } else {
        this.$message.error('请输入一个正整数的生产数');
      }
    },
    confirmRepairLabel() {
      if (this.replaceLabel.length > 0) {
        this.isDialogVisible3 = false
      } else {
        this.$message.error('请输入待返修刻印号');
      }
    },
    confirmReplaceLabelText(){
      if (this.replaceLabelText.length > 0) {
        this.isDialogVisible4 = false
      } else {
        this.$message.error('请输入待返修刻印号');
      }
    },
    lastNonEmptyItem(arr) {
      for (let i = arr.length - 1; i >= 0; i--) {
        if (arr[i].trim() && arr[i].length > 1) {
          return arr[i];
        }
      }
      return null; // 如果数组为空或者全是空项，则返回null
    },
    toggleListening() {
      this.listening = !this.listening;
      if (this.listening) {
        this.startListening();
      }
    },
    treatCode(code){
      if (code.indexOf('1T') <= 0)
        return ''
      else 
        return String(Number(code.substr(code.indexOf('1T') + 2,7)))
    },
    startListening() {
      let self = this
      if (!this.listening) {
        this.listening = true;
        this.currentInput = ''; // 清空当前输入
        this.inputHandler = async (event) => {
          if (this.canScan == true) {
            if (event.key === 'Enter') {
              this.errCode = this.currentInput
              if (this.currentStep == 1) {
                //扫看板
                this.fetchBoardInfo(this.lastNonEmptyItem(this.currentInput.split(' ')));
                this.currentInput = '';
              } else if (this.currentStep == 2) {
                //扫初物
                if (this.errStep == false && this.errUser == false) {
                  if (this.currentItemIndex == -1){
                    console.log('扫到初物码' + this.currentInput)
                    console.log('处理为初物' + this.treatCode(this.currentInput))
                    let code = this.treatCode(this.currentInput)
                    if (code.length > 0){
                      if (!this.boardInfo.labelCount || this.boardInfo.labelCount == 0) {
                        this.statusMessage = '请输入生产数';
                        // 验证初物布标号是否与看板信息一致
                      } else if (code !== String(this.result.labelStartId)) {
                        this.mismatchDetected = true;
                        this.showMismatchDialog(code);
                      } else {
                        this.fetchFirstCodeInfo(this.currentInput);
                      }
                    } else {
                      this.currentInput = ''
                      this.$modal.msgError("布标不正确，请重新扫描");
                    }
                  }
                }else if (this.errUser == true){
                  if (this.errCode.length > 10){
                    this.errCode = ''
                    this.currentInput = ''
                    this.$modal.msgError("异常处置人员(" + this.errCode +")不正确，请重新扫描");
                  }
                  if (this.errCode.length > 0) {
                    // event.preventDefault(); // 阻止 Enter 键的默认行为
                    this.$emit('scanConfirmed', this.errCode);
                    this.currentInput = ''; // 清空当前输入
                  }
                }
                else {
                  if (this.errCode.length > 10){
                    this.errCode = ''
                    this.currentInput = ''
                    this.$modal.msgError("异常处置人员(" + this.errCode +")不正确，请重新扫描");
                  }
                  if (this.errCode.length > 0) {
                    const errorUserId=  this.errCode
                    const isValidUser = await self.checkUserPass(errorUserId); // 等待 checkUserPass 完成
                    if (isValidUser) {
                      this.fetchFirstError(errorUserId)
                    } else {
                      this.$modal.msgError("异常处置人员(" + this.errCode +")不正确，请重新扫描");
                      self.currentInput = "";
                    }
                  }
                }
                this.currentInput = ''; // 清空当前输入
              } else if (this.currentStep == 2.5) {
                //扫看板
                this.checkBoardNumber(this.lastNonEmptyItem(this.currentInput.split(' ')));
                this.currentInput = '';
              } else if (this.currentStep == 3) {
                //扫终物
                if (this.errStep == false && this.errUser == false) {
                  
                  if (this.currentItemIndex == - 1){
                    console.log('扫到终物码' + this.currentInput)
                    console.log('处理为终物' + this.treatCode(this.currentInput))
                    let code = this.treatCode(this.currentInput)
                    if (code.length > 0) {
                      if (!this.boardInfo.labelCount || this.boardInfo.labelCount == 0) {
                        this.statusMessage = '请输入生产数';
                        // 验证初物布标号是否与看板信息一致
                      } else if (code !== String(this.result.labelEndId)) {
                        this.mismatchDetected = true;
                        this.showMismatchDialogEnd(code);
                      } else {
                        this.fetchFirstCodeInfo(this.currentInput);
                      }
                    } else {
                      this.currentInput = ''
                      this.$modal.msgError("布标不正确，请重新扫描");
                    }
                  }
                }else if (this.errUser == true){
                  if (this.errCode.length > 10){
                    this.errCode = ''
                    this.currentInput = ''
                    this.$modal.msgError("异常处置人员(" + this.errCode +")不正确，请重新扫描");
                  }
                  if (this.errCode.length > 0) {
                    // event.preventDefault(); // 阻止 Enter 键的默认行为
                    
                    console.log('进入scanConfirmed3:' + this.errCode)
                    this.$emit('scanConfirmed', this.errCode);
                    this.currentInput = ''; // 清空当前输入
                  }
                }
                else {
                  if (this.errCode.length > 10){
                    this.errCode = ''
                    this.currentInput = ''
                    this.$modal.msgError("异常处置人员(" + this.errCode +")不正确，请重新扫描");
                  }
                  if (this.errCode.length > 0){
                    const errorUserId=  this.errCode
                    const isValidUser = await self.checkUserPass(errorUserId); // 等待 checkUserPass 完成
                    if (isValidUser) {
                      this.fetchEndError(errorUserId)
                    } else {
                      this.$modal.msgError("异常处置人员(" + this.errCode +")不正确，请重新扫描");
                      self.currentInput = "";
                    }
                  }
                }
                this.currentInput = ''; // 清空当前输入
              } else if (this.currentStep === 4 && this.isDialogVisibleResualt) {
                if (this.currentInput.length > 10){
                    this.currentInput = ''
                    this.$modal.msgError("检查人员不正确，请重新扫描");
                  }
                if (this.currentInput.length > 0) {
                  this.result.checkUserQr = this.currentInput
                  const errorUserId=  this.currentInput
                  const isValidUser = await self.checkUserConfirm(errorUserId); // 等待 checkUserPass 完成
                  if (isValidUser) {
                    if (this.result.barCodeEnable == 1) {
                      this.result.labelEndId = String(this.result.labelEndId)//.padStart(7, '0')
                    }
                    if (Number(this.result.labelCount) > 1)
                      this.$set(this.result, 'labelType', 2) 
                    if (this.result.correctionCount == '')
                      this.result.correctionCount = '0'
                    console.log('完成检查存库')
                    saveCheck(this.result).then(response => {
                      this.statusMessage = '完成检查';
                      setTimeout(() => {
                        this.isDialogVisibleResualt = false
                        this.$nextTick(() => {
                          this.InitPage(false);
                          this.currentInput = ''; // 清空当前输入
                        });
                      }, 500);
                    }).catch(() => {});
                  } else {
                    this.$modal.msgError("确认人员不正确，请重新扫描");
                    self.currentInput = "";
                  }
                  
                }
              } else if (this.currentStep === 21) {
                //扫初物异常人员
                this.currentStep = 2
                this.currentInput = ''// 清空当前输入
              } else if (this.currentStep == 100){
                
                if (self.abnormalUserText == '') {
                  if (self.currentInput != '') {
                    if (self.currentInput.length > 10){
                      self.currentInput = ''
                      this.$modal.msgError("异常处置人员不正确，请重新扫描");
                    } else {
                      let code = self.currentInput
                      const isValidUser = await self.checkUserPass(code); // 等待 checkUserPass 完成
                      if (isValidUser) {
                        self.repairLabel = ''
                        self.fetchrepairError(code);
                      } else {
                        this.$modal.msgError("异常处置人员(" + code +")不正确，请重新扫描");
                        self.currentInput = "";
                      }
                    }
                  }
                }
                else if (self.repairLabel == ''){
                  self.repairLabel = self.treatCode(self.currentInput)
                  self.$forceUpdate()
                  if (self.repairLabel != '') {
                    self.isDialogVisible2 = false; 
                    self.statusMessage1 = "请选择是否替换布标。"
                  }
                } else if (self.replaceLabelFlag  == 2 && !self.replaceLabelText) {
                  self.replaceLabelText = self.treatCode(self.currentInput)
                  if (self.replaceLabelText != '') {
                    self.isDialogVisible2 = false; 
                    self.$forceUpdate()
                    self.statusMessage1 = "请选择返修工程和细节后确认。"
                  }
                }
                
                self.currentInput = ''; // 清空当前输入
              }
            } else if (event.key === 'Shift') {
              this.shiftPressed = !this.shiftPressed;
            } else {
              let key = event.key;
              if (this.shiftPressed && /[a-z]/.test(key)) {
                key = key.toUpperCase();
                this.shiftPressed = false;
              } else if (this.shiftPressed && this.specialChars[key]) {
                key = this.specialChars[key];
                this.shiftPressed = false;
              }
              this.currentInput += key;
            }
          }
        };
        document.addEventListener('keyup', this.inputHandler);
      }
    },
    stopListening() {
      if (this.listening) {
        this.listening = false;
        document.removeEventListener('keyup', this.inputHandler);
      }
    },
    fetchBoardInfo(code) {
      const loading = this.$loading({
          lock: true,
          text: '数据读取中',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
      scanback({ backNo: code }).then(response => {
        // console.log(JSON.stringify(response.data));
        // 模拟接口返回数据
        const mockData = response.data;

        this.boardInfo = {
          image: mockData.url,
          backNumber: mockData.backNo,
          labelSerial: mockData.labelNo,
          mNo: mockData.modelNo,
          labelCount: '', // 需要手动输入
          labelCount_correctionCount: '', // 需要手动输入
          checkList:mockData.checkList
        };
        this.barCodeEnable = mockData.barCodeEnable
        this.result = mockData.result;
        
        if (this.result.labelCount != null && this.result.labelCount != '')
          this.boardInfo.labelCount = this.result.labelCount
        loading.close();
        if (this.result.checkNo == 0){
          this.fetchInspectionItems(1)
          this.statusMessage = '请开始初物检查';
          this.currentStep = 2; // 请开始初物检查
          if (this.barCodeEnable == 2) {
            //无布标看板
            this.statusMessage = '请开始初物检查';
            // this.openLabelInputDialog(1)
          }
          if (this.supportLabelCount == 2) {
            this.result.labelCount = 888
          }
          if (this.result.labelCount == 0) {
              this.openInputDialog()
          }
        } else {
          //跳到扫终物状态
          this.fetchInspectionItems(2)
          
          this.statusMessage = '等待终物检查';
          this.currentStep = 3; // 更新步骤状态
          if (this.barCodeEnable == 2) {
            this.canScan = true;
          } else {
            this.boardInfo.labelCount = this.result.labelCount
            this.canScan = true;
            this.currentItemIndex = - 1
          }
          
          this.result.labelEndId = Number(this.result.labelStartId) + Number(this.result.labelCount) - 1
        }
      }).catch(() => {
        this.currentInput = ''
        loading.close();
      });
    },
    checkBoardNumber(code){
      if (this.boardInfo.backNumber != code) {
        this.statusMessage1 = "看板信息不符，请重新扫描或点击初始化。"
      } else {
        this.statusMessage = '等待检查终物';
        this.currentStep = 3;
      }
    },
    fetchFirstCodeInfo(code) {
      this.currentItemIndex = -1;
      this.showNextInspectionOption(); // 显示第一个项目的选项
    },
    async showFirstCode(){
      //扫码后处理
      document.querySelector('#firstButton').blur();
      let nextFlag = true;
      
      this.currentStep = 2; // 等待扫描初物布标
      if (this.result.startCheckList.length > 0 && this.result.startCheckList[0].checkStatus > 0){
        console.log('进入3')
        nextFlag = await this.showScanConfirmation(3,this.result.labelStartId,this.result.labelStartId);
      }
      if (nextFlag){
        this.statusMessage = '扫描初物布标';
        this.result.startCheckList.forEach(function(item){
          item.checkStatus = 0;
          item.propId = 0;
          item.confirm_user_qr = ''
        })
        this.result.endCheckList.forEach(function(item){
          item.checkStatus = 0;
          item.propId = 0;
          item.confirm_user_qr = ''
        })
        if (this.barCodeEnable == 2) {
          //无布标看板
          this.statusMessage = '请开始初物检查';
          this.canScan = false;
          if (this.result.labelCount == 0) {
            this.openInputDialog()
          } else
            this.openLabelInputDialog(1)
        }
        else {
          this.canScan = true;
          this.currentItemIndex = -1
          if (this.result.labelCount == 0) {
            this.openInputDialog()
          }
        }
      }
    },
    async showEndCode(){
      document.querySelector('#endButton').blur();
      let nextFlag = true;
      this.currentStep = 3;
      if (this.result.endCheckList.length > 0 && this.result.endCheckList[0].checkStatus > 0){
        console.log('进入6')
        nextFlag = await this.showScanConfirmation(6,this.result.labelStartId,this.result.labelStartId);
      }

      if (nextFlag){
        this.currentStep = 3; // 等待扫描初物布标
        this.result.endCheckList.forEach(function(item){
            item.propId = 0;
            item.checkStatus = 0;
            item.confirm_user_qr = ''
          })
        if (this.barCodeEnable == 2) {
          //无布标看板
          this.statusMessage = '请开始终物检查';
          this.openLabelInputDialog(2)
        }
        else {
          this.canScan = true;
          this.currentItemIndex = -1
          this.statusMessage = '等待扫描终物布标';
        }
      }
    },
    async showMismatchDialog(code) {
      // 创建并打开一个询问是否重新扫描的对话框
      this.$confirm('检测到初物布标号(' + code +')与看板信息不一致', '确认', {
        confirmButtonText: '重新扫描',
        cancelButtonText: '使用当前布标',
        type: 'warning',
        closeOnClickModal:false,
        showClose:false
      }).then(() => {
        // 用户选择重新扫描
        this.statusMessage = '请重新扫描初物布标';
        this.currentInput = ''
        this.currentStep = 2; // 回退到扫描初物布标的步骤
      }).catch(async () => {
        // 用户选择继续
        let nextFlag = true;
        nextFlag = await this.showScanConfirmation(4,this.result.labelStartId,code);
        if (nextFlag){
          this.mismatchDetected = false;
          this.result.labelStartId = code
          this.result.labelEndId = Number(this.result.labelStartId) + Number(this.result.labelCount) - 1
          this.currentItemIndex = -1;
          this.showNextInspectionOption();
        }
      });
      // 立即将焦点移到隐藏的输入框
      this.$nextTick(() => {
        this.$refs.hiddenInput.focus();
      });
    },
    async showMismatchDialogEnd(code) {
      // 创建并打开一个询问是否重新扫描的对话框
      this.$confirm('检测到终物布标号(' + code +')与看板信息不一致？', '确认', {
        confirmButtonText: '重新扫描',
        cancelButtonText: '使用当前布标',
        type: 'warning',
        closeOnClickModal:false,
        showClose:false
      }).then(() => {
        // 用户选择重新扫描
        this.statusMessage = '请重新扫描终物布标';
        this.currentInput = ''
        this.currentStep = 3; // 回退到扫描终物布标的步骤
      }).catch(async () => {
        // 用户选择继续
        let nextFlag = true;
        nextFlag = await this.showScanConfirmation(7,this.result.labelEndId,code);
        console.log(nextFlag)
        if (nextFlag){
          this.mismatchDetected = false;
          this.result.labelEndId = code
          this.currentItemIndex = -1;
          this.showNextInspectionOption();
        }
        // this.currentStep = 4; // 继续执行下一步
      });
      // 立即将焦点移到隐藏的输入框
      this.$nextTick(() => {
        this.$refs.hiddenInput.focus();
      });
    },
    async showMismatchDialogEnd1(code) {
      // 创建并打开一个询问是否重新扫描的对话框
      this.$confirm('检测到终物刻印号(' + code +')与看板信息不一致', '确认', {
        confirmButtonText: '重新输入',
        cancelButtonText: '使用当前刻印号',
        type: 'warning',
        closeOnClickModal:false,
        showClose:false
      }).then(() => {
        // 用户选择重新扫描
        this.statusMessage = '请重新输入终物刻印号';
        this.openLabelInputDialog(2)
        this.currentInput = ''
        this.currentStep = 3; // 回退到扫描终物布标的步骤
      }).catch(async () => {
        // 用户选择继续
        let nextFlag = true;
        nextFlag = await this.showScanConfirmation(7,this.result.labelEndId,code);
        if (nextFlag){
          this.mismatchDetected = false;
          this.result.labelEndId = code
          this.currentItemIndex = -1;
          this.showNextInspectionOption();
        }
        // this.currentStep = 4; // 继续执行下一步
      });
      // 立即将焦点移到隐藏的输入框
      this.$nextTick(() => {
        this.$refs.hiddenInput.focus();
      });
    },
    showNextInspectionOption() {
      this.canScan = false;
      // console.log(this.currentItemIndex)
      // console.log(this.inspectionItems)
      if (this.currentItemIndex + 1 < this.inspectionItems.length) {
        this.currentItemIndex++;
        this.statusMessage = '当前检查项目：' + this.currentInspectionItem.typeName;
        this.openInspectionDialog(this.currentInspectionItem.propList);
      } else {
        // 所有检查项目已完成
        if (this.currentStep == 2 && Number(this.result.labelCount) > 1){
          //初物检查完
          this.$set(this.result, 'labelType', 1) 
          saveCheck(this.result).then(response => {
              this.result.checkNo = response.data;
              this.canScan = true;
              this.statusMessage = '等待扫看板码进行终物检查';
              this.currentItemIndex = -1
              this.currentStep = 2.5; // 更新步骤状态
            }).catch(() => {});
        } else if (Number(this.result.labelCount) == 1) {
          this.$set(this.result, 'labelType', 1) 
          this.canScan = true;
          this.isDialogVisibleResualt = true;
          this.statusMessage = '等待扫描检查人员';
          this.currentStep = 4; // 更新步骤状态
        }else if (this.currentStep == 3){
          this.canScan = true;
          this.isDialogVisibleResualt = true;
          this.statusMessage = '等待扫描检查人员';
          this.currentStep = 4; // 更新步骤状态
        }
        // console.log(this.result.startCheckList)
      }
    },
    fetchFirstError(code){
      //存库
      this.result.startCheckList[this.currentItemIndex].confirm_user_qr = code;
      this.saveAbnormal(this.result.startCheckList[this.currentItemIndex],8,this.result.labelStartId,this.result.labelStartId,code).then(()=>{
        this.errStep = false
        this.buttonLoading = false
        console.log( '异常人员' + code)
        this.statusMessage = '异常人员已确认';
        this.$forceUpdate()
      })
      
    },
    finishError(){
      setTimeout(() => {
          this.$nextTick(() => {
          // 关闭当前对话框
          this.isDialogVisible1 = false;
          this.$forceUpdate()
          // 显示下一个检查项目的选项
          this.showNextInspectionOption();
          });
        }, 500);
    },
    fetchEndError(code){
      //存库
      this.result.endCheckList[this.currentItemIndex].confirm_user_qr = code;
      this.saveAbnormal(this.result.endCheckList[this.currentItemIndex],8,this.result.labelEndId,this.result.labelEndId,code).then(()=>{
        this.errStep = false
        this.buttonLoading = false
        console.log( '异常人员' + code)
        this.statusMessage = '异常人员已确认';
        this.$forceUpdate()
      })
    },
    async saveAbnormal(item, handleType, labelId, newlabelId, confirmUserQr) {
      let ret = true;
      const formData = {
        handleType: handleType,
        backNo: this.boardInfo.backNumber,
        labelId: newlabelId,
        confirmUserQr: confirmUserQr,
        data: {},
      };

      if (handleType == 1) {
        // 背番没有找到
      } else if (handleType == 2) {
        // 初物ID不匹配
      } else if (handleType == 3) {
        // 初物重扫
      } else if (handleType == 4) {
        // 强制当前ID为初物
        this.$set(formData.data, 'originId', labelId);
        this.$set(formData.data, 'newId', newlabelId);
      } else if (handleType == 5) {
        // 终物ID不匹配
      } else if (handleType == 6) {
        // 终物重扫
      } else if (handleType == 7) {
        // 强制当前ID为终物
        this.$set(formData.data, 'originId', labelId);
        this.$set(formData.data, 'newId', newlabelId);
      } else if (handleType == 8) {
        // 外观检查
        this.$set(formData.data, 'typeId', item.typeId);
        this.$set(formData.data, 'propId', item.propId);
      } else if (handleType == 9) {
        // 布标数量变更
      }

      try {
        await addAbnormal(formData);
      } catch (error) {
        ret = false;
        this.$modal.msgError("处理失败");
      }

      return ret;
    },
    openInspectionDialog(options) {
      // 创建并打开一个包含选项的对话框
      this.isDialogVisible1 = true;
      this.buttonLoading = false;
      if (this.currentInspectionItem.inputType == 2) {
        setTimeout(() => {
          this.$nextTick(() => {
            this.inputResult = ''
            this.$refs.inputResultCount.focus();
          });
        }, 500);
      }
      if (this.currentInspectionItem.inputType == 3) {
        setTimeout(() => {
          this.$nextTick(() => {
            this.inputResult = ''
            this.$refs.inputResultText.focus();
          });
        }, 500);
      }
      this.inspectionOptions = options;
    },
    selectOption(option) {
      let self = this
      if (this.inspectionItems[this.currentItemIndex].inputType == 2){
        option = {}
        this.$set(option, 'propId', 0) 
        const resultCount = parseInt(this.inputResult, 10);
        if (!isNaN(resultCount) && resultCount > 0) {
          
          this.$set(option, 'inputText', resultCount) 
          let passIds = this.inspectionItems[self.currentItemIndex].propId.split(',');
          passIds.forEach(function(passId){
            let passItem = self.inspectionItems[self.currentItemIndex].propList.find(t=>t.propId == passId)
            let passmin = parseInt(passItem.propName.split('~')[0], 10)
            let passmax = parseInt(passItem.propName.split('~')[1], 10)
            if (resultCount <= passmax && resultCount >= passmin) {
              option.propId = passItem.propId
            }
          })
          if (option.propId == 0) {
            self.inspectionItems[this.currentItemIndex].propList.forEach(function(item){
              let min = parseInt(item.propName.split('~')[0], 10)
              let max = parseInt(item.propName.split('~')[1], 10)
              if (resultCount <= max && resultCount >= min) {
                option.propId = item.propId
              }
            })
          }
        } else {
          this.inputResult = ''
          this.$message.error('请输入一个正整数');
          return;
        }
      }else if (this.inspectionItems[this.currentItemIndex].inputType == 3){
        option = {}
        this.$set(option, 'propId', -1) 
        if (self.inputResult != '') {
          self.inspectionItems[this.currentItemIndex].propList.forEach(function(item){
            if (item.propName == self.inputResult) {
              option.propId = item.propId
            }
          })
        }
      } else {
        document.querySelector('#prop_' + option.propId).blur();
      } 
      
      self.buttonLoading = true;
      // 设置当前项目的最终值
      if (this.currentStep == 2){
        self.currentInput = '';
        this.result.startCheckList[this.currentItemIndex].propId = option.propId
        self.result.startCheckList[this.currentItemIndex].inputText = option.inputText
        self.result.startCheckList[this.currentItemIndex].confirm_user_qr = ''
        if (this.inspectionItems[this.currentItemIndex].inputType == 3 && option.propId == -1){
          this.result.startCheckList[this.currentItemIndex].checkStatus = 3
        }else
          this.result.startCheckList[this.currentItemIndex].checkStatus = this.inspectionItems[this.currentItemIndex].propId.split(",").includes(String(option.propId))?1:2;
        if (this.result.startCheckList[this.currentItemIndex].checkStatus == 1 || this.result.startCheckList[this.currentItemIndex].checkStatus == 3) {
          setTimeout(() => {
            this.$nextTick(() => {
            // 关闭当前对话框
            this.isDialogVisible1 = false;
            this.$forceUpdate()
            // 显示下一个检查项目的选项
            this.showNextInspectionOption();
            });
          }, 300);
        } else if (this.result.startCheckList[this.currentItemIndex].checkStatus == 2) {
          this.canScan = true;
          this.errStep = true;
        }
      }
      if (this.currentStep == 3){
        self.currentInput = '';
        this.result.endCheckList[this.currentItemIndex].propId = option.propId
        self.result.endCheckList[this.currentItemIndex].confirm_user_qr = ''
        self.result.endCheckList[this.currentItemIndex].inputText = option.inputText
        if (this.inspectionItems[this.currentItemIndex].inputType == 3 && option.propId == -1){
          this.result.endCheckList[this.currentItemIndex].checkStatus = 3
        }else
          this.result.endCheckList[this.currentItemIndex].checkStatus = this.inspectionItems[this.currentItemIndex].propId.split(",").includes(String(option.propId))?1:2;
        if (this.result.endCheckList[this.currentItemIndex].checkStatus == 1 || this.result.endCheckList[this.currentItemIndex].checkStatus == 3) {
          setTimeout(() => {
            this.$nextTick(() => {
            // 关闭当前对话框
            this.isDialogVisible1 = false;
            this.$forceUpdate()
            // 显示下一个检查项目的选项
            this.showNextInspectionOption();
            });
          }, 300);
        } else if (this.result.endCheckList[this.currentItemIndex].checkStatus == 2) {
          this.canScan = true;
          this.errStep = true;
        }
      }
    },
    fetchInspectionItems(param) {
      let self = this
      this.inspectionItems = this.boardInfo.checkList;
      if (param == 1) {
        self.result.startCheckList = []
      }
      self.result.endCheckList = []
      self.inspectionItems.forEach(function(item){
        if (param == 1) {
          self.result.startCheckList.push({
            'typeId' : item.typeId,
            'propId' : 0,
            'checkStatus':0,
            'confirm_user_qr':'',
            'img':item.imageList[0].imageUrl
          })
        }
          self.result.endCheckList.push({
              typeId : item.typeId,
              propId : 0,
              checkStatus:0,
              confirm_user_qr:'',
              'img':item.imageList[0].imageUrl
          })
      })

      console.log(self.result)
    },
    repairProcess() {
      // 开始返修流程
      // alert('返修流程开始...');
      this.selectedReason = '';
      this.selectedDetail = '';
      this.replaceLabelFlag = 1;
      this.abnormalUserText = ''
      this.repairMemo = '';
      this.lastStep = this.currentStep
      this.currentStep = 100;
      rrlistOnRequest({useStatus:1}).then(response => {
        this.rrlist = response.data[0].rrList;
        
        this.rrlist.push({
          reasonCode:-1,
          reasonName:'其他',
          memo:'',
        })
      });
      ptlistOnRequest({useStatus:1}).then(response => {
        this.ptlist = response.data[0].ptList;
      }).catch(() => {
      });
      this.isDialogVisible2 = true;
      document.querySelector('#repairButton').blur();
      this.canScan = true;
      this.statusMessage1 = '请异常处置人员扫码。'
    },
    fetchrepairError(code){
      this.abnormalUserText = code
      this.isDialogVisible2 = false;
      if (this.repairLabel == '' && this.barCodeEnable == 1)
      {
        this.isDialogVisible2 = true; 
        this.statusMessage1 = "请扫描待返修布标"
      } else if (this.repairLabel && this.barCodeEnable == 2) {
        this.isDialogVisible3 = true; 
        this.statusMessage1 = "请填入待返修刻印号"
      }
    },
    closeRepair(){
      this.canScan = true;
      this.currentStep = this.lastStep
    }
  },
  mounted() {
    if (!document.fullscreenElement && !document.mozFullScreenElement && !document.webkitFullscreenElement && !document.msFullscreenElement) {
      this.isFullscreen = false;
    }
    else {
      this.isFullscreen = true;
    }
    // 初始化时立即检查一次
    this.checkDatabaseConnection();
    // 每 10 秒检查一次
    this.intervalId = setInterval(this.checkDatabaseConnection, 60000);
    //

  },
  beforeDestroy() {
    // 组件销毁时清除定时器
    clearInterval(this.intervalId);
  },
};
</script>
<style scoped>
/* 在你的全局 CSS 文件中 */
.el-loading-text {
  font-size: 36px !important; /* 按需更改字体大小 */
}

/* 自定义淡入淡出过渡效果 */
.dialog-fade-enter-active,
.dialog-fade-leave-active {
  transition: opacity 0.8s ease;
}
.dialog-fade-enter,
.dialog-fade-leave-to /* .dialog-fade-leave-active 在2.1.8之前的版本中 */
{
  opacity: 0;
}

.db-status {
  position: fixed;
  top: 10px;
  right: 10px;
  padding: 10px;
  border-radius: 5px;
  font-size: 14px;
  font-weight: bold;
}

.connected {
  font-size: 18px;
  color: green;
  background-color:transparent;
}

.disconnected {
  font-size: 18px;
  color: red;
  background-color: transparent;
}
</style>