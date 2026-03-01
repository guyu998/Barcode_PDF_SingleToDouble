import request from '@/utils/request'

// 查询客户列表
export function listCustomer() {
  return request({
    url: '/base/customer/list',
    method: 'get'
  })
}

// 上传标签文件
export function uploadLabelFile(data) {
  return request({
    url: '/task/upload',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 查询批次列表
export function listTask(query) {
  return request({
    url: '/task/list',
    method: 'get',
    params: query
  })
}

// 查询标签详情（用于标签下载）
export function listLabelDetail(query) {
  return request({
    url: '/task/material/list',
    method: 'get',
    params: query
  })
}

// 删除批次
export function deleteTask(id) {
  return request({
    url: `/task/delete/${id}`,
    method: 'post'
  })
}

// 打印标签
export function printLabels(data) {
  return request({
    url: '/task/material/supplementPrint',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

// 获取任务异常日志
export function getTaskLog(taskId) {
  return request({
    url: `/task/log/${taskId}`,
    method: 'get'
  })
}