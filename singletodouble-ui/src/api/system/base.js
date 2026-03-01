import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 外观检查项目
export function listAppearanceType(query) {
  return request({
    url: '/base/appearancetype/list',
    method: 'get',
    params: query
  })
}

export function addAppearanceType(data) {
  return request({
    url: '/base/appearancetype/add',
    method: 'post',
    data: data
  })
}

export function updateAppearanceType(data) {
  return request({
    url: '/base/appearancetype/edit',
    method: 'post',
    data: data
  })
}

export function updateAppearanceTypeStatus(data) {
  return request({
    url: '/base/appearancetype/updatestatus',
    method: 'post',
    data: data
  })
}

export function delAppearanceType(id) {
  return request({
    url: '/base/appearancetype/delete/' + id,
    method: 'delete'
  })
}

// 外观检查配置
export function listAppearanceCheck(query) {
  return request({
    url: '/base/appearancecheck/list',
    method: 'get',
    params: query
  })
}

export function addAppearanceCheck(data) {
  return request({
    url: '/base/appearancecheck/add',
    method: 'post',
    data: data
  })
}

export function updateAppearanceCheck(data) {
  return request({
    url: '/base/appearancecheck/edit',
    method: 'post',
    data: data
  })
}

export function delAppearanceCheck(id) {
  return request({
    url: '/base/appearancecheck/delete/' + id,
    method: 'delete'
  })
}

// 流水线
export function listPipeline(query) {
  return request({
    url: '/base/pipeline/list',
    method: 'get',
    params: query
  })
}

export function addPipeline(data) {
  return request({
    url: '/base/pipeline/add',
    method: 'post',
    data: data
  })
}

export function updatePipeline(data) {
  return request({
    url: '/base/pipeline/edit',
    method: 'post',
    data: data
  })
}

export function updatePipelineStatus(data) {
  return request({
    url: '/base/pipeline/updatestatus',
    method: 'post',
    data: data
  })
}

export function delPipeline(id) {
  return request({
    url: '/base/pipeline/delete/' + id,
    method: 'delete'
  })
}

// 工程
export function listProjectType(query) {
  return request({
    url: '/base/projecttype/list',
    method: 'get',
    params: query
  })
}

export function addProjectType(data) {
  return request({
    url: '/base/projecttype/add',
    method: 'post',
    data: data
  })
}

export function updateProjectType(data) {
  return request({
    url: '/base/projecttype/edit',
    method: 'post',
    data: data
  })
}

export function updateProjectTypeStatus(data) {
  return request({
    url: '/base/projecttype/updatestatus',
    method: 'post',
    data: data
  })
}

export function delProjectType(id) {
  return request({
    url: '/base/projecttype/delete/' + id,
    method: 'delete'
  })
}

// 返修原因
export function listRepairReason(query) {
  return request({
    url: '/base/repairreason/list',
    method: 'get',
    params: query
  })
}

export function addRepairReason(data) {
  return request({
    url: '/base/repairreason/add',
    method: 'post',
    data: data
  })
}

export function updateRepairReason(data) {
  return request({
    url: '/base/repairreason/edit',
    method: 'post',
    data: data
  })
}

export function updateRepairReasonStatus(data) {
  return request({
    url: '/base/repairreason/updatestatus',
    method: 'post',
    data: data
  })
}

export function delRepairReason(id) {
  return request({
    url: '/base/repairreason/delete/' + id,
    method: 'delete'
  })
}

// 背番图片管理
export function listCodeimage(query) {
  return request({
    url: '/base/codeimage/list',
    method: 'get',
    params: query
  })
}

export function uploadCodeimage(data) {
  return request({
    url: '/base/codeimage/upload',
    method: 'post',
    data: data
  })
}

export function delCodeimage(id) {
  return request({
    url: '/base/codeimage/delete/' + id,
    method: 'delete'
  })
}

export function scanback(query) {
  return request({
    url: '/check/scanback',
    method: 'get',
    params: query
  })
}


// 工程配置
export function listPtRelation(query) {
  return request({
    url: '/base/ptset/list',
    method: 'get',
    params: query
  })
}

export function addPtRelation(data) {
  return request({
    url: '/base/ptset/add',
    method: 'post',
    data: data
  })
}

export function updatePtRelation(data) {
  return request({
    url: '/base/ptset/edit',
    method: 'post',
    data: data
  })
}

export function delPtRelation(id) {
  return request({
    url: '/base/ptset/delete/' + id,
    method: 'delete'
  })
}

// 返修原因配置
export function listRrRelation(query) {
  return request({
    url: '/base/rrset/list',
    method: 'get',
    params: query
  })
}

export function addRrRelation(data) {
  return request({
    url: '/base/rrset/add',
    method: 'post',
    data: data
  })
}

export function updateRrRelation(data) {
  return request({
    url: '/base/rrset/edit',
    method: 'post',
    data: data
  })
}

export function delRrRelation(id) {
  return request({
    url: '/base/rrset/delete/' + id,
    method: 'delete'
  })
}

// 异常处置人员
export function listAbnormalUser(query) {
  return request({
    url: '/base/abnormaluser/list',
    method: 'get',
    params: query
  })
}

export function addAbnormalUser(data) {
  return request({
    url: '/base/abnormaluser/add',
    method: 'post',
    data: data
  })
}

export function updateAbnormalUser(data) {
  return request({
    url: '/base/abnormaluser/edit',
    method: 'post',
    data: data
  })
}

export function updateAbnormalUserStatus(data) {
  return request({
    url: '/base/abnormaluser/updatestatus',
    method: 'post',
    data: data
  })
}

export function delAbnormalUser(id) {
  return request({
    url: '/base/abnormaluser/delete/' + id,
    method: 'delete'
  })
}

// 数据
export function listHistory(query) {
  return request({
    url: '/base/data/history',
    method: 'get',
    params: query
  })
}

export function listData(query) {
  return request({
    url: '/base/data/list',
    method: 'get',
    params: query
  })
}

export function delData(lineName, backNo) {
  return request({
    url: '/base/data/delete/' + lineName + '/' + backNo,
    method: 'delete'
  })
}

export function laset(id) {
  return request({
    url: '/base/laset/' + id,
    method: 'get',
  })
}

export function addlaset(data) {
  return request({
    url: '/base/laset/add',    
    method: 'post',
    data: data
  })
}

export function uploadBackData(data) {
  return request({
    url: '/base/data/upload',
    method: 'post',
    data: data
  })
}

export function ptlistOnRequest(query) {
  return request({
    url: '/check/ptset/listOnRequest',
    method: 'get',
    params: query
  })
}

export function rrlistOnRequest(query) {
  return request({
    url: '/check/rrset/listOnRequest',
    method: 'get',
    params: query
  })
}

export function saveRepair(data) {
  return request({
    url: '/check/repair/add',
    method: 'post',
    data: data
  })
}

export function saveCheck(data) {
  return request({
    url: '/check/save',
    method: 'post',
    data: data
  })
}

export function addAbnormal(data) {
  return request({
    url: '/check/abnormal/add',
    method: 'post',
    data: data
  })
}

export function checkUser(query) {
  return request({
    url: '/check/abnormal/checkuser',
    method: 'get',
    params: query
  })
}

export function checkUser_confirm(query) {
  return request({
    url: '/check/confirm/checkuser',
    method: 'get',
    params: query
  })
}

export function uploadTmp(data) {
  return request({
    url: '/common/uploadTmp',
    method: 'post',
    data: data
  })
}

export function uploadAbnormaluser(data) {
  return request({
    url: '/base/abnormaluser/upload',
    method: 'post',
    data: data
  })
}
export function checkLine() {
  return request({
    url: '/check/line',
    method: 'get',
  })
}