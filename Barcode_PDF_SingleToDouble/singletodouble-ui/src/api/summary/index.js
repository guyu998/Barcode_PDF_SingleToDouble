import request from '@/utils/request'

// 返修列表
export function listRepair(query) {
  return request({
    url: '/summary/repair/list',
    method: 'get',
    params: query
  })
}

// 异常处置列表
export function listAbnormal(query) {
  return request({
    url: '/summary/abnormalhandle/list',
    method: 'get',
    params: query
  })
}

// 检查结果列表
export function listResult(query) {
  return request({
    url: '/summary/result/list',
    method: 'get',
    params: query
  })
}

// 检查结果列表
export function listResultDetail(query) {
  return request({
    url: '/summary/resultdetail/list',
    method: 'get',
    params: query
  })
}