import request from '@/utils/request'

// 查询角色列表
export function getData(data) {
  return request({
    url: '/base/vDrawingInfo/list',
    method: 'get',
    params: data
  })
}

// 查询支付详细
export function getVDrawingAnnex(data) {
  return request({
    url: '/base/vDrawingAnnex/getDrawing',
    method: 'post',
    responseType: 'blob',
    data: data
  })
}

export function getBom(payId) {
  return request({
    url: '/base/tebomview/getbom/' + payId,
    method: 'get',
  })
}
export function getnextcbbom(payId) {
  return request({
    url: '/base/tebomview/getnextcbbom/' + payId,
    method: 'get',
  })
}