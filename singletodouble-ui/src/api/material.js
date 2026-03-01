import request from '@/utils/request'

// 查询客户列表
export function listCustomer() {
  return request({
    url: '/base/customer/list',
    method: 'get'
  })
}

// 上传物料文件
export function uploadMaterialFile(data) {
  return request({
    url: '/base/material/upload',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 查询物料列表
export function listMaterial(query) {
  return request({
    url: '/base/material/list',
    method: 'get',
    params: query
  })
}

// 查询物料详情
export function getMaterialDetail(makerMatNo, custMatNo,custColorCode) {
  return request({
    url: `/base/material/${makerMatNo}/${custMatNo}/${custColorCode}`,
    method: 'get'
  })
}

// 查询BOM信息
export function listBOM(query) {
  return request({
    url: '/base/material/bom/list',
    method: 'get',
    params: query
  })
}