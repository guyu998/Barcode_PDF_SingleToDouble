import request from '@/utils/request'

// 查询基础信息列表
export function listBasicInfo(query) {
  return request({
    url: '/system/basicInfo/list',
    method: 'get',
    params: query
  })
}

// 查询基础信息详细
export function getBasicInfo(infoId) {
  return request({
    url: '/system/basicInfo/' + infoId,
    method: 'get'
  })
}

// 新增基础信息
export function addBasicInfo(data) {
  return request({
    url: '/system/basicInfo',
    method: 'post',
    data: data
  })
}

// 修改基础信息
export function updateBasicInfo(data) {
  return request({
    url: '/system/basicInfo',
    method: 'put',
    data: data
  })
}

// 删除基础信息
export function delBasicInfo(infoId) {
  return request({
    url: '/system/basicInfo/' + infoId,
    method: 'delete'
  })
}

// 上传基础信息文件
export function uploadBasicInfoFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/system/basicInfo/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}